package com.qin.catcat.unite.popo.vo;

import java.math.BigDecimal;
import java.sql.Date;

import com.qin.catcat.unite.popo.entity.FundRecord;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 资金VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-05 16:16
 */
@Data
@NoArgsConstructor
public class FundRecordVO {
    private Integer id; // 主键ID
    private Date date; // 日期
    private BigDecimal amount; // 金额
    private Integer type; // 类型：1：收入 2：支出
    private String description; // 描述

    public FundRecordVO(FundRecord fundRecord) {
        this.id = fundRecord.getId();
        this.date = fundRecord.getDate();
        this.amount = fundRecord.getAmount();
        this.type = fundRecord.getType();
        this.description = fundRecord.getDescription();
    }
}
