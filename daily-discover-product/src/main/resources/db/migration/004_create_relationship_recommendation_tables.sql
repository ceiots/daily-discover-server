-- ============================================
-- 商品关系与推荐表结构
-- 创建时间: 2026-02-04
-- 业务模块: 商品关系与推荐
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_tag_relations;
DROP TABLE IF EXISTS product_tags;
DROP TABLE IF EXISTS product_search_keywords;
DROP TABLE IF EXISTS product_recommendations;
DROP TABLE IF EXISTS time_based_product_details;
DROP TABLE IF EXISTS time_based_products;
DROP TABLE IF EXISTS related_products;

-- 相关商品表
CREATE TABLE IF NOT EXISTS related_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    related_product_id BIGINT NOT NULL COMMENT '相关商品ID',
    relation_type ENUM('similar', 'complementary', 'recommended', 'bundle') NOT NULL COMMENT '关联类型',
    relation_strength DECIMAL(3,2) DEFAULT 1.0 COMMENT '关联强度',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_auto_generated BOOLEAN DEFAULT false COMMENT '是否自动生成',
    notes VARCHAR(500) COMMENT '关联说明',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_related_product_id (related_product_id),
    INDEX idx_relation_type (relation_type),
    INDEX idx_relation_strength (relation_strength),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_auto_generated (is_auto_generated)
) COMMENT '相关商品表';

-- 时间维度商品基础数据表
CREATE TABLE IF NOT EXISTS time_based_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间维度ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    date DATE NOT NULL COMMENT '日期',
    time_dimension ENUM('yesterday', 'today', 'week', 'month', 'quarter', 'year') NOT NULL COMMENT '时间维度',
    `rank` INT NOT NULL COMMENT '排名',
    yesterday_rank INT COMMENT '昨日排名',
    yesterday_sales INT COMMENT '昨日销量',
    week_sales INT COMMENT '周销量',
    month_sales INT COMMENT '月销量',
    quarter_sales INT COMMENT '季度销量',
    year_sales INT COMMENT '年销量',
    sales_growth_rate DECIMAL(5,2) COMMENT '销量增长率',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    favorite_count INT DEFAULT 0 COMMENT '收藏量',
    cart_count INT DEFAULT 0 COMMENT '加购量',
    conversion_rate DECIMAL(5,2) COMMENT '转化率',
    is_today_new BOOLEAN DEFAULT false COMMENT '是否今日新品',
    is_today_hot BOOLEAN DEFAULT false COMMENT '是否今日热销',
    real_time_rank INT COMMENT '实时排名',
    real_time_sales INT COMMENT '实时销量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_date (date),
    INDEX idx_time_dimension (time_dimension),
    INDEX idx_rank (`rank`),
    INDEX idx_sales_growth_rate (sales_growth_rate),
    UNIQUE KEY uk_product_date_dimension (product_id, date, time_dimension)
) COMMENT '时间维度商品基础数据表';

-- 时间维度商品详情表
CREATE TABLE IF NOT EXISTS time_based_product_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '详情ID',
    time_based_product_id BIGINT NOT NULL COMMENT '时间维度商品ID',
    preview_content TEXT COMMENT '明日预告内容',
    coupon_info JSON COMMENT '优惠券信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_time_based_product_id (time_based_product_id),
    INDEX idx_time_based_product_id (time_based_product_id)
) COMMENT '时间维度商品详情表';

-- 商品推荐表
CREATE TABLE IF NOT EXISTS product_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '推荐ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    recommended_product_id BIGINT NOT NULL COMMENT '推荐商品ID',
    recommendation_type ENUM('collaborative', 'content_based', 'popular', 'trending', 'personalized') NOT NULL COMMENT '推荐类型',
    recommendation_score DECIMAL(5,2) DEFAULT 0.0 COMMENT '推荐分数',
    position INT DEFAULT 0 COMMENT '推荐位置',
    algorithm_version VARCHAR(50) COMMENT '算法版本',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    expire_at TIMESTAMP NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_recommended_product_id (recommended_product_id),
    INDEX idx_recommendation_type (recommendation_type),
    INDEX idx_recommendation_score (recommendation_score),
    INDEX idx_position (position),
    INDEX idx_is_active (is_active),
    INDEX idx_expire_at (expire_at)
) COMMENT '商品推荐表';

