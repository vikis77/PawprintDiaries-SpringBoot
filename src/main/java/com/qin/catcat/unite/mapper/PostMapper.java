package com.qin.catcat.unite.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.popo.entity.EsPostIndex;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.vo.HomePostVO;

@Mapper
public interface PostMapper extends BaseMapper<Post>{
    @Select("select * from post")
    List<Post> selectAllPost();

    // @Select("SELECT p.* FROM post p JOIN user u ON p.author_id = u.user_id WHERE u.nickname LIKE #{nickName}")
    // IPage<Post> selectPostsByUserNickname(Page<Post> pageObj,String nickName);

    // /**
    // * 根据发布时间分页查询前十条帖子
    // * @param page 分页对象
    // * @param queryWrapper 查询条件
    // * @return 分页后的帖子列表
    // */
    // @Select({
    //     "<script>",
    //     "SELECT p.post_id, p.author_id, u.nick_name AS author_nickname, u.avatar AS author_avatar, ",
    //     "p.title, p.cover_picture, p.like_count ",
    //     "FROM post p ",
    //     "LEFT JOIN user u ON p.author_id = u.user_id ",
    //     "<where>",
    //     "    ${ew.customSqlSegment}",
    //     "</where>",
    //     "ORDER BY p.send_time DESC",
    //     "</script>"
    // })
    // @Results({
    //     @Result(column = "post_id", property = "postId"),
    //     @Result(column = "author_id", property = "authorId"),
    //     @Result(column = "author_nickname", property = "authorNickname"),
    //     @Result(column = "title", property = "title"),
    //     @Result(column = "cover_picture", property = "coverPicture"),
    //     @Result(column = "like_count", property = "likeCount"),
    //     @Result(column = "author_avatar", property = "authorAvatar")
    // })
    // IPage<HomePostVO> selectPostsBySendtime(Page<?> page, @Param("ew") QueryWrapper<Post> queryWrapper);

    /**
     * 用于搜索帖子，根据post_id查询帖子，返回字段为指定的HomePostVO字段内容，按时间降序分页查询
     * @param page 分页参数
     * @param postIds 帖子ID集合
     * @return 分页后的帖子内容
     */
    @Select({
        "<script>",
        "SELECT p.id, p.author_id, u.nick_name as author_nickname, u.avatar as author_avatar, ",
        "p.title, p.cover_picture, p.like_count ",
        "FROM post p ",
        "LEFT JOIN user u ON p.author_id = u.id ",
        "<where>",
        "  <if test='postIds != null and postIds.size() > 0'>",
        "    AND p.post_id IN ",
        "    <foreach item='id' collection='postIds' open='(' separator=',' close=')'>",
        "      #{id}",
        "    </foreach>",
        "  </if>",
        "</where>",
        "ORDER BY p.send_time DESC",
        "</script>"
    })
    @Results({
        @Result(column = "id", property = "postId"),
        @Result(column = "author_id", property = "authorId"),
        @Result(column = "author_nickname", property = "authorNickname"),
        @Result(column = "title", property = "title"),
        @Result(column = "cover_picture", property = "coverPicture"),
        @Result(column = "like_count", property = "likeCount"),
        @Result(column = "author_avatar", property = "authorAvatar")
    })
    IPage<HomePostVO> selectPostsByPostIdsOrderBySendtime(Page<?> page, @Param("postIds") List<Long> postIds);

    
    /**
    * // 从MySQL中取出需要同步的数据
    * @param 
    * @return 
    */
    @Select("SELECT id,title,article FROM POST")
    List<EsPostIndex> selectEsPostIndex();

}
