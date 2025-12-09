# Daily Discover 数据库设计规范

## 1. 核心设计原则

### 1.1 禁止使用外键约束
- 所有表间关系通过应用层逻辑维护

### 1.2 禁止使用存储过程和触发器
- 业务逻辑统一在应用层实现

### 1.3 命名规范
- **表名**：复数形式，小写字母，下划线分隔（如：`users`, `browse_history`）
- **字段名**：小写字母，下划线分隔（如：`user_id`, `created_at`）
- **索引名**：`idx_表名_字段名`（如：`idx_users_email`）
- **唯一约束**：`uk_表名_字段名`（如：`uk_users_email`）

## 2. 表结构设计规范

### 2.1 基础字段要求
```sql
id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID'
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
```

### 2.2 数据类型选择
- **主键**：`BIGINT AUTO_INCREMENT`
- **字符串**：`VARCHAR` 根据实际长度选择
- **文本**：`TEXT` 用于长文本内容
- **数字**：`INT` 用于整数，`DECIMAL(10,2)` 用于金额
- **布尔**：`BOOLEAN` 或 `TINYINT(1)`
- **时间**：`TIMESTAMP` 或 `DATETIME`

### 2.3 索引设计原则
- 所有表必须有主键
- 根据查询模式设计复合索引
- 注意最左前缀原则

## 3. 数据关系设计

### 3.1 一对多关系
```sql
CREATE TABLE browse_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    INDEX idx_user_id (user_id)
);
```

### 3.2 多对多关系
```sql
CREATE TABLE user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_role (user_id, role_id)
);
```

## 4. 迁移脚本规范

### 4.1 文件命名
```
V{YYYYMMDDHHMM}__{描述}.sql
示例：V202412151430__Create_user_tables_and_data.sql
```

### 4.2 脚本内容结构
```sql
-- 使用数据库
USE database_name;

-- 创建表（包含索引定义）
CREATE TABLE table_name (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    UNIQUE KEY uk_user_created (user_id, created_at)
);

-- 初始化数据
INSERT INTO ...;

-- 提交事务
COMMIT;
```

## 5. AI编程指导要点

### 5.1 代码生成规范
- 实体类字段名与数据库列名保持一致
- 查询方法命名规范：`findByUserIdAndStatus`
- 合理使用 `@Transactional` 注解

### 5.2 最佳实践
- 使用批量插入/更新提高性能
- 合理配置连接池参数
- 结合Redis缓存减少数据库压力