package com.qin.catcat.unite.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.CoordinateVO;

/**
 * 坐标表Mapper
 */
@Mapper
public interface CoordinateMapper extends BaseMapper<Coordinate> {

        /**
         * 查询全部猫猫坐标
         * (SQL优化 延迟关联)
         * @return
         */
        List<CoordinateVO> getAllCatsWithLatestCoordinates();

        /**
         * 分页查询单只猫的历史坐标信息
         * 
         * @param catId
         * @return
         */
        IPage<CoordinateVO> selectCoordinatesByCatId(Page<?> page, @Param("catId") Long catId);
}
