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
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);