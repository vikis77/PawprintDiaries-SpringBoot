package com.qin.catcat.unite.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.PostComment;

/**
 * @Description 帖子评论Mapper.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 12:50
 */
@Mapper
public interface PostCommentMapper extends BaseMapper<PostComment> {
    @Select("SELECT * FROM post_comment WHERE id IN (" +
            "SELECT son_id FROM comment_relationship WHERE father_id = #{fatherId}) " +
            "ORDER BY create_time DESC")
    IPage<PostComment> selectCommentsByFatherId(Page<PostComment> page, @Param("fatherId") Long fatherId);
}
