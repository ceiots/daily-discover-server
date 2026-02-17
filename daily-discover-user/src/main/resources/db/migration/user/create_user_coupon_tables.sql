-- ============================================
-- 用户优惠券模块表结构
-- 业务模块: 用户优惠券资产管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_coupons;

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS user_coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户优惠券ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    
    -- 优惠券基本信息（冗余存储，减少查询）
    coupon_code VARCHAR(50) NOT NULL COMMENT '优惠券代码',
    coupon_name VARCHAR(200) NOT NULL COMMENT '优惠券名称',
    coupon_type ENUM('discount', 'amount', 'free_shipping') NOT NULL COMMENT '优惠券类型',
    discount_value DECIMAL(10,2) COMMENT '优惠值（折扣或金额）',
    min_order_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低订单金额',
    max_discount_amount DECIMAL(10,2) COMMENT '最大优惠金额',
    
    -- 使用状态
    status ENUM('unused', 'used', 'expired') DEFAULT 'unused' COMMENT '使用状态',
    
    -- 使用信息
    used_order_id BIGINT COMMENT '使用的订单ID',
    used_at TIMESTAMP NULL COMMENT '使用时间',
    
    -- 有效期（用户领取时的有效期）
    valid_from TIMESTAMP NOT NULL COMMENT '有效期开始',
    valid_to TIMESTAMP NOT NULL COMMENT '有效期结束',
    
    -- 时间戳
    received_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_coupon_id (coupon_id),
    INDEX idx_status (status),
    INDEX idx_valid_to (valid_to),
    INDEX idx_coupon_type (coupon_type),
    INDEX idx_received_at (received_at),
    UNIQUE KEY uk_user_coupon (user_id, coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券表';

-- 初始化用户优惠券数据
INSERT INTO user_coupons (user_id, coupon_id, coupon_code, coupon_name, coupon_type, discount_value, min_order_amount, max_discount_amount, valid_from, valid_to) VALUES
(1, 1, 'WELCOME10', '新用户专享券', 'discount', 10.00, 100.00, 50.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
(1, 2, 'FREESHIP', '免运费券', 'free_shipping', NULL, 0.00, NULL, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
(2, 3, 'SELLER20', '商家专享券', 'amount', 20.00, 200.00, 20.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59'),
(3, 4, 'ADMIN50', '管理员专享券', 'amount', 50.00, 0.00, 50.00, '2024-01-01 00:00:00', '2024-12-31 23:59:59');

COMMIT;