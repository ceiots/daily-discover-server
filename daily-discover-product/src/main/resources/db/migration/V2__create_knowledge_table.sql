-- 创建每日知识表
CREATE TABLE IF NOT EXISTS daily_knowledge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '知识标题',
    category VARCHAR(50) NOT NULL COMMENT '知识分类：psychology, history, aesthetics, science, innovation',
    concept TEXT NOT NULL COMMENT '核心概念',
    detail TEXT NOT NULL COMMENT '详细描述',
    application TEXT COMMENT '应用场景',
    difficulty VARCHAR(20) DEFAULT 'beginner' COMMENT '难度：beginner, intermediate, advanced',
    read_time INT DEFAULT 3 COMMENT '阅读时间（分钟）',
    tags VARCHAR(500) COMMENT '标签，用逗号分隔',
    icon VARCHAR(10) COMMENT '图标emoji',
    color_scheme VARCHAR(20) DEFAULT 'blue' COMMENT '颜色方案',
    image_url VARCHAR(500) COMMENT '配图URL',
    is_active BOOLEAN DEFAULT true COMMENT '是否激活',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_difficulty (difficulty),
    INDEX idx_created_at (created_at),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日知识表';

-- 插入示例数据
INSERT INTO daily_knowledge (title, category, concept, detail, application, difficulty, read_time, tags, icon, color_scheme, image_url) VALUES
('峰终定律', 'psychology', '体验评价 = 峰值感受 + 结束感受', '诺贝尔奖得主丹尼尔·卡尼曼发现，人们对一段体验的评价主要由两个因素决定：体验过程中的峰值（最好或最坏的时刻）和结束时的感受。这解释了为什么一次旅行的美好回忆往往集中在某几个特殊时刻。', '设计产品时，重点优化关键节点体验，如注册成功页面给用户惊喜，能显著提升整体满意度', 'beginner', 2, '用户体验,认知心理学,产品设计', '🧠', 'blue', 'https://images.unsplash.com/photo-1559757148-5c350d0d3c56?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwc3ljaG9sb2d5JTIwYnJhaW58ZW58MXx8fHwxNzU1OTE2NTczfDA&ixlib=rb-4.1.0&q=80&w=400'),

('包豪斯诞生', 'history', '1919年现代设计运动的起源', '瓦尔特·格罗皮乌斯在德国魏玛创立包豪斯学院，提出"形式追随功能"的设计理念。这所学院培养了一批影响世界的设计师，奠定了现代工业设计的基础，影响至今。', '现代UI设计中的极简主义、功能性优先的设计原则都源于包豪斯理念', 'intermediate', 3, '设计史,现代主义,建筑', '🏛️', 'amber', 'https://images.unsplash.com/photo-1541961017774-22349e4a1262?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxtaW5pbWFsaXN0JTIwYXJjaGl0ZWN0dXJlfGVufDF8fHx8MTc1NTkxNjU3M3ww&ixlib=rb-4.1.0&q=80&w=400'),

('黄金比例', 'aesthetics', '1:1.618的完美比例关系', '黄金比例φ≈1.618，在自然界中广泛存在（如向日葵种子排列、鹦鹉螺壳），被认为是最美的比例。从古希腊帕特农神庙到现代苹果产品，都运用了这一比例。', '版面设计、产品尺寸、界面布局中运用黄金比例能创造视觉和谐感', 'beginner', 2, '美学原理,数学,设计法则', '📐', 'emerald', 'https://images.unsplash.com/photo-1635070041078-e363dbe005cb?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxnb2xkZW4lMjByYXRpbyUyMG5hdHVyZXxlbnwxfHx8fDE3NTU5MTY1NzN8MA&ixlib=rb-4.1.0&q=80&w=400'),

('记忆宫殿法', 'science', '空间记忆 > 抽象记忆', '古希腊诗人西蒙尼德斯发明的记忆技巧，利用熟悉空间的位置来记忆信息。现代神经科学证实，大脑的空间记忆能力远超抽象记忆，这就是为什么我们能记住回家的路却记不住电话号码。', '学习新知识时，将抽象概念与具体场景关联，能大幅提升记忆效果', 'intermediate', 3, '认知科学,学习方法,记忆', '🧭', 'purple', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsaWJyYXJ5JTIwYXJjaGl0ZWN0dXJlfGVufDF8fHx8MTc1NTkxNjU3M3ww&ixlib=rb-4.1.0&q=80&w=400'),

('SCAMPER法', 'innovation', '7个维度激发创意思维', 'SCAMPER是创意思维工具：Substitute(替代)、Combine(结合)、Adapt(适应)、Modify(修改)、Put to other uses(其他用途)、Eliminate(消除)、Reverse(逆向)。通过这7个角度重新审视问题，能产生意想不到的解决方案。', '产品创新、问题解决时，用SCAMPER逐一分析，能系统性地产生创意', 'advanced', 4, '创新方法,设计思维,问题解决', '💡', 'orange', 'https://images.unsplash.com/photo-1553877522-43269d4ea984?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjcmVhdGl2aXR5JTIwaWRlYXxlbnwxfHx8fDE3NTU5MTY1NzN8MA&ixlib=rb-4.1.0&q=80&w=400'),

('色彩心理学', 'psychology', '颜色影响情绪与行为', '不同颜色会激发人们不同的情绪反应和行为倾向。红色激发兴奋和紧迫感，蓝色带来平静和信任感，绿色象征自然和成长。这种反应既有生理基础，也受文化影响。', '在品牌设计、室内装修、产品包装中合理运用色彩，能有效影响用户感受', 'beginner', 2, '色彩理论,心理学,设计应用', '🎨', 'blue', 'https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=400'),

('断舍离哲学', 'aesthetics', '减法生活的智慧', '日本作家山下英子提出的生活哲学：断绝不需要的东西，舍弃多余的物品，脱离对物品的执着。这不仅是整理术，更是一种重新审视人生的方式。', '应用于设计中即是极简主义，去除冗余元素，突出核心功能和美感', 'intermediate', 3, '生活哲学,极简主义,设计思维', '🗃️', 'emerald', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400'),

('费茨定律', 'science', '指向目标的时间与距离和大小有关', '1954年保罗·费茨发现，用手指指向目标所需时间与距离成正比，与目标大小成反比。这一定律后来成为人机交互设计的重要指导原则。', 'UI设计中，重要按钮应该做得更大、放在更近的位置，能显著提升用户操作效率', 'intermediate', 3, '人机交互,用户体验,界面设计', '🎯', 'purple', 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400');