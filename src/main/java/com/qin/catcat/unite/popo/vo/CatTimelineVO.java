package com.qin.catcat.unite.popo.vo;

import lombok.Data;

/**
 * @Description 小猫时间线VO.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-27 21:07
 */
@Data
public class CatTimelineVO {
    // 时间线ID
    private Integer id;
    // 时间线日期,格式:yyyy-MM-dd
    private String date;
    // 时间线标题
    private String title;
    // 时间线描述内容
    private String description;
}
