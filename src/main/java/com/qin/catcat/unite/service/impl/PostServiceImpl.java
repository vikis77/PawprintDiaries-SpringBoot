package com.qin.catcat.unite.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.service.PostService;

@Service
public class PostServiceImpl implements PostService{

    @Autowired PostMapper postMapper;
    /**
    * 新增帖子
    * @param 
    * @return 
    */
    public Boolean add(Post post){
        int siginal = postMapper.insert(post);
        if(siginal!=1){
            //TODO throw new 
        }
        return true;
    }
}
