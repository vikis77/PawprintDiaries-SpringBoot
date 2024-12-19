package com.qin.catcat.unite.service;

import java.util.List;

import com.qin.catcat.unite.common.result.Result;
import com.qin.catcat.unite.popo.dto.AddCatCommentDTO;
import com.qin.catcat.unite.popo.dto.AuditCatCommentDTO;
import com.qin.catcat.unite.popo.dto.DeleteCatCommentDTO;
import com.qin.catcat.unite.popo.entity.CatComment;
import com.qin.catcat.unite.popo.vo.CatCommentVO;

/**
 * @Description 小猫评论服务接口
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-15 00:28
 */
public interface CatCommentService {
    /**
     * 新增小猫评论
     */
    void addCatComment(AddCatCommentDTO addCatCommentDTO);

    /**
     * 删除小猫评论
     */
    void deleteCatComment(DeleteCatCommentDTO deleteCatCommentDTO);

    /**
     * 获取小猫评论
     */
    Result<List<CatCommentVO>> getCatComment(Integer catId);

    /**
     * 审核通过小猫评论
     */
    void auditCatComment(AuditCatCommentDTO auditCatCommentDTO);

    /**
     * 审核不通过小猫评论
     */
    void auditNotCatComment(AuditCatCommentDTO auditCatCommentDTO);
} 