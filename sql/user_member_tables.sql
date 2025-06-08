-- 用户主表（用户基本信息）
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `phone` varchar(16) DEFAULT NULL COMMENT '手机号',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `gender` tinyint(4) DEFAULT '0' COMMENT '性别:0-未知,1-男,2-女',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `user_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户类型:1-普通用户,2-商家,3-官方账号',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status_type` (`status`, `user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户主表';

-- 用户扩展信息表（用户附加信息）
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `preferences` json DEFAULT NULL COMMENT '兴趣偏好标签',
  `ai_profile` json DEFAULT NULL COMMENT 'AI用户画像数据',
  `level` tinyint(4) DEFAULT '1' COMMENT '用户等级',
  `points` int(11) DEFAULT '0' COMMENT '积分',
  `register_ip` varchar(64) DEFAULT NULL COMMENT '注册IP',
  `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户扩展信息表';

-- 用户账户表（用户资金账户）
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '余额',
  `freeze_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `points` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
  `total_consume` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '累计消费',
  `account_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户状态:1-正常,0-冻结',
  `pay_password` varchar(128) DEFAULT NULL COMMENT '支付密码',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_account_status` (`account_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户表';

-- 用户账户流水表（账户资金变动记录）
CREATE TABLE IF NOT EXISTS `user_account_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-收入,2-支出,3-冻结,4-解冻',
  `amount` decimal(10,2) NOT NULL COMMENT '变动金额',
  `balance` decimal(10,2) NOT NULL COMMENT '变动后余额',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-充值,2-消费,3-退款,4-提现,5-佣金,6-赠送',
  `relation_id` varchar(64) DEFAULT NULL COMMENT '关联单号',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_type` tinyint(4) DEFAULT '1' COMMENT '操作人类型:1-用户,2-商家,3-系统,4-管理员',
  `operator_note` varchar(255) DEFAULT NULL COMMENT '操作备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type_source` (`type`, `source`),
  KEY `idx_relation_time` (`relation_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户流水表';

-- 会员等级表（会员等级定义）
CREATE TABLE IF NOT EXISTS `member_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '等级ID',
  `level_name` varchar(32) NOT NULL COMMENT '等级名称',
  `level` int(11) NOT NULL COMMENT '等级数值',
  `upgrade_points` int(11) NOT NULL COMMENT '升级所需积分',
  `upgrade_amount` decimal(10,2) NOT NULL COMMENT '升级所需消费金额',
  `discount_rate` decimal(3,2) NOT NULL DEFAULT '1.00' COMMENT '折扣率',
  `description` varchar(255) DEFAULT NULL COMMENT '等级描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '等级图标',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- 用户会员信息表（用户会员数据）
CREATE TABLE IF NOT EXISTS `user_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `level_id` bigint(20) NOT NULL COMMENT '等级ID',
  `current_points` int(11) NOT NULL DEFAULT '0' COMMENT '当前积分',
  `total_points` int(11) NOT NULL DEFAULT '0' COMMENT '累计积分',
  `points_expire_time` datetime DEFAULT NULL COMMENT '积分过期时间',
  `current_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '当前消费金额',
  `upgrade_progress` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '升级进度(百分比)',
  `become_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '成为会员时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-暂停',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_level_status` (`level_id`, `status`),
  KEY `idx_points_time` (`current_points`, `become_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员信息表';

-- 用户积分记录表（积分变动记录）
CREATE TABLE IF NOT EXISTS `user_points_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-增加,2-减少,3-过期',
  `points` int(11) NOT NULL COMMENT '积分变动',
  `balance` int(11) NOT NULL COMMENT '变动后积分',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-购物,2-评价,3-签到,4-活动,5-调整,6-兑换',
  `relation_id` varchar(64) DEFAULT NULL COMMENT '关联单号',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type_source` (`type`, `source`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_relation_time` (`relation_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分记录表';

-- 用户授权表（第三方账号关联）
CREATE TABLE IF NOT EXISTS `user_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '授权ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `identity_type` varchar(20) NOT NULL COMMENT '认证类型:wechat,alipay,apple,google,facebook等',
  `identifier` varchar(128) NOT NULL COMMENT '第三方账号',
  `credential` varchar(255) DEFAULT NULL COMMENT '第三方凭证',
  `expires_at` datetime DEFAULT NULL COMMENT '凭证过期时间',
  `refresh_token` varchar(255) DEFAULT NULL COMMENT '刷新凭证',
  `verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否验证:0-未验证,1-已验证',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_identifier` (`identity_type`,`identifier`),
  KEY `idx_user_type` (`user_id`, `identity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户授权表';

-- 用户收藏表（用户收藏记录）
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-商品,2-店铺,3-内容',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type_target` (`user_id`,`type`,`target_id`),
  KEY `idx_type_target` (`type`, `target_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户浏览历史表（浏览记录）
CREATE TABLE IF NOT EXISTS `user_browse_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '历史ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-商品,2-店铺,3-内容',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `browse_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type_target` (`user_id`,`type`,`target_id`),
  KEY `idx_type_target` (`type`, `target_id`),
  KEY `idx_browse_time` (`browse_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览历史表';