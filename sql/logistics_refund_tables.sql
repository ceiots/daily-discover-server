-- 物流公司表
CREATE TABLE IF NOT EXISTS `logistics_company` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物流公司ID',
  `name` varchar(50) NOT NULL COMMENT '物流公司名称',
  `code` varchar(20) NOT NULL COMMENT '物流公司编码',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '客服电话',
  `website` varchar(100) DEFAULT NULL COMMENT '官网地址',
  `logo_url` varchar(255) DEFAULT NULL COMMENT 'logo图片',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流公司表';

-- 物流订单主表
CREATE TABLE IF NOT EXISTS `logistics_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物流订单ID',
  `logistics_no` varchar(32) NOT NULL COMMENT '物流单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `logistics_company` varchar(50) NOT NULL COMMENT '物流公司名称',
  `company_code` varchar(20) NOT NULL COMMENT '物流公司编码',
  `tracking_number` varchar(50) NOT NULL COMMENT '物流跟踪号',
  `logistics_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '物流状态:1-待发货,2-已发货,3-运输中,4-已签收,5-异常',
  `logistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '物流类型:1-发货,2-退货',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `estimated_delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_logistics_no` (`logistics_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_logistics_status` (`logistics_status`),
  KEY `idx_shipping_time` (`shipping_time`),
  KEY `idx_delivery_time` (`delivery_time`),
  KEY `idx_company_code` (`company_code`),
  KEY `idx_tracking_number` (`tracking_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流订单主表';

