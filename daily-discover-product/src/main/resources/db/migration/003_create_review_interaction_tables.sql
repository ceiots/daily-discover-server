-- ============================================
-- 评价与互动管理表结构
-- 创建时间: 2026-02-04
-- 业务模块: 评价与互动
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS review_stats;
DROP TABLE IF EXISTS review_replies;
DROP TABLE IF EXISTS user_review_stats;
DROP TABLE IF EXISTS user_review_details;
DROP TABLE IF EXISTS user_reviews;

-- 用户评价表（垂直分表设计）
CREATE TABLE IF NOT EXISTS user_reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    order_id BIGINT COMMENT '订单ID',
    rating INT NOT NULL COMMENT '评分(1-5)',
    title VARCHAR(200) COMMENT '评价标题',
    is_anonymous BOOLEAN DEFAULT false COMMENT '是否匿名',
    is_verified_purchase BOOLEAN DEFAULT false COMMENT '是否验证购买',
    review_date DATE NOT NULL COMMENT '评价日期',
    status ENUM('pending', 'approved', 'rejected', 'hidden') DEFAULT 'pending' COMMENT '评价状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_product_id (product_id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_rating (rating),
    INDEX idx_review_date (review_date),
    INDEX idx_status (status),
    INDEX idx_is_verified_purchase (is_verified_purchase),
    
    -- 性能优化索引（高频查询）
    INDEX idx_product_status_rating (product_id, status, rating) COMMENT '商品状态评分查询',
    INDEX idx_user_status (user_id, status) COMMENT '用户状态查询'
) COMMENT '用户评价表（核心信息）';

-- 用户评价详情表（大字段单独存储）
CREATE TABLE IF NOT EXISTS user_review_details (
    review_id BIGINT PRIMARY KEY COMMENT '评价ID',
    user_avatar VARCHAR(500) COMMENT '用户头像（缓存字段）',
    comment TEXT COMMENT '评价内容',
    image_urls JSON COMMENT '评价图片',
    video_url VARCHAR(500) COMMENT '评价视频',
    moderation_notes VARCHAR(500) COMMENT '审核备注',
    
    INDEX idx_review_id (review_id)
) COMMENT '用户评价详情表';

-- 用户评价统计表（实时统计字段）
CREATE TABLE IF NOT EXISTS user_review_stats (
    review_id BIGINT PRIMARY KEY COMMENT '评价ID',
    helpful_count INT DEFAULT 0 COMMENT '有用数量',
    reply_count INT DEFAULT 0 COMMENT '回复数量',
    like_count INT DEFAULT 0 COMMENT '点赞数量',
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    
    INDEX idx_review_id (review_id),
    INDEX idx_helpful_count (helpful_count),
    INDEX idx_like_count (like_count)
) COMMENT '用户评价统计表';

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



-- 商品评价统计表（聚合统计）
CREATE TABLE IF NOT EXISTS review_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    total_reviews INT DEFAULT 0 COMMENT '总评价数',
    average_rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
    rating_distribution JSON COMMENT '评分分布',
    purchased_reviews_count INT DEFAULT 0 COMMENT '已购评价数（真实购买用户的评价）',
    last_30_days_reviews INT DEFAULT 0 COMMENT '近30天评价数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_product_id (product_id),
    INDEX idx_average_rating (average_rating),
    INDEX idx_total_reviews (total_reviews)
) COMMENT '商品评价统计表';





COMMIT;

-- ============================================
-- 评价与互动表初始数据
-- ============================================

-- 插入用户评价数据
INSERT INTO user_reviews (id, product_id, user_id, order_id, rating, title, is_anonymous, is_verified_purchase, review_date, status) VALUES
(1, 1, 1001, 2001, 5, '非常满意！功能强大', false, true, '2026-01-15', 'approved'),
(2, 1, 1002, 2002, 4, '性价比高', false, true, '2026-01-20', 'approved'),
(3, 2, 1003, 2003, 5, '音质超棒！', false, true, '2026-01-18', 'approved'),
(4, 2, 1004, 2004, 4, '不错的耳机', true, true, '2026-01-22', 'approved'),
(5, 3, 1005, 2005, 5, '性能强劲', false, true, '2026-01-25', 'approved'),
(6, 4, 1006, 2006, 5, '拍照效果惊艳！', false, true, '2026-01-28', 'approved'),
(7, 4, 1007, 2007, 4, '性价比不错的旗舰机', false, true, '2026-02-01', 'approved'),
(8, 4, 1008, 2008, 5, '5G速度超快', true, true, '2026-02-05', 'approved'),
(9, 4, 1009, 2009, 4, '系统优化不错', false, true, '2026-02-10', 'approved'),
(10, 4, 1010, 2010, 5, '完美的商务手机', false, true, '2026-02-12', 'approved');

