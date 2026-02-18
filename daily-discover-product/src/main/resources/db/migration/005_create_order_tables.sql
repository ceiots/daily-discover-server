-- ============================================
-- 5. 订单模块表结构
-- 业务模块: 订单管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS after_sales_applications;
DROP TABLE IF EXISTS order_invoices;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders_extend;
DROP TABLE IF EXISTS orders_core;

-- 订单核心表（高频查询字段，8个字段）
CREATE TABLE IF NOT EXISTS orders_core (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 核心金额信息
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    
    -- 核心状态信息
    payment_status ENUM('unpaid', 'paid', 'refunding', 'refunded') DEFAULT 'unpaid' COMMENT '支付状态',
    status TINYINT NOT NULL COMMENT '订单状态：1-待付款 2-已付款 3-已发货 4-已完成 5-已取消 6-售后处理中',
    
    -- 核心时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 核心索引（高频查询）
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
) COMMENT '订单核心表（高频查询字段）';

-- 订单扩展表（低频查询字段，14个字段）
CREATE TABLE IF NOT EXISTS orders_extend (
    order_id BIGINT PRIMARY KEY COMMENT '订单ID（关联orders_core.id）',
    
    -- 地址信息
    address_id BIGINT NOT NULL COMMENT '收货地址ID（关联user_addresses.id）',
    province_id VARCHAR(10) COMMENT '省份编码（关联regions表）',
    city_id VARCHAR(10) COMMENT '城市编码（关联regions表）',
    district_id VARCHAR(10) COMMENT '区县编码（关联regions表）',
    detailed_address VARCHAR(200) COMMENT '详细地址（街道、门牌号等）',
    
    -- 优惠信息
    coupon_id BIGINT COMMENT '优惠券ID',
    coupon_discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠券折扣金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    
    -- 支付方式
    payment_method_id BIGINT COMMENT '支付方式ID（关联payment_methods.id）',
    
    -- 物流关联
    shipping_id BIGINT COMMENT '物流ID（关联order_shipping.id）',
    
    -- 发票关联
    invoice_id BIGINT COMMENT '发票ID（关联order_invoices.id）',
    
    -- 扩展时间戳
    paid_at TIMESTAMP NULL COMMENT '付款时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    -- 扩展索引
    INDEX idx_address_id (address_id),
    INDEX idx_coupon_id (coupon_id),
    INDEX idx_payment_method_id (payment_method_id),
    INDEX idx_shipping_id (shipping_id),
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_paid_at (paid_at),
    
    -- 关联索引优化
    INDEX idx_address_order (address_id, order_id) COMMENT '地址订单关联查询'
) COMMENT '订单扩展表（低频查询字段）';

-- 订单项表（包含规格信息快照）
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID（关联orders_core.id）',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    
    -- 商品信息快照（防止商品信息变更）
    product_title VARCHAR(200) NOT NULL COMMENT '商品标题快照',
    product_image VARCHAR(500) COMMENT '商品图片快照',
    
    -- 规格信息快照（持久化存储用户选择的规格）
    specs_json JSON COMMENT '规格组合JSON快照：{"颜色": "黑色", "存储": "128GB"}',
    specs_text VARCHAR(500) COMMENT '规格文本快照：黑色 128GB',
    
    -- 价格信息快照
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价快照',
    quantity INT NOT NULL COMMENT '购买数量',
    subtotal_amount DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_sku_id (sku_id)
) COMMENT '订单项表（包含规格信息快照）';

-- ============================================
-- 发票表（订单相关）
-- ============================================

-- 订单发票表
CREATE TABLE IF NOT EXISTS order_invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '发票ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    
    -- 发票类型
    invoice_type ENUM('personal', 'company') DEFAULT 'personal' COMMENT '发票类型：个人、企业',
    invoice_title VARCHAR(200) NOT NULL COMMENT '发票抬头',
    
    -- 企业发票信息
    tax_number VARCHAR(50) COMMENT '纳税人识别号',
    company_address VARCHAR(200) COMMENT '企业地址',
    company_phone VARCHAR(20) COMMENT '企业电话',
    bank_name VARCHAR(100) COMMENT '开户银行',
    bank_account VARCHAR(50) COMMENT '银行账户',
    
    -- 发票内容
    invoice_content VARCHAR(100) DEFAULT '商品明细' COMMENT '发票内容',
    invoice_amount DECIMAL(10,2) NOT NULL COMMENT '开票金额',
    
    -- 发票状态
    invoice_status ENUM('pending', 'issued', 'cancelled') DEFAULT 'pending' COMMENT '发票状态',
    
    -- 开票信息
    issued_at TIMESTAMP NULL COMMENT '开票时间',
    invoice_no VARCHAR(100) COMMENT '发票号码',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_order_id (order_id),
    INDEX idx_invoice_status (invoice_status),
    INDEX idx_issued_at (issued_at),
    UNIQUE KEY uk_order_id (order_id)
) COMMENT '订单发票表';

-- ============================================
-- 售后申请表（合并到订单模块中）
-- ============================================

