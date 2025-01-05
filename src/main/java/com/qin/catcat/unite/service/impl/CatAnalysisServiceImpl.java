package com.qin.catcat.unite.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.sql.Date;
import java.math.BigDecimal;
import java.util.TreeMap;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.manage.CatManage;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.mapper.FundRecordMapper;
import com.qin.catcat.unite.popo.dto.AddFundRecordDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.FundRecord;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.popo.vo.FundCalculateVO;
import com.qin.catcat.unite.popo.vo.FundRecordVO;
import com.qin.catcat.unite.service.CatAnalysisService;
import com.qin.catcat.unite.service.CatService;

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
    @Autowired
    private CacheUtils cacheUtils;
    @Autowired
    private CatService catService;   
    @Autowired
    private CatManage catManage;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FundRecordMapper fundRecordMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 分析猫咪数据
     * 
     * @return 分析结果VO
     */
    @Override
    @SuppressWarnings("unchecked")
    public DataAnalysisVO analysis() {
        try {
            // 从缓存中获取数据
            Object cachedData = cacheUtils.getWithMultiLevel(Constant.HOT_FIRST_TIME_CAT_DATA_ANALYSIS, DataAnalysisVO.class, () -> {
                // 查询全部猫猫数据
                List<Cat> cats = cacheUtils.getWithMultiLevel(Constant.HOT_FIRST_TIME_CAT_LIST, List.class, () -> {
                    // 如果缓存中没有数据，则从数据库中查询
                    return catManage.getCatList();
                });
                
                // 初始化分析结果
                HashMap<String, Integer> ageDistribution = new HashMap<>(); // 年龄分布
                HashMap<String, Integer> healthStatus = new HashMap<>(); // 健康状态
                HashMap<String, Integer> areaDistribution = new HashMap<>(); // 区域分布
                HashMap<String, Integer> genderRatio = new HashMap<>(); // 性别比例
                HashMap<String, Integer> sterilizationRatio = new HashMap<>(); // 绝育比例
                HashMap<String, Integer> vaccinationRatio = new HashMap<>(); // 疫苗接种比例
                Integer monthlyNewCount = 0; // 本月新增数量
                BigDecimal fundBalance = BigDecimal.ZERO; // 资金余额
                BigDecimal monthExpense = BigDecimal.ZERO; // 本月支出
                BigDecimal monthIncome = BigDecimal.ZERO; // 本月收入
                
                // 初始化数据分析结果的Map
                initializeMaps(ageDistribution, healthStatus, areaDistribution, 
                            genderRatio, sterilizationRatio, vaccinationRatio,
                            monthlyNewCount, fundBalance, monthExpense, monthIncome);
                
                // 分析猫咪数据
                analyzeCats(cats, ageDistribution, healthStatus, areaDistribution,
                        genderRatio, sterilizationRatio, vaccinationRatio);
                
                // 构建数据分析VO
                DataAnalysisVO dataAnalysisVO = buildAnalysisResult(ageDistribution, healthStatus, areaDistribution,
                        genderRatio, sterilizationRatio, vaccinationRatio);
                
                // 计算已领养数量
                Integer adoptionCount = (int) cats.stream().filter(cat -> cat.getIsAdopted() == 1).count();
                dataAnalysisVO.setAdoptionCount(adoptionCount);

                // 获取本月的起始时间和结束时间
                LocalDate now = LocalDate.now();
                LocalDate firstDayOfMonth = now.withDayOfMonth(1);
                LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

                // 计算本月新增数量
                QueryWrapper<Cat> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("is_adopted", 0);
                queryWrapper.eq("is_deleted", 0);
                queryWrapper.between("create_time", Date.valueOf(firstDayOfMonth), Date.valueOf(lastDayOfMonth));
                monthlyNewCount = Math.toIntExact(catMapper.selectCount(queryWrapper));

                // 计算本月资金余额
                QueryWrapper<FundRecord> fundQueryWrapper = new QueryWrapper<>();
                fundQueryWrapper.eq("type", 1)
                            .eq("is_deleted", 0)
                            .between("date", Date.valueOf(firstDayOfMonth), Date.valueOf(lastDayOfMonth));
                fundBalance = fundRecordMapper.selectList(fundQueryWrapper).stream()
                        .map(FundRecord::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);  
                
                // 计算本月支出
                QueryWrapper<FundRecord> expenseQueryWrapper = new QueryWrapper<>();
                expenseQueryWrapper.eq("type", 2)
                                .eq("is_deleted", 0)
                                .between("date", Date.valueOf(firstDayOfMonth), Date.valueOf(lastDayOfMonth));
                monthExpense = fundRecordMapper.selectList(expenseQueryWrapper).stream()
                        .map(FundRecord::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // 计算本月收入
                QueryWrapper<FundRecord> incomeQueryWrapper = new QueryWrapper<>();
                incomeQueryWrapper.eq("type", 1)
                                .eq("is_deleted", 0)
                                .between("date", Date.valueOf(firstDayOfMonth), Date.valueOf(lastDayOfMonth));
                monthIncome = fundRecordMapper.selectList(incomeQueryWrapper).stream()
                        .map(FundRecord::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);  
                
                log.info("本月新增数量: {}", monthlyNewCount);
                log.info("本月资金余额: {}", fundBalance);
                log.info("本月支出: {}", monthExpense);
                log.info("本月收入: {}", monthIncome);
                dataAnalysisVO.setMonthlyNewCount(monthlyNewCount);
                dataAnalysisVO.setFundBalance(fundBalance);
                dataAnalysisVO.setMonthExpense(monthExpense);
                dataAnalysisVO.setMonthIncome(monthIncome);
                return dataAnalysisVO;
            });

            // 处理类型转换
            if (cachedData instanceof java.util.LinkedHashMap) {
                return objectMapper.convertValue(cachedData, DataAnalysisVO.class);
            } else if (cachedData instanceof DataAnalysisVO) {
                return (DataAnalysisVO) cachedData;
            }
            
            return new DataAnalysisVO();
        } catch (Exception e) {
            log.error("分析猫咪数据失败: ", e);
            return new DataAnalysisVO();
        }
    }
    
    /**
     * 初始化数据分析结果的Map
     */
    private void initializeMaps(
            HashMap<String, Integer> ageDistribution,
            HashMap<String, Integer> healthStatus,
            HashMap<String, Integer> areaDistribution,
            HashMap<String, Integer> genderRatio,
            HashMap<String, Integer> sterilizationRatio,
            HashMap<String, Integer> vaccinationRatio,
            Integer monthlyNewCount,
            BigDecimal fundBalance,
            BigDecimal monthExpense,
            BigDecimal monthIncome) {
        
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
        areaDistribution.put("未知", 0);
        
        // 初始化性别比例
        genderRatio.put("公猫", 0);
        genderRatio.put("母猫", 0);
        
        // 初始化绝育比例
        sterilizationRatio.put("已绝育", 0);
        sterilizationRatio.put("未绝育", 0);
        
        // 初始化疫苗接种比例
        vaccinationRatio.put("已接种", 0);
        vaccinationRatio.put("未接种", 0);

        // 初始化本月新增数量
        monthlyNewCount = 0;

        // 初始化资金余额
        fundBalance = BigDecimal.ZERO;
        monthExpense = BigDecimal.ZERO;
        monthIncome = BigDecimal.ZERO;
    }
    
    /**
     * 分析猫咪数据
     */
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
            String area = cat.getArea();
            if (area != null && areaDistribution.containsKey(area)) {
                areaDistribution.put(area, areaDistribution.get(area) + 1);
            } else {
                areaDistribution.put("未知", areaDistribution.get("未知") + 1);
                log.warn("发现未知区域: {}", area);
            }

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
    
    /**
     * 构建数据分析VO
     */
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

    /**
     * @Description 添加或更新资金记录
     */
    @Override
    public void addOrUpdateFundRecord(AddFundRecordDTO addFundRecordDTO) {
        // 获取当前用户ID
        Integer userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        FundRecord fundRecord = new FundRecord();
        BeanUtils.copyProperties(addFundRecordDTO, fundRecord);
        fundRecord.setCreateUserId(userId);
        fundRecord.setType(addFundRecordDTO.getType());
        fundRecord.setIsDeleted(0);
        fundRecordMapper.insertOrUpdate(fundRecord);
    }

    /**
     * 获取资金记录
     */
    @Override
    public List<FundRecordVO> getFundRecord(Integer type) {
        LambdaQueryWrapper<FundRecord> queryWrapper = new LambdaQueryWrapper<>();
        // 如果type为空或为0，则查询"收入和支出"的全部记录
        queryWrapper.eq(type != null && type != 0, FundRecord::getType, type);
        queryWrapper.eq(FundRecord::getIsDeleted, 0);
        // 先按记录日期降序排序,再按更新时间降序排序
        queryWrapper.orderByDesc(FundRecord::getDate)
                   .orderByDesc(FundRecord::getUpdateTime);
        List<FundRecord> fundRecords = fundRecordMapper.selectList(queryWrapper);
        return fundRecords.stream().map(FundRecordVO::new).collect(Collectors.toList());
    }

    /**
     * 删除资金记录
     */
    @Override
    public void deleteFundRecord(Integer id) {
        fundRecordMapper.deleteById(id);
    }

    /**
     * 计算资金统计数据
     * 根据传入的类型计算近6个月的资金数据
     * @param type 资金类型：救助资金剩余、资金支出、资金收入
     * @return 近6个月的资金统计数据列表
     */
    @Override
    public List<FundCalculateVO> calculateFund(String type) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 获取6个月前的日期
        LocalDate sixMonthsAgo = currentDate.minusMonths(6);
        
        // 创建查询条件
        QueryWrapper<FundRecord> queryWrapper = new QueryWrapper<>();
        // 设置时间范围条件
        queryWrapper.ge("date", Date.valueOf(sixMonthsAgo))
                   .le("date", Date.valueOf(currentDate))
                   .eq("is_deleted", 0);

        // 初始化最近6个月的数据
        Map<Integer, FundCalculateVO> monthlyData = new TreeMap<>();
        for (int i = 0; i < 6; i++) {
            LocalDate date = currentDate.minusMonths(i);
            int month = date.getMonthValue();
            FundCalculateVO vo = new FundCalculateVO();
            vo.setMonth(month);
            vo.setRemainingFund(BigDecimal.ZERO);
            vo.setTotalExpenses(BigDecimal.ZERO);
            vo.setTotalIncome(BigDecimal.ZERO);
            monthlyData.put(month, vo);
        }
        
        switch (type) {
            case "救助资金剩余":
                // 计算近6个月救助资金剩余数据
                List<FundRecord> allRecords = fundRecordMapper.selectList(queryWrapper);
                
                // 按月份分组计算收入和支出
                for (FundRecord record : allRecords) {
                    int month = record.getDate().toLocalDate().getMonthValue();
                    if (monthlyData.containsKey(month)) {
                        FundCalculateVO vo = monthlyData.get(month);
                        // 收入加上金额，支出减去金额
                        BigDecimal currentBalance = vo.getRemainingFund();
                        BigDecimal amount = record.getType() == 1 ? record.getAmount() : record.getAmount().negate();
                        vo.setRemainingFund(currentBalance.add(amount));
                    }
                }
                break;
                
            case "资金支出":
                // 计算近6个月资金支出数据
                queryWrapper.eq("type", 2); // 2表示支出
                List<FundRecord> expenseRecords = fundRecordMapper.selectList(queryWrapper);
                
                // 按月份分组计算
                for (FundRecord record : expenseRecords) {
                    int month = record.getDate().toLocalDate().getMonthValue();
                    if (monthlyData.containsKey(month)) {
                        FundCalculateVO vo = monthlyData.get(month);
                        BigDecimal currentExpenses = vo.getTotalExpenses();
                        vo.setTotalExpenses(currentExpenses.add(record.getAmount()));
                    }
                }
                break;
                
            case "资金收入":
                // 计算近6个月资金收入数据
                queryWrapper.eq("type", 1); // 1表示收入
                List<FundRecord> incomeRecords = fundRecordMapper.selectList(queryWrapper);
                
                // 按月份分组计算
                for (FundRecord record : incomeRecords) {
                    int month = record.getDate().toLocalDate().getMonthValue();
                    if (monthlyData.containsKey(month)) {
                        FundCalculateVO vo = monthlyData.get(month);
                        BigDecimal currentIncome = vo.getTotalIncome();
                        vo.setTotalIncome(currentIncome.add(record.getAmount()));
                    }
                }
                break;
                
            default:
                break;
        }
        
        // 将Map转换为List并返回
        return new ArrayList<>(monthlyData.values());
    }
} 