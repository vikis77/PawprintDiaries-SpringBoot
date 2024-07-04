package com.qin.catcat.unite.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.Post;

@Mapper
public interface PostMapper extends BaseMapper<Post>{
    @Select("select * from post")
    List<Post> selectAllPost();
}
