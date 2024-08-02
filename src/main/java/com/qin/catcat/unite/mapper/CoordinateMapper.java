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
         * 
         * @return
         */
        @Select("SELECT coord.id, coord.cat_id, c.catname, coord.longitude,coord.latitude,coord.update_time,coord.area,coord.description "
                        +
                        "FROM cat c " +
                        "LEFT JOIN ( " +
                        "  SELECT coord1.id, coord1.cat_id, coord1.longitude, coord1.latitude, coord1.update_time, coord1.area, coord1.description "
                        +
                        "  FROM coordinate coord1 " +
                        "  INNER JOIN ( " +
                        "    SELECT cat_id, MAX(update_time) AS latest_timestamp " +
                        "    FROM coordinate " +
                        "    GROUP BY cat_id " +
                        "  ) coord2 ON coord1.cat_id = coord2.cat_id AND coord1.update_time = coord2.latest_timestamp "
                        +
                        ") coord ON c.cat_id = coord.cat_id")
        List<CoordinateVO> getAllCatsWithLatestCoordinates();

        /**
         * 分页查询单只猫的历史坐标信息
         * 
         * @param catId
         * @return
         */
        @Select("SELECT coord.id, coord.cat_id, c.catname, coord.longitude,coord.latitude,coord.update_time,coord.area,coord.description "
                        +
                        "FROM cat c " +
                        "LEFT JOIN coordinate coord ON c.cat_id = coord.cat_id " +
                        "WHERE coord.cat_id = #{catId} " +
                        "ORDER BY coord.update_time DESC")
        IPage<CoordinateVO> selectCoordinatesByCatId(Page<?> page, @Param("catId") Long catId);
}
