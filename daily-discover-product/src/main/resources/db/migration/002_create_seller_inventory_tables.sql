-- ============================================
-- 商家与库存管理表结构
-- 创建时间: 2026-02-04
-- 业务模块: 商家与库存
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS inventory_transactions;
DROP TABLE IF EXISTS product_inventory;
DROP TABLE IF EXISTS product_skus;
DROP TABLE IF EXISTS seller_profiles;
DROP TABLE IF EXISTS sellers;

-- 商家基础信息表
CREATE TABLE IF NOT EXISTS sellers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商家ID',
    name VARCHAR(200) NOT NULL COMMENT '商家名称',
    description TEXT COMMENT '商家描述',
    logo_url VARCHAR(500) COMMENT '商家Logo',
    cover_url VARCHAR(500) COMMENT '商家封面图',
    rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '商家评分',
    response_time VARCHAR(50) COMMENT '响应时间',
    delivery_time VARCHAR(50) COMMENT '配送时间',
    followers_count INT DEFAULT 0 COMMENT '粉丝数量',
    total_products INT DEFAULT 0 COMMENT '商品总数',
    monthly_sales INT DEFAULT 0 COMMENT '月销量',
    is_verified BOOLEAN DEFAULT false COMMENT '是否认证',
    is_premium BOOLEAN DEFAULT false COMMENT '是否优选商家',
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active' COMMENT '商家状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_name (name),
    INDEX idx_rating (rating),
    INDEX idx_followers_count (followers_count),
    INDEX idx_monthly_sales (monthly_sales),
    INDEX idx_is_verified (is_verified),
    INDEX idx_is_premium (is_premium),
    INDEX idx_status (status)
) COMMENT '商家基础信息表';

-- 商家资料表
CREATE TABLE IF NOT EXISTS seller_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资料ID',
    seller_id BIGINT NOT NULL COMMENT '商家ID',
    positive_feedback DECIMAL(5,2) DEFAULT 0.0 COMMENT '好评率',
    contact_info JSON COMMENT '联系信息',
    services JSON COMMENT '服务项目',
    certifications JSON COMMENT '认证信息',
    business_hours JSON COMMENT '营业时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_seller_id (seller_id),
    INDEX idx_seller_id (seller_id)
) COMMENT '商家资料表';

-- 商品SKU表
CREATE TABLE IF NOT EXISTS product_skus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'SKU ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_code VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    sku_name VARCHAR(200) COMMENT 'SKU名称',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock_quantity INT DEFAULT 0 COMMENT '库存数量',
    reserved_quantity INT DEFAULT 0 COMMENT '预留数量',
    available_quantity INT DEFAULT 0 COMMENT '可用数量',
    spec_combination JSON COMMENT '规格组合',
    weight DECIMAL(8,2) COMMENT '重量(kg)',
    volume DECIMAL(8,2) COMMENT '体积(m³)',
    barcode VARCHAR(100) COMMENT '条形码',
    is_default BOOLEAN DEFAULT false COMMENT '是否默认SKU',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_sku_code (sku_code),
    INDEX idx_product_id (product_id),
    INDEX idx_price (price),
    INDEX idx_stock_quantity (stock_quantity),
    INDEX idx_available_quantity (available_quantity),
    INDEX idx_is_default (is_default),
    INDEX idx_is_active (is_active)
) COMMENT '商品SKU表';

-- 库存管理表
CREATE TABLE IF NOT EXISTS product_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '库存ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    warehouse_id BIGINT COMMENT '仓库ID',
    location_code VARCHAR(100) COMMENT '库位编码',
    quantity INT DEFAULT 0 COMMENT '库存数量',
    reserved_quantity INT DEFAULT 0 COMMENT '预留数量',
    available_quantity INT DEFAULT 0 COMMENT '可用数量',
    safety_stock INT DEFAULT 0 COMMENT '安全库存',
    min_stock_level INT DEFAULT 0 COMMENT '最低库存',
    max_stock_level INT DEFAULT 0 COMMENT '最高库存',
    last_restock_date TIMESTAMP NULL COMMENT '最后补货时间',
    next_restock_date TIMESTAMP NULL COMMENT '预计补货时间',
    inventory_status ENUM('normal', 'low', 'out_of_stock', 'overstock') DEFAULT 'normal' COMMENT '库存状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_location_code (location_code),
    INDEX idx_quantity (quantity),
    INDEX idx_available_quantity (available_quantity),
    INDEX idx_inventory_status (inventory_status)
) COMMENT '库存管理表';

-- 库存操作记录表
CREATE TABLE IF NOT EXISTS inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '操作ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID',
    transaction_type ENUM('in', 'out', 'adjust', 'transfer') NOT NULL COMMENT '操作类型',
    quantity_change INT NOT NULL COMMENT '数量变化',
    previous_quantity INT NOT NULL COMMENT '变更前数量',
    new_quantity INT NOT NULL COMMENT '变更后数量',
    reference_type ENUM('order', 'return', 'adjustment', 'transfer') COMMENT '关联类型',
    reference_id BIGINT COMMENT '关联ID',
    notes VARCHAR(500) COMMENT '备注',
    operator_id BIGINT COMMENT '操作人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_reference_type (reference_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_created_at (created_at)
) COMMENT '库存操作记录表';

