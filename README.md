# 运行命令
java -jar daily-discover-0.0.1-SNAPSHOT.jar

# 启停nginx
.\nginx.exe -s reload
或者
.\nginx.exe -s stop
start nginx.exe

#  登录数据库
mysql -u root -p

# 授权语句
CREATE USER 'demo0000'@'localhost' IDENTIFIED BY 'demo0000';
GRANT SELECT,INSERT,UPDATE,DELETE,CREATE,ALTER,DROP ON demo.* TO 'demo0000'@'localhost';

# 刷新权限语句
FLUSH PRIVILEGES;

# 数据库创建语句
CREATE DATABASE demo;

# 数据库使用语句
USE demo;

# db建表语句

# 配置表
CREATE TABLE config (
    id INT AUTO_INCREMENT PRIMARY KEY,
    `key` VARCHAR(255) NOT NULL,
    `value` VARCHAR(255) NOT NULL
);
-- 插入初始数据
INSERT INTO config (`key`, `value`) VALUES ('image_prefix', 'https://dailydiscover.top/');

# 事件表
CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    event_date DATETIME NOT NULL,
    image_url VARCHAR(255),
    INDEX idx_event_date (event_date)
);

# 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    member_level VARCHAR(50),
    avatar VARCHAR(255),
    registration_time DATETIME,
    nickname VARCHAR(100),
    INDEX idx_phone (phone_number)
);

# 商品表 (recommendations)
CREATE TABLE recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    image_url VARCHAR(255),
    shop_name VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    sold_count INT DEFAULT 0,
    shop_avatar_url VARCHAR(255),
    specifications JSON,
    product_details JSON,
    purchase_notices JSON,
    store_description TEXT,
    created_at DATETIME,
    category_id BIGINT,
    deleted TINYINT(1) DEFAULT 0,
    shop_id BIGINT,
    INDEX idx_category (category_id),
    INDEX idx_shop (shop_id)
);

# 分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    image_url VARCHAR(255)
);

# 订单表
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    payment_method INT,
    payment_amount DECIMAL(10,2),
    payment_time DATETIME,
    order_addr_id BIGINT,
    status INT NOT NULL DEFAULT 0,
    shipping_address TEXT,
    INDEX idx_user (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status)
);

# 订单项表
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_image VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    specs TEXT,
    attributes TEXT,
    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
);

# 订单地址表
CREATE TABLE order_addr (
    order_addr_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    INDEX idx_user (user_id)
);

# 物流信息表
CREATE TABLE logistics_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL UNIQUE,
    tracking_number VARCHAR(100) NOT NULL,
    company_code VARCHAR(50) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    status INT DEFAULT 0,
    shipping_time DATETIME,
    estimated_delivery_time DATETIME,
    actual_delivery_time DATETIME,
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(20),
    receiver_address TEXT,
    INDEX idx_order (order_id),
    INDEX idx_tracking_number (tracking_number)
);

# 物流轨迹表
CREATE TABLE logistics_track (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    logistics_id BIGINT NOT NULL,
    track_time DATETIME NOT NULL,
    location VARCHAR(255),
    description TEXT,
    status VARCHAR(100),
    status_code VARCHAR(50),
    operator VARCHAR(100),
    operator_phone VARCHAR(20),
    INDEX idx_logistics (logistics_id),
    INDEX idx_track_time (track_time)
);

# 物流公司表
CREATE TABLE logistics_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(50),
    phone VARCHAR(20),
    website VARCHAR(255),
    logo VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,
    sort INT DEFAULT 0,
    remark TEXT
);

# 购物车表
CREATE TABLE cart_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_image VARCHAR(255),
    specifications JSON,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    shop_name VARCHAR(255),
    shop_avatar_url VARCHAR(255),
    INDEX idx_user (user_id),
    INDEX idx_product (product_id)
);

# 评论表
CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    user_avatar_url VARCHAR(255),
    content TEXT NOT NULL,
    rating DECIMAL(2,1) NOT NULL,
    date DATETIME NOT NULL,
    INDEX idx_product (product_id),
    INDEX idx_user (user_id)
);

# 省份表
CREATE TABLE provinces (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL
);

# 城市表
CREATE TABLE cities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    province_code VARCHAR(20) NOT NULL,
    INDEX idx_province (province_code)
);

# 区县表
CREATE TABLE districts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    city_code VARCHAR(20) NOT NULL,
    INDEX idx_city (city_code)
);
