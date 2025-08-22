-- 每日发现页面数据库迁移脚本
-- 创建支持每日发现功能的数据库表

use daily_discover;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除已存在的表
DROP TABLE IF EXISTS user_likes;
DROP TABLE IF EXISTS user_favorites;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS daily_mottos;
DROP TABLE IF EXISTS product_categories;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 每日美学箴言表
CREATE TABLE daily_mottos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    text VARCHAR(500) NOT NULL COMMENT '箴言内容',
    date DATE NOT NULL COMMENT '日期',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_date (date),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日美学箴言表';

-- 商品表
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    image_url VARCHAR(500) COMMENT '商品图片URL',
    category_id BIGINT COMMENT '商品分类ID',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否上架',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    favorite_count INT DEFAULT 0 COMMENT '收藏次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category_id),
    INDEX idx_active (is_active),
    INDEX idx_view_count (view_count),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';



-- 用户点赞记录表
CREATE TABLE user_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user (user_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户点赞记录表';

-- 用户收藏记录表
CREATE TABLE user_favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user (user_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏记录表';

-- 商品分类表
CREATE TABLE product_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    parent_id BIGINT COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent (parent_id),
    INDEX idx_sort (sort_order),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 初始化数据
-- 插入默认分类
INSERT INTO product_categories (name, description, sort_order) VALUES
('晨光序曲', '限量版晨间美学系列，90%用户反馈提升生活幸福感，今日下单享专属礼遇', 1),
('午后诗篇', '明星同款午后时光系列，限时优惠中，让每一刻都成为值得珍藏的美好记忆', 2),
('暮色温柔', '热销夜生活美学系列，库存告急，为忙碌生活注入温暖治愈的仪式感', 3),
('周末叙事', '会员专享周末升级系列，立即拥有让朋友羡慕的品质生活方式', 4);

-- 插入示例箴言
INSERT INTO daily_mottos (text, date) VALUES
('晨光如诗，今天就开始投资自己的幸福，从一件精致好物开始', CURDATE()),
('午后时光，你值得拥有这样的美好时刻，立即行动让生活更有品质', CURDATE()),
('暮色四合，别让忙碌偷走生活的仪式感，现在就为自己选择温暖', CURDATE()),
('夜深人静，每个细节都值得被珍视，今晚就给自己最好的犒赏', CURDATE()),
('周末慢活，生活不该只有工作，趁现在让每一天都充满期待', CURDATE());

-- 插入示例商品
INSERT INTO products (name, description, price, image_url, category_id) VALUES
('晨光序曲·咖啡杯', '⚡限时特惠！手工陶瓷限量版，原价268现价168，已售2000+件。90%用户反馈清晨幸福感提升，今日下单送专属杯垫，仅剩最后50个！', 168.00, 'https://img.freepik.com/free-photo/top-view-coffee-cup-with-donut_23-2149396433.jpg', 1),
('午后诗篇·抱枕', '🔥爆款热卖！明星同款亚麻抱枕，小红书10万+推荐，柔软度提升300%。限时优惠129元，买2送1，朋友都在问的链接，立即抢购！', 129.00, 'https://img.freepik.com/free-photo/white-pillow-sofa_53876-138615.jpg', 2),
('暮色温柔·香薰', '💖治愈必备！天然植物香薰，1000+用户5星好评，缓解焦虑效果显著。特价88元，库存告急最后100瓶，今晚下单送精美礼盒！', 88.00, 'https://img.freepik.com/free-photo/candles-with-aromatic-oils_23-2149396432.jpg', 3),
('晨光序曲·花瓶', '✨设计师联名！北欧极简花瓶，ins风家居必备，提升空间格调200%。原价286现价186，限量发售，送干花束，手慢无！', 186.00, 'https://img.freepik.com/free-photo/vase-with-flowers-living-room-interior_53876-138616.jpg', 1),
('周末叙事·毛毯', '🏆品质之选！100%羊毛毛毯，温暖度提升400%，周末宅家必备。特价268元，会员专享9折，送收纳袋，让每个周末都温暖！', 268.00, 'https://img.freepik.com/free-photo/blanket-sofa_53876-138617.jpg', 4),
('午后诗篇·茶具', '🍵茶艺师推荐！天然木纹茶具，茶香提升150%，午后仪式感必备。228元限时优惠，送茶叶试喝装，朋友都说有品位！', 228.00, 'https://img.freepik.com/free-photo/tea-set-wooden-table_53876-138618.jpg', 2),
('晨光序曲·桌布', '🌟餐桌美学！棉麻桌布，让早餐颜值提升300%，家人都夸有格调。158元特价，买即送餐巾，改变从早餐开始！', 158.00, 'https://img.freepik.com/free-photo/tablecloth-dining-table_53876-138619.jpg', 1),
('暮色温柔·夜灯', '💡睡眠神器！智能调光夜灯，改善睡眠质量85%，都市人必备。298元限时特惠，送香薰精油，今晚就给自己最好的！', 298.00, 'https://img.freepik.com/free-photo/night-lamp-bedroom_53876-138620.jpg', 3);