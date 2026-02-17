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
DROP TABLE IF EXISTS product_yearly_sales;
DROP TABLE IF EXISTS product_monthly_sales;
DROP TABLE IF EXISTS product_daily_sales;
DROP TABLE IF EXISTS recommendation_effects;
DROP TABLE IF EXISTS user_behavior_logs;

-- 统一推荐表（合并相关商品和推荐功能）
CREATE TABLE IF NOT EXISTS product_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '推荐ID',
    user_id BIGINT COMMENT '用户ID（NULL表示通用推荐）',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    recommended_product_id BIGINT NOT NULL COMMENT '推荐商品ID',
    recommendation_type ENUM('similar', 'complementary', 'bundle', 'collaborative', 'content_based', 'popular', 'trending', 'personalized', 'daily_discovery', 'new_arrival', 'limited_time') NOT NULL COMMENT '推荐类型',
    recommendation_score DECIMAL(5,2) DEFAULT 0.0 COMMENT '推荐分数',
    position INT DEFAULT 0 COMMENT '推荐位置',
    algorithm_version VARCHAR(50) COMMENT '算法版本',
    recommendation_context JSON COMMENT '推荐上下文（如：基于哪些标签、行为等）',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    expire_at TIMESTAMP NULL COMMENT '过期时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_product (user_id, product_id),
    INDEX idx_recommendation_type (recommendation_type),
    INDEX idx_recommendation_score (recommendation_score),
    INDEX idx_is_active_expire (is_active, expire_at)
) COMMENT '商品推荐表';

-- ============================================
-- 时间维度销量统计表（按维度拆分，推荐方案）
-- ============================================

-- 日维度销量统计表
CREATE TABLE IF NOT EXISTS product_daily_sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日维度ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sale_date DATE NOT NULL COMMENT '销售日期',
    
    -- 核心业务数据
    `rank` INT NOT NULL COMMENT '日排名',
    sales_count INT DEFAULT 0 COMMENT '日销量',
    sales_amount DECIMAL(12,2) DEFAULT 0.0 COMMENT '日销售额',
    sales_growth_rate DECIMAL(5,2) COMMENT '日销量增长率',
    
    -- 用户行为数据
    view_count INT DEFAULT 0 COMMENT '日浏览量',
    favorite_count INT DEFAULT 0 COMMENT '日收藏量',
    cart_count INT DEFAULT 0 COMMENT '日加购量',
    conversion_rate DECIMAL(5,2) COMMENT '日转化率',
    
    -- 业务标识
    is_trending BOOLEAN DEFAULT false COMMENT '是否日趋势商品',
    
    -- 每日发现特色字段
    discovery_highlight VARCHAR(200) COMMENT '发现亮点（如：今日爆款、新品首发等）',
    discovery_reason TEXT COMMENT '发现理由（为什么推荐这个商品）',
    special_offer JSON COMMENT '特别优惠信息',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_sale_date (sale_date),
    INDEX idx_rank (`rank`),
    INDEX idx_sales_count (sales_count),
    INDEX idx_is_trending (is_trending),
    
    -- 唯一约束（避免重复统计）
    UNIQUE KEY uk_product_date (product_id, sale_date)
) COMMENT '日维度销量统计表';

