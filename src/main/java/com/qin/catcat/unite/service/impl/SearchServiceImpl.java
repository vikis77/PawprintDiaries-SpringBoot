package com.qin.catcat.unite.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.exception.BusinessException;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.EsPostIndex;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.vo.HomePostVO;
import com.qin.catcat.unite.popo.vo.SearchVO;
import com.qin.catcat.unite.repository.EsPostIndexRepository;
import com.qin.catcat.unite.service.SearchService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SearchServiceImpl implements SearchService{
    @Autowired
    private EsPostIndexRepository esPostIndexRepository;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CatMapper catMapper;

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
            Page<EsPostIndex> postResults = esPostIndexRepository.findByTitleOrArticle(words.trim(), esPageable);
            
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
                    
                    List<HomePostVO> postVOList = postList.stream()
                        .map(post -> {
                            HomePostVO homePostVO = new HomePostVO();
                            BeanUtils.copyProperties(post, homePostVO);
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

}