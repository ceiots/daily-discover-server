-- 创建营销促销系统数据库
CREATE DATABASE IF NOT EXISTS marketing_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用营销促销系统数据库
USE marketing_db;

-- 营销促销系统表结构（优化版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: marketing_promotion_mvp.sql
-- 高并发优化策略: 
-- 1. 表分区：按时间范围分区、按用户ID哈希分区、按状态列表分区
-- 2. 避免外键约束：通过业务逻辑保证数据一致性
-- 3. 索引优化：核心字段索引、组合索引、状态时间复合索引
-- 4. 冷热数据分离：通过时间范围分区实现

-- 促销活动表
CREATE TABLE IF NOT EXISTS `promotion_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `activity_code` varchar(32) NOT NULL COMMENT '活动编码',
  `activity_name` varchar(100) NOT NULL COMMENT '活动名称',
  `activity_type` tinyint(4) NOT NULL COMMENT '活动类型:1-满减,2-折扣,3-直降,4-秒杀,5-拼团,6-限时,7-套装',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待开始,1-进行中,2-已结束,3-已取消',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `description` varchar(255) DEFAULT NULL COMMENT '活动描述',
  `activity_rule` text NOT NULL COMMENT '活动规则JSON',
  `limit_count` int(11) DEFAULT NULL COMMENT '限制次数',
  `limit_quantity` int(11) DEFAULT NULL COMMENT '限制数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_code` (`activity_code`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_status_time` (`status`,`start_time`,`end_time`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销活动表'
PARTITION BY RANGE (TO_DAYS(start_time)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 促销商品表 (移除product_name字段，可从product表关联获取)
CREATE TABLE IF NOT EXISTS `promotion_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `promotion_id` bigint(20) NOT NULL COMMENT '促销活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'SKU ID',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `promotion_price` decimal(10,2) NOT NULL COMMENT '促销价',
  `promotion_stock` int(11) DEFAULT NULL COMMENT '促销库存',
  `limit_quantity` int(11) DEFAULT NULL COMMENT '限购数量',
  `sold_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已售数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_promotion_sku` (`promotion_id`,`sku_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销商品表'
PARTITION BY HASH(promotion_id) PARTITIONS 8;

-- 优惠券表
CREATE TABLE IF NOT EXISTS `coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '优惠券ID',
  `coupon_code` varchar(32) NOT NULL COMMENT '优惠券编码',
  `coupon_name` varchar(100) NOT NULL COMMENT '优惠券名称',
  `coupon_type` tinyint(4) NOT NULL COMMENT '优惠券类型:1-满减券,2-折扣券,3-无门槛券,4-运费券',
  `amount` decimal(10,2) NOT NULL COMMENT '面额',
  `discount` decimal(5,2) DEFAULT NULL COMMENT '折扣率',
  `min_amount` decimal(10,2) DEFAULT NULL COMMENT '最低消费',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `total_count` int(11) NOT NULL COMMENT '发行总量',
  `take_count` int(11) NOT NULL DEFAULT '0' COMMENT '已领取数量',
  `used_count` int(11) NOT NULL DEFAULT '0' COMMENT '已使用数量',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `product_range_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '使用范围:1-全场通用,2-指定分类,3-指定商品',
  `product_range_values` varchar(1000) DEFAULT NULL COMMENT '使用范围值JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coupon_code` (`coupon_code`),
  KEY `idx_coupon_type` (`coupon_type`),
  KEY `idx_status_time` (`status`,`start_time`,`end_time`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表'
PARTITION BY RANGE (TO_DAYS(start_time)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS `user_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `coupon_code` varchar(32) NOT NULL COMMENT '优惠券编码',
  `coupon_type` tinyint(4) NOT NULL COMMENT '优惠券类型',
  `amount` decimal(10,2) NOT NULL COMMENT '面额',
  `min_amount` decimal(10,2) DEFAULT NULL COMMENT '最低消费',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-未使用,1-已使用,2-已过期,3-已冻结',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `get_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '获取方式:1-主动领取,2-系统发放,3-活动赠送',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 秒杀活动表
CREATE TABLE IF NOT EXISTS `seckill_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `activity_code` varchar(32) NOT NULL COMMENT '活动编码',
  `activity_name` varchar(100) NOT NULL COMMENT '活动名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待开始,1-进行中,2-已结束,3-已取消',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `description` varchar(255) DEFAULT NULL COMMENT '活动描述',
  `banner_url` varchar(255) DEFAULT NULL COMMENT 'banner图片',
  `total_stock` int(11) NOT NULL COMMENT '总库存',
  `available_stock` int(11) NOT NULL COMMENT '可用库存',
  `limit_count` int(11) DEFAULT NULL COMMENT '限制次数',
  `limit_quantity` int(11) DEFAULT NULL COMMENT '限购数量',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_code` (`activity_code`),
  KEY `idx_status_time` (`status`,`start_time`,`end_time`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表'
PARTITION BY RANGE (TO_DAYS(start_time)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 秒杀商品表 (移除product_name字段，可从product表关联获取)
CREATE TABLE IF NOT EXISTS `seckill_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价',
  `total_stock` int(11) NOT NULL COMMENT '总库存',
  `available_stock` int(11) NOT NULL COMMENT '可用库存',
  `lock_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `limit_quantity` int(11) DEFAULT NULL COMMENT '限购数量',
  `sold_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已售数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seckill_sku` (`seckill_id`,`sku_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品表'
PARTITION BY HASH(seckill_id) PARTITIONS 8;

-- 拼团活动表
CREATE TABLE IF NOT EXISTS `group_buy_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `activity_code` varchar(32) NOT NULL COMMENT '活动编码',
  `activity_name` varchar(100) NOT NULL COMMENT '活动名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待开始,1-进行中,2-已结束,3-已取消',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `description` varchar(255) DEFAULT NULL COMMENT '活动描述',
  `banner_url` varchar(255) DEFAULT NULL COMMENT 'banner图片',
  `group_size` int(11) NOT NULL COMMENT '成团人数',
  `limit_count` int(11) DEFAULT NULL COMMENT '限制次数',
  `limit_quantity` int(11) DEFAULT NULL COMMENT '限购数量',
  `expire_time` int(11) NOT NULL COMMENT '开团过期时间(小时)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_code` (`activity_code`),
  KEY `idx_status_time` (`status`,`start_time`,`end_time`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拼团活动表'
PARTITION BY RANGE (TO_DAYS(start_time)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 拼团商品表 (移除product_name字段，可从product表关联获取)
CREATE TABLE IF NOT EXISTS `group_buy_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_buy_id` bigint(20) NOT NULL COMMENT '拼团活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `group_price` decimal(10,2) NOT NULL COMMENT '拼团价',
  `limit_quantity` int(11) DEFAULT NULL COMMENT '限购数量',
  `sold_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已售数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_buy_sku` (`group_buy_id`,`sku_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拼团商品表'
PARTITION BY HASH(group_buy_id) PARTITIONS 8;

-- 拼团记录表
CREATE TABLE IF NOT EXISTS `group_buy_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_id` varchar(32) NOT NULL COMMENT '拼团ID',
  `group_buy_id` bigint(20) NOT NULL COMMENT '拼团活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `leader_id` bigint(20) NOT NULL COMMENT '团长用户ID',
  `leader_nickname` varchar(64) DEFAULT NULL COMMENT '团长昵称',
  `leader_avatar` varchar(255) DEFAULT NULL COMMENT '团长头像',
  `required_num` int(11) NOT NULL COMMENT '所需人数',
  `current_num` int(11) NOT NULL DEFAULT '1' COMMENT '当前人数',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-拼团中,1-拼团成功,2-拼团失败',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `success_time` datetime DEFAULT NULL COMMENT '成功时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`),
  KEY `idx_group_buy_id` (`group_buy_id`),
  KEY `idx_leader_id` (`leader_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拼团记录表'
PARTITION BY LIST(status) (
  PARTITION p_in_progress VALUES IN (0),
  PARTITION p_success VALUES IN (1),
  PARTITION p_fail VALUES IN (2)
);

-- 拼团成员表
CREATE TABLE IF NOT EXISTS `group_buy_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `group_id` varchar(32) NOT NULL COMMENT '拼团ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `nickname` varchar(64) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `is_leader` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否团长:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待支付,1-已支付,2-已退款',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_user` (`group_id`,`user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='拼团成员表'
PARTITION BY HASH(user_id) PARTITIONS 8;

-- 营销活动记录表
CREATE TABLE IF NOT EXISTS `marketing_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `activity_type` tinyint(4) NOT NULL COMMENT '活动类型:1-满减,2-折扣,3-直降,4-秒杀,5-拼团,6-限时,7-套装',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `product_id` bigint(20) DEFAULT NULL COMMENT '商品ID',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'SKU ID',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动记录表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 优惠券领取记录表
CREATE TABLE IF NOT EXISTS `coupon_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `get_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '获取方式:1-主动领取,2-系统发放,3-活动赠送',
  `source` varchar(64) DEFAULT NULL COMMENT '来源',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券领取记录表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 签到活动表
CREATE TABLE IF NOT EXISTS `sign_in_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `activity_code` varchar(32) NOT NULL COMMENT '活动编码',
  `activity_name` varchar(100) NOT NULL COMMENT '活动名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `rule_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '规则类型:1-连续签到,2-累计签到,3-自定义规则',
  `rule_config` text DEFAULT NULL COMMENT '规则配置JSON',
  `reward_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '奖励类型:1-积分,2-优惠券,3-礼品',
  `reward_config` text DEFAULT NULL COMMENT '奖励配置JSON',
  `description` varchar(255) DEFAULT NULL COMMENT '活动描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_code` (`activity_code`),
  KEY `idx_status_time` (`status`,`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到活动表'
PARTITION BY RANGE (TO_DAYS(start_time)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 用户签到记录表
CREATE TABLE IF NOT EXISTS `user_sign_in` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `activity_id` bigint(20) DEFAULT NULL COMMENT '活动ID',
  `sign_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `sign_date` date NOT NULL COMMENT '签到日期',
  `continuous_days` int(11) NOT NULL DEFAULT '1' COMMENT '连续签到天数',
  `total_days` int(11) NOT NULL DEFAULT '1' COMMENT '累计签到天数',
  `reward_type` tinyint(4) DEFAULT NULL COMMENT '奖励类型',
  `reward_value` varchar(64) DEFAULT NULL COMMENT '奖励值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`sign_date`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_sign_date` (`sign_date`),
  KEY `idx_sign_time` (`sign_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签到记录表'
PARTITION BY RANGE (TO_DAYS(sign_date)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);
