-- 搜索推荐功能表结构（MVP版本）
-- 设计原则: 每表字段不超过20个，无外键约束，针对高并发高可用场景优化
-- 保留核心功能，去除非必要字段，提高查询性能

-- 搜索配置表（搜索系统配置）
CREATE TABLE IF NOT EXISTS `search_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` varchar(1000) NOT NULL COMMENT '配置值',
  `config_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '配置类型:1-系统,2-业务,3-算法',
  `description` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_type` (`config_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索配置表';

-- 热门搜索关键词表
CREATE TABLE IF NOT EXISTS `hot_search_keyword` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `search_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '用户数量',
  `category_id` bigint(20) DEFAULT NULL COMMENT '所属分类ID',
  `score` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '热度分数',
  `source` tinyint(4) DEFAULT '1' COMMENT '来源:1-系统统计,2-人工设置',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-显示,0-隐藏',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword` (`keyword`),
  KEY `idx_score_status` (`score` DESC, `status`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门搜索关键词表';

-- 搜索词典表（分词词典）
CREATE TABLE IF NOT EXISTS `search_word_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `word` varchar(100) NOT NULL COMMENT '词条',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '类型:1-普通词,2-停用词,3-同义词,4-品牌词,5-专有名词',
  `weight` decimal(5,2) DEFAULT '1.00' COMMENT '权重',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word_type` (`word`, `type`),
  KEY `idx_type_status` (`type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索词典表';

-- 用户搜索历史表
CREATE TABLE IF NOT EXISTS `user_search_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `keyword` varchar(100) NOT NULL COMMENT '搜索关键词',
  `result_count` int(11) DEFAULT '0' COMMENT '结果数量',
  `search_type` tinyint(4) DEFAULT '1' COMMENT '搜索类型:1-商品,2-内容,3-用户,4-话题',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`, `create_time` DESC),
  KEY `idx_keyword` (`keyword`(32))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户搜索历史表';

-- 推荐配置表（推荐算法配置）
CREATE TABLE IF NOT EXISTS `recommend_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_key` varchar(50) NOT NULL COMMENT '配置键',
  `config_value` text NOT NULL COMMENT '配置值',
  `config_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '配置类型:1-算法,2-规则,3-权重,4-过滤',
  `description` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `scene_type` varchar(50) DEFAULT NULL COMMENT '场景类型',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key_scene` (`config_key`, `scene_type`),
  KEY `idx_scene_type` (`scene_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐配置表';

-- 搜索同义词表
CREATE TABLE IF NOT EXISTS `search_synonym` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `word` varchar(100) NOT NULL COMMENT '词条',
  `synonyms` varchar(500) NOT NULL COMMENT '同义词列表,逗号分隔',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word` (`word`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索同义词表';

-- 搜索纠错表
CREATE TABLE IF NOT EXISTS `search_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `error_word` varchar(100) NOT NULL COMMENT '错误词',
  `correct_word` varchar(100) NOT NULL COMMENT '正确词',
  `frequency` int(11) DEFAULT '0' COMMENT '出现频率',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_error_word` (`error_word`),
  KEY `idx_frequency` (`frequency` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索纠错表';

-- 用户兴趣标签表（简化版，用于搜索推荐）
CREATE TABLE IF NOT EXISTS `user_interest_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_type` tinyint(4) NOT NULL COMMENT '标签类型:1-品类,2-品牌,3-功能,4-场景,5-内容',
  `weight` decimal(5,2) NOT NULL DEFAULT '1.00' COMMENT '权重',
  `source` tinyint(4) NOT NULL DEFAULT '1' COMMENT '来源:1-行为,2-主动设置,3-问卷',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
  KEY `idx_tag_name` (`tag_name`),
  KEY `idx_weight` (`weight` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签表';

-- 推荐结果记录表（记录推荐结果）
CREATE TABLE IF NOT EXISTS `recommendation_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `scene_type` varchar(50) NOT NULL COMMENT '场景类型',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-用户,4-话题',
  `object_id` bigint(20) NOT NULL COMMENT '对象ID',
  `algorithm` varchar(50) NOT NULL COMMENT '算法',
  `score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '推荐得分',
  `position` int(11) DEFAULT NULL COMMENT '展示位置',
  `is_click` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否点击',
  `expose_time` datetime DEFAULT NULL COMMENT '曝光时间',
  `click_time` datetime DEFAULT NULL COMMENT '点击时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_scene` (`user_id`, `scene_type`),
  KEY `idx_object` (`object_type`, `object_id`),
  KEY `idx_expose_time` (`expose_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐结果记录表';

-- 搜索分类表（搜索页面分类）
CREATE TABLE IF NOT EXISTS `search_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-显示,0-隐藏',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '层级',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_level` (`parent_id`, `level`),
  KEY `idx_sort_status` (`sort_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索分类表'; 