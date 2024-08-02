package com.qin.catcat.unite.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.Cat;

@Mapper
public interface CatMapper extends BaseMapper<Cat>{
    
    //查找全部猫猫信息
    @Select("Select * from cat")
    List<Cat> findAll();

}