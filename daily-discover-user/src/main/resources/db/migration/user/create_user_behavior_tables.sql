-- 用户行为数据表迁移脚本
-- 创建用户行为相关数据表

-- 使用数据库
USE daily_discover;

-- 删除现有表（如果存在）
DROP TABLE IF EXISTS user_posts;
DROP TABLE IF EXISTS user_collections;
DROP TABLE IF EXISTS user_browse_history;

-- 1. 浏览历史表
CREATE TABLE IF NOT EXISTS browse_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    item_type VARCHAR(20) NOT NULL COMMENT '内容类型',
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

-- 2. 用户收藏表
CREATE TABLE IF NOT EXISTS user_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    item_type VARCHAR(20) NOT NULL COMMENT '内容类型',
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

-- 3. 用户发布表
CREATE TABLE IF NOT EXISTS user_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_type VARCHAR(20) NOT NULL COMMENT '发布类型',
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

-- 初始化行为数据
-- 1. 初始化浏览历史数据
INSERT INTO browse_history (user_id, item_type, item_id, title, image_url, viewed_at) VALUES
(1, 'product', 'product-001', '现代简约风格客厅设计', 'https://images.pexels.com/photos/1643383/pexels-photo-1643383.jpeg', '2024-01-15 14:30:00'),
(1, 'knowledge', 'knowledge-001', '北欧风格卧室装修案例', 'https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg', '2024-01-14 10:15:00'),
(1, 'product', 'product-002', '小户型厨房收纳技巧', 'https://images.pexels.com/photos/1080696/pexels-photo-1080696.jpeg', '2024-01-13 16:45:00'),
(1, 'product', 'product-003', '智能家居产品推荐', 'https://images.pexels.com/photos/1571460/pexels-photo-1571460.jpeg', '2024-01-12 09:20:00'),
(1, 'knowledge', 'knowledge-002', '阳台花园设计灵感', 'https://images.pexels.com/photos/1029599/pexels-photo-1029599.jpeg', '2024-01-11 11:30:00');

-- 2. 初始化收藏数据
INSERT INTO user_collections (user_id, item_type, item_id, title, image_url, price, original_price, collected_at) VALUES
(1, 'product', 'product-001', '现代简约风格客厅设计', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7', 2999.00, 3999.00, '2024-01-15 14:30:00'),
(1, 'knowledge', 'knowledge-001', '北欧风格卧室装修案例', 'https://images.unsplash.com/photo-1560185893-a55cbc8c57e8', NULL, NULL, '2024-01-14 10:15:00'),
(1, 'product', 'product-002', '小户型厨房收纳技巧', 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136', 899.00, 1299.00, '2024-01-13 16:45:00');

-- 3. 初始化发布数据
INSERT INTO user_posts (user_id, post_type, title, content, image_url, likes_count, comments_count, created_at) VALUES
(1, 'product', '现代简约风格客厅设计', '分享一套现代简约风格的客厅设计方案，包含家具搭配和色彩选择建议。', 'https://images.pexels.com/photos/276724/pexels-photo-276724.jpeg', 123, 45, '2024-01-15 14:30:00'),
(1, 'knowledge', '北欧风格卧室装修案例', '详细介绍北欧风格卧室装修的要点和注意事项。', 'https://images.pexels.com/photos/271618/pexels-photo-271618.jpeg', 0, 0, '2024-01-14 10:15:00'),
(1, 'product', '小户型厨房收纳技巧', '分享小户型厨房的收纳技巧和实用工具推荐。', 'https://images.pexels.com/photos/1080721/pexels-photo-1080721.jpeg', 67, 12, '2024-01-13 16:45:00');

-- 完成迁移
COMMIT;