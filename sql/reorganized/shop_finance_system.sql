-- 店铺财务系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: shop_finance_mvp.sql

-- 店铺账户表 (记录店铺账户的资金相关信息)
CREATE TABLE IF NOT EXISTS `shop_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `account_no` varchar(32) NOT NULL COMMENT '账户编号',
  `account_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户类型:1-结算账户,2-佣金账户,3-保证金账户',
  `balance` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  `frozen_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '冻结金额',
  `settlement_cycle` tinyint(4) NOT NULL DEFAULT '1' COMMENT '结算周期:1-T+1,2-T+7,3-T+15,4-月结',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-冻结,1-正常',
  `last_settled_time` datetime DEFAULT NULL COMMENT '上次结算时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_shop_account_type` (`shop_id`,`account_type`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺账户表';

-- 店铺结算记录表 (记录店铺结算)
CREATE TABLE IF NOT EXISTS `shop_settlement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `settlement_no` varchar(32) NOT NULL COMMENT '结算单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `account_id` bigint(20) NOT NULL COMMENT '店铺账户ID',
  `account_no` varchar(32) NOT NULL COMMENT '账户编号',
  `settlement_start_time` datetime NOT NULL COMMENT '结算开始时间',
  `settlement_end_time` datetime NOT NULL COMMENT '结算结束时间',
  `total_amount` decimal(12,2) NOT NULL COMMENT '结算总金额',
  `order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
  `refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `commission_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '佣金金额',
  `actual_amount` decimal(12,2) NOT NULL COMMENT '实际结算金额',
  `receipt_amount` decimal(12,2) DEFAULT NULL COMMENT '实际到账金额',
  `fee_amount` decimal(10,2) DEFAULT '0.00' COMMENT '手续费金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待结算,1-结算中,2-已结算,3-结算失败',
  `payout_time` datetime DEFAULT NULL COMMENT '打款时间',
  `payout_batch_no` varchar(64) DEFAULT NULL COMMENT '打款批次号',
  `payout_remark` varchar(255) DEFAULT NULL COMMENT '打款备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_settlement_no` (`settlement_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_settlement_time` (`settlement_start_time`,`settlement_end_time`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_payout_time` (`payout_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺结算记录表';

-- 店铺结算明细表 (结算涉及的订单明细)
CREATE TABLE IF NOT EXISTS `shop_settlement_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `settlement_id` bigint(20) NOT NULL COMMENT '结算ID',
  `settlement_no` varchar(32) NOT NULL COMMENT '结算单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单编号',
  `order_amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `refund_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `commission_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '佣金金额',
  `actual_amount` decimal(10,2) NOT NULL COMMENT '结算金额',
  `payment_time` datetime NOT NULL COMMENT '支付时间',
  `is_refund` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否退款:0-否,1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_settlement_id` (`settlement_id`),
  KEY `idx_settlement_no` (`settlement_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_payment_time` (`payment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺结算明细表';

-- 店铺交易流水表 (记录店铺交易流水)
CREATE TABLE IF NOT EXISTS `shop_transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `transaction_no` varchar(32) NOT NULL COMMENT '交易流水号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `account_no` varchar(32) NOT NULL COMMENT '账户编号',
  `account_type` tinyint(4) NOT NULL COMMENT '账户类型:1-结算账户,2-佣金账户,3-保证金账户',
  `transaction_type` tinyint(4) NOT NULL COMMENT '交易类型:1-订单结算,2-退款,3-佣金扣除,4-提现,5-调账,6-充值保证金',
  `amount` decimal(12,2) NOT NULL COMMENT '交易金额',
  `balance` decimal(12,2) NOT NULL COMMENT '交易后余额',
  `direction` tinyint(4) NOT NULL COMMENT '资金方向:1-收入,2-支出',
  `reference_no` varchar(64) DEFAULT NULL COMMENT '关联单号',
  `reference_type` tinyint(4) DEFAULT NULL COMMENT '关联类型:1-订单,2-结算单,3-提现单,4-退款单',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-处理中,1-成功,2-失败',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_transaction_type` (`transaction_type`),
  KEY `idx_reference_no` (`reference_no`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺交易流水表';

-- 店铺提现申请表 (记录店铺提现申请)
CREATE TABLE IF NOT EXISTS `shop_withdraw` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `withdraw_no` varchar(32) NOT NULL COMMENT '提现单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `account_no` varchar(32) NOT NULL COMMENT '账户编号',
  `withdraw_amount` decimal(12,2) NOT NULL COMMENT '提现金额',
  `service_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '手续费',
  `actual_amount` decimal(12,2) NOT NULL COMMENT '实际到账金额',
  `payee_account` varchar(100) DEFAULT NULL COMMENT '收款账号',
  `payee_name` varchar(50) DEFAULT NULL COMMENT '收款人姓名',
  `payee_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '收款方式:1-银行卡,2-支付宝,3-微信',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待审核,1-审核通过,2-已打款,3-审核拒绝,4-打款失败',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `payout_time` datetime DEFAULT NULL COMMENT '打款时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_withdraw_no` (`withdraw_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_payout_time` (`payout_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺提现申请表';

-- 店铺收款账户表 (记录店铺收款账户信息)
CREATE TABLE IF NOT EXISTS `shop_payment_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `account_type` tinyint(4) NOT NULL COMMENT '账户类型:1-银行卡,2-支付宝,3-微信',
  `account_name` varchar(50) NOT NULL COMMENT '账户名称',
  `account_no` varchar(50) NOT NULL COMMENT '账号',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '银行名称',
  `bank_branch` varchar(100) DEFAULT NULL COMMENT '支行名称',
  `id_card_no` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认:0-否,1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_account_type` (`account_type`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺收款账户表';

-- 平台佣金规则表 (记录平台佣金规则)
CREATE TABLE IF NOT EXISTS `commission_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `rule_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '规则类型:1-分类佣金,2-店铺佣金,3-促销佣金',
  `target_id` bigint(20) DEFAULT NULL COMMENT '目标ID',
  `target_name` varchar(100) DEFAULT NULL COMMENT '目标名称',
  `rate` decimal(5,2) NOT NULL COMMENT '佣金比例%',
  `min_amount` decimal(10,2) DEFAULT NULL COMMENT '最小金额',
  `max_amount` decimal(10,2) DEFAULT NULL COMMENT '最大金额',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `priority` int(11) NOT NULL DEFAULT '0' COMMENT '优先级',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台佣金规则表';

-- 店铺发票表 (记录店铺发票信息)
CREATE TABLE IF NOT EXISTS `shop_invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `invoice_no` varchar(32) NOT NULL COMMENT '发票号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `settlement_id` bigint(20) DEFAULT NULL COMMENT '结算ID',
  `settlement_no` varchar(32) DEFAULT NULL COMMENT '结算单号',
  `invoice_amount` decimal(12,2) NOT NULL COMMENT '发票金额',
  `invoice_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '发票类型:1-增值税专用发票,2-增值税普通发票,3-电子发票',
  `invoice_title` varchar(100) NOT NULL COMMENT '发票抬头',
  `tax_no` varchar(50) DEFAULT NULL COMMENT '税号',
  `invoice_content` varchar(100) DEFAULT NULL COMMENT '发票内容',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待开票,1-已开票,2-已作废',
  `invoice_time` datetime DEFAULT NULL COMMENT '开票时间',
  `invoice_url` varchar(255) DEFAULT NULL COMMENT '发票链接',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invoice_no` (`invoice_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_settlement_id` (`settlement_id`),
  KEY `idx_settlement_no` (`settlement_no`),
  KEY `idx_status` (`status`),
  KEY `idx_invoice_time` (`invoice_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺发票表';

-- 财务对账单表 (记录财务对账单)
CREATE TABLE IF NOT EXISTS `financial_statement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `statement_no` varchar(32) NOT NULL COMMENT '对账单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `statement_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '对账类型:1-日对账,2-周对账,3-月对账',
  `statement_date` date NOT NULL COMMENT '对账日期',
  `total_order_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `total_order_count` int(11) NOT NULL DEFAULT '0' COMMENT '订单总数',
  `total_refund_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '退款总金额',
  `total_refund_count` int(11) NOT NULL DEFAULT '0' COMMENT '退款总数',
  `total_commission_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '佣金总金额',
  `total_settlement_amount` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '结算总金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-生成中,1-已生成,2-有异常',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_statement_no` (`statement_no`),
  UNIQUE KEY `uk_shop_date_type` (`shop_id`,`statement_date`,`statement_type`),
  KEY `idx_statement_date` (`statement_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务对账单表';

-- 店铺保证金记录表 (记录店铺保证金变动)
CREATE TABLE IF NOT EXISTS `shop_deposit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `deposit_no` varchar(32) NOT NULL COMMENT '保证金单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `account_no` varchar(32) NOT NULL COMMENT '账户编号',
  `deposit_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '保证金类型:1-开店保证金,2-活动保证金,3-违规保证金',
  `operation_type` tinyint(4) NOT NULL COMMENT '操作类型:1-缴纳,2-扣除,3-退还',
  `amount` decimal(12,2) NOT NULL COMMENT '金额',
  `balance` decimal(12,2) NOT NULL COMMENT '操作后余额',
  `reference_no` varchar(64) DEFAULT NULL COMMENT '关联单号',
  `reference_type` tinyint(4) DEFAULT NULL COMMENT '关联类型',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-处理中,1-成功,2-失败',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人名称',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_deposit_no` (`deposit_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_account_no` (`account_no`),
  KEY `idx_reference_no` (`reference_no`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺保证金记录表';
