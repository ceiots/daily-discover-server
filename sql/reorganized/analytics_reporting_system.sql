-- 创建分析报表系统数据库
CREATE DATABASE IF NOT EXISTS analytics_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用分析报表系统数据库
USE analytics_db;

-- 分析报表系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: report_system_mvp.sql, 部分 user_behavior_statistics 表
-- 高并发优化策略: 1. 按时间范围分区 2. 冷热数据分离 3. 数据定期归档

-- 销售统计表（平台级）
CREATE TABLE IF NOT EXISTS `sales_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_sales` decimal(14,2) NOT NULL DEFAULT '0.00' COMMENT '总销售额',
  `total_orders` int(11) NOT NULL DEFAULT '0' COMMENT '总订单数',
  `paid_orders` int(11) NOT NULL DEFAULT '0' COMMENT '已支付订单数',
  `paid_amount` decimal(14,2) NOT NULL DEFAULT '0.00' COMMENT '已支付金额',
  `refund_orders` int(11) NOT NULL DEFAULT '0' COMMENT '退款订单数',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `new_users` int(11) NOT NULL DEFAULT '0' COMMENT '新增用户数',
  `active_users` int(11) NOT NULL DEFAULT '0' COMMENT '活跃用户数',
  `order_conversion_rate` decimal(5,2) DEFAULT NULL COMMENT '下单转化率(%)',
  `average_order_value` decimal(10,2) DEFAULT NULL COMMENT '平均订单价值',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`),
  KEY `idx_total_sales` (`total_sales`),
  KEY `idx_paid_orders` (`paid_orders`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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

-- 店铺销售统计表
CREATE TABLE IF NOT EXISTS `shop_sales_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `total_sales` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '总销售额',
  `total_orders` int(11) NOT NULL DEFAULT '0' COMMENT '总订单数',
  `paid_orders` int(11) NOT NULL DEFAULT '0' COMMENT '已支付订单数',
  `paid_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '已支付金额',
  `refund_orders` int(11) NOT NULL DEFAULT '0' COMMENT '退款订单数',
  `refund_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `visitor_count` int(11) NOT NULL DEFAULT '0' COMMENT '访客数',
  `payment_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '付款人数',
  `conversion_rate` decimal(5,2) DEFAULT NULL COMMENT '转化率(%)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_date` (`shop_id`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_total_sales` (`total_sales`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺销售统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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

-- 商品销售统计表
CREATE TABLE IF NOT EXISTS `product_sales_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `pv` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `uv` int(11) NOT NULL DEFAULT '0' COMMENT '访客数',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单次数',
  `order_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单人数',
  `order_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '下单件数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '下单金额',
  `paid_count` int(11) NOT NULL DEFAULT '0' COMMENT '支付次数',
  `paid_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '支付人数',
  `paid_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '支付件数',
  `paid_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '支付金额',
  `conversion_rate` decimal(5,2) DEFAULT NULL COMMENT '转化率(%)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_date` (`product_id`,`stat_date`),
  KEY `idx_shop_date` (`shop_id`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_paid_amount` (`paid_amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品销售统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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

-- 用户行为统计表
CREATE TABLE IF NOT EXISTS `user_behavior_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `behavior_type` tinyint(4) NOT NULL COMMENT '行为类型:1-浏览,2-点赞,3-收藏,4-评论,5-分享,6-购买,7-搜索',
  `object_type` tinyint(4) NOT NULL COMMENT '对象类型:1-内容,2-商品,3-用户,4-话题,5-全部',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '用户数',
  `action_count` int(11) NOT NULL DEFAULT '0' COMMENT '行为次数',
  `new_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '新用户数',
  `user_retention_rate` decimal(5,2) DEFAULT NULL COMMENT '用户留存率(%)',
  `avg_duration` int(11) DEFAULT NULL COMMENT '平均停留时长(秒)',
  `bounce_rate` decimal(5,2) DEFAULT NULL COMMENT '跳出率(%)',
  `peak_hour` tinyint(4) DEFAULT NULL COMMENT '高峰小时',
  `device_distribution` json DEFAULT NULL COMMENT '设备分布',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_behavior_object` (`stat_date`,`behavior_type`,`object_type`),
  KEY `idx_behavior_type` (`behavior_type`),
  KEY `idx_object_type` (`object_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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

-- 用户行为每日统计表（按天聚合的统计数据）
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为每日统计表'
PARTITION BY HASH(user_id) PARTITIONS 8;

-- 搜索关键词统计表
CREATE TABLE IF NOT EXISTS `search_keyword_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `keyword` varchar(100) NOT NULL COMMENT '关键词',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `search_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '搜索用户数',
  `click_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击次数',
  `click_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击用户数',
  `conversion_count` int(11) NOT NULL DEFAULT '0' COMMENT '转化次数',
  `conversion_rate` decimal(5,2) DEFAULT NULL COMMENT '转化率(%)',
  `avg_position` decimal(5,2) DEFAULT NULL COMMENT '平均排名',
  `ctr` decimal(5,2) DEFAULT NULL COMMENT '点击率(%)',
  `no_result_count` int(11) NOT NULL DEFAULT '0' COMMENT '无结果次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword_date` (`keyword`,`stat_date`),
  KEY `idx_search_count` (`search_count`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索关键词统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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

-- 营销活动统计表
CREATE TABLE IF NOT EXISTS `marketing_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `campaign_id` bigint(20) NOT NULL COMMENT '活动ID',
  `campaign_name` varchar(100) DEFAULT NULL COMMENT '活动名称',
  `campaign_type` tinyint(4) NOT NULL COMMENT '活动类型:1-满减,2-折扣,3-秒杀,4-拼团,5-优惠券',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `pv` int(11) NOT NULL DEFAULT '0' COMMENT '浏览量',
  `uv` int(11) NOT NULL DEFAULT '0' COMMENT '访客数',
  `click_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击次数',
  `click_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击人数',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单数',
  `order_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单人数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
  `paid_count` int(11) NOT NULL DEFAULT '0' COMMENT '支付订单数',
  `paid_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '支付金额',
  `conversion_rate` decimal(5,2) DEFAULT NULL COMMENT '转化率(%)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_campaign_date` (`campaign_id`,`stat_date`),
  KEY `idx_campaign_type` (`campaign_type`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_paid_amount` (`paid_amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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

-- 会员统计表
CREATE TABLE IF NOT EXISTS `member_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `member_level` int(11) NOT NULL COMMENT '会员等级',
  `member_count` int(11) NOT NULL DEFAULT '0' COMMENT '会员数',
  `new_member_count` int(11) NOT NULL DEFAULT '0' COMMENT '新增会员数',
  `active_member_count` int(11) NOT NULL DEFAULT '0' COMMENT '活跃会员数',
  `order_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单次数',
  `order_member_count` int(11) NOT NULL DEFAULT '0' COMMENT '下单会员数',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '下单金额',
  `paid_count` int(11) NOT NULL DEFAULT '0' COMMENT '支付次数',
  `paid_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '支付金额',
  `avg_order_amount` decimal(10,2) DEFAULT NULL COMMENT '平均订单金额',
  `points_total` bigint(20) NOT NULL DEFAULT '0' COMMENT '总积分',
  `points_used` bigint(20) NOT NULL DEFAULT '0' COMMENT '已使用积分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_date` (`member_level`,`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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

-- 平台监控表
CREATE TABLE IF NOT EXISTS `platform_monitor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `monitor_time` datetime NOT NULL COMMENT '监控时间',
  `api_request_count` int(11) NOT NULL DEFAULT '0' COMMENT 'API请求次数',
  `api_error_count` int(11) NOT NULL DEFAULT '0' COMMENT 'API错误次数',
  `api_avg_response_time` int(11) DEFAULT NULL COMMENT 'API平均响应时间(ms)',
  `page_view_count` int(11) NOT NULL DEFAULT '0' COMMENT '页面浏览次数',
  `unique_visitor_count` int(11) NOT NULL DEFAULT '0' COMMENT '独立访客数',
  `server_cpu_usage` decimal(5,2) DEFAULT NULL COMMENT 'CPU使用率(%)',
  `server_memory_usage` decimal(5,2) DEFAULT NULL COMMENT '内存使用率(%)',
  `server_disk_usage` decimal(5,2) DEFAULT NULL COMMENT '磁盘使用率(%)',
  `db_connection_count` int(11) DEFAULT NULL COMMENT '数据库连接数',
  `db_query_count` int(11) DEFAULT NULL COMMENT '数据库查询次数',
  `cache_hit_rate` decimal(5,2) DEFAULT NULL COMMENT '缓存命中率(%)',
  `concurrent_user_count` int(11) DEFAULT NULL COMMENT '并发用户数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_monitor_time` (`monitor_time`),
  KEY `idx_api_error_count` (`api_error_count`),
  KEY `idx_api_avg_response_time` (`api_avg_response_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台监控表'
PARTITION BY RANGE (TO_DAYS(monitor_time)) (
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

-- 用户画像标签表
CREATE TABLE IF NOT EXISTS `user_profile_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_category` varchar(50) NOT NULL COMMENT '标签分类',
  `tag_level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '标签层级',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父标签ID',
  `tag_rule` text DEFAULT NULL COMMENT '标签规则',
  `user_count` int(11) NOT NULL DEFAULT '0' COMMENT '用户数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_category` (`tag_name`,`tag_category`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_user_count` (`user_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户画像标签表';

-- 推荐统计表
CREATE TABLE IF NOT EXISTS `recommendation_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `scene_type` tinyint(4) NOT NULL COMMENT '场景类型:1-首页,2-详情页,3-搜索结果,4-个人中心',
  `algorithm_type` varchar(50) NOT NULL COMMENT '算法类型',
  `exposure_count` int(11) NOT NULL DEFAULT '0' COMMENT '曝光次数',
  `exposure_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '曝光用户数',
  `click_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击次数',
  `click_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '点击用户数',
  `conversion_count` int(11) NOT NULL DEFAULT '0' COMMENT '转化次数',
  `conversion_user_count` int(11) NOT NULL DEFAULT '0' COMMENT '转化用户数',
  `ctr` decimal(5,2) DEFAULT NULL COMMENT '点击率(%)',
  `cvr` decimal(5,2) DEFAULT NULL COMMENT '转化率(%)',
  `avg_score` decimal(5,2) DEFAULT NULL COMMENT '平均得分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_scene_algorithm` (`stat_date`,`scene_type`,`algorithm_type`),
  KEY `idx_ctr` (`ctr`),
  KEY `idx_cvr` (`cvr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='推荐统计表'
PARTITION BY RANGE (TO_DAYS(stat_date)) (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门趋势表'
PARTITION BY RANGE (TO_DAYS(trend_date)) (
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

-- 热门推荐内容表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门推荐内容表'
PARTITION BY RANGE (TO_DAYS(trending_date)) (
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
