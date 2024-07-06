package com.qin.catcat.unite.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.Comment;
import com.qin.catcat.unite.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService{
    /**
    * 根据帖子ID分页查询前十条评论
    * @param 
    * @return 
    */
    public IPage<Comment> getCommentByPostid(Long postId,int page,int size){
        Page<Comment> comment = new Page(page, size);
        
    }
}
