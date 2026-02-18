-- ============================================
-- 8. 营销促销流程模块表结构
-- 业务模块: 营销促销管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS coupon_usage_records;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS promotion_activities;

-- 促销活动表
CREATE TABLE IF NOT EXISTS promotion_activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '活动ID',
    activity_name VARCHAR(200) NOT NULL COMMENT '活动名称',
    activity_type VARCHAR(20) NOT NULL COMMENT '活动类型',
    
    -- 活动时间
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '结束时间',
    
    -- 活动范围
    target_type VARCHAR(20) DEFAULT 'all' COMMENT '目标类型',
    target_ids JSON COMMENT '目标ID集合',
    
    -- 活动规则
    rules JSON COMMENT '活动规则配置',
    description TEXT COMMENT '活动描述',
    
    -- 活动状态
    status VARCHAR(20) DEFAULT 'draft' COMMENT '活动状态',
    
    -- 统计信息
    participation_count INT DEFAULT 0 COMMENT '参与人数',
    order_count INT DEFAULT 0 COMMENT '订单数量',
    total_discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '总优惠金额',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_activity_type (activity_type),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_status (status),
    INDEX idx_activity_type_status (activity_type, status)
) COMMENT '促销活动表';

-- 优惠券表
CREATE TABLE IF NOT EXISTS coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '优惠券ID',
    coupon_code VARCHAR(50) NOT NULL UNIQUE COMMENT '优惠券代码',
    coupon_name VARCHAR(200) NOT NULL COMMENT '优惠券名称',
    coupon_type VARCHAR(20) NOT NULL COMMENT '优惠券类型',
    
    -- 优惠规则
    discount_value DECIMAL(10,2) COMMENT '优惠值（折扣或金额）',
    min_order_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低订单金额',
    max_discount_amount DECIMAL(10,2) COMMENT '最大优惠金额',
    
    -- 使用限制
    usage_limit INT DEFAULT 1 COMMENT '每人使用限制',
    total_quantity INT DEFAULT 0 COMMENT '总发行数量',
    used_quantity INT DEFAULT 0 COMMENT '已使用数量',
    
    -- 有效期
    valid_from TIMESTAMP NOT NULL COMMENT '有效期开始',
    valid_to TIMESTAMP NOT NULL COMMENT '有效期结束',
    
    -- 适用范围
    applicable_scope VARCHAR(20) DEFAULT 'all' COMMENT '适用范围',
    applicable_ids JSON COMMENT '适用ID集合',
    
    -- 状态
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_coupon_code (coupon_code),
    INDEX idx_coupon_type (coupon_type),
    INDEX idx_valid_from (valid_from),
    INDEX idx_valid_to (valid_to),
    INDEX idx_status (status)
) COMMENT '优惠券表';



-- 优惠券使用记录表
CREATE TABLE IF NOT EXISTS coupon_usage_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '使用记录ID',
    user_coupon_id BIGINT NOT NULL COMMENT '用户优惠券ID（关联user_coupons.id）',
    order_id BIGINT NOT NULL COMMENT '订单ID（关联orders_core.id）',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID（关联coupons.id）',
    user_id BIGINT NOT NULL COMMENT '用户ID（关联users.id）',
    
    -- 优惠信息
    discount_amount DECIMAL(10,2) NOT NULL COMMENT '优惠金额',
    order_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    
    -- 使用时间
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '使用时间',
    
    -- 索引优化
    INDEX idx_user_coupon_id (user_coupon_id),
    INDEX idx_order_id (order_id),
    INDEX idx_coupon_id (coupon_id),
    INDEX idx_user_id (user_id),
    INDEX idx_used_at (used_at),
    
    -- 复合索引优化
    INDEX idx_user_order (user_id, order_id) COMMENT '用户订单关联查询',
    INDEX idx_coupon_order (coupon_id, order_id) COMMENT '优惠券订单关联查询',
    INDEX idx_user_coupon_order (user_id, coupon_id, order_id) COMMENT '用户优惠券订单关联查询'
) COMMENT '优惠券使用记录表';



