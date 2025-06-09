-- 用户会员体系表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: user_member_mvp.sql, discovery_app_mvp.sql中的用户相关表
-- 高并发优化策略: 
-- 1. 表分区: 用户表按用户ID哈希分区，用户行为表按行为时间范围分区，会员表按状态列表分区
-- 2. 避免外键约束: 使用应用层维护数据一致性
-- 3. 索引优化: 为高频查询字段创建索引
-- 4. 冷热数据分离: 使用分区策略将活跃用户和不活跃用户数据分开

-- 用户基础表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `salt` varchar(32) DEFAULT NULL COMMENT '加密盐',
  `mobile` varchar(16) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `gender` tinyint(4) DEFAULT '0' COMMENT '性别:0-未知,1-男,2-女',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常,2-锁定',
  `user_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户类型:1-普通用户,2-商家,3-官方账号',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `register_ip` varchar(64) DEFAULT NULL COMMENT '注册IP',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_mobile` (`mobile`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础表'
PARTITION BY HASH(id) PARTITIONS 16;

-- 用户详细信息表
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `id_card_type` tinyint(4) DEFAULT NULL COMMENT '证件类型:1-身份证,2-护照,3-军官证',
  `id_card_no` varchar(32) DEFAULT NULL COMMENT '证件号码',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `education` tinyint(4) DEFAULT NULL COMMENT '学历:1-小学,2-初中,3-高中,4-大专,5-本科,6-硕士,7-博士',
  `profession` varchar(50) DEFAULT NULL COMMENT '职业',
  `income_level` tinyint(4) DEFAULT NULL COMMENT '收入水平:1-低,2-中,3-高',
  `bio` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `province` varchar(32) DEFAULT NULL COMMENT '所在省',
  `city` varchar(32) DEFAULT NULL COMMENT '所在市',
  `district` varchar(32) DEFAULT NULL COMMENT '所在区',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_real_name` (`real_name`),
  KEY `idx_id_card` (`id_card_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户详细信息表';

-- 用户账户表
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `balance` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  `freeze_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `points` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
  `growth_value` int(11) NOT NULL DEFAULT '0' COMMENT '成长值',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-冻结,1-正常',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_points` (`points`),
  KEY `idx_growth` (`growth_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 用户账户流水表
CREATE TABLE IF NOT EXISTS `user_account_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-收入,2-支出,3-冻结,4-解冻,5-积分,6-成长值',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '变动金额',
  `points` int(11) DEFAULT NULL COMMENT '变动积分',
  `growth` int(11) DEFAULT NULL COMMENT '变动成长值',
  `before_amount` decimal(12,2) DEFAULT NULL COMMENT '变动前金额',
  `after_amount` decimal(12,2) DEFAULT NULL COMMENT '变动后金额',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-订单,2-退款,3-充值,4-提现,5-系统调整,6-签到,7-活动',
  `source_id` varchar(64) DEFAULT NULL COMMENT '来源ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type_source` (`type`, `source`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户流水表'
PARTITION BY RANGE (TO_DAYS(create_time)) (
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

-- 会员等级表
CREATE TABLE IF NOT EXISTS `member_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `level` int(11) NOT NULL COMMENT '等级',
  `name` varchar(50) NOT NULL COMMENT '等级名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '等级图标',
  `growth_min` int(11) NOT NULL COMMENT '成长值下限',
  `growth_max` int(11) NOT NULL COMMENT '成长值上限',
  `discount` decimal(3,2) DEFAULT NULL COMMENT '折扣',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `benefits` varchar(500) DEFAULT NULL COMMENT '会员权益',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `free_shipping` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否包邮:0-否,1-是',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level` (`level`),
  KEY `idx_growth` (`growth_min`, `growth_max`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- 用户会员表
CREATE TABLE IF NOT EXISTS `user_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `member_level` int(11) NOT NULL DEFAULT '1' COMMENT '会员等级',
  `growth_value` int(11) NOT NULL DEFAULT '0' COMMENT '成长值',
  `points` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
  `used_points` int(11) NOT NULL DEFAULT '0' COMMENT '已使用积分',
  `is_forever` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否永久会员:0-否,1-是',
  `start_time` datetime DEFAULT NULL COMMENT '会员开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '会员结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常',
  `free_shipping_count` int(11) NOT NULL DEFAULT '0' COMMENT '免邮次数',
  `free_return_count` int(11) NOT NULL DEFAULT '0' COMMENT '免退次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_member_level` (`member_level`),
  KEY `idx_status` (`status`),
  KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员表'
PARTITION BY LIST(status) (
  PARTITION p_normal VALUES IN (1),
  PARTITION p_disabled VALUES IN (0)
);

-- 用户积分记录表
CREATE TABLE IF NOT EXISTS `user_points_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-获取,2-消费,3-过期,4-调整',
  `points` int(11) NOT NULL COMMENT '积分数量',
  `before_points` int(11) NOT NULL COMMENT '变动前积分',
  `after_points` int(11) NOT NULL COMMENT '变动后积分',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-订单,2-签到,3-活动,4-邀请,5-系统',
  `source_id` varchar(64) DEFAULT NULL COMMENT '来源ID',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type_source` (`type`, `source`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分记录表'
PARTITION BY HASH(user_id) PARTITIONS 8;

-- 用户授权表
CREATE TABLE IF NOT EXISTS `user_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `identity_type` varchar(20) NOT NULL COMMENT '登录类型:username,mobile,email,weixin,qq,weibo,apple',
  `identifier` varchar(100) NOT NULL COMMENT '标识(手机号/邮箱/用户名/第三方应用的唯一标识)',
  `credential` varchar(100) DEFAULT NULL COMMENT '凭据(密码/第三方token)',
  `verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已验证:0-未验证,1-已验证',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `bind_time` datetime DEFAULT NULL COMMENT '绑定时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_identity` (`user_id`,`identity_type`),
  UNIQUE KEY `uk_identity_identifier` (`identity_type`,`identifier`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户授权表'
PARTITION BY HASH(user_id) PARTITIONS 16;

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '收藏类型:1-商品,2-店铺,3-内容,4-活动',
  `target_id` bigint(20) NOT NULL COMMENT '收藏对象ID',
  `collect_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type_target` (`user_id`,`type`,`target_id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_collect_time` (`collect_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 用户登录日志表
CREATE TABLE IF NOT EXISTS `user_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `login_ip` varchar(64) DEFAULT NULL COMMENT '登录IP',
  `login_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '登录方式:1-账号密码,2-手机验证码,3-第三方登录',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC',
  `device_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
  `device_model` varchar(64) DEFAULT NULL COMMENT '设备型号',
  `os_version` varchar(32) DEFAULT NULL COMMENT '系统版本',
  `app_version` varchar(32) DEFAULT NULL COMMENT 'APP版本',
  `location` varchar(64) DEFAULT NULL COMMENT '位置',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-失败,1-成功',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录日志表';

-- 用户设备表
CREATE TABLE IF NOT EXISTS `user_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `device_id` varchar(64) NOT NULL COMMENT '设备ID',
  `device_type` tinyint(4) NOT NULL COMMENT '设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC',
  `device_model` varchar(64) DEFAULT NULL COMMENT '设备型号',
  `device_name` varchar(64) DEFAULT NULL COMMENT '设备名称',
  `os_version` varchar(32) DEFAULT NULL COMMENT '系统版本',
  `app_version` varchar(32) DEFAULT NULL COMMENT 'APP版本',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `push_token` varchar(128) DEFAULT NULL COMMENT '推送Token',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_device` (`user_id`,`device_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设备表';

-- 用户行为表
CREATE TABLE IF NOT EXISTS `user_behavior` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `behavior_type` tinyint(4) NOT NULL COMMENT '行为类型:1-浏览,2-点赞,3-收藏,4-评论,5-分享,6-购买,7-搜索',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `target_type` tinyint(4) NOT NULL COMMENT '目标类型:1-内容,2-商品,3-用户,4-话题,5-评论',
  `behavior_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '行为时间',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC',
  `device_id` varchar(64) DEFAULT NULL COMMENT '设备ID',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_behavior_type` (`behavior_type`),
  KEY `idx_target` (`target_id`,`target_type`),
  KEY `idx_behavior_time` (`behavior_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表'
PARTITION BY RANGE (TO_DAYS(behavior_time)) (
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

-- 用户关系表
CREATE TABLE IF NOT EXISTS `user_relationship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `related_user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `relation_type` tinyint(4) NOT NULL COMMENT '关系类型:1-好友,2-黑名单,3-特别关注',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-解除,1-有效',
  `remark` varchar(50) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_relation` (`user_id`,`related_user_id`,`relation_type`),
  KEY `idx_related_user_id` (`related_user_id`),
  KEY `idx_relation_type` (`relation_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关系表'; 