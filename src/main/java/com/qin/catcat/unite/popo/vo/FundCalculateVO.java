package com.qin.catcat.unite.popo.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 资金统计数据VO 三个接口共用，没用到属性设置为null
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-05 17:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundCalculateVO {
    private Integer month; // 月份
    private BigDecimal remainingFund; // 救助资金剩余
    private BigDecimal totalExpenses; // 资金支出
    private BigDecimal totalIncome; // 资金收入
}
