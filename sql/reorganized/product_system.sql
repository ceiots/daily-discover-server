-- 商品系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: product_mvp.sql

-- 商品基础表（商品基本信息）
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌ID',
  `product_name` varchar(200) NOT NULL COMMENT '商品名称',
  `product_code` varchar(64) DEFAULT NULL COMMENT '商品编码',
  `main_image` varchar(255) DEFAULT NULL COMMENT '主图',
  `sub_images` varchar(2000) DEFAULT NULL COMMENT '子图JSON',
  `detail` text COMMENT '详情',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-下架,1-上架',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_brand_id` (`brand_id`),
  KEY `idx_product_name` (`product_name`(32)),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品基础表';

-- 商品价格表（价格相关信息）
CREATE TABLE IF NOT EXISTS `product_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `original_price` decimal(10,2) NOT NULL COMMENT '原价',
  `sell_price` decimal(10,2) NOT NULL COMMENT '售价',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `market_price` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `vip_price` decimal(10,2) DEFAULT NULL COMMENT '会员价',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_sell_price` (`sell_price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品价格表';

-- 商品属性表（规格参数）
CREATE TABLE IF NOT EXISTS `product_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `attribute_name` varchar(64) NOT NULL COMMENT '属性名称',
  `attribute_value` varchar(255) NOT NULL COMMENT '属性值',
  `attribute_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型:1-规格,2-参数',
  `is_key` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否关键属性:0-否,1-是',
  `search_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '搜索类型:0-不需要,1-精确,2-范围',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_attribute_name` (`attribute_name`),
  KEY `idx_search_type` (`search_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品属性表';

-- 商品SKU表（库存单元）
CREATE TABLE IF NOT EXISTS `product_sku` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'SKU ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(255) DEFAULT NULL COMMENT 'SKU名称',
  `spec_data` json DEFAULT NULL COMMENT '规格数据JSON',
  `image` varchar(255) DEFAULT NULL COMMENT 'SKU图片',
  `sell_price` decimal(10,2) NOT NULL COMMENT '售价',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '库存',
  `lock_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `warning_stock` int(11) DEFAULT '0' COMMENT '预警库存',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '重量(kg)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  UNIQUE KEY `uk_sku_code` (`sku_code`),
  KEY `idx_stock` (`stock`),
  KEY `idx_sell_price` (`sell_price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父分类ID',
  `name` varchar(64) NOT NULL COMMENT '分类名称',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '层级:1-一级,2-二级,3-三级',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示:0-否,1-是',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热门:0-否,1-是',
  `keywords` varchar(255) DEFAULT NULL COMMENT '关键词',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 品牌表
CREATE TABLE IF NOT EXISTS `brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
  `name` varchar(64) NOT NULL COMMENT '品牌名称',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌LOGO',
  `description` varchar(255) DEFAULT NULL COMMENT '品牌描述',
  `first_letter` char(1) DEFAULT NULL COMMENT '首字母',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `is_show` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否显示:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_first_letter` (`first_letter`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `sku_id` bigint(20) DEFAULT NULL COMMENT 'SKU ID',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `score` tinyint(4) NOT NULL DEFAULT '5' COMMENT '评分(1-5)',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片，JSON数组',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-隐藏,1-显示,2-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 商品统计表
CREATE TABLE IF NOT EXISTS `product_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `favorite_count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `sales_count` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `virtual_sales_count` int(11) NOT NULL DEFAULT '0' COMMENT '虚拟销量',
  `rating` decimal(3,2) NOT NULL DEFAULT '5.00' COMMENT '评分',
  `is_new` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否新品:0-否,1-是',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热门:0-否,1-是',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `promotion_type` tinyint(4) DEFAULT '0' COMMENT '促销类型:0-无,1-满减,2-折扣,3-限时,4-秒杀',
  `promotion_desc` varchar(255) DEFAULT NULL COMMENT '促销描述',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_id` (`product_id`),
  KEY `idx_view_count` (`view_count` DESC),
  KEY `idx_sales_count` (`sales_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品统计表';

-- 商品标签表
CREATE TABLE IF NOT EXISTS `product_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '标签类型:1-商品属性,2-商品特点,3-促销标签',
  `use_count` int(11) NOT NULL DEFAULT '0' COMMENT '使用次数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`,`type`),
  KEY `idx_type_status` (`type`, `status`),
  KEY `idx_use_count` (`use_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品标签表';

-- 商品标签关联表
CREATE TABLE IF NOT EXISTS `product_tag_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_tag` (`product_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品标签关联表'; 