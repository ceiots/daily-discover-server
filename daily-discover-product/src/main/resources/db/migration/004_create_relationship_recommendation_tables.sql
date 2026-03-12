-- ============================================
-- 商品关系与推荐表结构
-- 创建时间: 2026-02-04
-- 业务模块: 商品关系与推荐
-- ============================================

USE daily_discover;

-- 删除表（便于可重复执行）
DROP TABLE IF EXISTS product_search_keywords;
DROP TABLE IF EXISTS user_intent_preferences;
DROP TABLE IF EXISTS product_sales_stats;
DROP TABLE IF EXISTS scene_product_relation;
DROP TABLE IF EXISTS scene_recommendations;


-- ============================================
-- 销量统计表（单一表设计，主流电商最佳实践）
-- ============================================

-- 销量统计表（支持多种时间粒度）
CREATE TABLE IF NOT EXISTS product_sales_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '统计ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    
    -- 时间粒度
    time_granularity VARCHAR(10) NOT NULL COMMENT '时间粒度',
    stat_date DATE NOT NULL COMMENT '统计日期（如：日粒度-2026-02-01，月粒度-2026-02-01，年粒度-2026-01-01）',
    
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


-- 用户意图偏好表（核心意图，不带点击记录）
CREATE TABLE IF NOT EXISTS user_intent_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '意图偏好ID',
    user_id BIGINT COMMENT '用户ID（NULL表示通用意图）',
    intent_type VARCHAR(30) NOT NULL COMMENT '意图类型：cheap-便宜, quality-高质量, new-新品, popular-热门, personalized-个性化, seasonal-季节',
    intent_label VARCHAR(200) NOT NULL COMMENT '具体意图词（如"便宜""耐高温水杯"，补充intent_type的精细化描述）',
    preference_weight DOUBLE NOT NULL DEFAULT 0.5 COMMENT '偏好权重（0.0~1.0）：1.0表示强偏好，0.1表示弱偏好',
    usage_count INT DEFAULT 1 COMMENT '使用次数',
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '最后使用时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 索引优化
    INDEX idx_user_intent (user_id, intent_type) COMMENT '用户意图查询',
    INDEX idx_intent_type (intent_type) COMMENT '意图类型查询',
    INDEX idx_weight (preference_weight) COMMENT '权重查询',
    INDEX idx_usage (usage_count) COMMENT '使用次数查询',
    -- 唯一约束：支持ON DUPLICATE KEY UPDATE操作
    UNIQUE KEY uk_user_intent_label (user_id, intent_type, intent_label) COMMENT '用户意图词唯一约束'
) COMMENT '用户意图偏好表';

-- 场景推荐表（基于日期+时段的精准设计）
CREATE TABLE IF NOT EXISTS scene_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '场景ID',
    target_date DATE NOT NULL COMMENT '场景对应日期（如2026-03-15，支持365天精准配置）',
    time_period VARCHAR(20) NOT NULL COMMENT '时段：morning(早晨)/afternoon(下午)/evening(晚上)',
    
    -- 场景核心文案（人工运营填写，保证走心）
    scene_sentence VARCHAR(200) NOT NULL COMMENT '场景核心句子（如"清晨温柔一点，在阳台喝一杯温水"）',
    scene_subtitle VARCHAR(200) COMMENT '场景辅助文案（如"用一杯温水，唤醒身体的温柔"）',
    cover_image VARCHAR(500) COMMENT '场景背景图URL',
    
    -- 管理字段
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 核心索引：保证"日期+时段"查询效率
    UNIQUE KEY uk_date_period (target_date, time_period)
) COMMENT '场景主表（按日期+时段存储场景）';

-- 场景-商品关联表（绑定时段场景的商品）
CREATE TABLE IF NOT EXISTS scene_product_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    scene_id BIGINT NOT NULL COMMENT '关联场景主表ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_reason VARCHAR(200) NOT NULL COMMENT '商品适配场景的理由（走心描述）',
    display_order TINYINT NOT NULL COMMENT '展示顺序：1/2/3',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_scene_product (scene_id, product_id),
    INDEX idx_scene_order (scene_id, display_order)
) COMMENT '场景-商品关联表（绑定时段场景的商品）';



-- 商品搜索关键词表
CREATE TABLE IF NOT EXISTS product_search_keywords (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关键词ID',
    keyword VARCHAR(200) NOT NULL COMMENT '搜索关键词',
    search_count INT DEFAULT 1 COMMENT '搜索次数',
    click_count INT DEFAULT 0 COMMENT '点击次数',
    conversion_count INT DEFAULT 0 COMMENT '转化次数',
    last_searched_at TIMESTAMP NULL COMMENT '最后搜索时间',
    is_trending BOOLEAN DEFAULT false COMMENT '是否热门',
    is_recommended BOOLEAN DEFAULT false COMMENT '是否推荐',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_keyword (keyword),
    INDEX idx_search_count (search_count),
    INDEX idx_click_count (click_count),
    INDEX idx_is_trending (is_trending),
    INDEX idx_is_recommended (is_recommended)
) COMMENT '商品搜索关键词表';





