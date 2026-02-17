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
    activity_type ENUM('discount', 'coupon', 'points', 'flash_sale', 'group_buy') NOT NULL COMMENT '活动类型',
    
    -- 活动时间
    start_time TIMESTAMP NOT NULL COMMENT '开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '结束时间',
    
    -- 活动范围
    target_type ENUM('all', 'category', 'product', 'user_group') DEFAULT 'all' COMMENT '目标类型',
    target_ids JSON COMMENT '目标ID集合',
    
    -- 活动规则
    rules JSON COMMENT '活动规则配置',
    description TEXT COMMENT '活动描述',
    
    -- 活动状态
    status ENUM('draft', 'active', 'paused', 'ended') DEFAULT 'draft' COMMENT '活动状态',
    
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
    coupon_type ENUM('discount', 'amount', 'free_shipping') NOT NULL COMMENT '优惠券类型',
    
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
    applicable_scope ENUM('all', 'category', 'product', 'brand') DEFAULT 'all' COMMENT '适用范围',
    applicable_ids JSON COMMENT '适用ID集合',
    
    -- 状态
    status ENUM('draft', 'active', 'paused', 'expired') DEFAULT 'draft' COMMENT '状态',
    
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
    user_coupon_id BIGINT NOT NULL COMMENT '用户优惠券ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
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
    INDEX idx_used_at (used_at)
) COMMENT '优惠券使用记录表';



COMMIT;