COMMIT;

-- ============================================
-- 商家与库存表初始数据
-- ============================================

-- 插入商家信息数据
INSERT INTO sellers (id, name, description, logo_url, cover_url, rating, response_time, delivery_time, followers_count, positive_feedback, total_products, monthly_sales, contact_info, services, certifications, business_hours, is_verified, is_premium, status) VALUES
(1, 'TechBrand官方旗舰店', 'TechBrand官方旗舰店，提供最新科技产品', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200&h=200&fit=crop', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=800&h=400&fit=crop', 4.8, '5分钟内', '24小时内', 5000, 98.5, 50, 200, '{"phone": "400-123-4567", "email": "service@techbrand.com", "address": "北京市朝阳区科技园区"}', '["7天无理由退货", "全国联保", "快速发货"]', '["品牌授权", "正品保证"]', '{"weekdays": "9:00-18:00", "weekends": "10:00-17:00"}', true, true, 'active'),
(2, 'SoundTech官方旗舰店', 'SoundTech官方旗舰店，专业音频设备', 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=200&h=200&fit=crop', 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800&h=400&fit=crop', 4.7, '3分钟内', '48小时内', 3000, 97.8, 30, 150, '{"phone": "400-123-4568", "email": "service@soundtech.com", "address": "上海市浦东新区音频产业园"}', '["15天无理由退货", "专业售后", "免费试听"]', '["品牌授权", "音质认证"]', '{"weekdays": "8:30-17:30", "weekends": "9:00-16:00"}', true, true, 'active'),
(3, 'FashionStyle服饰店', '时尚潮流服饰，品质保证', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=200&h=200&fit=crop', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=800&h=400&fit=crop', 4.5, '10分钟内', '72小时内', 1500, 95.2, 100, 80, '{"phone": "400-123-4569", "email": "service@fashionstyle.com", "address": "广州市天河区服装城"}', '["30天退换货", "尺码不合适可换", "免费修改"]', '["品质认证", "面料检测"]', '{"weekdays": "9:00-21:00", "weekends": "10:00-20:00"}', true, false, 'active');

-- 插入商品SKU数据
INSERT INTO product_skus (id, product_id, sku_code, sku_name, price, original_price, stock_quantity, reserved_quantity, available_quantity, spec_combination, weight, volume, barcode, is_default, is_active) VALUES
(1, 1, 'SW001-BLK', '智能手表 Pro 黑色', 299.00, 399.00, 100, 5, 95, '{"颜色": "黑色", "尺寸": "标准"}', 0.05, 0.0001, '6931234567890', true, true),
(2, 1, 'SW001-WHT', '智能手表 Pro 白色', 299.00, 399.00, 80, 0, 80, '{"颜色": "白色", "尺寸": "标准"}', 0.05, 0.0001, '6931234567891', false, true),
(3, 2, 'HE001-BLK', '无线降噪耳机 黑色', 199.00, 299.00, 200, 10, 190, '{"颜色": "黑色"}', 0.08, 0.0002, '6931234567892', true, true),
(4, 3, 'LP001-SLV', '轻薄笔记本电脑 银色', 5999.00, 6999.00, 50, 0, 50, '{"颜色": "银色", "内存": "16GB", "硬盘": "512GB"}', 1.5, 0.015, '6931234567893', true, true);

-- 插入库存管理数据
INSERT INTO product_inventory (product_id, sku_id, warehouse_id, location_code, quantity, reserved_quantity, available_quantity, safety_stock, min_stock_level, max_stock_level, inventory_status) VALUES
(1, 1, 1, 'A-01-01', 100, 5, 95, 10, 5, 200, 'normal'),
(1, 2, 1, 'A-01-02', 80, 0, 80, 10, 5, 150, 'normal'),
(2, 3, 1, 'A-02-01', 200, 10, 190, 20, 10, 300, 'normal'),
(3, 4, 1, 'B-01-01', 50, 0, 50, 5, 2, 100, 'normal');

-- 插入库存操作记录数据
INSERT INTO inventory_transactions (product_id, sku_id, transaction_type, quantity_change, previous_quantity, new_quantity, reference_type, reference_id, notes, operator_id) VALUES
(1, 1, 'in', 100, 0, 100, 'adjustment', 1, '初始入库', 1),
(1, 2, 'in', 80, 0, 80, 'adjustment', 2, '初始入库', 1),
(2, 3, 'in', 200, 0, 200, 'adjustment', 3, '初始入库', 1),
(3, 4, 'in', 50, 0, 50, 'adjustment', 4, '初始入库', 1);

COMMIT;