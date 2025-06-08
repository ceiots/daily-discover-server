-- 搜索配置表
CREATE TABLE IF NOT EXISTS `search_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '配置名称',
  `search_type` tinyint(4) NOT NULL COMMENT '搜索类型:1-商品,2-店铺,3-内容',
  `weight_factors` text NOT NULL COMMENT '权重因子配置,JSON格式',
  `filter_factors` text DEFAULT NULL COMMENT '过滤因子配置,JSON格式',
  `sort_factors` text DEFAULT NULL COMMENT '排序因子配置,JSON格式',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_search_type` (`search_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索配置表';

-- 热搜词表
CREATE TABLE IF NOT EXISTS `hot_search_keyword` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `search_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '搜索类型:1-商品,2-店铺,3-内容',
  `score` int(11) NOT NULL DEFAULT '0' COMMENT '热度分数',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `is_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否推荐:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `start_time` datetime DEFAULT NULL COMMENT '有效期开始',
  `end_time` datetime DEFAULT NULL COMMENT '有效期结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword_type` (`keyword`,`search_type`),
  KEY `idx_score` (`score`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_is_recommend` (`is_recommend`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热搜词表';

-- 搜索词库表
CREATE TABLE IF NOT EXISTS `search_word_dict` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `word` varchar(100) NOT NULL COMMENT '词语',
  `word_type` tinyint(4) NOT NULL COMMENT '词类型:1-品牌,2-属性,3-分类,4-同义词,5-停用词',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT '权重',
  `synonym` varchar(255) DEFAULT NULL COMMENT '同义词,多个逗号分隔',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word_type` (`word`,`word_type`),
  KEY `idx_word_type` (`word_type`),
  KEY `idx_weight` (`weight`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索词库表';

-- 用户搜索历史表
CREATE TABLE IF NOT EXISTS `user_search_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `keyword` varchar(100) NOT NULL COMMENT '搜索关键词',
  `search_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '搜索类型:1-商品,2-店铺,3-内容',
  `search_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
  `search_result_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索结果数',
  `device_info` varchar(128) DEFAULT NULL COMMENT '设备信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_search_type` (`search_type`),
  KEY `idx_search_time` (`search_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户搜索历史表';

-- 搜索推荐配置表
CREATE TABLE IF NOT EXISTS `search_recommend_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '配置名称',
  `search_type` tinyint(4) NOT NULL COMMENT '搜索类型:1-商品,2-店铺,3-内容',
  `scene` varchar(50) NOT NULL COMMENT '场景',
  `strategy` text NOT NULL COMMENT '推荐策略,JSON格式',
  `rule_config` text DEFAULT NULL COMMENT '规则配置,JSON格式',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_search_type` (`search_type`),
  KEY `idx_scene` (`scene`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索推荐配置表';

-- 内容推荐配置表
CREATE TABLE IF NOT EXISTS `content_recommend_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '配置名称',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-图集,4-问答',
  `scene` varchar(50) NOT NULL COMMENT '场景',
  `strategy` text NOT NULL COMMENT '推荐策略,JSON格式',
  `rule_config` text DEFAULT NULL COMMENT '规则配置,JSON格式',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_scene` (`scene`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容推荐配置表';

-- 商品推荐配置表
CREATE TABLE IF NOT EXISTS `product_recommend_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) NOT NULL COMMENT '配置名称',
  `scene` varchar(50) NOT NULL COMMENT '场景',
  `strategy` text NOT NULL COMMENT '推荐策略,JSON格式',
  `rule_config` text DEFAULT NULL COMMENT '规则配置,JSON格式',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_scene` (`scene`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品推荐配置表';

-- 搜索同义词表
CREATE TABLE IF NOT EXISTS `search_synonym` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `word` varchar(100) NOT NULL COMMENT '词语',
  `synonyms` varchar(500) NOT NULL COMMENT '同义词,多个逗号分隔',
  `is_two_way` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否双向:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word` (`word`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索同义词表';

-- 搜索纠错表
CREATE TABLE IF NOT EXISTS `search_correction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `wrong_word` varchar(100) NOT NULL COMMENT '错误词',
  `correct_word` varchar(100) NOT NULL COMMENT '正确词',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wrong_word` (`wrong_word`),
  KEY `idx_correct_word` (`correct_word`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索纠错表';

-- 用户兴趣标签表
CREATE TABLE IF NOT EXISTS `user_interest_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tag_type` tinyint(4) NOT NULL COMMENT '标签类型:1-品类,2-品牌,3-属性,4-价格区间,5-场景',
  `tag_id` bigint(20) DEFAULT NULL COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `score` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '兴趣分数',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-浏览,2-收藏,3-加购,4-购买,5-评价,6-自定义',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type_tag` (`user_id`,`tag_type`,`tag_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_type` (`tag_type`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_score` (`score`),
  KEY `idx_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签表'; 