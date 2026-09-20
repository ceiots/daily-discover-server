-- 创建审核系统数据库
CREATE DATABASE IF NOT EXISTS audit_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用审核系统数据库
USE audit_db;

-- 审核系统表结构
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 高并发优化策略: 
-- 1. 表分区：按审核状态列表分区、按时间范围分区
-- 2. 避免外键约束：通过业务逻辑保证数据一致性
-- 3. 索引优化：核心字段索引、组合索引、状态时间复合索引
-- 4. 冷热数据分离：通过时间范围分区实现

-- 审核任务表（记录所有需要审核的任务）
CREATE TABLE IF NOT EXISTS `audit_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '审核任务ID',
  `task_no` varchar(32) NOT NULL COMMENT '任务编号',
  `audit_type` tinyint(4) NOT NULL COMMENT '审核类型:1-商品,2-店铺,3-内容,4-评论,5-用户资料,6-广告',
  `object_id` bigint(20) NOT NULL COMMENT '审核对象ID',
  `object_type` varchar(50) NOT NULL COMMENT '对象类型',
  `submit_user_id` bigint(20) NOT NULL COMMENT '提交用户ID',
  `submit_user_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '提交用户类型:1-普通用户,2-商家,3-管理员',
  `priority` tinyint(4) NOT NULL DEFAULT '5' COMMENT '优先级:1-10,10为最高',
  `audit_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '审核状态:0-待审核,1-审核通过,2-审核拒绝,3-需人工复核',
  `auto_audit_status` tinyint(4) DEFAULT NULL COMMENT '自动审核状态:0-待审核,1-通过,2-拒绝,3-需人工复核',
  `manual_audit_status` tinyint(4) DEFAULT NULL COMMENT '人工审核状态:0-待审核,1-通过,2-拒绝',
  `auditor_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `submit_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_no` (`task_no`),
  KEY `idx_object` (`object_id`, `object_type`),
  KEY `idx_submit_user` (`submit_user_id`, `submit_user_type`),
  KEY `idx_audit_status` (`audit_status`),
  KEY `idx_priority_submit` (`priority`, `submit_time`),
  KEY `idx_auditor_id` (`auditor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核任务表'
PARTITION BY LIST(audit_status) (
  PARTITION p_pending VALUES IN (0),
  PARTITION p_approved VALUES IN (1),
  PARTITION p_rejected VALUES IN (2),
  PARTITION p_manual VALUES IN (3)
);

-- 审核详情表（记录审核的详细信息）
CREATE TABLE IF NOT EXISTS `audit_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '审核详情ID',
  `task_id` bigint(20) NOT NULL COMMENT '审核任务ID',
  `task_no` varchar(32) NOT NULL COMMENT '任务编号',
  `object_snapshot` text DEFAULT NULL COMMENT '对象快照JSON',
  `audit_items` json DEFAULT NULL COMMENT '审核项JSON',
  `audit_result` json DEFAULT NULL COMMENT '审核结果JSON',
  `reject_reasons` json DEFAULT NULL COMMENT '拒绝原因JSON',
  `ai_suggestion` varchar(50) DEFAULT NULL COMMENT 'AI建议',
  `ai_score` decimal(5,2) DEFAULT NULL COMMENT 'AI评分',
  `risk_level` tinyint(4) DEFAULT NULL COMMENT '风险等级:1-低,2-中,3-高',
  `audit_remark` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `evidence_images` varchar(1000) DEFAULT NULL COMMENT '证据图片,JSON数组',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`),
  KEY `idx_task_no` (`task_no`),
  KEY `idx_risk_level` (`risk_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核详情表'
PARTITION BY HASH(task_id) PARTITIONS 8;

-- 审核日志表（记录审核过程中的操作日志）
CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `task_id` bigint(20) NOT NULL COMMENT '审核任务ID',
  `task_no` varchar(32) NOT NULL COMMENT '任务编号',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人名称',
  `operator_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '操作人类型:1-系统,2-管理员,3-AI',
  `operation_type` tinyint(4) NOT NULL COMMENT '操作类型:1-提交审核,2-自动审核,3-人工审核,4-修改审核,5-加急,6-撤回',
  `status_before` tinyint(4) DEFAULT NULL COMMENT '操作前状态',
  `status_after` tinyint(4) DEFAULT NULL COMMENT '操作后状态',
  `operation_result` tinyint(4) DEFAULT NULL COMMENT '操作结果:1-成功,2-失败',
  `operation_note` varchar(255) DEFAULT NULL COMMENT '操作备注',
  `operation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_task_no` (`task_no`),
  KEY `idx_operator` (`operator_id`, `operator_type`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核日志表'
PARTITION BY RANGE (TO_DAYS(operation_time)) (
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

-- 审核规则表（配置不同类型审核的规则）
CREATE TABLE IF NOT EXISTS `audit_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_code` varchar(50) NOT NULL COMMENT '规则编码',
  `audit_type` tinyint(4) NOT NULL COMMENT '审核类型:1-商品,2-店铺,3-内容,4-评论,5-用户资料,6-广告',
  `rule_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '规则类型:1-文本,2-图片,3-视频,4-组合',
  `rule_config` json NOT NULL COMMENT '规则配置JSON',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `is_auto` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否自动:0-否,1-是',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`),
  KEY `idx_audit_type` (`audit_type`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核规则表';

-- 审核人员表（记录审核人员信息）
CREATE TABLE IF NOT EXISTS `audit_staff` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '审核员ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `staff_name` varchar(50) NOT NULL COMMENT '审核员姓名',
  `staff_code` varchar(50) DEFAULT NULL COMMENT '审核员编码',
  `audit_types` json NOT NULL COMMENT '可审核类型JSON数组',
  `audit_level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '审核级别:1-初级,2-中级,3-高级',
  `daily_capacity` int(11) DEFAULT NULL COMMENT '日审核容量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `work_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '工作状态:0-空闲,1-繁忙,2-离线',
  `last_audit_time` datetime DEFAULT NULL COMMENT '最后审核时间',
  `performance_score` decimal(5,2) DEFAULT NULL COMMENT '绩效评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_staff_code` (`staff_code`),
  KEY `idx_status` (`status`),
  KEY `idx_work_status` (`work_status`),
  KEY `idx_audit_level` (`audit_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核人员表';

-- 审核统计表（记录审核统计数据）
CREATE TABLE IF NOT EXISTS `audit_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `audit_type` tinyint(4) NOT NULL COMMENT '审核类型:1-商品,2-店铺,3-内容,4-评论,5-用户资料,6-广告',
  `total_count` int(11) NOT NULL DEFAULT '0' COMMENT '总审核数',
  `pending_count` int(11) NOT NULL DEFAULT '0' COMMENT '待审核数',
  `approved_count` int(11) NOT NULL DEFAULT '0' COMMENT '通过数',
  `rejected_count` int(11) NOT NULL DEFAULT '0' COMMENT '拒绝数',
  `manual_count` int(11) NOT NULL DEFAULT '0' COMMENT '人工审核数',
  `auto_count` int(11) NOT NULL DEFAULT '0' COMMENT '自动审核数',
  `avg_audit_time` int(11) DEFAULT NULL COMMENT '平均审核时间(秒)',
  `max_audit_time` int(11) DEFAULT NULL COMMENT '最长审核时间(秒)',
  `min_audit_time` int(11) DEFAULT NULL COMMENT '最短审核时间(秒)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_type` (`stat_date`,`audit_type`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_audit_type` (`audit_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核统计表'
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

-- 审核队列表（记录待审核任务队列）
CREATE TABLE IF NOT EXISTS `audit_queue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` bigint(20) NOT NULL COMMENT '审核任务ID',
  `task_no` varchar(32) NOT NULL COMMENT '任务编号',
  `audit_type` tinyint(4) NOT NULL COMMENT '审核类型:1-商品,2-店铺,3-内容,4-评论,5-用户资料,6-广告',
  `priority` tinyint(4) NOT NULL DEFAULT '5' COMMENT '优先级:1-10,10为最高',
  `queue_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '队列状态:0-等待中,1-处理中,2-已完成,3-已取消',
  `assigned_staff_id` bigint(20) DEFAULT NULL COMMENT '分配审核员ID',
  `enqueue_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入队时间',
  `assign_time` datetime DEFAULT NULL COMMENT '分配时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `timeout_time` datetime DEFAULT NULL COMMENT '超时时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`),
  KEY `idx_task_no` (`task_no`),
  KEY `idx_queue_status` (`queue_status`),
  KEY `idx_priority_enqueue` (`priority`,`enqueue_time`),
  KEY `idx_assigned_staff_id` (`assigned_staff_id`),
  KEY `idx_timeout_time` (`timeout_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核队列表'
PARTITION BY LIST(queue_status) (
  PARTITION p_waiting VALUES IN (0),
  PARTITION p_processing VALUES IN (1),
  PARTITION p_completed VALUES IN (2),
  PARTITION p_cancelled VALUES IN (3)
);

-- 敏感词表（记录系统敏感词）
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
PARTITION BY LIST(type) (
  PARTITION p_political VALUES IN (1),
  PARTITION p_porn VALUES IN (2),
  PARTITION p_violence VALUES IN (3),
  PARTITION p_ad VALUES IN (4),
  PARTITION p_other VALUES IN (5)
);

-- 商品审核扩展表（记录商品审核特有信息）
CREATE TABLE IF NOT EXISTS `product_audit_ext` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` bigint(20) NOT NULL COMMENT '审核任务ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `brand_id` bigint(20) DEFAULT NULL COMMENT '品牌ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称',
  `price` decimal(10,2) NOT NULL COMMENT '售价',
  `market_price` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `main_image` varchar(255) DEFAULT NULL COMMENT '商品主图',
  `image_audit_result` json DEFAULT NULL COMMENT '图片审核结果',
  `text_audit_result` json DEFAULT NULL COMMENT '文本审核结果',
  `category_match_score` decimal(5,2) DEFAULT NULL COMMENT '类目匹配度',
  `price_risk_score` decimal(5,2) DEFAULT NULL COMMENT '价格风险度',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品审核扩展表'
PARTITION BY HASH(product_id) PARTITIONS 8;

-- 店铺审核扩展表（记录店铺审核特有信息）
CREATE TABLE IF NOT EXISTS `shop_audit_ext` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` bigint(20) NOT NULL COMMENT '审核任务ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `user_id` bigint(20) NOT NULL COMMENT '店主用户ID',
  `shop_name` varchar(100) NOT NULL COMMENT '店铺名称',
  `shop_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '店铺类型:1-个人,2-企业,3-旗舰,4-专卖',
  `shop_industry` varchar(50) DEFAULT NULL COMMENT '店铺行业',
  `qualification_images` json DEFAULT NULL COMMENT '资质图片',
  `business_license` varchar(255) DEFAULT NULL COMMENT '营业执照',
  `id_card_images` json DEFAULT NULL COMMENT '身份证图片',
  `verify_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '认证状态:0-未认证,1-已认证,2-认证失败',
  `risk_level` tinyint(4) DEFAULT NULL COMMENT '风险等级:1-低,2-中,3-高',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_verify_status` (`verify_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺审核扩展表'
PARTITION BY HASH(shop_id) PARTITIONS 8;

-- 内容审核扩展表（记录内容审核特有信息）
CREATE TABLE IF NOT EXISTS `content_audit_ext` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` bigint(20) NOT NULL COMMENT '审核任务ID',
  `content_id` bigint(20) NOT NULL COMMENT '内容ID',
  `content_type` tinyint(4) NOT NULL COMMENT '内容类型:1-文章,2-视频,3-直播,4-问答,5-短内容',
  `user_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `text_audit_result` json DEFAULT NULL COMMENT '文本审核结果',
  `image_audit_result` json DEFAULT NULL COMMENT '图片审核结果',
  `video_audit_result` json DEFAULT NULL COMMENT '视频审核结果',
  `sensitive_word_count` int(11) DEFAULT '0' COMMENT '敏感词数量',
  `adult_score` decimal(5,2) DEFAULT NULL COMMENT '成人内容评分',
  `spam_score` decimal(5,2) DEFAULT NULL COMMENT '垃圾内容评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_id` (`task_id`),
  KEY `idx_content_id` (`content_id`,`content_type`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容审核扩展表'
PARTITION BY HASH(content_id) PARTITIONS 8; 