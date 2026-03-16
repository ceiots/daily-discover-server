-- ============================================
-- 商品核心信息表结构（简化版）
-- 业务模块: 商品核心信息
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_sku_spec_options;
DROP TABLE IF EXISTS product_sku_specs;
DROP TABLE IF EXISTS product_skus;
DROP TABLE IF EXISTS product_details;
DROP TABLE IF EXISTS product_categories;
DROP TABLE IF EXISTS products;

-- 商品基础信息表（SPU - 标准化产品单元）
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    seller_id BIGINT NOT NULL COMMENT '商家ID',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    
    -- 品牌和型号信息（便于查询和展示）
    brand VARCHAR(100) COMMENT '品牌名称',
    model VARCHAR(100) COMMENT '型号信息',
    
    -- 商品专属推荐语（基于情绪+场景+利益）
    goods_slogan VARCHAR(255) DEFAULT '' COMMENT '商品专属推荐语',
    
    -- 价格范围（冗余字段，由SKU价格同步更新）
    min_price DECIMAL(10,2) COMMENT '最低价格（SKU价格最小值）',
    max_price DECIMAL(10,2) COMMENT '最高价格（SKU价格最大值）',
    
    -- 商品状态（SPU层面状态）
    status TINYINT DEFAULT 1 COMMENT '状态：0-已下架 1-销售中',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-正常 1-删除',
    
    -- 主图（冗余存储，用于快速展示）
    main_image_url VARCHAR(500) COMMENT '商品主图URL',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 核心索引（优化复合索引）
    INDEX idx_seller_id (seller_id),
    INDEX idx_category_status_price (category_id, status, min_price) COMMENT '分类状态价格查询',
    INDEX idx_created_at (created_at) COMMENT '新品推荐',
    INDEX idx_brand_model (brand, model) COMMENT '品牌型号查询',
    INDEX idx_status_is_deleted (status, is_deleted) COMMENT '状态和删除状态复合索引',
    
    -- 软删除索引
    INDEX idx_is_deleted (is_deleted) COMMENT '软删除状态查询'
) COMMENT '商品基础信息表（SPU - 标准化产品单元）';

-- 商品分类表（优化树形结构）
CREATE TABLE IF NOT EXISTS product_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父分类ID',
    
    -- 路径优化（支持快速查询子分类）
    path VARCHAR(255) COMMENT '分类路径（如：1/5/23）',
    
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    image_url VARCHAR(500) COMMENT '分类图片',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    level INT DEFAULT 1 COMMENT '分类层级',
    
    -- 状态管理
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-正常 1-删除',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 优化索引
    INDEX idx_parent_id (parent_id),
    INDEX idx_name (name),
    INDEX idx_path (path) COMMENT '路径查询',
    INDEX idx_level (level),
    INDEX idx_sort_order (sort_order),
    INDEX idx_parent_status (parent_id, status) COMMENT '父分类状态查询',
    
    -- 路径查询优化索引（前缀索引，提高查询性能）
    INDEX idx_path_prefix (path(20)) COMMENT '路径前缀查询',
    INDEX idx_level_status (level, status) COMMENT '层级状态查询',
    INDEX idx_path_status (path(20), status) COMMENT '路径状态查询',
    
    -- 软删除索引
    INDEX idx_is_deleted (is_deleted) COMMENT '软删除状态查询'
) COMMENT '商品分类表（优化树形结构）';

-- ============================================
-- 2. 商品媒体资源模块（图片/视频统一管理）
-- ============================================

-- 商品详情表（电商简化版）
CREATE TABLE IF NOT EXISTS product_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '详情ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    
    -- 媒体类型（电商标准：1-轮播图 2-详情图）
    media_type TINYINT NOT NULL COMMENT '媒体类型：1-轮播图（商品展示图片） 2-详情图（详情页内容图片）',
    
    -- 媒体内容
    media_url VARCHAR(500) NOT NULL COMMENT '媒体URL',
    is_video TINYINT DEFAULT 0 COMMENT '是否为视频：0-图片 1-视频',
    
    -- 缩略图（用于列表页、规格选择弹窗等性能优化场景）
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_media_type (media_type),
    INDEX idx_sort_order (sort_order),
    INDEX idx_media_type_sort (media_type, sort_order) COMMENT '支持按类型和顺序查询'
) COMMENT '商品详情表（电商简化版）';

