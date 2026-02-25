-- ============================================
-- 10. 内容管理模块表结构
-- 业务模块: 内容管理与推荐
-- 创建时间: 2026-02-25
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS content_tag_relations;
DROP TABLE IF EXISTS content_tags;
DROP TABLE IF EXISTS content_analytics;
DROP TABLE IF EXISTS content_interactions;
DROP TABLE IF EXISTS content_media;
DROP TABLE IF EXISTS content_versions;
DROP TABLE IF EXISTS content_categories;
DROP TABLE IF EXISTS contents;

-- ============================================
-- 内容核心表
-- ============================================

-- 内容基础信息表
CREATE TABLE IF NOT EXISTS contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '内容ID',
    
    -- 内容基本信息
    title VARCHAR(200) NOT NULL COMMENT '内容标题',
    subtitle VARCHAR(300) COMMENT '内容副标题',
    summary TEXT COMMENT '内容摘要',
    content_type VARCHAR(30) NOT NULL COMMENT '内容类型：article-文章, video-视频, image-图片, audio-音频, live-直播, story-故事',
    
    -- 内容状态
    status VARCHAR(20) DEFAULT 'draft' COMMENT '状态：draft-草稿, review-审核中, published-已发布, archived-已归档',
    publish_at TIMESTAMP NULL COMMENT '发布时间',
    expire_at TIMESTAMP NULL COMMENT '过期时间',
    
    -- 内容分类
    category_id BIGINT COMMENT '分类ID',
    subcategory_id BIGINT COMMENT '子分类ID',
    
    -- 内容元数据
    author_id BIGINT NOT NULL COMMENT '作者ID',
    editor_id BIGINT COMMENT '编辑ID',
    source VARCHAR(100) COMMENT '内容来源',
    language VARCHAR(10) DEFAULT 'zh-CN' COMMENT '语言',
    
    -- 内容属性
    is_featured BOOLEAN DEFAULT false COMMENT '是否精选',
    is_trending BOOLEAN DEFAULT false COMMENT '是否热门',
    is_ai_generated BOOLEAN DEFAULT false COMMENT '是否AI生成',
    
    -- 内容质量
    quality_score DECIMAL(3,2) DEFAULT 0.0 COMMENT '质量评分',
    engagement_score DECIMAL(3,2) DEFAULT 0.0 COMMENT '互动评分',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 核心索引
    INDEX idx_content_type_status (content_type, status) COMMENT '类型状态查询',
    INDEX idx_publish_at (publish_at) COMMENT '发布时间查询',
    INDEX idx_category_status (category_id, status) COMMENT '分类状态查询',
    INDEX idx_is_featured (is_featured) COMMENT '精选内容查询',
    INDEX idx_is_trending (is_trending) COMMENT '热门内容查询',
    INDEX idx_author_status (author_id, status) COMMENT '作者状态查询',
    INDEX idx_quality_score (quality_score) COMMENT '质量评分查询',
    INDEX idx_engagement_score (engagement_score) COMMENT '互动评分查询'
) COMMENT '内容基础信息表';

-- ============================================
-- 内容分类表
-- ============================================

-- 内容分类表
CREATE TABLE IF NOT EXISTS content_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    
    -- 分类信息
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) UNIQUE NOT NULL COMMENT '分类代码',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    
    -- 分类属性
    category_type VARCHAR(30) DEFAULT 'general' COMMENT '分类类型：general-通用, topic-专题, brand-品牌, seasonal-季节性',
    description TEXT COMMENT '分类描述',
    
    -- 展示设置
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT true COMMENT '是否启用',
    
    -- 统计信息
    content_count INT DEFAULT 0 COMMENT '内容数量',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_parent_id (parent_id) COMMENT '父分类查询',
    INDEX idx_category_type (category_type) COMMENT '分类类型查询',
    INDEX idx_sort_order (sort_order) COMMENT '排序查询',
    INDEX idx_is_active (is_active) COMMENT '启用状态查询'
) COMMENT '内容分类表';

