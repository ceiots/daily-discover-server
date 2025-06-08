-- 供应商表（基础信息）
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `name` varchar(100) NOT NULL COMMENT '供应商名称',
  `contact_person` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 供应商产品关联表
CREATE TABLE IF NOT EXISTS `supplier_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `cost_price` decimal(10,2) NOT NULL COMMENT '成本价',
  `min_order_quantity` int(11) DEFAULT '1' COMMENT '最小订购量',
  `lead_time_days` int(11) DEFAULT '7' COMMENT '交货周期(天)',
  `is_primary` tinyint(1) DEFAULT '0' COMMENT '是否主要供应商:1-是,0-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_product` (`supplier_id`,`product_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_supplier_primary` (`supplier_id`,`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商产品关联表';

-- 库存主表
CREATE TABLE IF NOT EXISTS `inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '库存ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `available_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '可用库存',
  `locked_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '锁定库存',
  `alert_threshold` int(11) DEFAULT '10' COMMENT '库存预警阈值',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_shop` (`product_id`,`shop_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_alert` (`available_quantity`,`alert_threshold`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存主表';

-- 库存日志表
CREATE TABLE IF NOT EXISTS `inventory_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `inventory_id` bigint(20) NOT NULL COMMENT '库存ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `log_type` tinyint(4) NOT NULL COMMENT '日志类型:1-入库,2-出库,3-锁定,4-解锁',
  `quantity` int(11) NOT NULL COMMENT '数量',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `po_id` bigint(20) DEFAULT NULL COMMENT '关联采购单ID',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_log_type` (`log_type`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_po_id` (`po_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存日志表';

-- 采购单主表
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '采购单ID',
  `po_number` varchar(32) NOT NULL COMMENT '采购单号',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待确认,2-已确认,3-部分入库,4-全部入库,5-已取消',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `expected_arrival_date` datetime DEFAULT NULL COMMENT '预计到货日期',
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
  KEY `idx_create_time` (`create_time`),
  KEY `idx_expected_arrival_date` (`expected_arrival_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单主表';

-- 采购单明细表
CREATE TABLE IF NOT EXISTS `purchase_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `po_id` bigint(20) NOT NULL COMMENT '采购单ID',
  `po_number` varchar(32) NOT NULL COMMENT '采购单号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `product_name` varchar(255) NOT NULL COMMENT '产品名称',
  `quantity` int(11) NOT NULL COMMENT '采购数量',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价',
  `total_price` decimal(10,2) NOT NULL COMMENT '总价',
  `received_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '已入库数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_po_id` (`po_id`),
  KEY `idx_po_number` (`po_number`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细表';

-- 采购入库表
CREATE TABLE IF NOT EXISTS `purchase_receipt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '入库单ID',
  `receipt_number` varchar(32) NOT NULL COMMENT '入库单号',
  `po_id` bigint(20) NOT NULL COMMENT '采购单ID',
  `po_number` varchar(32) NOT NULL COMMENT '采购单号',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `receipt_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-待入库,2-已入库,3-部分入库',
  `operator_id` bigint(20) NOT NULL COMMENT '操作人ID',
  `receipt_time` datetime DEFAULT NULL COMMENT '入库时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_receipt_number` (`receipt_number`),
  KEY `idx_po_id` (`po_id`),
  KEY `idx_po_number` (`po_number`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_receipt_status` (`receipt_status`),
  KEY `idx_receipt_time` (`receipt_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购入库表';

-- 采购入库明细表
CREATE TABLE IF NOT EXISTS `purchase_receipt_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `receipt_id` bigint(20) NOT NULL COMMENT '入库单ID',
  `receipt_number` varchar(32) NOT NULL COMMENT '入库单号',
  `po_id` bigint(20) NOT NULL COMMENT '采购单ID',
  `po_item_id` bigint(20) NOT NULL COMMENT '采购单明细ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `expected_quantity` int(11) NOT NULL COMMENT '预期数量',
  `actual_quantity` int(11) NOT NULL COMMENT '实际入库数量',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_receipt_id` (`receipt_id`),
  KEY `idx_receipt_number` (`receipt_number`),
  KEY `idx_po_id` (`po_id`),
  KEY `idx_po_item_id` (`po_item_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购入库明细表';

-- 库存盘点表
CREATE TABLE IF NOT EXISTS `inventory_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '盘点单ID',
  `check_number` varchar(32) NOT NULL COMMENT '盘点单号',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `check_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-盘点中,2-已完成,3-已取消',
  `checker_id` bigint(20) NOT NULL COMMENT '盘点人ID',
  `check_time` datetime DEFAULT NULL COMMENT '盘点完成时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_number` (`check_number`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_check_status` (`check_status`),
  KEY `idx_checker_id` (`checker_id`),
  KEY `idx_check_time` (`check_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点表';

-- 库存盘点明细表
CREATE TABLE IF NOT EXISTS `inventory_check_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `check_id` bigint(20) NOT NULL COMMENT '盘点单ID',
  `check_number` varchar(32) NOT NULL COMMENT '盘点单号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `system_quantity` int(11) NOT NULL COMMENT '系统库存数量',
  `actual_quantity` int(11) NOT NULL COMMENT '实际盘点数量',
  `difference` int(11) NOT NULL COMMENT '差异数量',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_check_id` (`check_id`),
  KEY `idx_check_number` (`check_number`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_difference` (`difference`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点明细表';

-- 库存预警记录表
CREATE TABLE IF NOT EXISTS `inventory_alert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_sku_id` bigint(20) NOT NULL COMMENT '产品SKU ID',
  `product_name` varchar(255) NOT NULL COMMENT '产品名称',
  `shop_id` bigint(20) NOT NULL COMMENT '店铺ID',
  `current_quantity` int(11) NOT NULL COMMENT '当前库存数量',
  `threshold` int(11) NOT NULL COMMENT '预警阈值',
  `alert_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-未处理,2-已处理,3-已忽略',
  `processor_id` bigint(20) DEFAULT NULL COMMENT '处理人ID',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `process_remark` varchar(255) DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_product_sku_id` (`product_sku_id`),
  KEY `idx_shop_id` (`shop_id`),
  KEY `idx_alert_status` (`alert_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警记录表'; 