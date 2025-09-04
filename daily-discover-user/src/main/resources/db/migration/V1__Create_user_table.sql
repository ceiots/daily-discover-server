-- 创建用户表
CREATE TABLE dd_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    nickname VARCHAR(100) NOT NULL COMMENT '昵称',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    birthday DATETIME COMMENT '生日',
    bio TEXT COMMENT '个人简介',
    
    -- 用户等级和积分相关字段
    points INT DEFAULT 0 COMMENT '用户积分',
    level VARCHAR(20) DEFAULT '新用户' COMMENT '用户等级：新用户、青铜会员、白银会员、黄金会员、钻石会员、VIP',
    membership VARCHAR(20) DEFAULT '普通会员' COMMENT '会员类型：普通会员、青铜会员、白银会员、黄金会员、钻石会员、至尊会员',
    
    -- 用户行为统计字段
    favorites_count INT DEFAULT 0 COMMENT '收藏数量',
    orders_pending_payment INT DEFAULT 0 COMMENT '待付款订单数',
    orders_pending_shipment INT DEFAULT 0 COMMENT '待发货订单数',
    orders_pending_receipt INT DEFAULT 0 COMMENT '待收货订单数',
    orders_completed INT DEFAULT 0 COMMENT '已完成订单数',
    
    -- 系统字段
    status TINYINT DEFAULT 0 COMMENT '用户状态：0-正常，1-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
INSERT INTO dd_user (username, password, nickname, email, phone, avatar, gender, bio, status, points, level, membership, favorites_count, orders_pending_payment, orders_pending_shipment, orders_pending_receipt, orders_completed) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '设计达人', 'admin@dailydiscover.com', '13800138000', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face', 1, '享受美学生活的每一刻', 0, 2450, 'VIP', '钻石会员', 24, 2, 1, 3, 12),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '测试用户', 'test@dailydiscover.com', '13800138001', 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face', 2, '这是一个测试用户账户', 0, 100, '新用户', '普通会员', 5, 0, 0, 1, 3);

-- 密码都是: 123456