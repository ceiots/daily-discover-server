-- 内容分类表（内容分类管理）
CREATE TABLE IF NOT EXISTS `content_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID,0表示一级分类',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '分类级别',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_level` (`parent_id`, `level`),
  KEY `idx_sort_status` (`sort_order`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容分类表';

-- 内容主表（内容基本信息）
CREATE TABLE IF NOT EXISTS `content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `summary` varchar(255) DEFAULT NULL COMMENT '摘要',
  `content_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容类型:1-文章,2-视频',
  `thumbnail` varchar(255) DEFAULT NULL COMMENT '缩略图',
  `source` varchar(50) DEFAULT NULL COMMENT '来源',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID',
  `author_name` varchar(64) DEFAULT NULL COMMENT '作者名称',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-草稿,1-待审核,2-已发布,3-已下架,4-已删除',
  `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶:0-否,1-是',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `source_url` varchar(500) DEFAULT NULL COMMENT '原始链接',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_type` (`category_id`, `content_type`),
  KEY `idx_author_status` (`author_id`, `status`),
  KEY `idx_publish_display` (`publish_time`, `is_top`, `is_recommend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容主表';

-- 内容审核表（内容审核信息）
CREATE TABLE IF NOT EXISTS `content_audit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `audit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审核状态:0-未审核,1-审核通过,2-审核拒绝',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_user_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_id` (`content_id`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_audit_time` (`audit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容审核表';

-- 内容AI特征表（内容AI增强信息）
CREATE TABLE IF NOT EXISTS `content_ai_features` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `media_urls` json DEFAULT NULL COMMENT '媒体文件URLs',
  `tags` json DEFAULT NULL COMMENT '标签数组',
  `quality_score` decimal(3,2) DEFAULT '0.00' COMMENT 'AI质量评分',
  `trending_score` decimal(8,2) DEFAULT '0.00' COMMENT '热度分数',
  `ai_keywords` json DEFAULT NULL COMMENT 'AI提取关键词',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_id` (`content_id`),
  KEY `idx_quality_score` (`quality_score`),
  KEY `idx_trending_score` (`trending_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容AI特征表';

-- 内容统计表（内容计数数据）
CREATE TABLE IF NOT EXISTS `content_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `share_count` int(11) NOT NULL DEFAULT '0' COMMENT '分享数',
  `collect_count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_id` (`content_id`),
  KEY `idx_view_count` (`view_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容统计表';

-- 内容详情表（内容详细内容）
CREATE TABLE IF NOT EXISTS `content_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content` text NOT NULL COMMENT '内容详情',
  `content_format` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容格式:1-HTML,2-Markdown,3-纯文本',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_id` (`content_id`),
  KEY `idx_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容详情表';

-- 内容标签关联表（内容与标签关系）
CREATE TABLE IF NOT EXISTS `content_tag_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签关联表';

-- 标签表（标签信息）
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '标签类型:1-内容标签,2-商品标签,3-用户标签',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`,`type`),
  KEY `idx_type_status` (`type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 商品评价表（商品评价信息）
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
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-待审核,1-已发布,2-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_item_id` (`order_item_id`),
  KEY `idx_order` (`order_id`, `order_no`),
  KEY `idx_user_shop` (`user_id`, `shop_id`),
  KEY `idx_product` (`product_id`, `product_sku_id`),
  KEY `idx_rating_status` (`rating`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 商品评价扩展表（评价附加信息）
CREATE TABLE IF NOT EXISTS `product_review_ext` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `review_id` bigint(20) NOT NULL COMMENT '评价ID',
  `video_url` varchar(255) DEFAULT NULL COMMENT '视频URL',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名:0-否,1-是',
  `admin_reply` varchar(500) DEFAULT NULL COMMENT '商家回复',
  `admin_reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_review_id` (`review_id`),
  KEY `idx_like_count` (`like_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价扩展表';

-- 商品评价回复表（评价回复信息）
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
  PRIMARY KEY (`id`),
  KEY `idx_review_id` (`review_id`),
  KEY `idx_user_parent` (`user_id`, `parent_id`),
  KEY `idx_to_user_status` (`to_user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价回复表';

-- 评价点赞表（评价点赞记录）
CREATE TABLE IF NOT EXISTS `review_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `review_id` bigint(20) NOT NULL COMMENT '评价ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_review_user` (`review_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价点赞表';

-- 内容评论表（内容评论信息）
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
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_user_parent` (`user_id`, `parent_id`),
  KEY `idx_to_user_status` (`to_user_id`, `status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容评论表';

-- 内容点赞表（内容点赞记录）
CREATE TABLE IF NOT EXISTS `content_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_user` (`content_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容点赞表';

-- 评论点赞表（评论点赞记录）
CREATE TABLE IF NOT EXISTS `comment_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user` (`comment_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论点赞表'; 