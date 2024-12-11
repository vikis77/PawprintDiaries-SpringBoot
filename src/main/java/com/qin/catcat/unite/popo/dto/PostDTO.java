package com.qin.catcat.unite.popo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * 帖子表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    //标题
    private String title;
    //文章
    private String article;
    // 图片集合
    private List<String> pictrueList;
}
