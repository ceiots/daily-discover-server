-- 创建商品相关表结构

USE daily_discover;

-- 商品基础信息表
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    description TEXT COMMENT '商品描述',
    image_url VARCHAR(500) COMMENT '商品主图URL',
    category VARCHAR(50) NOT NULL COMMENT '商品分类',
    price DECIMAL(10,2) NOT NULL COMMENT '当前价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    discount DECIMAL(5,2) COMMENT '折扣百分比',
    rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '评分',
    review_count INT DEFAULT 0 COMMENT '评价数量',
    sales INT DEFAULT 0 COMMENT '销量',
    is_new BOOLEAN DEFAULT false COMMENT '是否新品',
    tags JSON COMMENT '商品标签',
    brand VARCHAR(100) COMMENT '品牌',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_brand (brand),
    INDEX idx_price (price),
    INDEX idx_rating (rating),
    INDEX idx_sales (sales),
    INDEX idx_created_at (created_at)
) COMMENT '商品基础信息表';

-- 商品详情表
CREATE TABLE product_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '详情ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    specifications JSON COMMENT '规格参数',
    features JSON COMMENT '商品特性',
    usage_instructions JSON COMMENT '使用说明',
    precautions JSON COMMENT '注意事项',
    package_contents JSON COMMENT '包装清单',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_product_id (product_id)
) COMMENT '商品详情表';

-- 商品图片表
CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图片ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    image_type ENUM('main', 'multi_angle', 'details', 'scenes') NOT NULL COMMENT '图片类型',
    image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_image_type (image_type),
    INDEX idx_sort_order (sort_order)
) COMMENT '商品图片表';

-- 商品规格表
CREATE TABLE product_specs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规格ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    spec_name VARCHAR(100) NOT NULL COMMENT '规格名称',
    spec_label VARCHAR(100) COMMENT '规格标签',
    spec_value VARCHAR(200) COMMENT '规格值',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_spec_name (spec_name)
) COMMENT '商品规格表';

-- 商品SKU表
CREATE TABLE product_skus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'SKU ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sku_code VARCHAR(50) NOT NULL COMMENT 'SKU编码',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    stock INT DEFAULT 0 COMMENT '库存',
    spec_combination JSON COMMENT '规格组合',
    is_default BOOLEAN DEFAULT false COMMENT '是否默认SKU',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sku_code (sku_code),
    INDEX idx_product_id (product_id),
    INDEX idx_price (price),
    INDEX idx_stock (stock)
) COMMENT '商品SKU表';

COMMIT;