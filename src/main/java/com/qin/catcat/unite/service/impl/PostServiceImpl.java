package com.qin.catcat.unite.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
    * 查询全部帖子
    * @param 
    * @return 
    */
    public List<Post> getAllPost(){
        List<Post> posts = postMapper.selectAllPost();
        return posts;
    }

    /**
    * 根据帖子ID查询帖子
    * @param 
    * @return 
    */
    public Post getPostByPostId(String PostId){
        Post post = postMapper.selectById(PostId);
        return post;
    }

    /**
    * 根据发布时间分页查询前十条帖子
    * @param 
    * @return 
    */
    public IPage<Post> getPostBySendtime(int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("send_time");
        IPage<Post> posts = postMapper.selectPage(postObj, queryWrapper);
        return posts;
    }

    /**
    * 根据点赞数分页查询前十条帖子
    * @param 
    * @return 
    */
    public IPage<Post> getPostByLikecount(int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("like_count");
        IPage<Post> posts = postMapper.selectPage(postObj, queryWrapper);
        return posts;
    }

    /**
    * 根据标题搜索相关帖子（匹配标题、匹配文章内容）分页搜索
    * @param 
    * @return 
    */
    public IPage<Post> getPostByTitle(String title,int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title",title).or().like("article", title);
        IPage<Post> posts = postMapper.selectPage(postObj, queryWrapper);
        return posts;
    }

    /**
    * 根据作者昵称搜索相关帖子
    * @param 
    * @return 
    */
    public IPage<Post> getPostByNickname(String nickName,int page,int pageSize){
        Page<Post> postObj = new Page<>(page, pageSize);
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("author_nickname", nickName);
        IPage<Post> posts = postMapper.selectPage(postObj,queryWrapper);
        return posts;
    }

    /**
    * 根据帖子ID删除帖子
    * @param 
    * @return 
    */
    public Boolean delete(String postId){
        int signal = postMapper.deleteById(Long.parseLong(postId));
        if(signal!=1){
            //TODO throw new
            return false;
        }else{
            return true;
        }
    }

    /**
    * 更新帖子
    * @param 
    * @return 
    */
    public int update(Post post){
        int signal = postMapper.updateById(post);
        return signal;
    }
}
