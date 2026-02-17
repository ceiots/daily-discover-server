-- ============================================
-- 商家与库存管理表结构
-- 创建时间: 2026-02-04
-- 业务模块: 商家与库存
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS inventory_transactions;
DROP TABLE IF EXISTS product_inventory_config;
DROP TABLE IF EXISTS product_inventory_core;
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
    INDEX idx_status (status),
    
    -- 性能优化索引（精简版）
    INDEX idx_rating_status (rating, status) COMMENT '评分状态查询',
    INDEX idx_premium_status (is_premium, status) COMMENT '优选商家状态查询'
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



-- ============================================
-- 库存管理表（垂直分表设计 - 核心表）
-- ============================================

-- 库存核心表（高频读写，最小化字段）
CREATE TABLE IF NOT EXISTS product_inventory_core (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '库存ID',
    product_id BIGINT NOT NULL COMMENT '商品ID（关联products.id）',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID（引用product_skus.id）',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    
    -- 核心数量字段（高频更新）
    quantity INT DEFAULT 0 COMMENT '库存数量',
    reserved_quantity INT DEFAULT 0 COMMENT '预留数量',
    -- 注意：available_quantity = quantity - reserved_quantity（通过计算获得，避免冗余）
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 核心索引（最小化，提高写性能）
    INDEX idx_product_sku_warehouse (product_id, sku_id, warehouse_id) COMMENT '商品SKU仓库复合查询',
    INDEX idx_warehouse_product (warehouse_id, product_id) COMMENT '仓库商品查询',
    INDEX idx_quantity (quantity) COMMENT '数量查询',
    INDEX idx_updated_at (updated_at) COMMENT '更新时间排序',
    
    -- 唯一约束（避免重复库存记录）
    UNIQUE KEY uk_product_sku_warehouse (product_id, sku_id, warehouse_id)
) COMMENT '库存核心表（高频读写）';

-- 库存配置表（低频读写，扩展信息）
CREATE TABLE IF NOT EXISTS product_inventory_config (
    inventory_id BIGINT PRIMARY KEY COMMENT '库存ID（关联product_inventory_core.id）',
    
    -- 库存名称和标识
    inventory_name VARCHAR(200) COMMENT '库存名称（如：北京仓-电子产品区）',
    inventory_code VARCHAR(100) UNIQUE COMMENT '库存编码（唯一标识）',
    
    -- 库位信息（通过warehouse_id关联仓库信息，避免冗余存储）
    location_code VARCHAR(100) COMMENT '库位编码（仓库内的具体位置）',
    location_description VARCHAR(500) COMMENT '库位描述',
    
    -- 库存配置信息（低频更新）
    safety_stock INT DEFAULT 0 COMMENT '安全库存',
    min_stock_level INT DEFAULT 0 COMMENT '最低库存',
    max_stock_level INT DEFAULT 0 COMMENT '最高库存',
    
    -- 补货信息
    last_restock_date TIMESTAMP NULL COMMENT '最后补货时间',
    next_restock_date TIMESTAMP NULL COMMENT '预计补货时间',
    
    -- 时间戳（通过触发器确保创建时间不早于核心表）
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引优化
    INDEX idx_inventory_name (inventory_name) COMMENT '库存名称查询',
    INDEX idx_inventory_code (inventory_code) COMMENT '库存编码查询',
    INDEX idx_next_restock (next_restock_date) COMMENT '预计补货时间查询',
    INDEX idx_location_code (location_code) COMMENT '库位编码查询'
) COMMENT '库存配置表（低频读写）';

