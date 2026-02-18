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
    status VARCHAR(20) DEFAULT 'offline' COMMENT '工作状态',
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
    category_type VARCHAR(20) NOT NULL COMMENT '分类类型',
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
    status VARCHAR(20) DEFAULT 'waiting' COMMENT '会话状态',
    priority VARCHAR(20) DEFAULT 'medium' COMMENT '优先级',
    
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
    sender_type VARCHAR(20) NOT NULL COMMENT '发送者类型',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    
    -- 消息内容
    message_type VARCHAR(20) DEFAULT 'text' COMMENT '消息类型',
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

-- ============================================
-- 客服功能模块表初始数据
-- ============================================

-- 插入客服坐席数据
INSERT INTO customer_service_agents (id, user_id, agent_name, agent_code, department, status, max_concurrent_chats, current_chats, total_chats, avg_response_time, satisfaction_rate, skills, last_activity_at) VALUES
(1, 10001, '张客服', 'CS001', '售前咨询部', 'online', 5, 2, 150, 45, 4.8, '["产品咨询", "订单处理", "售后支持"]', '2026-02-01 16:30:00'),
(2, 10002, '李客服', 'CS002', '售后支持部', 'busy', 5, 4, 200, 60, 4.7, '["售后处理", "退款申请", "投诉处理"]', '2026-02-01 16:25:00'),
(3, 10003, '王客服', 'CS003', '技术支持部', 'online', 5, 1, 120, 75, 4.6, '["技术问题", "产品使用", "故障排除"]', '2026-02-01 16:20:00'),
(4, 10004, '赵客服', 'CS004', '售前咨询部', 'away', 5, 0, 80, 50, 4.5, '["产品咨询", "价格咨询", "活动咨询"]', '2026-02-01 15:45:00');

-- 插入客服分类数据
INSERT INTO customer_service_categories (id, category_name, parent_id, category_type, description, sort_order, is_enabled) VALUES
(1, '商品问题', 0, 'product', '商品相关咨询和问题', 1, true),
(2, '订单问题', 0, 'order', '订单相关咨询和问题', 2, true),
(3, '支付问题', 0, 'payment', '支付相关咨询和问题', 3, true),
(4, '物流问题', 0, 'delivery', '物流相关咨询和问题', 4, true),
(5, '售后问题', 0, 'refund', '售后相关咨询和问题', 5, true),
(6, '其他问题', 0, 'other', '其他类型咨询和问题', 6, true),

-- 商品问题子分类
(7, '商品咨询', 1, 'product', '商品信息、功能咨询', 1, true),
(8, '商品质量', 1, 'product', '商品质量问题反馈', 2, true),
(9, '商品价格', 1, 'product', '商品价格相关咨询', 3, true),

-- 订单问题子分类
(10, '订单状态', 2, 'order', '订单状态查询', 1, true),
(11, '订单修改', 2, 'order', '订单信息修改', 2, true),
(12, '订单取消', 2, 'order', '订单取消申请', 3, true);

-- 插入客服会话数据
INSERT INTO customer_service_conversations (id, user_id, agent_id, category_id, session_id, title, status, priority, related_order_id, related_product_id, first_response_at, resolved_at, closed_at, satisfaction_rating, satisfaction_comment) VALUES
(1, 1001, 1, 7, 'SESS202602010001', '智能手表功能咨询', 'resolved', 'medium', NULL, 1, '2026-02-01 10:15:00', '2026-02-01 10:30:00', '2026-02-01 10:35:00', 5, '客服解答很专业，问题解决很快'),
(2, 1002, 2, 8, 'SESS202602010002', '耳机质量问题反馈', 'active', 'high', 2, 2, '2026-02-01 11:20:00', NULL, NULL, NULL, NULL),
(3, 1003, 3, 10, 'SESS202602010003', '笔记本电脑订单状态查询', 'resolved', 'medium', 3, 3, '2026-02-01 14:25:00', '2026-02-01 14:35:00', '2026-02-01 14:40:00', 4, '客服态度很好，解答清晰'),
(4, 1004, 1, 12, 'SESS202602010004', '手机订单取消申请', 'waiting', 'medium', 4, 4, NULL, NULL, NULL, NULL, NULL),
(5, 1005, 2, 5, 'SESS202602010005', '退款进度查询', 'active', 'high', 5, 4, '2026-02-01 16:50:00', NULL, NULL, NULL, NULL);

-- 插入客服消息数据
INSERT INTO customer_service_messages (conversation_id, sender_type, sender_id, message_type, content, file_url, file_name, file_size, related_order_id, related_product_id, is_read, read_at) VALUES
(1, 'user', 1001, 'text', '你好，我想咨询一下智能手表的心率监测功能准确吗？', NULL, NULL, NULL, NULL, 1, true, '2026-02-01 10:15:00'),
(1, 'agent', 1, 'text', '您好！我们的智能手表采用专业的心率传感器，监测准确度达到医疗级标准，可以放心使用。', NULL, NULL, NULL, NULL, 1, true, '2026-02-01 10:16:00'),
(1, 'user', 1001, 'text', '那续航时间怎么样？', NULL, NULL, NULL, NULL, 1, true, '2026-02-01 10:17:00'),
(1, 'agent', 1, 'text', '正常使用情况下续航可达7天，如果开启所有功能大约3-4天。', NULL, NULL, NULL, NULL, 1, true, '2026-02-01 10:18:00'),

(2, 'user', 1002, 'text', '我收到的耳机有杂音，质量有问题', NULL, NULL, NULL, 2, 2, true, '2026-02-01 11:20:00'),
(2, 'agent', 2, 'text', '非常抱歉给您带来不便。请提供一下订单号和具体问题描述，我们会尽快处理。', NULL, NULL, NULL, 2, 2, true, '2026-02-01 11:21:00'),
(2, 'user', 1002, 'text', '订单号：DD202602010002，耳机在播放音乐时有明显的杂音', NULL, NULL, NULL, 2, 2, true, '2026-02-01 11:22:00'),

(3, 'user', 1003, 'text', '我的笔记本电脑订单什么时候能发货？', NULL, NULL, NULL, 3, 3, true, '2026-02-01 14:25:00'),
(3, 'agent', 3, 'text', '您的订单已安排发货，预计今天下午发出，物流单号稍后会更新。', NULL, NULL, NULL, 3, 3, true, '2026-02-01 14:26:00'),

(4, 'user', 1004, 'text', '我想取消刚下的手机订单', NULL, NULL, NULL, 4, 4, false, NULL),

(5, 'user', 1005, 'text', '我的退款申请处理到哪一步了？', NULL, NULL, NULL, 5, 4, true, '2026-02-01 16:50:00'),
(5, 'agent', 2, 'text', '您的退款申请正在审核中，预计1-3个工作日内完成。', NULL, NULL, NULL, 5, 4, false, NULL);

COMMIT;