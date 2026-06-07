-- ============================================
-- Flink 实时计算统计表
-- 创建时间：2026-06-07
-- 业务模块：实时数据统计
-- 设计目的：集中管理需要 Flink CDC 实时计算的统计表
-- 数据更新：通过 daily-discovery-platform 的 Flink CDC 管道实时同步
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS review_stats;
DROP TABLE IF EXISTS user_review_stats;
DROP TABLE IF EXISTS product_sales_stats;

-- ============================================
-- 1. 商品评价统计表（聚合统计）
-- ============================================

-- 商品评价统计表（实时统计字段）
CREATE TABLE IF NOT EXISTS review_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID',
    total_reviews INT DEFAULT 0 COMMENT '总评价数',
    average_rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
    rating_distribution JSON COMMENT '评分分布',
    purchased_reviews_count INT DEFAULT 0 COMMENT '已购评价数（真实购买用户的评价）',
    last_30_days_reviews INT DEFAULT 0 COMMENT '近 30 天评价数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_product_id (product_id),
    INDEX idx_average_rating (average_rating),
    INDEX idx_total_reviews (total_reviews)
) COMMENT '商品评价统计表（聚合统计）';

-- ============================================
-- 2. 用户评价统计表（单个评价的互动统计）
-- ============================================

-- 用户评价统计表（实时统计字段）
CREATE TABLE IF NOT EXISTS user_review_stats (
    review_id BIGINT PRIMARY KEY COMMENT '评价 ID',
    helpful_count INT DEFAULT 0 COMMENT '有用数量',
    reply_count INT DEFAULT 0 COMMENT '回复数量',
    like_count INT DEFAULT 0 COMMENT '点赞数量',
    last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    
    INDEX idx_review_id (review_id),
    INDEX idx_helpful_count (helpful_count),
    INDEX idx_like_count (like_count)
) COMMENT '用户评价统计表（单个评价的互动统计）';

-- ============================================
-- 3. 商品销量统计表（支持多种时间粒度）
-- ============================================

-- 销量统计表（单一表设计，主流电商最佳实践）
CREATE TABLE IF NOT EXISTS product_sales_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计 ID',
    product_id BIGINT NOT NULL COMMENT '商品 ID',
    
    -- 时间粒度
    time_granularity VARCHAR(10) NOT NULL COMMENT '时间粒度',
    stat_date DATE NOT NULL COMMENT '统计日期（如：日粒度 -2026-02-01，月粒度 -2026-02-01，年粒度 -2026-01-01）',
    
    -- 核心业务数据
    `rank` INT NOT NULL COMMENT '排名',
    sales_count INT DEFAULT 0 COMMENT '销量',
    sales_amount DECIMAL(12,2) DEFAULT 0.0 COMMENT '销售额',
    sales_growth_rate DECIMAL(5,2) COMMENT '销量增长率',
    
    -- 用户行为数据
    view_count INT DEFAULT 0 COMMENT '浏览量',
    favorite_count INT DEFAULT 0 COMMENT '收藏量',
    share_count INT DEFAULT 0 COMMENT '分享量',
    cart_count INT DEFAULT 0 COMMENT '加购量',
    
    -- 商品质量数据
    avg_rating DECIMAL(3,2) DEFAULT 0.0 COMMENT '平均评分',
    review_count INT DEFAULT 0 COMMENT '评价数量',
    return_count INT DEFAULT 0 COMMENT '退货数量',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_product_id (product_id),
    INDEX idx_time_granularity (time_granularity),
    INDEX idx_stat_date (stat_date),
    INDEX idx_rank (`rank`),
    INDEX idx_sales_count (sales_count),
    INDEX idx_avg_rating (avg_rating),
    INDEX idx_product_granularity_date (product_id, time_granularity, stat_date),
    
    -- 唯一约束（避免重复统计）
    UNIQUE KEY uk_product_granularity_date (product_id, time_granularity, stat_date)
) COMMENT '销量统计表（支持多种时间粒度）';

COMMIT;

-- ============================================
-- 初始测试数据（Flink 实时计算前的示例数据）
-- ============================================

-- 插入评价统计数据
INSERT INTO review_stats (product_id, total_reviews, average_rating, rating_distribution, purchased_reviews_count, last_30_days_reviews) VALUES
(1, 128, 4.5, '{"5": 80, "4": 35, "3": 10, "2": 2, "1": 1}', 120, 25),
(2, 89, 4.3, '{"5": 50, "4": 25, "3": 10, "2": 3, "1": 1}', 80, 18),
(3, 256, 4.7, '{"5": 180, "4": 60, "3": 12, "2": 3, "1": 1}', 240, 40),
(4, 194, 4.8, '{"5": 150, "4": 40, "3": 3, "2": 1, "1": 0}', 180, 35);

