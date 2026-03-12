-- ============================================
-- 用户相关表结构
-- 创建时间: 2026-03-12
-- 业务模块: 用户管理与推荐
-- ============================================

USE daily_discover;
-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_groups;
DROP TABLE IF EXISTS user_group_memberships;
DROP TABLE IF EXISTS user_interest_profiles;
DROP TABLE IF EXISTS intent_click_statistics;
DROP TABLE IF EXISTS user_behavior_logs_core;
DROP TABLE IF EXISTS user_behavior_logs_details;

-- 用户群体分类表（MVP简化版）
CREATE TABLE IF NOT EXISTS user_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '群体ID',
    group_name VARCHAR(100) NOT NULL COMMENT '群体名称',
    group_type VARCHAR(50) NOT NULL COMMENT '群体类型：price_sensitive-价格敏感, quality_focused-品质追求, trend_follower-潮流追随, family_user-家庭用户, student-学生群体, professional-职场人士, senior-中老年群体',
    group_description TEXT COMMENT '群体描述',
    
    -- 群体特征标签（MVP简化）
    characteristic_tags JSON COMMENT '特征标签：{"price_sensitivity": "high", "brand_preference": "strong", "category_preference": "electronics"}',
    
    -- 管理字段
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_group_type (group_type) COMMENT '群体类型查询',
    INDEX idx_is_active (is_active) COMMENT '启用状态查询',
    UNIQUE KEY uk_group_name (group_name) COMMENT '群体名称唯一约束'
) COMMENT '用户群体分类表';

-- 用户群体关联表（MVP简化版）
CREATE TABLE IF NOT EXISTS user_group_memberships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    group_id BIGINT NOT NULL COMMENT '群体ID',
    
    -- 关联权重（用户属于该群体的置信度）
    membership_weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '关联权重（0.0~1.0）',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_user_group (user_id, group_id) COMMENT '用户群体关联查询',
    INDEX idx_group_user (group_id, user_id) COMMENT '群体用户查询',
    INDEX idx_membership_weight (membership_weight) COMMENT '关联权重排序',
    
    -- 唯一约束：避免重复关联
    UNIQUE KEY uk_user_group (user_id, group_id) COMMENT '用户群体唯一约束'
) COMMENT '用户群体关联表';


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


-- 意图点击统计表（记录点击转化情况）
CREATE TABLE IF NOT EXISTS intent_click_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    intent_preference_id BIGINT NOT NULL COMMENT '关联用户意图偏好ID',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    conversion_count INT DEFAULT 0 COMMENT '转化次数（如购买、收藏等）',
    conversion_rate DOUBLE DEFAULT 0.0 COMMENT '转化率',
    avg_session_duration DOUBLE DEFAULT 0.0 COMMENT '平均会话时长（秒）',
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引优化
    INDEX idx_intent_preference (intent_preference_id) COMMENT '意图偏好查询',
    INDEX idx_conversion_rate (conversion_rate) COMMENT '转化率查询',
    UNIQUE KEY uk_intent_preference (intent_preference_id) COMMENT '意图偏好唯一约束'
) COMMENT '意图点击统计表';

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

-- ============================================
-- 用户相关测试数据
-- ============================================

-- 插入用户群体测试数据
INSERT INTO user_groups (group_name, group_type, group_description, characteristic_tags, member_count) VALUES
('价格敏感用户', 'price_sensitive', '关注价格优惠，喜欢性价比高的商品', '{"price_sensitivity": "高", "brand_preference": "中等", "category_preference": "生活用品"}', 500),
('品质追求用户', 'quality_focused', '注重商品品质和品牌，愿意为质量买单', '{"price_sensitivity": "低", "brand_preference": "强", "category_preference": "电子产品"}', 300),
('潮流追随用户', 'trend_follower', '关注时尚潮流，喜欢新品和热门商品', '{"price_sensitivity": "中等", "brand_preference": "强", "category_preference": "服装配饰"}', 400),
('家庭用户', 'family_user', '关注家庭生活用品，注重实用性和安全性', '{"price_sensitivity": "中等", "brand_preference": "中等", "category_preference": "家居用品"}', 600),
('学生群体', 'student', '预算有限，关注性价比和实用性', '{"price_sensitivity": "高", "brand_preference": "中等", "category_preference": "学习用品"}', 200),
('职场人士', 'professional', '注重品质和形象，关注商务和办公用品', '{"price_sensitivity": "低", "brand_preference": "强", "category_preference": "办公用品"}', 350),
('中老年群体', 'senior', '注重实用性和健康，关注养生和家居用品', '{"price_sensitivity": "中等", "brand_preference": "中等", "category_preference": "健康养生"}', 250);

-- 插入用户兴趣画像测试数据
INSERT INTO user_interest_profiles (user_id, interest_tags, behavior_patterns, discovery_preferences, trending_interests, last_updated, profile_version) VALUES
(1, '{"科技": 0.8, "运动": 0.6, "时尚": 0.4}', '{"浏览时段": "19:00-22:00", "点击偏好": "图片>文字"}', '{"新品偏好": "高", "价格敏感度": "中等"}', '{"热点事件": 0.9, "季节性": 0.7}', '2026-02-27 10:00:00', 1)
ON DUPLICATE KEY UPDATE 
interest_tags = VALUES(interest_tags),
behavior_patterns = VALUES(behavior_patterns),
discovery_preferences = VALUES(discovery_preferences),
trending_interests = VALUES(trending_interests),
last_updated = VALUES(last_updated),
profile_version = profile_version + 1;

-- 插入用户兴趣画像测试数据（用户ID=4）
INSERT INTO user_interest_profiles (user_id, interest_tags, behavior_patterns, discovery_preferences, trending_interests, last_updated, profile_version) VALUES
(4, '["智能家居", "影音娱乐", "健康生活", "办公设备", "个人护理"]', 
 '{"browse_frequency": "daily", "purchase_preference": "tech_products", "price_sensitivity": "medium", "brand_loyalty": "moderate"}', 
 '{"preferred_categories": [8, 9, 10, 11, 12], "discovery_intensity": "high", "new_product_interest": "very_high"}', 
 '["智能家居", "便携设备", "健康监测", "无线技术"]', 
 '2026-03-09 10:00:00', '2.0');


-- 插入意图点击统计测试数据
INSERT INTO intent_click_statistics (intent_preference_id, click_count, conversion_count, conversion_rate, avg_session_duration) VALUES
-- 便宜意图的统计数据
(1, 150, 45, 0.3, 120.5),
(5, 300, 75, 0.25, 110.2),
-- 高质量意图的统计数据
(2, 120, 36, 0.3, 135.8),
(6, 200, 50, 0.25, 125.3),
-- 新品意图的统计数据
(3, 80, 20, 0.25, 140.2),
(7, 150, 30, 0.2, 130.5),
-- 热门意图的统计数据
(4, 200, 60, 0.3, 115.7),
(8, 400, 100, 0.25, 105.8);


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
