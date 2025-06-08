-- 订单主表（基础信息）- 拆分后更聚焦
CREATE TABLE IF NOT EXISTS `order_main` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `order_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单类型:1-普通订单,2-团购订单,3-秒杀订单',
  `order_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单状态:1-待付款,2-待发货,3-待收货,4-已完成,5-已取消,6-已退款',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `expired_time` datetime DEFAULT NULL COMMENT '订单过期时间',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_order_type` (`order_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单金额表（拆分自order表，专注于金额相关字段）
CREATE TABLE IF NOT EXISTS `order_amount` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `coupon_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠券抵扣金额',
  `points_amount` decimal(10,2) DEFAULT '0.00' COMMENT '积分抵扣金额',
  `shipping_amount` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `tax_amount` decimal(10,2) DEFAULT '0.00' COMMENT '税费',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单金额表';

-- 订单支付表（拆分自order表，专注于支付相关字段）
CREATE TABLE IF NOT EXISTS `order_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `pay_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态:0-未支付,1-已支付,2-部分退款,3-全额退款',
  `payment_method` tinyint(4) DEFAULT NULL COMMENT '支付方式:1-微信,2-支付宝,3-银联,4-余额',
  `payment_no` varchar(100) DEFAULT NULL COMMENT '支付流水号',
  `payment_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payment_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态:0-未支付,1-支付中,2-已支付,3-支付失败',
  `expire_time` datetime DEFAULT NULL COMMENT '支付过期时间',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `callback_content` text DEFAULT NULL COMMENT '回调内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_payment_no` (`payment_no`),
  KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