-- 插入用户评价统计数据
INSERT INTO user_review_stats (review_id, helpful_count, reply_count, like_count) VALUES
(1, 12, 2, 8),
(2, 5, 0, 3),
(3, 18, 1, 15),
(4, 3, 0, 2),
(5, 8, 0, 6),
(6, 25, 2, 20),
(7, 7, 1, 5),
(8, 10, 0, 8),
(9, 4, 0, 3),
(10, 15, 1, 12);

-- 插入社区热榜数据（销量统计）
INSERT INTO product_sales_stats (product_id, time_granularity, stat_date, `rank`, sales_count, sales_amount, view_count, favorite_count, avg_rating) VALUES
(1, 'daily', CURDATE(), 1, 150, 75000.00, 3000, 120, 4.8),
(2, 'daily', CURDATE(), 2, 120, 48000.00, 2500, 95, 4.7),
(3, 'daily', CURDATE(), 3, 100, 60000.00, 2200, 85, 4.6),
(4, 'daily', CURDATE(), 4, 90, 45000.00, 2000, 75, 4.5),
(5, 'daily', CURDATE(), 5, 80, 32000.00, 1800, 65, 4.4),
(6, 'daily', CURDATE(), 6, 70, 28000.00, 1600, 55, 4.3);

-- 插入销量统计数据（多时间粒度）
INSERT INTO product_sales_stats (product_id, time_granularity, stat_date, `rank`, sales_count, sales_amount, sales_growth_rate, view_count, favorite_count, share_count, cart_count, avg_rating, review_count, return_count) VALUES
-- 日粒度数据
(1, 'daily', '2026-02-01', 1, 25, 7475.00, 25.5, 500, 45, 15, 30, 4.5, 20, 1),
(2, 'daily', '2026-02-01', 2, 18, 3582.00, 18.3, 450, 35, 8, 25, 4.8, 15, 0),
(3, 'daily', '2026-02-01', 3, 12, 71988.00, 30.1, 600, 50, 5, 35, 4.2, 8, 1),
(4, 'daily', '2026-02-01', 4, 8, 39992.00, 15.7, 400, 30, 20, 20, 4.7, 12, 0),

-- 月粒度数据
(1, 'monthly', '2026-02-01', 1, 80, 23920.00, 15.2, 2500, 120, 45, 80, 4.5, 80, 4),
(2, 'monthly', '2026-02-01', 2, 60, 11940.00, 12.8, 2000, 100, 24, 70, 4.8, 60, 2),
(3, 'monthly', '2026-02-01', 3, 40, 239952.00, 18.5, 8000, 400, 15, 300, 4.2, 32, 3),
(4, 'monthly', '2026-02-01', 4, 30, 149976.00, 14.3, 6000, 300, 60, 200, 4.7, 48, 1),

-- 年粒度数据
(1, 'yearly', '2026-01-01', 1, 500, 149500.00, 20.5, 15000, 800, 180, 500, 4.5, 400, 20),
(2, 'yearly', '2026-01-01', 2, 350, 69650.00, 18.2, 12000, 600, 96, 400, 4.8, 280, 10),
(3, 'yearly', '2026-01-01', 3, 250, 1499760.00, 15.8, 50000, 2500, 60, 1800, 4.2, 200, 15),
(4, 'yearly', '2026-01-01', 4, 200, 999840.00, 13.5, 40000, 2000, 240, 1500, 4.7, 240, 8);

COMMIT;

-- ============================================
-- Flink CDC 数据同步说明
-- ============================================

/*
## Flink CDC Topic 映射

### review_stats
- 来源：MySQL binlog (daily_discover.review_stats)
- Topic: cdc_review_stats
- 写入：Flink SQL UPSERT into review_stats

### user_review_stats
- 来源：MySQL binlog (daily_discover.user_review_stats)
- Topic: cdc_user_review_stats
- 写入：Flink SQL UPSERT into user_review_stats

### product_sales_stats
- 来源：MySQL binlog (daily_discover.product_sales_stats)
- Topic: cdc_product_sales_stats
- 写入：Flink SQL UPSERT into product_sales_stats

## 数据流

原始数据（user_reviews, orders, behaviors）
    │
    ▼
Flink 实时计算
    │
    ├─→ 聚合评价 → review_stats
    ├─→ 评价互动 → user_review_stats
    └─→ 销量统计 → product_sales_stats
*/