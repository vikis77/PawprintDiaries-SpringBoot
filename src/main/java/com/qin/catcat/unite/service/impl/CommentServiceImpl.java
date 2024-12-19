package com.qin.catcat.unite.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.manage.CommentManage;
import com.qin.catcat.unite.mapper.CatCommentMapper;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.mapper.CommentLikeMapper;
import com.qin.catcat.unite.mapper.PostCommentMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.dto.AddPostCommentDTO;
import com.qin.catcat.unite.popo.entity.CatComment;
import com.qin.catcat.unite.popo.entity.CommentLike;
import com.qin.catcat.unite.popo.entity.PostComment;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.AuditCommentVO;
import com.qin.catcat.unite.popo.vo.CatCommentVO;
import com.qin.catcat.unite.popo.vo.PostCommentVO;
import com.qin.catcat.unite.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired PostCommentMapper postCommentMapper;
    @Autowired CatCommentMapper catCommentMapper;
    @Autowired CommentManage commentManage;
    @Autowired UserMapper userMapper;
    @Autowired CommentLikeMapper commentLikeMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    /**
    * 根据帖子ID分页查询前十条评论(按评论时间 最新)
    * @param 
    * @return 
    */
    public IPage<PostComment> getCommentByPostidOrderByDescTime(Long postId,int page,int size){
        Page<PostComment> pageObj = new Page<>(page, size);
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId).orderByDesc("comment_time");
        IPage<PostComment> comments = postCommentMapper.selectPage(pageObj, queryWrapper);
        return comments;
    }

    /**
    * 根据帖子ID分页查询前十条评论(按点赞数 最多)
    * @param 
    * @return 
    */
    public List<PostCommentVO> getCommentByPostidOrderByDescLikecount(Integer postId,int page,int size){
        Page<PostComment> pageObj = new Page<>(page, size);
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.eq("status", 20);
        queryWrapper.eq("post_id", postId).orderByDesc("like_count");
        List<PostComment> comments = postCommentMapper.selectPage(pageObj, queryWrapper).getRecords();

        List<PostCommentVO> postCommentVOList = new ArrayList<>();
        for(PostComment postComment : comments){
            User user = userMapper.selectById(postComment.getCommentUserId());
            PostCommentVO postCommentVO = new PostCommentVO();
            BeanUtils.copyProperties(postComment, postCommentVO);
            postCommentVO.setAvatar(user.getAvatar());
            postCommentVO.setNickName(user.getNickName());
            // 当前用户是否点赞
            if (TokenHolder.getToken() != null) {
                Integer userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
                QueryWrapper<CommentLike> queryCommentLikeWrapper = new QueryWrapper<>();
                queryCommentLikeWrapper.eq("type", 20);
                queryCommentLikeWrapper.eq("target_id", postId);
                queryCommentLikeWrapper.eq("user_id", userId);
                queryCommentLikeWrapper.eq("status", 1);
                CommentLike commentLike = commentLikeMapper.selectOne(queryCommentLikeWrapper);
                postCommentVO.setLiked(commentLike != null);
            } else {
                postCommentVO.setLiked(false);
            }
            postCommentVOList.add(postCommentVO);
        }
        return postCommentVOList;
    }

    /**
     * 根据父评论ID分页查询子评论
     * @param fatherId 父评论ID
     * @param page 当前页数
     * @param size 每页条数
     * @return 分页后的子评论列表
     */
    public IPage<PostComment> getCommentByFatheridByDescTime(Long fatherId,int page,int size){
        Page<PostComment> commentPage = new Page<>(page, size);
        IPage<PostComment> comments = postCommentMapper.selectCommentsByFatherId(commentPage, fatherId);
        return comments;
    }

    /**
     * 按时间分页查询待审核评论
     * @param page 页码
     * @param pageSize 每页大小
     * @param type 评论类型：all、post、cat
     * @param sort 排序方式：desc、asc
     * @return 待审核评论列表（分页）
     */
    public AuditCommentVO getAuditCommentByDescTime(int page,int pageSize,String type,String sort){
        if(type.equals("all")){
            // 查询小猫待审核评论
            List<CatComment> catComments = commentManage.getCatCommentByDescTime(page, pageSize, sort);
            // 将小猫评论转换为VO
            List<CatCommentVO> catCommentVOList = new ArrayList<>();
            for(CatComment catComment : catComments){
                // 获取评论者
                User user = userMapper.selectById(catComment.getCommentUserId());
                CatCommentVO catCommentVO = new CatCommentVO();
                BeanUtils.copyProperties(catComment, catCommentVO);
                catCommentVO.setAvatar(user.getAvatar());
                catCommentVO.setNickName(user.getNickName());
                catCommentVOList.add(catCommentVO);
            }
            // 查询帖子待审核评论
            List<PostComment> postComments = commentManage.getPostCommentByDescTime(page, pageSize, sort);
            List<PostCommentVO> postCommentVOList = new ArrayList<>();
            // 将帖子评论转换为VO
            for(PostComment postComment : postComments){
                User user = userMapper.selectById(postComment.getCommentUserId());
                PostCommentVO postCommentVO = new PostCommentVO();
                BeanUtils.copyProperties(postComment, postCommentVO);
                postCommentVO.setAvatar(user.getAvatar());
                postCommentVO.setNickName(user.getNickName());
                postCommentVOList.add(postCommentVO);
            }
            // 合并评论列表
            AuditCommentVO auditCommentVO = new AuditCommentVO();
            auditCommentVO.setCatComments(catCommentVOList);
            auditCommentVO.setPostComments(postCommentVOList);
            return auditCommentVO;
        } else if(type.equals("post")){
            List<PostComment> postComments = commentManage.getPostCommentByDescTime(page, pageSize, sort);
            List<PostCommentVO> postCommentVOList = new ArrayList<>();
            // 将帖子评论转换为VO
            for(PostComment postComment : postComments){
                User user = userMapper.selectById(postComment.getCommentUserId());
                PostCommentVO postCommentVO = new PostCommentVO();
                BeanUtils.copyProperties(postComment, postCommentVO);
                postCommentVO.setAvatar(user.getAvatar());
                postCommentVO.setNickName(user.getNickName());
                postCommentVOList.add(postCommentVO);
            }
            AuditCommentVO auditCommentVO = new AuditCommentVO();
            auditCommentVO.setPostComments(postCommentVOList);
            return auditCommentVO;
        } else if(type.equals("cat")){
            List<CatComment> catComments = commentManage.getCatCommentByDescTime(page, pageSize, sort);
            List<CatCommentVO> catCommentVOList = new ArrayList<>();
            // 将小猫评论转换为VO
            for(CatComment catComment : catComments){
                User user = userMapper.selectById(catComment.getCommentUserId());
                CatCommentVO catCommentVO = new CatCommentVO();
                BeanUtils.copyProperties(catComment, catCommentVO);
                catCommentVO.setAvatar(user.getAvatar());
                catCommentVO.setNickName(user.getNickName());
                catCommentVOList.add(catCommentVO);
            }
            AuditCommentVO auditCommentVO = new AuditCommentVO();
            auditCommentVO.setCatComments(catCommentVOList);
            return auditCommentVO;
        }
        return null;
    }


    /**
    * 新增帖子评论
    * @param 
    * @return 
    */ 
    public Boolean addComment(AddPostCommentDTO addPostCommentDTO){
        PostComment postComment = new PostComment();
        BeanUtils.copyProperties(addPostCommentDTO, postComment);
        postComment.setStatus(10); // 待审核
        postComment.setIsDeleted(0); // 未删除
        postComment.setIsTop(0); // 不置顶
        postComment.setLikeCount(0); // 点赞数
        Integer userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        postComment.setCommentUserId(Integer.valueOf(userId)); // 评论者ID
        postCommentMapper.insert(postComment);
        return true;
    }

    /**
    * 删除评论
    * @param 
    * @return 
    */
    public Boolean deleteComment(Long commentId){
        int signal = postCommentMapper.deleteById(commentId);
        //TODO
        return true;
    }

    @Override
    public void reviewComment(Integer id, Integer type, String action) {
        if(type == 10){
            // 小猫评论
            QueryWrapper<CatComment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            queryWrapper.eq("is_deleted", 0);
            queryWrapper.eq("status", 10);  // 待审核
            queryWrapper.eq("type", 10);
            CatComment comment = catCommentMapper.selectOne(queryWrapper);
            if(comment == null){
                throw new RuntimeException("评论不存在");
            }
            // 审核评论
            if ("approve".equals(action)) {
                comment.setStatus(20);
            } else if ("reject".equals(action)) {
                comment.setStatus(30);
            } else {
                throw new IllegalArgumentException("无效的操作类型");
            }
            catCommentMapper.updateById(comment);
        } else if(type == 20){
            // 帖子评论
            QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", id);
            queryWrapper.eq("is_deleted", 0);
            queryWrapper.eq("status", 10);  // 待审核
            queryWrapper.eq("type", 20);
            PostComment comment = postCommentMapper.selectOne(queryWrapper);
            if(comment == null){
                throw new RuntimeException("评论不存在");
            }
            // 审核评论
            if ("approve".equals(action)) {
                comment.setStatus(20);
            } else if ("reject".equals(action)) {
                comment.setStatus(30);
            } else {
                throw new IllegalArgumentException("无效的操作类型");
            }
            postCommentMapper.updateById(comment);
        }
    }
}
