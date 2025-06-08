-- 供应链管理系统表结构（优化版）
-- 每个表不超过20个字段，不使用外键，从业务功能正确性和SQL性能两方面进行优化
-- 确保业务的最小可行性

-- 供应商表（优化版）
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `name` varchar(100) NOT NULL COMMENT '供应商名称',
  `contact_person` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `code` varchar(20) DEFAULT NULL COMMENT '供应商编码',
  `level` tinyint(4) DEFAULT '3' COMMENT '供应商等级:1-核心,2-重要,3-一般',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 供应商产品关联表（优化版）
CREATE TABLE IF NOT EXISTS `supplier_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `cost_price` decimal(10,2) NOT NULL COMMENT '成本价',
  `min_order_quantity` int(11) DEFAULT '1' COMMENT '最小订购量',
  `lead_time_days` int(11) DEFAULT '7' COMMENT '交货周期(天)',
  `is_primary` tinyint(1) DEFAULT '0' COMMENT '是否主要供应商:1-是,0-否',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_product_sku` (`supplier_id`,`product_id`,`product_sku_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_supplier_primary` (`supplier_id`,`is_primary`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商产品关联表';

-- 库存主表（优化版）
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `available_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '可用库存',
  `locked_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `alert_threshold` int(11) DEFAULT '10' COMMENT '库存预警阈值',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_sku_shop` (`product_id`,`product_sku_id`,`shop_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_alert` (`available_quantity`,`alert_threshold`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存主表';

-- 库存变更日志表（优化版）
CREATE TABLE IF NOT EXISTS `inventory_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `inventory_id` bigint(20) NOT NULL COMMENT '库存ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `change_type` tinyint(4) NOT NULL COMMENT '变更类型:1-入库,2-出库,3-锁定,4-解锁,5-调整',
  `quantity` int(11) NOT NULL COMMENT '变更数量',
  `before_quantity` int(11) NOT NULL COMMENT '变更前数量',
  `after_quantity` int(11) NOT NULL COMMENT '变更后数量',
  `relation_id` varchar(32) DEFAULT NULL COMMENT '关联单号',
  `relation_type` tinyint(4) DEFAULT NULL COMMENT '关联类型:1-订单,2-采购,3-退货,4-盘点',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_relation_id` (`relation_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变更日志表';

-- 采购单表（优化版）
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '采购单ID',
  `po_number` varchar(32) NOT NULL COMMENT '采购单号',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待确认,2-已确认,3-部分入库,4-全部入库,5-已取消',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `expected_arrival_date` date DEFAULT NULL COMMENT '预计到货日期',
  `creator_id` bigint(20) NOT NULL COMMENT '创建人ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_po_number` (`po_number`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expected_arrival_date` (`expected_arrival_date`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单表';

-- 采购单明细表（优化版）
CREATE TABLE IF NOT EXISTS `purchase_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `po_id` bigint(20) NOT NULL COMMENT '采购单ID',
  `po_number` varchar(32) NOT NULL COMMENT '采购单号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `sku_code` varchar(50) DEFAULT NULL COMMENT 'SKU编码',
  `product_name` varchar(128) NOT NULL COMMENT '产品名称',
  `quantity` int(11) NOT NULL COMMENT '采购数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `received_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已入库数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待入库,2-部分入库,3-已入库',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_po_id` (`po_id`),
  KEY `idx_po_number` (`po_number`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_sku_code` (`sku_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细表';

-- 入库单表（优化版）
CREATE TABLE IF NOT EXISTS `stock_in` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '入库单ID',
  `in_number` varchar(32) NOT NULL COMMENT '入库单号',
  `po_id` bigint(20) DEFAULT NULL COMMENT '采购单ID',
  `po_number` varchar(32) DEFAULT NULL COMMENT '采购单号',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `in_type` tinyint(4) NOT NULL COMMENT '入库类型:1-采购入库,2-退货入库,3-调拨入库,4-盘盈入库,5-其他',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待入库,2-已入库,3-已取消',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `in_time` datetime DEFAULT NULL COMMENT '入库时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_in_number` (`in_number`),
  KEY `idx_po_id` (`po_id`),
  KEY `idx_po_number` (`po_number`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_in_type` (`in_type`),
  KEY `idx_status` (`status`),
  KEY `idx_in_time` (`in_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 入库单明细表（优化版）
CREATE TABLE IF NOT EXISTS `stock_in_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `in_id` bigint(20) NOT NULL COMMENT '入库单ID',
  `in_number` varchar(32) NOT NULL COMMENT '入库单号',
  `po_item_id` bigint(20) DEFAULT NULL COMMENT '采购单明细ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `sku_code` varchar(50) DEFAULT NULL COMMENT 'SKU编码',
  `product_name` varchar(128) NOT NULL COMMENT '产品名称',
  `expected_quantity` int(11) NOT NULL COMMENT '预期数量',
  `actual_quantity` int(11) NOT NULL COMMENT '实际入库数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待入库,2-已入库',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_in_id` (`in_id`),
  KEY `idx_in_number` (`in_number`),
  KEY `idx_po_item_id` (`po_item_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_sku_code` (`sku_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表';

-- 出库单表（优化版）
CREATE TABLE IF NOT EXISTS `stock_out` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '出库单ID',
  `out_number` varchar(32) NOT NULL COMMENT '出库单号',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单ID',
  `order_no` varchar(32) DEFAULT NULL COMMENT '订单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `out_type` tinyint(4) NOT NULL COMMENT '出库类型:1-销售出库,2-调拨出库,3-盘亏出库,4-其他',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待出库,2-已出库,3-已取消',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `out_time` datetime DEFAULT NULL COMMENT '出库时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_out_number` (`out_number`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_out_type` (`out_type`),
  KEY `idx_status` (`status`),
  KEY `idx_out_time` (`out_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 出库单明细表（优化版）
CREATE TABLE IF NOT EXISTS `stock_out_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `out_id` bigint(20) NOT NULL COMMENT '出库单ID',
  `out_number` varchar(32) NOT NULL COMMENT '出库单号',
  `order_item_id` bigint(20) DEFAULT NULL COMMENT '订单明细ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `sku_code` varchar(50) DEFAULT NULL COMMENT 'SKU编码',
  `product_name` varchar(128) NOT NULL COMMENT '产品名称',
  `quantity` int(11) NOT NULL COMMENT '出库数量',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待出库,2-已出库',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_out_id` (`out_id`),
  KEY `idx_out_number` (`out_number`),
  KEY `idx_order_item_id` (`order_item_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_sku_code` (`sku_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';

-- 库存盘点表（优化版）
CREATE TABLE IF NOT EXISTS `inventory_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '盘点单ID',
  `check_number` varchar(32) NOT NULL COMMENT '盘点单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `check_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '盘点类型:1-全场盘点,2-分类盘点,3-抽样盘点',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-盘点中,2-已完成,3-已取消',
  `checker_id` bigint(20) NOT NULL COMMENT '盘点人ID',
  `check_time` datetime DEFAULT NULL COMMENT '盘点完成时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_number` (`check_number`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_check_type` (`check_type`),
  KEY `idx_status` (`status`),
  KEY `idx_checker_id` (`checker_id`),
  KEY `idx_check_time` (`check_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点表';

-- 盘点明细表（优化版）
CREATE TABLE IF NOT EXISTS `inventory_check_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `check_id` bigint(20) NOT NULL COMMENT '盘点单ID',
  `check_number` varchar(32) NOT NULL COMMENT '盘点单号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `sku_code` varchar(50) DEFAULT NULL COMMENT 'SKU编码',
  `product_name` varchar(128) NOT NULL COMMENT '产品名称',
  `system_quantity` int(11) NOT NULL COMMENT '系统库存数量',
  `actual_quantity` int(11) NOT NULL COMMENT '实际盘点数量',
  `difference` int(11) NOT NULL COMMENT '差异数量',
  `adjustment_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '调整状态:0-未调整,1-已调整',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_check_id` (`check_id`),
  KEY `idx_check_number` (`check_number`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_sku_code` (`sku_code`),
  KEY `idx_difference` (`difference`),
  KEY `idx_adjustment_status` (`adjustment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点明细表';
