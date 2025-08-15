-- 创建品牌表
CREATE TABLE IF NOT EXISTS brands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '品牌名称',
    logo VARCHAR(500) COMMENT '品牌logo URL',
    description VARCHAR(500) COMMENT '品牌描述',
    category VARCHAR(50) COMMENT '品牌分类',
    followers VARCHAR(20) COMMENT '关注者数量',
    trend VARCHAR(10) COMMENT '增长趋势',
    featured BOOLEAN DEFAULT FALSE COMMENT '是否精选',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_brands_category ON brands(category);
CREATE INDEX idx_brands_featured ON brands(featured);