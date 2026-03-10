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
    recommendation_reason VARCHAR(200) COMMENT '推荐理由',
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
    INDEX idx_user_type_active (user_id, recommendation_type, is_active) COMMENT '个性化推荐查询优化',
    
    -- 唯一约束：防止同一用户对同一商品的重复推荐
    UNIQUE KEY uk_user_product_type (user_id, recommended_product_id, recommendation_type) COMMENT '用户商品类型唯一约束'
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

-- 场景推荐表（基于时间、日期、节日的简化设计）
CREATE TABLE IF NOT EXISTS scenario_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 用户关联（关键改进：支持个性化）
    user_id BIGINT COMMENT '用户ID（NULL表示通用模板）',
    
    -- 时间维度（核心推荐维度）
    time_period VARCHAR(20) NOT NULL COMMENT '时间段：morning(早晨)/afternoon(下午)/evening(晚上)/night(深夜)',
    day_type VARCHAR(20) NOT NULL COMMENT '日期类型：weekday(工作日)/weekend(周末)/holiday(节假日)',
    season_type VARCHAR(20) NOT NULL COMMENT '季节类型：spring(春季)/summer(夏季)/autumn(秋季)/winter(冬季)',
    
    -- 推荐内容
    recommended_products JSON NOT NULL COMMENT '推荐商品ID列表',
    
    -- 推荐语核心内容
    recommendation_title VARCHAR(200) NOT NULL COMMENT '推荐标题',
    recommendation_description TEXT NOT NULL COMMENT '推荐描述',
    
    -- 管理字段
    approval_status VARCHAR(20) DEFAULT 'pending' COMMENT '审核状态：pending(待审核)/approved(已通过)/rejected(已拒绝)',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 优化索引
    INDEX idx_user_time_day (user_id, time_period, day_type) COMMENT '用户时间日期关联查询',
    INDEX idx_time_day_season (time_period, day_type, season_type) COMMENT '时间日期季节组合查询',
    INDEX idx_approval_status (approval_status) COMMENT '审核状态查询'
) COMMENT '场景推荐表（基于时间、日期、节日的简化设计）';

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

-- ========== 个性化推荐（personalized） ==========
(1, 1, 2, 'personalized', 0.95, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 3, 'personalized', 0.92, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 4, 'personalized', 0.88, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 5, 'personalized', 0.85, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 1, 6, 'personalized', 0.82, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),
(1, 2, 1, 'personalized', 0.90, true, '2026-02-27 10:00:00', '2026-02-27 10:00:00'),

-- ========== 相似商品推荐（similar） ==========
(NULL, 1, 2, 'complementary', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'similar', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'similar', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'price_sensitive', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'price_sensitive', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'price_sensitive', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 5, 'price_sensitive', 0.7, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 6, 'price_sensitive', 0.65, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 7, 'price_sensitive', 0.6, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 8, 'price_sensitive', 0.55, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 9, 'price_sensitive', 0.5, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 10, 'price_sensitive', 0.45, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 11, 'price_sensitive', 0.4, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 12, 'price_sensitive', 0.35, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 13, 'price_sensitive', 0.3, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 14, 'price_sensitive', 0.25, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 15, 'price_sensitive', 0.2, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 1, 'similar', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 3, 'similar', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 4, 'similar', 0.70, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 1, 'similar', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 2, 'similar', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 4, 'similar', 0.72, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),

