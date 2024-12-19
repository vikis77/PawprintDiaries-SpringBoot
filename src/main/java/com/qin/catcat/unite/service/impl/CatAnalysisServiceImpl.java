package com.qin.catcat.unite.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.service.CatAnalysisService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 猫咪数据分析服务实现类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:17
 */
@Service
@Slf4j
public class CatAnalysisServiceImpl implements CatAnalysisService {

    @Autowired
    private CatMapper catMapper;
    
    @Override
    public DataAnalysisVO analysis() {
        LambdaQueryWrapper<Cat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Cat::getIsDeleted, 0);
        List<Cat> cats = catMapper.selectList(queryWrapper);
        HashMap<String, Integer> ageDistribution = new HashMap<>();
        HashMap<String, Integer> healthStatus = new HashMap<>();
        HashMap<String, Integer> areaDistribution = new HashMap<>();
        HashMap<String, Integer> genderRatio = new HashMap<>();
        HashMap<String, Integer> sterilizationRatio = new HashMap<>();
        HashMap<String, Integer> vaccinationRatio = new HashMap<>();
        
        initializeMaps(ageDistribution, healthStatus, areaDistribution, 
                      genderRatio, sterilizationRatio, vaccinationRatio);
                      
        analyzeCats(cats, ageDistribution, healthStatus, areaDistribution,
                   genderRatio, sterilizationRatio, vaccinationRatio);
        
        return buildAnalysisResult(ageDistribution, healthStatus, areaDistribution,
                                 genderRatio, sterilizationRatio, vaccinationRatio);
    }
    
    private void initializeMaps(
            HashMap<String, Integer> ageDistribution,
            HashMap<String, Integer> healthStatus,
            HashMap<String, Integer> areaDistribution,
            HashMap<String, Integer> genderRatio,
            HashMap<String, Integer> sterilizationRatio,
            HashMap<String, Integer> vaccinationRatio) {
        
        // 初始化年龄分布
        ageDistribution.put("3个月以内", 0);
        ageDistribution.put("3-6个月", 0);
        ageDistribution.put("6-12个月", 0);
        ageDistribution.put("12-18个月", 0);
        ageDistribution.put("18-24个月", 0);
        ageDistribution.put("24个月以上", 0);
        
        // 初始化健康状态
        healthStatus.put("健康", 0);
        healthStatus.put("疾病", 0);
        healthStatus.put("营养不良", 0);
        healthStatus.put("肥胖", 0);
        
        // 初始化区域分布
        areaDistribution.put("北门", 0);
        areaDistribution.put("岐头", 0);
        areaDistribution.put("凤翔", 0);
        areaDistribution.put("厚德楼", 0);
        areaDistribution.put("香晖苑", 0);
        
        // 初始化性别比例
        genderRatio.put("公猫", 0);
        genderRatio.put("母猫", 0);
        
        // 初始化绝育比例
        sterilizationRatio.put("已绝育", 0);
        sterilizationRatio.put("未绝育", 0);
        
        // 初始化疫苗接种比例
        vaccinationRatio.put("已接种", 0);
        vaccinationRatio.put("未接种", 0);
    }
    
    private void analyzeCats(
            List<Cat> cats,
            HashMap<String, Integer> ageDistribution,
            HashMap<String, Integer> healthStatus,
            HashMap<String, Integer> areaDistribution,
            HashMap<String, Integer> genderRatio,
            HashMap<String, Integer> sterilizationRatio,
            HashMap<String, Integer> vaccinationRatio) {
        
        for(Cat cat : cats) {
            // 分析年龄分布
            Integer age = cat.getAge();
            if (age < 3) {
                ageDistribution.put("3个月以内", ageDistribution.get("3个月以内") + 1);
            } else if (age >= 3 && age < 6) {
                ageDistribution.put("3-6个月", ageDistribution.get("3-6个月") + 1);
            } else if (age >= 6 && age < 12) {
                ageDistribution.put("6-12个月", ageDistribution.get("6-12个月") + 1);
            } else if (age >= 12 && age < 18) {
                ageDistribution.put("12-18个月", ageDistribution.get("12-18个月") + 1);
            } else if (age >= 18 && age < 24) {
                ageDistribution.put("18-24个月", ageDistribution.get("18-24个月") + 1);
            } else {
                ageDistribution.put("24个月以上", ageDistribution.get("24个月以上") + 1);
            }

            // 分析健康状态
            healthStatus.put(cat.getHealthStatus(), healthStatus.get(cat.getHealthStatus()) + 1);

            // 分析区域分布
            areaDistribution.put(cat.getArea(), areaDistribution.get(cat.getArea()) + 1);

            // 分析性别比例
            if (cat.getGender().equals(1)) {
                genderRatio.put("公猫", genderRatio.get("公猫") + 1);
            } else {
                genderRatio.put("母猫", genderRatio.get("母猫") + 1);
            }

            // 分析绝育情况
            sterilizationRatio.put(cat.getSterilizationStatus(), 
                                 sterilizationRatio.get(cat.getSterilizationStatus()) + 1);

            // 分析疫苗接种情况
            vaccinationRatio.put(cat.getVaccinationStatus(), 
                               vaccinationRatio.get(cat.getVaccinationStatus()) + 1);
        }
    }
    
    private DataAnalysisVO buildAnalysisResult(
            HashMap<String, Integer> ageDistribution,
            HashMap<String, Integer> healthStatus,
            HashMap<String, Integer> areaDistribution,
            HashMap<String, Integer> genderRatio,
            HashMap<String, Integer> sterilizationRatio,
            HashMap<String, Integer> vaccinationRatio) {
        
        DataAnalysisVO dataAnalysisVO = new DataAnalysisVO();
        dataAnalysisVO.setAgeDistribution(ageDistribution);
        dataAnalysisVO.setHealthStatus(healthStatus);
        dataAnalysisVO.setAreaDistribution(areaDistribution);
        dataAnalysisVO.setGenderRatio(genderRatio);
        dataAnalysisVO.setSterilizationRatio(sterilizationRatio);
        dataAnalysisVO.setVaccinationRatio(vaccinationRatio);
        
        return dataAnalysisVO;
    }
} 