COMMIT;

-- ============================================
-- 营销促销模块表初始数据
-- ============================================

-- 插入促销活动数据
INSERT INTO promotion_activities (id, activity_name, activity_type, start_time, end_time, target_type, target_ids, rules, description, status, participation_count, order_count, total_discount_amount) VALUES
(1, '新春大促', 'discount', '2026-02-01 00:00:00', '2026-02-28 23:59:59', 'all', NULL, '{"discount_rate": 0.9, "min_amount": 100}', '新春佳节，全场9折优惠', 'active', 500, 200, 5000.00),
(2, '限时秒杀', 'flash_sale', '2026-02-01 10:00:00', '2026-02-01 12:00:00', 'product', '[1, 2]', '{"limit_per_user": 1, "stock": 100}', '热门商品限时秒杀', 'ended', 300, 100, 2000.00),
(3, '满减优惠', 'discount', '2026-02-01 00:00:00', '2026-02-15 23:59:59', 'all', NULL, '{"full_amount": 200, "minus_amount": 20}', '满200减20', 'active', 800, 350, 7000.00),
(4, '拼团活动', 'group_buy', '2026-02-01 00:00:00', '2026-02-10 23:59:59', 'category', '[1]', '{"group_size": 3, "discount_rate": 0.8}', '电子产品拼团享8折', 'active', 200, 60, 3000.00),
(5, '积分兑换', 'points', '2026-02-01 00:00:00', '2026-02-28 23:59:59', 'all', NULL, '{"points_per_yuan": 100, "max_points": 5000}', '积分兑换现金券', 'active', 150, 80, 1500.00);

-- 插入优惠券数据
INSERT INTO coupons (id, coupon_code, coupon_name, coupon_type, discount_value, min_order_amount, max_discount_amount, usage_limit, total_quantity, used_quantity, valid_from, valid_to, applicable_scope, applicable_ids, status) VALUES
(1, 'WELCOME10', '新用户专享券', 'discount', 0.9, 100.00, 50.00, 1, 1000, 200, '2026-02-01 00:00:00', '2026-02-28 23:59:59', 'all', NULL, 'active'),
(2, 'FREESHIP', '免运费券', 'free_shipping', NULL, 0.00, NULL, 1, 500, 100, '2026-02-01 00:00:00', '2026-02-15 23:59:59', 'all', NULL, 'active'),
(3, 'DISCOUNT20', '20元优惠券', 'amount', 20.00, 100.00, 20.00, 1, 800, 150, '2026-02-01 00:00:00', '2026-02-20 23:59:59', 'category', '[1]', 'active'),
(4, 'VIP50', 'VIP专享券', 'amount', 50.00, 500.00, 50.00, 1, 200, 50, '2026-02-01 00:00:00', '2026-02-25 23:59:59', 'all', NULL, 'active'),
(5, 'FLASHSALE', '秒杀专享券', 'discount', 0.8, 50.00, 100.00, 1, 300, 80, '2026-02-01 00:00:00', '2026-02-05 23:59:59', 'product', '[1, 2]', 'expired');

-- 插入优惠券使用记录数据
INSERT INTO coupon_usage_records (user_coupon_id, order_id, coupon_id, user_id, discount_amount, order_amount, used_at) VALUES
(1, 3, 1, 1003, 0.00, 5999.00, '2026-02-01 14:20:00'),
(2, 5, 2, 1005, 0.00, 5499.00, '2026-02-01 18:30:00'),
(3, 1, 3, 1001, 20.00, 299.00, '2026-02-01 10:30:00'),
(4, 4, 4, 1004, 50.00, 4999.00, '2026-02-01 16:45:00'),
(5, 2, 5, 1002, 0.00, 199.00, '2026-02-01 11:15:00');

COMMIT;