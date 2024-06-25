package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.github.pagehelper.PageInfo;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.entity.Cat;

public interface CatService {
    /**
    * 新增猫猫信息
    * @param 
    * @return 
    */
    public void add(CatDTO cat);

    /**
    * 查找全部猫猫信息
    * @param 
    * @return 
    */
    public List<Cat> selectAll();

    /**
     * 分页查找全部猫猫信息
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 分页结果
     */
    public IPage<Cat> selectByPage(int page,int size);

    /**
    * 根据猫猫名字查找猫猫信息 可能有多只
    * @param 
    * @return 
    */
    public List<Cat> selectByName(String name);

    /**
    * 根据猫猫ID查找某一只猫猫信息
    * @param 
    * @return 
    */
    public Cat selectById(String ID);

    /**
    * 更新某只猫信息
    * @param 
    * @return 
    */
    public void update(Cat cat);

    /**
    * 根据猫猫ID删除信息
    * @param 
    * @return 
    */
    public void delete(Long ID);
}
