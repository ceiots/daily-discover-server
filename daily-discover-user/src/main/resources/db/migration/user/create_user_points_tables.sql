-- ============================================
-- 用户积分模块表结构
-- 业务模块: 用户积分和奖励管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_points_transactions;
DROP TABLE IF EXISTS user_points;

-- 1. 用户积分表
CREATE TABLE IF NOT EXISTS user_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '积分ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 积分信息
    total_points INT DEFAULT 0 COMMENT '总积分',
    available_points INT DEFAULT 0 COMMENT '可用积分',
    used_points INT DEFAULT 0 COMMENT '已用积分',
    expired_points INT DEFAULT 0 COMMENT '过期积分',
    
    -- 积分有效期
    points_expiry_date DATE COMMENT '积分过期日期',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_points_expiry_date (points_expiry_date),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户积分表';

-- 2. 用户积分交易记录表
CREATE TABLE IF NOT EXISTS user_points_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '交易记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 交易信息
    transaction_type ENUM('earn', 'use', 'expire', 'adjust') NOT NULL COMMENT '交易类型',
    points_change INT NOT NULL COMMENT '积分变化',
    points_balance INT NOT NULL COMMENT '积分余额',
    
    -- 关联信息
    reference_type ENUM('order', 'review', 'signin', 'promotion', 'invitation', 'other') COMMENT '关联类型',
    reference_id BIGINT COMMENT '关联ID',
    
    -- 交易描述
    description VARCHAR(500) COMMENT '交易描述',
    
    -- 时间戳
    transaction_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_reference_type (reference_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_transaction_time (transaction_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户积分交易记录表';

-- 初始化数据
-- 1. 初始化用户积分数据
INSERT INTO user_points (user_id, total_points, available_points, used_points, expired_points, points_expiry_date) VALUES
(1, 300, 300, 0, 0, '2025-12-31'),
(2, 5000, 5000, 0, 0, '2025-12-31'),
(3, 10000, 10000, 0, 0, '2025-12-31');

-- 2. 初始化积分交易记录
INSERT INTO user_points_transactions (user_id, transaction_type, points_change, points_balance, reference_type, reference_id, description) VALUES
(1, 'earn', 100, 100, 'signin', NULL, '每日签到奖励'),
(1, 'earn', 200, 300, 'order', 1001, '订单消费奖励'),
(2, 'earn', 5000, 5000, 'promotion', 2001, '商家活动奖励'),
(3, 'earn', 10000, 10000, 'invitation', 3001, '邀请好友奖励');

COMMIT;