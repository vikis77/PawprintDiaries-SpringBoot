package com.qin.catcat.unite.popo.dto;

import com.qin.catcat.unite.param.AddFundRecordParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 添加资金记录DTO
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-05 15:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddFundRecordDTO extends AddFundRecordParam{
    private Integer userId; // 用户ID
}
