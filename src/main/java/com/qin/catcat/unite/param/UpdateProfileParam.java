package com.qin.catcat.unite.param;

import java.io.Serial;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import com.qin.catcat.unite.popo.entity.Post;

import lombok.Data;

/**
 * @Description 更新个人信息入参.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-13 23:42
 */
@Data
public class UpdateProfileParam {
    // 用户ID
    private String userId;
    // 用户名
    private String username;
    // 昵称
    private String nickName;
    // 邮箱
    private String email;
    // 手机号
    private String phoneNumber;
    // 生日
    private Date birthday;
    // 地址
    private String address;
    // 个性签名
    private String signature;
    // 头像
    private String avatar;
    // 状态(1:启用,0:禁用)
    private Integer status;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
    // 粉丝数
    private Integer fansCount;
    // 关注数
    private Integer followCount;
    // 帖子数
    private Integer postCount;
    // 帖子点赞数
    private Integer postLikedCount;
    // 帖子
    private List<Post> postList;
}
