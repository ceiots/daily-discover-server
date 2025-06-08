-- 用户会员系统表结构（MVP版本）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 保留核心功能，去除非必要字段，提高查询性能

-- 用户基础表（核心用户信息）
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) DEFAULT NULL COMMENT '密码',
  `salt` varchar(32) DEFAULT NULL COMMENT '密码盐',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `mobile` varchar(16) DEFAULT NULL COMMENT '手机号',
  `email` varchar(64) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `gender` tinyint(4) DEFAULT '0' COMMENT '性别:0-未知,1-男,2-女',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-正常',
  `user_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户类型:1-普通用户,2-商家,3-管理员',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `register_ip` varchar(64) DEFAULT NULL COMMENT '注册IP',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_mobile` (`mobile`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_register_time` (`register_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基础表';

-- 用户扩展信息表（用户详细信息）
CREATE TABLE IF NOT EXISTS `user_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `province` varchar(32) DEFAULT NULL COMMENT '省份',
  `city` varchar(32) DEFAULT NULL COMMENT '城市',
  `district` varchar(32) DEFAULT NULL COMMENT '区县',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `education` varchar(32) DEFAULT NULL COMMENT '学历',
  `profession` varchar(32) DEFAULT NULL COMMENT '职业',
  `bio` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `interests` varchar(255) DEFAULT NULL COMMENT '兴趣爱好',
  `identity_card` varchar(32) DEFAULT NULL COMMENT '身份证号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_real_name` (`real_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户扩展信息表';

-- 用户账户表（用户资金账户）
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  `freeze_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `points` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
  `total_points` int(11) NOT NULL DEFAULT '0' COMMENT '累计积分',
  `total_consume` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '累计消费',
  `account_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户状态:0-冻结,1-正常',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_account_status` (`account_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户表';

-- 用户账户流水表（账户变动记录）
CREATE TABLE IF NOT EXISTS `user_account_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-收入,2-支出',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `balance` decimal(10,2) NOT NULL COMMENT '变动后余额',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-充值,2-提现,3-消费,4-退款,5-佣金,6-活动奖励',
  `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID',
  `biz_type` varchar(32) DEFAULT NULL COMMENT '业务类型',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_biz_id` (`biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户流水表';

-- 会员等级表（会员等级定义）
CREATE TABLE IF NOT EXISTS `member_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `level` int(11) NOT NULL COMMENT '等级',
  `name` varchar(32) NOT NULL COMMENT '等级名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '等级图标',
  `min_points` int(11) NOT NULL DEFAULT '0' COMMENT '最低积分',
  `max_points` int(11) DEFAULT NULL COMMENT '最高积分',
  `discount` decimal(3,2) DEFAULT '1.00' COMMENT '折扣率',
  `description` varchar(255) DEFAULT NULL COMMENT '等级描述',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

-- 用户会员表（用户会员信息）
CREATE TABLE IF NOT EXISTS `user_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `level_id` bigint(20) NOT NULL COMMENT '等级ID',
  `level` int(11) NOT NULL COMMENT '等级',
  `growth_value` int(11) NOT NULL DEFAULT '0' COMMENT '成长值',
  `upgrade_need_growth` int(11) DEFAULT NULL COMMENT '升级还需成长值',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
  `is_permanent` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否永久:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_level` (`level`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会员表';

-- 用户积分日志表（积分变动记录）
CREATE TABLE IF NOT EXISTS `user_points_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `points` int(11) NOT NULL COMMENT '积分变动值',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-增加,2-减少',
  `balance` int(11) NOT NULL COMMENT '变动后积分',
  `source` tinyint(4) NOT NULL COMMENT '来源:1-注册,2-登录,3-购物,4-评价,5-活动,6-签到',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户积分日志表';

-- 用户认证表（第三方认证信息）
CREATE TABLE IF NOT EXISTS `user_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `identity_type` varchar(32) NOT NULL COMMENT '认证类型:mobile,email,weixin,qq,weibo,apple',
  `identifier` varchar(128) NOT NULL COMMENT '认证标识',
  `credential` varchar(128) DEFAULT NULL COMMENT '凭证',
  `verified` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否验证:0-未验证,1-已验证',
  `bind_time` datetime DEFAULT NULL COMMENT '绑定时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_identity` (`identity_type`,`identifier`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认证表';

-- 用户收藏表（用户收藏商品/内容）
CREATE TABLE IF NOT EXISTS `user_favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_id` bigint(20) NOT NULL COMMENT '目标ID',
  `target_type` tinyint(4) NOT NULL COMMENT '目标类型:1-商品,2-内容,3-店铺',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`target_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户浏览历史表（商品浏览记录）
CREATE TABLE IF NOT EXISTS `user_browse_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) DEFAULT NULL COMMENT '商品名称',
  `product_image` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `product_price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `browse_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`browse_time`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览历史表';

-- 用户登录日志表（登录记录）
CREATE TABLE IF NOT EXISTS `user_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  `login_ip` varchar(64) DEFAULT NULL COMMENT '登录IP',
  `login_type` tinyint(4) DEFAULT '1' COMMENT '登录类型:1-账号密码,2-手机验证码,3-第三方',
  `device_type` tinyint(4) DEFAULT NULL COMMENT '设备类型:1-PC,2-Android,3-iOS,4-小程序',
  `device_id` varchar(128) DEFAULT NULL COMMENT '设备ID',
  `os_version` varchar(32) DEFAULT NULL COMMENT '系统版本',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-失败,1-成功',
  `city` varchar(32) DEFAULT NULL COMMENT '城市',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_login_time` (`login_time`),
  KEY `idx_login_ip` (`login_ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录日志表'; 