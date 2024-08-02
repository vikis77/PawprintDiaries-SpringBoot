package com.qin.catcat.unite.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
// import com.github.pagehelper.PageHelper;
// import com.github.pagehelper.PageInfo;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.mapper.CoordinateMapper;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.service.CatService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CatServiceImpl  extends ServiceImpl<CoordinateMapper, Coordinate> implements CatService{
    @Autowired CatMapper catMapper;
    @Autowired CoordinateMapper coordinateMapper;
    @Autowired GeneratorIdUtil generatorIdUtil;

    /**
    * 新增猫猫信息
    * @param 
    * @return 
    */
    public void add(CatDTO catDTO){
        Cat cat = new Cat();

        //属性拷贝DTO to entity
        BeanUtils.copyProperties(catDTO, cat);

        //生成ID
        Long ID = Long.parseLong(generatorIdUtil.GeneratorRandomId());
        log.info("生成ID {}",ID);

        cat.setCatId(ID);
        catMapper.insert(cat);
        log.info("新增完成");
    }

    /**
    * 查找全部猫猫信息
    * @param 
    * @return 
    */
    // 缓存所有猫猫信息
    @Cacheable(value = "allCats")
    public List<Cat> selectAll(){
        List<Cat> cats = catMapper.findAll();
        log.info("查找全部猫猫信息完成");
        return cats;
    }

    /**
     * 分页查找全部猫猫信息
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 分页结果
     */
    public IPage<Cat> selectByPage(int page,int size){
        Page<Cat> pageObj = new Page<>(page,size);
        QueryWrapper<Cat> wrapper = new QueryWrapper<>();
        IPage<Cat> pageInfo = catMapper.selectPage(pageObj, wrapper);
        log.info("查找全部猫猫信息完成");
        return pageInfo;
    }

    /**
    * 根据猫猫名字查找猫猫信息 可能有多只
    * @param 
    * @return 
    */
    public List<Cat> selectByName(String name){
        if (name == null || name.isEmpty()) {
            log.warn("猫猫名字不能为空");
            //TODO throw new 
            return null;
        }

        QueryWrapper<Cat> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("catname", name);
        List<Cat> cats = catMapper.selectList(queryWrapper);

        if (cats == null || cats.isEmpty()) {
            log.info("没有找到名字为 {} 的猫猫", name);
        } else {
            log.info("根据猫猫名字查找猫猫信息完成，找到 {} 只猫猫", cats.size());
        }

        log.info("根据猫猫名字查找猫猫信息完成");
        return cats;
    }

    /**
    * 根据猫猫ID查找某一只猫猫信息
    * @param 
    * @return 
    */
    public Cat selectById(String ID){
        Cat cat = catMapper.selectById(ID);
        log.info("根据猫猫ID查找某一只猫猫信息完成");
        return cat;
    }

    /**
    * 更新某只猫信息
    * @param 
    * @return 
    */
    public void update(Cat cat){
        int rows = catMapper.updateById(cat);
        if(rows<=0){
            log.info("更新失败");
            //TODO throw new 
        }
        log.info("更新{}猫信息完成",cat.getCatname());
    }

    /**
    * 根据猫猫ID删除信息
    * @param 
    * @return 
    */
    public void delete(Long ID){
        int row = catMapper.deleteById(ID);
        if(row<=0){
            log.info("删除失败");
            //TODO throw new 
        }
        log.info("根据猫猫ID删除信息完成");
    }

    /**
    * 新增猫猫坐标
    * @param coordinateDTO 坐标信息DTO
    * @return 
    */
    public void addCoordinate(CoordinateDTO coordinateDTO){
        // 获取列表中的猫猫名
        List<String> catNames = coordinateDTO.getCatNames();

        // 遍历猫名列表
        if (!CollectionUtils.isEmpty(catNames)) {
            for (String catName : catNames) {
            Coordinate coordinate = new Coordinate();
            //属性拷贝DTO to entity
            BeanUtils.copyProperties(coordinateDTO, coordinate);

            //生成ID并设置这一条坐标信息的唯一ID
            Long ID = Long.parseLong(generatorIdUtil.GeneratorRandomId());
            coordinate.setId(ID);

            //设置更新时间
            coordinate.setUpdateTime(Timestamp.from(Instant.now()));

            //根据猫猫名查找猫猫ID
            QueryWrapper<Cat> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("catname",catName);
            Cat cat = catMapper.selectOne(queryWrapper);

            if(cat==null){
                log.info("更新失败,猫猫名不存在" + catName);
                //TODO throw new 
                // throw new CatNotFoundException("猫猫名不存在: " + catName);
                continue;
            }
            coordinate.setCatId(cat.getCatId());
            
            //插入
            coordinateMapper.insert(coordinate);
            log.info("新增猫猫坐标完成");
            }
        }else{
            log.info("猫猫名列表为空");
            //TODO throw new 
        }
    }

    /**
    * 删除猫猫坐标
    * @param 
    * @return 
    */
    public void deleteCoordinate(List<Long> ids){
        if(CollectionUtils.isEmpty(ids)){
            log.info("猫猫ID列表为空");
            //TODO throw new IllegalArgumentException("猫猫ID列表不能为空");
        }

        //批量删除
        boolean removed = this.removeByIds(ids);
        if(!removed){
            log.info("删除失败");
            //TODO throw new RuntimeException("删除猫猫坐标失败");
        }
        log.info("删除猫猫坐标完成");
    }

    /**
    * 修改坐标信息
    * @param 
    * @return 
    */
    public void updateCoordinate(Coordinate coordinate){
        coordinateMapper.updateById(coordinate);
        log.info("更新猫猫坐标完成");
    }

    /**
    * 查找猫猫坐标 全部坐标信息（最新）
    * @param 
    * @return 
    */
    public List<CoordinateVO> selectCoordinate(){
        List<CoordinateVO> CoordinateVOs = coordinateMapper.getAllCatsWithLatestCoordinates();
        log.info("查找猫猫坐标完成");
        return CoordinateVOs;
    }

    /**
     * 分页查询单只猫的历史坐标信息
     * @param catId 猫ID
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 分页结果
     */
    public IPage<CoordinateVO> selectCoordinateByCatId(Long cat_id,int page,int size){
        Page<CoordinateVO> pageObj = new Page<>(page, size);
        IPage<CoordinateVO> coordinateVOIPage = coordinateMapper.selectCoordinatesByCatId(pageObj, cat_id);
        log.info("分页查询单只猫的历史坐标信息完成");
        return coordinateVOIPage;
    }
}
