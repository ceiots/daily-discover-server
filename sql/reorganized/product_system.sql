-- 商品系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: product_mvp.sql, ecommerce_order_tables.sql中的商品相关表

-- 商品主表
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `product_title` varchar(200) NOT NULL COMMENT '商品标题',
  `product_code` varchar(50) DEFAULT NULL COMMENT '商品编码',
  `main_image` varchar(255) DEFAULT NULL COMMENT '商品主图',
  `specs_type` tinyint(4) DEFAULT '1' COMMENT '规格类型:1-单规格,2-多规格',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `market_price` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-下架,1-上架,2-待审核',
  `sales` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除:0-未删除,1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_brand_id` (`brand_id`),
  KEY `idx_product_code` (`product_code`),
  KEY `idx_status_deleted` (`status`,`deleted`),
  KEY `idx_sales` (`sales`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品主表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父分类ID',
  `category_name` varchar(50) NOT NULL COMMENT '分类名称',
  `category_code` varchar(50) DEFAULT NULL COMMENT '分类编码',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '层级:1-一级,2-二级,3-三级',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `image` varchar(255) DEFAULT NULL COMMENT '分类图片',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `commission_rate` decimal(5,2) DEFAULT NULL COMMENT '佣金比例(%)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_category_code` (`category_code`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品品牌表
CREATE TABLE IF NOT EXISTS `product_brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
  `brand_name` varchar(50) NOT NULL COMMENT '品牌名称',
  `brand_code` varchar(50) DEFAULT NULL COMMENT '品牌编码',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌LOGO',
  `description` varchar(255) DEFAULT NULL COMMENT '品牌描述',
  `official_website` varchar(255) DEFAULT NULL COMMENT '官网',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_brand_name` (`brand_name`),
  KEY `idx_brand_code` (`brand_code`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品品牌表';

-- 商品详情表
CREATE TABLE IF NOT EXISTS `product_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '详情ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `detail_content` text COMMENT '详情内容',
  `detail_mobile` text COMMENT '移动端详情',
  `images` json DEFAULT NULL COMMENT '商品图片',
  `attributes` json DEFAULT NULL COMMENT '商品属性',
  `package_info` varchar(255) DEFAULT NULL COMMENT '包装信息',
  `after_service` varchar(255) DEFAULT NULL COMMENT '售后服务',
  `keywords` varchar(255) DEFAULT NULL COMMENT '搜索关键词',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品详情表';

-- 商品SKU表
CREATE TABLE IF NOT EXISTS `product_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(255) DEFAULT NULL COMMENT 'SKU名称',
  `barcode` varchar(64) DEFAULT NULL COMMENT '条形码',
  `spec_data` json DEFAULT NULL COMMENT '规格数据',
  `spec_desc` varchar(255) DEFAULT NULL COMMENT '规格描述',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `market_price` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `locked_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `sku_image` varchar(255) DEFAULT NULL COMMENT 'SKU图片',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '重量(kg)',
  `volume` decimal(10,2) DEFAULT NULL COMMENT '体积(m³)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_product_shop` (`product_id`,`shop_id`),
  KEY `idx_barcode` (`barcode`),
  KEY `idx_stock` (`stock`,`locked_stock`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- 商品规格表
CREATE TABLE IF NOT EXISTS `product_spec` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `shop_id` bigint(20) DEFAULT NULL COMMENT '店铺ID',
  `spec_name` varchar(50) NOT NULL COMMENT '规格名称',
  `spec_code` varchar(50) DEFAULT NULL COMMENT '规格编码',
  `spec_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '规格类型:1-文字,2-图片,3-颜色',
  `spec_values` json DEFAULT NULL COMMENT '规格值列表',
  `is_generic` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否通用:0-否,1-是',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_spec_code` (`spec_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品规格表';

-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_item_id` bigint(20) NOT NULL COMMENT '订单项ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `images` json DEFAULT NULL COMMENT '评价图片',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名:0-否,1-是',
  `rating` tinyint(4) NOT NULL COMMENT '评分:1-5星',
  `spec_rating` tinyint(4) DEFAULT NULL COMMENT '规格相符评分',
  `service_rating` tinyint(4) DEFAULT NULL COMMENT '服务评分',
  `logistics_rating` tinyint(4) DEFAULT NULL COMMENT '物流评分',
  `has_additional` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有追评:0-否,1-是',
  `additional_content` varchar(1000) DEFAULT NULL COMMENT '追评内容',
  `additional_images` json DEFAULT NULL COMMENT '追评图片',
  `additional_time` datetime DEFAULT NULL COMMENT '追评时间',
  `reply_content` varchar(500) DEFAULT NULL COMMENT '商家回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '商家回复时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-待审核,1-已发布,2-已屏蔽',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_item_id` (`order_item_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 商品收藏表
CREATE TABLE IF NOT EXISTS `product_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-取消,1-收藏',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product` (`user_id`,`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

-- 商品浏览历史表
CREATE TABLE IF NOT EXISTS `product_browse_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `browse_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  `stay_time` int(11) DEFAULT NULL COMMENT '停留时间(秒)',
  `source_type` tinyint(4) DEFAULT NULL COMMENT '来源类型:1-首页,2-搜索,3-分类,4-推荐,5-其他',
  `source` varchar(64) DEFAULT NULL COMMENT '来源详情',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型:1-PC,2-Android,3-iOS,4-小程序,5-其他',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_browse_time` (`browse_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品浏览历史表';

-- 店铺表
CREATE TABLE IF NOT EXISTS `shop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '店铺ID',
  `shop_name` varchar(100) NOT NULL COMMENT '店铺名称',
  `shop_code` varchar(50) DEFAULT NULL COMMENT '店铺编码',
  `user_id` bigint(20) NOT NULL COMMENT '店主用户ID',
  `shop_logo` varchar(255) DEFAULT NULL COMMENT '店铺LOGO',
  `shop_banner` varchar(255) DEFAULT NULL COMMENT '店铺Banner',
  `shop_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '店铺类型:1-个人,2-企业,3-旗舰,4-专卖',
  `shop_industry` varchar(50) DEFAULT NULL COMMENT '店铺行业',
  `shop_description` varchar(500) DEFAULT NULL COMMENT '店铺描述',
  `shop_address` varchar(255) DEFAULT NULL COMMENT '店铺地址',
  `shop_phone` varchar(20) DEFAULT NULL COMMENT '店铺电话',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待审核,1-正常,2-关闭,3-冻结',
  `open_time` datetime DEFAULT NULL COMMENT '开店时间',
  `close_time` datetime DEFAULT NULL COMMENT '关店时间',
  `rating` decimal(2,1) DEFAULT '5.0' COMMENT '店铺评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_name` (`shop_name`),
  KEY `idx_shop_code` (`shop_code`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺表'; 