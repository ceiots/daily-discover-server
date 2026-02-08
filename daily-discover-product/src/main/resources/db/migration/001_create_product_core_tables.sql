-- ============================================
-- 商品核心信息表结构
-- 创建时间: 2026-02-04
-- 业务模块: 商品核心信息
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_categories;
DROP TABLE IF EXISTS product_specs;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS product_details;
DROP TABLE IF EXISTS products;

-- 商品基础信息表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    seller_id BIGINT NOT NULL COMMENT '商家ID',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    brand VARCHAR(100) COMMENT '品牌',
    base_price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    discount DECIMAL(5,2) COMMENT '折扣百分比',
    rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '评分',
    review_count INT DEFAULT 0 COMMENT '评价数量',
    total_sales INT DEFAULT 0 COMMENT '总销量',
    monthly_sales INT DEFAULT 0 COMMENT '月销量',
    is_new BOOLEAN DEFAULT false COMMENT '是否新品',
    is_hot BOOLEAN DEFAULT false COMMENT '是否热销',
    is_recommended BOOLEAN DEFAULT false COMMENT '是否推荐',
    status ENUM('active', 'inactive', 'deleted') DEFAULT 'active' COMMENT '商品状态',
    urgency_level ENUM('high', 'medium', 'normal') DEFAULT 'normal' COMMENT '热点紧急程度',
    hotspot_type VARCHAR(50) COMMENT '热点类型',
    tags JSON COMMENT '商品标签',
    main_image_url VARCHAR(500) COMMENT '商品主图URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_seller_id (seller_id),
    INDEX idx_category_id (category_id),
    INDEX idx_brand (brand),
    INDEX idx_base_price (base_price),
    INDEX idx_rating (rating),
    INDEX idx_total_sales (total_sales),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_is_new (is_new),
    INDEX idx_is_hot (is_hot),
    INDEX idx_is_recommended (is_recommended)
) COMMENT '商品基础信息表';

-- 商品详情表
CREATE TABLE IF NOT EXISTS product_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '详情ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    specifications JSON COMMENT '规格参数',
    features JSON COMMENT '商品特性',
    usage_instructions JSON COMMENT '使用说明',
    precautions JSON COMMENT '注意事项',
    package_contents JSON COMMENT '包装清单',
    warranty_info JSON COMMENT '保修信息',
    shipping_info JSON COMMENT '配送信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_product_id (product_id),
    INDEX idx_product_id (product_id)
) COMMENT '商品详情表';

-- 商品图片表
CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图片ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    image_type ENUM('main', 'multi_angle', 'details', 'scenes', 'certificate') NOT NULL COMMENT '图片类型',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    alt_text VARCHAR(200) COMMENT '图片描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_primary BOOLEAN DEFAULT false COMMENT '是否主图',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_image_type (image_type),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_primary (is_primary)
) COMMENT '商品图片表';

-- 商品规格表
CREATE TABLE IF NOT EXISTS product_specs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    spec_name VARCHAR(100) NOT NULL COMMENT '规格名称',
    spec_label VARCHAR(100) COMMENT '规格标签',
    spec_value VARCHAR(200) COMMENT '规格值',
    spec_unit VARCHAR(50) COMMENT '规格单位',
    spec_group VARCHAR(100) COMMENT '规格分组',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_spec_name (spec_name),
    INDEX idx_spec_group (spec_group),
    INDEX idx_sort_order (sort_order)
) COMMENT '商品规格表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS product_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(500) COMMENT '分类描述',
    image_url VARCHAR(500) COMMENT '分类图片',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    level INT DEFAULT 1 COMMENT '分类层级',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_parent_id (parent_id),
    INDEX idx_name (name),
    INDEX idx_level (level),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_active (is_active)
) COMMENT '商品分类表';

COMMIT;

-- ============================================
-- 商品核心信息表初始数据
-- ============================================

