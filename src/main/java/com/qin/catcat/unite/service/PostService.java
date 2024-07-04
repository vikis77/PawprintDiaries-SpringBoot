package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.popo.entity.Post;

public interface PostService {
    /**
    * 新增帖子
    * @param 
    * @return 
    */
    public Boolean add(Post post);

    /**
    * 查询全部帖子
    * @param 
    * @return 
    */
    public List<Post> getAllPost();

    /**
    * 根据帖子ID查询帖子
    * @param 
    * @return 
    */
    public Post getPostByPostId(String PostId);

    /**
    * 根据发布时间分页查询前十条帖子
    * @param 
    * @return 
    */
    public IPage<Post> getPostBySendtime(int page,int pageSize);

    /**
    * 根据帖子ID删除帖子
    * @param 
    * @return 
    */
    public Boolean delete(String postId);

    /**
    * 更新帖子
    * @param 
    * @return 
    */
    public int update(Post post);
}
