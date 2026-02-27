-- ============================================
-- 商品关系与推荐表结构
-- 创建时间: 2026-02-04
-- 业务模块: 商品关系与推荐
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_search_keywords;
DROP TABLE IF EXISTS product_recommendations;
DROP TABLE IF EXISTS content_recommendations;
DROP TABLE IF EXISTS product_sales_stats;
DROP TABLE IF EXISTS recommendation_effects;
DROP TABLE IF EXISTS user_behavior_logs_core;
DROP TABLE IF EXISTS user_behavior_logs_details;
DROP TABLE IF EXISTS scenario_recommendations;
DROP TABLE IF EXISTS user_interest_profiles;
DROP TABLE IF EXISTS product_knowledge_graph;
DROP TABLE IF EXISTS user_lifecycle_events;

-- 统一推荐表（合并相关商品和推荐功能）
CREATE TABLE IF NOT EXISTS product_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '推荐ID',
    user_id BIGINT COMMENT '用户ID（NULL表示通用推荐）',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    recommended_product_id BIGINT NOT NULL COMMENT '推荐商品ID',
    recommendation_type VARCHAR(30) NOT NULL COMMENT '推荐类型：similar-相似商品, complementary-互补商品, bundle-组合套装, collaborative-协同过滤, content_based-内容相似, popular-热门商品, trending-趋势商品, personalized-个性化推荐, daily_discovery-每日发现, new_arrival-新品推荐, limited_time-限时推荐',
    recommendation_score DECIMAL(5,2) DEFAULT 0.0 COMMENT '推荐分数',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_product (user_id, product_id),
    INDEX idx_type_score (recommendation_type, recommendation_score) COMMENT '类型分数查询',
    INDEX idx_is_active (is_active),
    INDEX idx_product_type (product_id, recommendation_type),
    INDEX idx_recommended_product (recommended_product_id),
    
    INDEX idx_product_active (product_id, is_active) COMMENT '商品详情页推荐查询优化',
    INDEX idx_recommended_active (recommended_product_id, is_active) COMMENT '推荐商品有效性查询优化',
    INDEX idx_user_type_active (user_id, recommendation_type, is_active) COMMENT '个性化推荐查询优化'
) COMMENT '商品推荐表';

-- 内容推荐表（支持内容发现功能）
CREATE TABLE IF NOT EXISTS content_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '推荐ID',
    user_id BIGINT COMMENT '用户ID（NULL表示通用推荐）',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    recommendation_type VARCHAR(30) NOT NULL COMMENT '推荐类型：daily_discovery-每日发现, trending-趋势内容, personalized-个性化推荐, related-相关内容',
    recommendation_score DECIMAL(5,2) DEFAULT 0.0 COMMENT '推荐分数',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_content (user_id, content_id),
    INDEX idx_type_score (recommendation_type, recommendation_score),
    INDEX idx_is_active (is_active),
    INDEX idx_content_type (content_id, recommendation_type)
) COMMENT '内容推荐表';

-- ============================================
-- 销量统计表（单一表设计，主流电商最佳实践）
-- ============================================

-- 销量统计表（支持多种时间粒度）
CREATE TABLE IF NOT EXISTS product_sales_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    
    -- 时间粒度
    time_granularity VARCHAR(10) NOT NULL COMMENT '时间粒度',
    stat_date DATE NOT NULL COMMENT '统计日期（如：日粒度-2026-02-01，月粒度-2026-02-01，年粒度-2026-01-01）',
    
    -- 核心业务数据
    `rank` INT NOT NULL COMMENT '排名',
    sales_count INT DEFAULT 0 COMMENT '销量',
    sales_amount DECIMAL(12,2) DEFAULT 0.0 COMMENT '销售额',
    sales_growth_rate DECIMAL(5,2) COMMENT '销量增长率',
    
    -- 用户行为数据
    view_count INT DEFAULT 0 COMMENT '浏览量',
    favorite_count INT DEFAULT 0 COMMENT '收藏量',
    share_count INT DEFAULT 0 COMMENT '分享量',
    cart_count INT DEFAULT 0 COMMENT '加购量',
    
    -- 商品质量数据
    avg_rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
    review_count INT DEFAULT 0 COMMENT '评价数量',
    return_count INT DEFAULT 0 COMMENT '退货数量',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_time_granularity (time_granularity),
    INDEX idx_stat_date (stat_date),
    INDEX idx_rank (`rank`),
    INDEX idx_sales_count (sales_count),
    INDEX idx_avg_rating (avg_rating),
    INDEX idx_product_granularity_date (product_id, time_granularity, stat_date),
    
    -- 唯一约束（避免重复统计）
    UNIQUE KEY uk_product_granularity_date (product_id, time_granularity, stat_date)
) COMMENT '销量统计表（支持多种时间粒度）';



