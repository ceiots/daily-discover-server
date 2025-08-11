-- 数据库初始化脚本
-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS daily_discover 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 创建数据库用户（可选，用于生产环境）
-- CREATE USER IF NOT EXISTS 'daily_discover'@'localhost' IDENTIFIED BY 'your_password';
-- GRANT ALL PRIVILEGES ON daily_discover.* TO 'daily_discover'@'localhost';
-- FLUSH PRIVILEGES;

-- 注意：在实际使用中，数据库创建通常在应用启动前手动完成
-- 此脚本主要用于开发环境的快速初始化