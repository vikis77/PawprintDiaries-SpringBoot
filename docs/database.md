# 数据库表结构

本项目使用MySQL数据库，以下是所有数据库表的结构说明。

## 核心表

### 1. user - 用户表
```sql
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(16) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `nick_name` varchar(45) DEFAULT NULL COMMENT '昵称',
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(45) DEFAULT NULL COMMENT '手机号',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `address` varchar(45) DEFAULT NULL COMMENT '住址',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像文件名',
  `post_count` int DEFAULT '0' COMMENT '发帖数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2. cat - 猫咪信息表
```sql
CREATE TABLE `cat` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '猫咪ID',
  `catname` varchar(50) NOT NULL COMMENT '猫咪名字',
  `gender` tinyint DEFAULT NULL COMMENT '性别：1雄性 0雌性',
  `age` int DEFAULT NULL COMMENT '年龄（月）',
  `birthday` datetime DEFAULT NULL COMMENT '生日',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `food` text COMMENT '食物偏好',
  `taboo` text COMMENT '忌讳',
  `cat_character` text COMMENT '性格',
  `health_status` text COMMENT '健康状况',
  `sterilization_status` text COMMENT '绝育情况',
  `vaccination_status` text COMMENT '疫苗接种情况',
  `bad_record` text COMMENT '不良行为记录',
  `area` varchar(100) DEFAULT NULL COMMENT '区域',
  `breed` varchar(50) DEFAULT NULL COMMENT '品种',
  `cat_guide` text COMMENT '撸猫指南',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪信息表';
```

### 3. post - 帖子表
```sql
CREATE TABLE `post` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `article` text COMMENT '文章内容',
  `author_id` int NOT NULL COMMENT '作者ID',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `collecting_count` int DEFAULT '0' COMMENT '收藏数',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `send_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发帖时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `cover_picture` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  `is_adopted` tinyint DEFAULT '0' COMMENT '是否通过审核：0待审核 1通过 2拒绝',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';
```

## 评论相关表

