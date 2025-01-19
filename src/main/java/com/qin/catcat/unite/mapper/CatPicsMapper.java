package com.qin.catcat.unite.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.CatPics;

import java.util.List;

@Mapper
public interface CatPicsMapper extends BaseMapper<CatPics> {
    /**
     * 批量插入猫咪图片
     */
    int insertBatch(@Param("list") List<CatPics> list);
}
