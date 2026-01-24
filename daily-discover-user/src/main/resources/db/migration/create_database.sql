-- 创建每日发现数据库

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `daily_discover` 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `daily_discover`;

-- 创建用户并授权（可选，用于生产环境）
-- CREATE USER IF NOT EXISTS 'daily_discover_user'@'localhost' IDENTIFIED BY 'your_password_here';
-- GRANT ALL PRIVILEGES ON `daily_discover`.* TO 'daily_discover_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 显示数据库创建结果
SELECT '数据库 daily_discover 创建成功' AS '执行结果';

-- 显示当前数据库
SELECT DATABASE() AS '当前使用的数据库';