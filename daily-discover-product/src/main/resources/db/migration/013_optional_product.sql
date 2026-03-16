-- ============================================
-- 非核心可选表 - 服务相关
-- 业务模块: 产品服务信息（仅特殊商品需要）
-- 新增商品时完全可不加，非必需
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_service_info_values;
DROP TABLE IF EXISTS product_service_categories;

-- ============================================
-- 8. 产品服务信息模块（可扩展设计）
-- ============================================

-- 产品服务信息分类表（简化版）
CREATE TABLE IF NOT EXISTS product_service_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    
    -- 分类基本信息
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称（如：产品参数、售后服务、认证信息）',
    category_code VARCHAR(50) NOT NULL COMMENT '分类代码（英文标识，如：specs, service, certification）',
    
    -- 分类属性
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    
    -- 状态管理
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    UNIQUE KEY uk_category_code (category_code),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status)
) COMMENT '产品服务信息分类表';

-- 产品服务信息值表（合并信息项和值存储）
CREATE TABLE IF NOT EXISTS product_service_info_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '值ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    
    -- 信息项定义
    info_key VARCHAR(100) NOT NULL COMMENT '信息项键（英文标识，如：weight, warranty_period）',
    info_label VARCHAR(100) NOT NULL COMMENT '信息项标签（显示名称，如：产品重量、质保期限）',
    
    -- 数据类型配置
    data_type VARCHAR(20) DEFAULT 'text' COMMENT '数据类型：text-文本, number-数字, boolean-布尔, date-日期',
    value_unit VARCHAR(20) COMMENT '数值单位（如：kg, month, year）',
    
    -- 值存储（支持多值）
    string_value VARCHAR(500) COMMENT '字符串值',
    number_value DECIMAL(15,4) COMMENT '数值',
    boolean_value BOOLEAN COMMENT '布尔值',
    date_value DATE COMMENT '日期值',
    
    -- 显示配置
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    
    -- 状态管理
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_category_id (category_id),
    INDEX idx_info_key (info_key),
    INDEX idx_product_category (product_id, category_id),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status),
    
    -- 唯一约束（一个商品一个分类一个信息项只能有一条记录）
    UNIQUE KEY uk_product_category_info (product_id, category_id, info_key)
) COMMENT '产品服务信息值表（合并信息项和值存储）';

-- ============================================
-- 产品服务信息初始数据
-- ============================================

-- 插入产品服务信息分类数据
INSERT INTO product_service_categories (category_name, category_code, sort_order) VALUES
('产品参数', 'specs', 1),
('售后服务', 'service', 2),
('认证信息', 'certification', 3),
('包装清单', 'package', 4),
('使用说明', 'instructions', 5);

-- 为现有商品插入产品服务信息值（示例数据）
INSERT INTO product_service_info_values (product_id, category_id, info_key, info_label, data_type, value_unit, string_value, number_value, sort_order) VALUES
-- 智能手表 Pro（产品ID=1）
(1, 1, 'origin', '产品产地', 'text', NULL, '中国', NULL, 1),
(1, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.05, 2),
(1, 1, 'dimensions', '产品尺寸', 'text', NULL, '4.2×3.6×1.2cm', NULL, 3),
(1, 1, 'material', '产品材质', 'text', NULL, '不锈钢表壳，蓝宝石玻璃', NULL, 4),
(1, 1, 'shelf_life', '保质期', 'number', 'month', NULL, 24, 5),
(1, 2, 'return_policy', '退换货政策', 'text', NULL, '7天无理由退换货，1年质保', NULL, 1),
(1, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 12, 2),
(1, 2, 'customer_service_hotline', '客服热线', 'text', NULL, '400-123-4567', NULL, 3),
(1, 4, 'package_contents', '包装清单', 'text', NULL, 'Apple Watch Series 8，充电线，说明书', NULL, 1),
(1, 5, 'usage_method', '使用方法', 'text', NULL, '长按侧边按钮开机，连接手机App设置', NULL, 1),

-- 无线降噪耳机（产品ID=2）
(2, 1, 'origin', '产品产地', 'text', NULL, '日本', NULL, 1),
(2, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.25, 2),
(2, 1, 'material', '产品材质', 'text', NULL, '塑料外壳，蛋白皮耳罩', NULL, 3),
(2, 1, 'shelf_life', '保质期', 'number', 'month', NULL, 36, 4),
(2, 2, 'return_policy', '退换货政策', 'text', NULL, '15天无理由退换货，2年质保', NULL, 1),
(2, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 24, 2),
(2, 4, 'package_contents', '包装清单', 'text', NULL, 'Sony WH-1000XM5，充电线，收纳盒，说明书', NULL, 1),

-- 轻薄笔记本电脑（产品ID=3）
(3, 1, 'origin', '产品产地', 'text', NULL, '美国', NULL, 1),
(3, 1, 'weight', '产品重量', 'number', 'kg', NULL, 1.29, 2),
(3, 1, 'dimensions', '产品尺寸', 'text', NULL, '30.4×21.5×1.6cm', NULL, 3),
(3, 1, 'material', '产品材质', 'text', NULL, '铝合金机身，Retina显示屏', NULL, 4),
(3, 2, 'return_policy', '退换货政策', 'text', NULL, '质量问题7天包退，15天包换，2年保修', NULL, 1),
(3, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 24, 2),

-- 智能手机旗舰版（产品ID=4）
(4, 1, 'origin', '产品产地', 'text', NULL, '中国', NULL, 1),
(4, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.17, 2),
(4, 1, 'dimensions', '产品尺寸', 'text', NULL, '14.7×7.1×0.78cm', NULL, 3),
(4, 1, 'material', '产品材质', 'text', NULL, '玻璃后盖，不锈钢边框', NULL, 4),
(4, 2, 'return_policy', '退换货政策', 'text', NULL, '7天无理由退换货，1年官方保修', NULL, 1),
(4, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 12, 2),
(4, 3, 'quality_certification', '质量认证', 'text', NULL, '3C认证，入网许可证', NULL, 1),
(4, 4, 'package_contents', '包装清单', 'text', NULL, 'iPhone 15，充电线，说明书，SIM卡针', NULL, 1),

-- 运动蓝牙耳机（产品ID=5）
(5, 1, 'origin', '产品产地', 'text', NULL, '美国', NULL, 1),
(5, 1, 'weight', '产品重量', 'number', 'kg', NULL, 0.08, 2),
(5, 1, 'dimensions', '产品尺寸', 'text', NULL, '2.5×2.5×1.2cm', NULL, 3),
(5, 1, 'material', '产品材质', 'text', NULL, '硅胶材质，防水设计', NULL, 4),
(5, 1, 'battery_life', '电池续航', 'number', 'hour', NULL, 6, 5),
(5, 2, 'return_policy', '退换货政策', 'text', NULL, '30天无理由退换货，2年质保', NULL, 1),
(5, 2, 'warranty_period', '质保期限', 'number', 'month', NULL, 24, 2),
(5, 4, 'package_contents', '包装清单', 'text', NULL, 'Bose QuietComfort Earbuds II，充电盒，USB-C充电线，说明书', NULL, 1),
(5, 5, 'usage_method', '使用方法', 'text', NULL, '打开充电盒自动连接，支持触控操作和语音助手', NULL, 1);