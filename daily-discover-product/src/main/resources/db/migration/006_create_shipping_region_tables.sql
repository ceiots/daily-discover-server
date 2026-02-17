-- ============================================
-- 6. 物流和地区模块表结构
-- 业务模块: 物流管理、地区管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS order_shipping_tracks;
DROP TABLE IF EXISTS order_shipping;
DROP TABLE IF EXISTS regions;

-- 地区表（国家标准行政区划）
CREATE TABLE IF NOT EXISTS regions (
    region_id VARCHAR(10) PRIMARY KEY COMMENT '地区编码（国家标准GB/T 2260）',
    region_name VARCHAR(50) NOT NULL COMMENT '地区名称',
    region_parent_id VARCHAR(10) COMMENT '父级地区编码',
    region_level TINYINT NOT NULL COMMENT '地区级别：1-省/直辖市 2-市 3-区/县',
    region_code VARCHAR(20) COMMENT '行政地区编号（6位编码）',
    
    -- 索引优化
    INDEX idx_region_parent_id (region_parent_id),
    INDEX idx_region_level (region_level),
    INDEX idx_region_name (region_name)
) COMMENT '地区表（国家标准行政区划）';

-- 订单物流信息表
CREATE TABLE IF NOT EXISTS order_shipping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '物流ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    
    -- 物流公司信息
    shipping_company VARCHAR(100) COMMENT '物流公司',
    shipping_no VARCHAR(100) COMMENT '物流单号',
    shipping_fee DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
    
    -- 物流状态
    shipping_status TINYINT NOT NULL COMMENT '物流状态：1-待发货 2-已发货 3-运输中 4-派送中 5-已签收',
    
    -- 发货信息
    shipped_at TIMESTAMP NULL COMMENT '发货时间',
    estimated_delivery_at TIMESTAMP NULL COMMENT '预计送达时间',
    delivered_at TIMESTAMP NULL COMMENT '实际送达时间',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_order_id (order_id),
    INDEX idx_shipping_no (shipping_no),
    INDEX idx_shipping_status (shipping_status),
    UNIQUE KEY uk_order_id (order_id)
) COMMENT '订单物流信息表';

-- 物流跟踪记录表
CREATE TABLE IF NOT EXISTS order_shipping_tracks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '跟踪记录ID',
    shipping_id BIGINT NOT NULL COMMENT '物流ID',
    
    -- 跟踪信息
    track_time TIMESTAMP NOT NULL COMMENT '跟踪时间',
    track_description VARCHAR(500) NOT NULL COMMENT '跟踪描述',
    track_location VARCHAR(200) COMMENT '跟踪地点',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引优化
    INDEX idx_shipping_id (shipping_id),
    INDEX idx_track_time (track_time)
) COMMENT '物流跟踪记录表';

COMMIT;