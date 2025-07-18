-- 创建内容管理系统数据库
CREATE DATABASE IF NOT EXISTS content_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用内容管理系统数据库
USE content_db;

-- 内容管理系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: content_review_mvp.sql和部分discovery_app_mvp.sql中的内容相关表
-- 高并发优化策略:
--   1. 使用表分区: 根据业务特点选择时间范围分区、哈希分区或列表分区
--   2. 精心设计索引: 覆盖高频查询场景，避免过多索引影响写入性能
--   3. 冷热数据分离: 活跃数据保留在主库，非活跃数据定期归档
--   4. 适当冗余设计: 减少表连接操作，提高查询效率
--   5. 无外键约束: 提高写入性能，避免跨表锁定

-- 内容基础表
CREATE TABLE IF NOT EXISTS `content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容类型:1-文章,2-视频,3-直播,4-问答,5-短内容',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
  `content` text DEFAULT NULL COMMENT '内容详情',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图',
  `user_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `author_name` varchar(64) DEFAULT NULL COMMENT '作者名称',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-草稿,1-待审核,2-已发布,3-已下线,4-已删除',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞量',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论量',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签,逗号分隔',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_tags` (`tags`(32))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容基础表'
-- 按发布时间范围分区，便于历史数据归档和管理
PARTITION BY RANGE (TO_DAYS(publish_time)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 内容分类表
CREATE TABLE IF NOT EXISTS `content_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `code` varchar(50) DEFAULT NULL COMMENT '分类编码',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '分类级别',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_parent` (`name`,`parent_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容分类表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容类型:1-文章,2-视频,3-直播,4-问答,5-短内容',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父评论ID',
  `root_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '根评论ID',
  `user_id` bigint(20) NOT NULL COMMENT '评论用户ID',
  `to_user_id` bigint(20) DEFAULT NULL COMMENT '回复用户ID',
  `content` varchar(1000) NOT NULL COMMENT '评论内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片,JSON数组',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞量',
  `dislike_count` int(11) NOT NULL DEFAULT '0' COMMENT '不喜欢量',
  `reply_count` int(11) NOT NULL DEFAULT '0' COMMENT '回复量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-待审核,1-已发布,2-已屏蔽',
  `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶:0-否,1-是',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热门:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`,`content_type`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_root_id` (`root_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_to_user_id` (`to_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表'
-- 按内容ID哈希分区，避免热点内容评论集中，提高并发写入性能
PARTITION BY KEY(content_id)
PARTITIONS 8;

-- 内容标签表
CREATE TABLE IF NOT EXISTS `content_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型:1-内容标签,2-用户标签,3-商品标签',
  `use_count` int(11) NOT NULL DEFAULT '0' COMMENT '使用次数',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`,`type`),
  KEY `idx_use_count` (`use_count` DESC),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签表';

