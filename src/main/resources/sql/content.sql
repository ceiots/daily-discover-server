-- 创建内容表
CREATE TABLE IF NOT EXISTS `content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `content` text COMMENT '正文内容',
  `images` text COMMENT '图片URL列表(JSON格式)',
  `tags` varchar(255) DEFAULT '[]' COMMENT '标签列表(JSON格式)',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0草稿，1已发布',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime NOT NULL COMMENT '更新时间',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论次数',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户创建的图文内容';

-- 创建内容标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `count` int(11) NOT NULL DEFAULT '0' COMMENT '使用次数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签';

-- 创建内容点赞表
CREATE TABLE IF NOT EXISTS `content_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_user` (`content_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容点赞记录';

-- 创建内容评论表
CREATE TABLE IF NOT EXISTS `content_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `comment` varchar(1000) NOT NULL COMMENT '评论内容',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父评论ID(回复)',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容评论'; 