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
DROP TABLE IF EXISTS product_sales_stats;
DROP TABLE IF EXISTS recommendation_effects;
DROP TABLE IF EXISTS user_behavior_logs;
DROP TABLE IF EXISTS scenario_recommendations;
DROP TABLE IF EXISTS user_interest_profiles;

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
    INDEX idx_type_score (recommendation_type, recommendation_score) COMMENT '类型分数查询',
    INDEX idx_is_active_expire (is_active, expire_at)
) COMMENT '商品推荐表';

-- ============================================
-- 销量统计表（单一表设计，主流电商最佳实践）
-- ============================================

-- 销量统计表（支持多种时间粒度）
CREATE TABLE IF NOT EXISTS product_sales_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    
    -- 时间粒度
    time_granularity ENUM('daily', 'monthly', 'yearly') NOT NULL COMMENT '时间粒度',
    stat_date DATE NOT NULL COMMENT '统计日期（如：日粒度-2026-02-01，月粒度-2026-02-01，年粒度-2026-01-01）',
    
    -- 核心业务数据
    `rank` INT NOT NULL COMMENT '排名',
    sales_count INT DEFAULT 0 COMMENT '销量',
    sales_amount DECIMAL(12,2) DEFAULT 0.0 COMMENT '销售额',
    sales_growth_rate DECIMAL(5,2) COMMENT '销量增长率',
    
    -- 用户行为数据
    view_count INT DEFAULT 0 COMMENT '浏览量',
    favorite_count INT DEFAULT 0 COMMENT '收藏量',
    cart_count INT DEFAULT 0 COMMENT '加购量',
    conversion_rate DECIMAL(5,2) COMMENT '转化率',
    
    -- 业务标识
    is_trending BOOLEAN DEFAULT false COMMENT '是否趋势商品',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_time_granularity (time_granularity),
    INDEX idx_stat_date (stat_date),
    INDEX idx_rank (`rank`),
    INDEX idx_sales_count (sales_count),
    INDEX idx_is_trending (is_trending),
    INDEX idx_product_granularity_date (product_id, time_granularity, stat_date),
    
    -- 唯一约束（避免重复统计）
    UNIQUE KEY uk_product_granularity_date (product_id, time_granularity, stat_date)
) COMMENT '销量统计表（支持多种时间粒度）';



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

-- 场景推荐表（基于用户场景的个性化推荐）
CREATE TABLE IF NOT EXISTS scenario_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 用户关联（关键改进：支持个性化）
    user_id BIGINT COMMENT '用户ID（NULL表示通用模板）',
    
    -- 场景定义
    scenario_type ENUM('morning', 'commute', 'work', 'lunch', 'evening', 'weekend', 'travel', 'gift') COMMENT '场景类型',
    time_slot VARCHAR(20) COMMENT '时间段: "07:00-09:00"',
    location_context JSON COMMENT '位置上下文: {"home", "office", "commute"}',
    
    -- 场景特征
    user_state ENUM('relaxed', 'focused', 'social', 'shopping') COMMENT '用户状态',
    weather_conditions JSON COMMENT '天气条件: {"sunny", "rainy"}',
    
    -- 推荐内容（动态计算，非固定列表）
    recommended_products JSON COMMENT '推荐商品ID列表',
    scenario_story VARCHAR(500) COMMENT '场景故事文案',
    
    -- 效果指标
    success_rate DECIMAL(5,4) COMMENT '场景成功率',
    avg_engagement DECIMAL(5,2) COMMENT '平均参与度',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_scenario (user_id, scenario_type) COMMENT '用户场景关联查询',
    INDEX idx_scenario_type (scenario_type),
    INDEX idx_time_slot (time_slot),
    INDEX idx_user_state (user_state)
) COMMENT '场景推荐表';

-- 用户兴趣画像表（个性化推荐基础）
CREATE TABLE IF NOT EXISTS user_interest_profiles (
    user_id BIGINT PRIMARY KEY COMMENT '用户ID',
    
    -- 基础兴趣标签（带权重）
    interest_tags JSON COMMENT '兴趣标签及权重: {"科技": 0.8, "运动": 0.6, "时尚": 0.4}',
    
    -- 行为偏好分析
    behavior_patterns JSON COMMENT '行为模式: {"浏览时段": "19:00-22:00", "点击偏好": "图片>文字"}',
    
    -- 发现偏好设置
    discovery_preferences JSON COMMENT '发现偏好: {"新品偏好": "高", "价格敏感度": "中等"}',
    
    -- 实时兴趣热度
    trending_interests JSON COMMENT '实时兴趣热度: {"热点事件": 0.9, "季节性": 0.7}',
    
    -- 时间戳
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
    profile_version INT DEFAULT 1 COMMENT '画像版本',
    
    INDEX idx_last_updated (last_updated)
) COMMENT '用户兴趣画像表';

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

-- 插入销量统计数据（单一表设计）
INSERT INTO product_sales_stats (product_id, time_granularity, stat_date, `rank`, sales_count, sales_amount, sales_growth_rate, view_count, favorite_count, cart_count, conversion_rate, is_trending) VALUES
-- 日粒度数据
(1, 'daily', '2026-02-01', 1, 25, 7475.00, 25.5, 500, 45, 30, 3.0, true),
(2, 'daily', '2026-02-01', 2, 18, 3582.00, 18.3, 450, 35, 25, 2.8, true),
(3, 'daily', '2026-02-01', 3, 12, 71988.00, 30.1, 600, 50, 35, 3.5, false),
(4, 'daily', '2026-02-01', 4, 8, 39992.00, 15.7, 400, 30, 20, 2.5, false),