-- 物流地址表（发件人和收件人地址）
CREATE TABLE IF NOT EXISTS `logistics_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '物流地址ID',
  `logistics_id` bigint(20) NOT NULL COMMENT '物流订单ID',
  `logistics_no` varchar(32) NOT NULL COMMENT '物流单号',
  `address_type` tinyint(4) NOT NULL COMMENT '地址类型:1-发件人,2-收件人',
  `name` varchar(64) NOT NULL COMMENT '姓名',
  `phone` varchar(16) NOT NULL COMMENT '电话',
  `province` varchar(32) NOT NULL COMMENT '省份',
  `city` varchar(32) NOT NULL COMMENT '城市',
  `district` varchar(32) NOT NULL COMMENT '区/县',
  `address` varchar(255) NOT NULL COMMENT '详细地址',
  `zip_code` varchar(16) DEFAULT NULL COMMENT '邮编',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_logistics_id_type` (`logistics_id`,`address_type`),
  KEY `idx_logistics_no` (`logistics_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流地址表';

-- 物流跟踪记录表
CREATE TABLE IF NOT EXISTS `logistics_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '跟踪记录ID',
  `logistics_id` bigint(20) NOT NULL COMMENT '物流订单ID',
  `logistics_no` varchar(32) NOT NULL COMMENT '物流单号',
  `tracking_info` varchar(500) NOT NULL COMMENT '跟踪信息',
  `tracking_location` varchar(255) DEFAULT NULL COMMENT '当前位置',
  `tracking_time` datetime NOT NULL COMMENT '跟踪时间',
  `tracking_status` tinyint(4) NOT NULL COMMENT '跟踪状态:1-已揽收,2-运输中,3-派送中,4-已签收,5-异常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_logistics_id` (`logistics_id`),
  KEY `idx_logistics_no` (`logistics_no`),
  KEY `idx_tracking_time` (`tracking_time`),
  KEY `idx_tracking_status` (`tracking_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流跟踪记录表';

-- 退款申请主表
CREATE TABLE IF NOT EXISTS `refund_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款申请ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单商品ID,NULL表示整单退款',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '退款类型:1-仅退款,2-退货退款',
  `refund_reason_type` tinyint(4) NOT NULL COMMENT '退款原因类型:1-质量问题,2-尺寸不合适,3-颜色/款式与描述不符,4-发错货,5-不想要了,6-其他',
  `refund_reason` varchar(255) NOT NULL COMMENT '退款原因',
  `refund_description` varchar(500) DEFAULT NULL COMMENT '详细描述',
  `evidence_images` json DEFAULT NULL COMMENT '凭证图片',
  `refund_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '退款状态:1-待处理,2-商家同意,3-商家拒绝,4-退货中,5-商家收货,6-退款成功,7-退款关闭',
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
  KEY `idx_create_time` (`create_time`),
  KEY `idx_refund_type` (`refund_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款申请主表';

-- 退款处理表（商家处理记录）
CREATE TABLE IF NOT EXISTS `refund_process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '处理记录ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `process_status` tinyint(4) NOT NULL COMMENT '处理状态:1-待处理,2-已同意,3-已拒绝,4-已收货,5-已退款',
  `process_result` tinyint(4) DEFAULT NULL COMMENT '处理结果:1-同意,2-拒绝',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `operator_type` tinyint(4) NOT NULL DEFAULT '2' COMMENT '操作人类型:1-用户,2-商家,3-系统,4-管理员',
  `refuse_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `operator_note` varchar(500) DEFAULT NULL COMMENT '操作备注',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_process_status` (`process_status`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款处理表';

-- 退款物流表（退货物流信息）
CREATE TABLE IF NOT EXISTS `refund_logistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款物流ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `logistics_company` varchar(50) DEFAULT NULL COMMENT '物流公司名称',
  `company_code` varchar(20) DEFAULT NULL COMMENT '物流公司编码',
  `tracking_number` varchar(50) DEFAULT NULL COMMENT '物流跟踪号',
  `logistics_status` tinyint(4) DEFAULT '1' COMMENT '物流状态:1-待发货,2-已发货,3-运输中,4-已签收,5-异常',
  `shipping_time` datetime DEFAULT NULL COMMENT '退货发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '商家收货时间',
  `receiver_name` varchar(64) DEFAULT NULL COMMENT '收货人姓名（商家）',
  `receiver_phone` varchar(16) DEFAULT NULL COMMENT '收货人电话（商家）',
  `receiver_address` varchar(255) DEFAULT NULL COMMENT '收货地址（商家）',
  `tracking_data` json DEFAULT NULL COMMENT '物流跟踪数据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_tracking_number` (`tracking_number`),
  KEY `idx_logistics_status` (`logistics_status`),
  KEY `idx_shipping_time` (`shipping_time`),
  KEY `idx_receive_time` (`receive_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款物流表';

-- 退款支付表（退款金额处理信息）
CREATE TABLE IF NOT EXISTS `refund_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款支付ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `payment_no` varchar(100) DEFAULT NULL COMMENT '支付退款流水号',
  `original_payment_no` varchar(100) DEFAULT NULL COMMENT '原支付流水号',
  `payment_method` tinyint(4) DEFAULT NULL COMMENT '退款方式:1-原路退回,2-退到余额,3-手动处理',
  `payment_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '退款状态:1-待退款,2-退款中,3-退款成功,4-退款失败',
  `payment_time` datetime DEFAULT NULL COMMENT '退款时间',
  `payment_remark` varchar(255) DEFAULT NULL COMMENT '退款备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_payment_no` (`payment_no`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款支付表';

-- 退货商品表（退货的商品信息）
CREATE TABLE IF NOT EXISTS `return_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退货商品ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_item_id` bigint(20) NOT NULL COMMENT '订单商品ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
  `product_name` varchar(255) NOT NULL COMMENT '商品名称',
  `sku_properties_name` varchar(500) DEFAULT NULL COMMENT 'SKU规格名称',
  `quantity` int(11) NOT NULL COMMENT '退货数量',
  `price` decimal(10,2) NOT NULL COMMENT '商品单价',
  `goods_status` tinyint(4) DEFAULT '1' COMMENT '商品状态:1-待退货,2-已退货,3-已收货,4-已拒收',
  `description` varchar(500) DEFAULT NULL COMMENT '商品描述（退货原因）',
  `evidence_images` json DEFAULT NULL COMMENT '凭证图片',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_item_id` (`order_item_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_goods_status` (`goods_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退货商品表';

-- 退款操作日志表
CREATE TABLE IF NOT EXISTS `refund_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `refund_id` bigint(20) NOT NULL COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `previous_status` tinyint(4) DEFAULT NULL COMMENT '操作前状态',
  `current_status` tinyint(4) NOT NULL COMMENT '操作后状态',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_type` tinyint(4) DEFAULT '1' COMMENT '操作人类型:1-用户,2-商家,3-系统,4-管理员',
  `operation` varchar(64) NOT NULL COMMENT '操作类型',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `ip` varchar(64) DEFAULT NULL COMMENT '操作IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_refund_id` (`refund_id`),
  KEY `idx_refund_no` (`refund_no`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_current_status` (`current_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款操作日志表'; 