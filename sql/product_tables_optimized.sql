-- 商品核心信息表 (精简到20个字段以内)
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '商品标题',
  `description` varchar(500) DEFAULT NULL COMMENT '商品简短描述',
  `image_url` varchar(255) DEFAULT NULL COMMENT '商品主图URL',
  `category_id` bigint(20) DEFAULT NULL COMMENT '商品分类ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌ID',
  `status` tinyint(4) DEFAULT '0' COMMENT '商品状态:0-草稿,1-在线,2-下线',
  `audit_status` tinyint(4) DEFAULT '0' COMMENT '审核状态:0-待审核,1-已通过,2-未通过',
  `price` decimal(10,2) DEFAULT '0.00' COMMENT '基础销售价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价/市场价',
  `total_stock` int(11) DEFAULT '0' COMMENT '商品总库存',
  `total_sales` int(11) DEFAULT '0' COMMENT '商品总销量',
  `weight` int(11) DEFAULT '0' COMMENT '排序权重',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_brand_id` (`brand_id`),
  KEY `idx_status_deleted` (`status`, `deleted`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品核心信息表';

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

-- 商品SKU表
CREATE TABLE IF NOT EXISTS `product_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_code` varchar(50) DEFAULT NULL COMMENT 'SKU编码',
  `barcode` varchar(50) DEFAULT NULL COMMENT '条形码',
  `price` decimal(10,2) NOT NULL COMMENT '销售价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价/市场价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '可售库存',
  `locked_stock` int(11) DEFAULT '0' COMMENT '锁定库存',
  `sales_count` int(11) DEFAULT '0' COMMENT '销售数量',
  `image_url` varchar(255) DEFAULT NULL COMMENT 'SKU图片URL',
  `specifications` json DEFAULT NULL COMMENT '规格值JSON',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认选中',
  `status` tinyint(4) DEFAULT '1' COMMENT 'SKU状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  UNIQUE KEY `uk_barcode` (`barcode`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status_deleted` (`status`, `deleted`),
  KEY `idx_stock` (`stock`, `locked_stock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

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

-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `sku_id` bigint(20) DEFAULT NULL COMMENT '购买的SKU ID',
  `rating` tinyint(4) NOT NULL COMMENT '评分(1-5)',
  `content` text COMMENT '评论内容',
  `images` json DEFAULT NULL COMMENT '评论图片',
  `is_anonymous` tinyint(1) DEFAULT '0' COMMENT '是否匿名评价',
  `reply_content` text COMMENT '商家回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '商家回复时间',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态:0-隐藏,1-显示',
  `has_image` tinyint(1) DEFAULT '0' COMMENT '是否有图片',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_product_rating` (`product_id`, `rating`),
  KEY `idx_product_image` (`product_id`, `has_image`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

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