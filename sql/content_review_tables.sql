-- 内容分类表
CREATE TABLE IF NOT EXISTS `content_category` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容分类表';

-- 内容主表
CREATE TABLE IF NOT EXISTS `content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `summary` varchar(255) DEFAULT NULL COMMENT '摘要',
  `content_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容类型:1-文章,2-视频,3-图集,4-问答',
  `thumbnail` varchar(255) DEFAULT NULL COMMENT '缩略图',
  `source` varchar(50) DEFAULT NULL COMMENT '来源',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID',
  `author_name` varchar(64) DEFAULT NULL COMMENT '作者名称',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-草稿,1-待审核,2-已发布,3-已下架,4-已删除',
  `audit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审核状态:0-未审核,1-审核通过,2-审核拒绝',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_user_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶:0-否,1-是',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_status` (`status`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_is_recommend` (`is_recommend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容主表';

-- 内容详情表
CREATE TABLE IF NOT EXISTS `content_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content` text NOT NULL COMMENT '内容详情',
  `content_format` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容格式:1-HTML,2-Markdown,3-纯文本',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_id` (`content_id`),
  KEY `idx_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容详情表';

-- 内容标签关联表
CREATE TABLE IF NOT EXISTS `content_tag_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签关联表';

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '标签类型:1-内容标签,2-商品标签,3-用户标签',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`,`type`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `order_item_id` bigint(20) NOT NULL COMMENT '订单商品ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
  `product_name` varchar(128) NOT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `sku_properties_name` varchar(255) DEFAULT NULL COMMENT 'SKU属性名称',
  `rating` tinyint(4) NOT NULL DEFAULT '5' COMMENT '评分:1-5星',
  `review_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '评价类型:1-自评,2-系统默认好评',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '评价图片,最多9张,JSON格式',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-待审核,1-已发布,2-已删除',
  `admin_reply` varchar(500) DEFAULT NULL COMMENT '商家回复',
  `admin_reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_item_id` (`order_item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_rating` (`rating`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 商品评价回复表
CREATE TABLE IF NOT EXISTS `product_review_reply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '回复ID',
  `review_id` bigint(20) NOT NULL COMMENT '评价ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名称',
  `user_avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `content` varchar(500) NOT NULL COMMENT '回复内容',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父回复ID,0表示一级回复',
  `to_user_id` bigint(20) DEFAULT NULL COMMENT '回复目标用户ID',
  `to_user_name` varchar(64) DEFAULT NULL COMMENT '回复目标用户名',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-待审核,1-已发布,2-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_review_id` (`review_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_to_user_id` (`to_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价回复表';

-- 评价点赞表
CREATE TABLE IF NOT EXISTS `review_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `review_id` bigint(20) NOT NULL COMMENT '评价ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_review_user` (`review_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价点赞表';

-- 内容评论表
CREATE TABLE IF NOT EXISTS `content_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名称',
  `user_avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父评论ID,0表示一级评论',
  `to_user_id` bigint(20) DEFAULT NULL COMMENT '回复目标用户ID',
  `to_user_name` varchar(64) DEFAULT NULL COMMENT '回复目标用户名',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待审核,1-已发布,2-已删除',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `reply_count` int(11) NOT NULL DEFAULT '0' COMMENT '回复数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_to_user_id` (`to_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容评论表';

-- 内容点赞表
CREATE TABLE IF NOT EXISTS `content_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_user` (`content_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容点赞表';

-- 评论点赞表
CREATE TABLE IF NOT EXISTS `comment_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表'; 