package com.qin.catcat.unite.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.CatComment;

/**
 * @Description 小猫评论Mapper接口
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
@Mapper
public interface CatCommentMapper extends BaseMapper<CatComment> {
} 