package com.qin.catcat.unite.popo.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("permissions")
public class Permission {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型(menu:菜单,button:按钮)
     */
    private String permissionType;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 路径
     */
    private String path;

    /**
     * 权限描述
     */
    private String description;

    /**
     * 状态(1:启用,0:禁用)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子权限列表
     */
    @TableField(exist = false)
    private List<Permission> children;
} 