-- 用户基础表迁移脚本
-- 创建用户核心基础信息表

-- 使用数据库
USE daily_discover;

-- 删除现有表（如果存在）
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_levels;

-- 1. 用户等级配置表
CREATE TABLE IF NOT EXISTS user_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL COMMENT '等级名称',
    min_points INT NOT NULL COMMENT '最小积分要求',
    max_points INT COMMENT '最大积分要求',
    color VARCHAR(20) COMMENT '等级颜色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_level_name (level_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户等级配置表';

-- 2. 用户基本信息表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL COMMENT '昵称',
    username VARCHAR(100) UNIQUE COMMENT '用户名（用于登录）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱（用于登录和找回密码）',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    bio TEXT COMMENT '个人简介',
    points INT DEFAULT 0 COMMENT '积分',
    level_id BIGINT COMMENT '等级ID',
    membership VARCHAR(50) DEFAULT '普通会员' COMMENT '会员类型',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    email_verified BOOLEAN DEFAULT FALSE COMMENT '邮箱是否已验证',
    phone_verified BOOLEAN DEFAULT FALSE COMMENT '手机号是否已验证',
    last_login_at TIMESTAMP NULL COMMENT '最后登录时间',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE, INACTIVE, BANNED',
    verification_code VARCHAR(10) COMMENT '验证码',
    verification_code_expires_at TIMESTAMP NULL COMMENT '验证码过期时间',
    reset_token VARCHAR(255) COMMENT '密码重置令牌',
    reset_token_expires_at TIMESTAMP NULL COMMENT '重置令牌过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_level_id (level_id),
    INDEX idx_points (points),
    INDEX idx_status (status),
    INDEX idx_verification_code (verification_code),
    INDEX idx_reset_token (reset_token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';

-- 初始化数据
-- 1. 初始化用户等级配置
INSERT INTO user_levels (level_name, min_points, max_points, color) VALUES
('普通会员', 0, 999, '#666666'),
('银牌会员', 1000, 4999, '#C0C0C0'),
('金牌会员', 5000, 9999, '#FFD700'),
('钻石会员', 10000, NULL, '#B9F2FF');

-- 2. 初始化默认用户
INSERT INTO users (nickname, username, email, phone, password, bio, points, membership, avatar_url, phone_verified, status) VALUES
('设计生活家', 'design_life', 'design_life@example.com', '13800000000', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '记录生活，也创造生活', 300, '普通会员', NULL, TRUE, 'ACTIVE');

-- 3. 创建验证码表（用于登录、注册、找回密码等场景）
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT COMMENT '用户ID（可为空，用于注册场景）',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    code VARCHAR(10) NOT NULL COMMENT '验证码',
    type VARCHAR(20) NOT NULL COMMENT '验证码类型：LOGIN, REGISTER, RESET_PASSWORD, CHANGE_EMAIL, CHANGE_PHONE',
    used BOOLEAN DEFAULT FALSE COMMENT '是否已使用',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_code_type (code, type),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';

-- 4. 创建刷新令牌表（用于JWT刷新机制）
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token VARCHAR(255) NOT NULL COMMENT '刷新令牌',
    expires_at TIMESTAMP NOT NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_token (token),
    INDEX idx_expires_at (expires_at),
    UNIQUE KEY uk_user_token (user_id, token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='刷新令牌表';

-- 完成迁移
COMMIT;