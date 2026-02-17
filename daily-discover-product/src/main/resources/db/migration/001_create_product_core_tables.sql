-- ============================================
-- 商品核心信息表结构（简化版）
-- 业务模块: 商品核心信息
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS shopping_cart;
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
    INDEX idx_product_code (product_code),
    
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



-- 购物车表（支持多规格购买）
CREATE TABLE IF NOT EXISTS shopping_cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_id BIGINT NOT NULL COMMENT 'SKU ID（对应具体规格）',
    
    -- 购买数量
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    
    -- 规格信息（记录用户选择的规格组合）
    specs_json JSON COMMENT '规格组合JSON：{"颜色": "黑色", "存储": "128GB"}',
    specs_text VARCHAR(500) COMMENT '规格文本：黑色 128GB',
    
    -- 购物车项状态
    is_selected TINYINT DEFAULT 1 COMMENT '是否选中：0-未选中 1-选中',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_sku_id (sku_id),
    INDEX idx_user_product (user_id, product_id),
    INDEX idx_is_selected (is_selected),
    
    -- 唯一约束：一个用户对同一个SKU只能有一条记录
    UNIQUE KEY uk_user_sku (user_id, sku_id)
) COMMENT '购物车表（支持多规格购买）';

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

-- 商品规格选项表（规格具体值）- 明确是SKU规格选项
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
    INDEX idx_option_value (option_value)
) COMMENT '商品规格选项表（规格具体值）';



COMMIT;

-- ============================================
-- 初始数据
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
INSERT INTO products (seller_id, title, brand, model, product_code, category_id, min_price, max_price, status, main_image_url) VALUES
(1, '智能手表 Pro', 'Apple', 'Watch Series 8', 'APPL-WATCHS8', 4, 299.00, 399.00, 1, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop'),
(1, '无线降噪耳机', 'Sony', 'WH-1000XM5', 'SONY-WH1000XM5', 4, 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop'),
(2, '轻薄笔记本电脑', 'Apple', 'MacBook Air', 'APPL-MBAIR', 3, 5999.00, 6999.00, 1, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop'),
(2, '智能手机旗舰版', 'Apple', 'iPhone 15', 'APPL-IPHONE15', 2, 4999.00, 5999.00, 1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop');

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
(3, 2, 5999.00, 6999.00);

-- 插入购物车数据
INSERT INTO shopping_cart (user_id, product_id, sku_id, quantity, specs_json, specs_text, is_selected) VALUES
(1001, 1, 1, 1, '{"颜色": "黑色", "存储": "128GB"}', '黑色 128GB', 1),
(1001, 2, 3, 2, '{"颜色": "黑色"}', '黑色', 1),
(1002, 4, 7, 1, '{"颜色": "蓝色", "存储": "256GB", "网络版本": "5G版"}', '蓝色 256GB 5G版', 1);

COMMIT;