-- 创建订单交易系统数据库
CREATE DATABASE IF NOT EXISTS order_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用订单交易系统数据库
USE order_db;

-- 订单交易系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: order_cart_mvp.sql, 部分logistics_refund_mvp.sql的支付相关表, 部分shop_finance_mvp.sql的支付相关表
-- 高并发优化策略: 
-- 1. 表分区：按时间范围分区、按用户ID哈希分区、按订单状态列表分区
-- 2. 避免外键约束：通过业务逻辑保证数据一致性
-- 3. 索引优化：核心字段索引、组合索引、状态时间复合索引
-- 4. 冷热数据分离：通过时间范围分区实现

-- 订单表（核心订单信息）
CREATE TABLE IF NOT EXISTS `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `order_status` tinyint(4) NOT NULL DEFAULT '10' COMMENT '订单状态:10-待付款,20-待发货,30-待收货,40-已完成,50-已取消,60-已关闭',
  `payment_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态:0-未支付,1-已支付,2-部分支付,3-已退款',
  `shipping_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发货状态:0-未发货,1-已发货,2-部分发货,3-已收货',
  `consignee` varchar(64) NOT NULL COMMENT '收货人',
  `mobile` varchar(16) NOT NULL COMMENT '手机号',
  `address` varchar(255) NOT NULL COMMENT '收货地址',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `freight_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '运费',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表'
PARTITION BY RANGE (TO_DAYS(create_time)) (
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

-- 订单项表（订单商品明细）
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(64) DEFAULT NULL COMMENT 'SKU编码',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'SKU名称',
  `sku_spec` varchar(255) DEFAULT NULL COMMENT 'SKU规格，JSON格式',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `price` decimal(10,2) NOT NULL COMMENT '购买单价',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `real_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `refund_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '退款状态:0-无退款,1-退款中,2-已退款',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表'
PARTITION BY HASH(order_id) PARTITIONS 8;

-- 订单支付表（支付信息）
CREATE TABLE IF NOT EXISTS `order_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `payment_no` varchar(64) NOT NULL COMMENT '支付单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `payment_method` tinyint(4) NOT NULL COMMENT '支付方式:1-微信,2-支付宝,3-银联,4-余额',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态:0-待支付,1-支付中,2-支付成功,3-支付失败',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '支付平台交易号',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `timeout_time` datetime DEFAULT NULL COMMENT '超时时间',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `callback_content` text DEFAULT NULL COMMENT '回调内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表'
PARTITION BY HASH(order_id) PARTITIONS 8;

-- 支付记录表
CREATE TABLE IF NOT EXISTS `payment_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `payment_no` varchar(32) NOT NULL COMMENT '支付单号',
  `payment_type` tinyint(4) NOT NULL COMMENT '支付类型:1-订单,2-充值,3-其他',
  `payment_method` tinyint(4) NOT NULL COMMENT '支付方式:1-微信,2-支付宝,3-银联,4-余额,5-货到付款',
  `relation_id` varchar(64) NOT NULL COMMENT '关联ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待支付,1-支付中,2-已支付,3-支付失败,4-已退款',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `channel_payment_no` varchar(64) DEFAULT NULL COMMENT '渠道支付单号',
  `refund_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已退款金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_relation_id` (`relation_id`),
  KEY `idx_user_shop` (`user_id`, `shop_id`),
  KEY `idx_type_method` (`payment_type`, `payment_method`),
  KEY `idx_status_time` (`status`, `pay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表'
PARTITION BY RANGE (TO_DAYS(create_time)) (
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

-- 订单日志表（订单状态变更记录）
CREATE TABLE IF NOT EXISTS `order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `operation_type` tinyint(4) NOT NULL COMMENT '操作类型:1-创建订单,2-支付订单,3-发货,4-收货,5-取消订单,6-退款',
  `order_status` tinyint(4) NOT NULL COMMENT '订单状态',
  `payment_status` tinyint(4) DEFAULT NULL COMMENT '支付状态',
  `shipping_status` tinyint(4) DEFAULT NULL COMMENT '发货状态',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `operator_role` tinyint(4) DEFAULT NULL COMMENT '操作人角色:1-用户,2-商家,3-管理员,4-系统',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单日志表'
PARTITION BY HASH(order_id) PARTITIONS 8;

-- 订单配送表（物流配送信息）
CREATE TABLE IF NOT EXISTS `order_shipping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `shipping_code` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `shipping_company` varchar(64) DEFAULT NULL COMMENT '物流公司',
  `consignee` varchar(64) NOT NULL COMMENT '收货人',
  `mobile` varchar(16) NOT NULL COMMENT '手机号',
  `province` varchar(32) DEFAULT NULL COMMENT '省份',
  `city` varchar(32) DEFAULT NULL COMMENT '城市',
  `district` varchar(32) DEFAULT NULL COMMENT '区县',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `delivery_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配送状态:0-待发货,1-已发货,2-已收货',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_shipping_code` (`shipping_code`),
  KEY `idx_delivery_status` (`delivery_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单配送表'
PARTITION BY LIST(delivery_status) (
  PARTITION p_pending VALUES IN (0),
  PARTITION p_shipped VALUES IN (1),
  PARTITION p_received VALUES IN (2)
);

-- 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'SKU名称',
  `sku_spec` varchar(255) DEFAULT NULL COMMENT 'SKU规格，JSON格式',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `checked` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否选中:0-未选中,1-已选中',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_checked` (`checked`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 用户地址表
CREATE TABLE IF NOT EXISTS `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(16) NOT NULL COMMENT '收货人电话',
  `province` varchar(32) NOT NULL COMMENT '省份',
  `city` varchar(32) NOT NULL COMMENT '城市',
  `district` varchar(32) NOT NULL COMMENT '区/县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `postal_code` varchar(10) DEFAULT NULL COMMENT '邮政编码',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认:0-否,1-是',
  `tag` varchar(10) DEFAULT NULL COMMENT '地址标签:家,公司,学校等',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表'
PARTITION BY HASH(user_id) PARTITIONS 8;

-- 发票信息表
CREATE TABLE IF NOT EXISTS `invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '发票ID',
  `invoice_no` varchar(32) NOT NULL COMMENT '发票号码',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `invoice_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '发票类型:1-增值税普通发票,2-电子发票,3-增值税专用发票',
  `invoice_title` varchar(100) NOT NULL COMMENT '发票抬头',
  `invoice_content` varchar(64) NOT NULL DEFAULT '商品明细' COMMENT '发票内容',
  `tax_no` varchar(32) DEFAULT NULL COMMENT '税号',
  `amount` decimal(10,2) NOT NULL COMMENT '发票金额',
  `tax_amount` decimal(10,2) DEFAULT NULL COMMENT '税额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待开票,1-已开票,2-已作废',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invoice_no` (`invoice_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order` (`order_id`, `order_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票信息表'
PARTITION BY HASH(user_id) PARTITIONS 8;

-- 用户发票信息表
CREATE TABLE IF NOT EXISTS `user_invoice_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `title` varchar(100) NOT NULL COMMENT '发票抬头',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '抬头类型:1-个人,2-企业',
  `tax_no` varchar(32) DEFAULT NULL COMMENT '税号',
  `register_address` varchar(255) DEFAULT NULL COMMENT '注册地址',
  `register_phone` varchar(20) DEFAULT NULL COMMENT '注册电话',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户银行',
  `bank_account` varchar(32) DEFAULT NULL COMMENT '银行账户',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_default` (`user_id`, `is_default`),
  KEY `idx_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户发票信息表'
PARTITION BY HASH(user_id) PARTITIONS 8; 