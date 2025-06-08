-- 营销活动表
CREATE TABLE IF NOT EXISTS `marketing_campaign` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(100) NOT NULL COMMENT '活动标题',
  `description` varchar(500) DEFAULT NULL COMMENT '活动描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-未开始,1-进行中,2-已结束,3-已取消',
  `type` tinyint(4) NOT NULL COMMENT '活动类型:1-满减,2-折扣,3-秒杀,4-拼团,5-限时,6-新人,7-会员专享',
  `priority` int(11) DEFAULT '0' COMMENT '优先级',
  `limit_per_user` int(11) DEFAULT NULL COMMENT '每人限购',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID,NULL表示平台活动',
  `banner_url` varchar(255) DEFAULT NULL COMMENT '活动banner图片',
  `page_url` varchar(255) DEFAULT NULL COMMENT '活动页面地址',
  `rules` text COMMENT '活动规则(JSON格式)',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动表';

-- 活动商品关联表
CREATE TABLE IF NOT EXISTS `campaign_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `campaign_id` bigint(20) NOT NULL COMMENT '活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) DEFAULT NULL COMMENT '商品SKU ID,NULL表示所有SKU',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `activity_price` decimal(10,2) NOT NULL COMMENT '活动价',
  `stock` int(11) DEFAULT NULL COMMENT '活动库存,NULL表示使用原库存',
  `sold` int(11) NOT NULL DEFAULT '0' COMMENT '已售数量',
  `limit_per_user` int(11) DEFAULT NULL COMMENT '每人限购',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_campaign_product_sku` (`campaign_id`,`product_id`,`product_sku_id`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动商品关联表';

-- 优惠券模板表
CREATE TABLE IF NOT EXISTS `coupon_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `name` varchar(64) NOT NULL COMMENT '优惠券名称',
  `type` tinyint(4) NOT NULL COMMENT '优惠券类型:1-满减券,2-折扣券,3-无门槛券,4-运费券',
  `use_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '使用类型:1-全场通用,2-指定商品,3-指定分类,4-指定店铺',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT '优惠金额',
  `discount_rate` decimal(3,2) DEFAULT NULL COMMENT '折扣率',
  `min_spend` decimal(10,2) DEFAULT NULL COMMENT '最低消费',
  `max_discount` decimal(10,2) DEFAULT NULL COMMENT '最高优惠限额',
  `total_count` int(11) DEFAULT '-1' COMMENT '发行数量,-1表示无限',
  `used_count` int(11) NOT NULL DEFAULT '0' COMMENT '已使用数量',
  `limit_per_user` int(11) DEFAULT '1' COMMENT '每人限领',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `valid_days` int(11) DEFAULT NULL COMMENT '有效天数',
  `valid_start_time` datetime DEFAULT NULL COMMENT '有效期开始时间',
  `valid_end_time` datetime DEFAULT NULL COMMENT '有效期结束时间',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID,NULL表示平台券',
  `description` varchar(255) DEFAULT NULL COMMENT '使用说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_valid_end_time` (`valid_end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- 优惠券适用范围表
CREATE TABLE IF NOT EXISTS `coupon_scope` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `coupon_template_id` bigint(20) NOT NULL COMMENT '优惠券模板ID',
  `scope_type` tinyint(4) NOT NULL COMMENT '范围类型:1-商品,2-分类,3-店铺',
  `scope_id` bigint(20) NOT NULL COMMENT '范围ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_type_id` (`coupon_template_id`,`scope_type`,`scope_id`),
  KEY `idx_coupon_template_id` (`coupon_template_id`),
  KEY `idx_scope_type` (`scope_type`),
  KEY `idx_scope_id` (`scope_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券适用范围表';

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS `user_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户券ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `coupon_template_id` bigint(20) NOT NULL COMMENT '优惠券模板ID',
  `coupon_code` varchar(32) NOT NULL COMMENT '优惠券码',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-未使用,2-已使用,3-已过期,4-已冻结',
  `get_type` tinyint(4) NOT NULL COMMENT '获取方式:1-领取,2-发放,3-兑换',
  `get_time` datetime NOT NULL COMMENT '获取时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint(20) DEFAULT NULL COMMENT '使用订单ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '使用订单号',
  `start_time` datetime NOT NULL COMMENT '有效期开始时间',
  `end_time` datetime NOT NULL COMMENT '有效期结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_coupon_code` (`coupon_code`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_template_id` (`coupon_template_id`),
  KEY `idx_status` (`status`),
  KEY `idx_get_time` (`get_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 促销满减规则表
CREATE TABLE IF NOT EXISTS `promotion_full_reduction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(64) NOT NULL COMMENT '规则名称',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID,NULL表示平台规则',
  `apply_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '适用类型:1-全场,2-指定商品,3-指定分类',
  `rule_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '规则类型:1-满减,2-满折,3-满赠',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `priority` int(11) DEFAULT '0' COMMENT '优先级',
  `rules` text NOT NULL COMMENT '规则JSON,如[{"full":100,"reduction":10},{"full":200,"reduction":30}]',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销满减规则表';

-- 促销范围表
CREATE TABLE IF NOT EXISTS `promotion_scope` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `promotion_id` bigint(20) NOT NULL COMMENT '促销ID',
  `promotion_type` tinyint(4) NOT NULL COMMENT '促销类型:1-满减,2-折扣,3-秒杀,4-拼团',
  `scope_type` tinyint(4) NOT NULL COMMENT '范围类型:1-商品,2-分类',
  `scope_id` bigint(20) NOT NULL COMMENT '范围ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_promotion_scope` (`promotion_id`,`promotion_type`,`scope_type`,`scope_id`),
  KEY `idx_promotion_id` (`promotion_id`),
  KEY `idx_promotion_type` (`promotion_type`),
  KEY `idx_scope_type` (`scope_type`),
  KEY `idx_scope_id` (`scope_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='促销范围表';

-- 秒杀活动表
CREATE TABLE IF NOT EXISTS `seckill_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `name` varchar(64) NOT NULL COMMENT '活动名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-未开始,1-进行中,2-已结束,3-已取消',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID,NULL表示平台活动',
  `limit_per_user` int(11) DEFAULT '1' COMMENT '每人限购',
  `banner_url` varchar(255) DEFAULT NULL COMMENT '活动banner',
  `description` varchar(500) DEFAULT NULL COMMENT '活动描述',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- 秒杀商品表
CREATE TABLE IF NOT EXISTS `seckill_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `seckill_price` decimal(10,2) NOT NULL COMMENT '秒杀价',
  `stock` int(11) NOT NULL COMMENT '秒杀库存',
  `lock_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `sold` int(11) NOT NULL DEFAULT '0' COMMENT '已售数量',
  `limit_per_user` int(11) DEFAULT '1' COMMENT '每人限购',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_seckill_product_sku` (`seckill_id`,`product_id`,`product_sku_id`),
  KEY `idx_seckill_id` (`seckill_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品表'; 