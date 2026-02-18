-- ============================================
-- 用户基础信息模块表结构
-- 业务模块: 用户核心信息管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_addresses;
DROP TABLE IF EXISTS users;

-- 2. 用户信息表（合并核心信息和扩展信息）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 核心认证信息
    nickname VARCHAR(100) NOT NULL COMMENT '昵称',
    phone VARCHAR(20) UNIQUE NOT NULL COMMENT '手机号（用于登录）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    
    -- 用户等级信息（冗余字段，便于快速查询）
    membership VARCHAR(50) DEFAULT '普通会员' COMMENT '会员类型',
    
    -- 用户状态
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED', 'DELETED') DEFAULT 'ACTIVE' COMMENT '用户状态',
    
    -- 扩展个人信息
    real_name VARCHAR(100) COMMENT '真实姓名',
    gender VARCHAR(20) DEFAULT 'UNKNOWN' COMMENT '性别',
    birthday DATE COMMENT '生日',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引优化
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_membership (membership),
    INDEX idx_status (status),
    INDEX idx_gender (gender),
    INDEX idx_birthday (birthday)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 4. 用户地址表（支持多地址和默认地址）
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 收货人信息
    recipient_name VARCHAR(100) NOT NULL COMMENT '收货人姓名',
    recipient_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    
    -- 地址信息（使用国家标准地区编码关联）
    province_id VARCHAR(10) NOT NULL COMMENT '省份编码（关联regions表）',
    city_id VARCHAR(10) NOT NULL COMMENT '城市编码（关联regions表）',
    district_id VARCHAR(10) NOT NULL COMMENT '区县编码（关联regions表）',
    detail_address VARCHAR(500) NOT NULL COMMENT '详细地址',
    
    -- 地址标签和默认设置
    address_label VARCHAR(50) COMMENT '地址标签（如：家、公司）',
    is_default BOOLEAN DEFAULT FALSE COMMENT '是否默认地址',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_province_id (province_id),
    INDEX idx_city_id (city_id),
    INDEX idx_district_id (district_id),
    INDEX idx_is_default (is_default),
    UNIQUE KEY uk_user_default (user_id, is_default) COMMENT '每个用户只能有一个默认地址'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 初始化数据
-- 2. 初始化默认用户（包含完整信息）
INSERT INTO users (nickname, phone, email, password, membership, status, real_name, gender, birthday, avatar_url, bio) VALUES
('设计生活家', '13800000000', 'user@dailydiscover.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '普通会员', 'ACTIVE', '张三', 'MALE', '1990-01-01', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face', '记录生活，也创造生活'),
('商家用户', '13800000001', 'seller@dailydiscover.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '金牌会员', 'ACTIVE', '李四', 'FEMALE', '1985-05-15', 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face', '专业家居设计师'),
('管理员', '13800000002', 'admin@dailydiscover.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '钻石会员', 'ACTIVE', '王五', 'MALE', '1980-12-25', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face', '系统管理员');

-- 4. 初始化用户地址（每个用户多个地址，其中一个为默认地址）
INSERT INTO user_addresses (user_id, recipient_name, recipient_phone, province_id, city_id, district_id, detail_address, address_label, is_default) VALUES
-- 用户1（张三）的地址
(1, '张三', '13800138001', '110000', '110100', '110105', '朝阳区建国路88号', '家', TRUE),
(1, '张三', '13800138001', '310000', '310100', '310115', '浦东新区陆家嘴金融中心', '公司', FALSE),
-- 用户2（李四）的地址
(2, '李四', '13900139002', '310000', '310100', '310115', '浦东新区陆家嘴金融中心', '家', TRUE),
(2, '李四', '13900139002', '440000', '440300', '440305', '南山区科技园', '公司', FALSE),
-- 用户3（王五）的地址
(3, '王五', '13700137003', '440000', '440300', '440305', '南山区科技园', '家', TRUE);

COMMIT;