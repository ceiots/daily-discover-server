-- ============================================
-- 5. 订单模块表结构
-- 业务模块: 订单管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS user_addresses;

-- 用户收货地址表
CREATE TABLE IF NOT EXISTS user_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 收货信息
    receiver_name VARCHAR(100) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    
    -- 地区关联（使用国家标准地区编码）
    province_id VARCHAR(10) COMMENT '省份编码（关联regions表）',
    city_id VARCHAR(10) COMMENT '城市编码（关联regions表）',
    district_id VARCHAR(10) COMMENT '区县编码（关联regions表）',
    
    -- 详细地址
    detail_address VARCHAR(500) NOT NULL COMMENT '详细地址',
    
    -- 地址状态
    is_default TINYINT DEFAULT 0 COMMENT '是否默认地址：0-否 1-是',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default),
    INDEX idx_province_id (province_id),
    INDEX idx_city_id (city_id),
    INDEX idx_district_id (district_id)
) COMMENT '用户收货地址表';

-- 订单主表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    address_id BIGINT NOT NULL COMMENT '收货地址ID',
    
    -- 地区关联（冗余存储，便于查询）
    province_id VARCHAR(10) COMMENT '省份编码（关联regions表）',
    city_id VARCHAR(10) COMMENT '城市编码（关联regions表）',
    district_id VARCHAR(10) COMMENT '区县编码（关联regions表）',
    
    -- 订单金额信息
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    
    -- 订单状态
    status TINYINT NOT NULL COMMENT '订单状态：1-待付款 2-已付款 3-已发货 4-已完成 5-已取消',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    paid_at TIMESTAMP NULL COMMENT '付款时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_province_id (province_id),
    INDEX idx_city_id (city_id),
    INDEX idx_district_id (district_id)
) COMMENT '订单主表';

-- 订单项表（包含规格信息快照）
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
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

COMMIT;