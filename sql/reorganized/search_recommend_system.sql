-- 搜索推荐系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: search_recommend_mvp.sql和discovery_app_mvp.sql中的搜索推荐相关表
-- 高并发优化策略: 
-- 1. 表分区：按时间范围分区、按用户ID哈希分区、按内容类型列表分区
-- 2. 避免外键约束：通过业务逻辑保证数据一致性
-- 3. 索引优化：核心字段索引、组合索引、状态时间复合索引
-- 4. 冷热数据分离：通过时间范围分区实现

-- 搜索关键词表
CREATE TABLE IF NOT EXISTS `search_keyword` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关键词ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `search_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `is_hot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否热门:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常',
  `category_id` bigint(20) DEFAULT NULL COMMENT '关联分类ID',
  `suggestion_count` int(11) NOT NULL DEFAULT '0' COMMENT '被推荐次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword` (`keyword`),
  KEY `idx_search_count` (`search_count` DESC),
  KEY `idx_is_hot` (`is_hot`),
  KEY `idx_status` (`status`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索关键词表'
PARTITION BY HASH(id) PARTITIONS 8;

-- 搜索历史表
CREATE TABLE IF NOT EXISTS `search_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `keyword` varchar(100) NOT NULL COMMENT '搜索关键词',
  `search_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
  `search_result_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索结果数量',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_search_time` (`search_time` DESC),
  KEY `idx_keyword` (`keyword`(32))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表'
PARTITION BY RANGE (TO_DAYS(search_time)) (
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

-- 热搜榜表
CREATE TABLE IF NOT EXISTS `hot_search` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `rank_no` int(11) NOT NULL COMMENT '排名',
  `search_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `rank_date` date NOT NULL COMMENT '榜单日期',
  `trend` tinyint(4) DEFAULT NULL COMMENT '趋势:1-上升,2-下降,3-持平,4-新上榜',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_keyword` (`rank_date`,`keyword`),
  KEY `idx_rank_date` (`rank_date`,`rank_no`),
  KEY `idx_search_count` (`search_count` DESC),
  KEY `idx_status` (`status`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热搜榜表'
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

-- 搜索同义词表
CREATE TABLE IF NOT EXISTS `search_synonym` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `source_word` varchar(100) NOT NULL COMMENT '原词',
  `target_word` varchar(100) NOT NULL COMMENT '同义词',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source_target` (`source_word`,`target_word`),
  KEY `idx_source_word` (`source_word`),
  KEY `idx_target_word` (`target_word`),
  KEY `idx_priority` (`priority`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索同义词表'
PARTITION BY HASH(id) PARTITIONS 4;

-- 搜索黑名单表
CREATE TABLE IF NOT EXISTS `search_blacklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `reason` varchar(255) DEFAULT NULL COMMENT '原因',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-已解除,1-生效中',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword` (`keyword`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索黑名单表'
PARTITION BY HASH(id) PARTITIONS 4;

-- 用户兴趣标签表
CREATE TABLE IF NOT EXISTS `user_interest_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '标签类型:1-系统标签,2-用户标签,3-兴趣标签,4-行为标签',
  `weight` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '权重',
  `source` tinyint(4) NOT NULL DEFAULT '1' COMMENT '来源:1-用户选择,2-行为分析,3-内容分析,4-搜索记录',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`,`tag_id`,`tag_type`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_weight` (`weight` DESC),
  KEY `idx_source` (`source`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 用户兴趣画像表
CREATE TABLE IF NOT EXISTS `user_interest_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `category_preferences` varchar(1000) DEFAULT NULL COMMENT '分类偏好JSON',
  `tag_preferences` varchar(1000) DEFAULT NULL COMMENT '标签偏好JSON',
  `time_period_activity` varchar(1000) DEFAULT NULL COMMENT '时段活跃度JSON',
  `content_preferences` varchar(1000) DEFAULT NULL COMMENT '内容偏好JSON',
  `device_info` varchar(500) DEFAULT NULL COMMENT '设备信息JSON',
  `location_info` varchar(500) DEFAULT NULL COMMENT '位置信息JSON',
  `browse_behavior` varchar(1000) DEFAULT NULL COMMENT '浏览行为JSON',
  `active_days` int(11) NOT NULL DEFAULT '0' COMMENT '活跃天数',
  `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_active_days` (`active_days` DESC),
  KEY `idx_last_active_time` (`last_active_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣画像表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 内容特征表
CREATE TABLE IF NOT EXISTS `content_feature` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品,4-话题,5-用户',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签JSON',
  `keywords` varchar(500) DEFAULT NULL COMMENT '关键词JSON',
  `quality_score` decimal(5,2) DEFAULT NULL COMMENT '质量分',
  `heat_score` decimal(5,2) DEFAULT NULL COMMENT '热度分',
  `time_sensitivity` tinyint(4) DEFAULT NULL COMMENT '时效性:1-高,2-中,3-低',
  `feature_vector` text DEFAULT NULL COMMENT '特征向量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content` (`content_id`,`content_type`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_quality_score` (`quality_score` DESC),
  KEY `idx_heat_score` (`heat_score` DESC),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容特征表'
PARTITION BY LIST(content_type) (
  PARTITION p_article VALUES IN (1),
  PARTITION p_video VALUES IN (2),
  PARTITION p_product VALUES IN (3),
  PARTITION p_topic VALUES IN (4),
  PARTITION p_user VALUES IN (5)
);

-- 推荐结果表
CREATE TABLE IF NOT EXISTS `recommend_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `scene_type` tinyint(4) NOT NULL COMMENT '场景类型:1-首页推荐,2-相关推荐,3-猜你喜欢,4-热门推荐',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品,4-话题,5-用户',
  `algorithm_type` tinyint(4) DEFAULT NULL COMMENT '算法类型:1-协同过滤,2-内容相似,3-规则,4-热门,5-新品',
  `score` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '推荐分数',
  `rank` int(11) DEFAULT NULL COMMENT '排名',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-未曝光,1-已曝光,2-已点击,3-已跳过',
  `expose_time` datetime DEFAULT NULL COMMENT '曝光时间',
  `click_time` datetime DEFAULT NULL COMMENT '点击时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_scene` (`user_id`,`scene_type`),
  KEY `idx_content` (`content_id`,`content_type`),
  KEY `idx_algorithm_type` (`algorithm_type`),
  KEY `idx_score` (`score` DESC),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐结果表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 推荐反馈表
CREATE TABLE IF NOT EXISTS `recommend_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品,4-话题,5-用户',
  `scene_type` tinyint(4) NOT NULL COMMENT '场景类型:1-首页推荐,2-相关推荐,3-猜你喜欢,4-热门推荐',
  `feedback_type` tinyint(4) NOT NULL COMMENT '反馈类型:1-不感兴趣,2-内容质量差,3-重复,4-不喜欢作者,5-其他',
  `reason` varchar(255) DEFAULT NULL COMMENT '原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_content` (`content_id`,`content_type`),
  KEY `idx_scene_type` (`scene_type`),
  KEY `idx_feedback_type` (`feedback_type`),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐反馈表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 内容相似表
CREATE TABLE IF NOT EXISTS `content_similarity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品,4-话题,5-用户',
  `similar_content_id` bigint(20) NOT NULL COMMENT '相似内容ID',
  `similar_content_type` tinyint(4) NOT NULL COMMENT '相似内容类型',
  `similarity_score` decimal(5,4) NOT NULL DEFAULT '0.0000' COMMENT '相似度分数',
  `similarity_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '相似度类型:1-内容相似,2-标签相似,3-行为相似',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_similar` (`content_id`,`content_type`,`similar_content_id`,`similar_content_type`),
  KEY `idx_similar_content` (`similar_content_id`,`similar_content_type`),
  KEY `idx_similarity_score` (`similarity_score` DESC),
  KEY `idx_similarity_type` (`similarity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容相似表'
PARTITION BY HASH(content_id) PARTITIONS 8;

-- 每日发现表
CREATE TABLE IF NOT EXISTS `daily_discovery` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(100) NOT NULL COMMENT '发现标题',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '副标题',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图',
  `discovery_date` date NOT NULL COMMENT '发现日期',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型:1-内容集合,2-专题,3-活动,4-榜单',
  `content_ids` varchar(1000) DEFAULT NULL COMMENT '内容ID集合',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `curator_id` bigint(20) DEFAULT NULL COMMENT '策展人ID',
  `curator_name` varchar(64) DEFAULT NULL COMMENT '策展人名称',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_discovery_date` (`discovery_date`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_curator_id` (`curator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日发现表'
PARTITION BY RANGE (TO_DAYS(discovery_date)) (
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

-- 内容埋点表
CREATE TABLE IF NOT EXISTS `content_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品,4-话题,5-用户',
  `scene_type` tinyint(4) NOT NULL COMMENT '场景类型:1-首页推荐,2-相关推荐,3-猜你喜欢,4-热门推荐',
  `event_type` tinyint(4) NOT NULL COMMENT '事件类型:1-曝光,2-点击,3-停留,4-点赞,5-收藏,6-分享',
  `stay_time` int(11) DEFAULT NULL COMMENT '停留时间(秒)',
  `event_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件时间',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC',
  `device_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
  `event_data` varchar(1000) DEFAULT NULL COMMENT '事件数据JSON',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_content` (`content_id`,`content_type`),
  KEY `idx_scene_type` (`scene_type`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_event_time` (`event_time`),
  KEY `idx_device_type` (`device_type`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容埋点表'
PARTITION BY RANGE (TO_DAYS(event_time)) (
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