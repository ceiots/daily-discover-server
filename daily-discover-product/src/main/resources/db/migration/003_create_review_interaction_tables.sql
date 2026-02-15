-- ============================================
-- 评价与互动管理表结构
-- 创建时间: 2026-02-04
-- 业务模块: 评价与互动
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS user_favorites;
DROP TABLE IF EXISTS product_actions;
DROP TABLE IF EXISTS review_stats;
DROP TABLE IF EXISTS review_likes;
DROP TABLE IF EXISTS review_replies;
DROP TABLE IF EXISTS user_reviews;

-- 用户评价表
CREATE TABLE IF NOT EXISTS user_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_avatar VARCHAR(500) COMMENT '用户头像',
    order_id BIGINT COMMENT '订单ID',
    rating INT NOT NULL COMMENT '评分(1-5)',
    title VARCHAR(200) COMMENT '评价标题',
    comment TEXT COMMENT '评价内容',
    image_urls JSON COMMENT '评价图片',
    video_url VARCHAR(500) COMMENT '评价视频',
    is_anonymous BOOLEAN DEFAULT false COMMENT '是否匿名',
    is_verified_purchase BOOLEAN DEFAULT false COMMENT '是否验证购买',
    helpful_count INT DEFAULT 0 COMMENT '有用数量',
    reply_count INT DEFAULT 0 COMMENT '回复数量',
    like_count INT DEFAULT 0 COMMENT '点赞数量',
    review_date DATE NOT NULL COMMENT '评价日期',
    status ENUM('pending', 'approved', 'rejected', 'hidden') DEFAULT 'pending' COMMENT '评价状态',
    moderation_notes VARCHAR(500) COMMENT '审核备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_rating (rating),
    INDEX idx_review_date (review_date),
    INDEX idx_helpful_count (helpful_count),
    INDEX idx_status (status),
    INDEX idx_is_verified_purchase (is_verified_purchase)
) COMMENT '用户评价表';

-- 评价回复表
CREATE TABLE IF NOT EXISTS review_replies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '回复ID',
    review_id BIGINT NOT NULL COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '回复用户ID',
    parent_reply_id BIGINT COMMENT '父回复ID',
    content TEXT NOT NULL COMMENT '回复内容',
    is_seller_reply BOOLEAN DEFAULT false COMMENT '是否商家回复',
    like_count INT DEFAULT 0 COMMENT '点赞数量',
    status ENUM('active', 'deleted') DEFAULT 'active' COMMENT '回复状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_review_id (review_id),
    INDEX idx_user_id (user_id),
    INDEX idx_parent_reply_id (parent_reply_id),
    INDEX idx_is_seller_reply (is_seller_reply),
    INDEX idx_status (status)
) COMMENT '评价回复表';

-- 评价点赞表
CREATE TABLE IF NOT EXISTS review_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    review_id BIGINT NOT NULL COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    UNIQUE KEY uk_review_user (review_id, user_id),
    INDEX idx_review_id (review_id),
    INDEX idx_user_id (user_id)
) COMMENT '评价点赞表';

-- 评价统计表
CREATE TABLE IF NOT EXISTS review_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    total_reviews INT DEFAULT 0 COMMENT '总评价数',
    average_rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
    rating_distribution JSON COMMENT '评分分布',
    verified_reviews_count INT DEFAULT 0 COMMENT '验证购买评价数',
    image_reviews_count INT DEFAULT 0 COMMENT '带图评价数',
    video_reviews_count INT DEFAULT 0 COMMENT '带视频评价数',
    last_30_days_reviews INT DEFAULT 0 COMMENT '近30天评价数',
    helpful_reviews_count INT DEFAULT 0 COMMENT '有用评价数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_product_id (product_id),
    INDEX idx_average_rating (average_rating),
    INDEX idx_total_reviews (total_reviews)
) COMMENT '评价统计表';