-- 月粒度数据
(1, 'monthly', '2026-02-01', 1, 80, 23920.00, 15.2, 2500, 120, 80, 3.2, true),
(2, 'monthly', '2026-02-01', 2, 60, 11940.00, 12.8, 2000, 100, 70, 2.9, true),
(3, 'monthly', '2026-02-01', 3, 40, 239952.00, 18.5, 8000, 400, 300, 3.8, false),
(4, 'monthly', '2026-02-01', 4, 30, 149976.00, 14.3, 6000, 300, 200, 3.1, false),

-- 年粒度数据
(1, 'yearly', '2026-01-01', 1, 500, 149500.00, 20.5, 15000, 800, 500, 3.5, true),
(2, 'yearly', '2026-01-01', 2, 350, 69650.00, 18.2, 12000, 600, 400, 3.2, true),
(3, 'yearly', '2026-01-01', 3, 250, 1499760.00, 15.8, 50000, 2500, 1800, 3.6, false),
(4, 'yearly', '2026-01-01', 4, 200, 999840.00, 13.5, 40000, 2000, 1500, 3.3, false);



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

-- 插入用户行为日志数据
INSERT INTO user_behavior_logs (user_id, product_id, behavior_type, behavior_weight, session_id, referrer_url, behavior_context) VALUES
(1001, 1, 'view', 1.0, 'session001', 'https://example.com/products', '{"device": "mobile", "location": "北京"}'),
(1001, 1, 'favorite', 1.5, 'session001', 'https://example.com/products/1', '{"device": "mobile", "location": "北京"}'),
(1002, 2, 'view', 1.0, 'session002', 'https://example.com/products', '{"device": "desktop", "location": "上海"}'),
(1002, 2, 'cart', 2.0, 'session002', 'https://example.com/products/2', '{"device": "desktop", "location": "上海"}'),
(1003, 3, 'view', 1.0, 'session003', 'https://example.com/products', '{"device": "tablet", "location": "广州"}'),
(1003, 3, 'purchase', 3.0, 'session003', 'https://example.com/products/3', '{"device": "tablet", "location": "广州"}');

-- 插入推荐效果追踪数据
INSERT INTO recommendation_effects (recommendation_id, user_id, impression_count, click_count, conversion_count, last_impressed_at, last_clicked_at) VALUES
(1, 1001, 5, 2, 1, '2026-02-01 10:30:00', '2026-02-01 10:35:00'),
(2, 1002, 3, 1, 0, '2026-02-01 14:20:00', '2026-02-01 14:25:00'),
(3, 1003, 8, 3, 2, '2026-02-01 16:45:00', '2026-02-01 16:50:00');

-- 插入场景推荐数据（包含通用模板和个性化推荐）
INSERT INTO scenario_recommendations (user_id, scenario_type, time_slot, location_context, user_state, weather_conditions, recommended_products, scenario_story, success_rate, avg_engagement) VALUES
-- 通用模板（user_id为NULL）
(NULL, 'morning', '07:00-09:00', '{"location": "home"}', 'relaxed', '{"weather": "sunny"}', '[1, 2]', '早晨时光，用科技产品开启美好一天。智能手表监测健康，无线耳机享受音乐', 0.75, 4.2),
(NULL, 'commute', '08:00-09:00', '{"location": "subway"}', 'focused', '{"weather": "comfortable"}', '[2, 5]', '通勤路上，降噪耳机隔绝嘈杂，便携充电宝随时续航', 0.68, 3.8),

-- 个性化推荐（关联具体用户）
(1001, 'morning', '07:00-09:00', '{"location": "home"}', 'relaxed', '{"weather": "sunny"}', '[1, 3]', '科技爱好者专属：智能手表追踪健康数据，搭配最新智能家居设备开启高效一天', 0.85, 4.8),
(1002, 'commute', '08:00-09:00', '{"location": "subway"}', 'focused', '{"weather": "comfortable"}', '[2, 8]', '时尚达人通勤：降噪耳机享受高品质音乐，搭配时尚背包展现个性', 0.78, 4.5),
(1003, 'work', '09:00-12:00', '{"location": "office"}', 'focused', '{"weather": "indoor"}', '[3, 9]', '办公达人必备：轻薄笔记本高效办公，搭配人体工学椅保护健康', 0.72, 4.0),
(1001, 'evening', '19:00-22:00', '{"location": "home"}', 'relaxed', '{"weather": "evening"}', '[4, 10]', '科技宅晚间：智能手机娱乐放松，智能家居打造舒适环境', 0.65, 3.5);

-- 插入用户兴趣画像数据
INSERT INTO user_interest_profiles (user_id, interest_tags, behavior_patterns, discovery_preferences, trending_interests) VALUES
(1001, '{"科技": 0.8, "运动": 0.6, "健康": 0.7}', '{"浏览时段": "19:00-22:00", "设备偏好": "手机"}', '{"新品偏好": "高", "价格敏感度": "低"}', '{"热点事件": 0.9}'),
(1002, '{"音频": 0.9, "时尚": 0.5, "旅行": 0.6}', '{"浏览时段": "12:00-14:00", "点击偏好": "视频>图片"}', '{"新品偏好": "中等", "品牌忠诚度": "高"}', '{"季节性": 0.7}'),
(1003, '{"办公": 0.8, "游戏": 0.7, "摄影": 0.6}', '{"浏览时段": "20:00-23:00", "购买决策": "详细比较"}', '{"新品偏好": "低", "性价比优先": "是"}', '{"技术更新": 0.8}');

COMMIT;