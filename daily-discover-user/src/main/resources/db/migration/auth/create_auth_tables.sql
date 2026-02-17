-- ============================================
-- 认证授权模块表结构
-- 业务模块: 用户认证和权限管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_login_records;
DROP TABLE IF EXISTS user_tokens;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_permissions;
DROP TABLE IF EXISTS login_attempts;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS user_verification_codes;
DROP TABLE IF EXISTS user_reset_tokens;
DROP TABLE IF EXISTS user_login_stats;

-- 1. 用户令牌表（统一管理所有令牌）
CREATE TABLE IF NOT EXISTS user_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token_type ENUM('refresh', 'verification', 'reset') NOT NULL COMMENT '令牌类型',
    token_value VARCHAR(255) NOT NULL COMMENT '令牌值',
    
    -- 验证码特定字段
    code_type ENUM('register', 'login', 'reset_password', 'change_phone') COMMENT '验证码类型（仅verification类型使用）',
    
    -- 通用字段
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    is_used BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
    used_at TIMESTAMP NULL COMMENT '使用时间',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_token_type (token_type),
    INDEX idx_token_value (token_value),
    INDEX idx_expires_at (expires_at),
    INDEX idx_is_used (is_used),
    UNIQUE KEY uk_user_token_type (user_id, token_type, token_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户令牌表';

-- 2. 用户登录记录表（合并登录尝试和统计）
CREATE TABLE IF NOT EXISTS user_login_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID（可为空，表示匿名登录尝试）',
    identifier VARCHAR(100) NOT NULL COMMENT '登录标识（手机号）',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    device_info VARCHAR(200) COMMENT '设备信息',
    result ENUM('success', 'failed') NOT NULL COMMENT '登录结果',
    failure_reason VARCHAR(200) COMMENT '失败原因（如果失败）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_identifier (identifier),
    INDEX idx_ip_address (ip_address),
    INDEX idx_result (result),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录记录表';

-- 4. 用户验证码表（已移除，统一到user_tokens表）
-- 5. 用户重置令牌表（已移除，统一到user_tokens表）

-- 6. 用户登录统计表（已移除，通过user_login_records表统计）

-- 6. 用户角色表（已移除，改为代码配置）
-- 角色配置移到应用代码中管理，减少数据库复杂度

-- 初始化权限数据（已移除，改为代码配置）

-- 初始化角色数据（已移除，改为代码配置）

COMMIT;