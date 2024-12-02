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
CREATE TABLE warehouses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    delivery_time INT NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id BIGINT NOT NULL, -- 仓库ID，不使用外键
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL
);

# 插入仓库数据
-- 插入库存数据
INSERT INTO inventory (warehouse_id, product_name, quantity) VALUES
('1', 'Product X', 100),
('1', 'Product Y', 50),
('2', 'Product Z', 75);

INSERT INTO warehouses (name, location, delivery_time, latitude, longitude) VALUES
('Beijing Warehouse', '北京市朝阳区', 2, 39.9042, 116.4074),
('Shanghai Warehouse', '上海市浦东新区', 3, 31.2304, 121.4737),
('Guangzhou Warehouse', '广州市天河区', 4, 23.1291, 113.2644);
