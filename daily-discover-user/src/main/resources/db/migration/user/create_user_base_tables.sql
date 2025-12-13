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
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    bio TEXT COMMENT '个人简介',
    points INT DEFAULT 0 COMMENT '积分',
    level_id BIGINT COMMENT '等级ID',
    membership VARCHAR(50) DEFAULT '普通会员' COMMENT '会员类型',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_level_id (level_id),
    INDEX idx_points (points)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';

-- 初始化数据
-- 1. 初始化用户等级配置
INSERT INTO user_levels (level_name, min_points, max_points, color) VALUES
('普通会员', 0, 999, '#666666'),
('银牌会员', 1000, 4999, '#C0C0C0'),
('金牌会员', 5000, 9999, '#FFD700'),
('钻石会员', 10000, NULL, '#B9F2FF');

-- 2. 初始化默认用户
INSERT INTO users (nickname, phone, bio, points, membership, avatar_url) VALUES
('设计生活家', '13800000000', '记录生活，也创造生活', 300, '普通会员', NULL);

-- 完成迁移
COMMIT;