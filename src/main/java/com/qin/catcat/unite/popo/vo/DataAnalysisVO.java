package com.qin.catcat.unite.popo.vo;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DataAnalysisVO {
    // 月份列表
    private List<Integer> monthList;
    // 年龄分布 ["3个月以内","3-6个月","6-12个月","12-18个月","18-24个月","24个月以上"]
    private Object AgeDistribution;

    // 数量变化 
    private Object QuantityChange;

    // 健康状态 健康 疾病 营养不良 肥胖
    private Object HealthStatus;

    // 区域分布 北门 岐头 凤翔 厚德楼 香晖苑
    private Object AreaDistribution;

    // 性别比例 雄性 雌性
    private Object GenderRatio;

    // 绝育比例 绝育 未绝育
    private Object SterilizationRatio;

    // 接种比例 接种 未接种
    private Object VaccinationRatio;

    // 已领养数量
    private Integer adoptionCount;

    // 本月新增数量
    private Integer monthlyNewCount;

    // 资金余额 
    private BigDecimal fundBalance;

    // 本月支出
    private BigDecimal monthExpense; 

    // 本月收入
    private BigDecimal monthIncome;
}
