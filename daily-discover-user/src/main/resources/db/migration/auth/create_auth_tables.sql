-- 认证相关表结构迁移脚本（简化版）
-- 根据主流电商应用最佳实践优化

-- 使用数据库
USE daily_discover;

-- 开始迁移
START TRANSACTION;

-- 1. 创建刷新令牌表（用于JWT刷新机制）
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
    UNIQUE KEY uk_user_token (user_id, token),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='刷新令牌表';

-- 2. 创建登录尝试记录表（用于安全控制）
-- 说明：防止暴力破解，记录登录尝试次数和IP限制
CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    ip_address VARCHAR(45) NOT NULL COMMENT 'IP地址',
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '尝试时间',
    is_success BOOLEAN DEFAULT FALSE COMMENT '是否成功',
    failure_reason VARCHAR(200) COMMENT '失败原因',
    INDEX idx_username (username),
    INDEX idx_ip_address (ip_address),
    INDEX idx_attempt_time (attempt_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录尝试记录表';

-- 完成迁移
COMMIT;