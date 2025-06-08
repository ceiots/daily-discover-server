-- 商品主表（商品基本信息）
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `sub_title` varchar(200) DEFAULT NULL COMMENT '商品副标题',
  `main_image` varchar(255) DEFAULT NULL COMMENT '商品主图',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '重量(kg)',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品主表';

-- 商品价格表（商品价格信息）
CREATE TABLE IF NOT EXISTS `product_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `price` decimal(10,2) NOT NULL COMMENT '销售价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `commission_rate` decimal(5,2) DEFAULT '0.00' COMMENT '佣金比例',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品价格表';

-- 商品状态表（商品状态信息）
CREATE TABLE IF NOT EXISTS `product_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-下架,1-上架,2-售罄',
  `publish_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发布状态:0-未发布,1-已发布',
  `audit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审核状态:0-未审核,1-审核通过,2-审核拒绝',
  `rating` decimal(3,2) DEFAULT '0.00' COMMENT '评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_status_publish` (`status`,`publish_status`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品状态表';

-- 商品营销特性表（商品营销信息）
CREATE TABLE IF NOT EXISTS `product_marketing_features` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `tags` json DEFAULT NULL COMMENT '标签',
  `affiliate_url` varchar(500) DEFAULT NULL COMMENT '推广链接',
  `ai_features` json DEFAULT NULL COMMENT 'AI提取特征',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品营销特性表';