-- 月维度销量统计表
CREATE TABLE IF NOT EXISTS product_monthly_sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '月维度ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sale_month DATE NOT NULL COMMENT '销售月份（存储月份第一天，如2026-02-01）',
    
    -- 核心业务数据
    `rank` INT NOT NULL COMMENT '月排名',
    sales_count INT DEFAULT 0 COMMENT '月销量',
    sales_amount DECIMAL(12,2) DEFAULT 0.0 COMMENT '月销售额',
    sales_growth_rate DECIMAL(5,2) COMMENT '月销量增长率',
    
    -- 用户行为数据
    view_count INT DEFAULT 0 COMMENT '月浏览量',
    favorite_count INT DEFAULT 0 COMMENT '月收藏量',
    cart_count INT DEFAULT 0 COMMENT '月加购量',
    conversion_rate DECIMAL(5,2) COMMENT '月转化率',
    
    -- 业务标识
    is_trending BOOLEAN DEFAULT false COMMENT '是否月趋势商品',
    
    -- 月度特色字段
    monthly_highlight VARCHAR(200) COMMENT '月度亮点（如：月度爆款、新品热销等）',
    monthly_reason TEXT COMMENT '月度推荐理由',
    monthly_offer JSON COMMENT '月度优惠信息',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_sale_month (sale_month),
    INDEX idx_rank (`rank`),
    INDEX idx_sales_count (sales_count),
    INDEX idx_is_trending (is_trending),
    
    -- 唯一约束（避免重复统计）
    UNIQUE KEY uk_product_month (product_id, sale_month)
) COMMENT '月维度销量统计表';

-- 年维度销量统计表
CREATE TABLE IF NOT EXISTS product_yearly_sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '年维度ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sale_year DATE NOT NULL COMMENT '销售年份（存储年份第一天，如2026-01-01）',
    
    -- 核心业务数据
    `rank` INT NOT NULL COMMENT '年排名',
    sales_count INT DEFAULT 0 COMMENT '年销量',
    sales_amount DECIMAL(12,2) DEFAULT 0.0 COMMENT '年销售额',
    sales_growth_rate DECIMAL(5,2) COMMENT '年销量增长率',
    
    -- 用户行为数据
    view_count INT DEFAULT 0 COMMENT '年浏览量',
    favorite_count INT DEFAULT 0 COMMENT '年收藏量',
    cart_count INT DEFAULT 0 COMMENT '年加购量',
    conversion_rate DECIMAL(5,2) COMMENT '年转化率',
    
    -- 业务标识
    is_trending BOOLEAN DEFAULT false COMMENT '是否年趋势商品',
    
    -- 年度特色字段
    yearly_highlight VARCHAR(200) COMMENT '年度亮点（如：年度爆款、热销商品等）',
    yearly_reason TEXT COMMENT '年度推荐理由',
    yearly_offer JSON COMMENT '年度优惠信息',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_sale_year (sale_year),
    INDEX idx_rank (`rank`),
    INDEX idx_sales_count (sales_count),
    INDEX idx_is_trending (is_trending),
    
    -- 唯一约束（避免重复统计）
    UNIQUE KEY uk_product_year (product_id, sale_year)
) COMMENT '年维度销量统计表';



-- 用户行为表（记录浏览、点击、购买等行为）
CREATE TABLE IF NOT EXISTS user_behavior_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '行为ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    behavior_type ENUM('view', 'click', 'cart', 'purchase', 'favorite') NOT NULL COMMENT '行为类型',
    behavior_weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '行为权重',
    session_id VARCHAR(100) COMMENT '会话ID',
    referrer_url VARCHAR(500) COMMENT '来源页面',
    behavior_context JSON COMMENT '行为上下文（设备、位置等）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_behavior (user_id, behavior_type, created_at),
    INDEX idx_product_behavior (product_id, behavior_type, created_at),
    INDEX idx_session (session_id)
) COMMENT '用户行为日志表';

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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_tag_name (tag_name),
    INDEX idx_tag_type (tag_type)
) COMMENT '商品标签表';

-- 推荐效果追踪表
CREATE TABLE IF NOT EXISTS recommendation_effects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '效果ID',
    recommendation_id BIGINT NOT NULL COMMENT '推荐ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    impression_count INT DEFAULT 1 COMMENT '展示次数',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    conversion_count INT DEFAULT 0 COMMENT '转化次数',
    last_impressed_at TIMESTAMP NULL COMMENT '最后展示时间',
    last_clicked_at TIMESTAMP NULL COMMENT '最后点击时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_recommendation_user (recommendation_id, user_id),
    INDEX idx_impression_time (last_impressed_at),
    INDEX idx_click_time (last_clicked_at)
) COMMENT '推荐效果追踪表';