-- 商品搜索关键词表
CREATE TABLE IF NOT EXISTS product_search_keywords (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关键词ID',
    keyword VARCHAR(200) NOT NULL COMMENT '搜索关键词',
    search_count INT DEFAULT 1 COMMENT '搜索次数',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    conversion_count INT DEFAULT 0 COMMENT '转化次数',
    last_searched_at TIMESTAMP NULL COMMENT '最后搜索时间',
    is_trending BOOLEAN DEFAULT false COMMENT '是否热门',
    is_recommended BOOLEAN DEFAULT false COMMENT '是否推荐',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_keyword (keyword),
    INDEX idx_search_count (search_count),
    INDEX idx_click_count (click_count),
    INDEX idx_is_trending (is_trending),
    INDEX idx_is_recommended (is_recommended)
) COMMENT '商品搜索关键词表';

-- 商品标签表
CREATE TABLE IF NOT EXISTS product_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    tag_type ENUM('category', 'feature', 'style', 'season', 'custom') DEFAULT 'custom' COMMENT '标签类型',
    tag_color VARCHAR(20) COMMENT '标签颜色',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    is_hot BOOLEAN DEFAULT false COMMENT '是否热门',
    is_recommended BOOLEAN DEFAULT false COMMENT '是否推荐',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_tag_name (tag_name),
    INDEX idx_tag_type (tag_type),
    INDEX idx_usage_count (usage_count),
    INDEX idx_is_hot (is_hot),
    INDEX idx_is_recommended (is_recommended)
) COMMENT '商品标签表';

-- 商品标签关联表
CREATE TABLE IF NOT EXISTS product_tag_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    tag_weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '标签权重',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY uk_product_tag (product_id, tag_id),
    INDEX idx_product_id (product_id),
    INDEX idx_tag_id (tag_id),
    INDEX idx_tag_weight (tag_weight)
) COMMENT '商品标签关联表';

COMMIT;

-- ============================================
-- 商品关系与推荐表初始数据
-- ============================================

-- 插入相关商品数据
INSERT INTO related_products (product_id, related_product_id, relation_type, relation_strength, sort_order, is_auto_generated, notes) VALUES
(1, 2, 'complementary', 0.8, 1, true, '智能手表与无线耳机搭配使用'),
(1, 3, 'similar', 0.7, 2, true, '同为智能穿戴设备'),
(2, 1, 'complementary', 0.8, 1, true, '无线耳机与智能手表搭配使用'),
(2, 4, 'similar', 0.6, 2, true, '同为音频设备'),
(3, 1, 'similar', 0.7, 1, true, '同为电子产品'),
(3, 4, 'complementary', 0.9, 2, true, '笔记本电脑与智能手机搭配使用');

