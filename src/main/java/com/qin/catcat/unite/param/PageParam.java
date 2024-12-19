package com.qin.catcat.unite.param;

import lombok.Data;

/**
 * 分页参数
 */
@Data
public class PageParam {
    //页码
    private int page;
    //每页大小
    private int size;
}