-- 插入用户意图偏好测试数据
INSERT INTO user_intent_preferences (user_id, intent_type, intent_label, preference_weight, usage_count) VALUES
-- 用户1的意图偏好
(1, 'cheap', '便宜', 0.9, 5),
(1, 'quality', '高质量', 0.8, 3),
(1, 'new', '新品', 0.7, 2),
(1, 'popular', '热门', 0.6, 4),
-- 通用意图偏好（user_id为NULL）
(NULL, 'cheap', '便宜', 0.8, 10),
(NULL, 'quality', '高质量', 0.7, 8),
(NULL, 'new', '新品', 0.6, 6),
(NULL, 'popular', '热门', 0.5, 12),
(NULL, 'personalized', '个性化', 0.4, 3),
(NULL, 'seasonal', '季节', 0.3, 2);

-- 插入场景推荐数据（基于日期+时段的精准设计）
INSERT INTO scene_recommendations (
    target_date, time_period, scene_sentence, scene_subtitle, cover_image, is_active
) VALUES
-- ==================== 通用场景推荐 ====================
-- 工作日早晨场景
('2026-03-15', 'morning', 
'清晨温柔一点，在阳台喝一杯温水', 
'用一杯温水，唤醒身体的温柔', 
'https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=800', 
true),

-- 工作日午后场景
('2026-03-15', 'afternoon', 
'午后阳光正好，给自己15分钟的阅读时光', 
'在忙碌中寻找片刻宁静，让心灵得到滋养', 
'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800', 
true),

-- 工作日晚上场景
('2026-03-15', 'evening', 
'夜晚来临，点一盏温暖的灯，享受独处时光', 
'在安静中与自己对话，感受内心的平静', 
'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=800', 
true),

-- 周末早晨场景
('2026-03-16', 'morning', 
'周末的早晨，慢悠悠地准备一顿丰盛早餐', 
'用美食开启美好周末，享受生活的仪式感', 
'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800', 
true),

-- 周末午后场景
('2026-03-16', 'afternoon', 
'午后时光，约上好友一起喝杯下午茶', 
'在谈笑风生中，感受友谊的温暖', 
'https://images.unsplash.com/photo-1515823064-d6e0c04616a7?w=800', 
true),

-- 周末晚上场景
('2026-03-16', 'evening', 
'周末夜晚，看一部治愈系电影放松身心', 
'让光影故事带走一周的疲惫', 
'https://images.unsplash.com/photo-1489599809519-364a47ae3cde?w=800', 
true),

-- 春季早晨场景
('2026-03-20', 'morning', 
'春日清晨，打开窗户迎接第一缕阳光', 
'让清新的空气和温暖的阳光唤醒新的一天', 
'https://images.unsplash.com/photo-1497435334941-8c899ee9e8e9?w=800', 
true),

-- 夏季午后场景
('2026-06-15', 'afternoon', 
'夏日午后，在树荫下享受清凉的微风', 
'避开炎炎烈日，寻找片刻的清凉与宁静', 
'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800', 
true),

-- 秋季晚上场景
('2026-09-20', 'evening', 
'秋夜微凉，泡一杯热茶温暖身心', 
'在渐浓的秋意中，感受时光的温柔流转', 
'https://images.unsplash.com/photo-1571934811356-5cc061b6821f?w=800', 
true),

-- 冬季早晨场景
('2026-12-15', 'morning', 
'冬日早晨，用温暖的早餐开启一天', 
'在寒冷季节里，用美食给自己一份温暖', 
'https://images.unsplash.com/photo-1482049016688-2d3e1b311543?w=800', 
true),

-- ==================== 近一周数据（2026-03-12 ~ 2026-03-13） ====================
-- 2026-03-12 早晨场景
('2026-03-12', 'morning', 
'周三清晨，给自己一个温柔的起床仪式', 
'用小小的仪式感，开启充满能量的一天', 
'https://images.unsplash.com/photo-1542838132-92c53300491e?w=800', 
true),

-- 2026-03-12 午后场景
('2026-03-12', 'afternoon', 
'午后小憩，让忙碌的思绪暂时停歇', 
'在工作的间隙，给自己15分钟的放松时光', 
'https://images.unsplash.com/photo-1519681393784-d120267933ba?w=800', 
true),

-- 2026-03-12 晚上场景
('2026-03-12', 'evening', 
'周三夜晚，为明天的自己做好准备', 
'提前规划，让明天的工作更加从容', 
'https://images.unsplash.com/photo-1513475382585-d06e58bcb0e0?w=800', 
true),

