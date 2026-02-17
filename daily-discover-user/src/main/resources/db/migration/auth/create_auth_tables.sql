-- ============================================
-- 认证授权模块表结构
-- 业务模块: 用户认证和权限管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_permissions;
DROP TABLE IF EXISTS login_attempts;
DROP TABLE IF EXISTS refresh_tokens;

-- 1. 刷新令牌表（用于JWT刷新机制）
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token VARCHAR(255) NOT NULL COMMENT '刷新令牌',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_token (token),
    INDEX idx_expires_at (expires_at),
    UNIQUE KEY uk_user_token (user_id, token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='刷新令牌表';

-- 2. 登录尝试记录表（用于安全控制）
CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID（可为空，表示匿名登录尝试）',
    identifier VARCHAR(100) NOT NULL COMMENT '登录标识（手机号）',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    device_info VARCHAR(200) COMMENT '设备信息',
    result VARCHAR(20) NOT NULL COMMENT '登录结果（成功/失败）',
    failure_reason VARCHAR(200) COMMENT '失败原因（如果失败）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_identifier (identifier),
    INDEX idx_ip_address (ip_address),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录尝试记录表';

-- 3. 用户权限表
CREATE TABLE IF NOT EXISTS user_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(50) NOT NULL COMMENT '权限代码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    description TEXT COMMENT '权限描述',
    module VARCHAR(50) COMMENT '所属模块',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限表';

-- 4. 用户角色表
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL COMMENT '角色代码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description TEXT COMMENT '角色描述',
    permissions JSON COMMENT '权限集合',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色表';

-- 初始化权限数据
INSERT INTO user_permissions (permission_code, permission_name, description, module) VALUES
('user:read', '查看用户信息', '可以查看用户基本信息', 'user'),
('user:write', '编辑用户信息', '可以编辑用户个人信息', 'user'),
('user:delete', '删除用户', '可以删除用户账户', 'user'),
('product:read', '查看商品', '可以查看商品信息', 'product'),
('product:write', '编辑商品', '可以编辑商品信息', 'product'),
('product:delete', '删除商品', '可以删除商品', 'product'),
('order:read', '查看订单', '可以查看订单信息', 'order'),
('order:write', '编辑订单', '可以编辑订单状态', 'order'),
('order:delete', '删除订单', '可以删除订单', 'order'),
('admin:all', '管理员权限', '拥有所有管理权限', 'admin');

-- 初始化角色数据
INSERT INTO user_roles (role_code, role_name, description, permissions) VALUES
('user', '普通用户', '普通注册用户', '["user:read", "user:write", "product:read", "order:read"]'),
('seller', '商家用户', '商品销售商家', '["user:read", "user:write", "product:read", "product:write", "order:read", "order:write"]'),
('admin', '管理员', '系统管理员', '["admin:all"]');

COMMIT;