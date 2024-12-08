package com.qin.catcat.unite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    // @Autowired
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
        // 1、尝试 ES 查询帖子，返回搜索结果（可能是空/ES帖子集合）
        List<EsPostIndex> postResults = esPostIndexRepository.findByTitleOrArticle(words, words);
        if (!postResults.isEmpty()) { // 如果存在ES帖子集合，尝试 MySQL 根据ES帖子ID查询帖子详细信息（分页第一页），返回搜索结果）
            log.info("ES搜索到 {} 个帖子", postResults.size());
            List<Long> postIds = postResults.stream().map(esPostIndex -> Long.parseLong(esPostIndex.getPostId())).toList(); // String to Long
            Page<Post> postsObj = new Page<>(page,size); // 创建分页对象
            IPage<HomePostVO> posts = postMapper.selectPostsByPostIdsOrderBySendtime(postsObj,postIds); // 前往MySQL分页查询结果
            searchVO.setPosts(posts); // 合并帖子结果
        }
        // 2、尝试 MySQL 查询猫猫(匹配catId或catName)，返回搜索结果（可能是空/猫猫ID集合）
        List<Cat> catIds = catMapper.selectCatByCatWords(words);
        if (!catIds.isEmpty()) {
            log.info("MySQL搜索到 {} 个猫猫", catIds.size());
            searchVO.setCats(catIds); // 合并猫猫结果
        }
        return searchVO;
    }

}