-- 插入今日推荐商品数据
-- 为每日发现页面提供今日推荐商品数据

use daily_discover;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入今日箴言数据
INSERT INTO daily_mottos (text, date, is_active) VALUES
('生活美学，从每一个细节开始，今天就让精致成为你的日常习惯', CURDATE(), TRUE),
('投资自己的幸福，从选择一件真正喜欢的好物开始，你值得拥有最好的', CURDATE(), TRUE),
('忙碌的生活中，别忘了给自己留一份仪式感，让每一天都充满期待', CURDATE(), TRUE);

-- 插入今日推荐商品数据
INSERT INTO products (name, description, price, image_url, category_id, is_active, view_count, like_count, favorite_count) VALUES
('晨光序曲·咖啡杯', '⚡限时特惠！手工陶瓷限量版，原价268现价168，已售2000+件。90%用户反馈清晨幸福感提升，今日下单送专属杯垫，仅剩最后50个！', 168.00, 'https://img.freepik.com/free-photo/top-view-coffee-cup-with-donut_23-2149396433.jpg', 1, TRUE, 1250, 89, 67),
('午后诗篇·抱枕', '🔥爆款热卖！明星同款亚麻抱枕，小红书10万+推荐，柔软度提升300%。限时优惠129元，买2送1，朋友都在问的链接，立即抢购！', 129.00, 'https://img.freepik.com/free-photo/white-pillow-sofa_53876-138615.jpg', 2, TRUE, 2100, 156, 98),
('暮色温柔·香薰', '💖治愈必备！天然植物香薰，1000+用户5星好评，缓解焦虑效果显著。特价88元，库存告急最后100瓶，今晚下单送精美礼盒！', 88.00, 'https://img.freepik.com/free-photo/candles-with-aromatic-oils_23-2149396432.jpg', 3, TRUE, 980, 134, 76),
('晨光序曲·花瓶', '✨设计师联名！北欧极简花瓶，ins风家居必备，提升空间格调200%。原价286现价186，限量发售，送干花束，手慢无！', 186.00, 'https://img.freepik.com/free-photo/vase-with-flowers-living-room-interior_53876-138616.jpg', 1, TRUE, 760, 45, 32),
('周末叙事·毛毯', '🏆品质之选！100%羊毛毛毯，温暖度提升400%，周末宅家必备。特价268元，会员专享9折，送收纳袋，让每个周末都温暖！', 268.00, 'https://img.freepik.com/free-photo/blanket-sofa_53876-138617.jpg', 4, TRUE, 1450, 98, 54),
('午后诗篇·茶具', '🍵茶艺师推荐！天然木纹茶具，茶香提升150%，午后仪式感必备。228元限时优惠，送茶叶试喝装，朋友都说有品位！', 228.00, 'https://img.freepik.com/free-photo/tea-set-wooden-table_53876-138618.jpg', 2, TRUE, 890, 67, 43),
('晨光序曲·桌布', '🌟餐桌美学！棉麻桌布，让早餐颜值提升300%，家人都夸有格调。158元特价，买即送餐巾，改变从早餐开始！', 158.00, 'https://img.freepik.com/free-photo/tablecloth-dining-table_53876-138619.jpg', 1, TRUE, 670, 34, 28),
('暮色温柔·夜灯', '💡睡眠神器！智能调光夜灯，改善睡眠质量85%，都市人必备。298元限时特惠，送香薰精油，今晚就给自己最好的！', 298.00, 'https://img.freepik.com/free-photo/night-lamp-bedroom_53876-138620.jpg', 3, TRUE, 1120, 78, 61);

-- 显示插入的数据统计
SELECT 
    COUNT(*) as total_products,
    SUM(view_count) as total_views,
    SUM(like_count) as total_likes,
    SUM(favorite_count) as total_favorites
FROM products 
WHERE is_active = TRUE
AND created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-%d 00:00:00')
AND created_at <= DATE_FORMAT(CURDATE(), '%Y-%m-%d 23:59:59');

SELECT 
    COUNT(*) as total_mottos
FROM daily_mottos 
WHERE is_active = TRUE
AND date = CURDATE();

-- 输出执行完成信息
SELECT '今日推荐商品数据插入完成！' as message;