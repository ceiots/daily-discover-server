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
    order_id BIGINT NOT NULL COMMENT '订单ID',
    payment_method_id BIGINT NOT NULL COMMENT '支付方式ID',
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
    INDEX idx_payment_completed_at (payment_completed_at)
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