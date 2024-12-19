package com.qin.catcat.unite.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.param.AddCatCommentParam;
import com.qin.catcat.unite.param.AuditCatCommentParam;
import com.qin.catcat.unite.param.DeleteCatCommentParam;
import com.qin.catcat.unite.popo.dto.AddCatCommentDTO;
import com.qin.catcat.unite.popo.dto.AuditCatCommentDTO;
import com.qin.catcat.unite.popo.dto.DeleteCatCommentDTO;
import com.qin.catcat.unite.popo.entity.CatComment;
import com.qin.catcat.unite.popo.vo.CatCommentVO;
import com.qin.catcat.unite.security.HasPermission;
import com.qin.catcat.unite.service.CatCommentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description 小猫评论控制器.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
@RestController
@RequestMapping("/api/cat/comment")
@Slf4j
public class CatCommentController {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CatCommentService catCommentService;

    @Operation(summary = "新增小猫评论")
    @PostMapping("/add")
    @HasPermission("system:cat:comment:add")
    public Result<?> addCatComment(@RequestBody AddCatCommentParam addCatCommentParam){
        AddCatCommentDTO addCatCommentDTO = new AddCatCommentDTO();
        BeanUtils.copyProperties(addCatCommentParam, addCatCommentDTO);
        if (TokenHolder.getToken() != null){
            addCatCommentDTO.setCommentUserId(Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken())));
        } else {
            return Result.error("用户未登录");
        }

        try {
            catCommentService.addCatComment(addCatCommentDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("新增小猫评论失败", e);
            return Result.error("新增评论失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除小猫评论")
    @DeleteMapping("/delete")
    @HasPermission("system:cat:comment:delete")
    public Result<?> deleteCatComment(@RequestBody DeleteCatCommentParam deleteCatCommentParam){
        if (deleteCatCommentParam.getId() == null) {
            return Result.error("评论ID不能为空");
        }

        DeleteCatCommentDTO deleteCatCommentDTO = new DeleteCatCommentDTO();
        BeanUtils.copyProperties(deleteCatCommentParam, deleteCatCommentDTO);
        
        // 获取当前用户ID
        if (TokenHolder.getToken() != null){
            deleteCatCommentDTO.setCommentUserId(Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken())));
        } else {
            return Result.error("用户未登录");
        }

        try {
            catCommentService.deleteCatComment(deleteCatCommentDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("删除小猫评论失败", e);
            return Result.error("删除评论失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取小猫评论列表（目前只支持获取10条）")
    @GetMapping("/get/{catId}")
    @HasPermission("system:cat:comment:get")
    public Result<List<CatCommentVO>> getCatComment(@PathVariable Integer catId){
        log.info("获取小猫评论，小猫ID：{}", catId);
        // 参数校验
        if (catId == null) {
            return Result.error("小猫ID不能为空");
        }
        return catCommentService.getCatComment(catId);
    }

    // 该接口未启用 - 使用的CommentController的接口
    @Operation(summary = "审核通过小猫评论")
    @PutMapping("/audit")
    @HasPermission("system:cat:comment:audit")
    public Result<?> auditCatComment(@RequestBody AuditCatCommentParam auditCatCommentParam){
        log.info("审核通过小猫评论，参数：{}", auditCatCommentParam);
        // 参数校验
        if (auditCatCommentParam.getId() == null) {
            return Result.error("评论ID不能为空");
        }
        AuditCatCommentDTO auditCatCommentDTO = new AuditCatCommentDTO();
        BeanUtils.copyProperties(auditCatCommentParam, auditCatCommentDTO);
        // 获取当前用户ID
        if (TokenHolder.getToken() != null){
            auditCatCommentDTO.setAuditUserId(Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken())));
        } else {
            return Result.error("用户未登录");
        }
        try {
            catCommentService.auditCatComment(auditCatCommentDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("审核小猫评论失败", e);
            return Result.error("审核评论失败：" + e.getMessage());
        }
    }

    // 该接口未启用 - 使用的CommentController的接口
    @Operation(summary = "审核不通过小猫评论")
    @PutMapping("/audit/not")
    @HasPermission("system:cat:comment:audit:not")
    public Result<?> auditNotCatComment(@RequestBody AuditCatCommentParam auditCatCommentParam){
        log.info("审核不通过小猫评论，参数：{}", auditCatCommentParam);
        // 参数校验
        if (auditCatCommentParam.getId() == null) {
            return Result.error("评论ID不能为空");
        }
        AuditCatCommentDTO auditCatCommentDTO = new AuditCatCommentDTO();
        BeanUtils.copyProperties(auditCatCommentParam, auditCatCommentDTO);
        // 获取当前用户ID
        if (TokenHolder.getToken() != null){
            auditCatCommentDTO.setAuditUserId(Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken())));
        } else {
            return Result.error("用户未登录");
        }
        try {
            catCommentService.auditNotCatComment(auditCatCommentDTO);
            return Result.success();
        } catch (Exception e) {
            log.error("审核小猫评论失败", e);
            return Result.error("审核评论失败：" + e.getMessage());
        }
    }
}
