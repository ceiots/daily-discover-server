-- ============================================
-- 7. 支付流程模块表结构
-- 业务模块: 支付管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS refund_records;
DROP TABLE IF EXISTS payment_transactions;
DROP TABLE IF EXISTS payment_methods;

-- 支付方式表
CREATE TABLE IF NOT EXISTS payment_methods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付方式ID',
    method_code VARCHAR(50) NOT NULL UNIQUE COMMENT '支付方式代码',
    method_name VARCHAR(100) NOT NULL COMMENT '支付方式名称',
    method_type ENUM('online', 'offline', 'wallet') NOT NULL COMMENT '支付类型',
    icon_url VARCHAR(500) COMMENT '图标URL',
    description VARCHAR(500) COMMENT '描述',
    is_enabled BOOLEAN DEFAULT true COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    config_params JSON COMMENT '配置参数',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_method_code (method_code),
    INDEX idx_method_type (method_type),
    INDEX idx_is_enabled (is_enabled),
    INDEX idx_sort_order (sort_order)
) COMMENT '支付方式表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '支付记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID（关联orders_core.id）',
    payment_method_id BIGINT NOT NULL COMMENT '支付方式ID（关联payment_methods.id）',
    transaction_no VARCHAR(100) NOT NULL UNIQUE COMMENT '支付交易号',
    
    -- 支付金额信息
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    currency VARCHAR(10) DEFAULT 'CNY' COMMENT '货币代码',
    
    -- 支付状态
    status ENUM('pending', 'processing', 'success', 'failed', 'cancelled') DEFAULT 'pending' COMMENT '支付状态',
    
    -- 第三方支付信息
    third_party_transaction_no VARCHAR(100) COMMENT '第三方交易号',
    third_party_status VARCHAR(100) COMMENT '第三方状态',
    third_party_response JSON COMMENT '第三方响应数据',
    
    -- 支付时间信息
    payment_request_at TIMESTAMP NULL COMMENT '支付请求时间',
    payment_completed_at TIMESTAMP NULL COMMENT '支付完成时间',
    payment_expired_at TIMESTAMP NULL COMMENT '支付过期时间',
    
    -- 错误信息
    error_code VARCHAR(100) COMMENT '错误代码',
    error_message VARCHAR(500) COMMENT '错误信息',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_order_id (order_id),
    INDEX idx_payment_method_id (payment_method_id),
    INDEX idx_transaction_no (transaction_no),
    INDEX idx_status (status),
    INDEX idx_third_party_transaction_no (third_party_transaction_no),
    INDEX idx_payment_request_at (payment_request_at),
    INDEX idx_payment_completed_at (payment_completed_at),
    
    -- 复合索引优化（提高支付状态同步查询性能）
    INDEX idx_order_status (order_id, status) COMMENT '订单支付状态查询',
    INDEX idx_method_status (payment_method_id, status) COMMENT '支付方式状态查询',
    INDEX idx_time_status (created_at, status) COMMENT '时间状态查询',
    INDEX idx_order_method (order_id, payment_method_id) COMMENT '订单支付方式关联查询'
) COMMENT '支付记录表';

-- 退款记录表
CREATE TABLE IF NOT EXISTS refund_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '退款记录ID',
    payment_transaction_id BIGINT NOT NULL COMMENT '支付记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    refund_no VARCHAR(100) NOT NULL UNIQUE COMMENT '退款单号',
    
    -- 退款金额信息
    refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    refund_reason VARCHAR(500) COMMENT '退款原因',
    
    -- 退款状态
    status ENUM('pending', 'processing', 'success', 'failed') DEFAULT 'pending' COMMENT '退款状态',
    
    -- 第三方退款信息
    third_party_refund_no VARCHAR(100) COMMENT '第三方退款号',
    third_party_status VARCHAR(100) COMMENT '第三方状态',
    third_party_response JSON COMMENT '第三方响应数据',
    
    -- 退款时间信息
    refund_request_at TIMESTAMP NULL COMMENT '退款请求时间',
    refund_completed_at TIMESTAMP NULL COMMENT '退款完成时间',
    
    -- 操作信息
    operator_id BIGINT COMMENT '操作人ID',
    operator_notes VARCHAR(500) COMMENT '操作备注',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_payment_transaction_id (payment_transaction_id),
    INDEX idx_order_id (order_id),
    INDEX idx_refund_no (refund_no),
    INDEX idx_status (status),
    INDEX idx_refund_request_at (refund_request_at)
) COMMENT '退款记录表';

