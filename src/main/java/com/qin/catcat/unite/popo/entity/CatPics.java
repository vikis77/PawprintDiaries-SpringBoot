package com.qin.catcat.unite.popo.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class CatPics {
    private Long Id;
    private Long catId;
    private String url;
    private Timestamp createTime;
    private Timestamp updateTime;
}
