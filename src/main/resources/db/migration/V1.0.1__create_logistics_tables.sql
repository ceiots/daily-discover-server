-- 创建物流公司表
CREATE TABLE IF NOT EXISTS logistics_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL COMMENT '物流公司编码',
    name VARCHAR(100) NOT NULL COMMENT '物流公司名称',
    short_name VARCHAR(50) COMMENT '物流公司简称',
    phone VARCHAR(20) COMMENT '物流公司电话',
    website VARCHAR(200) COMMENT '物流公司网址',
    logo VARCHAR(200) COMMENT '物流公司Logo',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    sort INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流公司表';

-- 创建物流信息表
CREATE TABLE IF NOT EXISTS logistics_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    tracking_number VARCHAR(50) NOT NULL COMMENT '物流单号',
    company_code VARCHAR(50) NOT NULL COMMENT '物流公司编码',
    company_name VARCHAR(100) NOT NULL COMMENT '物流公司名称',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '物流状态：0-待发货，1-已发货，2-运输中，3-已签收，4-异常',
    shipping_time TIMESTAMP NULL COMMENT '发货时间',
    estimated_delivery_time TIMESTAMP NULL COMMENT '预计送达时间',
    actual_delivery_time TIMESTAMP NULL COMMENT '实际送达时间',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收件人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收件人电话',
    receiver_address VARCHAR(500) NOT NULL COMMENT '收件人地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_tracking_number (tracking_number),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';

-- 创建物流轨迹表
CREATE TABLE IF NOT EXISTS logistics_track (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    logistics_id BIGINT NOT NULL COMMENT '物流信息ID',
    track_time TIMESTAMP NOT NULL COMMENT '轨迹时间',
    location VARCHAR(100) NOT NULL COMMENT '轨迹地点',
    description VARCHAR(500) NOT NULL COMMENT '轨迹描述',
    status VARCHAR(50) NOT NULL COMMENT '轨迹状态',
    status_code VARCHAR(50) NOT NULL COMMENT '轨迹状态码',
    operator VARCHAR(50) COMMENT '操作人',
    operator_phone VARCHAR(20) COMMENT '操作人电话',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_logistics_id (logistics_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹表';

-- 插入默认物流公司数据
INSERT INTO logistics_company (code, name, short_name, phone, website, enabled, sort) VALUES
('SF', '顺丰速运', '顺丰', '95338', 'https://www.sf-express.com', 1, 1),
('YTO', '圆通速递', '圆通', '95554', 'https://www.yto.net.cn', 1, 2),
('ZTO', '中通快递', '中通', '95311', 'https://www.zto.com', 1, 3),
('STO', '申通快递', '申通', '95543', 'https://www.sto.cn', 1, 4),
('YD', '韵达快递', '韵达', '95546', 'https://www.yundaex.com', 1, 5),
('EMS', 'EMS', 'EMS', '11183', 'https://www.ems.com.cn', 1, 6),
('HTKY', '百世快递', '百世', '95320', 'https://www.800bestex.com', 1, 7),
('JD', '京东物流', '京东', '950616', 'https://www.jdl.com', 1, 8),
('QFKD', '全峰快递', '全峰', '400-100-0001', 'https://www.qfkd.com.cn', 1, 9),
('GTO', '国通快递', '国通', '400-111-1123', 'https://www.gto365.com', 1, 10); 