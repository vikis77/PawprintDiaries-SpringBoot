# 部署指南

本文档介绍如何在不同环境中部署校猫日记项目。

## 环境要求

### 基础环境
- Java 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 可选环境
- Elasticsearch 8.0+ (搜索功能)
- RabbitMQ 3.8+ (消息队列)
- Nginx (反向代理)

## 开发环境部署

### 1. 克隆项目
```bash
git clone https://github.com/your-username/PawprintDiaries.git
cd PawprintDiaries
```

### 2. 数据库准备
```sql
-- 创建数据库
CREATE DATABASE catcat CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'catcat'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON catcat.* TO 'catcat'@'localhost';
FLUSH PRIVILEGES;
```

### 3. 导入数据库表结构
执行 `docs/database.md` 中的所有SQL语句创建表结构。

### 4. 配置文件
```bash
# 复制配置模板
cp src/main/resources/application-dev.yml.template src/main/resources/application-dev.yml

# 编辑配置文件
vim src/main/resources/application-dev.yml
```

主要配置项：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/catcat?useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

jwt:
  secret: your_jwt_secret_base64_encoded
  
file:
  upload-dir: /path/to/your/upload/directory
```

### 5. 启动项目
```bash
# 开发模式启动
mvn spring-boot:run

# 或者打包后启动
mvn clean package
java -jar target/catcat-0.0.1-SNAPSHOT.jar
```

## 生产环境部署

### 1. 服务器准备
推荐配置：
- CPU: 2核+
- 内存: 4GB+
- 存储: 50GB+
- 操作系统: CentOS 7+ / Ubuntu 18.04+

### 2. 安装依赖环境

#### 安装Java 17
```bash
# CentOS
sudo yum install java-17-openjdk java-17-openjdk-devel

# Ubuntu
sudo apt update
sudo apt install openjdk-17-jdk
```

#### 安装MySQL
```bash
# CentOS
sudo yum install mysql-server
sudo systemctl start mysqld
sudo systemctl enable mysqld

# Ubuntu
sudo apt install mysql-server
sudo systemctl start mysql
sudo systemctl enable mysql
```

#### 安装Redis
```bash
# CentOS
sudo yum install redis
sudo systemctl start redis
sudo systemctl enable redis

# Ubuntu
sudo apt install redis-server
sudo systemctl start redis-server
sudo systemctl enable redis-server
```

### 3. 项目部署

#### 方式一：直接部署
```bash
# 上传项目文件
scp -r PawprintDiaries/ user@server:/opt/

# 编译打包
cd /opt/PawprintDiaries
mvn clean package -Dmaven.test.skip=true

# 创建启动脚本
cat > start.sh << 'EOF'
#!/bin/bash
nohup java -jar -Xms512m -Xmx1024m \
  -Dspring.profiles.active=prod \
  target/catcat-0.0.1-SNAPSHOT.jar \
  > logs/app.log 2>&1 &
echo $! > app.pid
EOF

chmod +x start.sh

# 启动应用
./start.sh
```

#### 方式二：Docker部署
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/catcat-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application-prod.yml application-prod.yml

EXPOSE 8080

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

```bash
# 构建镜像
docker build -t pawprintdiaries:latest .

# 运行容器
docker run -d \
  --name pawprintdiaries \
  -p 8080:8080 \
  -v /opt/uploads:/app/uploads \
  -v /opt/logs:/app/logs \
  pawprintdiaries:latest
```

### 4. Nginx配置
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 静态文件
    location /uploads/ {
        alias /opt/uploads/;
        expires 30d;
    }
    
    # API代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
    
    # 前端页面
    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
    }
}
```

### 5. 系统服务配置
```bash
# 创建systemd服务文件
sudo cat > /etc/systemd/system/pawprintdiaries.service << 'EOF'
[Unit]
Description=PawprintDiaries Application
After=network.target

[Service]
Type=forking
User=app
WorkingDirectory=/opt/PawprintDiaries
ExecStart=/opt/PawprintDiaries/start.sh
ExecStop=/bin/kill -TERM $MAINPID
PIDFile=/opt/PawprintDiaries/app.pid
Restart=always

[Install]
WantedBy=multi-user.target
EOF

# 启用服务
sudo systemctl daemon-reload
sudo systemctl enable pawprintdiaries
sudo systemctl start pawprintdiaries
```

## 监控与维护

### 日志管理
```bash
# 查看应用日志
tail -f logs/app.log

# 日志轮转配置
sudo cat > /etc/logrotate.d/pawprintdiaries << 'EOF'
/opt/PawprintDiaries/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 644 app app
}
EOF
```

### 性能监控
访问 `http://your-domain.com:8080/actuator` 查看应用健康状态。

### 备份策略
```bash
# 数据库备份脚本
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u username -p password catcat > /backup/catcat_$DATE.sql
find /backup -name "catcat_*.sql" -mtime +7 -delete
```

## 故障排查

### 常见问题
1. **端口占用**: `netstat -tlnp | grep 8080`
2. **内存不足**: 调整JVM参数 `-Xms` 和 `-Xmx`
3. **数据库连接**: 检查数据库服务状态和连接配置
4. **Redis连接**: 检查Redis服务状态和密码配置

### 日志分析
```bash
# 查看错误日志
grep -i error logs/app.log

# 查看启动日志
grep -i "started" logs/app.log

# 实时监控
tail -f logs/app.log | grep -i error
```