-- 商品详情表（商品详细信息）
CREATE TABLE IF NOT EXISTS `product_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `description` text COMMENT '商品描述',
  `rich_content` text COMMENT '富文本内容',
  `mobile_content` text COMMENT '移动端展示内容',
  `packing_list` varchar(1000) DEFAULT NULL COMMENT '包装清单',
  `after_service` varchar(1000) DEFAULT NULL COMMENT '售后服务',
  `attribute_list` json DEFAULT NULL COMMENT '属性列表JSON',
  `param_list` json DEFAULT NULL COMMENT '参数列表JSON',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品详情表';

-- 商品SKU表（商品规格和库存）
CREATE TABLE IF NOT EXISTS `product_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `sku_code` varchar(64) DEFAULT NULL COMMENT 'SKU编码',
  `barcode` varchar(64) DEFAULT NULL COMMENT '条形码',
  `sku_name` varchar(255) DEFAULT NULL COMMENT 'SKU名称',
  `spec_data` json DEFAULT NULL COMMENT '规格数据JSON',
  `main_image` varchar(255) DEFAULT NULL COMMENT '主图',
  `images` json DEFAULT NULL COMMENT '图片列表',
  `price` decimal(10,2) NOT NULL COMMENT '销售价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `lock_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_sku_code` (`sku_code`),
  KEY `idx_barcode` (`barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- 商品分类表（商品分类）
CREATE TABLE IF NOT EXISTS `product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID,0表示一级分类',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `level` tinyint(4) NOT NULL COMMENT '分类层级:1-一级,2-二级,3-三级',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示:0-否,1-是',
  `is_menu` tinyint(1) DEFAULT '1' COMMENT '是否导航:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `seo_title` varchar(100) DEFAULT NULL COMMENT 'SEO标题',
  `seo_keywords` varchar(255) DEFAULT NULL COMMENT 'SEO关键字',
  `seo_description` varchar(255) DEFAULT NULL COMMENT 'SEO描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_sort_show` (`sort_order`,`is_show`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品品牌表（品牌信息）
CREATE TABLE IF NOT EXISTS `product_brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
  `name` varchar(50) NOT NULL COMMENT '品牌名称',
  `english_name` varchar(100) DEFAULT NULL COMMENT '英文名称',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌Logo',
  `description` varchar(500) DEFAULT NULL COMMENT '品牌描述',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_sort_show` (`sort_order`,`is_show`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品品牌表';

-- 商品图片表（商品图片集）
CREATE TABLE IF NOT EXISTS `product_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'SKU ID,NULL表示商品主图',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `is_main` tinyint(1) DEFAULT '0' COMMENT '是否主图:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_is_main` (`is_main`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 商品属性表（商品参数）
CREATE TABLE IF NOT EXISTS `product_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '属性ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `attribute_name` varchar(50) NOT NULL COMMENT '属性名称',
  `attribute_value` varchar(255) NOT NULL COMMENT '属性值',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `is_search` tinyint(1) DEFAULT '0' COMMENT '是否支持搜索:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_is_search` (`is_search`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

-- 商品规格表（规格定义）
CREATE TABLE IF NOT EXISTS `product_spec` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `spec_name` varchar(50) NOT NULL COMMENT '规格名称',
  `spec_type` tinyint(4) DEFAULT '1' COMMENT '规格类型:1-文字,2-颜色,3-图片',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 商品规格值表（规格值）
CREATE TABLE IF NOT EXISTS `product_spec_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规格值ID',
  `spec_id` bigint(20) NOT NULL COMMENT '规格ID',
  `spec_value` varchar(50) NOT NULL COMMENT '规格值',
  `spec_image` varchar(255) DEFAULT NULL COMMENT '规格图片',
  `spec_color` varchar(20) DEFAULT NULL COMMENT '规格颜色值',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_spec_id` (`spec_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格值表';

-- 商品评价表（商品评论）
CREATE TABLE IF NOT EXISTS `product_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_item_id` bigint(20) NOT NULL COMMENT '订单商品ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `content` varchar(500) DEFAULT NULL COMMENT '评价内容',
  `images` json DEFAULT NULL COMMENT '评价图片',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `rating` tinyint(4) NOT NULL DEFAULT '5' COMMENT '评分:1-5星',
  `is_anonymous` tinyint(1) DEFAULT '0' COMMENT '是否匿名:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-隐藏,1-显示',
  `reply_content` varchar(500) DEFAULT NULL COMMENT '商家回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '商家回复时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_order_item_id` (`order_item_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 商品内容表 (详细展示内容)
CREATE TABLE IF NOT EXISTS `product_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `images` json DEFAULT NULL COMMENT '商品图片列表',
  `details` json DEFAULT NULL COMMENT '商品详情内容',
  `purchase_notices` json DEFAULT NULL COMMENT '购买须知',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `rich_description` text COMMENT '富文本描述',
  `seo_title` varchar(255) DEFAULT NULL COMMENT 'SEO标题',
  `seo_keywords` varchar(255) DEFAULT NULL COMMENT 'SEO关键词',
  `seo_description` varchar(500) DEFAULT NULL COMMENT 'SEO描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品内容表';

-- 商品规格表 (销售和非销售属性)
CREATE TABLE IF NOT EXISTS `product_specification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `specifications` json DEFAULT NULL COMMENT '销售规格(SKU区分项)',
  `attributes` json DEFAULT NULL COMMENT '非销售属性',
  `has_multiple_skus` tinyint(1) DEFAULT '0' COMMENT '是否多规格商品',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 商品促销表
CREATE TABLE IF NOT EXISTS `product_promotion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'SKU ID，NULL表示适用所有SKU',
  `promotion_price` decimal(10,2) DEFAULT NULL COMMENT '促销价格',
  `promotion_title` varchar(100) DEFAULT NULL COMMENT '促销标题',
  `promotion_desc` varchar(255) DEFAULT NULL COMMENT '促销描述',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(4) DEFAULT '1' COMMENT '促销状态:0-未开始,1-进行中,2-已结束',
  `promotion_type` tinyint(4) DEFAULT '1' COMMENT '促销类型:1-直降,2-折扣,3-限时,4-满减',
  `promotion_rule` json DEFAULT NULL COMMENT '促销规则，如满减阈值',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_status_time` (`status`, `start_time`, `end_time`),
  KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品促销表';

-- 商品库存日志表
CREATE TABLE IF NOT EXISTS `product_inventory_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `change_type` tinyint(4) NOT NULL COMMENT '变更类型:1-入库,2-出库,3-锁定,4-解锁',
  `quantity` int(11) NOT NULL COMMENT '变更数量',
  `before_stock` int(11) NOT NULL COMMENT '变更前库存',
  `after_stock` int(11) NOT NULL COMMENT '变更后库存',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品库存日志表';

-- 商品统计表
CREATE TABLE IF NOT EXISTS `product_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `view_count` int(11) DEFAULT '0' COMMENT '浏览次数',
  `favorite_count` int(11) DEFAULT '0' COMMENT '收藏次数',
  `share_count` int(11) DEFAULT '0' COMMENT '分享次数',
  `rating` decimal(3,1) DEFAULT NULL COMMENT '商品评分',
  `review_count` int(11) DEFAULT '0' COMMENT '评论数量',
  `positive_reviews` int(11) DEFAULT '0' COMMENT '好评数',
  `negative_reviews` int(11) DEFAULT '0' COMMENT '差评数',
  `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
  `daily_views_trend` json DEFAULT NULL COMMENT '每日浏览趋势',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_rating_reviews` (`rating`, `review_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品统计表';

-- 商品营销表
CREATE TABLE IF NOT EXISTS `product_marketing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `is_hot` tinyint(1) DEFAULT '0' COMMENT '是否热门商品',
  `is_new` tinyint(1) DEFAULT '0' COMMENT '是否新品',
  `is_recommended` tinyint(1) DEFAULT '0' COMMENT '是否推荐商品',
  `labels` json DEFAULT NULL COMMENT '商品标签列表',
  `related_product_ids` json DEFAULT NULL COMMENT '相关商品ID列表',
  `show_in_homepage` tinyint(1) DEFAULT '0' COMMENT '是否在首页展示',
  `marketing_start_time` datetime DEFAULT NULL COMMENT '营销开始时间',
  `marketing_end_time` datetime DEFAULT NULL COMMENT '营销结束时间',
  `sort_weight` int(11) DEFAULT '0' COMMENT '排序权重',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_marketing_flags` (`is_hot`, `is_new`, `is_recommended`),
  KEY `idx_homepage` (`show_in_homepage`, `sort_weight`),
  KEY `idx_marketing_time` (`marketing_start_time`, `marketing_end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品营销表';

-- 商品分类关联表
CREATE TABLE IF NOT EXISTS `product_category_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `parent_category_id` bigint(20) DEFAULT NULL COMMENT '父级分类ID',
  `grand_category_id` bigint(20) DEFAULT NULL COMMENT '祖父级分类ID',
  `is_primary` tinyint(1) DEFAULT '1' COMMENT '是否主分类',
  `sort_weight` int(11) DEFAULT '0' COMMENT '排序权重',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_category` (`product_id`,`category_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_category_tree` (`category_id`, `parent_category_id`, `grand_category_id`),
  KEY `idx_primary_weight` (`is_primary`, `sort_weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类关联表';

-- 商品审核记录表
CREATE TABLE IF NOT EXISTS `product_audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `audit_status` tinyint(4) NOT NULL COMMENT '审核状态:0-待审核,1-已通过,2-未通过',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `auditor_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_audit_time` (`audit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品审核记录表';

-- 商品标签表
CREATE TABLE IF NOT EXISTS `product_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `tag_name` varchar(50) DEFAULT NULL COMMENT '标签名称',
  `tag_type` tinyint(4) DEFAULT '1' COMMENT '标签类型:1-普通,2-活动,3-系统',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_tag` (`product_id`,`tag_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_tag_type` (`tag_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品标签关联表';

-- 商品搜索关键词表
CREATE TABLE IF NOT EXISTS `product_search_keyword` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `keyword` varchar(100) NOT NULL COMMENT '搜索关键词',
  `weight` int(11) DEFAULT '0' COMMENT '权重',
  `is_manual` tinyint(1) DEFAULT '0' COMMENT '是否手动添加',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_keyword` (`keyword`(20)),
  KEY `idx_weight` (`weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品搜索关键词表'; 