-- 用户行为核心表（高频查询字段，支持实时业务）
CREATE TABLE IF NOT EXISTS user_behavior_logs_core (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '行为ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    behavior_type VARCHAR(20) NOT NULL COMMENT '行为类型：view-浏览, favorite-收藏, cart-加购, purchase-购买, share-分享',
    behavior_weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '行为权重',
    session_id VARCHAR(100) COMMENT '会话ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user_behavior (user_id, behavior_type, created_at),
    INDEX idx_product_behavior (product_id, behavior_type, created_at),
    INDEX idx_session (session_id),
    INDEX idx_created_at (created_at),
    INDEX idx_user_behavior_time (user_id, behavior_type, created_at),
    INDEX idx_product_behavior_time (product_id, behavior_type, created_at)
) COMMENT '用户行为核心表（高频查询字段）';

-- 用户行为详情表（低频查询大字段，用于深度分析）
CREATE TABLE IF NOT EXISTS user_behavior_logs_details (
    id BIGINT PRIMARY KEY COMMENT '行为ID（与核心表一致）',
    referrer_url VARCHAR(500) COMMENT '来源页面',
    behavior_context JSON COMMENT '行为上下文（设备、位置等）',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_id (id),
    INDEX idx_created_at (created_at)
) COMMENT '用户行为详情表（低频查询大字段）';

-- 场景推荐表（精简版，基于用户场景的个性化推荐）
CREATE TABLE IF NOT EXISTS scenario_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 用户关联（关键改进：支持个性化）
    user_id BIGINT COMMENT '用户ID（NULL表示通用模板）',
    
    -- 场景定义
    scenario_type VARCHAR(20) COMMENT '场景类型',
    time_slot VARCHAR(20) COMMENT '时间段: "07:00-09:00"',
    location_context JSON COMMENT '位置上下文: {"home", "office", "commute"}',
    
    -- 场景特征
    user_state VARCHAR(20) COMMENT '用户状态',
    weather_conditions JSON COMMENT '天气条件: {"sunny", "rainy"}',
    
    -- 推荐内容（动态计算，非固定列表）
    recommended_products JSON COMMENT '推荐商品ID列表',
    scenario_story VARCHAR(500) COMMENT '场景故事文案',
    
    -- 推荐语核心内容
    recommendation_title VARCHAR(200) COMMENT '推荐标题',
    recommendation_description TEXT COMMENT '推荐描述',
    
    -- 推荐语元数据（JSON格式存储辅助信息）
    recommendation_metadata JSON COMMENT '推荐语元数据：{"style": "default", "ai_generated": false, "approval_status": "pending", "quality_score": 0.0, "usage_count": 0}',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 核心索引
    INDEX idx_user_scenario (user_id, scenario_type) COMMENT '用户场景关联查询',
    INDEX idx_scenario_type (scenario_type),
    INDEX idx_time_slot (time_slot),
    INDEX idx_user_state (user_state),
    INDEX idx_scenario_user (user_id, scenario_type),
    
    -- 推荐语索引
    INDEX idx_recommendation_metadata ((CAST(recommendation_metadata->'$.approval_status' AS CHAR(20)))) COMMENT '审核状态查询',
    INDEX idx_recommendation_quality ((CAST(recommendation_metadata->'$.quality_score' AS DECIMAL(3,2)))) COMMENT '质量评分查询'
) COMMENT '场景推荐表（精简版）';

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

-- ============================================
-- 高级推荐功能新增表结构
-- ============================================

