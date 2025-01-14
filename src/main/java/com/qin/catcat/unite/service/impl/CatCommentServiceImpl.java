package com.qin.catcat.unite.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.mapper.CatCommentMapper;
import com.qin.catcat.unite.mapper.CommentLikeMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.popo.dto.AddCatCommentDTO;
import com.qin.catcat.unite.popo.dto.AuditCatCommentDTO;
import com.qin.catcat.unite.popo.dto.DeleteCatCommentDTO;
import com.qin.catcat.unite.popo.entity.CatComment;
import com.qin.catcat.unite.popo.entity.CommentLike;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.CatCommentVO;
import com.qin.catcat.unite.service.CatCommentService;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 小猫评论服务实现类
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
@Service
@Slf4j
public class CatCommentServiceImpl implements CatCommentService {

    @Autowired
    private CatCommentMapper catCommentMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentLikeMapper commentLikeMapper;
    @Autowired
    private CacheUtils cacheUtils;

    /**
     * @Description 新增小猫评论
     * @param addCatCommentDTO 新增小猫评论DTO
     * @return void
     */
    @Override
    public void addCatComment(AddCatCommentDTO addCatCommentDTO) {
        CatComment catComment = new CatComment();
        BeanUtils.copyProperties(addCatCommentDTO, catComment);
        try {
            // 设置评论状态为待审核
            catComment.setStatus(10);
            catComment.setIsDeleted(0);
            catComment.setLikeCount(0);
            catComment.setIsTop(0);
            catComment.setType(10);
            catCommentMapper.insert(catComment);
        } catch (Exception e) {
            log.error("新增评论失败", e);
            throw e;
        }
    }

    @Override
    public void deleteCatComment(DeleteCatCommentDTO deleteCatCommentDTO) {
        try {
            CatComment catComment = catCommentMapper.selectById(deleteCatCommentDTO.getId());
            if (catComment == null) {
                throw new RuntimeException("评论不存在");
            }
            // 检查是否是评论作者
            if (!catComment.getCommentUserId().equals(deleteCatCommentDTO.getCommentUserId())) {
                throw new RuntimeException("无权删除他人评论");
            }
            // 逻辑删除
            catComment.setIsDeleted(1);
            catCommentMapper.updateById(catComment);
        } catch (Exception e) {
            log.error("删除评论失败", e);
            throw e;
        }
    }

    @Override
    public Result<List<CatCommentVO>> getCatComment(Integer catId) {
        List<CatComment> catComments = catCommentMapper.selectList(new LambdaQueryWrapper<CatComment>()
                .eq(CatComment::getCatId, catId)
                .eq(CatComment::getIsDeleted, 0)
                .eq(CatComment::getStatus, 20)
                .orderByDesc(CatComment::getCreateTime)
                .last("limit 10"));
        if (catComments.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        // 当前用户ID
        Integer userId = null;
        if (TokenHolder.getToken() != null) {
            userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        }
        List<CatCommentVO> catCommentVOs = new ArrayList<>();
        for (CatComment catComment : catComments) {
            CatCommentVO catCommentVO = new CatCommentVO();
            BeanUtils.copyProperties(catComment, catCommentVO);
            // 获取评论者信息
            User user = userMapper.selectById(catComment.getCommentUserId());
            catCommentVO.setAvatar(user.getAvatar());
            catCommentVO.setNickName(user.getNickName());
            // 当前用户是否点赞
            if (userId != null) {
                CommentLike commentLike = commentLikeMapper.selectOne(new LambdaQueryWrapper<CommentLike>()
                        .eq(CommentLike::getType, 10) // 类型：10-小猫评论
                        .eq(CommentLike::getTargetId, catComment.getId()) // 小猫ID
                        .eq(CommentLike::getUserId, userId)); // 当前用户ID
                catCommentVO.setLiked(commentLike != null);
            }
            catCommentVOs.add(catCommentVO);
        }
        return Result.success(catCommentVOs);
    }

    /**
     * @Description 通过小猫评论
     * @param auditCatCommentDTO 审核小猫评论DTO
     * @return void
     */
    @Override
    public void auditCatComment(AuditCatCommentDTO auditCatCommentDTO) {
        try {
            CatComment catComment = catCommentMapper.selectById(auditCatCommentDTO.getId());
            if (catComment == null) {
                throw new RuntimeException("评论不存在");
            }
            catComment.setStatus(20); // 20表示通过
            catCommentMapper.updateById(catComment);
            // 更新缓存
            cacheUtils.remove(Constant.CAT_LIST_FOR_CATCLAW);
        } catch (Exception e) {
            log.error("审核评论失败", e);
            throw e;
        }
    }

    @Override
    public void auditNotCatComment(AuditCatCommentDTO auditCatCommentDTO) {
        try {
            CatComment catComment = catCommentMapper.selectById(auditCatCommentDTO.getId());
            if (catComment == null) {
                throw new RuntimeException("评论不存在");
            }
            catComment.setStatus(30); // 30表示不通过
            catCommentMapper.updateById(catComment);
        } catch (Exception e) {
            log.error("审核评论失败", e);
            throw e;
        }
    }
} 