-- ========== 其他推荐类型 ==========
(NULL, 2, 1, 'complementary', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 3, 'complementary', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 4, 'complementary', 0.7, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 1, 'complementary', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 2, 'complementary', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 4, 'complementary', 0.7, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'bundle', 0.9, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'bundle', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'bundle', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'collaborative', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'collaborative', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'collaborative', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'content_based', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'content_based', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'content_based', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'popular', 0.9, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'popular', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'popular', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'trending', 0.88, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'trending', 0.82, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'trending', 0.78, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'new_arrival', 0.92, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'new_arrival', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'new_arrival', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'limited_time', 0.9, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'limited_time', 0.85, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'limited_time', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 5, 'content_based', 0.65, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 2, 5, 'content_based', 0.65, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 1, 'similar', 0.7, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 3, 1, 'collaborative', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 2, 'collaborative', 0.8, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 3, 'collaborative', 0.75, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 4, 'collaborative', 0.7, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 5, 'collaborative', 0.65, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 6, 'collaborative', 0.6, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 7, 'collaborative', 0.55, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 8, 'collaborative', 0.5, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 9, 'collaborative', 0.45, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00'),
(NULL, 1, 10, 'collaborative', 0.4, true, '2026-02-01 10:00:00', '2026-02-01 10:00:00');

-- 插入场景推荐数据（基于时间、日期、节日的简化设计）
INSERT INTO scenario_recommendations (
    user_id, time_period, day_type, season_type, 
    recommended_products, recommendation_title, recommendation_description, approval_status
) VALUES
-- ==================== 通用场景推荐（user_id为NULL） ====================
-- 工作日早晨推荐（通勤场景）
(NULL, 'morning', 'weekday', 'spring', '[1, 2, 3]', 
'通勤必备神器，让路途更轻松', 
'精选通勤路上实用好物，降噪耳机、便携充电宝等，让您的通勤时光更加舒适高效。', 
'approved'),

-- 工作日午后推荐（办公场景）
(NULL, 'afternoon', 'weekday', 'spring', '[4, 5, 6]', 
'高效办公装备，提升工作效率', 
'针对办公场景需求，精选轻薄笔记本和人体工学椅，助您高效工作同时保护健康。', 
'approved'),

-- 工作日晚上推荐（学习场景）
(NULL, 'evening', 'weekday', 'spring', '[7, 8, 9]', 
'晚间学习装备，专注提升自我', 
'为您精选适合晚间学习的设备，帮助您专注学习，提升个人能力。', 
'approved'),

-- 周末早晨推荐（休闲场景）
(NULL, 'morning', 'weekend', 'spring', '[10, 11, 12]', 
'周末休闲好物，享受慢生活', 
'精选适合周末休闲的产品，让您在忙碌一周后享受轻松愉快的周末时光。', 
'approved'),

-- 周末午后推荐（娱乐场景）
(NULL, 'afternoon', 'weekend', 'spring', '[13, 14, 15]', 
'午后娱乐装备，放松身心', 
'为您精选适合午后娱乐的产品，游戏设备、影音装备等，让您的周末更加丰富多彩。', 
'approved'),

-- 周末晚上推荐（家庭场景）
(NULL, 'evening', 'weekend', 'spring', '[16, 17, 18]', 
'家庭娱乐好物，共享温馨时光', 
'精选适合家庭娱乐的产品，让您与家人共享温馨愉快的周末夜晚。', 
'approved'),

-- 节假日推荐（特殊场景）
(NULL, 'morning', 'holiday', 'spring', '[19, 20, 21]', 
'节日好物推荐，增添节日氛围', 
'为您精选适合节日的特色产品，增添节日氛围，让您的假期更加难忘。', 
'approved'),

-- 夏季周末推荐（季节性场景）
(NULL, 'afternoon', 'weekend', 'summer', '[22, 23, 24]', 
'夏日清凉装备，清爽过夏天', 
'精选夏季必备好物，清凉设备、防晒用品等，让您清爽舒适度过炎炎夏日。', 
'approved'),

-- 冬季工作日推荐（季节性场景）
(NULL, 'evening', 'weekday', 'winter', '[25, 26, 27]', 
'冬日温暖好物，抵御寒冷', 
'为您精选冬季保暖产品，取暖设备、保暖衣物等，让您在寒冷冬日也能温暖舒适。', 
'approved'),

-- ==================== 个性化场景推荐（用户ID=1） ====================
-- 个性化早晨推荐
(1, 'morning', 'weekday', 'spring', '[2, 3, 4]', 
'个性化早晨好物推荐', 
'基于您的兴趣偏好，为您定制专属的早晨好物推荐，助您开启美好一天。', 
'approved'),

-- 个性化周末推荐
(1, 'afternoon', 'weekend', 'spring', '[5, 6, 7]', 
'个性化周末休闲推荐', 
'根据您的兴趣爱好，为您精选适合周末休闲的个性化产品推荐。', 
'approved'),

-- 个性化晚间推荐
(1, 'evening', 'weekday', 'spring', '[8, 9, 10]', 
'个性化晚间娱乐推荐', 
'基于您的娱乐偏好，为您定制晚间娱乐装备推荐，让您的夜晚更加精彩。', 
'approved');

-- 3. 社区热榜数据（销量统计）
INSERT INTO product_sales_stats (product_id, time_granularity, stat_date, `rank`, sales_count, sales_amount, view_count, favorite_count, avg_rating) VALUES
(1, 'daily', CURDATE(), 1, 150, 75000.00, 3000, 120, 4.8),
(2, 'daily', CURDATE(), 2, 120, 48000.00, 2500, 95, 4.7),
(3, 'daily', CURDATE(), 3, 100, 60000.00, 2200, 85, 4.6),
(4, 'daily', CURDATE(), 4, 90, 45000.00, 2000, 75, 4.5),
(5, 'daily', CURDATE(), 5, 80, 32000.00, 1800, 65, 4.4),
(6, 'daily', CURDATE(), 6, 70, 28000.00, 1600, 55, 4.3);



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

-- ============================================
-- 为user_id 4添加个性化推荐数据
-- ============================================

-- 为user_id 4添加个性化推荐
INSERT INTO product_recommendations (user_id, product_id, recommended_product_id, recommendation_type, recommendation_score, recommendation_reason, is_active, created_at, updated_at) VALUES
-- 基于用户4的购物车商品进行个性化推荐
(4, 16, 17, 'personalized', 0.92, '智能家居爱好者可能也喜欢便携投影仪', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 16, 18, 'personalized', 0.88, '科技产品爱好者可能对机械键盘感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 16, 19, 'personalized', 0.85, '关注健康生活的用户可能喜欢体脂秤', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 17, 20, 'personalized', 0.90, '影音娱乐用户可能对降噪睡眠耳机感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 18, 21, 'personalized', 0.87, '办公设备用户可能对厨房电器感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 19, 22, 'personalized', 0.83, '健康生活用户可能对便携咖啡机感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 20, 23, 'personalized', 0.89, '睡眠改善用户可能对健身器材感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 21, 24, 'personalized', 0.86, '厨房电器用户可能对个人护理产品感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 22, 25, 'personalized', 0.91, '便携设备用户可能对充电宝感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00'),
(4, 23, 16, 'personalized', 0.84, '健身用户可能对智能家居产品感兴趣', true, '2026-03-09 10:00:00', '2026-03-09 10:00:00');

-- 更多个性化推荐


-- 为user_id 4创建用户兴趣画像
INSERT INTO user_interest_profiles (user_id, interest_tags, behavior_patterns, discovery_preferences, trending_interests, last_updated, profile_version) VALUES
(4, '["智能家居", "影音娱乐", "健康生活", "办公设备", "个人护理"]', 
 '{"browse_frequency": "daily", "purchase_preference": "tech_products", "price_sensitivity": "medium", "brand_loyalty": "moderate"}', 
 '{"preferred_categories": [8, 9, 10, 11, 12], "discovery_intensity": "high", "new_product_interest": "very_high"}', 
 '["智能家居", "便携设备", "健康监测", "无线技术"]', 
 '2026-03-09 10:00:00', '2.0');

-- 为user_id 4添加用户行为日志
INSERT INTO user_behavior_logs_core (user_id, product_id, behavior_type, behavior_weight, session_id) VALUES
(4, 16, 'view', 1.0, 'session_4_001'),
(4, 17, 'view', 1.0, 'session_4_001'),
(4, 18, 'view', 1.0, 'session_4_001'),
(4, 19, 'view', 1.0, 'session_4_001'),
(4, 20, 'view', 1.0, 'session_4_001'),
(4, 16, 'add_to_cart', 2.0, 'session_4_001'),
(4, 17, 'add_to_cart', 2.0, 'session_4_001'),
(4, 18, 'add_to_cart', 2.0, 'session_4_001'),
(4, 19, 'add_to_cart', 2.0, 'session_4_001'),
(4, 20, 'add_to_cart', 2.0, 'session_4_001'),

-- 更多行为数据
(4, 21, 'view', 1.0, 'session_4_002'),
(4, 22, 'view', 1.0, 'session_4_002'),
(4, 23, 'view', 1.0, 'session_4_002'),
(4, 24, 'view', 1.0, 'session_4_002'),
(4, 25, 'view', 1.0, 'session_4_002'),
(4, 21, 'add_to_cart', 2.0, 'session_4_002'),
(4, 22, 'add_to_cart', 2.0, 'session_4_002'),
(4, 23, 'add_to_cart', 2.0, 'session_4_002'),
(4, 24, 'add_to_cart', 2.0, 'session_4_002'),
(4, 25, 'add_to_cart', 2.0, 'session_4_002');

