-- 订单主表（订单基础信息）
CREATE TABLE IF NOT EXISTS `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `order_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单类型:1-普通,2-团购,3-秒杀',
  `order_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '订单状态:1-待付款,2-待发货,3-待收货,4-已完成,5-已取消,6-已退款',
  `pay_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态:0-未支付,1-已支付,2-部分退款,3-全额退款',
  `shipping_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '物流状态:0-未发货,1-已发货,2-已收货',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '应付金额',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `expired_time` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 订单商品表（订单商品明细）
CREATE TABLE IF NOT EXISTS `order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `sku_spec_data` json DEFAULT NULL COMMENT 'SKU规格',
  `price` decimal(10,2) NOT NULL COMMENT '销售价格',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `refund_status` tinyint(4) DEFAULT '0' COMMENT '退款状态:0-无退款,1-申请中,2-已退款',
  `refund_amount` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额',
  `review_status` tinyint(4) DEFAULT '0' COMMENT '评价状态:0-未评价,1-已评价',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_refund_status` (`refund_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 订单支付表（支付信息）
CREATE TABLE IF NOT EXISTS `order_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `payment_method` tinyint(4) DEFAULT NULL COMMENT '支付方式:1-微信,2-支付宝,3-银联,4-余额',
  `payment_no` varchar(100) DEFAULT NULL COMMENT '支付流水号',
  `payment_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `payment_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态:0-未支付,1-支付中,2-已支付,3-支付失败',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `payment_expire_time` datetime DEFAULT NULL COMMENT '支付过期时间',
  `callback_time` datetime DEFAULT NULL COMMENT '回调时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_payment_no` (`payment_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

-- 订单收货信息表（收货地址与物流信息）
CREATE TABLE IF NOT EXISTS `order_shipping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收货信息ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(16) NOT NULL COMMENT '收货人电话',
  `receiver_province` varchar(32) NOT NULL COMMENT '省份',
  `receiver_city` varchar(32) NOT NULL COMMENT '城市',
  `receiver_district` varchar(32) NOT NULL COMMENT '区/县',
  `receiver_address` varchar(255) NOT NULL COMMENT '详细地址',
  `logistics_company` varchar(32) DEFAULT NULL COMMENT '物流公司',
  `logistics_no` varchar(32) DEFAULT NULL COMMENT '物流单号',
  `logistics_status` tinyint(4) DEFAULT '0' COMMENT '物流状态:0-未发货,1-已发货,2-运输中,3-已签收,4-异常',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_logistics_status` (`logistics_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单收货信息表';

-- 订单日志表（订单状态变更日志）
CREATE TABLE IF NOT EXISTS `order_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `previous_status` tinyint(4) DEFAULT NULL COMMENT '操作前状态',
  `current_status` tinyint(4) NOT NULL COMMENT '操作后状态',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_type` tinyint(4) DEFAULT '1' COMMENT '操作人类型:1-用户,2-商家,3-系统,4-管理员',
  `operation` varchar(64) NOT NULL COMMENT '操作类型',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单日志表';

-- 订单退款表（退款信息）
CREATE TABLE IF NOT EXISTS `order_refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款ID',
  `refund_no` varchar(32) NOT NULL COMMENT '退款单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单商品ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额',
  `refund_reason_type` tinyint(4) NOT NULL COMMENT '退款原因类型',
  `refund_reason` varchar(255) NOT NULL COMMENT '退款原因',
  `refund_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '退款类型:1-仅退款,2-退货退款',
  `refund_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '退款状态:0-申请中,1-商家同意,2-商家拒绝,3-退款成功,4-退款关闭',
  `refund_payment_no` varchar(100) DEFAULT NULL COMMENT '退款支付流水号',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_refund_status` (`refund_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单退款表';

-- 购物车表（用户购物车）
CREATE TABLE IF NOT EXISTS `cart` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '加入时价格',
  `checked` tinyint(1) DEFAULT '1' COMMENT '是否选中结算:1-选中,0-未选中',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product_sku` (`user_id`,`product_id`,`sku_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_checked` (`checked`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 用户收货地址表（用户收货信息）
CREATE TABLE IF NOT EXISTS `user_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(16) NOT NULL COMMENT '收货人电话',
  `province` varchar(32) NOT NULL COMMENT '省份',
  `city` varchar(32) NOT NULL COMMENT '城市',
  `district` varchar(32) NOT NULL COMMENT '区/县',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `zip_code` varchar(16) DEFAULT NULL COMMENT '邮编',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址:1-是,0-否',
  `label` varchar(16) DEFAULT NULL COMMENT '地址标签:家、公司等',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_default` (`user_id`,`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- 订单统计表（订单业务统计）
CREATE TABLE IF NOT EXISTS `order_statistics_daily` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单总数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `paid_order_count` int(11) NOT NULL DEFAULT '0' COMMENT '已支付订单数',
  `paid_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '已支付金额',
  `refund_order_count` int(11) NOT NULL DEFAULT '0' COMMENT '退款订单数',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单用户数',
  `new_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '新用户下单数',
  `per_customer_transaction` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '客单价',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_date` (`shop_id`,`statistics_date`),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单统计表'; 