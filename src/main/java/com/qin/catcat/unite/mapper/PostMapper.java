package com.qin.catcat.unite.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.Post;

@Mapper
public interface PostMapper extends BaseMapper<Post>{
    @Select("select * from post")
    List<Post> selectAllPost();

    // @Select("SELECT p.* FROM post p JOIN user u ON p.author_id = u.user_id WHERE u.nickname LIKE #{nickName}")
    // IPage<Post> selectPostsByUserNickname(Page<Post> pageObj,String nickName);
}
