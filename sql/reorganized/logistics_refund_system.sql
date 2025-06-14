-- 创建物流退款系统数据库
CREATE DATABASE IF NOT EXISTS logistics_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用物流退款系统数据库
USE logistics_db;

-- 物流退款系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: logistics_refund_mvp.sql
-- 高并发优化策略: 1. 时间范围分区 2. 用户哈希分区 3. 状态列表分区 4. 冷热数据分离 5. 定期数据归档

-- 物流订单表（记录与物流相关的订单信息）
CREATE TABLE IF NOT EXISTS `logistics_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物流订单ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `logistics_no` varchar(50) DEFAULT NULL COMMENT '物流单号',
  `logistics_company` varchar(50) DEFAULT NULL COMMENT '物流公司',
  `logistics_company_code` varchar(20) DEFAULT NULL COMMENT '物流公司代码',
  `receiver_name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `receiver_address` varchar(255) NOT NULL COMMENT '收货地址',
  `sender_name` varchar(50) NOT NULL COMMENT '发货人姓名',
  `sender_phone` varchar(20) NOT NULL COMMENT '发货人电话',
  `sender_address` varchar(255) NOT NULL COMMENT '发货详细地址',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `shipping_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '运费',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待发货,1-已发货,2-已签收,3-异常',
  `delivery_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '配送方式:1-普通快递,2-加急,3-同城配送,4-自提',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_receiver_phone` (`receiver_phone`),
  KEY `idx_delivery_time` (`delivery_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流订单表'