-- 商品操作记录表
CREATE TABLE IF NOT EXISTS product_actions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '操作记录ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    action_type ENUM('view', 'cart', 'purchase', 'favorite', 'share', 'comment') NOT NULL COMMENT '操作类型',
    quantity INT DEFAULT 1 COMMENT '数量',
    session_id VARCHAR(100) COMMENT '会话ID',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    referrer VARCHAR(500) COMMENT '来源页面',
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    status VARCHAR(50) COMMENT '状态',
    metadata JSON COMMENT '附加数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_action_type (action_type),
    INDEX idx_action_date (action_date),
    INDEX idx_session_id (session_id)
) COMMENT '商品操作记录表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS user_favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    folder_name VARCHAR(100) DEFAULT '默认收藏夹' COMMENT '收藏夹名称',
    notes VARCHAR(500) COMMENT '收藏备注',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_product_id (product_id),
    INDEX idx_folder_name (folder_name)
) COMMENT '用户收藏表';

COMMIT;

-- ============================================
-- 评价与互动表初始数据
-- ============================================

-- 插入用户评价数据
INSERT INTO user_reviews (id, product_id, user_id, user_avatar, order_id, rating, title, comment, image_urls, is_anonymous, is_verified_purchase, helpful_count, reply_count, like_count, review_date, status) VALUES
(1, 1, 1001, 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=400&fit=crop&crop=face', 2001, 5, '非常满意！功能强大', '这款智能手表真的很不错，心率监测准确，运动追踪功能也很实用。续航能力超出预期！', '["https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop"]', false, true, 15, 3, 20, '2026-01-15', 'approved'),
(2, 1, 1002, 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=400&h=400&fit=crop&crop=face', 2002, 4, '性价比高', '功能齐全，价格合理。就是表带有点硬，希望能改进。', '["https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop"]', false, true, 8, 1, 12, '2026-01-20', 'approved'),
(3, 2, 1003, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop&crop=face', 2003, 5, '音质超棒！', '降噪效果很好，音质清晰，佩戴舒适。续航能力也很强，值得购买！', '["https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop"]', false, true, 20, 5, 25, '2026-01-18', 'approved'),
(4, 2, 1004, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&h=400&fit=crop&crop=face', 2004, 4, '不错的耳机', '降噪效果满意，音质清晰。就是充电盒有点大，携带不太方便。', '[]', true, true, 5, 0, 8, '2026-01-22', 'approved'),
(5, 3, 1005, 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=400&fit=crop&crop=face', 2005, 5, '性能强劲', '运行速度快，屏幕显示效果很好。轻薄便携，适合商务人士。', '[]', false, true, 12, 2, 15, '2026-01-25', 'approved'),
(6, 4, 1006, 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=400&h=400&fit=crop&crop=face', 2006, 5, '拍照效果惊艳！', '这款手机的拍照功能真的没话说，夜景模式特别强大，色彩还原很真实。性能也很流畅，玩游戏完全不卡顿。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop"]', false, true, 25, 4, 30, '2026-01-28', 'approved'),
(7, 4, 1007, 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=400&h=400&fit=crop&crop=face', 2007, 4, '性价比不错的旗舰机', '整体使用体验很好，屏幕显示效果细腻，系统流畅。就是续航稍微有点短，重度使用需要一天两充。', '[]', false, true, 12, 1, 18, '2026-02-01', 'approved'),
(8, 4, 1008, 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop&crop=face', 2008, 5, '5G速度超快', '下载速度真的很快，视频加载几乎无延迟。外观设计也很漂亮，手感舒适。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop"]', true, true, 8, 0, 12, '2026-02-05', 'approved'),
(9, 4, 1009, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&h=400&fit=crop&crop=face', 2009, 4, '系统优化不错', '系统运行很流畅，很少出现卡顿。充电速度也很快，30分钟就能充到80%。就是价格稍微有点高。', '[]', false, true, 6, 0, 9, '2026-02-10', 'approved'),
(10, 4, 1010, 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&h=400&fit=crop&crop=face', 2010, 5, '完美的商务手机', '非常适合商务人士使用，多任务处理很流畅，电池续航也能满足一天需求。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop"]', false, true, 15, 2, 22, '2026-02-12', 'approved');

-- 插入评价回复数据
INSERT INTO review_replies (review_id, user_id, content, is_seller_reply, like_count, status) VALUES
(1, 1, '感谢您的认可！我们会继续努力提供更好的产品和服务。', true, 5, 'active'),
(1, 1002, '我也觉得这款手表不错，特别是运动功能。', false, 3, 'active'),
(3, 2, '感谢您的支持！我们会继续优化产品体验。', true, 8, 'active'),
(6, 2, '感谢您对我们产品的认可！我们会继续优化拍照算法，提供更好的用户体验。', true, 6, 'active'),
(6, 1007, '确实，夜景模式真的很强大，我拍了很多漂亮的夜景照片。', false, 2, 'active'),
(7, 2, '感谢您的反馈！我们会持续优化电池续航表现。', true, 3, 'active'),
(10, 2, '感谢您的认可！这款手机确实专为商务人士设计，多任务处理能力很强。', true, 4, 'active');

-- 插入评价点赞数据
INSERT INTO review_likes (review_id, user_id) VALUES
(1, 1002),
(1, 1003),
(1, 1004),
(3, 1001),
(3, 1005),
(6, 1001),
(6, 1003),
(6, 1005),
(6, 1007),
(6, 1008),
(7, 1002),
(7, 1004),
(7, 1006),
(8, 1001),
(8, 1003),
(8, 1009),
(9, 1002),
(9, 1005),
(10, 1001),
(10, 1003),
(10, 1004),
(10, 1006),
(10, 1007);

-- 插入评价统计数据
INSERT INTO review_stats (product_id, total_reviews, average_rating, rating_distribution, verified_reviews_count, image_reviews_count, last_30_days_reviews, helpful_reviews_count) VALUES
(1, 128, 4.5, '{"5": 80, "4": 35, "3": 10, "2": 2, "1": 1}', 120, 45, 25, 85),
(2, 89, 4.3, '{"5": 50, "4": 25, "3": 10, "2": 3, "1": 1}', 80, 30, 18, 60),
(3, 256, 4.7, '{"5": 180, "4": 60, "3": 12, "2": 3, "1": 1}', 240, 80, 40, 150),
(4, 194, 4.8, '{"5": 150, "4": 40, "3": 3, "2": 1, "1": 0}', 180, 60, 35, 120);

-- 插入商品操作记录数据
INSERT INTO product_actions (product_id, user_id, action_type, quantity, session_id, ip_address, user_agent, referrer, action_date, status) VALUES
(1, 1001, 'view', 1, 'session001', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://example.com/products', '2026-01-15 10:30:00', 'completed'),
(1, 1001, 'cart', 1, 'session001', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://example.com/products/1', '2026-01-15 10:35:00', 'completed'),
(1, 1001, 'purchase', 1, 'session001', '192.168.1.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://example.com/cart', '2026-01-15 10:40:00', 'completed'),
(2, 1002, 'view', 1, 'session002', '192.168.1.2', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'https://example.com/products', '2026-01-16 14:20:00', 'completed'),
(2, 1002, 'favorite', 1, 'session002', '192.168.1.2', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'https://example.com/products/2', '2026-01-16 14:25:00', 'completed');

-- 插入用户收藏数据
INSERT INTO user_favorites (user_id, product_id, folder_name, notes, sort_order) VALUES
(1001, 2, '默认收藏夹', '考虑购买', 1),
(1001, 3, '默认收藏夹', '办公使用', 2),
(1002, 1, '默认收藏夹', '运动装备', 1),
(1003, 4, '电子产品', '手机升级', 1);

COMMIT;