-- ============================================
-- 商品核心信息表结构（简化版）
-- 业务模块: 商品核心信息
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_tag_relations;
DROP TABLE IF EXISTS product_selling_point_relations;
DROP TABLE IF EXISTS product_selling_points;
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
-- 8. 产品服务信息模块（可扩展设计）
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
INSERT INTO products (seller_id, title, brand, model, category_id, goods_slogan, min_price, max_price, status, main_image_url) VALUES
(1, '智能手表 Pro', 'Apple', 'Watch Series 8', 4, '全天候健康监测，运动数据实时记录', 299.00, 399.00, 1, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop'),
(1, '无线降噪耳机', 'Sony', 'WH-1000XM5', 4, '通勤路上隔绝嘈杂，专注享受音乐时光，让每一天都充满能量', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop'),
(2, '轻薄笔记本电脑', 'Apple', 'MacBook Air', 3, '轻薄便携高效办公，随时随地创作无限可能，让工作更自由', 5999.00, 6999.00, 1, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop'),
(2, '旗舰手机', 'Apple', 'iPhone 15', 2, '超强性能流畅体验，拍照摄影专业水准，记录生活每一刻精彩，智能AI助手贴心服务，超长续航全天无忧，5G网络极速连接，让工作生活更高效便捷', 4999.00, 5999.00, 1, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop'),
(3, '运动蓝牙耳机', 'Bose', 'QuietComfort Earbuds II', 4, '运动健身专属伴侣，防水防汗稳固佩戴，让运动更尽兴', 249.00, 349.00, 1, 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400&h=400&fit=crop');

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

-- ============================================
-- 商品卖点标签系统初始数据
-- ============================================

-- 插入商品卖点标签数据（高转化电商版本）
INSERT INTO product_selling_points (point_name, point_category, point_sub_category, point_description) VALUES
-- ====================== 安全性（统一大类）
('食品级材质', '安全性', '材质', '采用食品接触级安全材质，无毒无异味，母婴、老人日常使用都放心'),
('无任何添加', '安全性', '成分', '0防腐剂、0香精、0色素，天然纯净，吃进嘴里、接触皮肤都更安心'),
('0农残检测', '安全性', '检测', '经过专业机构检测，农残未检出，生鲜果蔬直接清洗即可食用'),
('银离子抗菌', '安全性', '抗菌', '内置银离子抗菌层，有效抑制日常细菌滋生，贴身佩戴更卫生'),
('无辐射安全', '安全性', '辐射', '低辐射合规设计，远低于国家安全标准，贴身佩戴、长期使用无负担'),

-- ====================== 性能感
('超轻便携', '性能感', '重量', '重量比手机还轻，随身放包、口袋无压力，出门携带完全不费劲'),
('柔软舒适', '性能感', '触感', '高弹柔肤材质，触感细腻不磨皮肤，长时间佩戴/使用也不闷不勒'),
('强效省电', '性能感', '能耗', '达到一级节能标准，待机更久、耗电更低，长期使用能省一大笔电费'),
('快速充电', '性能感', '充电', '充电10分钟就能用很久，碎片化时间快速回血，出门从不担心没电'),
('超长续航', '性能感', '续航', '一次充满，连续使用数天，告别频繁充电，出差旅行更省心'),

-- ====================== 体验感
('深度降噪', '体验感', '降噪', '有效隔绝外界嘈杂人声、交通噪音，办公、通勤、睡觉都能保持安静'),
('静音运行', '体验感', '噪音', '运行声音极轻，深夜使用不打扰家人，图书馆、卧室都能安心用'),
('一键操作', '体验感', '操作', '功能简单直观，一键开启/切换，不用看说明书，老人小孩都能轻松上手'),
('智能控制', '体验感', '智能', '支持手机APP远程控制，不用起身就能调节，懒人、老人使用超方便'),
('防滑不脱手', '体验感', '防滑', '表面防滑纹理设计，手上出汗、沾水也能牢牢握住，不易滑落摔落'),

-- ====================== 健康呵护
('护眼柔和', '健康呵护', '护眼', '屏幕无频闪、低蓝光，长时间看视频、办公、学习，眼睛不易酸胀疲劳'),
('亲肤透气', '健康呵护', '透气', '透气面料不闷汗，夏天佩戴不粘皮肤，长时间使用也清爽舒适'),
('环保材质', '健康呵护', '环保', '可降解环保材料，无毒无害，丢弃不污染环境，使用更有责任感'),

-- ====================== 耐用性
('耐高温', '耐用性', '耐温', '可承受高温不变形、不开裂、不释放有害物质，热水、高温环境都能用'),
('防摔耐造', '耐用性', '防摔', '加固结构设计，日常不小心跌落、碰撞不易损坏，结实抗造更耐用'),
('防水防尘', '耐用性', '防水', '日常泼溅、雨水、灰尘都不怕，户外、浴室、厨房都能安心使用');

-- ============================================
-- 为user_id 4添加更多商品数据
-- ============================================

-- 插入新商品数据（扩展产品线）
INSERT INTO products (id, seller_id, title, category_id, brand, model, goods_slogan, min_price, max_price, status, main_image_url, created_at, updated_at) VALUES
-- 继续现有商品ID序列
(16, 1, '智能家居摄像头', 8, '小米', '智能摄像头Pro', '24小时守护，智能看家更安心', 299.00, 399.00, 1, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop', '2026-03-01 10:00:00', '2026-03-01 10:00:00'),
(17, 2, '便携式投影仪', 9, '极米', 'Play特别版', '随时随地，打造私人影院', 1299.00, 1599.00, 1, 'https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=400&h=400&fit=crop', '2026-03-02 11:00:00', '2026-03-02 11:00:00'),
(18, 3, '无线机械键盘', 10, '罗技', 'MX Mechanical', '办公游戏两相宜，打字更舒适', 699.00, 899.00, 1, 'https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400&h=400&fit=crop', '2026-03-03 12:00:00', '2026-03-03 12:00:00'),
(19, 4, '智能体脂秤', 11, '华为', '智能体脂秤2', '专业体脂监测，健康管理好帮手', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop', '2026-03-04 13:00:00', '2026-03-04 13:00:00'),
(20, 5, '降噪睡眠耳机', 12, 'Bose', 'Sleepbuds II', '专为睡眠设计，告别噪音困扰', 1499.00, 1799.00, 1, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', '2026-03-05 14:00:00', '2026-03-05 14:00:00'),

-- 更多商品扩展
(21, 1, '多功能料理机', 13, '美的', '破壁料理机', '一机多用，厨房全能助手', 599.00, 799.00, 1, 'https://images.unsplash.com/photo-1556909114-4d0d853e5b0c?w=400&h=400&fit=crop', '2026-03-06 15:00:00', '2026-03-06 15:00:00'),
(22, 2, '便携咖啡机', 14, 'Nespresso', '便携咖啡机', '随时随地，享受现磨咖啡', 899.00, 1099.00, 1, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&h=400&fit=crop', '2026-03-07 16:00:00', '2026-03-07 16:00:00'),
(23, 3, '智能跳绳', 15, 'Keep', '智能计数跳绳', '科学计数，健身更高效', 129.00, 199.00, 1, 'https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&h=400&fit=crop', '2026-03-08 17:00:00', '2026-03-08 17:00:00'),
(24, 4, '电动牙刷', 16, '飞利浦', '声波电动牙刷', '深度清洁，呵护牙齿健康', 399.00, 599.00, 1, 'https://images.unsplash.com/photo-1584305574647-0d5c6c4c8a6b?w=400&h=400&fit=crop', '2026-03-09 18:00:00', '2026-03-09 18:00:00'),
(25, 5, '便携充电宝', 17, 'Anker', '20000mAh快充', '大容量快充，出行无忧', 199.00, 299.00, 1, 'https://images.unsplash.com/photo-1585155770447-2f66e2a397b5?w=400&h=400&fit=crop', '2026-03-10 19:00:00', '2026-03-10 19:00:00');

-- 为user_id 4添加购物车数据（使用不同的sku_id避免重复）
INSERT INTO shopping_cart (user_id, product_id, sku_id, quantity, specs_json, specs_text, is_selected) VALUES
(4, 16, 3, 1, '{"颜色": "白色", "分辨率": "1080P"}', '白色 1080P', 1),
(4, 17, 4, 1, '{"颜色": "黑色", "亮度": "500流明"}', '黑色 500流明', 1),
(4, 18, 5, 1, '{"颜色": "灰色", "轴体": "茶轴"}', '灰色 茶轴', 1),
(4, 19, 6, 1, '{"颜色": "白色", "精度": "0.1kg"}', '白色 0.1kg精度', 1),
(4, 20, 7, 1, '{"颜色": "黑色", "续航": "10小时"}', '黑色 10小时续航', 1),
(4, 21, 8, 1, '{"颜色": "银色", "功率": "1000W"}', '银色 1000W', 1),
(4, 22, 9, 1, '{"颜色": "红色", "水箱容量": "0.5L"}', '红色 0.5L水箱', 1),
(4, 23, 10, 1, '{"颜色": "蓝色", "计数方式": "智能计数"}', '蓝色 智能计数', 1),
(4, 24, 11, 1, '{"颜色": "粉色", "模式": "3种清洁模式"}', '粉色 3种清洁模式', 1),
(4, 25, 12, 1, '{"颜色": "黑色", "接口": "双USB-C"}', '黑色 双USB-C', 1);

-- 插入商品卖点关系数据（重新调整以匹配新卖点ID）
INSERT INTO product_selling_point_relations (product_id, selling_point_id) VALUES
-- 智能手表 Pro（产品ID=1）
(1, 6),  -- 超轻便携（性能感-重量）
(1, 7),  -- 柔软舒适（性能感-触感）
(1, 10), -- 超长续航（性能感-续航）
(1, 5),  -- 无辐射安全（安全性-辐射）
(1, 21), -- 防水防尘（耐用性-防水）

-- 无线降噪耳机（产品ID=2）
(2, 6),  -- 超轻便携（性能感-重量）
(2, 10), -- 超长续航（性能感-续航）
(2, 12), -- 静音运行（体验感-噪音）
(2, 14), -- 智能控制（体验感-智能）
(2, 20), -- 防摔耐造（耐用性-防摔）

-- 轻薄笔记本电脑（产品ID=3）
(3, 6),  -- 超轻便携（性能感-重量）
(3, 8),  -- 强效省电（性能感-能耗）
(3, 9),  -- 快速充电（性能感-充电）
(3, 13), -- 一键操作（体验感-操作）
(3, 19), -- 耐高温（耐用性-耐温）

-- 智能手机旗舰版（产品ID=4）
(4, 6),  -- 超轻便携（性能感-重量）
(4, 9),  -- 快速充电（性能感-充电）
(4, 10), -- 超长续航（性能感-续航）
(4, 14), -- 智能控制（体验感-智能）
(4, 15), -- 防滑不脱手（体验感-防滑）
(4, 18), -- 环保材质（健康呵护-环保）

-- 运动蓝牙耳机（产品ID=5）
(5, 6),  -- 超轻便携（性能感-重量）
(5, 7),  -- 柔软舒适（性能感-触感）
(5, 10), -- 超长续航（性能感-续航）
(5, 12), -- 静音运行（体验感-噪音）
(5, 21); -- 防水防尘（耐用性-防水）

COMMIT;