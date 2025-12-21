# 每日发现用户服务

## 服务运行

### 自动启动（推荐）
```bash
./start.bat
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

# tailscale 配置
tailscale funnel --bg --set-path=/common/api http://127.0.0.1:8090/common/api
tailscale funnel --bg --set-path=/user/api http://127.0.0.1:8091/user/api
tailscale funnel --bg --set-path=/product/api http://127.0.0.1:8092/product/api
tailscale funnel --bg --set-path=/api/dailylife http://127.0.0.1:3001/api/dailylife