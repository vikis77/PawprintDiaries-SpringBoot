package com.qin.catcat.unite.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.PostPics;

@Mapper
public interface PostPicsMapper extends BaseMapper<PostPics>{
    
    /**
    * 根据帖子ID查询帖子全部图片
    * @param 
    * @return 
    */
    @Select("select * from post_pics where post_id = #{postId} order by pic_number desc")
    List<PostPics> selectByPostId(Long postId);
}