-- ============================================
-- 3. SKU核心模块（电商核心）
-- ============================================

-- SKU表（电商核心 - 可销售最小单位）
CREATE TABLE IF NOT EXISTS product_skus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'SKU ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    seller_id BIGINT NOT NULL COMMENT '商家ID（冗余字段，便于查询）',
    
    -- 价格信息（SKU级别的价格）
    price DECIMAL(10,2) NOT NULL COMMENT '销售价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 核心索引
    INDEX idx_product_id (product_id),
    INDEX idx_seller_id (seller_id)
) COMMENT 'SKU表（电商核心 - 可销售最小单位）';




-- 商品规格定义表（购买选择型）- 明确是SKU规格
CREATE TABLE IF NOT EXISTS product_sku_specs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格定义ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    spec_name VARCHAR(100) NOT NULL COMMENT '规格名称',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    
    -- 是否必选
    is_required TINYINT DEFAULT 1 COMMENT '是否必选：0-可选 1-必选',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_spec_name (spec_name)
) COMMENT '商品规格定义表（购买选择型规格）';

-- 商品规格选项表（规格具体值）
CREATE TABLE IF NOT EXISTS product_sku_spec_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格选项ID',
    spec_id BIGINT NOT NULL COMMENT '规格定义ID（引用product_sku_specs.id）',
    option_value VARCHAR(100) NOT NULL COMMENT '选项值',
    option_image VARCHAR(500) COMMENT '选项图片',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_spec_id (spec_id),
    INDEX idx_option_value (option_value),
    
    -- 唯一性约束（防止同一规格下出现重复选项）
    UNIQUE KEY uk_spec_option (spec_id, option_value) COMMENT '规格选项唯一性约束'
) COMMENT '商品规格选项表（规格具体值）';















-- ============================================
-- 初始数据
-- ============================================

