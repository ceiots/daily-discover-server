-- ============================================
-- 9. 客服功能模块表结构
-- 业务模块: 客户服务管理
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS customer_service_messages;
DROP TABLE IF EXISTS customer_service_conversations;
DROP TABLE IF EXISTS customer_service_categories;
DROP TABLE IF EXISTS customer_service_agents;

-- 客服坐席表
CREATE TABLE IF NOT EXISTS customer_service_agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '坐席ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（客服人员）',
    
    -- 坐席信息
    agent_name VARCHAR(100) NOT NULL COMMENT '坐席名称',
    agent_code VARCHAR(50) UNIQUE COMMENT '坐席编号',
    department VARCHAR(100) COMMENT '所属部门',
    
    -- 工作状态
    status ENUM('online', 'busy', 'offline', 'away') DEFAULT 'offline' COMMENT '工作状态',
    max_concurrent_chats INT DEFAULT 5 COMMENT '最大并发聊天数',
    current_chats INT DEFAULT 0 COMMENT '当前聊天数',
    
    -- 统计信息
    total_chats INT DEFAULT 0 COMMENT '总接待聊天数',
    avg_response_time INT DEFAULT 0 COMMENT '平均响应时间（秒）',
    satisfaction_rate DECIMAL(5,2) DEFAULT 0 COMMENT '满意度评分',
    
    -- 技能标签
    skills JSON COMMENT '技能标签',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_activity_at TIMESTAMP NULL COMMENT '最后活动时间',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_agent_code (agent_code),
    INDEX idx_status (status),
    INDEX idx_department (department),
    INDEX idx_last_activity_at (last_activity_at)
) COMMENT '客服坐席表';

-- 客服分类表
CREATE TABLE IF NOT EXISTS customer_service_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父级分类ID',
    
    -- 分类信息
    category_type ENUM('product', 'order', 'payment', 'delivery', 'refund', 'other') NOT NULL COMMENT '分类类型',
    description VARCHAR(500) COMMENT '分类描述',
    
    -- 显示设置
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_enabled BOOLEAN DEFAULT true COMMENT '是否启用',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_parent_id (parent_id),
    INDEX idx_category_type (category_type),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_enabled (is_enabled)
) COMMENT '客服分类表';

-- 客服会话表（简化设计）
CREATE TABLE IF NOT EXISTS customer_service_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（关联users.id）',
    agent_id BIGINT COMMENT '客服坐席ID（关联customer_service_agents.id）',
    category_id BIGINT COMMENT '问题分类ID（关联customer_service_categories.id）',
    
    -- 会话信息
    session_id VARCHAR(100) UNIQUE COMMENT '会话标识',
    title VARCHAR(200) COMMENT '会话标题',
    
    -- 会话状态
    status ENUM('waiting', 'active', 'resolved', 'closed', 'transferred') DEFAULT 'waiting' COMMENT '会话状态',
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium' COMMENT '优先级',
    
    -- 关联信息
    related_order_id BIGINT COMMENT '关联订单ID（关联orders_core.id）',
    related_product_id BIGINT COMMENT '关联商品ID（关联products.id）',
    
    -- 时间信息
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    first_response_at TIMESTAMP NULL COMMENT '首次响应时间',
    resolved_at TIMESTAMP NULL COMMENT '解决时间',
    closed_at TIMESTAMP NULL COMMENT '关闭时间',
    
    -- 满意度评价（重要字段，用于客服质量评估）
    satisfaction_rating INT COMMENT '满意度评分（1-5）',
    satisfaction_comment TEXT COMMENT '满意度评价内容',
    
    -- 索引优化
    INDEX idx_user_id (user_id),
    INDEX idx_agent_id (agent_id),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_at (created_at),
    INDEX idx_related_order_id (related_order_id),
    INDEX idx_related_product_id (related_product_id),
    
    -- 复合索引优化
    INDEX idx_user_status (user_id, status) COMMENT '用户状态查询',
    INDEX idx_agent_status (agent_id, status) COMMENT '坐席状态查询',
    INDEX idx_category_status (category_id, status) COMMENT '分类状态查询'
) COMMENT '客服会话表';

-- 客服消息表
CREATE TABLE IF NOT EXISTS customer_service_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    conversation_id BIGINT NOT NULL COMMENT '会话ID',
    
    -- 发送者信息
    sender_type ENUM('user', 'agent', 'system') NOT NULL COMMENT '发送者类型',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    
    -- 消息内容
    message_type ENUM('text', 'image', 'file', 'order', 'product', 'system') DEFAULT 'text' COMMENT '消息类型',
    content TEXT COMMENT '消息内容',
    
    -- 文件消息
    file_url VARCHAR(500) COMMENT '文件URL',
    file_name VARCHAR(200) COMMENT '文件名称',
    file_size INT COMMENT '文件大小',
    
    -- 关联信息
    related_order_id BIGINT COMMENT '关联订单ID',
    related_product_id BIGINT COMMENT '关联商品ID',
    
    -- 消息状态
    is_read BOOLEAN DEFAULT false COMMENT '是否已读',
    read_at TIMESTAMP NULL COMMENT '阅读时间',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引优化
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender_type (sender_type),
    INDEX idx_sender_id (sender_id),
    INDEX idx_message_type (message_type),
    INDEX idx_created_at (created_at),
    INDEX idx_is_read (is_read)
) COMMENT '客服消息表';

COMMIT;