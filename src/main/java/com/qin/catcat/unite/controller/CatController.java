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
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.param.UploadCoordinateParam;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.service.CatService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/cat")
@Tag(name = "猫猫接口")
@Slf4j
// @CrossOrigin(origins = "https://pawprintdiaries.luckyiur.com") // 允许的来源
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
    public Result<List<Cat>> getAll(){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求查找全部猫猫信息");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求查找全部猫猫信息",username);
        }

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
    @GetMapping("/findById")
    public Result<Cat> getById(@RequestParam String ID) {
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求查询某只猫猫信息");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求查询某只猫猫信息",username);
        }
        Cat cat = catService.selectById(ID);
        log.info("根据猫猫ID查找某一只猫猫信息完成,结果为 {}",cat);
        return Result.success(cat);
    }

    /**
    * 查询某只猫猫的照片（分页）
    * @param 
    * @return 
    */
    @GetMapping("/findPhotoByIdforPage")
    public Result<List<CatPics>> getPhotoByIdforPage(@RequestParam String catId,@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size) {
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求查询某只猫猫的照片");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求查询某只猫猫的照片",username);
        }
        List<CatPics> picsList = catService.selectPhotoById(catId,page,size);
        return Result.success(picsList);
    }
    
    /**
    * 更新某只猫信息
    * @param 
    * @return 
    */
    @PostMapping("/update")
    public Result<?> update(@RequestBody Cat cat){
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求更新{}猫信息",username,cat.getCatname());
        log.info("{}",cat);
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

    /**
    * 新增猫猫坐标 (遍历猫名列表)
    * @param 
    * @return 
    */
    // TODO 暂时弃用
    @PostMapping("/addCoordinate")
    public Result<?> addCoordinate(@RequestBody CoordinateDTO coordinateDTO){
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求新增猫猫坐标",username);

        catService.addCoordinate(coordinateDTO);
        return Result.success();
    }

    /**
    * 删除坐标信息
    * @param 坐标信息id的列表
    * @return 
    */
    @DeleteMapping("/deleteCoordinate")
    public Result<?> deleteCoordinate(@RequestParam List<Long> ids){
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求删除猫猫坐标",username);

        catService.deleteCoordinate(ids);
        return Result.success();
        
    }

    /**
    * 修改坐标信息
    * @param 
    * @return 
    */
    @PutMapping("/updateCoordinate")
    public Result<?> updateCoordinate(@RequestBody Coordinate coordinate){
        String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
        log.info("用户{}请求修改猫猫坐标",username);

        catService.updateCoordinate(coordinate);
        return Result.success();
    }

    /**
    * 查找猫猫坐标 全部坐标信息（最新）
    * @param 
    * @return 
    */
    @GetMapping("/findCoordinate")
    public Result<List<CoordinateVO>> findCoordinate(){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求查找全部猫猫坐标");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求查找猫猫坐标",username);
        }

        List<CoordinateVO> coordinates = catService.selectCoordinate();
        return Result.success(coordinates);
    }

    /**
    * 查询单只猫的历史坐标信息（分页）
    * @param 
    * @return 
    */
    @GetMapping("/findCoordinateByPage")
    public Result<IPage<CoordinateVO>> findCoordinateByPage(@RequestParam Long catId,@RequestParam(defaultValue = "1") int page,@RequestParam(defaultValue = "10") int size){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求查询单只猫的历史坐标信息");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求查询单只猫的历史坐标信息",username);
        }

        IPage<CoordinateVO> coordinates = catService.selectCoordinateByCatId(catId,page,size);
        return Result.success(coordinates);
    }

    /**
    * 按日期查询小猫坐标信息
    * @param date 日期
    * @return 
    */
    @GetMapping("/findCoordinateByDate")
    public Result<List<CoordinateVO>> findCoordinateByDate(@RequestParam String date){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求按日期查询小猫坐标信息");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求按日期查询小猫坐标信息",username);
        }

        List<CoordinateVO> coordinates = catService.selectCoordinateByDate(date);
        return Result.success(coordinates);
    }

    /**
    * 按日期和猫猫ID查询坐标信息
    * @param date 日期
    * @param catId 猫猫ID
    * @return 
    */
    @GetMapping("/findCoordinateByDateAndCat")
    public Result<List<CoordinateVO>> findCoordinateByDateAndCat(@RequestParam String date,@RequestParam Long catId){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求按日期和猫猫ID查询坐标信息");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求按日期和猫猫ID查询坐标信息",username);
        }

        List<CoordinateVO> coordinates = catService.selectCoordinateByDateAndCatId(date,catId);
        return Result.success(coordinates);
    }


    /**
    * 上传小猫Map表单
    * @param 
    * @return 
    */
    @PostMapping("/uploadCatCoordinate")
    public Result<?> uploadCoordinate(@RequestBody UploadCoordinateParam uploadCoordinateParam) {
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求上传小猫Map表单");
        } else {
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求上传小猫Map表单，表单：{}",username,uploadCoordinateParam);
        }
        catService.addUploadCoordinate(uploadCoordinateParam);
        return Result.success();
    }
    

    /**
    * 数据分析接口
    * @param 
    * @return 
    */
    @GetMapping("/analysis")
    public Result<DataAnalysisVO> analysis(){
        if (TokenHolder.getToken() == null) {
            log.info("未登录用户请求数据分析");
        }
        else{
            String username = jwtTokenProvider.getUsernameFromToken(TokenHolder.getToken());
            log.info("用户{}请求数据分析",username);
        }
        DataAnalysisVO resVo = catService.analysis();
        return Result.success(resVo);
    }
}
