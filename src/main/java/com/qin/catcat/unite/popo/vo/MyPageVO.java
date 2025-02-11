package com.qin.catcat.unite.popo.vo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.qin.catcat.unite.popo.entity.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 响应给前端的我的页面信息 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageVO {
    //用户ID 主键
    private Integer userId;
    //用户名
    private String username;
    //昵称
    private String nickName;
    //邮箱
    private String email;
    //手机号
    private String phoneNumber;
    //生日
    private Date birthday;
    //地址
    private String address;
    //头像
    private String avatar;
    //账号状态
    private Integer status;
    //创建时间
    private Timestamp createTime;
    //更新时间
    private Timestamp updateTime;
    //发帖数（笔记）
    private Integer postCount;
    //粉丝数
    private Integer fansCount;
    //关注数
    private Integer followCount;
    // 获赞数
    private Integer postLikedCount;
    //个性签名
    private String signature;

    //加上该作者的首页帖子的集合
    private List<Post> postList;
}
