package com.qin.catcat.unite.popo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * 捐赠表DTO
 * @author qin
 * 去掉 时间
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonateDTO {
    /**
     * 金额
     */
    private Long amount;
    /**
     * 猫猫ID
     */
    private Long catId;
    /**
     * 猫猫名字
     */
    private String catName;
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    /**
     * 留言
     */
    private String message;
    /**
     * 捐赠人名字
     */
    private String name;
}
