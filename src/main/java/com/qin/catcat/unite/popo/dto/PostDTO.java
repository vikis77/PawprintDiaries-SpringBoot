package com.qin.catcat.unite.popo.dto;

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
}
