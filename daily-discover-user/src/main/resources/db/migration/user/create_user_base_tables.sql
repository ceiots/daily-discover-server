-- ============================================
-- 用户基础信息模块表结构
-- 业务模块: 用户核心信息管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS user_levels;
DROP TABLE IF EXISTS user_profiles;

-- 2. 用户基本信息表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL COMMENT '昵称',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号（用于登录）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    
    -- 用户等级信息（冗余字段，便于快速查询）
    membership VARCHAR(50) DEFAULT '普通会员' COMMENT '会员类型',
    
    -- 用户状态
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED', 'DELETED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    
    -- 验证信息
    phone_verified BOOLEAN DEFAULT FALSE COMMENT '手机号是否已验证',
    email_verified BOOLEAN DEFAULT FALSE COMMENT '邮箱是否已验证',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引优化
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_membership (membership),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';

-- 3. 用户扩展信息表
CREATE TABLE IF NOT EXISTS user_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 基本信息
    real_name VARCHAR(100) COMMENT '真实姓名',
    gender ENUM('MALE', 'FEMALE', 'OTHER', 'UNKNOWN') DEFAULT 'UNKNOWN' COMMENT '性别',
    birthday DATE COMMENT '生日',
    
    -- 联系信息
    avatar_url VARCHAR(500) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    
    -- 地址信息
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区县',
    detail_address VARCHAR(500) COMMENT '详细地址',
    
    -- 社交信息
    wechat VARCHAR(100) COMMENT '微信号',
    qq VARCHAR(50) COMMENT 'QQ号',
    
    -- 偏好设置
    preferences JSON COMMENT '用户偏好设置',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_gender (gender),
    INDEX idx_birthday (birthday),
    INDEX idx_province_city (province, city),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

-- 初始化数据
-- 1. 用户等级配置（已移到代码中配置）

-- 2. 初始化默认用户
INSERT INTO users (nickname, phone, email, password, membership, phone_verified, status) VALUES
('设计生活家', '13800000000', 'user@dailydiscover.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '普通会员', TRUE, 'ACTIVE'),
('商家用户', '13800000001', 'seller@dailydiscover.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '金牌会员', TRUE, 'ACTIVE'),
('管理员', '13800000002', 'admin@dailydiscover.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '钻石会员', TRUE, 'ACTIVE');

-- 3. 初始化用户扩展信息
INSERT INTO user_profiles (user_id, real_name, gender, birthday, avatar_url, bio, province, city, district, detail_address) VALUES
(1, '张三', 'MALE', '1990-01-01', 'https://example.com/avatar1.jpg', '记录生活，也创造生活', '北京市', '北京市', '朝阳区', '朝阳区建国路88号'),
(2, '李四', 'FEMALE', '1985-05-15', 'https://example.com/avatar2.jpg', '专业家居设计师', '上海市', '上海市', '浦东新区', '浦东新区陆家嘴金融中心'),
(3, '王五', 'MALE', '1980-12-25', 'https://example.com/avatar3.jpg', '系统管理员', '广东省', '深圳市', '南山区', '南山区科技园');

COMMIT;