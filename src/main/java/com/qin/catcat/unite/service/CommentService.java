package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.popo.dto.AddPostCommentDTO;
import com.qin.catcat.unite.popo.entity.PostComment;
import com.qin.catcat.unite.popo.vo.AuditCommentVO;
import com.qin.catcat.unite.popo.vo.PostCommentVO;

public interface CommentService {
    /**
    * 根据帖子ID分页查询前十条评论(按评论时间 最新)
    * @param 
    * @return 
    */
    IPage<PostComment> getCommentByPostidOrderByDescTime(Long postId,int page,int size);

    /**
    * 根据帖子ID分页查询前十条评论(按点赞数 最多)
    * @param 
    * @return 
    */
    List<PostCommentVO> getCommentByPostidOrderByDescLikecount(Integer postId,int page,int size);

    /**
     * 根据父评论ID分页查询子评论
     * @param fatherId 父评论ID
     * @param page 当前页数
     * @param size 每页条数
     * @return 分页后的子评论列表
     */
    IPage<PostComment> getCommentByFatheridByDescTime(Long fatherId,int page,int size);

    /**
     * 按时间分页查询待审核评论
     * @param page 页码
     * @param pageSize 每页大小
     * @param type 评论类型：all、post、cat
     * @param sort 排序方式：desc、asc
     * @return 待审核评论列表（分页）
     */
    AuditCommentVO getAuditCommentByDescTime(int page,int pageSize,String type,String sort);
    
    /**
    * 新增评论
    * @param 
    * @return 
    */ 
    Boolean addComment(AddPostCommentDTO addPostCommentDTO);

    /**
    * 删除评论
    * @param 
    * @return 
    */
    Boolean deleteComment(Long commentId);

    /**
     * 审核评论
     * @param id 评论ID
     * @param type 评论类型：10小猫评论 20帖子评论
     * @param action 审核操作：approve、reject
     */
    void reviewComment(Integer id, Integer type, String action);
}