-- 2026-03-13 早晨场景
('2026-03-13', 'morning', 
'周四早晨，用一杯热茶唤醒身心', 
'在茶香中，感受新一天的开始', 
'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800', 
true),

-- 2026-03-13 午后场景
('2026-03-13', 'afternoon', 
'午后时光，整理一周的工作收获', 
'在总结中，发现自己的成长与进步', 
'https://images.unsplash.com/photo-1499750310107-5fef28a66643?w=800', 
true),

-- 2026-03-13 晚上场景
('2026-03-13', 'evening', 
'周四夜晚，为周末的到来做好准备', 
'提前规划，让周末更加充实有意义', 
'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800', 
true);

-- 3. 社区热榜数据（销量统计）
INSERT INTO product_sales_stats (product_id, time_granularity, stat_date, `rank`, sales_count, sales_amount, view_count, favorite_count, avg_rating) VALUES
(1, 'daily', CURDATE(), 1, 150, 75000.00, 3000, 120, 4.8),
(2, 'daily', CURDATE(), 2, 120, 48000.00, 2500, 95, 4.7),
(3, 'daily', CURDATE(), 3, 100, 60000.00, 2200, 85, 4.6),
(4, 'daily', CURDATE(), 4, 90, 45000.00, 2000, 75, 4.5),
(5, 'daily', CURDATE(), 5, 80, 32000.00, 1800, 65, 4.4),
(6, 'daily', CURDATE(), 6, 70, 28000.00, 1600, 55, 4.3);





-- 插入销量统计数据（单一表设计）
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



-- 插入商品搜索关键词数据
INSERT INTO product_search_keywords (keyword, search_count, click_count, conversion_count, last_searched_at, is_trending, is_recommended) VALUES
('智能手表', 1500, 800, 120, '2026-02-01 15:30:00', true, true),
('无线耳机', 1200, 650, 95, '2026-02-01 14:20:00', true, true),
('笔记本电脑', 1800, 900, 150, '2026-02-01 16:45:00', true, false),
('智能手机', 2000, 1000, 180, '2026-02-01 17:10:00', true, false),
('男士衬衫', 800, 400, 60, '2026-02-01 11:30:00', false, false);


-- 为user_id 4添加用户行为日志
INSERT INTO user_behavior_logs_core (user_id, product_id, behavior_type, behavior_weight, session_id) VALUES
(4, 16, 'view', 1.0, 'session_4_001'),
(4, 17, 'view', 1.0, 'session_4_001'),
(4, 18, 'view', 1.0, 'session_4_001'),
(4, 19, 'view', 1.0, 'session_4_001'),
(4, 20, 'view', 1.0, 'session_4_001'),
(4, 16, 'add_to_cart', 2.0, 'session_4_001'),
(4, 17, 'add_to_cart', 2.0, 'session_4_001'),
(4, 18, 'add_to_cart', 2.0, 'session_4_001'),
(4, 19, 'add_to_cart', 2.0, 'session_4_001'),
(4, 20, 'add_to_cart', 2.0, 'session_4_001'),

-- 更多行为数据
(4, 21, 'view', 1.0, 'session_4_002'),
(4, 22, 'view', 1.0, 'session_4_002'),
(4, 23, 'view', 1.0, 'session_4_002'),
(4, 24, 'view', 1.0, 'session_4_002'),
(4, 25, 'view', 1.0, 'session_4_002'),
(4, 21, 'add_to_cart', 2.0, 'session_4_002'),
(4, 22, 'add_to_cart', 2.0, 'session_4_002'),
(4, 23, 'add_to_cart', 2.0, 'session_4_002'),
(4, 24, 'add_to_cart', 2.0, 'session_4_002'),
(4, 25, 'add_to_cart', 2.0, 'session_4_002');



-- 插入场景商品关联测试数据（走心描述）
INSERT INTO scene_product_relation (scene_id, product_id, product_reason, display_order, is_active) VALUES
-- 工作日早晨场景（场景ID=1）的商品关联
(1, 1, '清晨喝温水时，用这款保温杯可以保持水温恰到好处', 1, true),
(1, 2, '阳台时光需要舒适坐垫，让晨间放松更惬意', 2, true),
(1, 3, '轻柔的音乐伴随清晨，这款小音箱音质纯净', 3, true),

-- 工作日午后场景（场景ID=2）的商品关联
(2, 4, '午后阅读时光，这款舒适的阅读椅让身心放松', 1, true),
(2, 5, '纸质书的手感无法替代，这款书签让阅读更有仪式感', 2, true),
(2, 6, '阅读时配一杯香茶，这款茶具让品茶更有韵味', 3, true),

