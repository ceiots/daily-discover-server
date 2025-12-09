-- 用户核心业务表迁移脚本
-- 创建用户基础信息、行为数据等核心业务表

-- 使用数据库
USE daily_discover;

-- 1. 用户等级配置表
CREATE TABLE IF NOT EXISTS user_levels (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level_name VARCHAR(50) NOT NULL COMMENT '等级名称',
    min_points INT NOT NULL COMMENT '最小积分要求',
    max_points INT COMMENT '最大积分要求',
    color VARCHAR(20) COMMENT '等级颜色',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_level_name (level_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户等级配置表';

-- 2. 用户基本信息表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nickname VARCHAR(100) NOT NULL COMMENT '昵称',
    email VARCHAR(255) UNIQUE COMMENT '邮箱',
    bio TEXT COMMENT '个人简介',
    points INT DEFAULT 0 COMMENT '积分',
    level_id BIGINT COMMENT '等级ID',
    membership VARCHAR(50) DEFAULT '普通会员' COMMENT '会员类型',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_level_id (level_id),
    INDEX idx_points (points)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基本信息表';

-- 3. 浏览历史表
CREATE TABLE IF NOT EXISTS browse_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    item_type ENUM('product', 'knowledge') NOT NULL COMMENT '内容类型',
    item_id VARCHAR(100) NOT NULL COMMENT '内容ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    image_url VARCHAR(500) COMMENT '图片URL',
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_item_type_item_id (item_type, item_id),
    INDEX idx_viewed_at (viewed_at),
    INDEX idx_user_viewed (user_id, viewed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户浏览历史表';

-- 4. 用户收藏表
CREATE TABLE IF NOT EXISTS user_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    item_type ENUM('product', 'knowledge') NOT NULL COMMENT '内容类型',
    item_id VARCHAR(100) NOT NULL COMMENT '内容ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    image_url VARCHAR(500) COMMENT '图片URL',
    price DECIMAL(10,2) COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    collected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_item_type_item_id (item_type, item_id),
    INDEX idx_user_collected (user_id, collected_at),
    UNIQUE KEY uk_user_item (user_id, item_type, item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 5. 用户发布表
CREATE TABLE IF NOT EXISTS user_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_type ENUM('product', 'knowledge') NOT NULL COMMENT '发布类型',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    image_url VARCHAR(500) COMMENT '图片URL',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    comments_count INT DEFAULT 0 COMMENT '评论数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_post_type (post_type),
    INDEX idx_created_at (created_at),
    INDEX idx_user_created (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户发布表';

-- 6. 通知设置表
CREATE TABLE IF NOT EXISTS notification_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    push_notifications BOOLEAN DEFAULT TRUE COMMENT '推送通知',
    email_notifications BOOLEAN DEFAULT FALSE COMMENT '邮件通知',
    sms_notifications BOOLEAN DEFAULT FALSE COMMENT '短信通知',
    marketing_emails BOOLEAN DEFAULT TRUE COMMENT '营销邮件',
    order_updates BOOLEAN DEFAULT TRUE COMMENT '订单更新',
    price_alerts BOOLEAN DEFAULT FALSE COMMENT '价格提醒',
    new_product_alerts BOOLEAN DEFAULT TRUE COMMENT '新品提醒',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知设置表';

-- 7. 隐私设置表
CREATE TABLE IF NOT EXISTS privacy_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    profile_visibility ENUM('public', 'friends', 'private') DEFAULT 'public' COMMENT '个人资料可见性',
    activity_visibility ENUM('public', 'friends', 'private') DEFAULT 'public' COMMENT '活动动态可见性',
    data_collection BOOLEAN DEFAULT TRUE COMMENT '数据收集',
    personalized_ads BOOLEAN DEFAULT FALSE COMMENT '个性化广告',
    location_access BOOLEAN DEFAULT TRUE COMMENT '位置信息',
    contacts_sync BOOLEAN DEFAULT FALSE COMMENT '通讯录同步',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户隐私设置表';

-- 初始化数据
-- 1. 初始化用户等级配置
INSERT INTO user_levels (level_name, min_points, max_points, color) VALUES
('普通会员', 0, 999, '#666666'),
('银牌会员', 1000, 4999, '#C0C0C0'),
('金牌会员', 5000, 9999, '#FFD700'),
('钻石会员', 10000, NULL, '#B9F2FF');

-- 2. 初始化默认用户
INSERT INTO users (nickname, email, bio, points, membership, avatar_url) VALUES
('设计生活家', 'user@example.com', '记录生活，也创造生活', 300, '普通会员', NULL);

-- 3. 初始化浏览历史数据
INSERT INTO browse_history (user_id, item_type, item_id, title, image_url, viewed_at) VALUES
(1, 'product', 'product-001', '现代简约风格客厅设计', 'https://images.pexels.com/photos/1643383/pexels-photo-1643383.jpeg', '2024-01-15 14:30:00'),
(1, 'knowledge', 'knowledge-001', '北欧风格卧室装修案例', 'https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg', '2024-01-14 10:15:00'),
(1, 'product', 'product-002', '小户型厨房收纳技巧', 'https://images.pexels.com/photos/1080696/pexels-photo-1080696.jpeg', '2024-01-13 16:45:00'),
(1, 'product', 'product-003', '智能家居产品推荐', 'https://images.pexels.com/photos/1571460/pexels-photo-1571460.jpeg', '2024-01-12 09:20:00'),
(1, 'knowledge', 'knowledge-002', '阳台花园设计灵感', 'https://images.pexels.com/photos/1029599/pexels-photo-1029599.jpeg', '2024-01-11 11:30:00');

-- 4. 初始化收藏数据
INSERT INTO user_collections (user_id, item_type, item_id, title, image_url, price, original_price, collected_at) VALUES
(1, 'product', 'product-001', '现代简约风格客厅设计', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7', 2999.00, 3999.00, '2024-01-15 14:30:00'),
(1, 'knowledge', 'knowledge-001', '北欧风格卧室装修案例', 'https://images.unsplash.com/photo-1560185893-a55cbc8c57e8', NULL, NULL, '2024-01-14 10:15:00'),
(1, 'product', 'product-002', '小户型厨房收纳技巧', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136', 899.00, 1299.00, '2024-01-13 16:45:00');

-- 5. 初始化发布数据
INSERT INTO user_posts (user_id, post_type, title, content, image_url, likes_count, comments_count, created_at) VALUES
(1, 'product', '现代简约风格客厅设计', '分享一套现代简约风格的客厅设计方案，包含家具搭配和色彩选择建议。', 'https://images.pexels.com/photos/276724/pexels-photo-276724.jpeg', 123, 45, '2024-01-15 14:30:00'),
(1, 'knowledge', '北欧风格卧室装修案例', '详细介绍北欧风格卧室装修的要点和注意事项。', 'https://images.pexels.com/photos/271618/pexels-photo-271618.jpeg', 0, 0, '2024-01-14 10:15:00'),
(1, 'product', '小户型厨房收纳技巧', '分享小户型厨房的收纳技巧和实用工具推荐。', 'https://images.pexels.com/photos/1080721/pexels-photo-1080721.jpeg', 67, 12, '2024-01-13 16:45:00');

-- 6. 初始化通知设置
INSERT INTO notification_settings (user_id, push_notifications, email_notifications, sms_notifications, marketing_emails, order_updates, price_alerts, new_product_alerts) VALUES
(1, TRUE, FALSE, FALSE, TRUE, TRUE, FALSE, TRUE);

-- 7. 初始化隐私设置
INSERT INTO privacy_settings (user_id, profile_visibility, activity_visibility, data_collection, personalized_ads, location_access, contacts_sync) VALUES
(1, 'public', 'public', TRUE, FALSE, TRUE, FALSE);

-- 完成迁移
COMMIT;