-- 内容标签关联表
CREATE TABLE IF NOT EXISTS `content_tag_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签关联表'
-- 按标签ID哈希分区，提高热门标签的查询效率
PARTITION BY KEY(tag_id)
PARTITIONS 8;

-- 用户点赞记录表
CREATE TABLE IF NOT EXISTS `user_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `target_type` tinyint(4) NOT NULL COMMENT '目标类型:1-内容,2-评论,3-用户,4-商品',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-取消,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`target_type`),
  KEY `idx_target` (`target_id`,`target_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞记录表'
-- 按用户ID哈希分区，分散高频用户的写入压力
PARTITION BY KEY(user_id)
PARTITIONS 16;

-- 用户收藏记录表
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `target_type` tinyint(4) NOT NULL COMMENT '目标类型:1-内容,2-评论,3-用户,4-商品',
  `collection_id` bigint(20) DEFAULT NULL COMMENT '收藏夹ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-取消,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`target_type`),
  KEY `idx_target` (`target_id`,`target_type`),
  KEY `idx_collection_id` (`collection_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏记录表'
-- 按用户ID哈希分区，分散高频用户的写入压力
PARTITION BY KEY(user_id)
PARTITIONS 16;

-- 收藏夹表
CREATE TABLE IF NOT EXISTS `favorite_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收藏夹名称',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图',
  `is_public` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否公开:0-私密,1-公开',
  `item_count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数量',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏夹表';

-- 内容审核记录表
CREATE TABLE IF NOT EXISTS `content_review` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-直播,4-问答,5-短内容,6-评论,7-用户资料',
  `review_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审核状态:0-待审核,1-通过,2-拒绝,3-人工复核',
  `auto_review_status` tinyint(4) DEFAULT NULL COMMENT '自动审核状态:0-待审核,1-通过,2-拒绝,3-人工复核',
  `manual_review_status` tinyint(4) DEFAULT NULL COMMENT '人工审核状态:0-待审核,1-通过,2-拒绝',
  `reviewer_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `reviewer_name` varchar(64) DEFAULT NULL COMMENT '审核人姓名',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `review_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `review_result` text DEFAULT NULL COMMENT '审核详细结果',
  `ai_suggestion` varchar(50) DEFAULT NULL COMMENT 'AI建议',
  `ai_score` decimal(5,2) DEFAULT NULL COMMENT 'AI评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`,`content_type`),
  KEY `idx_review_status` (`review_status`),
  KEY `idx_auto_review_status` (`auto_review_status`),
  KEY `idx_manual_review_status` (`manual_review_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容审核记录表'
-- 按审核状态列表分区，便于快速定位待处理的审核任务
PARTITION BY LIST(review_status) (
  PARTITION p_pending VALUES IN (0),
  PARTITION p_passed VALUES IN (1),
  PARTITION p_rejected VALUES IN (2),
  PARTITION p_manual VALUES IN (3)
);

-- 敏感词表
CREATE TABLE IF NOT EXISTS `sensitive_word` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `word` varchar(100) NOT NULL COMMENT '敏感词',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型:1-政治,2-色情,3-暴力,4-广告,5-其他',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '级别:1-轻度,2-中度,3-重度',
  `action` tinyint(4) NOT NULL DEFAULT '1' COMMENT '处理方式:1-替换,2-审核,3-拒绝',
  `replace_word` varchar(100) DEFAULT NULL COMMENT '替换词',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word` (`word`),
  KEY `idx_type` (`type`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表'
-- 按敏感词类型列表分区，提高审核时的查询效率
PARTITION BY LIST(type) (
  PARTITION p_political VALUES IN (1),
  PARTITION p_porn VALUES IN (2),
  PARTITION p_violence VALUES IN (3),
  PARTITION p_ad VALUES IN (4),
  PARTITION p_other VALUES IN (5)
);

-- 内容举报表
CREATE TABLE IF NOT EXISTS `content_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `reporter_id` bigint(20) NOT NULL COMMENT '举报人ID',
  `target_id` bigint(20) NOT NULL COMMENT '被举报目标ID',
  `target_type` tinyint(4) NOT NULL COMMENT '举报类型:1-内容,2-评论,3-用户,4-商品',
  `reason_type` tinyint(4) NOT NULL COMMENT '举报原因类型:1-色情低俗,2-广告营销,3-攻击谩骂,4-违法违规,5-其他',
  `reason` varchar(255) DEFAULT NULL COMMENT '举报原因',
  `images` varchar(1000) DEFAULT NULL COMMENT '举报图片,JSON数组',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待处理,1-已处理,2-已忽略',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handler_id` bigint(20) DEFAULT NULL COMMENT '处理人ID',
  `handle_result` varchar(255) DEFAULT NULL COMMENT '处理结果',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_reporter_id` (`reporter_id`),
  KEY `idx_target` (`target_id`,`target_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容举报表'
-- 按状态列表分区，便于快速处理待处理的举报
PARTITION BY LIST(status) (
  PARTITION p_pending VALUES IN (0),
  PARTITION p_processed VALUES IN (1),
  PARTITION p_ignored VALUES IN (2)
);

-- 审核规则表
CREATE TABLE IF NOT EXISTS `review_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_type` tinyint(4) NOT NULL COMMENT '规则类型:1-文字,2-图片,3-视频,4-直播',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-直播,4-问答,5-短内容,6-评论,7-用户资料',
  `rule_config` text NOT NULL COMMENT '规则配置JSON',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核规则表';

-- 用户反馈表
CREATE TABLE IF NOT EXISTS `user_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `feedback_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '反馈类型:1-功能建议,2-内容举报,3-产品体验,4-其他',
  `content` varchar(1000) NOT NULL COMMENT '反馈内容',
  `images` varchar(1000) DEFAULT NULL COMMENT '图片,JSON数组',
  `contact` varchar(100) DEFAULT NULL COMMENT '联系方式',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待处理,1-处理中,2-已处理,3-已关闭',
  `handle_result` varchar(255) DEFAULT NULL COMMENT '处理结果',
  `handler_id` bigint(20) DEFAULT NULL COMMENT '处理人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_feedback_type` (`feedback_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈表'
-- 按反馈类型列表分区，便于分类处理
PARTITION BY LIST(feedback_type) (
  PARTITION p_feature VALUES IN (1),
  PARTITION p_report VALUES IN (2),
  PARTITION p_experience VALUES IN (3),
  PARTITION p_other VALUES IN (4)
);

-- 用户关注表
CREATE TABLE IF NOT EXISTS `user_follow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `follow_user_id` bigint(20) NOT NULL COMMENT '被关注用户ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-取消,1-有效',
  `remark` varchar(50) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`,`follow_user_id`),
  KEY `idx_follow_user_id` (`follow_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表'
-- 按用户ID哈希分区，分散高频用户的写入压力
PARTITION BY KEY(user_id)
PARTITIONS 16;

-- 话题表
CREATE TABLE IF NOT EXISTS `topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '话题ID',
  `name` varchar(50) NOT NULL COMMENT '话题名称',
  `description` varchar(255) DEFAULT NULL COMMENT '话题描述',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `discussion_count` int(11) NOT NULL DEFAULT '0' COMMENT '讨论量',
  `follow_count` int(11) NOT NULL DEFAULT '0' COMMENT '关注量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热门:0-否,1-是',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='话题表';

-- 内容话题关联表
CREATE TABLE IF NOT EXISTS `content_topic_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `topic_id` bigint(20) NOT NULL COMMENT '话题ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_topic` (`content_id`,`topic_id`),
  KEY `idx_topic_id` (`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容话题关联表'
-- 按话题ID哈希分区，提高热门话题的查询效率
PARTITION BY KEY(topic_id)
PARTITIONS 8;

-- 热门排行表
CREATE TABLE IF NOT EXISTS `content_rank` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-直播,4-问答,5-短内容',
  `rank_type` tinyint(4) NOT NULL COMMENT '排行类型:1-热门,2-推荐,3-最新',
  `rank_date` date NOT NULL COMMENT '排行日期',
  `score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '排行分数',
  `rank_no` int(11) DEFAULT NULL COMMENT '排名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_rank_date` (`content_id`,`content_type`,`rank_type`,`rank_date`),
  KEY `idx_rank_type_date` (`rank_type`,`rank_date`),
  KEY `idx_score` (`score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门排行表'
-- 按排行日期范围分区，便于历史数据归档
PARTITION BY RANGE (TO_DAYS(rank_date)) (
  PARTITION p_2025q1 VALUES LESS THAN (TO_DAYS('2025-04-01')),
  PARTITION p_2025q2 VALUES LESS THAN (TO_DAYS('2025-07-01')),
  PARTITION p_2025q3 VALUES LESS THAN (TO_DAYS('2025-10-01')),
  PARTITION p_2025q4 VALUES LESS THAN (TO_DAYS('2026-01-01')),
  PARTITION p_2026q1 VALUES LESS THAN (TO_DAYS('2026-04-01')),
  PARTITION p_2026q2 VALUES LESS THAN (TO_DAYS('2026-07-01')),
  PARTITION p_2026q3 VALUES LESS THAN (TO_DAYS('2026-10-01')),
  PARTITION p_2026q4 VALUES LESS THAN (TO_DAYS('2027-01-01')),
  PARTITION p_future VALUES LESS THAN MAXVALUE
);
