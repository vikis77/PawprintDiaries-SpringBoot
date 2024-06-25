package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.github.pagehelper.PageInfo;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.service.CatService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/cat")
@Tag(name = "猫猫接口")
@Slf4j
public class CatController {
    @Autowired private CatService catService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    /**
    * 新增猫猫信息
    * @param 
    * @return 
    */
    @PostMapping("/add")
    public Result<?> add(@RequestHeader("Authorization") String Token,@RequestBody CatDTO catDTO){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求新增猫猫{}信息",username,catDTO.getCatname());

        catService.add(catDTO);
        return Result.success();
    }

    /**
    * 查找全部猫猫信息
    * @param 
    * @return 
    */
    @GetMapping("/findAll")
    public Result<List<Cat>> getAll(@RequestHeader("Authorization") String Token){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求查找全部猫猫信息",username);

        List<Cat> cats = catService.selectAll();
        return Result.success(cats);
    }

    /**
     * 分页查找全部猫猫信息
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/findByPage")
    public Result<IPage<Cat>> getByPage(@RequestHeader("Authorization") String Token, @RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求分页查找全部猫猫信息 {} {}",username,page,size);

        IPage<Cat> cats = catService.selectByPage(page,size);
        return Result.success(cats);
    }

    /**
    * 根据猫猫名字查找猫猫信息 可能有多只
    * @param 
    * @return 
    */
    @GetMapping("/findByName")
    public Result<List<Cat>> getByName(@RequestHeader("Authorization") String Token,@RequestParam String name) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求根据猫猫名字查找猫猫信息 {}",username,name);

        List<Cat> cats = catService.selectByName(name);
        return Result.success(cats);
    }
    
    /**
    * 根据猫猫ID查找某一只猫猫信息
    * @param 
    * @return 
    */
    @GetMapping("/fingById")
    public Result<Cat> getById(@RequestHeader("Authorization") String Token,@RequestParam String ID) {
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求根据猫猫ID查找某一只猫猫信息 {}",username,ID);

        Cat cat = catService.selectById(ID);
        return Result.success(cat);
    }
    
    /**
    * 更新某只猫信息
    * @param 
    * @return 
    */
    @PostMapping("/update")
    public Result<?> update(@RequestHeader("Authorization") String Token,@RequestBody Cat cat){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求更新{}猫信息",username,cat.getCatname());

        catService.update(cat);
        return Result.success();
    }

    /**
    * 根据猫猫ID删除信息
    * @param 
    * @return 
    */
    @GetMapping("delete")
    public Result<?> delete(@RequestHeader("Authorization") String Token,@RequestParam Long ID){
        String username = jwtTokenProvider.getUsernameFromToken(Token);
        log.info("用户{}请求删除猫猫{}信息",username,ID);

        catService.delete(ID);
        return Result.success();
    }
}
