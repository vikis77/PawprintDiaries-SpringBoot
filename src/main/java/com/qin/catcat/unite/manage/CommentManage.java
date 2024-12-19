package com.qin.catcat.unite.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.mapper.CatCommentMapper;
import com.qin.catcat.unite.mapper.PostCommentMapper;
import com.qin.catcat.unite.popo.entity.CatComment;
import com.qin.catcat.unite.popo.entity.PostComment;

/**
 * @Description 评论管理 - 公共方法.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 12:45
 */
@Component
public class CommentManage {
    @Autowired
    CatCommentMapper catCommentMapper;
    @Autowired
    PostCommentMapper postCommentMapper;
    /**
     * @Description 查询小猫待审核评论.
     */
    public List<CatComment> getCatCommentByDescTime(int page,int pageSize,String sort){
        QueryWrapper<CatComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 10);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("create_time");
        Page<CatComment> pageObj = new Page<>(page, pageSize);
        IPage<CatComment> comments = catCommentMapper.selectPage(pageObj, queryWrapper);
        return comments.getRecords();
    }

    /**
     * @Description 查询帖子待审核评论.
     */
    public List<PostComment> getPostCommentByDescTime(int page,int pageSize,String sort){
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 10);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("create_time");
        Page<PostComment> pageObj = new Page<>(page, pageSize);
        IPage<PostComment> comments = postCommentMapper.selectPage(pageObj, queryWrapper);
        return comments.getRecords();
    }
}