-- 商品标签关联表
CREATE TABLE IF NOT EXISTS product_tag_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    
    UNIQUE KEY uk_product_tag (product_id, tag_id),
    INDEX idx_product_id (product_id),
    INDEX idx_tag_id (tag_id)
) COMMENT '商品标签关联表';

COMMIT;

-- ============================================
-- 商品关系与推荐表初始数据
-- ============================================

-- 插入统一推荐数据（合并原相关商品和推荐数据）
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, position, algorithm_version, recommendation_context, is_active, expire_at) VALUES
(NULL, 1, 2, 'complementary', 0.8, 1, 'v1.0', '{"reason": "智能手表与无线耳机搭配使用"}', true, '2026-03-01 00:00:00'),
(NULL, 1, 3, 'similar', 0.7, 2, 'v1.0', '{"reason": "同为智能穿戴设备"}', true, '2026-03-01 00:00:00'),
(NULL, 2, 1, 'complementary', 0.8, 1, 'v1.0', '{"reason": "无线耳机与智能手表搭配使用"}', true, '2026-03-01 00:00:00'),
(NULL, 2, 4, 'similar', 0.6, 2, 'v1.0', '{"reason": "同为音频设备"}', true, '2026-03-01 00:00:00'),
(NULL, 3, 1, 'similar', 0.7, 1, 'v1.0', '{"reason": "同为电子产品"}', true, '2026-03-01 00:00:00'),
(NULL, 3, 4, 'complementary', 0.9, 2, 'v1.0', '{"reason": "笔记本电脑与智能手机搭配使用"}', true, '2026-03-01 00:00:00'),
(NULL, 1, 2, 'collaborative', 0.85, 1, 'v1.0', '{"reason": "协同过滤推荐"}', true, '2026-03-01 00:00:00'),
(NULL, 1, 3, 'content_based', 0.78, 2, 'v1.0', '{"reason": "内容相似推荐"}', true, '2026-03-01 00:00:00'),
(NULL, 2, 1, 'collaborative', 0.82, 1, 'v1.0', '{"reason": "协同过滤推荐"}', true, '2026-03-01 00:00:00'),
(NULL, 2, 4, 'popular', 0.75, 2, 'v1.0', '{"reason": "热门商品推荐"}', true, '2026-03-01 00:00:00'),
(NULL, 3, 1, 'trending', 0.80, 1, 'v1.0', '{"reason": "趋势商品推荐"}', true, '2026-03-01 00:00:00'),
-- 每日发现特色推荐
(NULL, 1, 2, 'daily_discovery', 0.95, 1, 'v1.1', '{"reason": "今日爆款搭配新品首发", "highlight": "今日最佳组合"}', true, '2026-02-02 00:00:00'),
(NULL, 2, 1, 'daily_discovery', 0.92, 2, 'v1.1', '{"reason": "新品首发搭配经典爆款", "highlight": "热门组合推荐"}', true, '2026-02-02 00:00:00'),
(NULL, 3, 4, 'new_arrival', 0.88, 1, 'v1.1', '{"reason": "性能升级搭配旗舰机型", "highlight": "科技新品组合"}', true, '2026-02-05 00:00:00'),
(NULL, 4, 3, 'limited_time', 0.85, 1, 'v1.1', '{"reason": "限时优惠搭配性能升级", "highlight": "限时特惠组合"}', true, '2026-02-03 00:00:00');

-- 插入日维度销量数据
INSERT INTO product_daily_sales (product_id, sale_date, `rank`, sales_count, sales_amount, sales_growth_rate, view_count, favorite_count, cart_count, conversion_rate, is_trending, discovery_highlight, discovery_reason, special_offer) VALUES
(1, '2026-02-01', 1, 25, 7475.00, 25.5, 500, 45, 30, 3.0, true, '今日爆款', '销量增长25%，用户评价4.8分', '{"title": "限时特惠", "discount": "¥80", "condition": "今日有效"}'),
(2, '2026-02-01', 2, 18, 3582.00, 18.3, 450, 35, 25, 2.8, true, '新品首发', '主动降噪技术，音质升级', '{"title": "新品预售", "discount": "¥50", "condition": "预售期有效"}'),
(3, '2026-02-01', 3, 12, 71988.00, 30.1, 600, 50, 35, 3.5, false, '性能升级', '轻薄便携，续航提升30%', '{"title": "配置升级", "discount": "¥200", "condition": "限时优惠"}'),
(4, '2026-02-01', 4, 8, 39992.00, 15.7, 400, 30, 20, 2.5, false, '旗舰机型', '专业相机系统，5G网络', '{"title": "旗舰专享", "discount": "¥100", "condition": "旗舰机型专享"}');

