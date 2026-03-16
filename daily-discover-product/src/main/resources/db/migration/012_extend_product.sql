-- ============================================
-- 扩展选填表 - 卖点、标签相关
-- 业务模块: 商品扩展属性（提升商品吸引力）
-- 新增商品时可按需插入，非必需
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_selling_point_relations;
DROP TABLE IF EXISTS product_selling_points;
DROP TABLE IF EXISTS product_tag_relations;
DROP TABLE IF EXISTS product_tags;

-- ============================================
-- 6. 商品标签系统模块（商品属性扩展）
-- ============================================

-- 商品标签表
CREATE TABLE IF NOT EXISTS product_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    tag_type VARCHAR(20) DEFAULT 'custom' COMMENT '标签类型',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_tag_name (tag_name),
    INDEX idx_tag_type (tag_type),
    
    -- 唯一约束（防止重复标签）
    UNIQUE KEY uk_tag_name (tag_name)
) COMMENT '商品标签表';

-- 商品标签关联表
CREATE TABLE IF NOT EXISTS product_tag_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    
    UNIQUE KEY uk_product_tag (product_id, tag_id),
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_tag_id (tag_id),
    INDEX idx_product_tag (product_id, tag_id)
) COMMENT '商品标签关联表';

-- ============================================
-- 7. 商品卖点标签系统模块（推荐系统专用）
-- ============================================

-- 商品卖点标签表（推荐系统特征标签）
CREATE TABLE IF NOT EXISTS product_selling_points (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '卖点ID',
    
    -- 卖点基本信息
    point_name VARCHAR(50) NOT NULL COMMENT '卖点名称（通用、可复用、分级描述）',
    point_category VARCHAR(50) NOT NULL COMMENT '卖点大类（安全性、性能感、体验感、健康呵护、耐用性）',
    point_sub_category VARCHAR(50) COMMENT '卖点小类（材质、降噪、续航等）',
    point_description VARCHAR(200) COMMENT '卖点说明（具体、量化、打动人、有画面感）',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    UNIQUE KEY uk_point_name (point_name) COMMENT '卖点名称唯一性',
    INDEX idx_point_category (point_category),
    INDEX idx_point_sub_category (point_sub_category),
    INDEX idx_category_sub_category (point_category, point_sub_category)
) COMMENT '商品卖点标签表（推荐系统特征标签）';

-- 商品卖点关系表（商品与卖点关联）
CREATE TABLE IF NOT EXISTS product_selling_point_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    selling_point_id BIGINT NOT NULL COMMENT '卖点ID',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    UNIQUE KEY uk_product_point (product_id, selling_point_id) COMMENT '商品卖点唯一性',
    INDEX idx_product_id (product_id),
    INDEX idx_selling_point_id (selling_point_id)
) COMMENT '商品卖点关系表（商品与卖点关联）';

-- ============================================
-- 初始数据（可选）
-- ============================================

-- 插入商品标签数据（示例）
INSERT INTO product_tags (tag_name, tag_type) VALUES
('新品上市', 'system'),
('热销爆款', 'system'),
('限时优惠', 'system'),
('品质保证', 'system'),
('环保材质', 'custom'),
('智能科技', 'custom'),
('便携设计', 'custom'),
('高性价比', 'custom');

-- 插入商品卖点标签数据（示例）
INSERT INTO product_selling_points (point_name, point_category, point_sub_category, point_description) VALUES
('超长续航', '性能感', '续航', '一次充电可使用长达12小时，满足全天使用需求'),
('防水防尘', '耐用性', '防水', '日常泼溅、雨水、灰尘都不怕，户外、浴室、厨房都能安心使用'),
('智能降噪', '体验感', '降噪', '主动降噪技术，有效隔绝外界噪音，专注享受音乐时光'),
('轻巧便携', '体验感', '便携', '重量仅250克，轻松放入背包，随时随地享受高品质音乐'),
('快速充电', '性能感', '充电', '充电15分钟，可使用3小时，紧急情况下快速恢复使用'),
('高清音质', '体验感', '音质', '专业调音，还原音乐细节，带来身临其境的听觉体验'),
('舒适佩戴', '体验感', '舒适', '人体工学设计，长时间佩戴也不会感到不适'),
('多设备连接', '性能感', '连接', '支持同时连接2台设备，轻松切换使用场景');

-- 为现有商品插入标签关联（示例）
INSERT INTO product_tag_relations (product_id, tag_id) VALUES
(1, 1), (1, 2), (1, 4),  -- 智能手表 Pro
(2, 2), (2, 3), (2, 6),  -- 无线降噪耳机
(3, 1), (3, 4), (3, 7),  -- 轻薄笔记本电脑
(4, 2), (4, 3), (4, 8),  -- 旗舰手机
(5, 5), (5, 6), (5, 7);  -- 运动蓝牙耳机

-- 为现有商品插入卖点关联（示例）
INSERT INTO product_selling_point_relations (product_id, selling_point_id) VALUES
(1, 1), (1, 2), (1, 5),  -- 智能手表 Pro
(2, 3), (2, 6), (2, 7),  -- 无线降噪耳机
(3, 4), (3, 8),          -- 轻薄笔记本电脑
(4, 1), (4, 2), (4, 3),  -- 旗舰手机
(5, 2), (5, 4), (5, 7);  -- 运动蓝牙耳机