-- 插入商品分类数据
INSERT INTO product_categories (id, parent_id, name, description, image_url, sort_order, level, is_active) VALUES
(1, NULL, '电子产品', '各类电子设备及配件', 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=300&h=200&fit=crop', 1, 1, true),
(2, 1, '手机通讯', '智能手机、功能手机等', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300&h=200&fit=crop', 1, 2, true),
(3, 1, '电脑办公', '笔记本电脑、台式机等', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=300&h=200&fit=crop', 2, 2, true),
(4, 1, '智能穿戴', '智能手表、手环等', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&h=200&fit=crop', 3, 2, true),
(5, NULL, '服饰鞋包', '服装、鞋类、箱包等', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=300&h=200&fit=crop', 2, 1, true),
(6, 5, '男装', '男士服装', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=200&fit=crop', 1, 2, true),
(7, 5, '女装', '女士服装', 'https://images.unsplash.com/photo-1499952127939-9bbf5af6c51c?w=300&h=200&fit=crop', 2, 2, true);

-- 插入商品基础信息数据
INSERT INTO products (id, seller_id, title, description, category_id, brand, base_price, original_price, discount, rating, review_count, total_sales, monthly_sales, is_new, is_hot, is_recommended, status, tags, main_image_url) VALUES
(1, 1, '智能手表 Pro', '多功能智能手表，支持心率监测、运动追踪、消息提醒等功能', 4, 'TechBrand', 299.00, 399.00, 25.00, 4.5, 128, 500, 50, true, true, true, 'active', '["智能", "运动", "健康"]', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop'),
(2, 1, '无线降噪耳机', '主动降噪无线耳机，音质清晰，续航持久', 4, 'SoundTech', 199.00, 299.00, 33.33, 4.3, 89, 300, 30, false, true, true, 'active', '["无线", "降噪", "音乐"]', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop'),
(3, 2, '轻薄笔记本电脑', '高性能轻薄本，适合办公和娱乐', 3, 'LaptopPro', 5999.00, 6999.00, 14.29, 4.7, 256, 150, 15, true, false, true, 'active', '["轻薄", "高性能", "办公"]', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop'),
(4, 2, '智能手机旗舰版', '最新旗舰智能手机，拍照功能强大', 2, 'PhoneMax', 4999.00, 5999.00, 16.67, 4.6, 189, 800, 80, true, true, true, 'active', '["旗舰", "拍照", "5G"]', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop'),
(5, 3, '男士休闲衬衫', '纯棉男士衬衫，舒适透气', 6, 'FashionStyle', 199.00, 299.00, 33.33, 4.2, 45, 200, 20, false, false, false, 'active', '["纯棉", "休闲", "商务"]', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop');

-- 插入商品详情数据
INSERT INTO product_details (product_id, specifications, features, usage_instructions, precautions, package_contents, warranty_info, shipping_info) VALUES
(1, '[
  {"name": "屏幕尺寸", "value": "1.3英寸", "unit": "英寸"},
  {"name": "电池容量", "value": "200", "unit": "mAh"},
  {"name": "防水等级", "value": "IP68", "unit": "级"}
]', '["心率监测", "运动追踪", "睡眠监测", "消息提醒"]', '["首次使用请充满电", "下载配套APP使用"]', '["避免剧烈碰撞", "请勿在高温环境下使用"]', '["智能手表主机", "充电线", "说明书"]', '{"period": "12个月", "scope": "非人为损坏"}', '{"deliveryTime": "24小时内", "freeShipping": true}'),
(2, '[
  {"name": "蓝牙版本", "value": "5.2", "unit": ""},
  {"name": "续航时间", "value": "24", "unit": "小时"},
  {"name": "充电时间", "value": "1.5", "unit": "小时"}
]', '["主动降噪", "环境音模式", "快速充电", "触控操作"]', '["首次使用请配对设备", "充电时请使用原装充电器"]', '["避免接触液体", "请勿在潮湿环境下使用"]', '["耳机主体", "充电盒", "充电线", "说明书"]', '{"period": "12个月", "scope": "非人为损坏"}', '{"deliveryTime": "48小时内", "freeShipping": true}');

-- 插入商品图片数据
INSERT INTO product_images (product_id, image_type, image_url, alt_text, sort_order, is_primary) VALUES
(1, 'main', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&h=600&fit=crop', '智能手表主图', 0, true),
(1, 'multi_angle', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop&crop=left', '智能手表左侧视图', 1, false),
(1, 'multi_angle', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop&crop=right', '智能手表右侧视图', 2, false),
(1, 'details', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&h=800&fit=crop&crop=center', '智能手表细节图', 3, false),
(2, 'main', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=600&fit=crop', '无线耳机主图', 0, true),
(2, 'multi_angle', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop&crop=top', '无线耳机顶部视图', 1, false);

-- 插入商品规格数据
INSERT INTO product_specs (product_id, spec_name, spec_label, spec_value, spec_unit, spec_group, sort_order) VALUES
(1, '屏幕尺寸', '屏幕尺寸', '1.3', '英寸', '显示', 1),
(1, '分辨率', '分辨率', '360x360', '像素', '显示', 2),
(1, '电池容量', '电池容量', '200', 'mAh', '电池', 3),
(1, '防水等级', '防水等级', 'IP68', '级', '防护', 4),
(2, '蓝牙版本', '蓝牙版本', '5.2', '', '连接', 1),
(2, '续航时间', '续航时间', '24', '小时', '电池', 2),
(2, '充电时间', '充电时间', '1.5', '小时', '电池', 3);

COMMIT;