-- 插入月维度销量数据
INSERT INTO product_monthly_sales (product_id, sale_month, `rank`, sales_count, sales_amount, sales_growth_rate, view_count, favorite_count, cart_count, conversion_rate, is_trending, monthly_highlight, monthly_reason, monthly_offer) VALUES
(1, '2026-02-01', 1, 80, 23920.00, 15.2, 2500, 120, 80, 3.2, true, '月度热销', '本月销量冠军，用户满意度高', '{"title": "月度特惠", "discount": "¥50", "condition": "本月有效"}'),
(2, '2026-02-01', 2, 60, 11940.00, 12.8, 2000, 100, 70, 2.9, true, '月度新品', '本月新品销量领先', '{"title": "新品特惠", "discount": "¥30", "condition": "新品期有效"}'),
(3, '2026-02-01', 3, 40, 239952.00, 18.5, 8000, 400, 300, 3.8, false, '月度性能', '本月性能升级商品热销', '{"title": "性能特惠", "discount": "¥150", "condition": "本月有效"}'),
(4, '2026-02-01', 4, 30, 149976.00, 14.3, 6000, 300, 200, 3.1, false, '月度旗舰', '本月旗舰机型表现稳定', '{"title": "旗舰特惠", "discount": "¥80", "condition": "本月有效"}');

-- 插入年维度销量数据
INSERT INTO product_yearly_sales (product_id, sale_year, `rank`, sales_count, sales_amount, sales_growth_rate, view_count, favorite_count, cart_count, conversion_rate, is_trending, yearly_highlight, yearly_reason, yearly_offer) VALUES
(1, '2026-01-01', 1, 500, 149500.00, 20.5, 15000, 800, 500, 3.5, true, '年度爆款', '年度销量冠军，用户评价4.9分', '{"title": "年度特惠", "discount": "¥100", "condition": "年度有效"}'),
(2, '2026-01-01', 2, 350, 69650.00, 18.2, 12000, 600, 400, 3.2, true, '年度热销', '年度热销商品，用户满意度高', '{"title": "年度优惠", "discount": "¥80", "condition": "年度有效"}'),
(3, '2026-01-01', 3, 250, 1499760.00, 15.8, 50000, 2500, 1800, 3.6, false, '年度性能', '年度性能商品表现优异', '{"title": "年度性能特惠", "discount": "¥200", "condition": "年度有效"}'),
(4, '2026-01-01', 4, 200, 999840.00, 13.5, 40000, 2000, 1500, 3.3, false, '年度旗舰', '年度旗舰机型稳定热销', '{"title": "年度旗舰特惠", "discount": "¥150", "condition": "年度有效"}');



-- 插入商品搜索关键词数据
INSERT INTO product_search_keywords (keyword, search_count, click_count, conversion_count, last_searched_at, is_trending, is_recommended) VALUES
('智能手表', 1500, 800, 120, '2026-02-01 15:30:00', true, true),
('无线耳机', 1200, 650, 95, '2026-02-01 14:20:00', true, true),
('笔记本电脑', 1800, 900, 150, '2026-02-01 16:45:00', true, false),
('智能手机', 2000, 1000, 180, '2026-02-01 17:10:00', true, false),
('男士衬衫', 800, 400, 60, '2026-02-01 11:30:00', false, false);

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
(5, 8);  -- 男士衬衫 - 纯棉

COMMIT;