# 每日发现用户服务

## 服务运行

### 自动启动（推荐）
```bash
./start.bat
```

## 数据库迁移

### 命令行批量执行
```bash
# 一次性执行所有SQL文件
mysql -u root -p daily_discover < src/main/resources/db/migration/create_database.sql

# 执行用户表迁移
mysql -u root -p daily_discover < src/main/resources/db/migration/user/create_user_core_tables.sql
mysql -u root -p daily_discover < src/main/resources/db/migration/user/create_user_config_tables.sql
```
