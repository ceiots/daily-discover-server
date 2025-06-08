-- 内容审核模块表结构（MVP版本）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 保留核心功能，去除非必要字段，提高查询性能

-- 内容分类表（精简版）
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

-- 内容主表（精简版）
CREATE TABLE IF NOT EXISTS `content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `title` varchar(128) NOT NULL COMMENT '标题',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `summary` varchar(255) DEFAULT NULL COMMENT '摘要',
  `content_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容类型:1-文章,2-视频',
  `thumbnail` varchar(255) DEFAULT NULL COMMENT '缩略图',
  `author_id` bigint(20) NOT NULL COMMENT '作者ID',
  `author_name` varchar(64) DEFAULT NULL COMMENT '作者名称',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-草稿,1-待审核,2-已发布,3-已下架,4-已删除',
  `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶:0-否,1-是',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签,多个用逗号分隔',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_type` (`category_id`, `content_type`),
  KEY `idx_author_status` (`author_id`, `status`),
  KEY `idx_publish_display` (`publish_time`, `is_top`, `is_recommend`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容主表';

-- 内容审核表（精简版）
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

-- 内容详情表（精简版）
CREATE TABLE IF NOT EXISTS `content_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content` text NOT NULL COMMENT '内容详情',
  `content_format` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容格式:1-HTML,2-Markdown,3-纯文本',
  `media_urls` json DEFAULT NULL COMMENT '媒体文件URLs',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_id` (`content_id`),
  KEY `idx_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容详情表';

-- 内容统计表（精简版）
CREATE TABLE IF NOT EXISTS `content_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `share_count` int(11) NOT NULL DEFAULT '0' COMMENT '分享数',
  `collect_count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_id` (`content_id`),
  KEY `idx_view_count` (`view_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容统计表';

-- 标签表（精简版）
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '标签类型:1-内容标签,2-商品标签,3-用户标签',
  `use_count` int(11) NOT NULL DEFAULT '0' COMMENT '使用次数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`,`type`),
  KEY `idx_type_status` (`type`, `status`),
  KEY `idx_use_count` (`use_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 商品评价表（精简版）
CREATE TABLE IF NOT EXISTS `product_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_item_id` bigint(20) NOT NULL COMMENT '订单商品ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '商品SKU ID',
  `product_name` varchar(128) NOT NULL COMMENT '商品名称',
  `rating` tinyint(4) NOT NULL DEFAULT '5' COMMENT '评分:1-5星',
  `content` varchar(1000) DEFAULT NULL COMMENT '评价内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '评价图片,最多9张,JSON格式',
  `is_anonymous` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否匿名:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-待审核,1-已发布,2-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_item_id` (`order_item_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_rating_status` (`rating`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

-- 内容评论表（精简版）
CREATE TABLE IF NOT EXISTS `content_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '用户名称',
  `user_avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父评论ID,0表示一级评论',
  `to_user_id` bigint(20) DEFAULT NULL COMMENT '回复目标用户ID',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待审核,1-已发布,2-已删除',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `reply_count` int(11) NOT NULL DEFAULT '0' COMMENT '回复数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_user_parent` (`user_id`, `parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容评论表';

-- 点赞表（合并内容点赞和评论点赞）
CREATE TABLE IF NOT EXISTS `user_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-评论,3-商品评价',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_object` (`user_id`,`object_type`,`object_id`),
  KEY `idx_object` (`object_type`, `object_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表'; 