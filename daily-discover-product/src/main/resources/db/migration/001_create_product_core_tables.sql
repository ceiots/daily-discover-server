-- ============================================
-- 商品核心信息表结构（简化版）
-- 业务模块: 商品核心信息
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_tag_relations;
DROP TABLE IF EXISTS product_tags;
DROP TABLE IF EXISTS product_service_info_values;
DROP TABLE IF EXISTS product_service_categories;
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
-- 7. 产品服务信息模块（可扩展设计）
-- ============================================

-- 产品服务信息分类表（定义信息类型）
CREATE TABLE IF NOT EXISTS product_service_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    
    -- 分类基本信息
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称（如：产品参数、售后服务、认证信息）',
    category_code VARCHAR(50) NOT NULL COMMENT '分类代码（英文标识，如：specs, service, certification）',
    
    -- 分类属性
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_collapsible BOOLEAN DEFAULT true COMMENT '是否可折叠',
    
    -- 显示配置
    display_icon VARCHAR(100) COMMENT '显示图标',
    display_color VARCHAR(20) COMMENT '显示颜色',
    
    -- 状态管理
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    UNIQUE KEY uk_category_code (category_code),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status)
) COMMENT '产品服务信息分类表';

-- 产品服务信息值表（合并信息项和值存储）
CREATE TABLE IF NOT EXISTS product_service_info_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '值ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    
    -- 信息项定义
    info_key VARCHAR(100) NOT NULL COMMENT '信息项键（英文标识，如：weight, warranty_period）',
    info_label VARCHAR(100) NOT NULL COMMENT '信息项标签（显示名称，如：产品重量、质保期限）',
    
    -- 数据类型配置
    data_type VARCHAR(20) DEFAULT 'text' COMMENT '数据类型：text-文本, number-数字, boolean-布尔, date-日期',
    value_unit VARCHAR(20) COMMENT '数值单位（如：kg, month, year）',
    
    -- 值存储（支持多值）
    string_value VARCHAR(500) COMMENT '字符串值',
    number_value DECIMAL(15,4) COMMENT '数值',
    boolean_value BOOLEAN COMMENT '布尔值',
    date_value DATE COMMENT '日期值',
    
    -- 显示配置
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    
    -- 状态管理
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_category_id (category_id),
    INDEX idx_info_key (info_key),
    INDEX idx_product_category (product_id, category_id),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status),
    
    -- 唯一约束（一个商品一个分类一个信息项只能有一条记录）
    UNIQUE KEY uk_product_category_info (product_id, category_id, info_key)
) COMMENT '产品服务信息值表（合并信息项和值存储）';

-- ============================================
-- 产品服务信息初始数据
-- ============================================

-- 插入产品服务信息分类数据
INSERT INTO product_service_categories (category_name, category_code, sort_order, is_collapsible, display_icon, display_color) VALUES
('产品参数', 'specs', 1, true, '📦', '#3498db'),
('售后服务', 'service', 2, true, '🛡️', '#e74c3c'),
('认证信息', 'certification', 3, true, '✅', '#27ae60'),
('包装清单', 'package', 4, true, '📋', '#f39c12'),
('使用说明', 'instructions', 5, true, '📖', '#9b59b6');

-- 为现有商品插入产品服务信息值（示例数据）
INSERT INTO product_service_info_values (product_id, category_id, info_key, info_label, data_type, value_unit, string_value, number_value, sort_order) VALUES
-- 智能手表 Pro（产品ID=1）
(1, 1, 'origin', '产品产地', 'text', NULL, '中国', NULL, 1),
(1, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.05, 2),
(1, 1, 'dimensions', '产品尺寸', 'text', NULL, '4.2×3.6×1.2cm', NULL, 3),
(1, 1, 'material', '产品材质', 'text', NULL, '不锈钢表壳，蓝宝石玻璃', NULL, 4),
(1, 1, 'shelf_life', '保质期', 'number', 'month', NULL, 24, 5),
(1, 2, 'return_policy', '退换货政策', 'text', NULL, '7天无理由退换货，1年质保', NULL, 1),
(1, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 12, 2),
(1, 2, 'customer_service_hotline', '客服热线', 'text', NULL, '400-123-4567', NULL, 3),
(1, 4, 'package_contents', '包装清单', 'text', NULL, 'Apple Watch Series 8，充电线，说明书', NULL, 1),
(1, 5, 'usage_method', '使用方法', 'text', NULL, '长按侧边按钮开机，连接手机App设置', NULL, 1),

-- 无线降噪耳机（产品ID=2）
(2, 1, 'origin', '产品产地', 'text', NULL, '日本', NULL, 1),
(2, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.25, 2),
(2, 1, 'material', '产品材质', 'text', NULL, '塑料外壳，蛋白皮耳罩', NULL, 3),
(2, 1, 'shelf_life', '保质期', 'number', 'month', NULL, 36, 4),
(2, 2, 'return_policy', '退换货政策', 'text', NULL, '15天无理由退换货，2年质保', NULL, 1),
(2, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 24, 2),
(2, 4, 'package_contents', '包装清单', 'text', NULL, 'Sony WH-1000XM5，充电线，收纳盒，说明书', NULL, 1),

