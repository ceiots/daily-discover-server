-- 店铺信息表
CREATE TABLE IF NOT EXISTS `shop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  `name` varchar(64) NOT NULL COMMENT '店铺名称',
  `logo` varchar(255) DEFAULT NULL COMMENT '店铺logo',
  `shop_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '店铺类型:1-普通,2-旗舰,3-专营,4-官方',
  `user_id` bigint(20) NOT NULL COMMENT '店主用户ID',
  `contact_name` varchar(50) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
  `contact_email` varchar(64) DEFAULT NULL COMMENT '联系人邮箱',
  `province` varchar(32) DEFAULT NULL COMMENT '所在省',
  `city` varchar(32) DEFAULT NULL COMMENT '所在市',
  `district` varchar(32) DEFAULT NULL COMMENT '所在区',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `description` varchar(500) DEFAULT NULL COMMENT '店铺描述',
  `announcement` varchar(500) DEFAULT NULL COMMENT '店铺公告',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待审核,1-正常,2-关闭,3-冻结',
  `audit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审核状态:0-待审核,1-审核通过,2-审核拒绝',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_user_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `open_time` datetime DEFAULT NULL COMMENT '开店时间',
  `close_time` datetime DEFAULT NULL COMMENT '关店时间',
  `business_scope` varchar(500) DEFAULT NULL COMMENT '经营范围',
  `rating` decimal(2,1) NOT NULL DEFAULT '5.0' COMMENT '店铺评分',
  `rating_count` int(11) NOT NULL DEFAULT '0' COMMENT '评分数量',
  `product_count` int(11) NOT NULL DEFAULT '0' COMMENT '商品数量',
  `sold_count` int(11) NOT NULL DEFAULT '0' COMMENT '已售商品数',
  `sale_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '销售总额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_shop_type` (`shop_type`),
  KEY `idx_rating` (`rating`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺信息表';

-- 店铺资质表
CREATE TABLE IF NOT EXISTS `shop_qualification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `type` tinyint(4) NOT NULL COMMENT '资质类型:1-营业执照,2-法人身份证,3-开户许可,4-商标,5-品牌授权,6-质检报告,7-其他',
  `name` varchar(64) NOT NULL COMMENT '资质名称',
  `number` varchar(64) DEFAULT NULL COMMENT '证件号码',
  `expiry_date` date DEFAULT NULL COMMENT '有效期',
  `image_url` varchar(255) NOT NULL COMMENT '资质图片',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待审核,1-有效,2-无效,3-过期',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_expiry_date` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺资质表';

-- 店铺评分表
CREATE TABLE IF NOT EXISTS `shop_rating` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `product_rating` decimal(2,1) NOT NULL DEFAULT '5.0' COMMENT '商品评分',
  `service_rating` decimal(2,1) NOT NULL DEFAULT '5.0' COMMENT '服务评分',
  `logistics_rating` decimal(2,1) NOT NULL DEFAULT '5.0' COMMENT '物流评分',
  `average_rating` decimal(2,1) NOT NULL DEFAULT '5.0' COMMENT '平均评分',
  `comment` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `reply` varchar(500) DEFAULT NULL COMMENT '商家回复',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_order` (`user_id`,`order_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_average_rating` (`average_rating`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺评分表';

-- 店铺分类表
CREATE TABLE IF NOT EXISTS `shop_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID,0表示一级分类',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '分类级别',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺分类表';

-- 店铺分类关联表
CREATE TABLE IF NOT EXISTS `shop_category_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_category` (`shop_id`,`category_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺分类关联表';

-- 店铺账户表
CREATE TABLE IF NOT EXISTS `shop_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `balance` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `freeze_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `settle_cycle` tinyint(4) NOT NULL DEFAULT '1' COMMENT '结算周期:1-T+1,2-T+7,3-月结',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-冻结',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_id` (`shop_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺账户表';

-- 店铺账户流水表
CREATE TABLE IF NOT EXISTS `shop_account_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-收入,2-支出,3-冻结,4-解冻',
  `amount` decimal(10,2) NOT NULL COMMENT '变动金额',
  `balance` decimal(12,2) NOT NULL COMMENT '变动后余额',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-订单结算,2-退款,3-提现,4-佣金,5-平台调整',
  `relation_id` varchar(64) DEFAULT NULL COMMENT '关联单号',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_type` tinyint(4) DEFAULT '3' COMMENT '操作人类型:1-店铺,2-用户,3-系统,4-管理员',
  `operator_note` varchar(255) DEFAULT NULL COMMENT '操作备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_type` (`type`),
  KEY `idx_source` (`source`),
  KEY `idx_relation_id` (`relation_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺账户流水表';

-- 店铺结算单表
CREATE TABLE IF NOT EXISTS `shop_settlement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '结算单ID',
  `settlement_no` varchar(32) NOT NULL COMMENT '结算单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `shop_name` varchar(64) DEFAULT NULL COMMENT '店铺名称',
  `period_start` datetime NOT NULL COMMENT '结算周期开始',
  `period_end` datetime NOT NULL COMMENT '结算周期结束',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单笔数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
  `refund_count` int(11) NOT NULL DEFAULT '0' COMMENT '退款笔数',
  `refund_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `commission_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '佣金金额',
  `settle_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '结算金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待结算,1-结算中,2-已结算,3-结算失败',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_type` tinyint(4) DEFAULT NULL COMMENT '支付方式:1-银行转账,2-微信,3-支付宝',
  `pay_no` varchar(64) DEFAULT NULL COMMENT '支付单号',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_settlement_no` (`settlement_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_period_start` (`period_start`),
  KEY `idx_period_end` (`period_end`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺结算单表';

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
  `currency` varchar(10) NOT NULL DEFAULT 'CNY' COMMENT '币种',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待支付,1-支付中,2-已支付,3-支付失败,4-已退款',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `channel_payment_no` varchar(64) DEFAULT NULL COMMENT '渠道支付单号',
  `channel_id` varchar(20) DEFAULT NULL COMMENT '支付渠道标识',
  `client_ip` varchar(64) DEFAULT NULL COMMENT '客户端IP',
  `device_info` varchar(128) DEFAULT NULL COMMENT '设备信息',
  `refund_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已退款金额',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `return_params` text COMMENT '支付回调参数',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_payment_type` (`payment_type`),
  KEY `idx_payment_method` (`payment_method`),
  KEY `idx_relation_id` (`relation_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`),
  KEY `idx_pay_time` (`pay_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

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
  `register_address` varchar(255) DEFAULT NULL COMMENT '注册地址',
  `register_phone` varchar(20) DEFAULT NULL COMMENT '注册电话',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户银行',
  `bank_account` varchar(32) DEFAULT NULL COMMENT '银行账户',
  `amount` decimal(10,2) NOT NULL COMMENT '发票金额',
  `tax_amount` decimal(10,2) DEFAULT NULL COMMENT '税额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待开票,1-已开票,2-已作废',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invoice_no` (`invoice_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票信息表';

-- 用户发票信息表
CREATE TABLE IF NOT EXISTS `user_invoice_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `invoice_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '发票类型:1-增值税普通发票,2-电子发票,3-增值税专用发票',
  `title_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '抬头类型:1-个人,2-企业',
  `invoice_title` varchar(100) NOT NULL COMMENT '发票抬头',
  `tax_no` varchar(32) DEFAULT NULL COMMENT '税号',
  `register_address` varchar(255) DEFAULT NULL COMMENT '注册地址',
  `register_phone` varchar(20) DEFAULT NULL COMMENT '注册电话',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户银行',
  `bank_account` varchar(32) DEFAULT NULL COMMENT '银行账户',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户发票信息表'; 