-- ============================================
-- 内容版本管理表
-- ============================================

-- 内容版本管理表
CREATE TABLE IF NOT EXISTS content_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '版本ID',
    
    -- 版本关联
    content_id BIGINT NOT NULL COMMENT '内容ID',
    version_number INT DEFAULT 1 COMMENT '版本号',
    
    -- 版本信息
    version_title VARCHAR(200) NOT NULL COMMENT '版本标题',
    version_description TEXT COMMENT '版本描述',
    
    -- 内容详情（JSON格式存储完整内容）
    content_body JSON COMMENT '内容正文',
    content_metadata JSON COMMENT '内容元数据',
    
    -- 版本状态
    is_current BOOLEAN DEFAULT false COMMENT '是否当前版本',
    
    -- 操作信息
    created_by BIGINT NOT NULL COMMENT '创建者ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_content_id (content_id) COMMENT '内容ID查询',
    INDEX idx_version_number (version_number) COMMENT '版本号查询',
    INDEX idx_is_current (is_current) COMMENT '当前版本查询',
    INDEX idx_created_at (created_at) COMMENT '创建时间查询'
) COMMENT '内容版本管理表';

-- ============================================
-- 内容媒体资源表
-- ============================================

-- 内容媒体资源表
CREATE TABLE IF NOT EXISTS content_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '媒体ID',
    
    -- 媒体关联
    content_id BIGINT NOT NULL COMMENT '内容ID',
    
    -- 媒体信息
    media_type VARCHAR(20) NOT NULL COMMENT '媒体类型：image-图片, video-视频, audio-音频, document-文档',
    media_url VARCHAR(500) NOT NULL COMMENT '媒体URL',
    media_title VARCHAR(200) COMMENT '媒体标题',
    media_description TEXT COMMENT '媒体描述',
    
    -- 媒体属性
    file_size BIGINT DEFAULT 0 COMMENT '文件大小（字节）',
    duration INT DEFAULT 0 COMMENT '时长（秒，视频/音频）',
    resolution VARCHAR(20) COMMENT '分辨率（视频/图片）',
    
    -- 展示设置
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_cover BOOLEAN DEFAULT false COMMENT '是否封面',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_content_id (content_id) COMMENT '内容ID查询',
    INDEX idx_media_type (media_type) COMMENT '媒体类型查询',
    INDEX idx_is_cover (is_cover) COMMENT '封面查询',
    INDEX idx_sort_order (sort_order) COMMENT '排序查询'
) COMMENT '内容媒体资源表';

-- ============================================
-- 内容互动表
-- ============================================

-- 内容互动表
CREATE TABLE IF NOT EXISTS content_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '互动ID',
    
    -- 互动关联
    content_id BIGINT NOT NULL COMMENT '内容ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    
    -- 互动类型
    interaction_type VARCHAR(20) NOT NULL COMMENT '互动类型：view-浏览, like-点赞, share-分享, comment-评论, favorite-收藏, report-举报',
    
    -- 互动数据
    interaction_value TEXT COMMENT '互动值（评论内容、分享渠道等）',
    interaction_weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '互动权重',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    INDEX idx_content_user (content_id, user_id) COMMENT '内容用户查询',
    INDEX idx_interaction_type (interaction_type) COMMENT '互动类型查询',
    INDEX idx_created_at (created_at) COMMENT '创建时间查询',
    UNIQUE KEY uk_content_user_interaction (content_id, user_id, interaction_type) COMMENT '防止重复互动'
) COMMENT '内容互动表';

-- ============================================
-- 内容分析表
-- ============================================

