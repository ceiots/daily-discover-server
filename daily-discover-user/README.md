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
