package com.qin.catcat.unite.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.vo.HomePostVO;

@Mapper
public interface PostMapper extends BaseMapper<Post>{
    @Select("select * from post")
    List<Post> selectAllPost();

    // @Select("SELECT p.* FROM post p JOIN user u ON p.author_id = u.user_id WHERE u.nickname LIKE #{nickName}")
    // IPage<Post> selectPostsByUserNickname(Page<Post> pageObj,String nickName);

    /**
    * 根据发布时间分页查询前十条帖子
    * @param 
    * @return 
    */
    @Select("select p.post_id, p.author_id, u.nick_name as author_nickname, u.avatar as author_avatar,p.title, p.cover_picture, p.like_count "+
            "FROM post p "+
            "LEFT JOIN user u ON p.author_id = u.user_id "+
            "ORDER BY p.send_time DESC")
    @Results({
        @Result(column = "post_id", property = "postId"),
        @Result(column = "author_id", property = "authorId"),
        @Result(column = "author_nickname", property = "authorNickname"),
        @Result(column = "title", property = "title"),
        @Result(column = "cover_picture", property = "coverPicture"),
        @Result(column = "like_count", property = "likeCount"),
        @Result(column = "author_avatar", property = "authorAvatar")
    })
    IPage<HomePostVO> selectPostsBySendtime(Page<?> page);
}