COMMIT;

-- ============================================
-- 支付流程模块表初始数据
-- ============================================

-- 插入支付方式数据
INSERT INTO payment_methods (id, method_code, method_name, method_type, icon_url, description, is_enabled, sort_order, config_params) VALUES
(1, 'alipay', '支付宝', 'online', 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=100&h=100&fit=crop', '支付宝在线支付', true, 1, '{"app_id": "2021000123456789", "merchant_id": "2088123456789012"}'),
(2, 'wechat_pay', '微信支付', 'online', 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=100&h=100&fit=crop&brightness=0.8', '微信在线支付', true, 2, '{"app_id": "wx1234567890abcdef", "mch_id": "1234567890"}'),
(3, 'union_pay', '银联支付', 'online', 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=100&h=100&fit=crop&brightness=0.6', '银联在线支付', true, 3, '{"merchant_id": "123456789012345"}'),
(4, 'balance', '余额支付', 'wallet', 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=100&h=100&fit=crop', '使用账户余额支付', true, 4, '{"min_amount": 0.01, "max_amount": 10000.00}'),
(5, 'cod', '货到付款', 'offline', 'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=100&h=100&fit=crop&brightness=0.4', '货到付款，支持现金支付', true, 5, '{"max_amount": 5000.00, "supported_regions": ["11", "31", "44"]}');

-- 插入支付记录数据
INSERT INTO payment_transactions (id, order_id, payment_method_id, transaction_no, amount, currency, status, third_party_transaction_no, third_party_status, payment_request_at, payment_completed_at, payment_expired_at) VALUES
(1, 1, 1, 'PAY202602010001', 299.00, 'CNY', 'success', '20260201200040011111111111111111', 'TRADE_SUCCESS', '2026-02-01 10:25:00', '2026-02-01 10:30:00', '2026-02-01 11:25:00'),
(2, 2, 2, 'PAY202602010002', 199.00, 'CNY', 'success', '4200002026020100000000000001', 'SUCCESS', '2026-02-01 11:10:00', '2026-02-01 11:15:00', '2026-02-01 12:10:00'),
(3, 3, 1, 'PAY202602010003', 5999.00, 'CNY', 'success', '20260201200040011111111111111112', 'TRADE_SUCCESS', '2026-02-01 14:15:00', '2026-02-01 14:20:00', '2026-02-01 15:15:00'),
(4, 4, 1, 'PAY202602010004', 4999.00, 'CNY', 'success', '20260201200040011111111111111113', 'TRADE_SUCCESS', '2026-02-01 16:40:00', '2026-02-01 16:45:00', '2026-02-01 17:40:00'),
(5, 5, 1, 'PAY202602010005', 5499.00, 'CNY', 'pending', NULL, NULL, '2026-02-01 18:30:00', NULL, '2026-02-01 19:30:00');

-- 插入退款记录数据
INSERT INTO refund_records (payment_transaction_id, order_id, refund_no, refund_amount, refund_reason, status, third_party_refund_no, third_party_status, refund_request_at, refund_completed_at, operator_id, operator_notes) VALUES
(1, 1, 'REF202602010001', 299.00, '商品质量问题退款', 'success', '20260201200040011111111111111114', 'REFUND_SUCCESS', '2026-02-02 09:30:00', '2026-02-02 09:35:00', 1, '商品质量问题，同意退款'),
(2, 2, 'REF202602010002', 199.00, '颜色不符换货退款', 'processing', NULL, NULL, '2026-02-02 10:15:00', NULL, 1, '等待商家确认换货');

COMMIT;