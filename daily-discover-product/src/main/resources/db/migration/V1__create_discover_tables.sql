-- 每日发现页面数据库迁移脚本
-- 创建支持每日发现功能的数据库表

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

-- 每日主题表
CREATE TABLE daily_themes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL COMMENT '主题标题',
    subtitle VARCHAR(200) COMMENT '主题副标题',
    image_url VARCHAR(500) COMMENT '主题图片URL',
    theme_date DATE NOT NULL COMMENT '主题日期',
    theme_type VARCHAR(50) COMMENT '主题类型（如：重启日、松弛时刻等）',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_date (theme_date),
    INDEX idx_type (theme_type),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日主题表';

-- 每日推荐商品关联表
CREATE TABLE daily_recommend_products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    theme_id BIGINT NOT NULL COMMENT '主题ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    recommend_date DATE NOT NULL COMMENT '推荐日期',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (theme_id) REFERENCES daily_themes(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY uk_theme_product_date (theme_id, product_id, recommend_date),
    INDEX idx_date (recommend_date),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日推荐商品关联表';

-- 用户点赞记录表
CREATE TABLE user_likes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
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
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
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
    FOREIGN KEY (parent_id) REFERENCES product_categories(id) ON DELETE SET NULL,
    INDEX idx_parent (parent_id),
    INDEX idx_sort (sort_order),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 初始化数据
-- 插入默认分类
INSERT INTO product_categories (name, description, sort_order) VALUES
('生活家居', '提升生活品质的家居用品', 1),
('餐饮器具', '精美的餐饮相关用品', 2),
('装饰摆件', '美化空间的装饰品', 3),
('个人护理', '日常个人护理用品', 4);

-- 插入示例箴言
INSERT INTO daily_mottos (text, date) VALUES
('把今天过成值得收藏的日子', CURDATE()),
('慢下来，感受此刻的温度', CURDATE()),
('生活中的美好，藏在细节里', CURDATE()),
('用心感受，每个瞬间都值得珍藏', CURDATE()),
('让生活充满仪式感，从今天开始', CURDATE());

-- 插入示例主题
INSERT INTO daily_themes (title, subtitle, image_url, theme_date, theme_type) VALUES
('晨光里的温柔', '周一重启日', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop', CURDATE(), '重启日'),
('午后的悠闲', '周二放松日', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop', CURDATE(), '放松日'),
('傍晚的宁静', '周三静心日', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop', CURDATE(), '静心日'),
('夜空的星辰', '周四梦想日', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop', CURDATE(), '梦想日'),
('周末的松弛', '周五松弛时刻', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop', CURDATE(), '松弛时刻');

-- 插入示例商品
INSERT INTO products (name, description, price, image_url, category_id) VALUES
('手工陶瓷咖啡杯', '一杯咖啡的时间，让生活慢下来', 128.00, 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop', 2),
('亚麻抱枕', '柔软触感，温暖陪伴', 89.00, 'https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop', 1),
('香薰蜡烛', '温暖的光，治愈的味道', 68.00, 'https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop', 1),
('玻璃花瓶', '简约设计，装点生活', 156.00, 'https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop', 3),
('羊毛围巾', '温暖柔软，冬日必备', 238.00, 'https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop', 4),
('木质餐具套装', '自然材质，健康生活', 198.00, 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop', 2),
('棉麻桌布', '清新自然，提升用餐氛围', 128.00, 'https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop', 1),
('陶瓷花瓶', '手工制作，独一无二', 268.00, 'https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop', 3);

-- 插入每日推荐商品关联
INSERT INTO daily_recommend_products (theme_id, product_id, sort_order, recommend_date) VALUES
(1, 1, 1, CURDATE()),
(1, 2, 2, CURDATE()),
(1, 3, 3, CURDATE()),
(2, 4, 1, CURDATE()),
(2, 5, 2, CURDATE()),
(2, 6, 3, CURDATE()),
(3, 7, 1, CURDATE()),
(3, 8, 2, CURDATE()),
(4, 1, 1, CURDATE()),
(4, 3, 2, CURDATE()),
(5, 2, 1, CURDATE()),
(5, 4, 2, CURDATE());