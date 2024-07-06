package com.qin.catcat.unite.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.Comment;

@Mapper
public interface CommentMapper extends BaseMapper<Comment>{
    @Select("SELECT * FROM comment WHERE comment_id IN (" +
            "SELECT son_id FROM comment_relationship WHERE father_id = #{fatherId}) " +
            "ORDER BY comment_time DESC")
    IPage<Comment> selectCommentsByFatherId(Page<Comment> page, @Param("fatherId") Long fatherId);
}
