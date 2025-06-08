-- 每日发现App核心表结构
-- 支持智能推荐、场景化内容、互动功能和用户体验优化

-- 用户兴趣标签表（用户兴趣画像）
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

-- 用户行为序列表（用户行为轨迹）
CREATE TABLE IF NOT EXISTS `user_behavior_sequence` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `behavior_type` tinyint(4) NOT NULL COMMENT '行为类型:1-浏览,2-点赞,3-收藏,4-评论,5-分享,6-购买,7-搜索',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-用户,4-话题',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `duration` int(11) DEFAULT NULL COMMENT '停留时长(秒)',
  `device_info` varchar(100) DEFAULT NULL COMMENT '设备信息',
  `location` varchar(100) DEFAULT NULL COMMENT '地理位置',
  `scene_type` tinyint(4) DEFAULT NULL COMMENT '场景类型:1-首页,2-详情页,3-搜索结果,4-推荐页,5-分类页',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_object` (`object_type`, `object_id`),
  KEY `idx_behavior_type` (`behavior_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为序列表';

-- 用户行为统计表 
CREATE TABLE IF NOT EXISTS `user_behavior_stats` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_type` tinyint(4) NOT NULL COMMENT '目标类型: 1-内容 2-商品',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `action_type` tinyint(4) NOT NULL COMMENT '行为: 1-浏览 2-点赞 3-分享 4-收藏 5-购买 6-点击',
  `session_id` varchar(64) DEFAULT NULL COMMENT '会话ID',
  `duration` int(11) DEFAULT '0' COMMENT '停留时长（秒）',
  `source` varchar(50) DEFAULT NULL COMMENT '来源页面',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型: 1-手机 2-平板 3-PC',
  `ip` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_action` (`user_id`, `action_type`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_session` (`session_id`)
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

-- 用户收藏表 
CREATE TABLE IF NOT EXISTS `user_favorite_item` (
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

-- 热门趋势表 
CREATE TABLE IF NOT EXISTS `trending_topic` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `category_id` int(11) DEFAULT NULL COMMENT '分类ID',
  `trend_score` decimal(8,2) NOT NULL COMMENT '趋势分数',
  `search_count` int(11) DEFAULT '0' COMMENT '搜索次数',
  `content_count` int(11) DEFAULT '0' COMMENT '相关内容数',
  `growth_rate` decimal(6,2) DEFAULT '0.00' COMMENT '增长率%',
  `source` tinyint(4) DEFAULT '1' COMMENT '来源: 1-平台内 2-外部导入',
  `trend_date` date NOT NULL COMMENT '日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword_date` (`keyword`, `trend_date`),
  KEY `idx_score_date` (`trend_score` DESC, `trend_date` DESC),
  KEY `idx_category_date` (`category_id`, `trend_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门趋势表';

-- AI智能标签表 
CREATE TABLE IF NOT EXISTS `ai_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `category` varchar(50) DEFAULT NULL COMMENT '标签分类',
  `weight` decimal(5,4) DEFAULT '1.0000' COMMENT '权重',
  `use_count` int(11) DEFAULT '0' COMMENT '使用次数',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `color` varchar(7) DEFAULT NULL COMMENT '颜色代码',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_category` (`name`, `category`),
  KEY `idx_weight_count` (`weight` DESC, `use_count` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI智能标签表';

-- 个性化推荐表（推荐结果记录）
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
  `feedback` tinyint(4) DEFAULT NULL COMMENT '反馈:1-喜欢,2-不感兴趣,3-举报',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_object` (`user_id`,`object_type`,`object_id`),
  KEY `idx_exposure_time` (`exposure_time`),
  KEY `idx_algorithm` (`algorithm_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个性化推荐表';

-- 用户设备表 
CREATE TABLE IF NOT EXISTS `user_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `device_id` varchar(100) NOT NULL COMMENT '设备唯一标识',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型',
  `os_type` varchar(20) DEFAULT NULL COMMENT '操作系统',
  `os_version` varchar(20) DEFAULT NULL COMMENT '系统版本',
  `app_version` varchar(20) DEFAULT NULL COMMENT 'App版本',
  `push_token` varchar(200) DEFAULT NULL COMMENT '推送token',
  `is_primary` tinyint(4) DEFAULT '0' COMMENT '是否主设备',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_device` (`user_id`, `device_id`),
  KEY `idx_push_token` (`push_token`),
  KEY `idx_last_login` (`last_login_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';

-- 愿望清单表（用户期望商品）
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
  `match_time` datetime DEFAULT NULL COMMENT '匹配时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='愿望清单表';

-- 情境推送配置表 
CREATE TABLE IF NOT EXISTS `context_push_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `time_morning_start` time DEFAULT '07:00:00' COMMENT '晨间开始时间',
  `time_noon_start` time DEFAULT '11:30:00' COMMENT '午间开始时间',
  `time_evening_start` time DEFAULT '18:00:00' COMMENT '晚间开始时间',
  `weather_push` tinyint(4) DEFAULT '1' COMMENT '天气推送开关',
  `location_push` tinyint(4) DEFAULT '1' COMMENT '地理位置推送开关',
  `festival_push` tinyint(4) DEFAULT '1' COMMENT '节庆推送开关',
  `social_push` tinyint(4) DEFAULT '1' COMMENT '社交推送开关',
  `fatigue_detection` tinyint(4) DEFAULT '1' COMMENT '疲劳检测开关',
  `surprise_box` tinyint(4) DEFAULT '1' COMMENT '惊喜盲盒开关',
  `timemachine_push` tinyint(4) DEFAULT '1' COMMENT '时光机推送开关',
  `gamify_learning` tinyint(4) DEFAULT '1' COMMENT '游戏化学习开关',
  `push_frequency` tinyint(4) DEFAULT '3' COMMENT '推送频率: 1-低 2-中 3-高',
  `quiet_hours_start` time DEFAULT '22:00:00' COMMENT '免打扰开始时间',
  `quiet_hours_end` time DEFAULT '07:00:00' COMMENT '免打扰结束时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='情境推送配置表';

-- 惊喜盲盒表（随机优质推荐）
CREATE TABLE IF NOT EXISTS `surprise_box` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(100) NOT NULL COMMENT '盲盒名称',
  `description` varchar(255) DEFAULT NULL COMMENT '盲盒描述',
  `box_type` tinyint(4) NOT NULL COMMENT '盲盒类型:1-内容,2-商品,3-混合',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `rules` json DEFAULT NULL COMMENT '盲盒规则',
  `open_limit` int(11) DEFAULT NULL COMMENT '开启次数限制',
  `refresh_time` varchar(50) DEFAULT NULL COMMENT '刷新时间,cron表达式',
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
  `box_name` varchar(100) DEFAULT NULL COMMENT '盲盒名称',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-优惠券',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `object_name` varchar(200) DEFAULT NULL COMMENT '对象名称',
  `reaction` tinyint(4) DEFAULT NULL COMMENT '反应:1-喜欢,2-一般,3-不喜欢',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_box` (`user_id`,`box_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户盲盒记录表';

-- 社交关系表 
CREATE TABLE IF NOT EXISTS `user_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_user_id` bigint(20) NOT NULL COMMENT '目标用户ID',
  `relationship_type` tinyint(4) NOT NULL COMMENT '关系类型: 1-关注 2-好友 3-屏蔽',
  `common_interests` json DEFAULT NULL COMMENT '共同兴趣标签',
  `interaction_score` decimal(5,2) DEFAULT '0.00' COMMENT '互动分数',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态: 0-删除 1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_user_id`),
  KEY `idx_target_type` (`target_user_id`, `relationship_type`),
  KEY `idx_interaction_score` (`interaction_score` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交关系表';

-- 时光机内容表（往日的今天）
CREATE TABLE IF NOT EXISTS `time_machine_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-浏览记录,2-购买记录,3-评论记录,4-社交互动',
  `original_time` datetime NOT NULL COMMENT '原始时间',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-评论,4-社交',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `title` varchar(200) DEFAULT NULL COMMENT '标题',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-可展示,0-不可展示',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_original` (`user_id`,`original_time`),
  KEY `idx_object` (`object_type`,`object_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时光机内容表';

-- 用户社交关系表（好友/关注关系）
CREATE TABLE IF NOT EXISTS `user_social_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_user_id` bigint(20) NOT NULL COMMENT '目标用户ID',
  `relation_type` tinyint(4) NOT NULL COMMENT '关系类型:1-关注,2-好友',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-有效,0-无效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_user_id`,`relation_type`),
  KEY `idx_target_user` (`target_user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户社交关系表';

-- 游戏化学习记录表 
CREATE TABLE IF NOT EXISTS `gamify_learning_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `learning_type` tinyint(4) NOT NULL COMMENT '学习类型: 1-名著快读 2-知识碎片 3-技能学习',
  `title` varchar(200) NOT NULL COMMENT '学习内容标题',
  `progress` tinyint(4) DEFAULT '0' COMMENT '学习进度百分比',
  `duration_seconds` int(11) DEFAULT '0' COMMENT '学习时长秒数',
  `quiz_score` tinyint(4) DEFAULT '0' COMMENT '测试得分',
  `badges` json DEFAULT NULL COMMENT '获得徽章数组',
  `is_completed` tinyint(4) DEFAULT '0' COMMENT '是否完成',
  `completion_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_type` (`user_id`, `learning_type`),
  KEY `idx_user_progress` (`user_id`, `progress`),
  KEY `idx_completion` (`is_completed`, `completion_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏化学习记录表';

-- 知识碎片表（碎片化知识内容）
CREATE TABLE IF NOT EXISTS `knowledge_fragment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `source_content_id` bigint(20) DEFAULT NULL COMMENT '源内容ID',
  `category` varchar(50) DEFAULT NULL COMMENT '类别',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签,多个逗号分隔',
  `difficulty_level` tinyint(4) DEFAULT '1' COMMENT '难度级别:1-入门,2-初级,3-中级,4-高级,5-专家',
  `estimated_time` int(11) DEFAULT NULL COMMENT '预计阅读时间(秒)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_source_content` (`source_content_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  FULLTEXT KEY `ft_title_tags` (`title`, `tags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识碎片表';

-- 用户体验反馈表（体验疲劳/满意度）
CREATE TABLE IF NOT EXISTS `user_experience_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) DEFAULT NULL COMMENT '会话ID',
  `fatigue_level` tinyint(4) DEFAULT NULL COMMENT '疲劳度:1-5级',
  `satisfaction` tinyint(4) DEFAULT NULL COMMENT '满意度:1-5级',
  `feedback_type` tinyint(4) NOT NULL COMMENT '反馈类型:1-自动记录,2-主动反馈',
  `context` json DEFAULT NULL COMMENT '上下文信息',
  `device_info` varchar(100) DEFAULT NULL COMMENT '设备信息',
  `location` varchar(100) DEFAULT NULL COMMENT '地理位置',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_session` (`user_id`,`session_id`),
  KEY `idx_fatigue` (`fatigue_level`),
  KEY `idx_satisfaction` (`satisfaction`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户体验反馈表';

-- 地理位置兴趣点表（位置相关推荐）
CREATE TABLE IF NOT EXISTS `geo_interest_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `category` varchar(50) NOT NULL COMMENT '类别',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签,多个逗号分隔',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `longitude` decimal(10,6) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,6) DEFAULT NULL COMMENT '纬度',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_location` (`province`,`city`,`district`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  SPATIAL KEY `idx_geo` (`longitude`, `latitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地理位置兴趣点表';

-- 交互方式记录表（多模态交互）
CREATE TABLE IF NOT EXISTS `interaction_method_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) DEFAULT NULL COMMENT '会话ID',
  `interaction_type` tinyint(4) NOT NULL COMMENT '交互类型:1-点击,2-语音,3-图像识别,4-手势,5-表情',
  `interaction_content` text DEFAULT NULL COMMENT '交互内容',
  `result_type` tinyint(4) DEFAULT NULL COMMENT '结果类型:1-搜索,2-浏览,3-操作,4-反馈',
  `result_content` text DEFAULT NULL COMMENT '结果内容',
  `success` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否成功:0-失败,1-成功',
  `device_info` varchar(100) DEFAULT NULL COMMENT '设备信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_session` (`user_id`,`session_id`),
  KEY `idx_interaction_type` (`interaction_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交互方式记录表';

-- 算法模型配置表（推荐算法配置）
CREATE TABLE IF NOT EXISTS `algorithm_model_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `model_code` varchar(50) NOT NULL COMMENT '模型代码',
  `model_name` varchar(100) NOT NULL COMMENT '模型名称',
  `model_type` tinyint(4) NOT NULL COMMENT '模型类型:1-内容推荐,2-商品推荐,3-用户推荐,4-搜索优化',
  `parameters` json NOT NULL COMMENT '模型参数',
  `description` varchar(500) DEFAULT NULL COMMENT '模型描述',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `version` varchar(20) NOT NULL COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model_code_version` (`model_code`,`version`),
  KEY `idx_model_type` (`model_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='算法模型配置表';

-- A/B测试配置表（功能与算法测试）
CREATE TABLE IF NOT EXISTS `ab_test_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `test_code` varchar(50) NOT NULL COMMENT '测试代码',
  `test_name` varchar(100) NOT NULL COMMENT '测试名称',
  `description` varchar(500) DEFAULT NULL COMMENT '测试描述',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `traffic_percent` decimal(5,2) NOT NULL COMMENT '流量百分比',
  `test_objects` json NOT NULL COMMENT '测试对象配置',
  `metrics` json NOT NULL COMMENT '测试指标',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-未开始,1-进行中,2-已结束,3-已暂停',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_test_code` (`test_code`),
  KEY `idx_status_time` (`status`,`start_time`,`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='A/B测试配置表';

-- 用户A/B测试分组表（用户测试分组）
CREATE TABLE IF NOT EXISTS `user_ab_test_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `test_id` bigint(20) NOT NULL COMMENT '测试ID',
  `test_code` varchar(50) NOT NULL COMMENT '测试代码',
  `group_code` varchar(50) NOT NULL COMMENT '分组代码',
  `join_time` datetime NOT NULL COMMENT '加入时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_test` (`user_id`,`test_id`),
  KEY `idx_test_group` (`test_code`,`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户A/B测试分组表';

-- 热门推荐内容表（从super.sql整合trending_topics）
CREATE TABLE IF NOT EXISTS `trending_content` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品,4-话题',
  `title` varchar(200) NOT NULL COMMENT '内容标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '内容摘要',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `url` varchar(255) DEFAULT NULL COMMENT '内容链接',
  `trending_score` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '热度分数',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
  `share_count` int(11) NOT NULL DEFAULT '0' COMMENT '分享数',
  `comment_count` int(11) NOT NULL DEFAULT '0' COMMENT '评论数',
  `trending_date` date NOT NULL COMMENT '热门日期',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-下线',
  `source` varchar(50) DEFAULT NULL COMMENT '来源',
  `tags` varchar(255) DEFAULT NULL COMMENT '标签,多个逗号分隔',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_date` (`content_id`, `content_type`, `trending_date`),
  KEY `idx_trending_score` (`trending_score` DESC),
  KEY `idx_trending_date` (`trending_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门推荐内容表';

-- 发现页配置表（定制发现页显示）
CREATE TABLE IF NOT EXISTS `discovery_page_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_code` varchar(50) NOT NULL COMMENT '配置代码',
  `config_name` varchar(100) NOT NULL COMMENT '配置名称',
  `page_type` tinyint(4) NOT NULL COMMENT '页面类型:1-首页,2-分类页,3-个人中心',
  `module_layout` json NOT NULL COMMENT '模块布局配置',
  `target_user_groups` json DEFAULT NULL COMMENT '目标用户分组',
  `version` varchar(20) NOT NULL COMMENT '版本号',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_code_version` (`config_code`, `version`),
  KEY `idx_status_priority` (`status`, `priority`),
  KEY `idx_time_range` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发现页配置表';

-- AI内容生成配置表
CREATE TABLE IF NOT EXISTS `ai_content_generation_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `template_code` varchar(50) NOT NULL COMMENT '模板代码',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-推荐理由,3-产品描述',
  `prompt_template` text NOT NULL COMMENT '提示模板',
  `parameters` json DEFAULT NULL COMMENT '参数配置',
  `output_format` varchar(50) DEFAULT 'markdown' COMMENT '输出格式',
  `max_tokens` int(11) DEFAULT '2000' COMMENT '最大生成长度',
  `model_name` varchar(50) DEFAULT NULL COMMENT '模型名称',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI内容生成配置表';

-- 内容推荐理由表
CREATE TABLE IF NOT EXISTS `content_recommendation_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-商品',
  `reason_type` tinyint(4) NOT NULL COMMENT '理由类型:1-兴趣匹配,2-行为关联,3-人群相似,4-热门推荐,5-时间相关,6-位置相关',
  `reason_text` varchar(255) NOT NULL COMMENT '推荐理由文本',
  `matching_tags` varchar(255) DEFAULT NULL COMMENT '匹配的标签',
  `matching_score` decimal(5,2) DEFAULT NULL COMMENT '匹配分数',
  `is_clicked` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否点击:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_content` (`user_id`, `content_id`, `content_type`),
  KEY `idx_reason_type` (`reason_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容推荐理由表';

-- 用户情绪记录表
CREATE TABLE IF NOT EXISTS `user_emotion_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) DEFAULT NULL COMMENT '会话ID',
  `emotion_type` tinyint(4) NOT NULL COMMENT '情绪类型:1-开心,2-放松,3-专注,4-困倦,5-焦虑,6-沮丧',
  `emotion_intensity` tinyint(4) DEFAULT '3' COMMENT '情绪强度:1-5级',
  `source_type` tinyint(4) NOT NULL COMMENT '来源类型:1-用户反馈,2-行为推断,3-文本分析',
  `context_data` json DEFAULT NULL COMMENT '上下文数据',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time`),
  KEY `idx_emotion_type` (`emotion_type`, `emotion_intensity`),
  KEY `idx_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户情绪记录表'; 