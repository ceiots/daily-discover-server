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

-- ============================================
-- 物流和地区模块表初始数据
-- ============================================

-- 插入地区数据（国家标准行政区划）
INSERT INTO regions (region_id, region_name, region_parent_id, region_level, region_code) VALUES
-- 省级数据
('11', '北京市', NULL, 1, '110000'),
('12', '天津市', NULL, 1, '120000'),
('13', '河北省', NULL, 1, '130000'),
('31', '上海市', NULL, 1, '310000'),
('32', '江苏省', NULL, 1, '320000'),
('33', '浙江省', NULL, 1, '330000'),
('44', '广东省', NULL, 1, '440000'),

-- 市级数据（北京市）
('1101', '北京市', '11', 2, '110100'),

-- 市级数据（上海市）
('3101', '上海市', '31', 2, '310100'),

-- 市级数据（广东省）
('4401', '广州市', '44', 2, '440100'),
('4403', '深圳市', '44', 2, '440300'),

-- 区县级数据（北京市）
('110101', '东城区', '1101', 3, '110101'),
('110102', '西城区', '1101', 3, '110102'),
('110105', '朝阳区', '1101', 3, '110105'),

-- 区县级数据（上海市）
('310101', '黄浦区', '3101', 3, '310101'),
('310104', '徐汇区', '3101', 3, '310104'),
('310106', '静安区', '3101', 3, '310106'),

-- 区县级数据（广州市）
('440103', '荔湾区', '4401', 3, '440103'),
('440104', '越秀区', '4401', 3, '440104'),
('440106', '天河区', '4401', 3, '440106');

-- 插入订单物流信息数据
INSERT INTO order_shipping (order_id, shipping_company, shipping_no, shipping_fee, shipping_status, shipped_at, estimated_delivery_at, delivered_at) VALUES
(1, '顺丰速运', 'SF202602010001', 15.00, 5, '2026-02-01 14:30:00', '2026-02-03 18:00:00', '2026-02-03 15:20:00'),
(2, '圆通速递', 'YT202602010002', 12.00, 5, '2026-02-01 16:45:00', '2026-02-04 20:00:00', '2026-02-04 14:30:00'),
(3, '京东物流', 'JD202602010003', 20.00, 3, '2026-02-01 15:20:00', '2026-02-05 18:00:00', NULL),
(4, '顺丰速运', 'SF202602010004', 18.00, 2, NULL, '2026-02-06 20:00:00', NULL),
(5, '中通快递', 'ZT202602010005', 10.00, 1, NULL, '2026-02-07 18:00:00', NULL);

-- 插入物流跟踪记录数据
INSERT INTO order_shipping_tracks (shipping_id, track_time, track_description, track_location) VALUES
(1, '2026-02-01 14:30:00', '快件已揽收', '北京市东城区'),
(1, '2026-02-01 16:45:00', '快件已到达北京转运中心', '北京市'),
(1, '2026-02-02 08:30:00', '快件已发往上海转运中心', '北京市'),
(1, '2026-02-03 10:15:00', '快件已到达上海转运中心', '上海市'),
(1, '2026-02-03 14:20:00', '快件正在派送中', '上海市黄浦区'),
(1, '2026-02-03 15:20:00', '快件已签收，签收人：张三', '上海市黄浦区'),

(2, '2026-02-01 16:45:00', '快件已揽收', '上海市黄浦区'),
(2, '2026-02-02 09:30:00', '快件已到达上海转运中心', '上海市'),
(2, '2026-02-03 11:20:00', '快件已发往广州转运中心', '上海市'),
(2, '2026-02-04 08:45:00', '快件已到达广州转运中心', '广州市'),
(2, '2026-02-04 13:15:00', '快件正在派送中', '广州市天河区'),
(2, '2026-02-04 14:30:00', '快件已签收，签收人：李四', '广州市天河区'),

(3, '2026-02-01 15:20:00', '快件已揽收', '广州市天河区'),
(3, '2026-02-01 17:30:00', '快件已到达广州转运中心', '广州市'),
(3, '2026-02-02 10:45:00', '快件已发往北京转运中心', '广州市'),
(3, '2026-02-03 14:20:00', '快件已到达北京转运中心', '北京市'),
(3, '2026-02-04 09:30:00', '快件正在派送中', '北京市西城区');

COMMIT;