-- 售后申请表
CREATE TABLE IF NOT EXISTS after_sales_applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '售后申请ID',
    application_no VARCHAR(50) NOT NULL UNIQUE COMMENT '售后申请单号',
    
    -- 核心关联信息
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    
    -- 售后类型和原因
    after_sales_type ENUM('refund', 'exchange', 'repair') NOT NULL COMMENT '售后类型：退款、换货、维修',
    apply_reason VARCHAR(500) NOT NULL COMMENT '申请原因',
    
    -- 商品信息（简化快照）
    product_title VARCHAR(200) NOT NULL COMMENT '商品标题快照',
    purchase_price DECIMAL(10,2) NOT NULL COMMENT '购买价格快照',
    
    -- 售后状态
    status ENUM('pending', 'approved', 'rejected', 'processing', 'completed') DEFAULT 'pending' COMMENT '售后状态',
    
    -- 处理信息
    processor_id BIGINT COMMENT '处理人ID',
    process_notes VARCHAR(500) COMMENT '处理意见',
    
    -- 退款信息
    refund_amount DECIMAL(10,2) DEFAULT 0 COMMENT '退款金额',
    
    -- 时间戳
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    processed_at TIMESTAMP NULL COMMENT '处理时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_after_sales_type (after_sales_type),
    INDEX idx_applied_at (applied_at)
) COMMENT '售后申请表';

COMMIT;

-- ============================================
-- 订单模块表初始数据
-- ============================================

-- 插入订单核心数据
INSERT INTO orders_core (id, order_no, user_id, total_amount, actual_amount, payment_status, status) VALUES
(1, 'DD202602010001', 1001, 299.00, 299.00, 'paid', 4),
(2, 'DD202602010002', 1002, 199.00, 199.00, 'paid', 4),
(3, 'DD202602010003', 1003, 5999.00, 5999.00, 'paid', 3),
(4, 'DD202602010004', 1004, 4999.00, 4999.00, 'paid', 2),
(5, 'DD202602010005', 1005, 5499.00, 5499.00, 'unpaid', 1);

-- 插入订单扩展数据
INSERT INTO orders_extend (order_id, address_id, province_id, city_id, district_id, detailed_address, coupon_id, coupon_discount_amount, discount_amount, payment_method_id, paid_at) VALUES
(1, 1, '11', '1101', '110101', '北京市东城区王府井大街100号', NULL, 0.00, 0.00, 1, '2026-02-01 10:30:00'),
(2, 2, '31', '3101', '310101', '上海市黄浦区南京东路200号', NULL, 0.00, 0.00, 2, '2026-02-01 11:15:00'),
(3, 3, '44', '4401', '440103', '广州市天河区体育西路300号', 1, 0.00, 100.00, 1, '2026-02-01 14:20:00'),
(4, 4, '11', '1101', '110102', '北京市西城区金融街400号', NULL, 0.00, 0.00, 1, '2026-02-01 16:45:00'),
(5, 5, '31', '3101', '310104', '上海市静安区南京西路500号', 2, 0.00, 0.00, NULL, NULL);

-- 插入订单项数据
INSERT INTO order_items (order_id, product_id, sku_id, product_title, product_image, specs_json, specs_text, unit_price, quantity, subtotal_amount) VALUES
(1, 1, 1, '智能手表 Pro', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop', '{"颜色": "黑色", "存储": "128GB"}', '黑色 128GB', 299.00, 1, 299.00),
(2, 2, 3, '无线降噪耳机', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', '{"颜色": "黑色"}', '黑色', 199.00, 1, 199.00),
(3, 3, 4, '轻薄笔记本电脑', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', '{"配置": "i5 8G 256G"}', 'i5 8G 256G', 5999.00, 1, 5999.00),
(4, 4, 1, '智能手机旗舰版', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '{"颜色": "黑色", "存储": "128GB"}', '黑色 128GB', 4999.00, 1, 4999.00),
(5, 4, 4, '智能手机旗舰版', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '{"颜色": "黑色", "存储": "256GB"}', '黑色 256GB', 5499.00, 1, 5499.00);

-- 插入发票数据
INSERT INTO order_invoices (order_id, invoice_type, invoice_title, tax_number, company_address, company_phone, bank_name, bank_account, invoice_content, invoice_amount, invoice_status, issued_at, invoice_no) VALUES
(3, 'company', '广州科技有限公司', '91440101MA5XXXXXXX', '广州市天河区体育西路300号', '020-12345678', '中国工商银行', '6222021234567890123', '商品明细', 5999.00, 'issued', '2026-02-01 15:00:00', 'FP202602010001'),
(4, 'personal', '张三', NULL, NULL, NULL, NULL, NULL, '商品明细', 4999.00, 'pending', NULL, NULL);

-- 插入售后申请数据
INSERT INTO after_sales_applications (application_no, order_id, user_id, product_id, after_sales_type, apply_reason, product_title, purchase_price, status, processor_id, process_notes, refund_amount) VALUES
('AS202602010001', 1, 1001, 1, 'refund', '商品存在质量问题，无法正常使用', '智能手表 Pro', 299.00, 'approved', 1, '经核实，商品确实存在质量问题，同意退款', 299.00),
('AS202602010002', 2, 1002, 2, 'exchange', '收到商品颜色与订单不符', '无线降噪耳机', 199.00, 'processing', NULL, NULL, 0.00);

COMMIT;