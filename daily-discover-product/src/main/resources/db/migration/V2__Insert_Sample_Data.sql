-- 插入晨间仪式商品数据
INSERT INTO products (title, price, original_price, description, image_url, time_slot, category, tag, reason) VALUES
('智能咖啡机', 1299.00, 1599.00, '一键制作专业级咖啡，支持多种口味选择', 'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400', 'morning', '家电', '智能推荐', '为新的一天注入活力能量'),
('早餐谷物套装', 89.90, 128.00, '营养均衡的有机谷物，开启活力一天', 'https://images.unsplash.com/photo-1517673132405-a56a62b18caf?w=400', 'morning', '食品', '健康晨启', '均衡营养，保持最佳状态'),
('瑜伽垫', 158.00, 228.00, '防滑环保材质，适合晨间瑜伽练习', 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400', 'morning', '运动', '晨练必备', '在宁静中开启美好一天'),
('智能手环', 299.00, 399.00, '监测睡眠质量，记录晨间运动数据', 'https://images.unsplash.com/photo-1557935728-e6d1eaabe729?w=400', 'morning', '数码', '健康监测', '科技守护健康生活');

-- 插入午间专注商品数据
INSERT INTO products (title, price, original_price, description, image_url, time_slot, category, tag, reason) VALUES
('降噪耳机', 899.00, 1299.00, '主动降噪技术，专注工作不被打扰', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400', 'noon', '数码', '工作伴侣', '在忙碌中寻找片刻宁静'),
('办公椅', 1299.00, 1899.00, '人体工学设计，久坐不累', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400', 'noon', '家具', '舒适办公', '提升工作效率的必备装备'),
('绿植盆栽', 68.00, 98.00, '净化空气，缓解工作压力', 'https://images.unsplash.com/photo-1485955900006-10b4d6d612bf?w=400', 'noon', '装饰', '办公绿植', '为工作空间增添生机'),
('保温杯', 128.00, 188.00, '12小时保温，随时享用热饮', 'https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400', 'noon', '生活', '温暖陪伴', '为忙碌的工作提供温暖');

-- 插入午后休闲商品数据
INSERT INTO products (title, price, original_price, description, image_url, time_slot, category, tag, reason) VALUES
('蓝牙音箱', 399.00, 599.00, '高音质立体声，享受午后音乐时光', 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400', 'afternoon', '数码', '下午茶时光', '在忙碌中寻找片刻宁静'),
('茶具套装', 268.00, 388.00, '精致陶瓷茶具，品茶放松心情', 'https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=400', 'afternoon', '茶具', '匠心之作', '温暖的下午需要甜蜜相伴'),
('阅读灯', 189.00, 268.00, '护眼LED光源，适合午后阅读', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400', 'afternoon', '照明', '护眼阅读', '为阅读时光提供舒适光线'),
('香薰机', 158.00, 228.00, '超声波雾化，营造舒适环境', 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400', 'afternoon', '家居', '居家氛围', '营造温馨的休息环境');

-- 插入夜晚/周末商品数据
INSERT INTO products (title, price, original_price, description, image_url, time_slot, category, tag, reason) VALUES
('投影仪', 2999.00, 3999.00, '家庭影院级体验，周末观影首选', 'https://images.unsplash.com/photo-1485846234645-a62644f84728?w=400', 'evening', '数码', '家庭影院', '为周末时光增添娱乐乐趣'),
('红酒套装', 388.00, 588.00, '精选进口红酒，周末小酌佳品', 'https://images.unsplash.com/photo-1566411520896-01e7ca472647?w=400', 'evening', '酒类', '品味生活', '在宁静夜晚享受美好时光'),
('按摩仪', 599.00, 888.00, '缓解疲劳，放松身心', 'https://images.unsplash.com/photo-1599420186946-7b6fb4e297f0?w=400', 'evening', '保健', '夜间护理', '在夜晚中呵护身心健康'),
('香薰蜡烛', 88.00, 128.00, '天然植物蜡，营造温馨氛围', 'https://images.unsplash.com/photo-1518717758536-85ae29035b6d?w=400', 'evening', '装饰', '居家氛围', '营造温馨的夜晚氛围');