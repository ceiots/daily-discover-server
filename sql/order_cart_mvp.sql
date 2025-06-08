-- 订单购物车系统表结构（MVP版本）
-- 设计原则: 每表字段不超过20个，无外键约束，针对高并发高可用场景优化
-- 保留核心功能，去除非必要字段，提高查询性能

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单日志表';

-- 订单配送表（物流配送信息）
CREATE TABLE IF NOT EXISTS `order_shipping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `shipping_code` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `shipping_company` varchar(64) DEFAULT NULL COMMENT '物流公司',
  `shipping_name` varchar(32) DEFAULT NULL COMMENT '物流公司名称',
  `consignee` varchar(64) NOT NULL COMMENT '收货人',
  `mobile` varchar(16) NOT NULL COMMENT '手机号',
  `province` varchar(32) DEFAULT NULL COMMENT '省份',
  `city` varchar(32) DEFAULT NULL COMMENT '城市',
  `district` varchar(32) DEFAULT NULL COMMENT '区县',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `delivery_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '配送状态:0-待发货,1-已发货,2-已收货',
  `tracking_data` text DEFAULT NULL COMMENT '物流跟踪数据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_shipping_code` (`shipping_code`),
  KEY `idx_delivery_status` (`delivery_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单配送表';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表'; 