-- 订单状态变更表（拆分自order表，专注于状态变更相关字段）
CREATE TABLE IF NOT EXISTS `order_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `current_status` tinyint(4) NOT NULL COMMENT '当前状态',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `cancel_type` tinyint(4) DEFAULT NULL COMMENT '取消类型:1-用户取消,2-超时取消,3-商家取消,4-系统取消',
  `remarks` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_current_status` (`current_status`),
  KEY `idx_pay_time` (`pay_time`),
  KEY `idx_ship_time` (`ship_time`),
  KEY `idx_cancel_time` (`cancel_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态变更表';

-- 订单商品表（优化版）
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
  `product_name` varchar(255) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `sku_properties_name` varchar(500) DEFAULT NULL COMMENT 'SKU规格名称，例如：颜色:红色;尺码:XL',
  `price` decimal(10,2) NOT NULL COMMENT '单价',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '商品总金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '实付金额',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '优惠金额',
  `refund_status` tinyint(4) DEFAULT '0' COMMENT '退款状态:0-无退款,1-申请中,2-已退款',
  `refund_amount` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额',
  `review_status` tinyint(4) DEFAULT '0' COMMENT '评价状态:0-未评价,1-已评价',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_refund_status` (`refund_status`),
  KEY `idx_review_status` (`review_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 订单商品快照表（单独存储商品详细信息，减轻order_item表负担）
CREATE TABLE IF NOT EXISTS `order_item_snapshot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_item_id` bigint(20) NOT NULL COMMENT '订单商品ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
  `product_snapshot` json NOT NULL COMMENT '商品快照，包含下单时的商品详细信息',
  `sku_snapshot` json NOT NULL COMMENT 'SKU快照，包含下单时的SKU详细信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_item_id` (`order_item_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品快照表';

-- 订单收货地址表（优化版）
CREATE TABLE IF NOT EXISTS `order_shipping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(16) NOT NULL COMMENT '收货人电话',
  `receiver_province` varchar(32) NOT NULL COMMENT '省份',
  `receiver_city` varchar(32) NOT NULL COMMENT '城市',
  `receiver_district` varchar(32) NOT NULL COMMENT '区/县',
  `receiver_address` varchar(255) NOT NULL COMMENT '详细地址',
  `receiver_zip` varchar(16) DEFAULT NULL COMMENT '邮编',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单收货地址表';

-- 订单物流表（从order_shipping拆分，专注于物流信息）
CREATE TABLE IF NOT EXISTS `order_logistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `logistics_company` varchar(32) DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(32) DEFAULT NULL COMMENT '物流单号',
  `logistics_status` tinyint(4) DEFAULT '0' COMMENT '物流状态:0-未发货,1-已发货,2-运输中,3-已签收,4-异常',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `tracking_data` json DEFAULT NULL COMMENT '物流跟踪数据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_logistics_status` (`logistics_status`),
  KEY `idx_shipping_time` (`shipping_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单物流表';

-- 订单操作日志表（优化版）
CREATE TABLE IF NOT EXISTS `order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `previous_status` tinyint(4) DEFAULT NULL COMMENT '操作前状态',
  `current_status` tinyint(4) NOT NULL COMMENT '操作后状态',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID:用户ID或系统ID',
  `operator_type` tinyint(4) DEFAULT '1' COMMENT '操作人类型:1-用户,2-商家,3-系统,4-管理员',
  `operation` varchar(64) NOT NULL COMMENT '操作类型',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `ip` varchar(64) DEFAULT NULL COMMENT '操作IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_current_status` (`current_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';

-- 订单退款表（优化版）
CREATE TABLE IF NOT EXISTS `order_refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单商品ID，NULL表示整单退款',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_reason_type` tinyint(4) NOT NULL COMMENT '退款原因类型:1-质量问题,2-尺寸不合适,3-颜色/款式与描述不符,4-发错货,5-不想要了,6-其他',
  `refund_reason` varchar(255) NOT NULL COMMENT '退款原因',
  `refund_desc` varchar(500) DEFAULT NULL COMMENT '详细描述',
  `refund_evidence` json DEFAULT NULL COMMENT '凭证图片',
  `refund_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '退款类型:1-仅退款,2-退货退款',
  `refund_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '退款状态:0-申请中,1-商家同意,2-商家拒绝,3-退款成功,4-退款关闭',
  `refuse_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `refund_payment_no` varchar(100) DEFAULT NULL COMMENT '退款支付流水号',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_order_item_id` (`order_item_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_refund_status` (`refund_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款表';

-- 订单退货物流表（从order_refund拆分，专注于退货物流信息）
CREATE TABLE IF NOT EXISTS `order_refund_logistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `logistics_company` varchar(32) DEFAULT NULL COMMENT '退货物流公司',
  `logistics_no` varchar(32) DEFAULT NULL COMMENT '退货物流单号',
  `logistics_status` tinyint(4) DEFAULT '0' COMMENT '物流状态:0-未发货,1-已发货,2-运输中,3-已签收',
  `shipping_time` datetime DEFAULT NULL COMMENT '退货发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '商家收货时间',
  `tracking_data` json DEFAULT NULL COMMENT '物流跟踪数据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_logistics_status` (`logistics_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退货物流表';

-- 购物车表（优化版）
CREATE TABLE IF NOT EXISTS `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
  `checked` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否选中:0-未选中,1-已选中',
  `price` decimal(10,2) NOT NULL COMMENT '加入时单价',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_sku` (`user_id`,`product_sku_id`),
  KEY `idx_user_id_checked` (`user_id`, `checked`), -- 组合索引优化查询购物车已勾选商品
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`product_sku_id`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 用户收货地址表（优化版）
CREATE TABLE IF NOT EXISTS `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(16) NOT NULL COMMENT '收货人电话',
  `receiver_province` varchar(32) NOT NULL COMMENT '省份',
  `receiver_city` varchar(32) NOT NULL COMMENT '城市',
  `receiver_district` varchar(32) NOT NULL COMMENT '区/县',
  `receiver_address` varchar(255) NOT NULL COMMENT '详细地址',
  `receiver_zip` varchar(16) DEFAULT NULL COMMENT '邮编',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认:0-否,1-是',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id_default_deleted` (`user_id`, `is_default`, `is_deleted`), -- 组合索引优化用户地址查询
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- 订单统计表（按日统计）（优化版）
CREATE TABLE IF NOT EXISTS `order_statistics_daily` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `stat_date` date NOT NULL COMMENT '统计日期',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID，NULL表示全平台',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `paid_order_count` int(11) NOT NULL DEFAULT '0' COMMENT '已支付订单数',
  `paid_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '已支付金额',
  `refund_order_count` int(11) NOT NULL DEFAULT '0' COMMENT '退款订单数',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `new_customer_count` int(11) NOT NULL DEFAULT '0' COMMENT '新客户数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_shop` (`stat_date`,`shop_id`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单统计表（按日统计）'; 