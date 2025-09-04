-- 为用户表添加个人资料和统计字段
-- 用于支持前端页面的完整数据展示

-- 添加用户等级和积分相关字段
ALTER TABLE dd_user 
ADD COLUMN points INT DEFAULT 0 COMMENT '用户积分' AFTER bio,
ADD COLUMN level VARCHAR(20) DEFAULT '新用户' COMMENT '用户等级：新用户、青铜会员、白银会员、黄金会员、钻石会员、VIP' AFTER points,
ADD COLUMN membership VARCHAR(20) DEFAULT '普通会员' COMMENT '会员类型：普通会员、青铜会员、白银会员、黄金会员、钻石会员、至尊会员' AFTER level;

-- 添加用户行为统计字段
ALTER TABLE dd_user 
ADD COLUMN favorites_count INT DEFAULT 0 COMMENT '收藏数量' AFTER membership,
ADD COLUMN orders_pending_payment INT DEFAULT 0 COMMENT '待付款订单数' AFTER favorites_count,
ADD COLUMN orders_pending_shipment INT DEFAULT 0 COMMENT '待发货订单数' AFTER orders_pending_payment,
ADD COLUMN orders_pending_receipt INT DEFAULT 0 COMMENT '待收货订单数' AFTER orders_pending_shipment,
ADD COLUMN orders_completed INT DEFAULT 0 COMMENT '已完成订单数' AFTER orders_pending_receipt;

-- 添加索引以提高查询性能
CREATE INDEX idx_points ON dd_user(points);
CREATE INDEX idx_level ON dd_user(level);
CREATE INDEX idx_membership ON dd_user(membership);

-- 更新现有用户数据以匹配前端展示需求
UPDATE dd_user SET 
    nickname = '设计达人',
    bio = '享受美学生活的每一刻',
    points = 2450,
    level = 'VIP',
    membership = '钻石会员',
    favorites_count = 24,
    orders_pending_payment = 2,
    orders_pending_shipment = 1,
    orders_pending_receipt = 3,
    orders_completed = 12,
    avatar = 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face'
WHERE username = 'admin';

UPDATE dd_user SET 
    points = 100,
    level = '新用户',
    membership = '普通会员',
    favorites_count = 5,
    orders_pending_payment = 0,
    orders_pending_shipment = 0,
    orders_pending_receipt = 1,
    orders_completed = 3,
    avatar = 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face'
WHERE username = 'testuser';