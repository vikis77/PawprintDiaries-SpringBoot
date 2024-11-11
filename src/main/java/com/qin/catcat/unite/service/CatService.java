package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.param.UploadCoordinateParam;
// import com.github.pagehelper.PageInfo;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.dto.DonateDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;

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
    * 根据猫猫ID查找猫猫图片
    * @param 
    * @return 
    */
    public List<CatPics> selectPhotoById(String ID, int page, int size);

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

    /**
    * 新增猫猫坐标
    * @param 
    * @return 
    */
    public void addCoordinate(CoordinateDTO coordinateDTO);

    /**
    * 删除猫猫坐标
    * @param 
    * @return 
    */
    public void deleteCoordinate(List<Long> ids);

    /**
    * 修改坐标信息
    * @param 
    * @return 
    */
    public void updateCoordinate(Coordinate coordinate);

    /**
    * 查找猫猫坐标 全部坐标信息（最新）
    * @param 
    * @return 
    */
    public List<CoordinateVO> selectCoordinate();

    /**
    * 查询单只猫的历史坐标信息（分页）
    * @param 
    * @return 
    */
    public IPage<CoordinateVO> selectCoordinateByCatId(Long cat_id,int page,int size);

    /**
    * 数据分析
    * @param 
    * @return 
    */
    public DataAnalysisVO analysis();

    /**
    * 新增上传表单坐标
    * @param 
    * @return 
    */
    public int addUploadCoordinate(UploadCoordinateParam uploadCoordinateParam);
}
