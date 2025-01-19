package com.qin.catcat.unite.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.param.AddCatTimelineParam;
import com.qin.catcat.unite.param.AdoptParam;
import com.qin.catcat.unite.param.UpdateCatTimelineParam;
import com.qin.catcat.unite.param.UploadCoordinateParam;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.AddCatVO;
import com.qin.catcat.unite.popo.vo.CatListVO;
import com.qin.catcat.unite.popo.vo.CatTimelineVO;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.popo.vo.UpdateCatVO;

public interface CatService {
    /**
     * 获取所有猫咪列表
     */
    List<CatListVO> CatList();

    /**
     * 根据ID获取猫咪信息
     */
    Cat getById(Long id);

    /**
     * 创建新猫咪
     */
    AddCatVO createCat(CatDTO catDTO);

    /**
     * 更新猫咪信息
     */
    UpdateCatVO update(Cat cat);

    /**
     * 删除猫咪
     */
    void delete(Long id);

    /**
     * 点赞小猫
     */
    void likeCat(Long catId);

    /**
     * 领养小猫
     */
    void adoptCat(AdoptParam adoptParam);

    /**
     * 上传猫咪照片
     */
    String uploadPhoto(Long catId, MultipartFile file);

    /**
     * 获取猫咪照片列表
     */
    List<String> getPhotos(Long catId);

    /**
     * 获取猫咪位置列表
     */
    List<CoordinateDTO> getLocations(Long catId);

    /**
     * 添加猫咪位置
     */
    void addCoordinate(CoordinateDTO coordinateDTO);

    /**
     * 删除猫咪位置
     */
    void deleteCoordinate(List<Long> ids);

    /**
     * 更新猫咪位置
     */
    void updateCoordinate(Coordinate coordinate);

    /**
     * 获取所有猫咪位置
     */
    List<CoordinateVO> selectCoordinate();

    /**
     * 分页获取猫咪位置
     */
    IPage<CoordinateVO> selectCoordinateByCatId(Long catId, int page, int size);

    /**
     * 根据日期获取猫咪位置
     */
    List<CoordinateVO> selectCoordinateByDate(String date);

    /**
     * 根据日期和猫咪ID获取位置
     */
    List<CoordinateVO> selectCoordinateByDateAndCatId(String date, Long catId);

    /**
     * 添加上传的坐标信息
     */
    void addUploadCoordinate(UploadCoordinateParam param);

    /**
     * 获取数据分析结果
     */
    DataAnalysisVO analysis();

    /**
     * 分页获取猫咪列表
     */
    IPage<Cat> selectByPage(int page, int size);

    /**
     * 根据名字查找猫咪
     */
    List<Cat> selectByName(String name);

    /**
     * 分页获取猫咪照片
     */
    List<CatPics> selectPhotoById(String catId, int page, int size);

    /**
     * 获取猫咪时间线
     */
    List<CatTimelineVO> getCatTimeline(Long catId);

    /**
     * 新增猫咪时间线
     */
    void addCatTimeline(AddCatTimelineParam param);

    /**
     * 更新猫咪时间线
     */
    void updateCatTimeline(UpdateCatTimelineParam param);

    /**
     * 删除猫咪时间线
     */
    void deleteCatTimeline(Integer id);
}
