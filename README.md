# 运行命令
java -jar daily-discover-0.0.1-SNAPSHOT.jar

# 内网穿透
cpolar http 8081

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
    imageUrl VARCHAR(255) NOT NULL
);

INSERT INTO events (title, description, category, date, imageUrl) VALUES
('1965 年：百科全书《辞海》第一版正式出版', '《辞海》是我国最具权威性的大型综合性辞书，对传播中国传统文化、促进中外文化交流发挥了重要作用。第一版的出版是中国辞书编纂史上的重要里程碑。', '文化', '1965-01-15', 'http://pic.yupoo.com/ceiots/5a4adb21/fd238f4b.jpg'),
('1004 年：景德镇正式建镇', '北宋景德年间，景德镇正式建镇，开启了千年制瓷史。景德镇凭借优质的瓷土资源和精湛的制瓷工艺，成为了世界瓷都。', '历史', '1004-01-15', 'http://pic.yupoo.com/ceiots/ca539911/160ba38c.jpg'),
('138 年：张骞出使西域', '汉武帝派遣张骞出使西域，开辟了连接中国与中亚、西亚的贸易通道，这就是著名的“丝绸之路”的开端。', '贸易', '0138-01-15', 'http://pic.yupoo.com/ceiots/995142e5/762aedb0.jpg');

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1;

-- 插入示例数据到 categories 表
INSERT INTO categories (name, imageUrl) VALUES
('端午节', 'http://pic.yupoo.com/ceiots/98c8a6b2/483a8f32.jpg'),
('考古发现', 'http://pic.yupoo.com/ceiots/7ef5c35a/1ee8d426.jpg'),
('文化艺术', 'http://pic.yupoo.com/ceiots/7ebf4b0d/3c5923a3.jpg'),
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


INSERT INTO comments (recommendation_id, userName, userAvatarUrl, content, rating, date)
VALUES 
(1, '李雯雯', 'https://example.com/user_avatar1.jpg', '宣纸质量非常好，毛笔也很顺滑。', 5.0, '2023-06-15'),
(1, '张明', 'https://example.com/user_avatar2.jpg', '作为书法爱好者，这套文房四宝的品质让我很惊喜。', 4.0, '2023-06-10');

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



