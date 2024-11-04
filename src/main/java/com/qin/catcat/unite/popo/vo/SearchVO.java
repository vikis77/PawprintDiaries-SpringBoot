package com.qin.catcat.unite.popo.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.qin.catcat.unite.popo.entity.Post;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.popo.entity.Cat;

/* 
 * 响应给前端的搜索结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchVO {
    private IPage<HomePostVO> posts; // 帖子集合
    private List<Cat> cats; // 猫猫集合
}
