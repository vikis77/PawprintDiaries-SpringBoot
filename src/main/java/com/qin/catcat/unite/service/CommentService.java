package com.qin.catcat.unite.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.popo.entity.Comment;

public interface CommentService {
    /**
    * 根据帖子ID分页查询前十条评论(按评论时间 最新)
    * @param 
    * @return 
    */
    IPage<Comment> getCommentByPostidOrderByDescTime(Long postId,int page,int size);

    /**
    * 根据帖子ID分页查询前十条评论(按点赞数 最多)
    * @param 
    * @return 
    */
    IPage<Comment> getCommentByPostidOrderByDescLikecount(Long postId,int page,int size);

    /**
     * 根据父评论ID分页查询子评论
     * @param fatherId 父评论ID
     * @param page 当前页数
     * @param size 每页条数
     * @return 分页后的子评论列表
     */
    IPage<Comment> getCommentByFatheridByDescTime(Long fatherId,int page,int size);

    
    /**
    * 新增评论
    * @param 
    * @return 
    */ 
    Boolean addComment(Comment comment);

    /**
    * 删除评论
    * @param 
    * @return 
    */
    Boolean deleteComment(Long commentId);
}
