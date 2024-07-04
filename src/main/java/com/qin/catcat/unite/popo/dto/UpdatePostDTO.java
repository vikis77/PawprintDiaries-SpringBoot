package com.qin.catcat.unite.popo.dto;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class UpdatePostDTO {
    //主键ID
    private Long postId;
    //标题
    private String title;
    //文章
    private String article;
}
