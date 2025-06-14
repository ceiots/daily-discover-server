-- 创建供应链库存系统数据库
CREATE DATABASE IF NOT EXISTS supply_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 使用供应链库存系统数据库
USE supply_db;

-- 供应链和库存管理系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: supplier_inventory_mvp.sql和supply_chain_mvp.sql
-- 高并发优化策略: 
-- 1. 表分区：按时间范围分区、按仓库ID哈希分区、按库存类型列表分区
-- 2. 避免外键约束：通过业务逻辑保证数据一致性
-- 3. 索引优化：核心字段索引、组合索引、状态时间复合索引
-- 4. 冷热数据分离：通过时间范围分区实现

-- 供应商表
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_code` varchar(32) NOT NULL COMMENT '供应商编码',
  `supplier_name` varchar(100) NOT NULL COMMENT '供应商名称',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
  `province` varchar(32) DEFAULT NULL COMMENT '省份',
  `city` varchar(32) DEFAULT NULL COMMENT '城市',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `category` varchar(100) DEFAULT NULL COMMENT '供应商品类别',
  `level` tinyint(4) DEFAULT '3' COMMENT '供应商等级:1-一级,2-二级,3-三级',
  `credit_score` int(11) DEFAULT '60' COMMENT '信用评分(0-100)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_code` (`supplier_code`),
  KEY `idx_supplier_name` (`supplier_name`),
  KEY `idx_level_status` (`level`,`status`),
  KEY `idx_credit_score` (`credit_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表'
PARTITION BY LIST(status) (
  PARTITION p_disabled VALUES IN (0),
  PARTITION p_enabled VALUES IN (1)
);

-- 供应商资质表
CREATE TABLE IF NOT EXISTS `supplier_qualification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `qualification_type` tinyint(4) NOT NULL COMMENT '资质类型:1-营业执照,2-生产许可证,3-质量认证,4-经营许可证',
  `qualification_name` varchar(100) NOT NULL COMMENT '资质名称',
  `qualification_no` varchar(100) DEFAULT NULL COMMENT '资质编号',
  `issue_authority` varchar(100) DEFAULT NULL COMMENT '发证机构',
  `issue_date` date DEFAULT NULL COMMENT '发证日期',
  `expire_date` date DEFAULT NULL COMMENT '到期日期',
  `attachment_url` varchar(255) DEFAULT NULL COMMENT '附件URL',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-无效,1-有效',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_qualification_type` (`qualification_type`),
  KEY `idx_expire_date` (`expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商资质表'
PARTITION BY HASH(supplier_id) PARTITIONS 8;

-- 库存表
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `available_stock` int(11) NOT NULL DEFAULT '0' COMMENT '可用库存',
  `lock_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `total_stock` int(11) NOT NULL DEFAULT '0' COMMENT '总库存',
  `warning_stock` int(11) DEFAULT '10' COMMENT '预警库存',
  `max_stock` int(11) DEFAULT '9999' COMMENT '最大库存',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_warehouse` (`sku_id`,`warehouse_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_stock_status` (`available_stock`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表'
PARTITION BY HASH(warehouse_id) PARTITIONS 8;

-- 库存流水表
CREATE TABLE IF NOT EXISTS `inventory_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `inventory_id` bigint(20) NOT NULL COMMENT '库存ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `type` tinyint(4) NOT NULL COMMENT '类型:1-入库,2-出库,3-锁定,4-解锁,5-调整',
  `quantity` int(11) NOT NULL COMMENT '变更数量',
  `before_stock` int(11) NOT NULL COMMENT '变更前库存',
  `after_stock` int(11) NOT NULL COMMENT '变更后库存',
  `order_no` varchar(32) DEFAULT NULL COMMENT '关联订单号',
  `purchase_no` varchar(32) DEFAULT NULL COMMENT '关联采购单号',
  `operator` varchar(32) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_type_time` (`type`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表'
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

-- 仓库表
CREATE TABLE IF NOT EXISTS `warehouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `warehouse_code` varchar(32) NOT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(100) NOT NULL COMMENT '仓库名称',
  `warehouse_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '仓库类型:1-自营仓,2-第三方仓,3-虚拟仓,4-供应商仓',
  `province` varchar(32) DEFAULT NULL COMMENT '省份',
  `city` varchar(32) DEFAULT NULL COMMENT '城市',
  `district` varchar(32) DEFAULT NULL COMMENT '区县',
  `address` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_warehouse_code` (`warehouse_code`),
  KEY `idx_warehouse_name` (`warehouse_name`),
  KEY `idx_type_status` (`warehouse_type`,`status`),
  KEY `idx_province_city` (`province`,`city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表'
PARTITION BY LIST(warehouse_type) (
  PARTITION p_self VALUES IN (1),
  PARTITION p_third_party VALUES IN (2),
  PARTITION p_virtual VALUES IN (3),
  PARTITION p_supplier VALUES IN (4)
);

-- 采购订单表
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `purchase_no` varchar(32) NOT NULL COMMENT '采购单号',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `purchase_status` tinyint(4) NOT NULL DEFAULT '10' COMMENT '采购状态:10-待审核,20-待采购,30-采购中,40-部分入库,50-已入库,60-已取消',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总金额',
  `total_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '总数量',
  `purchase_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '采购类型:1-常规采购,2-紧急采购,3-预采购',
  `payment_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '付款状态:0-未付款,1-部分付款,2-已付款',
  `expected_time` datetime DEFAULT NULL COMMENT '预计到货时间',
  `buyer` varchar(32) DEFAULT NULL COMMENT '采购员',
  `auditor` varchar(32) DEFAULT NULL COMMENT '审核人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_purchase_no` (`purchase_no`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_purchase_status` (`purchase_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表'
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

-- 采购订单项表
CREATE TABLE IF NOT EXISTS `purchase_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `purchase_id` bigint(20) NOT NULL COMMENT '采购单ID',
  `purchase_no` varchar(32) NOT NULL COMMENT '采购单号',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `quantity` int(11) NOT NULL COMMENT '采购数量',
  `received_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已收货数量',
  `price` decimal(10,2) NOT NULL COMMENT '采购单价',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `tax_rate` decimal(5,2) DEFAULT '0.00' COMMENT '税率',
  `tax_amount` decimal(10,2) DEFAULT '0.00' COMMENT '税额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待收货,1-部分收货,2-已收货',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_purchase_id` (`purchase_id`),
  KEY `idx_purchase_no` (`purchase_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单项表'
PARTITION BY HASH(purchase_id) PARTITIONS 8;

-- 入库单表
CREATE TABLE IF NOT EXISTS `stock_in` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_in_no` varchar(32) NOT NULL COMMENT '入库单号',
  `purchase_id` bigint(20) DEFAULT NULL COMMENT '采购单ID',
  `purchase_no` varchar(32) DEFAULT NULL COMMENT '采购单号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `stock_in_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '入库类型:1-采购入库,2-退货入库,3-调拨入库,4-盘盈入库',
  `stock_in_status` tinyint(4) NOT NULL DEFAULT '10' COMMENT '入库状态:10-待入库,20-部分入库,30-已入库',
  `total_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '总数量',
  `received_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已入库数量',
  `operator` varchar(32) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stock_in_no` (`stock_in_no`),
  KEY `idx_purchase_id` (`purchase_id`),
  KEY `idx_purchase_no` (`purchase_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_stock_in_status` (`stock_in_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表'
PARTITION BY LIST(stock_in_status) (
  PARTITION p_pending VALUES IN (10),
  PARTITION p_partial VALUES IN (20),
  PARTITION p_completed VALUES IN (30)
);

-- 入库单明细表
CREATE TABLE IF NOT EXISTS `stock_in_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_in_id` bigint(20) NOT NULL COMMENT '入库单ID',
  `stock_in_no` varchar(32) NOT NULL COMMENT '入库单号',
  `purchase_item_id` bigint(20) DEFAULT NULL COMMENT '采购项ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'SKU名称',
  `expect_quantity` int(11) NOT NULL COMMENT '预期数量',
  `actual_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '实际数量',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '批次号',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待入库,1-已入库',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_stock_in_id` (`stock_in_id`),
  KEY `idx_stock_in_no` (`stock_in_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表'
PARTITION BY HASH(stock_in_id) PARTITIONS 8;

-- 出库单表
CREATE TABLE IF NOT EXISTS `stock_out` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_out_no` varchar(32) NOT NULL COMMENT '出库单号',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `stock_out_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '出库类型:1-销售出库,2-采购退货,3-调拨出库,4-盘亏出库',
  `stock_out_status` tinyint(4) NOT NULL DEFAULT '10' COMMENT '出库状态:10-待出库,20-部分出库,30-已出库',
  `total_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '总数量',
  `shipped_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已出库数量',
  `operator` varchar(32) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stock_out_no` (`stock_out_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_stock_out_status` (`stock_out_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表'
PARTITION BY LIST(stock_out_status) (
  PARTITION p_pending VALUES IN (10),
  PARTITION p_partial VALUES IN (20),
  PARTITION p_completed VALUES IN (30)
);

-- 出库单明细表
CREATE TABLE IF NOT EXISTS `stock_out_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `stock_out_id` bigint(20) NOT NULL COMMENT '出库单ID',
  `stock_out_no` varchar(32) NOT NULL COMMENT '出库单号',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单项ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `sku_name` varchar(200) DEFAULT NULL COMMENT 'SKU名称',
  `expect_quantity` int(11) NOT NULL COMMENT '预期数量',
  `actual_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '实际数量',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '批次号',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待出库,1-已出库',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_stock_out_id` (`stock_out_id`),
  KEY `idx_stock_out_no` (`stock_out_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表'
PARTITION BY HASH(stock_out_id) PARTITIONS 8;

-- 库存批次表
CREATE TABLE IF NOT EXISTS `inventory_batch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `batch_no` varchar(32) NOT NULL COMMENT '批次号',
  `available_stock` int(11) NOT NULL DEFAULT '0' COMMENT '可用库存',
  `lock_stock` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_warehouse_sku_batch` (`warehouse_id`,`sku_id`,`batch_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_expiry_date` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存批次表'
PARTITION BY HASH(warehouse_id) PARTITIONS 8;

-- 库存盘点表
CREATE TABLE IF NOT EXISTS `inventory_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `check_no` varchar(32) NOT NULL COMMENT '盘点单号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `check_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '盘点类型:1-全盘,2-抽盘,3-动态盘点',
  `check_status` tinyint(4) NOT NULL DEFAULT '10' COMMENT '盘点状态:10-待盘点,20-盘点中,30-已完成',
  `total_sku` int(11) NOT NULL DEFAULT '0' COMMENT 'SKU总数',
  `checked_sku` int(11) NOT NULL DEFAULT '0' COMMENT '已盘点SKU数',
  `profit_loss_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '盈亏金额',
  `profit_sku` int(11) NOT NULL DEFAULT '0' COMMENT '盘盈SKU数',
  `loss_sku` int(11) NOT NULL DEFAULT '0' COMMENT '盘亏SKU数',
  `normal_sku` int(11) NOT NULL DEFAULT '0' COMMENT '正常SKU数',
  `checker` varchar(32) DEFAULT NULL COMMENT '盘点人',
  `auditor` varchar(32) DEFAULT NULL COMMENT '审核人',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_no` (`check_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_check_status` (`check_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点表'
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

-- 库存盘点明细表
CREATE TABLE IF NOT EXISTS `inventory_check_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `check_id` bigint(20) NOT NULL COMMENT '盘点单ID',
  `check_no` varchar(32) NOT NULL COMMENT '盘点单号',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `sku_id` bigint(20) NOT NULL COMMENT 'SKU ID',
  `sku_code` varchar(64) NOT NULL COMMENT 'SKU编码',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '批次号',
  `system_stock` int(11) NOT NULL DEFAULT '0' COMMENT '系统库存',
  `actual_stock` int(11) DEFAULT NULL COMMENT '实际库存',
  `diff_stock` int(11) DEFAULT NULL COMMENT '差异库存',
  `cost_price` decimal(10,2) DEFAULT NULL COMMENT '成本价',
  `diff_amount` decimal(10,2) DEFAULT NULL COMMENT '差异金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0-待盘点,1-已盘点',
  `checker` varchar(32) DEFAULT NULL COMMENT '盘点人',
  `check_time` datetime DEFAULT NULL COMMENT '盘点时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_check_id` (`check_id`),
  KEY `idx_check_no` (`check_no`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_sku_id` (`sku_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点明细表'
PARTITION BY HASH(check_id) PARTITIONS 8;