-- 插入评价回复数据
INSERT INTO review_replies (review_id, user_id, content, is_seller_reply, like_count, status) VALUES
(1, 1, '感谢您的认可！我们会继续努力提供更好的产品和服务。', true, 5, 'active'),
(1, 1002, '我也觉得这款手表不错，特别是运动功能。', false, 3, 'active'),
(3, 2, '感谢您的支持！我们会继续优化产品体验。', true, 8, 'active'),
(6, 2, '感谢您对我们产品的认可！我们会继续优化拍照算法，提供更好的用户体验。', true, 6, 'active'),
(6, 1007, '确实，夜景模式真的很强大，我拍了很多漂亮的夜景照片。', false, 2, 'active'),
(7, 2, '感谢您的反馈！我们会持续优化电池续航表现。', true, 3, 'active'),
(10, 2, '感谢您的认可！这款手机确实专为商务人士设计，多任务处理能力很强。', true, 4, 'active');

-- 插入评价统计数据
INSERT INTO review_stats (product_id, total_reviews, average_rating, rating_distribution, purchased_reviews_count, last_30_days_reviews) VALUES
(1, 128, 4.5, '{"5": 80, "4": 35, "3": 10, "2": 2, "1": 1}', 120, 25),
(2, 89, 4.3, '{"5": 50, "4": 25, "3": 10, "2": 3, "1": 1}', 80, 18),
(3, 256, 4.7, '{"5": 180, "4": 60, "3": 12, "2": 3, "1": 1}', 240, 40),
(4, 194, 4.8, '{"5": 150, "4": 40, "3": 3, "2": 1, "1": 0}', 180, 35);

-- 插入用户评价详情数据
INSERT INTO user_review_details (review_id, user_avatar, comment, image_urls, video_url, moderation_notes) VALUES
(1, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '这款智能手表功能非常强大，特别是健康监测功能很准确。运动模式也很丰富，续航能力也不错。', '["https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop","https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&h=600&fit=crop"]', NULL, '评价内容符合规范'),
(2, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '性价比很高的一款智能手表，功能齐全，操作简单。适合日常使用和运动健身。', '["https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&h=400&fit=crop"]', NULL, '评价内容符合规范'),
(3, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '音质超棒！降噪效果很好，在嘈杂环境中也能享受纯净音乐。佩戴舒适，续航能力强。', '["https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop","https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=600&fit=crop"]', NULL, '评价内容符合规范'),
(4, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '不错的耳机，音质清晰，佩戴舒适。性价比高，适合日常通勤使用。', '["https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop"]', NULL, '评价内容符合规范'),
(5, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '性能强劲，运行流畅，轻薄便携。电池续航不错，适合商务办公和日常使用。', '["https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop"]', NULL, '评价内容符合规范'),
(6, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '拍照效果惊艳！夜景模式特别强大，系统流畅，电池续航也很给力。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop","https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=600&fit=crop"]', NULL, '评价内容符合规范'),
(7, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '性价比不错的旗舰机，性能稳定，拍照效果很好。系统优化不错，使用流畅。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop"]', NULL, '评价内容符合规范'),
(8, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '5G速度超快，网络连接稳定。系统流畅，应用加载速度快。整体体验很好。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop"]', NULL, '评价内容符合规范'),
(9, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '系统优化不错，界面简洁美观。电池续航表现良好，日常使用完全够用。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop"]', NULL, '评价内容符合规范'),
(10, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '完美的商务手机，多任务处理能力强。安全性高，适合商务人士使用。', '["https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop"]', NULL, '评价内容符合规范');

