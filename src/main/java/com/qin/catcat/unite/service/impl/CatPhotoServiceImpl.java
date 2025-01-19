package com.qin.catcat.unite.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.mapper.CatPicsMapper;
import com.qin.catcat.unite.popo.dto.AddCatPhotoDTO;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.popo.vo.AddCatPhotoVO;
import com.qin.catcat.unite.service.CatPhotoService;
import com.qin.catcat.unite.service.QiniuService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 猫咪图片服务实现类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:18
 */
@Service
@Slf4j
public class CatPhotoServiceImpl implements CatPhotoService {
    
    @Autowired
    private CatPicsMapper catPicsMapper;
    @Autowired
    private GeneratorIdUtil generatorIdUtil;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private QiniuService qiniuService;
    
    /**
     * @Description 新增猫咪照片
     * @param addCatPhotoDTO 新增猫咪照片DTO
     * @return 新增猫咪照片的URL
     */
    @Override
    public AddCatPhotoVO uploadPhoto(AddCatPhotoDTO addCatPhotoDTO) {
        Map<String, String> fileNameConvertMap = new HashMap<>();
        // 将图片名转换为新的文件名
        String newFileName = generatorIdUtil.GeneratorRandomId() + addCatPhotoDTO.getPictrueName().substring(addCatPhotoDTO.getPictrueName().lastIndexOf("."));
        fileNameConvertMap.put(addCatPhotoDTO.getPictrueName(), newFileName);
        // 将图片信息插入数据库
        CatPics catPics = new CatPics();
        catPics.setCatId(addCatPhotoDTO.getCatId());
        catPics.setUrl(newFileName);
        catPics.setUpdateUserId(Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken())));
        catPicsMapper.insert(catPics);
        AddCatPhotoVO addCatPhotoVO = new AddCatPhotoVO();
        addCatPhotoVO.setFileNameConvertMap(fileNameConvertMap);
        return addCatPhotoVO;
    }

    /**
     * @Description 新增猫咪照片（不改变原图片ID）
     * @param addCatPhotoDTO 新增猫咪照片DTO
     * @param originalPhotoId 原始照片ID
     * @return 新增猫咪照片的URL
     */
    public AddCatPhotoVO uploadPhoto(AddCatPhotoDTO addCatPhotoDTO, Integer originalPhotoId) {
        Map<String, String> fileNameConvertMap = new HashMap<>();
        // 将图片名转换为新的文件名
        String newFileName = generatorIdUtil.GeneratorRandomId() + addCatPhotoDTO.getPictrueName().substring(addCatPhotoDTO.getPictrueName().lastIndexOf("."));
        fileNameConvertMap.put(addCatPhotoDTO.getPictrueName(), newFileName);
        // 将图片信息插入数据库
        CatPics catPics = new CatPics();
        catPics.setId(originalPhotoId);
        catPics.setCatId(addCatPhotoDTO.getCatId());
        catPics.setUrl(newFileName);
        catPics.setUpdateUserId(Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken())));
        catPicsMapper.updateById(catPics);
        AddCatPhotoVO addCatPhotoVO = new AddCatPhotoVO();
        addCatPhotoVO.setFileNameConvertMap(fileNameConvertMap);
        return addCatPhotoVO;
    }

    /**
     * @Description 删除猫咪照片
     * @param catPhotoId 猫咪照片ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePhoto(Integer catPhotoId) {
        CatPics catPics = catPicsMapper.selectById(catPhotoId);
        // 删除七牛云图片
        qiniuService.deleteFile(catPics.getUrl(), "cat_pics");
        // 删除数据库图片记录
        catPicsMapper.deleteById(catPhotoId);
    }

    /**
     * @Description 更新猫咪照片
     * @param originalPhotoId 原始照片ID
     * @param addCatPhotoDTO 新增猫咪照片DTO
     */
    @Override
    public AddCatPhotoVO updatePhoto(Integer originalPhotoId, AddCatPhotoDTO addCatPhotoDTO) {
        // 1、删除原始图片
        CatPics catPics = catPicsMapper.selectById(originalPhotoId);
        qiniuService.deleteFile(catPics.getUrl(), "cat_pics");
        // 2、更新图片，不改变原图片ID：保持原顺序
        return uploadPhoto(addCatPhotoDTO, originalPhotoId);
    }
    
    @Override
    public List<CatPics> selectPhotoById(String catId, int page, int size) {
        Page<CatPics> pageObj = new Page<>(page, size);
        QueryWrapper<CatPics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_id", catId)
                   .orderByDesc("create_time");
        
        IPage<CatPics> result = catPicsMapper.selectPage(pageObj, queryWrapper);
        log.info("根据猫猫ID:{} 查找猫猫图片完成, 返回{}张图片", catId, result.getRecords().size());
        return result.getRecords();
    }
} 