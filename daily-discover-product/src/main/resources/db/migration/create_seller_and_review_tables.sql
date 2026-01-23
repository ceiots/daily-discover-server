-- 创建商家和评价相关表结构

USE daily_discover;

-- 商家信息表
CREATE TABLE sellers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商家ID',
    name VARCHAR(200) NOT NULL COMMENT '商家名称',
    rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '商家评分',
    response_time VARCHAR(50) COMMENT '响应时间',
    delivery_time VARCHAR(50) COMMENT '配送时间',
    followers INT DEFAULT 0 COMMENT '粉丝数量',
    positive_feedback DECIMAL(5,2) DEFAULT 0.0 COMMENT '好评率',
    description TEXT COMMENT '商家描述',
    contact_info JSON COMMENT '联系信息',
    services JSON COMMENT '服务项目',
    certifications JSON COMMENT '认证信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_name (name),
    INDEX idx_rating (rating),
    INDEX idx_followers (followers)
) COMMENT '商家信息表';

-- 用户评价表
CREATE TABLE user_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_name VARCHAR(100) NOT NULL COMMENT '用户名',
    rating INT NOT NULL COMMENT '评分(1-5)',
    comment TEXT COMMENT '评价内容',
    review_date DATE NOT NULL COMMENT '评价日期',
    helpful_count INT DEFAULT 0 COMMENT '有用数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_rating (rating),
    INDEX idx_review_date (review_date),
    INDEX idx_helpful_count (helpful_count)
) COMMENT '用户评价表';

-- 评价统计表
CREATE TABLE review_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    total_reviews INT DEFAULT 0 COMMENT '总评价数',
    average_rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
    rating_distribution JSON COMMENT '评分分布',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_product_id (product_id),
    INDEX idx_average_rating (average_rating)
) COMMENT '评价统计表';

COMMIT;