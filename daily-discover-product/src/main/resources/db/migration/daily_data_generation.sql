-- 每日数据生成脚本
-- 为每日发现应用生成当天的数据
-- 执行时间：2025-06-17

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入今日美学箴言
INSERT INTO daily_mottos (text, date, is_active) VALUES
('生活如诗，每一个细节都值得被珍视，今天就从选择美好开始', CURDATE(), TRUE),
('品质生活不需要奢华，只需要用心经营，现在就为自己投资', CURDATE(), TRUE),
('时光荏苒，别让忙碌偷走生活的仪式感，此刻就选择精致', CURDATE(), TRUE),
('每一天都是新的开始，用美好的事物装点生活，从现在开始', CURDATE(), TRUE),
('生活美学在于细节，让每个平凡的日子都充满不平凡的意义', CURDATE(), TRUE);

-- 插入今日推荐商品
INSERT INTO products (name, description, price, image_url, category_id, is_active) VALUES
('晨光序曲·早餐盘', '🌅晨间必备！设计师联名早餐盘，让早餐颜值提升400%，已售3000+件。原价198现价128，今日下单送餐巾，仅剩最后80个！', 128.00, 'https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop', 1, TRUE),
('午后诗篇·书签', '📚文艺必备！手工制作书签，阅读体验提升200%，小红书5万+推荐。特价39元，买3送1，让阅读更有仪式感！', 39.00, 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop', 2, TRUE),
('暮色温柔·浴巾', '🛁沐浴必备！纯棉浴巾，柔软度提升500%，1000+用户5星好评。特价98元，库存告急最后150条，今晚下单送收纳袋！', 98.00, 'https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop', 3, TRUE),
('周末叙事·野餐垫', '🧺户外必备！防水野餐垫，周末出游必备，舒适度提升300%。特价168元，会员专享8折，送收纳包，让周末更精彩！', 168.00, 'https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop', 4, TRUE),
('晨光序曲·闹钟', '⏰起床神器！智能闹钟，改善起床困难90%，晨间幸福感提升。特价188元，限时优惠，送电池，让每个早晨都充满活力！', 188.00, 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop', 1, TRUE),
('午后诗篇·笔记本', '📝办公必备！高级笔记本，书写体验提升150%，职场人士首选。特价68元，买2送1，让工作更有格调！', 68.00, 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop', 2, TRUE),
('暮色温柔·台灯', '💡阅读必备！护眼台灯，减少眼部疲劳80%，学生党必备。特价258元，限时特惠，送灯泡，让夜晚更温馨！', 258.00, 'https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop', 3, TRUE),
('周末叙事·靠垫', '🛋️居家必备！记忆棉靠垫，舒适度提升400%，宅家必备。特价138元，会员专享9折，送靠垫套，让每个周末都舒适！', 138.00, 'https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop', 4, TRUE);

-- 更新商品浏览次数（模拟用户浏览）
UPDATE products SET view_count = view_count + FLOOR(1 + RAND() * 50) WHERE is_active = TRUE;

-- 更新商品点赞次数（模拟用户互动）
UPDATE products SET like_count = like_count + FLOOR(1 + RAND() * 20) WHERE is_active = TRUE;

-- 更新商品收藏次数（模拟用户收藏）
UPDATE products SET favorite_count = favorite_count + FLOOR(1 + RAND() * 15) WHERE is_active = TRUE;

-- 插入新的商品分类（可选）
INSERT INTO product_categories (name, description, sort_order, is_active) VALUES
('季节限定', '当季特别推荐商品，限时发售，错过再等一年', 5, TRUE),
('设计师联名', '知名设计师联名款，独特设计，彰显品味', 6, TRUE);

-- 插入设计师联名商品
INSERT INTO products (name, description, price, image_url, category_id, is_active) VALUES
('季节限定·樱花杯', '🌸春季限定！樱花主题咖啡杯，限量发售1000个，已售800+。原价298现价198，送樱花书签，手慢无！', 198.00, 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop', 5, TRUE),
('设计师联名·艺术画', '🎨艺术家联名！限量版艺术画，提升空间格调300%，收藏价值高。特价588元，送画框，仅剩30幅！', 588.00, 'https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop', 6, TRUE);

-- 显示插入的数据统计
SELECT 
    COUNT(*) as total_mottos,
    SUM(CASE WHEN date = CURDATE() THEN 1 ELSE 0 END) as today_mottos
FROM daily_mottos;

SELECT 
    COUNT(*) as total_products,
    SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) as active_products,
    SUM(CASE WHEN created_at >= CURDATE() THEN 1 ELSE 0 END) as today_products
FROM products;

SELECT 
    COUNT(*) as total_categories,
    SUM(CASE WHEN is_active = TRUE THEN 1 ELSE 0 END) as active_categories
FROM product_categories;

-- 完成提示
SELECT '每日数据生成完成！' as message, CURDATE() as generate_date;