### 4. post_comment - 帖子评论表
```sql
CREATE TABLE `post_comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `type` int DEFAULT '20' COMMENT '评论类型：10小猫评论 20帖子评论',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` int DEFAULT '10' COMMENT '评论状态：10待审核 20通过 30未通过',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶：0否 1是',
  `comment_context` text NOT NULL COMMENT '评论内容',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `comment_user_id` int NOT NULL COMMENT '评论者ID',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_comment_user_id` (`comment_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子评论表';
```

### 5. cat_comment - 小猫评论表
```sql
CREATE TABLE `cat_comment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `cat_id` int NOT NULL COMMENT '小猫ID',
  `comment_context` text NOT NULL COMMENT '评论内容',
  `comment_user_id` int NOT NULL COMMENT '评论用户ID',
  `status` int DEFAULT '10' COMMENT '评论状态：10未审核 20通过 30不通过',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `like_count` int DEFAULT '0' COMMENT '点赞数',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶：0否 1是',
  `type` int DEFAULT '10' COMMENT '评论类型：10小猫评论 20帖子评论',
  PRIMARY KEY (`id`),
  KEY `idx_cat_id` (`cat_id`),
  KEY `idx_comment_user_id` (`comment_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小猫评论表';
```

## 位置和媒体表

### 6. coordinate - 坐标表
```sql
CREATE TABLE `coordinate` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `area` varchar(100) DEFAULT NULL COMMENT '区域',
  `cat_id` int DEFAULT NULL COMMENT '猫猫ID',
  `description` text COMMENT '描述',
  `latitude` double DEFAULT NULL COMMENT '纬度',
  `longitude` double DEFAULT NULL COMMENT '经度',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `uploader` varchar(50) DEFAULT NULL COMMENT '上传者',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_cat_id` (`cat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='坐标表';
```

### 7. cat_pics - 猫咪照片表
```sql
CREATE TABLE `cat_pics` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cat_id` int NOT NULL COMMENT '猫咪ID',
  `picture` varchar(255) NOT NULL COMMENT '图片文件名',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `uploader_id` int DEFAULT NULL COMMENT '上传者ID',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_cat_id` (`cat_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪照片表';
```

### 8. post_pics - 帖子图片表
```sql
CREATE TABLE `post_pics` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `picture` varchar(255) NOT NULL COMMENT '图片文件名',
  `pic_number` int DEFAULT NULL COMMENT '图片序号',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子图片表';
```

## 权限管理表

### 9. role - 角色表
```sql
CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_desc` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

### 10. permission - 权限表
```sql
CREATE TABLE `permission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `permission_desc` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';
```

### 11. user_role - 用户角色关联表
```sql
CREATE TABLE `user_role` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int NOT NULL COMMENT '用户ID',
  `role_id` int NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

### 12. role_permission - 角色权限关联表
```sql
CREATE TABLE `role_permission` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int NOT NULL COMMENT '角色ID',
  `permission_id` int NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
```

## 互动功能表

### 13. post_like - 帖子点赞表
```sql
CREATE TABLE `post_like` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';
```

### 14. post_collect - 帖子收藏表
```sql
CREATE TABLE `post_collect` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子收藏表';
```

### 15. comment_like - 评论点赞表
```sql
CREATE TABLE `comment_like` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `comment_id` int NOT NULL COMMENT '评论ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `type` int NOT NULL COMMENT '评论类型：10小猫评论 20帖子评论',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user_type` (`comment_id`, `user_id`, `type`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表';
```

## 功能扩展表

### 16. donate - 捐赠表
```sql
CREATE TABLE `donate` (
  `id` bigint NOT NULL COMMENT '主键',
  `amount` bigint DEFAULT NULL COMMENT '金额',
  `cat_id` bigint DEFAULT NULL COMMENT '猫猫ID',
  `cat_name` varchar(50) DEFAULT NULL COMMENT '猫猫名字',
  `message` text COMMENT '留言',
  `name` varchar(50) DEFAULT NULL COMMENT '捐赠人名字',
  `time` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='捐赠表';
```

### 17. fund_record - 资金记录表
```sql
CREATE TABLE `fund_record` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int NOT NULL COMMENT '类型：1收入 2支出',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `cat_id` int DEFAULT NULL COMMENT '相关猫咪ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `operator_id` int DEFAULT NULL COMMENT '操作者ID',
  PRIMARY KEY (`id`),
  KEY `idx_cat_id` (`cat_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金记录表';
```

### 18. cat_adopt_apply_record - 猫咪领养申请记录表
```sql
CREATE TABLE `cat_adopt_apply_record` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cat_id` int NOT NULL COMMENT '猫咪ID',
  `applicant_id` int NOT NULL COMMENT '申请人ID',
  `apply_reason` text COMMENT '申请理由',
  `contact_info` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `status` int DEFAULT '10' COMMENT '申请状态：10待审核 20通过 30拒绝',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `reviewer_id` int DEFAULT NULL COMMENT '审核人ID',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `review_comment` text COMMENT '审核意见',
  PRIMARY KEY (`id`),
  KEY `idx_cat_id` (`cat_id`),
  KEY `idx_applicant_id` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪领养申请记录表';
```

### 19. cat_timeline_event - 猫咪时间线事件表
```sql
CREATE TABLE `cat_timeline_event` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cat_id` int NOT NULL COMMENT '猫咪ID',
  `event_type` varchar(50) NOT NULL COMMENT '事件类型',
  `event_description` text NOT NULL COMMENT '事件描述',
  `event_time` datetime NOT NULL COMMENT '事件时间',
  `recorder_id` int DEFAULT NULL COMMENT '记录者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_cat_id` (`cat_id`),
  KEY `idx_event_time` (`event_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='猫咪时间线事件表';
```

### 20. shot_link - 短链接表
```sql
CREATE TABLE `shot_link` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `type` int DEFAULT NULL COMMENT '记录类型：10猫猫 20帖子',
  `origin_url` varchar(500) DEFAULT NULL COMMENT '原始Url信息',
  `convert_url` varchar(200) DEFAULT NULL COMMENT '转换后的url',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除：0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_convert_url` (`convert_url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接表';
```

## 推荐系统表

### 21. post_weight - 帖子权重表
```sql
CREATE TABLE `post_weight` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id` int NOT NULL COMMENT '帖子ID',
  `weight_score` decimal(10,4) DEFAULT '0.0000' COMMENT '权重分数',
  `view_count` int DEFAULT '0' COMMENT '浏览量',
  `calculate_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '计算时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子权重表';
```

## 关系表

### 22. comment_relationship - 评论关系表
```sql
CREATE TABLE `comment_relationship` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_comment_id` int NOT NULL COMMENT '父评论ID',
  `child_comment_id` int NOT NULL COMMENT '子评论ID',
  `comment_type` int NOT NULL COMMENT '评论类型：10小猫评论 20帖子评论',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_comment` (`parent_comment_id`),
  KEY `idx_child_comment` (`child_comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论关系表';
```

## 状态说明

### 评论状态
- `10`: 待审核
- `20`: 审核通过
- `30`: 审核拒绝

### 帖子审核状态
- `0`: 待审核
- `1`: 审核通过
- `2`: 审核拒绝

### 评论类型
- `10`: 小猫评论
- `20`: 帖子评论

### 性别
- `0`: 雌性
- `1`: 雄性

### 通用删除状态
- `0`: 未删除
- `1`: 已删除
