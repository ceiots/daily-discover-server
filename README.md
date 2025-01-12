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

# 历史的今天 SQL
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    imageUrl VARCHAR(255) NOT NULL
);

INSERT INTO events (title, description, category, date, imageUrl) VALUES
('1965 年：百科全书《辞海》第一版正式出版', '《辞海》是我国最具权威性的大型综合性辞书，对传播中国传统文化、促进中外文化交流发挥了重要作用。第一版的出版是中国辞书编纂史上的重要里程碑。', '文化', '1965-01-15', 'http://pic.yupoo.com/ceiots/5a4adb21/fd238f4b.jpg'),
('1004 年：景德镇正式建镇', '北宋景德年间，景德镇正式建镇，开启了千年制瓷史。景德镇凭借优质的瓷土资源和精湛的制瓷工艺，成为了世界瓷都。', '历史', '1004-01-15', 'http://pic.yupoo.com/ceiots/ca539911/160ba38c.jpg'),
('138 年：张骞出使西域', '汉武帝派遣张骞出使西域，开辟了连接中国与中亚、西亚的贸易通道，这就是著名的“丝绸之路”的开端。', '贸易', '0138-01-15', 'https://ai-public.mastergo.com/ai/img_res/7b56c2788b75c54901ed75d82579d3bf.jpg');

-- 创建 categories 表
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    imageUrl VARCHAR(255) NOT NULL
);

CREATE TABLE `recommendations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `imageUrl` varchar(255) NOT NULL,
  `shopName` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `soldCount` int NOT NULL,
  `shopAvatarUrl` varchar(255) DEFAULT NULL,
  `productDetails` TEXT DEFAULT NULL,
  `purchaseNotice` TEXT DEFAULT NULL comments '购买须知',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 插入示例数据到 categories 表
INSERT INTO categories (name, imageUrl) VALUES
('端午节', 'http://pic.yupoo.com/ceiots/98c8a6b2/483a8f32.jpg'),
('丝路文明', 'http://pic.yupoo.com/ceiots/98b77edf/cc6a5f25.jpg'),
('考古发现', 'http://pic.yupoo.com/ceiots/7ef5c35a/1ee8d426.jpg'),
('文化艺术', 'http://pic.yupoo.com/ceiots/7ebf4b0d/3c5923a3.jpg'),
('医药史', 'http://pic.yupoo.com/ceiots/803ab1fa/bf937eca.jpg'),
('年货专区', 'http://pic.yupoo.com/ceiots/75a732aa/1e1a404b.jpg');

-- 插入示例数据到 recommendations 表
INSERT INTO demo.recommendations
(id, title, imageUrl, shopName, price, soldCount, shopAvatarUrl)
VALUES(1, '【新品首发】纯手工宣纸文房四宝套装', 'http://pic.yupoo.com/ceiots/1654bd90/9dd70118.jpg', '墨香阁旗舰店', 299.00, 2300, 'http://pic.yupoo.com/ceiots/8b95f41c/c6648d5e.jpg'),
(2, '景德镇手绘青花瓷茶具套装', 'http://pic.yupoo.com/ceiots/716e61f9/3e6e627a.jpg', '品茗轩旗舰店', 468.00, 1800, 'http://pic.yupoo.com/ceiots/67538d66/f32bfbf6.jpg'),
(3, '陕西发现首个完整西周时期贵族墓园', 'http://pic.yupoo.com/ceiots/df44806c/35cbb7fe.jpg', '国家文物局', 300.00, 220, 'http://pic.yupoo.com/ceiots/d0d3c13b/2adb2df0.jpg');


##
-- 创建 comments 表
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    recommendation_id BIGINT NOT NULL,
    userName VARCHAR(255) NOT NULL,
    userAvatarUrl VARCHAR(255),
    content TEXT NOT NULL,
    rating DECIMAL(2, 1) NOT NULL,
    date DATE NOT NULL
);

INSERT INTO recommendations (title, imageUrl, shopName, shopAvatarUrl, price, soldCount, productDetails, purchaseNotice)
VALUES 
('纯手工宣纸文房四宝套装', 'https://example.com/image.jpg', '墨香阁旗舰店', 'https://example.com/shop_avatar.jpg', 299.00, 2300, '本套装采用上等宣纸，纯手工制作，包含毛笔、墨锭、砚台和印章。', '请在使用前仔细阅读说明书。');

INSERT INTO comments (recommendation_id, userName, userAvatarUrl, content, rating, date)
VALUES 
(1, '李雯雯', 'https://example.com/user_avatar1.jpg', '宣纸质量非常好，毛笔也很顺滑。', 5.0, '2023-06-15'),
(1, '张明', 'https://example.com/user_avatar2.jpg', '作为书法爱好者，这套文房四宝的品质让我很惊喜。', 4.0, '2023-06-10');