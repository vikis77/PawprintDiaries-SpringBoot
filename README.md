# 🐾 校猫日记 PawprintDiaries

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)

**一个基于 Spring Boot 开发的校园猫咪管理与社交平台**

[在线体验](https://pawprintdiaries.luckyiur.com) | [前端代码](https://github.com/vikis77/PawprintDiaries-H5) | [开发者博客](https://luckyiur.com) | [项目演示视频](https://www.bilibili.com/video/BV1iJA2e4EZ3/?spm_id_from=333.1387.homepage.video_card.click&vd_source=93ed68d6c3cef9d567969b4d3c9d0437)

</div>

## 📖 项目简介

校猫日记是一个结合校园流浪猫救助管理和社交分享的社区平台，通过记录和分享学校流浪猫的信息，连接爱猫人士，共同为猫猫创造更好的生活环境。

### 🎯 核心功能
- 🐱 **猫咪信息管理**: 记录猫咪基本信息、健康状况、性格特点等
- 📍 **位置追踪**: 基于高德地图的猫咪轨迹记录与展示
- 📝 **社交分享**: 用户可发布帖子分享猫咪动态
- 🤖 **AI识别**: 基于MobileNetV3的猫咪品种识别（85%准确率）
- 💬 **评论系统**: 支持帖子和猫咪评论，包含审核机制
- 🔍 **智能搜索**: 集成Elasticsearch的全文搜索
- 💰 **捐赠管理**: 猫咪救助资金管理
- 🏠 **领养申请**: 猫咪领养申请与审核流程

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 3.3.4
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis-Plus 3.5.7
- **缓存**: Redis + Caffeine (三级缓存)
- **搜索**: Elasticsearch 8.15.0
- **消息队列**: RabbitMQ
- **安全**: Spring Security + JWT
- **AI**: Spring AI + 阿里云DashScope
- **监控**: Micrometer + Prometheus

### 前端技术
- **移动端**: UniApp (H5/小程序)
- **地图**: 高德地图 JS SDK
- **实时通信**: WebSocket

## 🚀 快速开始

### 环境要求
- Java 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-username/PawprintDiaries.git
cd PawprintDiaries
```

2. **数据库配置**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE catcat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **导入数据库表结构**
```bash
# 执行 docs/database.md 中的SQL语句创建表结构
```

4. **配置文件**
```bash
# 复制配置模板
cp src/main/resources/application-dev.yml.template src/main/resources/application-dev.yml
# 编辑配置文件，填入你的数据库、Redis等配置信息
```

5. **启动项目**
```bash
mvn spring-boot:run
```

6. **访问接口文档**
```
http://localhost:8080/doc.html
```

## 🏗️ 技术架构

### 核心技术实现
- **🔐 安全认证**: Spring Security + JWT + BCrypt密码加密
- **🎯 权限控制**: 基于RBAC模型的自定义注解权限系统
- **⚡ 缓存策略**: Redis + Caffeine三级缓存 + 缓存预热
- **🔍 搜索引擎**: Elasticsearch全文搜索
- **🤖 AI识别**: MobileNetV3轻量化猫咪品种识别模型
- **📍 地图服务**: 高德地图SDK + WebSocket实时位置更新
- **🛡️ 限流防护**: 多层限流（IP/接口/全局）+ 黑名单机制
- **🔗 短链服务**: 自定义短链接生成与重定向
- **🌸 防重复**: Redis布隆过滤器防止重复点赞
- **📊 推荐算法**: 权重随机推荐 + 时间衰减 + 协同过滤

### 推荐算法详解
```
权重计算公式：
权重 = 点赞数 × 0.4 + 收藏数 × 0.3 + 评论数 × 0.2 + 浏览量 × 0.1

最终分数：
最终分数 = 权重 × (0.8 + Random.nextDouble() × 0.4)
```
    
## 📚 API文档

### 核心接口

#### 用户管理
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `GET /api/user/profile` - 获取用户信息

#### 猫咪管理
- `GET /api/cat/list` - 获取猫咪列表
- `POST /api/cat` - 添加猫咪信息
- `PUT /api/cat/{id}` - 更新猫咪信息
- `DELETE /api/cat/{id}` - 删除猫咪
- `POST /api/cat/like/{catId}` - 点赞猫咪
- `POST /api/cat/adopt/apply` - 申请领养

#### 帖子管理
- `POST /api/digital/addpost` - 发布帖子
- `POST /api/digital/getOnePost` - 获取随机帖子
- `POST /api/digital/getUnAuditedPost` - 获取待审核帖子
- `POST /api/digital/auditPost` - 审核通过帖子
- `POST /api/digital/rejectPost` - 拒绝帖子

#### 评论管理
- `POST /api/digital/comment` - 发表评论
- `POST /api/digital/getUnAuditedComment` - 获取待审核评论
- `POST /api/digital/auditComment` - 审核通过评论
- `POST /api/digital/rejectComment` - 拒绝评论

#### 位置服务
- `GET /api/cat/location/list` - 获取猫咪位置列表
- `POST /api/cat/location/upload` - 上传猫咪位置

#### AI服务
- `POST /api/cat/prediction/predict` - 猫咪品种识别

完整API文档请访问：`http://localhost:8080/doc.html`

## 🗄️ 数据库设计

详细的数据库表结构请查看：[数据库文档](docs/database.md)

### 主要数据表
- **user**: 用户信息表
- **cat**: 猫咪信息表
- **post**: 帖子表
- **post_comment**: 帖子评论表
- **cat_comment**: 猫咪评论表
- **coordinate**: 位置坐标表
- **role/permission**: 权限管理表
- **donate**: 捐赠记录表

## ⚙️ 配置说明

### 必需配置
1. 复制 `application-dev.yml.template` 为 `application-dev.yml`
2. 配置数据库连接信息
3. 配置Redis连接信息
4. 配置JWT密钥
5. 配置文件上传路径

### 可选配置
- Elasticsearch（搜索功能）
- RabbitMQ（消息队列）
- 阿里云DashScope API（AI对话）

## 🔧 开发说明

### 项目结构
```
src/main/java/com/qin/catcat/
├── unite/
│   ├── controller/     # 控制器层
│   ├── service/        # 服务层
│   ├── mapper/         # 数据访问层
│   ├── popo/          # 实体类和VO
│   ├── config/        # 配置类
│   ├── common/        # 公共工具类
│   └── security/      # 安全相关
└── CatcatApplication.java  # 启动类
```

### 开发规范
- 使用Lombok简化代码
- 统一的Result返回格式
- AOP切面日志记录
- 自定义权限注解
- 三级缓存策略

## 🤝 贡献指南

我们欢迎所有形式的贡献！

### 如何贡献
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request


## 📝 更新日志

### v1.0.0 (2025-01-XX)
- ✨ 基础功能完成
- 🐱 猫咪信息管理系统
- 📝 帖子发布与评论系统
- 🔐 用户认证与权限管理
- 📍 位置追踪功能
- 🤖 AI猫咪品种识别
- 💰 捐赠管理系统

## ❓ 常见问题

### Q: 如何启用Elasticsearch搜索？
A: 取消ES相关注释，配置elasticsearch连接信息即可。不配置ES也可正常使用MySQL搜索。

### Q: AI识别模型在哪里？
A: 识别模型代码暂未公开，但不影响其他功能运行。

### Q: 如何部署到生产环境？
A: 修改application-prod.yml配置，使用`java -jar`或Docker部署。

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 核心框架
- [MyBatis-Plus](https://baomidou.com/) - ORM框架
- [Knife4j](https://doc.xiaominfo.com/) - API文档
- [高德地图](https://lbs.amap.com/) - 地图服务
- [阿里云](https://www.aliyun.com/) - AI服务

## 📞 联系方式

- 项目地址: [GitHub](https://github.com/your-username/PawprintDiaries)
- 前端项目: [PawprintDiaries-H5](https://github.com/vikis77/PawprintDiaries-H5)
- 在线体验: [https://pawprintdiaries.luckyiur.com](https://pawprintdiaries.luckyiur.com)
- 开发者博客: [https://luckyiur.com](https://luckyiur.com)

---

<div align="center">

**如果这个项目对你有帮助，请给个 ⭐ Star 支持一下！**

Made with ❤️ for stray cats

</div>

## 📸 项目截图

实际页面效果如下（图片更新于2024/12，最新版已对部分页面重新优化）

![pic1_20250207234240](pic1_20250207234240.jpg)

![pic2_20250207234256](pic2_20250207234256.jpg)

![pic3_20250207234307](pic3_20250207234307.jpg)

![pic5_20250211221456](pic5_20250211221456.jpg)

![pic4_20250207234318](pic4_20250207234318.jpg)


