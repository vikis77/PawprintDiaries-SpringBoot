package com.qin.catcat.unite.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.github.pagehelper.PageHelper;
// import com.github.pagehelper.PageInfo;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.service.CatService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CatServiceImpl implements CatService{
    @Autowired CatMapper catMapper;
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
}