-- 内容分析表
CREATE TABLE IF NOT EXISTS content_analytics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分析ID',
    
    -- 分析关联
    content_id BIGINT NOT NULL COMMENT '内容ID',
    
    -- 时间粒度
    time_granularity VARCHAR(10) NOT NULL COMMENT '时间粒度：daily-日, weekly-周, monthly-月',
    stat_date DATE NOT NULL COMMENT '统计日期',
    
    -- 核心指标
    view_count INT DEFAULT 0 COMMENT '浏览量',
    unique_viewers INT DEFAULT 0 COMMENT '独立访客',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    share_count INT DEFAULT 0 COMMENT '分享数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    
    -- 深度指标
    avg_time_spent INT DEFAULT 0 COMMENT '平均停留时间（秒）',
    completion_rate DECIMAL(5,2) DEFAULT 0.0 COMMENT '完成率',
    bounce_rate DECIMAL(5,2) DEFAULT 0.0 COMMENT '跳出率',
    
    -- 转化指标
    click_count INT DEFAULT 0 COMMENT '点击量',
    conversion_count INT DEFAULT 0 COMMENT '转化量',
    conversion_rate DECIMAL(5,2) DEFAULT 0.0 COMMENT '转化率',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_content_time (content_id, time_granularity, stat_date) COMMENT '内容时间查询',
    INDEX idx_stat_date (stat_date) COMMENT '统计日期查询',
    INDEX idx_time_granularity (time_granularity) COMMENT '时间粒度查询',
    UNIQUE KEY uk_content_granularity_date (content_id, time_granularity, stat_date) COMMENT '防止重复统计'
) COMMENT '内容分析表';

-- ============================================
-- 内容标签系统
-- ============================================

-- 内容标签表
CREATE TABLE IF NOT EXISTS content_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    
    -- 标签信息
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    tag_type VARCHAR(20) DEFAULT 'general' COMMENT '标签类型：general-通用, topic-话题, keyword-关键词, emotion-情感',
    
    -- 标签属性
    tag_weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '标签权重',
    is_recommended BOOLEAN DEFAULT false COMMENT '是否推荐',
    
    -- 统计信息
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    UNIQUE KEY uk_tag_name (tag_name) COMMENT '标签名称唯一',
    INDEX idx_tag_type (tag_type) COMMENT '标签类型查询',
    INDEX idx_is_recommended (is_recommended) COMMENT '推荐标签查询',
    INDEX idx_usage_count (usage_count) COMMENT '使用次数查询'
) COMMENT '内容标签表';

-- 内容标签关联表
CREATE TABLE IF NOT EXISTS content_tag_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    
    -- 关联信息
    content_id BIGINT NOT NULL COMMENT '内容ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    
    -- 关联属性
    relation_weight DECIMAL(3,2) DEFAULT 1.0 COMMENT '关联权重',
    
    -- 时间戳
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引
    UNIQUE KEY uk_content_tag (content_id, tag_id) COMMENT '内容标签唯一关联',
    INDEX idx_content_id (content_id) COMMENT '内容ID查询',
    INDEX idx_tag_id (tag_id) COMMENT '标签ID查询'
) COMMENT '内容标签关联表';

-- ============================================
-- 初始数据
-- ============================================

-- 插入内容分类数据
INSERT INTO content_categories (category_name, category_code, parent_id, category_type, description, sort_order, is_active) VALUES
('科技资讯', 'tech_news', 0, 'topic', '最新科技动态和产品资讯', 1, true),
('产品评测', 'product_review', 0, 'topic', '深度产品使用评测', 2, true),
('使用教程', 'tutorial', 0, 'topic', '产品使用技巧和教程', 3, true),
('生活方式', 'lifestyle', 0, 'topic', '生活场景和产品搭配', 4, true),
('品牌故事', 'brand_story', 0, 'brand', '品牌文化和产品故事', 5, true);

