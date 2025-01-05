package com.qin.catcat.unite.param;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;

/**
 * @Description 添加资金记录参数
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-05 15:44
 */
@Data
public class AddFundRecordParam {
    private Integer id; // 主键ID   
    private Date date; // 日期
    private BigDecimal amount; // 金额
    private Integer type; // 类型：1：收入 2：支出
    private String description; // 描述
}
