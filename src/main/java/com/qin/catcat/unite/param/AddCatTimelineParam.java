package com.qin.catcat.unite.param;

import lombok.Data;

/**
 * @Description 新增猫咪时间线参数.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-27 21:12
 */
@Data
public class AddCatTimelineParam {
    // 猫咪ID
    private Integer catId;
    // 时间线日期,格式:yyyy-MM-dd
    private String date;
    // 时间线标题
    private String title;
    // 时间线描述内容
    private String description;
}
