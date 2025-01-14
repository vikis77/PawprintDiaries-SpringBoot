package com.qin.catcat.unite.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.exception.BusinessException;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.EsPostIndex;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.HomePostVO;
import com.qin.catcat.unite.popo.vo.SearchVO;
// import com.qin.catcat.unite.repository.EsPostIndexRepository;
import com.qin.catcat.unite.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService{
    // @Autowired（暂时停用ES）
    // private EsPostIndexRepository esPostIndexRepository;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CatMapper catMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * ES 和 MySQL 联合搜索（帖子或猫猫）
     * @param words 搜索关键词
     * @return 合并后的搜索结果
     */
    public SearchVO searchForEsAndMysql(String words, int page, int size) {
        SearchVO searchVO = new SearchVO();
        
        // 检查搜索关键词
        if (words == null || words.trim().isEmpty()) {
            log.warn("搜索关键词为空");
            return searchVO;
        }
        
        log.info("开始搜索，关键词：{}，页码：{}，每页大小：{}", words, page, size);
        
        // 1、ES查询帖子
        try {
            Pageable esPageable = PageRequest.of(page - 1, size);
            // Page<EsPostIndex> postResults = esPostIndexRepository.findByTitleOrArticle(words.trim(), esPageable);
            // 暂时停用ES
            Page<EsPostIndex> postResults = null;
            

            if (postResults != null && postResults.getTotalElements() > 0) {
                log.info("ES搜索到 {} 个帖子，总页数：{}", postResults.getTotalElements(), postResults.getTotalPages());
                List<Long> postIds = postResults.stream()
                    .filter(esPostIndex -> esPostIndex.getPostId() != null)
                    .map(esPostIndex -> {
                        try {
                            return Long.parseLong(esPostIndex.getPostId());
                        } catch (NumberFormatException e) {
                            log.warn("无效的postId格式: {}", esPostIndex.getPostId());
                            return null;
                        }
                    })
                    .filter(id -> id != null)
                    .toList();
                
                if (!postIds.isEmpty()) {
                    // 构建帖子VO
                    QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
                    queryWrapper.in("id", postIds)
                              .eq("is_deleted", 0)
                              .eq("is_adopted", 1);
                    List<Post> postList = postMapper.selectList(queryWrapper);
                    
                    log.info("从MySQL获取到 {} 条帖子详情", postList.size());
                    
                    // 查询作者信息
                    List<Integer> authorIdList = postList.stream()
                        .map(Post::getAuthorId)
                        .toList();
                    List<User> userList = userMapper.selectBatchIds(authorIdList);
                    Map<Integer,User> userMap = userList.stream()
                        .collect(Collectors.toMap(User::getUserId, Function.identity()));
                    List<HomePostVO> postVOList = postList.stream()
                        .map(post -> {
                            HomePostVO homePostVO = new HomePostVO();
                            BeanUtils.copyProperties(post, homePostVO);
                            homePostVO.setAuthorAvatar(userMap.get(post.getAuthorId()).getAvatar());
                            homePostVO.setAuthorNickname(userMap.get(post.getAuthorId()).getNickName());
                            return homePostVO;
                        })
                        .toList();
                    
                    IPage<HomePostVO> postPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
                    postPage.setRecords(postVOList);
                    postPage.setTotal(postResults.getTotalElements());
                    postPage.setPages(postResults.getTotalPages());
                    searchVO.setPosts(postPage);
                }
            } else {
                log.info("ES未搜索到相关帖子");
            }
        } catch (Exception e) {
            log.error("ES搜索发生错误: {}", e.getMessage(), e);
        }
        
        // 2、MySQL查询猫猫
        try {
            List<Cat> cats = catMapper.selectCatByCatWords(words);
            if (!cats.isEmpty()) {
                log.info("MySQL搜索到 {} 个猫猫", cats.size());
                searchVO.setCats(cats);
            } else {
                log.info("MySQL未搜索到相关猫猫");
            }
        } catch (Exception e) {
            log.error("MySQL搜索猫猫发生错误: {}", e.getMessage(), e);
        }
        
        return searchVO;
    }

    /**
     * @Description 搜索 - 分页MySQL方式查询
     * @param words 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 合并后的搜索结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SearchVO searchForMysql(String words, int page, int size) {
        SearchVO searchVO = new SearchVO();
        // 1、查询帖子
        // 查询帖子并分页
        IPage<Post> postPage = postMapper.selectPage(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<Post>(page, size),
            new QueryWrapper<Post>()
                .select("id as post_id", "title", "article", "author_id", "like_count", "cover_picture")
                .like("title", words)
                .like("article", words)
                .eq("is_deleted", 0)
                .eq("is_adopted", 1)
        );

        // 获取作者信息
        List<Integer> authorIds = postPage.getRecords().stream()
            .map(Post::getAuthorId)
            .collect(Collectors.toList());
            
        List<User> authors = userMapper.selectBatchIds(authorIds);
        Map<Integer, User> authorMap = authors.stream()
            .collect(Collectors.toMap(User::getUserId, Function.identity()));

        // 转换为HomePostVO
        List<HomePostVO> postVOList = postPage.getRecords().stream()
            .map(post -> {
                HomePostVO vo = new HomePostVO();
                vo.setPostId(post.getPostId());
                vo.setAuthorId(post.getAuthorId());
                vo.setTitle(post.getTitle());
                vo.setCoverPicture(post.getCoverPicture());
                vo.setLikeCount(post.getLikeCount());
                
                User author = authorMap.get(post.getAuthorId());
                if (author != null) {
                    vo.setAuthorNickname(author.getNickName());
                    vo.setAuthorAvatar(author.getAvatar());
                }
                return vo;
            })
            .collect(Collectors.toList());

        // 构建HomePostVO的分页对象    
        IPage<HomePostVO> homePostVOPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        homePostVOPage.setRecords(postVOList);
        homePostVOPage.setTotal(postPage.getTotal());
        homePostVOPage.setSize(postPage.getSize());
        homePostVOPage.setCurrent(postPage.getCurrent());
        searchVO.setPosts(homePostVOPage);
        // 2、查询猫猫
        List<Cat> cats = catMapper.selectCatByCatWords(words);
        searchVO.setCats(cats);
        return searchVO;
    }

}