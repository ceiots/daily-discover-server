-- 每日发现App核心表结构（MVP版本）
-- 设计原则: 每表字段不超过20个，无外键约束，针对高并发高可用场景优化
-- 保留核心功能，去除非必要字段，提高查询性能

-- 用户兴趣标签表（用户兴趣画像核心表）
CREATE TABLE IF NOT EXISTS `user_interest_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `interest_category` varchar(50) NOT NULL COMMENT '兴趣类别',
  `interest_tag` varchar(50) NOT NULL COMMENT '兴趣标签',
  `score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '兴趣分数',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-浏览,2-点赞,3-收藏,4-评论,5-分享,6-购买,7-搜索,8-明确选择',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`,`interest_category`,`interest_tag`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签表';

-- 用户行为记录表（简化版，合并了原行为序列表和行为统计表）
CREATE TABLE IF NOT EXISTS `user_behavior` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `behavior_type` tinyint(4) NOT NULL COMMENT '行为类型:1-浏览,2-点赞,3-收藏,4-评论,5-分享,6-购买,7-搜索',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-用户,4-话题',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `duration` int(11) DEFAULT NULL COMMENT '停留时长(秒)',
  `scene_type` tinyint(4) DEFAULT NULL COMMENT '场景类型:1-首页,2-详情页,3-搜索结果,4-推荐页,5-分类页',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型:1-手机,2-平板,3-PC',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_object` (`object_type`, `object_id`),
  KEY `idx_behavior_type` (`behavior_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为记录表';

-- 用户行为统计表（按天聚合的统计数据）
CREATE TABLE IF NOT EXISTS `user_behavior_daily_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `collect_count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `share_count` int(11) NOT NULL DEFAULT '0' COMMENT '分享次数',
  `purchase_count` int(11) NOT NULL DEFAULT '0' COMMENT '购买次数',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论次数',
  `search_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `active_minutes` int(11) NOT NULL DEFAULT '0' COMMENT '活跃时长(分钟)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为统计表';

-- 场景配置表（场景化推送配置）
CREATE TABLE IF NOT EXISTS `scene_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `scene_code` varchar(50) NOT NULL COMMENT '场景代码',
  `scene_name` varchar(50) NOT NULL COMMENT '场景名称',
  `scene_type` tinyint(4) NOT NULL COMMENT '场景类型:1-时间相关,2-位置相关,3-天气相关,4-节假日相关,5-用户状态相关',
  `trigger_condition` json NOT NULL COMMENT '触发条件',
  `content_strategy` json NOT NULL COMMENT '内容策略',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_code` (`scene_code`),
  KEY `idx_scene_type` (`scene_type`),
  KEY `idx_status_priority` (`status`, `priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场景配置表';

-- 用户收藏表（合并了原收藏表）
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_type` tinyint(4) NOT NULL COMMENT '类型: 1-内容 2-商品',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `folder_name` varchar(50) DEFAULT '默认' COMMENT '收藏夹名称',
  `note` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_type`, `target_id`),
  KEY `idx_user_folder` (`user_id`, `folder_name`),
  KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 热门趋势表（内容和关键词热度）
CREATE TABLE IF NOT EXISTS `trending_topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `category_id` int(11) DEFAULT NULL COMMENT '分类ID',
  `trend_score` decimal(8,2) NOT NULL COMMENT '趋势分数',
  `search_count` int(11) DEFAULT '0' COMMENT '搜索次数',
  `content_count` int(11) DEFAULT '0' COMMENT '相关内容数',
  `growth_rate` decimal(6,2) DEFAULT '0.00' COMMENT '增长率%',
  `trend_date` date NOT NULL COMMENT '日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword_date` (`keyword`, `trend_date`),
  KEY `idx_score_date` (`trend_score` DESC, `trend_date` DESC),
  KEY `idx_category_date` (`category_id`, `trend_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门趋势表';

-- AI智能标签表（简化版）
CREATE TABLE IF NOT EXISTS `ai_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `category` varchar(50) DEFAULT NULL COMMENT '标签分类',
  `weight` decimal(5,4) DEFAULT '1.0000' COMMENT '权重',
  `use_count` int(11) DEFAULT '0' COMMENT '使用次数',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_category` (`name`, `category`),
  KEY `idx_weight_count` (`weight` DESC, `use_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI智能标签表';

-- 个性化推荐表（简化版本）
CREATE TABLE IF NOT EXISTS `personalized_recommendation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-用户,4-话题',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `algorithm_type` varchar(50) NOT NULL COMMENT '算法类型',
  `reason` varchar(255) DEFAULT NULL COMMENT '推荐理由',
  `score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '推荐分数',
  `position` int(11) DEFAULT NULL COMMENT '推荐位置',
  `is_clicked` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否点击:0-否,1-是',
  `click_time` datetime DEFAULT NULL COMMENT '点击时间',
  `exposure_time` datetime DEFAULT NULL COMMENT '曝光时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_object` (`user_id`,`object_type`,`object_id`),
  KEY `idx_exposure_time` (`exposure_time`),
  KEY `idx_algorithm` (`algorithm_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个性化推荐表';

-- 用户设备表（简化版）
CREATE TABLE IF NOT EXISTS `user_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `device_id` varchar(100) NOT NULL COMMENT '设备唯一标识',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型',
  `os_type` varchar(20) DEFAULT NULL COMMENT '操作系统',
  `app_version` varchar(20) DEFAULT NULL COMMENT 'App版本',
  `push_token` varchar(200) DEFAULT NULL COMMENT '推送token',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_device` (`user_id`, `device_id`),
  KEY `idx_push_token` (`push_token`),
  KEY `idx_last_login` (`last_login_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';

-- 愿望清单表（简化版）
CREATE TABLE IF NOT EXISTS `wish_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `wish_title` varchar(200) NOT NULL COMMENT '愿望标题',
  `wish_description` varchar(500) DEFAULT NULL COMMENT '愿望描述',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `expected_price` decimal(10,2) DEFAULT NULL COMMENT '期望价格',
  `image_url` varchar(255) DEFAULT NULL COMMENT '参考图片',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-未匹配,2-已匹配,3-已购买,4-已过期',
  `matched_product_id` bigint(20) DEFAULT NULL COMMENT '匹配的商品ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='愿望清单表';

-- 惊喜盲盒表（简化版）
CREATE TABLE IF NOT EXISTS `surprise_box` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(100) NOT NULL COMMENT '盲盒名称',
  `description` varchar(255) DEFAULT NULL COMMENT '盲盒描述',
  `box_type` tinyint(4) NOT NULL COMMENT '盲盒类型:1-内容,2-商品,3-混合',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `rules` json DEFAULT NULL COMMENT '盲盒规则',
  `open_limit` int(11) DEFAULT NULL COMMENT '开启次数限制',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_box_type` (`box_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='惊喜盲盒表';

-- 用户盲盒记录表（用户开盒记录）
CREATE TABLE IF NOT EXISTS `user_surprise_box_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `box_id` bigint(20) NOT NULL COMMENT '盲盒ID',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-优惠券',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `object_name` varchar(200) DEFAULT NULL COMMENT '对象名称',
  `reaction` tinyint(4) DEFAULT NULL COMMENT '反应:1-喜欢,2-一般,3-不喜欢',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_box` (`user_id`,`box_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户盲盒记录表';

-- 社交关系表（合并了原用户社交关系表）
CREATE TABLE IF NOT EXISTS `user_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_user_id` bigint(20) NOT NULL COMMENT '目标用户ID',
  `relationship_type` tinyint(4) NOT NULL COMMENT '关系类型: 1-关注 2-好友 3-屏蔽',
  `interaction_score` decimal(5,2) DEFAULT '0.00' COMMENT '互动分数',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态: 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_user_id`),
  KEY `idx_target_type` (`target_user_id`, `relationship_type`),
  KEY `idx_interaction_score` (`interaction_score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交关系表';

-- 时光机内容表（简化版）
CREATE TABLE IF NOT EXISTS `time_machine_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-浏览记录,2-购买记录,3-评论记录,4-社交互动',
  `original_time` datetime NOT NULL COMMENT '原始时间',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-评论,4-社交',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-可展示,0-不可展示',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_original` (`user_id`,`original_time`),
  KEY `idx_object` (`object_type`,`object_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时光机内容表';

-- 算法模型配置表（简化版）
CREATE TABLE IF NOT EXISTS `algorithm_model_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `model_code` varchar(50) NOT NULL COMMENT '模型代码',
  `model_name` varchar(100) NOT NULL COMMENT '模型名称',
  `model_type` tinyint(4) NOT NULL COMMENT '模型类型:1-内容推荐,2-商品推荐,3-用户推荐,4-搜索优化',
  `parameters` json NOT NULL COMMENT '模型参数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `version` varchar(20) NOT NULL COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model_code_version` (`model_code`,`version`),
  KEY `idx_model_type` (`model_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='算法模型配置表';

-- 热门推荐内容表（简化版）
CREATE TABLE IF NOT EXISTS `trending_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品,4-话题',
  `title` varchar(200) NOT NULL COMMENT '内容标题',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `trending_score` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '热度分数',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `trending_date` date NOT NULL COMMENT '热门日期',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-下线',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签,多个逗号分隔',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_date` (`content_id`, `content_type`, `trending_date`),
  KEY `idx_trending_score` (`trending_score` DESC),
  KEY `idx_trending_date` (`trending_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门推荐内容表';

-- 发现页配置表（简化版）
CREATE TABLE IF NOT EXISTS `discovery_page_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_code` varchar(50) NOT NULL COMMENT '配置代码',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称',
  `page_type` tinyint(4) NOT NULL COMMENT '页面类型:1-首页,2-分类页,3-个人中心',
  `module_layout` json NOT NULL COMMENT '模块布局配置',
  `version` varchar(20) NOT NULL COMMENT '版本号',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_code_version` (`config_code`, `version`),
  KEY `idx_status` (`status`),
  KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发现页配置表'; 