-- 轻薄笔记本电脑（产品ID=3）
(3, 1, 'origin', '产品产地', 'text', NULL, '美国', NULL, 1),
(3, 1, 'weight', '产品重量', 'number', 'kg', NULL, 1.29, 2),
(3, 1, 'dimensions', '产品尺寸', 'text', NULL, '30.4×21.5×1.6cm', NULL, 3),
(3, 1, 'material', '产品材质', 'text', NULL, '铝合金机身，Retina显示屏', NULL, 4),
(3, 2, 'return_policy', '退换货政策', 'text', NULL, '质量问题7天包退，15天包换，2年保修', NULL, 1),
(3, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 24, 2),

-- 智能手机旗舰版（产品ID=4）
(4, 1, 'origin', '产品产地', 'text', NULL, '中国', NULL, 1),
(4, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.17, 2),
(4, 1, 'dimensions', '产品尺寸', 'text', NULL, '14.7×7.1×0.78cm', NULL, 3),
(4, 1, 'material', '产品材质', 'text', NULL, '玻璃后盖，不锈钢边框', NULL, 4),
(4, 2, 'return_policy', '退换货政策', 'text', NULL, '7天无理由退换货，1年官方保修', NULL, 1),
(4, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 12, 2),
(4, 3, 'quality_certification', '质量认证', 'text', NULL, '3C认证，入网许可证', NULL, 1),
(4, 4, 'package_contents', '包装清单', 'text', NULL, 'iPhone 15，充电线，说明书，SIM卡针', NULL, 1),
-- 运动蓝牙耳机（产品ID=5）
(5, 1, 'origin', '产品产地', 'text', NULL, '美国', NULL, 1),
(5, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.08, 2),
(5, 1, 'dimensions', '产品尺寸', 'text', NULL, '2.5×2.5×1.2cm', NULL, 3),
(5, 1, 'material', '产品材质', 'text', NULL, '硅胶材质，防水设计', NULL, 4),
(5, 1, 'battery_life', '电池续航', 'number', 'hour', NULL, 6, 5),
(5, 2, 'return_policy', '退换货政策', 'text', NULL, '30天无理由退换货，2年质保', NULL, 1),
(5, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 24, 2),
(5, 4, 'package_contents', '包装清单', 'text', NULL, 'Bose QuietComfort Earbuds II，充电盒，USB-C充电线，说明书', NULL, 1),
(5, 5, 'usage_method', '使用方法', 'text', NULL, '打开充电盒自动连接，支持触控操作和语音助手', NULL, 1);


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
INSERT INTO products (seller_id, title, brand, model, category_id, min_price, max_price, status, main_image_url) VALUES
(1, '智能手表 Pro', 'Apple', 'Watch Series 8', 4, 299.00, 399.00, 1, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop'),
(1, '无线降噪耳机', 'Sony', 'WH-1000XM5', 4, 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop'),
(2, '轻薄笔记本电脑', 'Apple', 'MacBook Air', 3, 5999.00, 6999.00, 1, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop'),
(2, '智能手机旗舰版', 'Apple', 'iPhone 15', 2, 4999.00, 5999.00, 1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop'),
(3, '运动蓝牙耳机', 'Bose', 'QuietComfort Earbuds II', 4, 249.00, 349.00, 1, 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400&h=400&fit=crop');

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

-- 插入商品标签数据
INSERT INTO product_tags (tag_name, tag_type) VALUES
('智能', 'feature'),
('运动', 'feature'),
('健康', 'feature'),
('无线', 'feature'),
('降噪', 'feature'),
('轻薄', 'feature'),
('旗舰', 'style'),
('纯棉', 'feature');

-- 插入商品标签关联数据
INSERT INTO product_tag_relations (product_id, tag_id) VALUES
(1, 1),  -- 智能手表 - 智能
(1, 2),  -- 智能手表 - 运动
(1, 3),  -- 智能手表 - 健康
(2, 4),  -- 无线耳机 - 无线
(2, 5),  -- 无线耳机 - 降噪
(3, 6),  -- 笔记本电脑 - 轻薄
(4, 7),  -- 智能手机 - 旗舰
(5, 2),  -- 运动蓝牙耳机 - 运动
(5, 4),  -- 运动蓝牙耳机 - 无线
(5, 5);  -- 运动蓝牙耳机 - 降噪

-- 插入购物车数据
INSERT INTO shopping_cart (user_id, product_id, sku_id, quantity, specs_json, specs_text, is_selected) VALUES
(1001, 4, 1, 1, '{"颜色": "黑色", "存储": "128GB"}', '黑色 128GB', 1),
(1001, 2, 3, 2, '{"颜色": "黑色"}', '黑色', 1),
(1002, 4, 7, 1, '{"颜色": "蓝色", "存储": "256GB", "网络版本": "5G版"}', '蓝色 256GB 5G版', 1);

COMMIT;