-- 插入商品分类数据
INSERT INTO product_categories (id, parent_id, name, image_url, sort_order, level, status) VALUES
(1, NULL, '电子产品', 'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=300&h=200&fit=crop', 1, 1, 1),
(2, 1, '手机通讯', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300&h=200&fit=crop', 1, 2, 1),
(3, 1, '电脑办公', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=300&h=200&fit=crop', 2, 2, 1),
(4, 1, '智能穿戴', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&h=200&fit=crop', 3, 2, 1),
(5, NULL, '服饰鞋包', 'https://images.unsplash.com/photo-1445205170230-053b83016050?w=300&h=200&fit=crop', 2, 1, 1),
(6, 5, '男装', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=300&h=200&fit=crop', 1, 2, 1),
(7, 5, '女装', 'https://images.unsplash.com/photo-1499952127939-9bbf5af6c51c?w=300&h=200&fit=crop', 2, 2, 1);

-- 插入商品基础信息数据
INSERT INTO products (seller_id, title, brand, model, category_id, goods_slogan, min_price, max_price, status, main_image_url) VALUES
(1, '智能手表 Pro', 'Apple', 'Watch Series 8', 4, '全天候健康监测，运动数据实时记录', 299.00, 399.00, 1, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop'),
(1, '无线降噪耳机', 'Sony', 'WH-1000XM5', 4, '通勤路上隔绝嘈杂，专注享受音乐时光，让每一天都充满能量', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop'),
(2, '轻薄笔记本电脑', 'Apple', 'MacBook Air', 3, '轻薄便携高效办公，随时随地创作无限可能，让工作更自由', 5999.00, 6999.00, 1, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop'),
(2, '旗舰手机', 'Apple', 'iPhone 15', 2, '超强性能流畅体验，拍照摄影专业水准，记录生活每一刻精彩，智能AI助手贴心服务，超长续航全天无忧，5G网络极速连接，让工作生活更高效便捷', 4999.00, 5999.00, 1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop'),
(3, '运动蓝牙耳机', 'Bose', 'QuietComfort Earbuds II', 4, '运动健身专属伴侣，防水防汗稳固佩戴，让运动更尽兴', 249.00, 349.00, 1, 'https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=400&h=400&fit=crop');

-- 插入商品详情数据（简化版）
INSERT INTO product_details (product_id, media_type, media_url, is_video, thumbnail_url, sort_order) VALUES
-- 智能手机旗舰版（产品ID=4）
-- 主图和轮播图
(4, 1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=600&fit=crop', false, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300&h=300&fit=crop', 1),
(4, 1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=800&fit=crop&crop=center', false, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', 2),
(4, 1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop&crop=top', false, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=200&h=200&fit=crop', 3),

-- 卖点展示
(4, 2, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=1200&fit=crop', false, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=600&fit=crop', 4),
(4, 2, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=1200&fit=crop', false, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=600&fit=crop', 5),

-- 参数规格
(4, 2, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=1200&fit=crop', false, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=600&fit=crop', 6),

-- 售后服务
(4, 2, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&h=1200&fit=crop', false, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=600&fit=crop', 7),

-- 智能手表 Pro（产品ID=1）
(1, 1, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop', false, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=200&h=200&fit=crop', 1),
(1, 1, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&h=600&fit=crop', false, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&h=300&fit=crop', 2),

-- 无线降噪耳机（产品ID=2）
(2, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', false, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200&h=200&fit=crop', 1),
(2, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=600&fit=crop', false, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&h=300&fit=crop', 2),
-- 运动蓝牙耳机（产品ID=5）
(5, 1, 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400&h=400&fit=crop', false, 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=200&h=200&fit=crop', 1),
(5, 1, 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=600&h=600&fit=crop', false, 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=300&h=300&fit=crop', 2),

-- 轻薄笔记本电脑（产品ID=3）
(3, 1, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', false, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=200&h=200&fit=crop', 1),
(3, 1, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&h=600&fit=crop', false, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=300&h=300&fit=crop', 2);

-- 插入SKU规格数据（购买选择型）- 以智能手机为例
INSERT INTO product_sku_specs (product_id, spec_name, sort_order, is_required) VALUES
(4, '颜色', 1, true),
(4, '存储容量', 2, true),
(4, '网络版本', 3, false);

-- 插入SKU规格选项
INSERT INTO product_sku_spec_options (spec_id, option_value, option_image, sort_order) VALUES
(1, '黑色', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&h=100&fit=crop&crop=center', 1),
(1, '白色', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&h=100&fit=crop&crop=center&brightness=1.2', 2),
(1, '蓝色', 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=100&h=100&fit=crop&crop=center&hue=240', 3),
(2, '128GB', NULL, 1),
(2, '256GB', NULL, 2),
(2, '512GB', NULL, 3),
(3, '4G版', NULL, 1),
(3, '5G版', NULL, 2);

-- 插入SKU数据（符合编码规则）
INSERT INTO product_skus (product_id, seller_id, price, original_price) VALUES
-- 智能手机旗舰版（产品ID=4）的SKU
(4, 2, 4999.00, 5999.00),
(4, 2, 4999.00, 5999.00),
(4, 2, 4999.00, 5999.00),
(4, 2, 5499.00, 6499.00),
(4, 2, 5499.00, 6499.00),
(4, 2, 5299.00, 6299.00),
(4, 2, 5799.00, 6799.00),

-- 智能手表 Pro（产品ID=1）的SKU
(1, 1, 299.00, 399.00),
(1, 1, 299.00, 399.00),

-- 无线降噪耳机（产品ID=2）的SKU
(2, 1, 199.00, 299.00),
(2, 1, 199.00, 299.00),

-- 轻薄笔记本电脑（产品ID=3）的SKU
(3, 2, 5999.00, 6999.00),
(3, 2, 6999.00, 7999.00),
(3, 2, 5999.00, 6999.00),
-- 运动蓝牙耳机（产品ID=5）的SKU
(5, 3, 249.00, 349.00),
(5, 3, 299.00, 399.00);




-- ============================================
-- 商品卖点标签系统初始数据
-- ============================================



-- ============================================
-- 为user_id 4添加更多商品数据
-- ============================================

-- 插入新商品数据（扩展产品线）
INSERT INTO products (id, seller_id, title, category_id, brand, model, goods_slogan, min_price, max_price, status, main_image_url, created_at, updated_at) VALUES
-- 补充缺失的商品ID 13,14,15
(13, 1, '茶具套装', 22, '景德镇', '青花瓷茶具', '下午茶时光的优雅伴侣，让聚会更有格调', 299.00, 499.00, 1, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop', '2026-03-01 09:00:00', '2026-03-01 09:00:00'),
(14, 2, '点心盘', 23, '宜家', '陶瓷点心盘', '与好友分享甜点的精致容器', 159.00, 259.00, 1, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=400&fit=crop', '2026-03-01 09:30:00', '2026-03-01 09:30:00'),
(15, 3, '蓝牙音箱', 24, 'JBL', '便携蓝牙音箱', '聊天时播放轻音乐，音质纯净出色', 399.00, 599.00, 1, 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=400&h=400&fit=crop', '2026-03-01 10:00:00', '2026-03-01 10:00:00'),
-- 继续现有商品ID序列
(16, 1, '智能家居摄像头', 8, '小米', '智能摄像头Pro', '24小时守护，智能看家更安心', 299.00, 399.00, 1, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop', '2026-03-01 10:00:00', '2026-03-01 10:00:00'),
(17, 2, '便携式投影仪', 9, '极米', 'Play特别版', '随时随地，打造私人影院', 1299.00, 1599.00, 1, 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=400&h=400&fit=crop', '2026-03-02 11:00:00', '2026-03-02 11:00:00'),
(18, 3, '无线机械键盘', 10, '罗技', 'MX Mechanical', '办公游戏两相宜，打字更舒适', 699.00, 899.00, 1, 'https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400&h=400&fit=crop', '2026-03-03 12:00:00', '2026-03-03 12:00:00'),
(19, 4, '智能体脂秤', 11, '华为', '智能体脂秤2', '专业体脂监测，健康管理好帮手', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop', '2026-03-04 13:00:00', '2026-03-04 13:00:00'),
(20, 5, '降噪睡眠耳机', 12, 'Bose', 'Sleepbuds II', '专为睡眠设计，告别噪音困扰', 1499.00, 1799.00, 1, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop', '2026-03-05 14:00:00', '2026-03-05 14:00:00'),

-- 更多商品扩展
(21, 1, '多功能料理机', 13, '美的', '破壁料理机', '一机多用，厨房全能助手', 599.00, 799.00, 1, 'https://images.unsplash.com/photo-1556909114-4d0d853e5b0c?w=400&h=400&fit=crop', '2026-03-06 15:00:00', '2026-03-06 15:00:00'),
(22, 2, '便携咖啡机', 14, 'Nespresso', '便携咖啡机', '随时随地，享受现磨咖啡', 899.00, 1099.00, 1, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&h=400&fit=crop', '2026-03-07 16:00:00', '2026-03-07 16:00:00'),
(23, 3, '智能跳绳', 15, 'Keep', '智能计数跳绳', '科学计数，健身更高效', 129.00, 199.00, 1, 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&h=400&fit=crop', '2026-03-08 17:00:00', '2026-03-08 17:00:00'),
(24, 4, '电动牙刷', 16, '飞利浦', '声波电动牙刷', '深度清洁，呵护牙齿健康', 399.00, 599.00, 1, 'https://images.unsplash.com/photo-1584305574647-0d5c6c4c8a6b?w=400&h=400&fit=crop', '2026-03-09 18:00:00', '2026-03-09 18:00:00'),
(25, 5, '便携充电宝', 17, 'Anker', '20000mAh快充', '大容量快充，出行无忧', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1585155770447-2f66e2a397b5?w=400&h=400&fit=crop', '2026-03-10 19:00:00', '2026-03-10 19:00:00'),

-- ==================== 场景推荐相关商品（ID: 26-43） ====================
-- 2026-03-12 早晨场景商品
(26, 1, '香薰机', 18, '无印良品', '超声波香薰机', '清晨起床仪式感，雾化细腻香气持久', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1606813907291-d86efa9b94db?w=400&h=400&fit=crop', '2026-03-11 09:00:00', '2026-03-11 09:00:00'),
(27, 2, '舒适睡衣', 19, '优衣库', 'AIRism睡衣', '温柔早晨的柔软陪伴，材质亲肤透气', 149.00, 249.00, 1, 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=400&fit=crop', '2026-03-11 10:00:00', '2026-03-11 10:00:00'),
(28, 3, '保温杯', 20, '膳魔师', 'JNL-500保温杯', '起床后补水必备，保温效果持久', 129.00, 199.00, 1, 'https://images.unsplash.com/photo-1572099606223-6e29045d294d?w=400&h=400&fit=crop', '2026-03-11 11:00:00', '2026-03-11 11:00:00'),

-- 2026-03-12 午后场景商品
(29, 4, '遮光眼罩', 21, 'Slip', '真丝遮光眼罩', '午后小憩好帮手，遮光效果极佳', 89.00, 159.00, 1, 'https://images.unsplash.com/photo-1584305574647-0d5c6c4c8a6b?w=400&h=400&fit=crop', '2026-03-11 12:00:00', '2026-03-11 12:00:00'),
(30, 5, '蓝牙耳机', 22, 'Sony', 'WF-1000XM4', '放松时听轻音乐，音质纯净细腻', 899.00, 1299.00, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', '2026-03-11 13:00:00', '2026-03-11 13:00:00'),
(31, 1, '茶叶礼盒', 23, '八马茶业', '铁观音礼盒', '小憩后提神醒脑，香气清新怡人', 199.00, 399.00, 1, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=400&fit=crop', '2026-03-11 14:00:00', '2026-03-11 14:00:00'),

-- 2026-03-12 晚上场景商品
(32, 2, '笔记本', 24, 'Moleskine', '经典笔记本', '晚间规划好伙伴，纸张质感上乘', 129.00, 229.00, 1, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=400&fit=crop', '2026-03-11 15:00:00', '2026-03-11 15:00:00'),
(33, 3, '护眼台灯', 25, '小米', '智能护眼台灯', '规划时点一盏灯，光线柔和舒适', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400&h=400&fit=crop', '2026-03-11 16:00:00', '2026-03-11 16:00:00'),
(34, 4, '保鲜盒', 26, '乐扣乐扣', '玻璃保鲜盒', '为明天准备早餐，密封保鲜效果好', 69.00, 129.00, 1, 'https://images.unsplash.com/photo-1572099606223-6e29045d294d?w=400&h=400&fit=crop', '2026-03-11 17:00:00', '2026-03-11 17:00:00'),

-- 2026-03-13 早晨场景商品
(35, 5, '茶具套装', 27, '景德镇', '青花瓷茶具', '早晨泡茶仪式感，设计简约优雅', 299.00, 499.00, 1, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop', '2026-03-12 09:00:00', '2026-03-12 09:00:00'),
(36, 1, '茶叶礼盒', 28, '西湖龙井', '明前龙井', '茶香需要好茶叶，口感醇厚回甘', 399.00, 699.00, 1, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400&h=400&fit=crop', '2026-03-12 10:00:00', '2026-03-12 10:00:00'),
(37, 2, '点心盘', 29, 'Royal Doulton', '骨瓷点心盘', '喝茶时配点心，精致实用美观', 159.00, 259.00, 1, 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=400&h=400&fit=crop', '2026-03-12 11:00:00', '2026-03-12 11:00:00'),

-- 2026-03-13 午后场景商品
(38, 3, '文件夹', 30, '得力', '分类文件夹', '整理工作收获，分类清晰便捷', 29.00, 59.00, 1, 'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=400&h=400&fit=crop', '2026-03-12 12:00:00', '2026-03-12 12:00:00'),
(39, 4, '笔记本', 31, '国誉', 'Campus笔记本', '记录成长点滴，书写流畅顺滑', 19.00, 39.00, 1, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop', '2026-03-12 13:00:00', '2026-03-12 13:00:00'),
(40, 5, '咖啡机', 32, '德龙', '全自动咖啡机', '总结时喝杯咖啡，操作简单便捷', 1299.00, 1899.00, 1, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&h=400&fit=crop', '2026-03-12 14:00:00', '2026-03-12 14:00:00'),

-- 2026-03-13 晚上场景商品
(41, 1, '日历', 33, 'Moleskine', '2026年日历', '周末规划好帮手，设计美观实用', 99.00, 169.00, 1, 'https://images.unsplash.com/photo-1506784983877-45594efa4cbe?w=400&h=400&fit=crop', '2026-03-12 15:00:00', '2026-03-12 15:00:00'),
(42, 2, '便签', 34, '3M', 'Post-it便签', '规划活动记录，粘贴牢固易撕', 15.00, 29.00, 1, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop', '2026-03-12 16:00:00', '2026-03-12 16:00:00'),
(43, 3, '保鲜盒', 35, 'Tupperware', '塑料保鲜盒', '为周末准备食材，密封性好耐用', 49.00, 89.00, 1, 'https://images.unsplash.com/photo-1572099606223-6e29045d294d?w=400&h=400&fit=crop', '2026-03-12 17:00:00', '2026-03-12 17:00:00');




COMMIT;