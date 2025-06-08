-- 报表系统表结构（MVP版本）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 保留核心功能，去除非必要字段，提高查询性能

-- 销售统计表（总体销售数据）
CREATE TABLE IF NOT EXISTS `sales_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `statistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '统计类型:1-日,2-周,3-月,4-年',
  `total_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '销售总额',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单数',
  `avg_order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '平均订单金额',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单用户数',
  `new_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '新用户下单数',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `refund_count` int(11) NOT NULL DEFAULT '0' COMMENT '退款订单数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_type` (`statistics_date`,`statistics_type`),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售统计表';

-- 店铺销售统计表（按店铺分组）
CREATE TABLE IF NOT EXISTS `shop_sales_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `statistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '统计类型:1-日,2-周,3-月,4-年',
  `total_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '销售总额',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单数',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单用户数',
  `new_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '新用户下单数',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `refund_count` int(11) NOT NULL DEFAULT '0' COMMENT '退款订单数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_date_type` (`shop_id`,`statistics_date`,`statistics_type`),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺销售统计表';

-- 商品销售统计表（按商品分组）
CREATE TABLE IF NOT EXISTS `product_sales_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `statistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '统计类型:1-日,2-周,3-月,4-年',
  `sales_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '销售金额',
  `sales_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '销售数量',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单数',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '购买用户数',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `refund_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '退款数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_date_type` (`product_id`,`statistics_date`,`statistics_type`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品销售统计表';

-- 用户行为统计表（用户行为数据）
CREATE TABLE IF NOT EXISTS `user_behavior_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `statistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '统计类型:1-日,2-周,3-月,4-年',
  `active_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '活跃用户数',
  `new_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '新增用户数',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '总浏览量',
  `view_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览用户数',
  `search_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `search_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索用户数',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单次数',
  `order_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单用户数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_type` (`statistics_date`,`statistics_type`),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为统计表';

-- 搜索关键词统计表
CREATE TABLE IF NOT EXISTS `search_keyword_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `statistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '统计类型:1-日,2-周,3-月',
  `search_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索用户数',
  `click_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击次数',
  `conversion_count` int(11) NOT NULL DEFAULT '0' COMMENT '转化次数',
  `avg_result_count` int(11) NOT NULL DEFAULT '0' COMMENT '平均结果数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword_date_type` (`keyword`(32),`statistics_date`,`statistics_type`),
  KEY `idx_count` (`search_count` DESC),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索关键词统计表';

-- 营销活动统计表
CREATE TABLE IF NOT EXISTS `marketing_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `campaign_id` bigint(20) NOT NULL COMMENT '活动ID',
  `campaign_name` varchar(100) NOT NULL COMMENT '活动名称',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `statistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '统计类型:1-日,2-活动周期',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '参与用户数',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
  `item_count` int(11) NOT NULL DEFAULT '0' COMMENT '商品件数',
  `coupon_count` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券领取数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_campaign_date_type` (`campaign_id`,`statistics_date`,`statistics_type`),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动统计表';

-- 会员统计表
CREATE TABLE IF NOT EXISTS `member_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `statistics_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '统计类型:1-日,2-周,3-月,4-年',
  `member_level` int(11) NOT NULL COMMENT '会员等级',
  `member_count` int(11) NOT NULL DEFAULT '0' COMMENT '会员数量',
  `new_member_count` int(11) NOT NULL DEFAULT '0' COMMENT '新增会员数',
  `active_member_count` int(11) NOT NULL DEFAULT '0' COMMENT '活跃会员数',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单次数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '下单金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_date_type` (`member_level`,`statistics_date`,`statistics_type`),
  KEY `idx_statistics_date` (`statistics_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员统计表';

-- 平台监控表（平台关键指标监控）
CREATE TABLE IF NOT EXISTS `platform_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `monitor_time` datetime NOT NULL COMMENT '监控时间',
  `monitor_type` tinyint(4) NOT NULL COMMENT '监控类型:1-分钟,2-小时,3-天',
  `api_qps` int(11) NOT NULL DEFAULT '0' COMMENT 'API请求QPS',
  `api_success_rate` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT 'API成功率',
  `api_avg_response_time` int(11) NOT NULL DEFAULT '0' COMMENT 'API平均响应时间(ms)',
  `db_connection_count` int(11) NOT NULL DEFAULT '0' COMMENT '数据库连接数',
  `db_qps` int(11) NOT NULL DEFAULT '0' COMMENT '数据库QPS',
  `cache_hit_rate` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '缓存命中率',
  `server_load` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '服务器负载',
  `memory_usage` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '内存使用率',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_time_type` (`monitor_time`,`monitor_type`),
  KEY `idx_monitor_time` (`monitor_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台监控表';

-- 用户画像标签表（用户画像数据）
CREATE TABLE IF NOT EXISTS `user_profile_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tag_type` varchar(50) NOT NULL COMMENT '标签类型',
  `tag_value` varchar(100) NOT NULL COMMENT '标签值',
  `confidence` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '置信度',
  `source` tinyint(4) NOT NULL DEFAULT '1' COMMENT '来源:1-系统计算,2-用户填写,3-外部导入',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type_value` (`user_id`,`tag_type`,`tag_value`),
  KEY `idx_tag_type_value` (`tag_type`,`tag_value`),
  KEY `idx_confidence` (`confidence` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像标签表';

-- 推荐效果统计表（推荐系统效果监控）
CREATE TABLE IF NOT EXISTS `recommendation_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `statistics_date` date NOT NULL COMMENT '统计日期',
  `scene_type` varchar(50) NOT NULL COMMENT '场景类型',
  `algorithm` varchar(50) NOT NULL COMMENT '算法',
  `exposure_count` int(11) NOT NULL DEFAULT '0' COMMENT '曝光次数',
  `click_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击次数',
  `click_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击用户数',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单次数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '下单金额',
  `ctr` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '点击率',
  `cvr` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '转化率',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_scene_algo` (`statistics_date`,`scene_type`,`algorithm`),
  KEY `idx_ctr` (`ctr` DESC),
  KEY `idx_cvr` (`cvr` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐效果统计表'; 