-- 工作日晚上场景（场景ID=3）的商品关联
(3, 7, '温暖的灯光营造宁静氛围，这款台灯光线柔和', 1, true),
(3, 8, '独处时点一支香薰，让心灵得到深度放松', 2, true),
(3, 9, '晚间静思时，这款笔记本记录灵感瞬间', 3, true),

-- 周末早晨场景（场景ID=4）的商品关联
(4, 10, '周末早餐需要精致的餐具，让美食更有仪式感', 1, true),
(4, 11, '新鲜食材需要保鲜，这款保鲜盒保持食物原味', 2, true),
(4, 12, '早餐配一杯现磨咖啡，这款咖啡机操作简单', 3, true),

-- 周末午后场景（场景ID=5）的商品关联
(5, 13, '下午茶时光，这款茶具套装让聚会更有格调', 1, true),
(5, 14, '与好友分享甜点，这款点心盘精致实用', 2, true),
(5, 15, '聊天时播放轻音乐，这款蓝牙音箱音质出色', 3, true),

-- 周末晚上场景（场景ID=6）的商品关联
(6, 16, '家庭影院体验，这款投影仪画质清晰', 1, true),
(6, 17, '观影时准备零食，这款零食盘方便实用', 2, true),
(6, 18, '营造影院氛围，这款星空灯效果浪漫', 3, true),

-- 春季早晨场景（场景ID=7）的商品关联
(7, 19, '春日清晨开窗通风，这款空气净化器保持空气清新', 1, true),
(7, 20, '迎接阳光，这款窗帘透光性好又保护隐私', 2, true),
(7, 21, '春日养花，这款花盆设计简约美观', 3, true),

-- 夏季午后场景（场景ID=8）的商品关联
(8, 22, '夏日树荫下乘凉，这款便携风扇风力柔和', 1, true),
(8, 23, '避暑时喝冷饮，这款保温杯保冷效果佳', 2, true),
(8, 24, '户外休息时，这款折叠椅轻便易携带', 3, true),

-- 秋季晚上场景（场景ID=9）的商品关联
(9, 25, '秋夜泡热茶，这款茶壶保温性能好', 1, true),
(9, 1, '微凉夜晚需要毛毯，这款毯子柔软舒适', 2, true),
(9, 2, '秋夜读书，这款阅读灯保护视力', 3, true),

-- 冬季早晨场景（场景ID=10）的商品关联
(10, 3, '冬日早餐需要保温，这款电饭煲预约功能实用', 1, true),
(10, 4, '寒冷早晨喝热饮，这款马克杯保温效果好', 2, true),
(10, 5, '冬季室内干燥，这款加湿器保持湿度适宜', 3, true),

-- ==================== 近一周数据（2026-03-12 ~ 2026-03-13） ====================
-- 2026-03-12 早晨场景（场景ID=11）的商品关联
(11, 26, '清晨起床仪式需要香薰，这款香薰机雾化细腻', 1, true),
(11, 27, '温柔的早晨需要舒适睡衣，这款睡衣材质柔软', 2, true),
(11, 28, '起床后需要补水，这款保温杯保持水温适宜', 3, true),

-- 2026-03-12 午后场景（场景ID=12）的商品关联
(12, 29, '午后小憩需要眼罩，这款眼罩遮光效果好', 1, true),
(12, 30, '放松时听轻音乐，这款蓝牙耳机音质纯净', 2, true),
(12, 31, '小憩后需要提神，这款茶叶香气清新', 3, true),

-- 2026-03-12 晚上场景（场景ID=13）的商品关联
(13, 32, '晚间规划需要笔记本，这款笔记本纸张质感好', 1, true),
(13, 33, '规划时点一盏灯，这款台灯光线柔和护眼', 2, true),
(13, 34, '为明天准备早餐，这款保鲜盒保持食物新鲜', 3, true),

-- 2026-03-13 早晨场景（场景ID=14）的商品关联
(14, 35, '早晨泡茶需要茶具，这款茶壶设计简约', 1, true),
(14, 36, '茶香需要好茶叶，这款茶叶口感醇厚', 2, true),
(14, 37, '喝茶时配点心，这款点心盘精致实用', 3, true),

-- 2026-03-13 午后场景（场景ID=15）的商品关联
(15, 38, '整理工作收获需要文件夹，这款文件夹分类清晰', 1, true),
(15, 39, '记录成长需要笔记本，这款笔记本书写流畅', 2, true),
(15, 40, '总结时喝杯咖啡，这款咖啡机操作简单', 3, true),

-- 2026-03-13 晚上场景（场景ID=16）的商品关联
(16, 41, '周末规划需要日历，这款日历设计美观', 1, true),
(16, 42, '规划活动需要便签，这款便签粘贴牢固', 2, true),
(16, 43, '为周末准备食材，这款保鲜盒密封性好', 3, true);

