# 运行命令
java -jar daily-discover-0.0.1-SNAPSHOT.jar

# 内网穿透
cpolar http 8081

# 启停nginx
.\nginx.exe -s reload
或者
.\nginx.exe -s stop
start nginx.exe


#  登录数据库
mysql -u root -p

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

# 历史的今天 SQL
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    date DATE NOT NULL,
    event_date DATE NOT NULL,
    imageUrl VARCHAR(255) NOT NULL
);

INSERT INTO events (title, description, category, date, imageUrl) VALUES
('1965 年：百科全书《辞海》第一版正式出版', '《辞海》是我国最具权威性的大型综合性辞书，对传播中国传统文化、促进中外文化交流发挥了重要作用。第一版的出版是中国辞书编纂史上的重要里程碑。', '文化', '1965-01-15', '/images/event1.jpg'),
('1004 年：景德镇正式建镇', '北宋景德年间，景德镇正式建镇，开启了千年制瓷史。景德镇凭借优质的瓷土资源和精湛的制瓷工艺，成为了世界瓷都。', '历史', '1004-01-15', '/images/event2.jpg'),
('138 年：张骞出使西域', '汉武帝派遣张骞出使西域，开辟了连接中国与中亚、西亚的贸易通道，这就是著名的“丝绸之路”的开端。', '贸易', '0138-01-15', '/images/event3.jpg');

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
  `specifications` JSON DEFAULT NULL COMMENT '规格参数',
  `productDetails` TEXT DEFAULT NULL,
  `purchaseNotice` TEXT DEFAULT NULL COMMENT '购买须知',
  `storeDescription` varchar(255) NULL COMMENT '店铺描述',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `category_id` BIGINT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1;

-- 插入示例数据到 categories 表
INSERT INTO categories (name, imageUrl) VALUES
('端午节', '/images/categories/categories1.jpg'),
('考古发现', '/images/categories/categories2.jpg'),
('文化艺术', '/images/categories/categories3.jpg'),
('年货专区', '/images/categories/categories4.jpg');

-- 插入示例数据到 recommendations 表
INSERT INTO demo.recommendations
(id, title, imageUrl, shopName, price, soldCount, shopAvatarUrl)
VALUES(1, '【新品首发】纯手工宣纸文房四宝套装', '/images/product/product1.jpg', '墨香阁旗舰店', 299.00, 2300, '/images/shop/shop1.jpg'),
(2, '景德镇手绘青花瓷茶具套装', '/images/product/product2.jpg', '品茗轩旗舰店', 468.00, 1800, '/images/shop/shop2.jpg'),
(3, '陕西发现首个完整西周时期贵族墓园', '/images/product/product3.jpg', '国家文物局', 300.00, 220, '/images/shop/shop3.jpg');


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


INSERT INTO comments (recommendation_id, userName, userAvatarUrl, content, rating, date)
VALUES 
(1, '李雯雯', '/images/avatar/avatar1.jpg', '宣纸质量非常好，毛笔也很顺滑。', 5.0, '2023-06-15'),
(1, '张明', '/images/avatar/avatar2.jpg', '作为书法爱好者，这套文房四宝的品质让我很惊喜。', 4.0, '2023-06-10');

## 购物车表
-- 创建购物车项表
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车项ID，主键',
    user_id BIGINT COMMENT '用户ID，用于关联用户',
    product_id BIGINT COMMENT '产品ID，用于关联商品',
    product_name VARCHAR(255) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(255) COMMENT '商品图片URL',
    product_variant VARCHAR(255) COMMENT '商品变体（如颜色、尺寸等）',
    price DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    quantity INT NOT NULL DEFAULT 1 COMMENT '商品数量，默认为1',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
    shopName varchar(255) NOT null,
    shopAvatarUrl varchar(255) DEFAULT NULL,
    INDEX idx_cart_items_user_id(user_id),
    INDEX idx_cart_items_product_id(product_id)
);

## 用户表 
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(15) NOT NULL UNIQUE, -- 手机号码
    password VARCHAR(255) NOT NULL, -- 密码（建议使用哈希存储）
    nickname VARCHAR(50), -- 昵称
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE demo.recommendation_categories (
    recommendation_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (recommendation_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# 配置表
CREATE TABLE config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(255) NOT NULL,
    `value` VARCHAR(255) NOT NULL
);
-- 插入初始数据
INSERT INTO config (`key`, `value`) VALUES ('image_prefix', 'http://1f582ab5.r5.cpolar.top');