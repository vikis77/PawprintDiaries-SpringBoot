package com.qin.catcat.unite.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.popo.vo.SearchVO;
import com.qin.catcat.unite.service.SearchService;

import ch.qos.logback.core.subst.Token;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/search")
@Tag(name = "搜索模块")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private SearchService searchSerivce;

    @GetMapping("/search")
    public Result<SearchVO> searchPostsOrCats(@RequestParam String words, @RequestParam(defaultValue = "1") int page, @RequestParam (defaultValue = "10") int size) {
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求搜索 {} {} {}",username,words,page,size);

        SearchVO resVo = searchSerivce.searchForEsAndMysql(words,page,size);
        return Result.success(resVo);
    }
    
}