-- 插入用户评价统计数据
INSERT INTO user_review_stats (review_id, helpful_count, reply_count, like_count) VALUES
(1, 12, 2, 8),
(2, 5, 0, 3),
(3, 18, 1, 15),
(4, 3, 0, 2),
(5, 8, 0, 6),
(6, 25, 2, 20),
(7, 7, 1, 5),
(8, 10, 0, 8),
(9, 4, 0, 3),
(10, 15, 1, 12);

-- ============================================
-- 为新增商品添加评价数据（user_id 4）
-- ============================================

-- 为新增商品添加用户评价
INSERT INTO user_reviews (id, product_id, user_id, order_id, rating, title, is_anonymous, is_verified_purchase, review_date, status) VALUES
(16, 16, 4, 1004, 5, '智能摄像头非常好用', false, true, '2026-03-08', 'approved'),
(17, 17, 4, 1005, 4, '投影效果很清晰', false, true, '2026-03-07', 'approved'),
(18, 18, 4, 1006, 5, '机械键盘手感很棒', false, true, '2026-03-06', 'approved'),
(19, 19, 4, 1007, 4, '体脂秤测量准确', false, true, '2026-03-05', 'approved'),
(20, 20, 4, 1008, 5, '睡眠耳机效果显著', false, true, '2026-03-04', 'approved'),
(21, 21, 4, 1009, 4, '料理机功能强大', false, true, '2026-03-03', 'approved'),
(22, 22, 4, 1010, 5, '便携咖啡机很方便', false, true, '2026-03-02', 'approved'),
(23, 23, 4, 1011, 4, '智能跳绳计数准确', false, true, '2026-03-01', 'approved'),
(24, 24, 4, 1012, 5, '电动牙刷清洁效果好', false, true, '2026-02-28', 'approved'),
(25, 25, 4, 1013, 4, '充电宝容量大充电快', false, true, '2026-02-27', 'approved');

-- 添加评价详情
INSERT INTO user_review_details (review_id, user_avatar, comment, image_urls, video_url, moderation_notes) VALUES
(16, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '安装简单，画质清晰，手机远程查看很方便，夜视功能也很棒。', '["https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop", "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&h=600&fit=crop"]', NULL, '评价内容真实有效'),
(17, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '投影效果超出预期，自动对焦功能很实用，携带方便。', '["https://images.unsplash.com/photo-1560472354-b33ff0c44a43?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效'),
(18, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '茶轴手感适中，RGB灯光效果很酷，无线连接稳定。', '["https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400&h=400&fit=crop", "https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=600&h=600&fit=crop"]', NULL, '评价内容真实有效'),
(19, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '测量数据准确，APP数据分析详细，有助于健康管理。', '["https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效'),
(20, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '降噪效果明显，佩戴舒适，睡眠质量明显提升。', '["https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效'),
(21, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '破壁效果细腻，噪音控制不错，清洗方便。', '["https://images.unsplash.com/photo-1556909114-4d0d853e5b0c?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效'),
(22, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '制作咖啡快速，口感醇厚，户外使用很方便。', '["https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效'),
(23, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '计数准确，APP数据同步及时，跳绳体验很好。', '["https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效'),
(24, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '清洁模式多样，刷头柔软，电池续航时间长。', '["https://images.unsplash.com/photo-1584305574647-0d5c6c4c8a6b?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效'),
(25, 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&h=100&fit=crop', '充电速度快，接口丰富，外出旅行必备。', '["https://images.unsplash.com/photo-1585155770447-2f66e2a397b5?w=400&h=400&fit=crop"]', NULL, '评价内容真实有效');

-- 添加评价统计
INSERT INTO user_review_stats (review_id, helpful_count, reply_count, like_count, last_updated_at) VALUES
(16, 12, 3, 25, '2026-03-09 10:00:00'),
(17, 8, 2, 18, '2026-03-09 10:00:00'),
(18, 15, 4, 32, '2026-03-09 10:00:00'),
(19, 6, 1, 14, '2026-03-09 10:00:00'),
(20, 20, 5, 45, '2026-03-09 10:00:00'),
(21, 9, 2, 19, '2026-03-09 10:00:00'),
(22, 11, 3, 26, '2026-03-09 10:00:00'),
(23, 7, 1, 15, '2026-03-09 10:00:00'),
(24, 13, 3, 28, '2026-03-09 10:00:00'),
(25, 10, 2, 22, '2026-03-09 10:00:00');


COMMIT;