PARTITION BY RANGE (TO_DAYS(delivery_time)) (
  PARTITION p_null VALUES LESS THAN (0),
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

-- 物流跟踪记录表 (记录物流状态变化)
CREATE TABLE IF NOT EXISTS `logistics_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `logistics_order_id` bigint(20) NOT NULL COMMENT '物流订单ID',
  `logistics_no` varchar(50) NOT NULL COMMENT '物流单号',
  `tracking_status` varchar(50) NOT NULL COMMENT '物流状态',
  `status_code` varchar(20) DEFAULT NULL COMMENT '状态代码',
  `location` varchar(100) DEFAULT NULL COMMENT '当前位置',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作员',
  `tracking_desc` varchar(255) NOT NULL COMMENT '跟踪描述',
  `tracking_time` datetime NOT NULL COMMENT '跟踪时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_logistics_order_id` (`logistics_order_id`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_tracking_time` (`tracking_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流跟踪记录表'
PARTITION BY RANGE (TO_DAYS(tracking_time)) (
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

-- 退款申请表 (记录退款申请信息)
CREATE TABLE IF NOT EXISTS `refund_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款编号',
  `refund_type` tinyint(4) NOT NULL COMMENT '退款类型:1-仅退款,2-退货退款',
  `refund_reason_type` tinyint(4) NOT NULL COMMENT '退款原因类型',
  `refund_reason` varchar(255) NOT NULL COMMENT '退款原因',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_method` tinyint(4) DEFAULT NULL COMMENT '退款方式:1-原路退回,2-退到余额',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `evidence_images` varchar(1000) DEFAULT NULL COMMENT '凭证图片(JSON数组)',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待处理,1-处理中,2-已退款,3-已拒绝,4-已取消',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_time` (`apply_time`),
  KEY `idx_refund_time` (`refund_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 退款处理记录表 (记录退款处理过程)
CREATE TABLE IF NOT EXISTS `refund_process_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款编号',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `operator_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '操作人类型:1-用户,2-商家,3-平台',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人名称',
  `action` tinyint(4) NOT NULL COMMENT '操作类型:1-提交申请,2-商家处理,3-平台介入,4-退款关闭,5-退款成功',
  `status_before` tinyint(4) NOT NULL COMMENT '操作前状态',
  `status_after` tinyint(4) NOT NULL COMMENT '操作后状态',
  `remark` varchar(255) DEFAULT NULL COMMENT '操作备注',
  `attachment` varchar(1000) DEFAULT NULL COMMENT '附件(JSON数组)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_operator_id` (`operator_id`, `operator_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款处理记录表'
PARTITION BY HASH(refund_id) PARTITIONS 8;

-- 退货物流表 (记录退货物流信息)
CREATE TABLE IF NOT EXISTS `return_logistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款编号',
  `logistics_no` varchar(50) DEFAULT NULL COMMENT '物流单号',
  `logistics_company` varchar(50) DEFAULT NULL COMMENT '物流公司',
  `logistics_company_code` varchar(20) DEFAULT NULL COMMENT '物流公司代码',
  `return_address` varchar(255) NOT NULL COMMENT '退货地址',
  `return_contact` varchar(50) NOT NULL COMMENT '退货联系人',
  `return_phone` varchar(20) NOT NULL COMMENT '退货联系电话',
  `sender_name` varchar(50) DEFAULT NULL COMMENT '发货人姓名',
  `sender_phone` varchar(20) DEFAULT NULL COMMENT '发货人电话',
  `sender_address` varchar(255) DEFAULT NULL COMMENT '发货地址',
  `tracking_link` varchar(255) DEFAULT NULL COMMENT '物流追踪链接',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待发货,1-已发货,2-已收货,3-异常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货物流表'
PARTITION BY LIST(status) (
  PARTITION p_pending VALUES IN (0),
  PARTITION p_shipped VALUES IN (1),
  PARTITION p_received VALUES IN (2),
  PARTITION p_abnormal VALUES IN (3)
);

-- 退款商品表 (记录退款涉及的商品)
CREATE TABLE IF NOT EXISTS `refund_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_item_id` bigint(20) NOT NULL COMMENT '订单项ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(50) DEFAULT NULL COMMENT 'SKU编码',
  `sku_name` varchar(100) DEFAULT NULL COMMENT 'SKU名称',
  `sku_attrs` varchar(255) DEFAULT NULL COMMENT 'SKU属性',
  `quantity` int(11) NOT NULL COMMENT '退款数量',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_shipping_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退运费',
  `is_received` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已收到货:0-未收到,1-已收到',
  `is_opened` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已拆封:0-未拆封,1-已拆封',
  `problem_desc` varchar(255) DEFAULT NULL COMMENT '问题描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_item_id` (`order_item_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款商品表'
PARTITION BY HASH(refund_id) PARTITIONS 8;

-- 物流公司表 (记录支持的物流公司信息)
CREATE TABLE IF NOT EXISTS `logistics_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `company_code` varchar(20) NOT NULL COMMENT '公司编码',
  `company_name` varchar(50) NOT NULL COMMENT '公司名称',
  `company_logo` varchar(255) DEFAULT NULL COMMENT '公司Logo',
  `api_code` varchar(50) DEFAULT NULL COMMENT 'API编码',
  `tracking_url` varchar(255) DEFAULT NULL COMMENT '追踪URL',
  `url_pattern` varchar(255) DEFAULT NULL COMMENT 'URL模式',
  `customer_service_phone` varchar(20) DEFAULT NULL COMMENT '客服电话',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热门:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_company_code` (`company_code`),
  KEY `idx_status` (`status`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流公司表'
PARTITION BY LIST(status) (
  PARTITION p_enabled VALUES IN (1),
  PARTITION p_disabled VALUES IN (0)
);

-- 运费模板表 (记录商家设置的运费规则)
CREATE TABLE IF NOT EXISTS `shipping_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `template_name` varchar(50) NOT NULL COMMENT '模板名称',
  `charge_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '计费方式:1-按件数,2-按重量,3-按体积',
  `delivery_deadline` int(11) DEFAULT NULL COMMENT '发货期限(小时)',
  `free_shipping` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包邮:0-否,1-是',
  `default_first_piece` int(11) DEFAULT NULL COMMENT '默认首件/重/体积',
  `default_first_fee` decimal(10,2) DEFAULT NULL COMMENT '默认首费',
  `default_additional_piece` int(11) DEFAULT NULL COMMENT '默认续件/重/体积',
  `default_additional_fee` decimal(10,2) DEFAULT NULL COMMENT '默认续费',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运费模板表'
PARTITION BY HASH(shop_id) PARTITIONS 8;

-- 运费模板规则表 (记录运费模板的详细规则)
CREATE TABLE IF NOT EXISTS `shipping_template_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `region_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '区域类型:1-省,2-市',
  `region_codes` varchar(500) NOT NULL COMMENT '区域编码,逗号分隔',
  `region_names` varchar(500) NOT NULL COMMENT '区域名称,逗号分隔',
  `first_piece` int(11) NOT NULL COMMENT '首件/重/体积',
  `first_fee` decimal(10,2) NOT NULL COMMENT '首费',
  `additional_piece` int(11) DEFAULT NULL COMMENT '续件/重/体积',
  `additional_fee` decimal(10,2) DEFAULT NULL COMMENT '续费',
  `is_free_shipping` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包邮:0-否,1-是',
  `free_condition_amount` decimal(10,2) DEFAULT NULL COMMENT '满额包邮条件',
  `free_condition_piece` int(11) DEFAULT NULL COMMENT '满件包邮条件',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运费模板规则表'
PARTITION BY HASH(template_id) PARTITIONS 8;

-- 售后服务记录表 (记录售后服务)
CREATE TABLE IF NOT EXISTS `after_sale_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `service_no` varchar(32) NOT NULL COMMENT '服务编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `service_type` tinyint(4) NOT NULL COMMENT '服务类型:1-退款,2-换货,3-维修,4-补发',
  `service_reason` varchar(255) NOT NULL COMMENT '服务原因',
  `reason_type` tinyint(4) NOT NULL COMMENT '原因类型',
  `description` varchar(500) DEFAULT NULL COMMENT '问题描述',
  `evidence_images` varchar(1000) DEFAULT NULL COMMENT '凭证图片(JSON数组)',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待处理,1-处理中,2-已完成,3-已拒绝,4-已取消',
  `refund_id` bigint(20) DEFAULT NULL COMMENT '关联退款ID',
  `refund_no` varchar(32) DEFAULT NULL COMMENT '关联退款编号',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_service_no` (`service_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`),
  KEY `idx_refund_id` (`refund_id`),
  KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='售后服务记录表';

-- 换货记录表 (记录换货信息)
CREATE TABLE IF NOT EXISTS `exchange_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `service_id` bigint(20) NOT NULL COMMENT '售后服务ID',
  `service_no` varchar(32) NOT NULL COMMENT '服务编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `return_logistics_id` bigint(20) DEFAULT NULL COMMENT '退货物流ID',
  `return_logistics_no` varchar(50) DEFAULT NULL COMMENT '退货物流单号',
  `return_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '退回状态:0-待退回,1-已退回,2-已收到退回',
  `delivery_logistics_id` bigint(20) DEFAULT NULL COMMENT '发货物流ID',
  `delivery_logistics_no` varchar(50) DEFAULT NULL COMMENT '发货物流单号',
  `delivery_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发货状态:0-待发货,1-已发货,2-已签收',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-处理中,1-已完成,2-已取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_service_no` (`service_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='换货记录表'
PARTITION BY HASH(user_id) PARTITIONS 8;

-- 订单配送表 (记录同城配送和自提信息)
CREATE TABLE IF NOT EXISTS `order_delivery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `logistics_order_id` bigint(20) DEFAULT NULL COMMENT '物流订单ID',
  `delivery_type` tinyint(4) NOT NULL COMMENT '配送类型:1-同城配送,2-到店自提',
  `delivery_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配送状态:0-待配送,1-配送中,2-已完成,3-已取消',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `shop_name` varchar(100) DEFAULT NULL COMMENT '店铺名称',
  `shop_address` varchar(255) DEFAULT NULL COMMENT '店铺地址',
  `shop_phone` varchar(20) DEFAULT NULL COMMENT '店铺电话',
  `deliverer_id` bigint(20) DEFAULT NULL COMMENT '配送员ID',
  `deliverer_name` varchar(50) DEFAULT NULL COMMENT '配送员姓名',
  `deliverer_phone` varchar(20) DEFAULT NULL COMMENT '配送员电话',
  `pickup_code` varchar(20) DEFAULT NULL COMMENT '取货码',
  `expected_pickup_time` datetime DEFAULT NULL COMMENT '预计自提时间',
  `expected_delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `actual_delivery_time` datetime DEFAULT NULL COMMENT '实际送达时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_logistics_order_id` (`logistics_order_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_deliverer_id` (`deliverer_id`),
  KEY `idx_pickup_code` (`pickup_code`),
  KEY `idx_delivery_status` (`delivery_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单配送表'
PARTITION BY LIST(delivery_status) (
  PARTITION p_pending VALUES IN (0),
  PARTITION p_delivering VALUES IN (1),
  PARTITION p_completed VALUES IN (2),
  PARTITION p_cancelled VALUES IN (3)
);
