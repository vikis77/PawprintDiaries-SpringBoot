package com.qin.catcat.unite.service;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.param.UploadCoordinateParam;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.CoordinateVO;

/**
 * @Description 猫咪坐标服务接口
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:19
 */
public interface CatLocationService {
    /**
     * 添加坐标
     */
    void addCoordinate(UploadCoordinateParam uploadCoordinateParam);
    /**
     * 删除坐标
     */ 
    void deleteCoordinate(List<Long> ids);
    /**
     * 更新坐标
     */ 
    void updateCoordinate(Coordinate coordinate);
    /**
     * 查询坐标
     */ 
    List<CoordinateVO> selectCoordinate();
    /**
     * 根据猫猫ID查询坐标
     */ 
    IPage<CoordinateVO> selectCoordinateByCatId(Long catId, int page, int size);
    /**
     * 根据日期查询坐标
     */ 
    List<CoordinateVO> selectCoordinateByDate(String date);
    /**
     * 根据日期和猫猫ID查询坐标
     */ 
    List<CoordinateVO> selectCoordinateByDateAndCatId(String date, Long catId);
} 