-- 插入内容标签数据
INSERT INTO content_tags (tag_name, tag_type, tag_weight, is_recommended, usage_count) VALUES
('智能手机', 'keyword', 0.9, true, 150),
('智能手表', 'keyword', 0.8, true, 120),
('无线耳机', 'keyword', 0.7, true, 100),
('笔记本电脑', 'keyword', 0.8, true, 130),
('科技前沿', 'topic', 0.9, true, 200),
('产品评测', 'topic', 0.8, true, 180),
('使用技巧', 'topic', 0.7, false, 150),
('生活场景', 'topic', 0.6, false, 120);

-- 插入示例内容数据
INSERT INTO contents (title, subtitle, content_type, status, publish_at, category_id, author_id, is_featured, is_trending, quality_score, engagement_score) VALUES
('2026年智能手机市场趋势分析', '深度解读未来科技发展方向', 'article', 'published', '2026-02-25 10:00:00', 1, 1001, true, true, 0.9, 0.85),
('智能手表健康功能全面评测', '7天深度体验报告', 'article', 'published', '2026-02-24 14:30:00', 2, 1002, true, false, 0.8, 0.75),
('无线耳机降噪技术对比', '主流品牌横向评测', 'video', 'published', '2026-02-23 16:45:00', 2, 1003, false, true, 0.7, 0.8),
('笔记本电脑选购指南', '从入门到专业的完整攻略', 'article', 'published', '2026-02-22 09:15:00', 3, 1001, false, false, 0.8, 0.7),
('科技改变生活：智能家居新体验', '未来生活的无限可能', 'story', 'published', '2026-02-21 11:20:00', 4, 1004, true, true, 0.9, 0.9);

-- 插入内容版本数据
INSERT INTO content_versions (content_id, version_number, version_title, version_description, content_body, content_metadata, is_current, created_by) VALUES
(1, 1, '初始版本', '首次发布', '{"sections": [{"title": "市场概述", "content": "2026年智能手机市场将迎来新的变革..."}]}', '{"word_count": 1500, "reading_time": 5}', true, 1001),
(2, 1, '评测初稿', '完整评测内容', '{"sections": [{"title": "外观设计", "content": "智能手表的外观设计越来越精致..."}]}', '{"word_count": 2000, "reading_time": 8}', true, 1002),
(3, 1, '视频内容', '完整视频脚本', '{"video_script": "本期视频将对比主流无线耳机的降噪效果..."}', '{"duration": 600, "format": "mp4"}', true, 1003);

-- 插入内容标签关联数据
INSERT INTO content_tag_relations (content_id, tag_id, relation_weight) VALUES
(1, 1, 0.9),  -- 智能手机市场趋势 - 智能手机
(1, 5, 0.8),  -- 智能手机市场趋势 - 科技前沿
(2, 2, 0.9),  -- 智能手表评测 - 智能手表
(2, 6, 0.7),  -- 智能手表评测 - 产品评测
(3, 3, 0.9),  -- 无线耳机评测 - 无线耳机
(3, 6, 0.8),  -- 无线耳机评测 - 产品评测
(4, 4, 0.9),  -- 笔记本电脑指南 - 笔记本电脑
(4, 7, 0.6),  -- 笔记本电脑指南 - 使用技巧
(5, 1, 0.7),  -- 科技改变生活 - 智能手机
(5, 8, 0.8);  -- 科技改变生活 - 生活场景

-- 插入内容分析数据
INSERT INTO content_analytics (content_id, time_granularity, stat_date, view_count, unique_viewers, like_count, share_count, comment_count, favorite_count, avg_time_spent, completion_rate, bounce_rate, click_count, conversion_count, conversion_rate) VALUES
(1, 'daily', '2026-02-25', 1500, 1200, 300, 150, 80, 200, 180, 0.75, 0.25, 500, 50, 0.1),
(2, 'daily', '2026-02-24', 1200, 1000, 250, 100, 60, 150, 150, 0.70, 0.30, 400, 40, 0.1),
(3, 'daily', '2026-02-23', 1800, 1500, 400, 200, 100, 250, 200, 0.80, 0.20, 600, 60, 0.1);

COMMIT;