-- 插入时间维度商品数据
INSERT INTO time_based_products (product_id, date, time_dimension, `rank`, yesterday_rank, yesterday_sales, week_sales, month_sales, sales_growth_rate, view_count, favorite_count, cart_count, conversion_rate, is_today_new, is_today_hot, real_time_rank, real_time_sales, preview_content, coupon_info) VALUES
(1, '2026-02-01', 'yesterday', 1, 2, 15, 80, 200, 25.5, 500, 45, 30, 3.0, true, true, 1, 25, '智能手表明日推出新配色，功能全面升级', '{"title": "新品预售券", "discount": "¥80", "condition": "满¥400可用", "expire": "明日有效"}'),
(1, '2026-02-01', 'week', 2, NULL, NULL, 80, 200, 15.2, 2500, 120, 80, 3.2, false, true, 2, 18, '本周智能手表持续热销，功能升级版即将上市', '{"title": "周度特惠券", "discount": "¥50", "condition": "满¥300可用", "expire": "本周有效"}'),
(1, '2026-02-01', 'month', 3, NULL, NULL, NULL, 200, 8.7, 8000, 300, 150, 2.5, false, false, 3, 12, '本月智能手表销量稳步增长，新品即将发布', '{"title": "月度专享券", "discount": "¥100", "condition": "满¥500可用", "expire": "本月有效"}'),
(2, '2026-02-01', 'yesterday', 2, 3, 12, 65, 180, 18.3, 450, 35, 25, 2.8, false, true, 2, 18, '耳机专场明日开启，多款新品限时特惠', '{"title": "音频专享券", "discount": "¥40", "condition": "满¥250可用", "expire": "明日过期"}'),
(3, '2026-02-01', 'yesterday', 3, 1, 18, 95, 250, 30.1, 600, 50, 35, 3.5, true, false, 3, 12, '轻薄笔记本明日发布新配置，性能提升30%', '{"title": "配置升级券", "discount": "¥200", "condition": "满¥1000可用", "expire": "3天后过期"}');

-- 插入商品推荐数据
INSERT INTO product_recommendations (product_id, recommended_product_id, recommendation_type, recommendation_score, position, algorithm_version, is_active, expire_at) VALUES
(1, 2, 'collaborative', 0.85, 1, 'v1.0', true, '2026-03-01 00:00:00'),
(1, 3, 'content_based', 0.78, 2, 'v1.0', true, '2026-03-01 00:00:00'),
(2, 1, 'collaborative', 0.82, 1, 'v1.0', true, '2026-03-01 00:00:00'),
(2, 4, 'popular', 0.75, 2, 'v1.0', true, '2026-03-01 00:00:00'),
(3, 1, 'trending', 0.80, 1, 'v1.0', true, '2026-03-01 00:00:00');

-- 插入商品搜索关键词数据
INSERT INTO product_search_keywords (keyword, search_count, click_count, conversion_count, last_searched_at, is_trending, is_recommended) VALUES
('智能手表', 1500, 800, 120, '2026-02-01 15:30:00', true, true),
('无线耳机', 1200, 650, 95, '2026-02-01 14:20:00', true, true),
('笔记本电脑', 1800, 900, 150, '2026-02-01 16:45:00', true, false),
('智能手机', 2000, 1000, 180, '2026-02-01 17:10:00', true, false),
('男士衬衫', 800, 400, 60, '2026-02-01 11:30:00', false, false);

-- 插入商品标签数据
INSERT INTO product_tags (tag_name, tag_type, tag_color, usage_count, is_hot, is_recommended) VALUES
('智能', 'feature', '#3498db', 50, true, true),
('运动', 'feature', '#e74c3c', 30, true, true),
('健康', 'feature', '#2ecc71', 25, false, true),
('无线', 'feature', '#9b59b6', 40, true, true),
('降噪', 'feature', '#f39c12', 35, true, true),
('轻薄', 'feature', '#1abc9c', 20, false, false),
('旗舰', 'style', '#e67e22', 15, true, false),
('纯棉', 'feature', '#27ae60', 10, false, false);

-- 插入商品标签关联数据
INSERT INTO product_tag_relations (product_id, tag_id, tag_weight) VALUES
(1, 1, 1.0), -- 智能手表 - 智能
(1, 2, 0.9), -- 智能手表 - 运动
(1, 3, 0.8), -- 智能手表 - 健康
(2, 4, 1.0), -- 无线耳机 - 无线
(2, 5, 0.9), -- 无线耳机 - 降噪
(3, 6, 0.8), -- 笔记本电脑 - 轻薄
(4, 7, 0.7), -- 智能手机 - 旗舰
(5, 8, 0.6); -- 男士衬衫 - 纯棉

COMMIT;