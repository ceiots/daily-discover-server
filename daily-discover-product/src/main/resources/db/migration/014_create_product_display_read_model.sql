-- ============================================
-- 商品展示读模型宽表
-- 业务模块: 今日热点、限时机会等高频查询场景
-- 设计目的: 减少跨表JOIN查询，提升查询性能
-- 数据更新: 通过定时任务或事件驱动刷新
-- ============================================

USE daily_discover;

DROP TABLE IF EXISTS product_display_read_model;

CREATE TABLE IF NOT EXISTS product_display_read_model (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',

    -- 商品基本信息
    title VARCHAR(200) NOT NULL COMMENT '商品标题',
    main_image_url VARCHAR(500) COMMENT '商品主图URL',
    min_price DECIMAL(10,2) COMMENT '最低价格',
    goods_slogan VARCHAR(255) DEFAULT '' COMMENT '商品专属推荐语',
    recommendation_reason VARCHAR(500) DEFAULT '' COMMENT 'AI推荐理由（从真实评价提取）',
    category_id BIGINT COMMENT '分类ID',
    brand VARCHAR(100) COMMENT '品牌名称',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已下架 1-销售中',

    -- 热度数据（来自product_sales_stats）
    sales_count INT DEFAULT 0 COMMENT '销量',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    favorite_count INT DEFAULT 0 COMMENT '收藏量',
    sales_growth_rate DECIMAL(5,2) COMMENT '销量增长率',
    hot_score DECIMAL(10,4) DEFAULT 0 COMMENT '热度评分（综合计算）',

    -- 评价数据（来自review_stats）
    average_rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
    total_reviews INT DEFAULT 0 COMMENT '评价数量',
    positive_rate DECIMAL(5,2) DEFAULT 0.0 COMMENT '好评率（%）',

    -- 标签和标记
    is_trending TINYINT DEFAULT 0 COMMENT '是否今日热门：0-否 1-是',
    is_new_arrival TINYINT DEFAULT 0 COMMENT '是否新品首发：0-否 1-是',
    hot_tag VARCHAR(20) DEFAULT '' COMMENT '热度标签（如：今日热门、新品首发）',

    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引优化
    UNIQUE KEY uk_product_id (product_id) COMMENT '商品唯一索引',
    INDEX idx_hot_score (hot_score DESC) COMMENT '热度排序',
    INDEX idx_is_trending (is_trending, hot_score DESC) COMMENT '今日热门排序',
    INDEX idx_category_hot (category_id, hot_score DESC) COMMENT '分类热度排序',
    INDEX idx_status (status) COMMENT '状态查询'
) COMMENT '商品展示读模型宽表（今日热点等高频查询场景）';