-- 商品知识图谱表（支持解决方案推荐）
CREATE TABLE IF NOT EXISTS product_knowledge_graph (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '知识图谱ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    related_product_id BIGINT NOT NULL COMMENT '关联商品ID',
    relationship_type VARCHAR(50) NOT NULL COMMENT '关系类型：solution-解决方案, prerequisite-前置条件, complementary-互补, alternative-替代',
    relationship_strength DECIMAL(3,2) DEFAULT 0.0 COMMENT '关系强度',
    context_description TEXT COMMENT '关系上下文描述',
    confidence_score DECIMAL(3,2) DEFAULT 0.0 COMMENT '置信度',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_relationship (product_id, relationship_type),
    INDEX idx_related_product (related_product_id),
    INDEX idx_relationship_strength (relationship_strength),
    INDEX idx_is_active (is_active),
    
    INDEX idx_product_active (product_id, is_active) COMMENT '商品详情页搭配推荐查询优化',
    INDEX idx_related_active (related_product_id, is_active) COMMENT '关联商品有效性查询优化',
    INDEX idx_product_type_active (product_id, relationship_type, is_active) COMMENT '特定关系类型查询优化',
    INDEX idx_strength_active (relationship_strength, is_active) COMMENT '按关系强度排序查询优化'
) COMMENT '商品知识图谱表（支持解决方案推荐）';

-- 用户生命周期表（支持动态时间轴推荐）
CREATE TABLE IF NOT EXISTS user_lifecycle_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '生命周期事件ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    event_type VARCHAR(50) NOT NULL COMMENT '事件类型：pregnancy-怀孕, new_parent-新手父母, career_change-职业变动, relocation-搬迁, education-教育阶段',
    event_start_date DATE COMMENT '事件开始日期',
    event_end_date DATE COMMENT '事件结束日期',
    predicted_next_event VARCHAR(50) COMMENT '预测下一个事件',
    confidence_score DECIMAL(3,2) DEFAULT 0.0 COMMENT '置信度',
    lifecycle_stage VARCHAR(30) COMMENT '生命周期阶段',
    event_context JSON COMMENT '事件上下文',
    is_active BOOLEAN DEFAULT true COMMENT '是否有效',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_event (user_id, event_type),
    INDEX idx_event_date (event_type, event_start_date),
    INDEX idx_lifecycle_stage (lifecycle_stage),
    INDEX idx_is_active (is_active)
) COMMENT '用户生命周期表（支持动态时间轴推荐）';

-- ============================================
-- 首页推荐四模块测试数据
-- ============================================

