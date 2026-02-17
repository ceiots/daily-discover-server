-- ============================================
-- 认证授权模块表结构
-- 业务模块: 用户认证和权限管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_login_records;
DROP TABLE IF EXISTS user_tokens;

-- 1. 用户令牌表（统一管理所有令牌）
CREATE TABLE IF NOT EXISTS user_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token_type VARCHAR(20) NOT NULL COMMENT '令牌类型（refresh/verification/reset）',
    token_value VARCHAR(255) NOT NULL COMMENT '令牌值',
    
    -- 验证码特定字段
    code_type VARCHAR(20) COMMENT '验证码类型（register/login/reset_password/change_phone）',
    
    -- 通用字段
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    is_used BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
    
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


COMMIT;