package com.qin.catcat.unite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.mapper.CommentMapper;
import com.qin.catcat.unite.popo.entity.Comment;
import com.qin.catcat.unite.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired CommentMapper commentMapper;
    /**
    * 根据帖子ID分页查询前十条评论(按评论时间 最新)
    * @param 
    * @return 
    */
    public IPage<Comment> getCommentByPostidOrderByDescTime(Long postId,int page,int size){
        Page<Comment> pageObj = new Page<>(page, size);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).orderByDesc("comment_time");
        IPage<Comment> comments = commentMapper.selectPage(pageObj, queryWrapper);
        return comments;
    }

    /**
    * 根据帖子ID分页查询前十条评论(按点赞数 最多)
    * @param 
    * @return 
    */
    public IPage<Comment> getCommentByPostidOrderByDescLikecount(Long postId,int page,int size){
        Page<Comment> pageObj = new Page<>(page, size);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).orderByDesc("like_count");
        IPage<Comment> comments = commentMapper.selectPage(pageObj, queryWrapper);
        return comments;
    }

    /**
     * 根据父评论ID分页查询子评论
     * @param fatherId 父评论ID
     * @param page 当前页数
     * @param size 每页条数
     * @return 分页后的子评论列表
     */
    public IPage<Comment> getCommentByFatheridByDescTime(Long fatherId,int page,int size){
        Page<Comment> commentPage = new Page<>(page, size);
        IPage<Comment> comments = commentMapper.selectCommentsByFatherId(commentPage, fatherId);
        return comments;
    }


    /**
    * 新增评论
    * @param 
    * @return 
    */ 
    public Boolean addComment(Comment comment){
        int signal = commentMapper.insert(comment);

        //TODO
        return true;
    }

    /**
    * 删除评论
    * @param 
    * @return 
    */
    public Boolean deleteComment(Long commentId){
        int signal = commentMapper.deleteById(commentId);
        //TODO
        return true;
    }
}
