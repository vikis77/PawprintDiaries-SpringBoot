package com.qin.catcat.unite.popo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "post_pics")
public class PostPics {
    @TableId(value = "id",type = IdType.INPUT)
    private Long id;
    private Long postId;
    private String picture;
    private Integer picNumber;
}
