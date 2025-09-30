-- H2数据库初始化脚本
-- 创建用户表
CREATE TABLE IF NOT EXISTS dd_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    avatar VARCHAR(500),
    gender TINYINT DEFAULT 0,
    birthday DATETIME,
    bio TEXT,
    status TINYINT DEFAULT 0,
    last_login_time DATETIME,
    last_login_ip VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_username ON dd_user(username);
CREATE INDEX IF NOT EXISTS idx_email ON dd_user(email);
CREATE INDEX IF NOT EXISTS idx_phone ON dd_user(phone);
CREATE INDEX IF NOT EXISTS idx_create_time ON dd_user(create_time);
CREATE INDEX IF NOT EXISTS idx_deleted ON dd_user(deleted);

-- 插入测试数据
INSERT INTO dd_user (username, password, nickname, email, phone, avatar, gender, bio, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '管理员', 'admin@dailydiscover.com', '13800138000', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100&h=100&fit=crop&crop=face', 1, '每日发现管理员账户', 0),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '测试用户', 'test@dailydiscover.com', '13800138001', 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=100&h=100&fit=crop&crop=face', 2, '这是一个测试用户账户', 0);

-- 密码都是: 123456