-- 1. 今日发现推荐数据（daily_discovery类型）
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, is_active, created_at, updated_at) VALUES
(NULL, 1, 2, 'daily_discovery', 0.95, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(NULL, 1, 3, 'daily_discovery', 0.88, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(NULL, 1, 4, 'daily_discovery', 0.85, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(NULL, 2, 1, 'daily_discovery', 0.92, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(NULL, 3, 4, 'daily_discovery', 0.90, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(NULL, 4, 3, 'daily_discovery', 0.87, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00');

-- 2. 生活场景推荐数据（morning/afternoon/evening场景）
INSERT INTO scenario_recommendations (
    user_id, scenario_type, time_slot, location_context, user_state, 
    weather_conditions, recommended_products, scenario_story,
    recommendation_title, recommendation_description, recommendation_metadata
) VALUES
-- 早晨场景
(NULL, 'morning', '07:00-09:00', '{"location": "home"}', 'relaxed', '{"weather": "sunny"}', '[1, 2]', '早晨时光，用科技产品开启美好一天',
'清晨好物推荐，开启美好一天', '为您精选清晨必备好物，让每个清晨都充满活力与期待。',
'{"style": "default", "ai_generated": true, "approval_status": "approved", "quality_score": 0.85, "usage_count": 0}'),

-- 下午场景
(NULL, 'afternoon', '14:00-16:00', '{"location": "office"}', 'working', '{"weather": "cloudy"}', '[3, 4]', '下午办公时间，提升工作效率的好物',
'下午办公好物推荐，提升工作效率', '为您精选适合下午办公使用的产品，提高工作效率和舒适度。',
'{"style": "professional", "ai_generated": true, "approval_status": "approved", "quality_score": 0.82, "usage_count": 0}'),

-- 晚上场景
(NULL, 'evening', '19:00-21:00', '{"location": "home"}', 'relaxed', '{"weather": "clear"}', '[5, 6]', '晚上休闲时光，放松身心的好物',
'晚间休闲好物推荐，放松身心', '为您精选适合晚间休闲娱乐的产品，让您放松身心享受美好时光。',
'{"style": "casual", "ai_generated": true, "approval_status": "approved", "quality_score": 0.80, "usage_count": 0}'),

-- 个性化场景（用户ID=1）
(1, 'morning', '07:00-09:00', '{"location": "home"}', 'energetic', '{"weather": "sunny"}', '[2, 3]', '个性化早晨推荐，根据您的喜好定制',
'个性化早晨好物推荐', '基于您的兴趣偏好，为您定制专属的早晨好物推荐。',
'{"style": "personalized", "ai_generated": true, "approval_status": "approved", "quality_score": 0.90, "usage_count": 0}');

-- 3. 社区热榜数据（销量统计）
INSERT INTO product_sales_stats (product_id, time_granularity, stat_date, `rank`, sales_count, sales_amount, view_count, favorite_count, avg_rating) VALUES
(1, 'daily', CURDATE(), 1, 150, 75000.00, 3000, 120, 4.8),
(2, 'daily', CURDATE(), 2, 120, 48000.00, 2500, 95, 4.7),
(3, 'daily', CURDATE(), 3, 100, 60000.00, 2200, 85, 4.6),
(4, 'daily', CURDATE(), 4, 90, 45000.00, 2000, 75, 4.5),
(5, 'daily', CURDATE(), 5, 80, 32000.00, 1800, 65, 4.4),
(6, 'daily', CURDATE(), 6, 70, 28000.00, 1600, 55, 4.3);

-- 4. 个性化发现流数据（用户ID=1的个性化推荐）
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, is_active, created_at, updated_at) VALUES
(1, 1, 2, 'personalized', 0.95, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 3, 'personalized', 0.92, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 4, 'personalized', 0.88, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 5, 'personalized', 0.85, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 6, 'personalized', 0.82, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 2, 1, 'personalized', 0.90, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 2, 3, 'personalized', 0.87, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 2, 4, 'personalized', 0.84, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00');

-- 5. 用户兴趣画像数据（用户ID=1）
INSERT INTO user_interest_profiles (user_id, interest_tags, behavior_patterns, discovery_preferences, trending_interests, last_updated, profile_version) VALUES
(1, '{"科技": 0.8, "运动": 0.6, "时尚": 0.4}', '{"浏览时段": "19:00-22:00", "点击偏好": "图片>文字"}', '{"新品偏好": "高", "价格敏感度": "中等"}', '{"热点事件": 0.9, "季节性": 0.7}', '2026-02-27 10:00:00', 1)
ON DUPLICATE KEY UPDATE 
interest_tags = VALUES(interest_tags),
behavior_patterns = VALUES(behavior_patterns),
discovery_preferences = VALUES(discovery_preferences),
trending_interests = VALUES(trending_interests),
last_updated = VALUES(last_updated),
profile_version = profile_version + 1;

COMMIT;

-- ============================================
-- 商品关系与推荐表初始数据
-- ============================================

-- 插入统一推荐数据（合并原相关商品和推荐数据）
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, is_active, created_at, updated_at) VALUES
(NULL, 1, 2, 'complementary', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'similar', 0.7, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'bundle', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'collaborative', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'content_based', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'popular', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'trending', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'personalized', 0.79, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'new_arrival', 0.83, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'limited_time', 0.86, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 1, 'complementary', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 4, 'similar', 0.6, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00');

-- 插入内容推荐数据（支持每日发现功能）
INSERT INTO content_recommendations (user_id, content_id, recommendation_type, recommendation_score, is_active) VALUES
(NULL, 1, 'daily_discovery', 0.9, true),
(NULL, 2, 'daily_discovery', 0.85, true),
(NULL, 3, 'trending', 0.8, true),
(NULL, 4, 'personalized', 0.75, true),
(1001, 1, 'personalized', 0.95, true),
(1002, 2, 'related', 0.7, true),
(1003, 1, 'personalized', 0.88, true),
(1004, 1, 'daily_discovery', 0.92, true),
(1005, 1, 'trending', 0.85, true);

-- 插入商品知识图谱数据（AI生成的关系数据）
INSERT INTO product_knowledge_graph (product_id, related_product_id, relationship_type, relationship_strength, context_description, confidence_score, is_active) VALUES
(1, 2, 'solution', 0.85, '购买手机后需要手机壳保护', 0.9, true),
(1, 3, 'complementary', 0.75, '手机和耳机是常用搭配', 0.8, true),
(2, 4, 'prerequisite', 0.9, '购买相机前需要学习摄影知识', 0.85, true),
(3, 5, 'alternative', 0.6, '不同品牌的耳机可以互相替代', 0.7, true);

-- 插入用户生命周期事件数据（AI预测的用户阶段）
INSERT INTO user_lifecycle_events (user_id, event_type, event_start_date, event_end_date, predicted_next_event, confidence_score, lifecycle_stage, event_context, is_active) VALUES
(1001, 'pregnancy', '2026-01-01', '2026-10-01', 'new_parent', 0.85, 'family_planning', '{"trimester": 2, "due_date": "2026-10-01"}', true),
(1002, 'career_change', '2026-02-01', '2026-05-01', 'relocation', 0.75, 'professional_development', '{"industry": "tech", "position": "senior"}', true),
(1003, 'education', '2026-03-01', '2026-06-30', 'career_change', 0.8, 'student_life', '{"major": "computer_science", "graduation_date": "2026-06-30"}', true);

-- 插入统一推荐数据（续）
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, is_active, created_at, updated_at) VALUES
(NULL, 2, 5, 'content_based', 0.65, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 1, 'similar', 0.7, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 4, 'complementary', 0.9, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 5, 'collaborative', 0.72, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 4, 1, 'collaborative', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 4, 3, 'popular', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 4, 5, 'trending', 0.68, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
-- 每日发现特色推荐
(NULL, 1, 2, 'daily_discovery', 0.95, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'daily_discovery', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'daily_discovery', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 1, 'daily_discovery', 0.92, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 4, 'new_arrival', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 4, 3, 'limited_time', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
-- 个性化推荐数据（模拟不同用户场景）
(1001, 1, 2, 'personalized', 0.92, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1001, 1, 3, 'personalized', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1001, 1, 4, 'personalized', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1002, 1, 2, 'personalized', 0.89, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1002, 1, 3, 'personalized', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1003, 1, 2, 'personalized', 0.91, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1003, 1, 4, 'personalized', 0.87, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1004, 1, 3, 'personalized', 0.84, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1004, 1, 4, 'personalized', 0.79, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1005, 1, 2, 'personalized', 0.93, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(1005, 1, 3, 'personalized', 0.86, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00');

-- 插入销量统计数据（单一表设计）
INSERT INTO product_sales_stats (product_id, time_granularity, stat_date, `rank`, sales_count, sales_amount, sales_growth_rate, view_count, favorite_count, share_count, cart_count, avg_rating, review_count, return_count) VALUES
-- 日粒度数据
(1, 'daily', '2026-02-01', 1, 25, 7475.00, 25.5, 500, 45, 15, 30, 4.5, 20, 1),
(2, 'daily', '2026-02-01', 2, 18, 3582.00, 18.3, 450, 35, 8, 25, 4.8, 15, 0),
(3, 'daily', '2026-02-01', 3, 12, 71988.00, 30.1, 600, 50, 5, 35, 4.2, 8, 1),
(4, 'daily', '2026-02-01', 4, 8, 39992.00, 15.7, 400, 30, 20, 20, 4.7, 12, 0),

-- 月粒度数据
(1, 'monthly', '2026-02-01', 1, 80, 23920.00, 15.2, 2500, 120, 45, 80, 4.5, 80, 4),
(2, 'monthly', '2026-02-01', 2, 60, 11940.00, 12.8, 2000, 100, 24, 70, 4.8, 60, 2),
(3, 'monthly', '2026-02-01', 3, 40, 239952.00, 18.5, 8000, 400, 15, 300, 4.2, 32, 3),
(4, 'monthly', '2026-02-01', 4, 30, 149976.00, 14.3, 6000, 300, 60, 200, 4.7, 48, 1),

-- 年粒度数据
(1, 'yearly', '2026-01-01', 1, 500, 149500.00, 20.5, 15000, 800, 180, 500, 4.5, 400, 20),
(2, 'yearly', '2026-01-01', 2, 350, 69650.00, 18.2, 12000, 600, 96, 400, 4.8, 280, 10),
(3, 'yearly', '2026-01-01', 3, 250, 1499760.00, 15.8, 50000, 2500, 60, 1800, 4.2, 200, 15),
(4, 'yearly', '2026-01-01', 4, 200, 999840.00, 13.5, 40000, 2000, 240, 1500, 4.7, 240, 8);



-- 插入商品搜索关键词数据
INSERT INTO product_search_keywords (keyword, search_count, click_count, conversion_count, last_searched_at, is_trending, is_recommended) VALUES
('智能手表', 1500, 800, 120, '2026-02-01 15:30:00', true, true),
('无线耳机', 1200, 650, 95, '2026-02-01 14:20:00', true, true),
('笔记本电脑', 1800, 900, 150, '2026-02-01 16:45:00', true, false),
('智能手机', 2000, 1000, 180, '2026-02-01 17:10:00', true, false),
('男士衬衫', 800, 400, 60, '2026-02-01 11:30:00', false, false);

-- 插入用户行为日志核心数据
INSERT INTO user_behavior_logs_core (user_id, product_id, behavior_type, behavior_weight, session_id) VALUES
(1001, 1, 'view', 1.0, 'session001'),
(1001, 1, 'favorite', 1.5, 'session001'),
(1001, 1, 'share', 1.2, 'session001'),
(1002, 2, 'view', 1.0, 'session002'),
(1002, 2, 'cart', 2.0, 'session002'),
(1002, 2, 'share', 1.2, 'session002'),
(1003, 3, 'view', 1.0, 'session003'),
(1003, 3, 'purchase', 3.0, 'session003'),
(1003, 3, 'share', 1.2, 'session003'),
(1001, 4, 'view', 1.0, 'session001'),
(1001, 4, 'share', 1.2, 'session001'),
(1002, 1, 'share', 1.2, 'session002'),
(1003, 2, 'favorite', 1.5, 'session003'),
(1003, 4, 'cart', 2.0, 'session003');

-- 插入用户行为日志详情数据（与核心表ID对应）
INSERT INTO user_behavior_logs_details (id, referrer_url, behavior_context) VALUES
(1, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop', '{"device": "mobile", "location": "北京"}'),
(2, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop', '{"device": "mobile", "location": "北京"}'),
(3, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop', '{"device": "mobile", "location": "北京", "sharing_type": "wechat"}'),
(4, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', '{"device": "desktop", "location": "上海"}'),
(5, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', '{"device": "desktop", "location": "上海"}'),
(6, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', '{"device": "desktop", "location": "上海", "sharing_type": "weibo"}'),
(7, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', '{"device": "tablet", "location": "广州"}'),
(8, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', '{"device": "tablet", "location": "广州"}'),
(9, 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop', '{"device": "tablet", "location": "广州", "sharing_type": "link"}'),
(10, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '{"device": "mobile", "location": "北京"}'),
(11, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '{"device": "mobile", "location": "北京", "sharing_type": "wechat"}'),
(12, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop', '{"device": "desktop", "location": "上海", "sharing_type": "qq"}'),
(13, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop', '{"device": "tablet", "location": "广州"}'),
(14, 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop', '{"device": "tablet", "location": "广州"}');

-- 插入推荐效果追踪数据
INSERT INTO recommendation_effects (recommendation_id, user_id, impression_count, click_count, conversion_count, last_impressed_at, last_clicked_at) VALUES
(1, 1001, 5, 2, 1, '2026-02-01 10:30:00', '2026-02-01 10:35:00'),
(2, 1002, 3, 1, 0, '2026-02-01 14:20:00', '2026-02-01 14:25:00'),
(3, 1003, 8, 3, 2, '2026-02-01 16:45:00', '2026-02-01 16:50:00');

-- 插入商品详情页通用场景推荐语
INSERT INTO scenario_recommendations (
    user_id, scenario_type, recommendation_title, recommendation_description, 
    recommendation_metadata
) VALUES
-- 商品详情页通用推荐语（scenario_type = 'product_detail'）
(NULL, 'product_detail', '智能手表推荐', '这款智能手表功能强大，适合运动健康监测，续航时间长，设计时尚。', '{"style": "default", "ai_generated": true, "approval_status": "approved", "quality_score": 0.85, "usage_count": 0}'),
(NULL, 'product_detail', '无线耳机推荐', '降噪效果出色，音质清晰通透，佩戴舒适，适合通勤和运动使用。', '{"style": "default", "ai_generated": true, "approval_status": "approved", "quality_score": 0.82, "usage_count": 0}'),
(NULL, 'product_detail', '笔记本电脑推荐', '轻薄便携，性能强劲，适合办公和学习使用，续航能力优秀。', '{"style": "professional", "ai_generated": true, "approval_status": "approved", "quality_score": 0.88, "usage_count": 0}'),
(NULL, 'product_detail', '智能手机推荐', '拍照效果出色，运行流畅，屏幕显示清晰，性价比高。', '{"style": "default", "ai_generated": true, "approval_status": "approved", "quality_score": 0.83, "usage_count": 0}'),
(NULL, 'product_detail', '男士衬衫推荐', '面料舒适，版型修身，适合商务和休闲场合，品质保证。', '{"style": "casual", "ai_generated": true, "approval_status": "approved", "quality_score": 0.79, "usage_count": 0}');

-- 插入场景推荐数据（包含通用模板和个性化推荐）
INSERT INTO scenario_recommendations (
    user_id, scenario_type, time_slot, location_context, user_state, 
    weather_conditions, recommended_products, scenario_story,
    recommendation_title, recommendation_description, recommendation_metadata
) VALUES
-- 通用模板（user_id为NULL）
(NULL, 'morning', '07:00-09:00', '{"location": "home"}', 'relaxed', '{"weather": "sunny"}', '[1, 2]', '早晨时光，用科技产品开启美好一天。智能手表监测健康，无线耳机享受音乐',
'清晨好物推荐，开启美好一天', '为您精选清晨必备好物，从早餐用具到个人护理，让每个清晨都充满活力与期待。',
'{"style": "default", "ai_generated": true, "approval_status": "approved", "quality_score": 0.8, "usage_count": 0}'),
(NULL, 'commute', '08:00-09:00', '{"location": "subway"}', 'focused', '{"weather": "comfortable"}', '[2, 5]', '通勤路上，降噪耳机隔绝嘈杂，便携充电宝随时续航',
'通勤必备神器，让路途更轻松', '精选通勤路上实用好物，降噪耳机、便携充电宝等，让您的通勤时光更加舒适高效。',
'{"style": "professional", "ai_generated": true, "approval_status": "approved", "quality_score": 0.75, "usage_count": 0}'),

-- 个性化推荐（关联具体用户）
(1001, 'morning', '07:00-09:00', '{"location": "home"}', 'relaxed', '{"weather": "sunny"}', '[1, 3]', '科技爱好者专属：智能手表追踪健康数据，搭配最新智能家居设备开启高效一天',
'科技达人专属，开启智能生活', '基于您的科技偏好，为您精选智能手表和智能家居设备，打造高效智能生活体验。',
'{"style": "creative", "ai_generated": true, "approval_status": "approved", "quality_score": 0.85, "usage_count": 0}'),
(1002, 'commute', '08:00-09:00', '{"location": "subway"}', 'focused', '{"weather": "comfortable"}', '[2, 8]', '时尚达人通勤：降噪耳机享受高品质音乐，搭配时尚背包展现个性',
'时尚通勤装备，展现个性魅力', '结合您的时尚品味，精选降噪耳机与时尚背包，让通勤路上也能展现独特风格。',
'{"style": "casual", "ai_generated": true, "approval_status": "approved", "quality_score": 0.82, "usage_count": 0}'),
(1003, 'work', '09:00-12:00', '{"location": "office"}', 'focused', '{"weather": "indoor"}', '[3, 9]', '办公达人必备：轻薄笔记本高效办公，搭配人体工学椅保护健康',
'高效办公装备，提升工作效率', '针对办公场景需求，精选轻薄笔记本和人体工学椅，助您高效工作同时保护健康。',
'{"style": "professional", "ai_generated": true, "approval_status": "approved", "quality_score": 0.78, "usage_count": 0}'),
(1001, 'evening', '19:00-22:00', '{"location": "home"}', 'relaxed', '{"weather": "evening"}', '[4, 10]', '科技宅晚间：智能手机娱乐放松，智能家居打造舒适环境',
'晚间放松时光，享受科技生活', '结束一天忙碌，用智能手机和智能家居设备打造舒适放松的晚间时光。',
'{"style": "casual", "ai_generated": true, "approval_status": "approved", "quality_score": 0.7, "usage_count": 0}'),

-- 插入场景推荐数据（基于用户场景的推荐）
(1001, 'fitness', NULL, NULL, NULL, NULL, '[1, 2, 4]', '运动爱好者专属：智能手表追踪运动数据，无线耳机享受运动音乐，智能手机记录运动轨迹',
'运动装备推荐，助力健康生活', '基于您的运动偏好，为您精选智能手表、无线耳机和智能手机，让运动更加科学有趣。',
'{"style": "active", "ai_generated": true, "approval_status": "approved", "quality_score": 0.87, "usage_count": 0}'),
(1002, 'work', NULL, NULL, NULL, NULL, '[3, 1, 4]', '职场精英必备：笔记本电脑高效办公，智能手表管理时间，智能手机沟通协作',
'职场装备推荐，提升工作效率', '针对职场需求，精选笔记本电脑、智能手表和智能手机，助您高效工作展现专业能力。',
'{"style": "professional", "ai_generated": true, "approval_status": "approved", "quality_score": 0.83, "usage_count": 0}'),
(1003, 'travel', NULL, NULL, NULL, NULL, '[2, 1, 4]', '旅行达人必备：无线耳机享受旅途音乐，智能手表导航定位，智能手机记录美好瞬间',
'旅行装备推荐，让旅途更精彩', '结合旅行场景需求，精选无线耳机、智能手表和智能手机，让您的旅行更加舒适便捷。',
'{"style": "casual", "ai_generated": true, "approval_status": "approved", "quality_score": 0.79, "usage_count": 0}'),
(1004, 'gaming', NULL, NULL, NULL, NULL, '[4, 1, 2]', '游戏玩家专属：智能手机畅玩游戏，智能手表监测游戏状态，无线耳机沉浸体验',
'游戏装备推荐，畅享游戏乐趣', '针对游戏爱好者，精选智能手机、智能手表和无线耳机，提供沉浸式游戏体验。',
'{"style": "creative", "ai_generated": true, "approval_status": "approved", "quality_score": 0.85, "usage_count": 0}'),
(1005, 'health', NULL, NULL, NULL, NULL, '[1, 2, 4]', '健康管理必备：智能手表监测健康指标，无线耳机放松身心，智能手机管理健康数据',
'健康装备推荐，关爱身心健康', '基于健康管理需求，精选智能手表、无线耳机和智能手机，助您科学管理健康生活。',
'{"style": "health", "ai_generated": true, "approval_status": "approved", "quality_score": 0.82, "usage_count": 0}');

-- 插入用户兴趣画像数据
INSERT INTO user_interest_profiles (user_id, interest_tags, behavior_patterns, discovery_preferences, trending_interests) VALUES
(1001, '{"科技": 0.8, "运动": 0.6, "健康": 0.7}', '{"浏览时段": "19:00-22:00", "设备偏好": "手机"}', '{"新品偏好": "高", "价格敏感度": "低"}', '{"热点事件": 0.9}'),
(1002, '{"音频": 0.9, "时尚": 0.5, "旅行": 0.6}', '{"浏览时段": "12:00-14:00", "点击偏好": "视频>图片"}', '{"新品偏好": "中等", "品牌忠诚度": "高"}', '{"季节性": 0.7}'),
(1003, '{"办公": 0.8, "游戏": 0.7, "摄影": 0.6}', '{"浏览时段": "20:00-23:00", "购买决策": "详细比较"}', '{"新品偏好": "低", "性价比优先": "是"}', '{"技术更新": 0.8}');

-- ============================================
-- 增加更多测试数据，确保每个查询都能返回2条以上的数据
-- ============================================

-- 为商品1（智能手表）增加更多相似推荐数据
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, is_active, created_at, updated_at) VALUES
(NULL, 1, 3, 'similar', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'similar', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 5, 'similar', 0.72, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00');

-- 为商品1（智能手表）增加更多价格敏感推荐数据
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, is_active, created_at, updated_at) VALUES
(NULL, 1, 2, 'price_sensitive', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'price_sensitive', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'price_sensitive', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00');

-- 为商品1（智能手表）增加更多知识图谱数据（用于互补推荐）
INSERT INTO product_knowledge_graph (product_id, related_product_id, relationship_type, relationship_strength, context_description, confidence_score, is_active) VALUES
(1, 2, 'complementary', 0.85, '智能手表与无线耳机是完美搭配', 0.9, true),
(1, 3, 'complementary', 0.78, '智能手表与笔记本电脑协同工作', 0.85, true),
(1, 4, 'complementary', 0.72, '智能手表与智能手机数据同步', 0.8, true),
(1, 5, 'complementary', 0.68, '智能手表与运动耳机健身组合', 0.75, true);

-- 为其他商品也增加推荐数据，确保系统完整性
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, is_active, created_at, updated_at) VALUES
(NULL, 2, 1, 'similar', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 3, 'similar', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 4, 'similar', 0.70, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 1, 'similar', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 2, 'similar', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 4, 'similar', 0.72, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00');

COMMIT;