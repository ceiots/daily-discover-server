-- 更新部分商品为热销商品
UPDATE products SET is_hot_sale = true WHERE id IN (1, 5, 13, 21);

-- 更新部分商品为高质量商品
UPDATE products SET is_high_quality = true WHERE id IN (2, 6, 14, 22);

-- 更新部分商品为快速配送商品
UPDATE products SET is_fast_delivery = true WHERE id IN (3, 7, 15, 23);

-- 添加商品特性描述
UPDATE products SET features = '一键制作，15种口味，智能温控，APP远程操作' WHERE id = 1;
UPDATE products SET features = '有机谷物，无添加剂，高纤维，富含蛋白质' WHERE id = 2;
UPDATE products SET features = '环保材质，防滑设计，易清洁，便携收纳' WHERE id = 3;
UPDATE products SET features = '24小时监测，防水设计，长续航，多运动模式' WHERE id = 4;
UPDATE products SET features = '主动降噪，环境音，40小时续航，快速充电' WHERE id = 5;
UPDATE products SET features = '人体工学，可调节高度，透气网布，承重150kg' WHERE id = 6;
UPDATE products SET features = '净化空气，易养护，观赏价值高，提升幸福感' WHERE id = 7;
UPDATE products SET features = '304不锈钢，双层真空，12小时保温，防漏设计' WHERE id = 8;
UPDATE products SET features = '360°环绕音，防水设计，10小时续航，便携' WHERE id = 9;
UPDATE products SET features = '高级陶瓷，精致做工，保温效果好，送礼佳品' WHERE id = 10;
UPDATE products SET features = '三档亮度，触控开关，USB充电，折叠便携' WHERE id = 11;
UPDATE products SET features = '超声波雾化，七彩灯光，定时功能，静音设计' WHERE id = 12;
UPDATE products SET features = '1080P高清，自动对焦，内置音响，无线投屏' WHERE id = 13;
UPDATE products SET features = '法国进口，橡木桶陈酿，果香浓郁，送礼佳品' WHERE id = 14;
UPDATE products SET features = '多功能按摩，热敷功能，智能定时，静音设计' WHERE id = 15;
UPDATE products SET features = '天然植物蜡，多种香型，长效燃烧，环保安全' WHERE id = 16;