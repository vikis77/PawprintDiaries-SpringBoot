package com.qin.catcat.unite.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.mapper.CoordinateMapper;
import com.qin.catcat.unite.param.UploadCoordinateParam;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.service.CatLocationService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 猫咪坐标服务实现类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:18
 */
@Service
@Slf4j
public class CatLocationServiceImpl implements CatLocationService {

    @Autowired private CoordinateMapper coordinateMapper;
    @Autowired private CatMapper catMapper;
    @Autowired private GeneratorIdUtil generatorIdUtil;
    @Autowired 
    private CacheUtils cacheUtils;
    
    /**
     * 新增猫猫坐标
     * @param uploadCoordinateParam 新增猫猫坐标参数
     */
    @Override
    public void addCoordinate(UploadCoordinateParam uploadCoordinateParam) {
        Coordinate coordinate = new Coordinate();
        BeanUtils.copyProperties(uploadCoordinateParam, coordinate);
        // 1、插入数据库
        coordinateMapper.insert(coordinate);
        // 2、更新缓存
        cacheUtils.remove(Constant.HOT_FIRST_TIME_COORDINATE_LIST);
        log.info("新增猫猫坐标完成");
    }

    @Override
    public void deleteCoordinate(List<Long> ids) {
        if(CollectionUtils.isEmpty(ids)){
            log.info("猫猫ID列表为空");
            //TODO throw new IllegalArgumentException("猫猫ID列表不能为空");
            return;
        }

        int rows = coordinateMapper.deleteBatchIds(ids);
        if(rows <= 0){
            log.info("删除失败");
            //TODO throw new RuntimeException("删除猫猫坐标失败");
        }
        log.info("删除猫猫坐标完成");
    }

    @Override
    public void updateCoordinate(Coordinate coordinate) {
        coordinateMapper.updateById(coordinate);
        log.info("更新猫猫坐标完成");
    }

    /* 
     * 获取所有猫咪最新位置
     * 
     * @return 所有猫咪最新位置集合
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<CoordinateVO> selectCoordinate() {
        // 从缓存中获取数据，如果缓存中没有数据，则从数据库中查询
        List<CoordinateVO> coordinateVOs = cacheUtils.getWithMultiLevel(Constant.HOT_FIRST_TIME_COORDINATE_LIST, List.class, 
                () -> coordinateMapper.getAllCatsWithLatestCoordinates());
        // 按时间排序
        coordinateVOs.sort(Comparator.comparing(CoordinateVO::getUpdateTime).reversed());
        log.info("获取所有猫咪最新位置完成,共查询到{}条记录", coordinateVOs.size());
        return coordinateVOs;
    }

    @Override
    public IPage<CoordinateVO> selectCoordinateByCatId(Long catId, int page, int size) {
        Page<CoordinateVO> pageObj = new Page<>(page, size);
        IPage<CoordinateVO> coordinateVOIPage = coordinateMapper.selectCoordinatesByCatId(pageObj, catId);
        log.info("分页查询单只猫的历史坐标信息完成");
        return coordinateVOIPage;
    }

    @Override
    public List<CoordinateVO> selectCoordinateByDate(String date) {
        // 构建查询条件 - 使用LIKE进行日期匹配
        QueryWrapper<Coordinate> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("update_time", date)
                   .orderByDesc("update_time"); // 按时间倒序排序
        // 执行查询
        List<Coordinate> coordinates = coordinateMapper.selectList(queryWrapper);
        // 使用HashSet存储已处理的猫ID
        Set<Integer> processedCatIds = new HashSet<>();
        List<CoordinateVO> coordinateVOs = new ArrayList<>();
        // 遍历所有坐标记录
        for(Coordinate coordinate : coordinates) {
            Integer catId = coordinate.getCatId();
            // 如果这只猫还没处理过
            if(!processedCatIds.contains(catId)) {
                CoordinateVO vo = new CoordinateVO();
                BeanUtils.copyProperties(coordinate, vo);
                // 查询猫名
                Cat cat = catMapper.selectById(catId);
                if(cat != null) {
                    vo.setCatName(cat.getCatname());
                }
                coordinateVOs.add(vo);
                processedCatIds.add(catId);
            }
        }
        log.info("按日期{}查询小猫坐标信息完成,共查询到{}条记录", date, coordinateVOs.size());
        return coordinateVOs;
    }

    @Override
    public List<CoordinateVO> selectCoordinateByDateAndCatId(String date, Long catId) {
        QueryWrapper<Coordinate> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("update_time", date)
                   .eq("cat_id", catId)
                   .orderByDesc("update_time"); // 按时间倒序排序
        List<Coordinate> coordinates = coordinateMapper.selectList(queryWrapper);
        List<CoordinateVO> coordinateVOs = new ArrayList<>();
        
        // 查询猫名
        Cat cat = catMapper.selectById(catId);
        String catName = cat != null ? cat.getCatname() : null;
        
        for(Coordinate coordinate : coordinates){
            CoordinateVO vo = new CoordinateVO();
            BeanUtils.copyProperties(coordinate, vo);
            vo.setCatName(catName); // 设置猫名
            coordinateVOs.add(vo);
        }
        return coordinateVOs;
    }
} 