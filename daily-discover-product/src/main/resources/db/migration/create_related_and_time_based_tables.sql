-- 创建相关商品和时间维度表结构

USE daily_discover;

-- 相关商品表
CREATE TABLE related_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    related_product_id BIGINT NOT NULL COMMENT '相关商品ID',
    relation_type ENUM('similar', 'complementary', 'recommended') NOT NULL COMMENT '关联类型',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_related_product_id (related_product_id),
    INDEX idx_relation_type (relation_type),
    INDEX idx_sort_order (sort_order)
) COMMENT '相关商品表';

-- 时间维度商品表
CREATE TABLE time_based_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间维度ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    date DATE NOT NULL COMMENT '日期',
    time_dimension ENUM('yesterday', 'today', 'week', 'month') NOT NULL COMMENT '时间维度',
    rank INT NOT NULL COMMENT '排名',
    yesterday_rank INT COMMENT '昨日排名',
    yesterday_sales INT COMMENT '昨日销量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_date (date),
    INDEX idx_time_dimension (time_dimension),
    INDEX idx_rank (rank),
    UNIQUE KEY uk_product_date_dimension (product_id, date, time_dimension)
) COMMENT '时间维度商品表';

-- 商品操作记录表
CREATE TABLE product_actions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '操作记录ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    action_type ENUM('view', 'cart', 'purchase', 'favorite') NOT NULL COMMENT '操作类型',
    quantity INT DEFAULT 1 COMMENT '数量',
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    status VARCHAR(50) COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_product_id (product_id),
    INDEX idx_action_type (action_type),
    INDEX idx_action_date (action_date),
    INDEX idx_status (status)
) COMMENT '商品操作记录表';

COMMIT;