package com.qin.catcat.unite.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.popo.entity.Comment;

public interface CommentService {
    /**
    * 根据帖子ID分页查询前十条评论
    * @param 
    * @return 
    */
    IPage<Comment> getCommentByPostid(Long postId);
}
