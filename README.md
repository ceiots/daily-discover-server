"# springboot" 

#  登录数据库
mysql -u demo -p

# 授权语句
CREATE USER 'demo0000'@'localhost' IDENTIFIED BY 'demo0000';
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,ALTER ON demo.* TO 'demo0000'@'localhost';

# 刷新权限语句
FLUSH PRIVILEGES;

# 数据库创建语句
CREATE DATABASE demo;

# 数据库使用语句
USE demo;

# db建表语句
# 用户行为
CREATE TABLE user_behavior_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

INSERT INTO user_behavior_log (user_id, action_type, details) VALUES
(1, 'browse', '用户浏览了产品1'),
(2, 'search', '用户搜索了笔记本电脑'),
(1, 'add_to_cart', '用户将产品2加入购物车'),
(3, 'browse', '用户浏览了产品3'),
(2, 'add_to_cart', '用户将产品4加入购物车');


# 用户表
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `registration_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_login_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# 产品表
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    rating DECIMAL(3, 2) DEFAULT 0.0,
    review_count INT DEFAULT 0,
    image VARCHAR(255),
    category VARCHAR(100)
);

INSERT INTO products (name, description, price, rating, review_count, image, category) VALUES
('笔记本电脑 Pro', '高性能笔记本电脑，配备 16GB 内存和 512GB SSD。', 999.99, 4.5, 120, 'https://example.com/images/laptop-pro.jpg', '电子产品'),
('智能手机 Ultra', '最新款智能手机，配备 128GB 存储和 6400万像素摄像头。', 699.99, 4.7, 150, 'https://example.com/images/smartphone-ultra.jpg', '电子产品'),
('无线蓝牙耳机', '降噪蓝牙耳机，电池寿命长。', 199.99, 4.3, 80, 'https://example.com/images/headphones.jpg', '电子产品'),
('畅销小说', '一位知名作者撰写的引人入胜的小说。', 14.99, 4.8, 200, 'https://example.com/images/novel.jpg', '书籍'),
('经典皮夹克', '适合男士的时尚皮夹克。', 79.99, 4.4, 100, 'https://example.com/images/jacket.jpg', '服装'),
('智能手表 Pro', '具备健康监测功能的高级智能手表。', 299.99, 4.6, 130, 'https://example.com/images/smartwatch.jpg', '电子产品'),
('咖啡机', '适用于家庭使用的紧凑型咖啡机。', 99.99, 4.2, 90, 'https://example.com/images/coffee-maker.jpg', '家用电器'),
('健身追踪器', '便携式健身追踪器，帮助您监控活动。', 49.99, 4.5, 110, 'https://example.com/images/fitness-tracker.jpg', '电子产品'),
('旅行背包', '耐用的旅行背包。', 59.99, 4.3, 140, 'https://example.com/images/backpack.jpg', '配件'),
('游戏主机', '下一代游戏主机，图形性能强大。', 499.99, 4.7, 160, 'https://example.com/images/gaming-console.jpg', '电子产品');

// TODO
1 忘记密码