-- 库存操作记录表（关联库存核心表）
CREATE TABLE IF NOT EXISTS inventory_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '操作ID',
    inventory_id BIGINT NOT NULL COMMENT '库存ID（关联product_inventory_core.id）',
    product_id BIGINT NOT NULL COMMENT '商品ID（关联products.id）',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID（引用product_skus.id）',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    
    transaction_type ENUM('in', 'out', 'adjust', 'transfer') NOT NULL COMMENT '操作类型',
    quantity_change INT NOT NULL COMMENT '数量变化',
    previous_quantity INT NOT NULL COMMENT '变更前数量',
    new_quantity INT NOT NULL COMMENT '变更后数量',
    
    reference_type ENUM('order', 'return', 'adjustment', 'transfer') COMMENT '关联类型（order关联orders_core.id）',
    reference_id BIGINT COMMENT '关联ID（如订单ID、退货单ID等）',
    notes VARCHAR(500) COMMENT '备注',
    operator_id BIGINT COMMENT '操作人ID（关联users.id）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引优化
    INDEX idx_inventory_id (inventory_id) COMMENT '库存ID查询',
    INDEX idx_product_id (product_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_transaction_type (transaction_type),
    INDEX idx_reference_type (reference_type),
    INDEX idx_reference_id (reference_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_created_at (created_at),
    
    -- 复合索引优化
    INDEX idx_inventory_transaction (inventory_id, transaction_type) COMMENT '库存操作类型查询',
    INDEX idx_product_sku (product_id, sku_id) COMMENT '商品SKU关联查询',
    INDEX idx_reference_order (reference_type, reference_id) COMMENT '订单关联查询',
    INDEX idx_transaction_time (transaction_type, created_at) COMMENT '操作时间查询',
    INDEX idx_warehouse_product (warehouse_id, product_id) COMMENT '仓库商品查询'
) COMMENT '库存操作记录表';

COMMIT;

-- ============================================
-- 商家与库存表初始数据
-- ============================================

-- 插入商家信息数据
INSERT INTO sellers (id, name, description, logo_url, cover_url, rating, response_time, delivery_time, followers_count, total_products, monthly_sales, is_verified, is_premium, status) VALUES
(1, 'TechBrand官方旗舰店', 'TechBrand官方旗舰店，提供最新科技产品', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=200&h=200&fit=crop', 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=800&h=400&fit=crop', 4.8, '5分钟内', '24小时内', 5000, 50, 200, true, true, 'active'),
(2, 'SoundTech官方旗舰店', 'SoundTech官方旗舰店，专业音频设备', 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=200&h=200&fit=crop', 'https://images.unsplash.com/photo-1484704849700-f032a568e944?w=800&h=400&fit=crop', 4.7, '3分钟内', '48小时内', 3000, 30, 150, true, true, 'active'),
(3, 'FashionStyle服饰店', '时尚潮流服饰，品质保证', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=200&h=200&fit=crop', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=800&h=400&fit=crop', 4.5, '10分钟内', '72小时内', 1500, 100, 80, true, false, 'active');

-- 商品SKU数据已在001文件中定义，此处只管理库存数据

-- 插入库存核心数据
INSERT INTO product_inventory_core (id, product_id, sku_id, warehouse_id, quantity, reserved_quantity) VALUES
(1, 1, 1, 1, 100, 5),
(2, 1, 2, 1, 80, 0),
(3, 2, 3, 1, 200, 10),
(4, 3, 4, 1, 50, 0);

-- 插入库存配置数据
INSERT INTO product_inventory_config (inventory_id, inventory_name, inventory_code, location_code, location_description, safety_stock, min_stock_level, max_stock_level, last_restock_date, next_restock_date) VALUES
(1, '北京仓-电子产品区-A01', 'BJ-WH-A01', 'A-01-01', '北京仓库A区01货架', 10, 5, 200, '2026-02-01 10:00:00', '2026-02-15 10:00:00'),
(2, '北京仓-电子产品区-A02', 'BJ-WH-A02', 'A-01-02', '北京仓库A区02货架', 10, 5, 150, '2026-02-01 10:00:00', '2026-02-15 10:00:00'),
(3, '北京仓-音频设备区-A02', 'BJ-WH-A02-AUDIO', 'A-02-01', '北京仓库A区02音频设备区', 20, 10, 300, '2026-02-01 10:00:00', '2026-02-20 10:00:00'),
(4, '北京仓-电脑设备区-B01', 'BJ-WH-B01', 'B-01-01', '北京仓库B区01货架', 5, 2, 100, '2026-02-01 10:00:00', '2026-02-25 10:00:00');

-- 插入库存操作记录数据
INSERT INTO inventory_transactions (inventory_id, product_id, sku_id, warehouse_id, transaction_type, quantity_change, previous_quantity, new_quantity, reference_type, reference_id, notes, operator_id) VALUES
(1, 1, 1, 1, 'in', 100, 0, 100, 'adjustment', 1, '初始入库', 1),
(2, 1, 2, 1, 'in', 80, 0, 80, 'adjustment', 2, '初始入库', 1),
(3, 2, 3, 1, 'in', 200, 0, 200, 'adjustment', 3, '初始入库', 1),
(4, 3, 4, 1, 'in', 50, 0, 50, 'adjustment', 4, '初始入库', 1);

-- 插入商家资料数据
INSERT INTO seller_profiles (seller_id, positive_feedback, contact_info, services, certifications, business_hours) VALUES
(1, 98.5, '{"phone": "400-123-4567", "email": "service@techbrand.com", "address": "北京市朝阳区科技园区"}', '["7天无理由退货", "全国联保", "快速发货"]', '["品牌授权", "正品保证"]', '{"weekdays": "9:00-18:00", "weekends": "10:00-17:00"}'),
(2, 97.8, '{"phone": "400-123-4568", "email": "service@soundtech.com", "address": "上海市浦东新区音频产业园"}', '["15天无理由退货", "专业售后", "免费试听"]', '["品牌授权", "音质认证"]', '{"weekdays": "8:30-17:30", "weekends": "9:00-16:00"}'),
(3, 95.2, '{"phone": "400-123-4569", "email": "service@fashionstyle.com", "address": "广州市天河区服装城"}', '["30天退换货", "尺码不合适可换", "免费修改"]', '["品质认证", "面料检测"]', '{"weekdays": "9:00-21:00", "weekends": "10:00-20:00"}');

COMMIT;