# 每日发现用户服务

### 跨平台启动选项

#### 后台启动（推荐生产环境）
```bash
./start.sh -b
# 或简写
./start.sh
```
  
#### 前台启动（推荐开发环境）
```bash
./start.sh -f
```

#### 检查服务状态
```bash
./start.sh --status
```

#### 停止服务
```bash
./stop.sh
```

#### 重启服务
```bash
./restart.sh
```

### 脚本特性
- ✅ **跨平台支持**：自动检测 Windows Git Bash 和 Ubuntu 环境
- ✅ **后台运行**：支持关闭终端后服务继续运行
- ✅ **进程管理**：完善的启动、停止、重启功能
- ✅ **日志管理**：日志输出到 `logs/application.log`

### 常用命令
```bash
# 查看实时日志
tail -f logs/application.log

# 检查服务状态
./start.sh --status

# 停止服务
./stop.sh

# 重启服务
./restart.sh
```

### 服务信息
- **服务地址**: http://localhost:8091
- **API文档**: http://localhost:8091

### 服务健康检查

#### 基础健康检查
```bash
# 检查服务是否启动（如果服务未运行会显示连接错误）
curl -s http://localhost:8091/actuator/health

# 检查服务基本信息
curl -s http://localhost:8091/actuator/info

# 带详细输出的健康检查
curl -v http://localhost:8091/actuator/health
```

#### API接口测试
```bash
# 本地测试认证接口（需要替换为实际参数）
curl -X POST http://localhost:8091/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"test123"}'

# 通过域名测试认证接口（nginx代理）
curl -X POST https://api.dailydiscover.top/v1/users/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"test123"}'

# 测试用户信息接口
curl -X GET https://api.dailydiscover.top/v1/users/user/info \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

```

#### 错误诊断
```bash
# 如果curl失败，先检查服务状态
./start.sh --status

# 检查端口占用情况
netstat -an | grep 8091

# 查看服务日志
tail -f logs/application.log
```

## 数据库迁移

### 命令行批量执行

# 数据库迁移
```bash
mysql -u root -p daily_discover < src/main/resources/db/migration/create_database.sql
```

# 执行用户表迁移
```bash
mysql -u root -p daily_discover < src/main/resources/db/migration/user/create_user_base_tables.sql
mysql -u root -p daily_discover < src/main/resources/db/migration/user/create_user_behavior_tables.sql
mysql -u root -p daily_discover < src/main/resources/db/migration/user/create_user_config_tables.sql
```

# 执行认证表迁移
```bash
mysql -u root -p daily_discover < src/main/resources/db/migration/auth/create_auth_tables.sql
```
