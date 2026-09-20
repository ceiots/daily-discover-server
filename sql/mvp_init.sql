-- =====================================================================
-- 每日发现 MVP · PostgreSQL 初始化脚本
-- 依据文档：01 Data-Model-MVP.md
--
-- 目标库：PostgreSQL（192.168.1.63:5432/daily_discover）
-- 执行方式：
--   PGPASSWORD='postgres' psql -h 192.168.1.63 -p 5432 -U postgres \
--     -d daily_discover -f sql/mvp_init.sql
--
-- 设计要点：
--   * 主键统一 BIGSERIAL（MVP 不引入 UUID / Snowflake）
--   * 时间统一 TIMESTAMPTZ，服务端统一保存 UTC
--   * 不使用数据库外键，关联完整性由 Spring Boot 业务代码保证
--   * 扩展信息使用 JSONB
-- =====================================================================

-- =====================================================================
-- 1. users｜匿名用户
-- =====================================================================
CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY,
    anonymous_id  VARCHAR(64) NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_anonymous_id
    ON users (anonymous_id);

-- =====================================================================
-- 2. discoveries｜每日发现（MVP 最核心数据）
--    status: DRAFT / PUBLISHED / OFFLINE
-- =====================================================================
CREATE TABLE IF NOT EXISTS discoveries (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(200)  NOT NULL,
    scene         TEXT,
    reason        TEXT,
    suitable_for  TEXT,
    price         NUMERIC(10,2),
    cover_url     TEXT,
    action_title  VARCHAR(100),
    action_url    TEXT,
    status        VARCHAR(32)   NOT NULL DEFAULT 'DRAFT',
    priority      INT           NOT NULL DEFAULT 0,
    published_at  TIMESTAMPTZ,
    expires_at    TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_discoveries_status_time_priority
    ON discoveries (status, published_at, expires_at, priority);

-- =====================================================================
-- 3. products｜商品
--    status: ACTIVE / INACTIVE
-- =====================================================================
CREATE TABLE IF NOT EXISTS products (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(200)  NOT NULL,
    description   TEXT,
    price         NUMERIC(10,2),
    image_url     TEXT,
    purchase_url  TEXT,
    platform      VARCHAR(32),
    status        VARCHAR(32)   NOT NULL DEFAULT 'ACTIVE',
    metadata      JSONB,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =====================================================================
-- 4. discovery_products｜发现与商品（N ─── N，只保存业务 ID，不建外键）
-- =====================================================================
CREATE TABLE IF NOT EXISTS discovery_products (
    discovery_id  BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    quantity      INT    NOT NULL DEFAULT 1,
    sort_order    INT    NOT NULL DEFAULT 0,
    PRIMARY KEY (discovery_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_discovery_products_product_id
    ON discovery_products (product_id);

-- =====================================================================
-- 5. behaviors｜用户行为（含用户判断，MVP 不单独建 Feedback 表）
--    behavior_type: IMPRESSION / DETAIL_VIEW / INTERESTED /
--                   NOT_INTERESTED / SKIP / ACTION_CLICK
-- =====================================================================
CREATE TABLE IF NOT EXISTS behaviors (
    id            BIGSERIAL PRIMARY KEY,
    anonymous_id  VARCHAR(64) NOT NULL,
    discovery_id  BIGINT      NOT NULL,
    behavior_type VARCHAR(32) NOT NULL,
    metadata      JSONB,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_behaviors_user_time
    ON behaviors (anonymous_id, created_at);

CREATE INDEX IF NOT EXISTS idx_behaviors_discovery_type_time
    ON behaviors (discovery_id, behavior_type, created_at);

-- =====================================================================
-- 6. MVP 初始化数据（10 条真实发现 + 对应商品）
-- =====================================================================
TRUNCATE discovery_products, behaviors, products, discoveries RESTART IDENTITY;

INSERT INTO discoveries
    (name, scene, reason, suitable_for, price, cover_url, action_title, action_url, status, priority, published_at, expires_at)
VALUES
('把阳台变成一个适合晚饭后放松的小空间',
 '下班后想在家放松，但阳台一直没有被真正使用起来。',
 '用少量预算增加灯光、桌椅和收纳，就能把原本闲置的阳台变成每天都能使用的休闲空间。',
 '有阳台、希望增加居家休闲空间的人。',
 69.00, 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80', '查看搭配', 'https://example.com/action/001', 'PUBLISHED', 90,
 NOW() - INTERVAL '1 hour', NULL),

('给通勤路上做一次极简整理',
 '每天通勤包里东西又多又乱，出门总在找钥匙和耳机。',
 '用几个小收纳件就能让通勤包井井有条，每天出门更快、更从容。',
 '通勤时间长、背包杂乱的人。',
 45.00, 'https://images.unsplash.com/photo-1547949003-9792a18a2601?auto=format&fit=crop&w=800&q=80', '看看清单', 'https://example.com/action/002', 'PUBLISHED', 80,
 NOW() - INTERVAL '2 hour', NULL),

('周末近郊一日露营装备入门',
 '想去郊外放松一天，但不知道需要准备哪些基础装备。',
 '一套基础露营装备不到三百元，就能支撑一次完整的近郊一日露营体验。',
 '想尝试露营但还没入门的新手。',
 289.00, 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&w=800&q=80', '查看装备单', 'https://example.com/action/003', 'PUBLISHED', 70,
 NOW() - INTERVAL '3 hour', NULL),

('把书桌改造成高效率办公角',
 '在家办公总是效率低，桌面物品堆积注意力分散。',
 '调整桌面布局并增加支架和灯光，专注度和舒适度都会明显提升。',
 '需要长时间伏案或在家办公的人。',
 129.00, 'https://images.unsplash.com/photo-1547082299-de196ea013d6?auto=format&fit=crop&w=800&q=80', '看改造方案', 'https://example.com/action/004', 'PUBLISHED', 60,
 NOW() - INTERVAL '4 hour', NULL),

('夏天到了，做一次基础护肤简化',
 '瓶瓶罐罐一大堆，真正适合自己的护肤品其实很少。',
 '简化到清洁、保湿、防晒三步，省钱省时间，皮肤状态反而更稳定。',
 '护肤流程繁琐、想精简步骤的人。',
 99.00, 'https://images.unsplash.com/photo-1570172619644-dfd03ed5d881?auto=format&fit=crop&w=800&q=80', '看精简方案', 'https://example.com/action/005', 'PUBLISHED', 50,
 NOW() - INTERVAL '5 hour', NULL),

('在家做一杯接近咖啡馆品质的手冲咖啡',
 '每天一杯咖啡馆咖啡开销不小，想在家复刻这个仪式感。',
 '一套入门手冲器具加新鲜豆子，单杯成本不到十元，风味却很接近。',
 '每天喝咖啡、想省钱又不将就的人。',
 158.00, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=800&q=80', '看入门指南', 'https://example.com/action/006', 'PUBLISHED', 40,
 NOW() - INTERVAL '6 hour', NULL),

('新手养猫的第一份必备清单',
 '刚决定养猫，但完全不清楚需要先准备什么。',
 '先备齐猫粮、猫砂盆等六件基础用品，接猫回家不慌乱。',
 '准备接猫回家的新手。',
 88.00, 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80', '看必备清单', 'https://example.com/action/007', 'PUBLISHED', 30,
 NOW() - INTERVAL '7 hour', NULL),

('雨天通勤不湿鞋的小方案',
 '下雨天鞋子进水、地板打滑，通勤体验很差。',
 '一双防滑鞋套加一把折叠伞，几十元解决整个雨季的通勤烦恼。',
 '步行或骑车通勤的上班族。',
 25.90, 'https://images.unsplash.com/photo-1428592953211-077101b2021b?auto=format&fit=crop&w=800&q=80', '看雨天方案', 'https://example.com/action/008', 'PUBLISHED', 20,
 NOW() - INTERVAL '8 hour', NOW() + INTERVAL '7 day'),

('十分钟搞定快手早餐',
 '早上没时间做早餐，长期外卖或干脆不吃。',
 '一口小电锅加上提前备料，每天十分钟就能吃上热乎早餐。',
 '上班族与学生党。',
 79.00, 'https://images.unsplash.com/photo-1525351484163-7529414344d8?auto=format&fit=crop&w=800&q=80', '看食谱', 'https://example.com/action/009', 'PUBLISHED', 10,
 NOW() - INTERVAL '9 hour', NULL),

('居家轻健身：从一张瑜伽垫开始',
 '想去健身但没时间，家里其实也可以练起来。',
 '一张瑜伽垫加一条弹力带，就能完成一套完整的居家训练。',
 '想开始运动但没有健身习惯的人。',
 109.00, 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=800&q=80', '看训练计划', 'https://example.com/action/010', 'PUBLISHED', 0,
 NOW() - INTERVAL '10 hour', NULL),

('一条已下线的发现（不应展示）',
 '下线内容。', '下线内容。', '无。',
 NULL, NULL, NULL, NULL, 'OFFLINE', 100,
 NOW() - INTERVAL '1 day', NULL);

INSERT INTO products (name, description, price, image_url, purchase_url, platform, status, metadata) VALUES
('暖光露营灯',     '适合阳台和室内使用的小型暖光灯。',           39.00,  'https://images.unsplash.com/photo-1478131143081-80f7f84ca84d?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/001', 'taobao', 'ACTIVE', '{"tags": ["阳台", "灯光"]}'),
('折叠小桌',       '轻便可折叠，收纳后不占空间。',               59.00,  'https://images.unsplash.com/photo-1526307616774-60d0098f7642?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/002', 'jd',     'ACTIVE', '{"tags": ["阳台", "家具"]}'),
('防水收纳箱',     '阳台或室内两用，防潮防尘。',                 29.00,  'https://images.unsplash.com/photo-1556228453-efd6c1ff04f6?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/003', 'pdd',    'ACTIVE', '{"tags": ["收纳"]}'),
('通勤收纳包',     '多分区设计，钥匙耳机卡包各有位置。',         45.00,  'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/004', 'taobao', 'ACTIVE', '{"tags": ["通勤"]}'),
('便携收纳袋套装', '三种尺寸组合，包内整理一步到位。',           19.90,  'https://images.unsplash.com/photo-1584824486509-112e4181ff6b?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/005', 'pdd',    'ACTIVE', '{"tags": ["收纳", "通勤"]}'),
('折叠双肩包',     '轻量背负，折叠后仅手掌大小。',               69.00,  'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/006', 'jd',     'ACTIVE', '{"tags": ["通勤", "背包"]}'),
('轻量天幕',       '一人即可搭建，遮阳挡小雨。',                 129.00, 'https://images.unsplash.com/photo-1526491109672-74740652b963?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/007', 'taobao', 'ACTIVE', '{"tags": ["露营"]}'),
('双人折叠椅',     '承重好且便于收纳携带。',                     79.00,  'https://images.unsplash.com/photo-1475483768296-6163e08872a1?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/008', 'jd',     'ACTIVE', '{"tags": ["露营"]}'),
('野餐垫',         '防潮耐磨，适合草地和沙地。',                 39.00,  'https://images.unsplash.com/photo-1523986371872-9d3ba2e2a389?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/009', 'pdd',    'ACTIVE', '{"tags": ["露营"]}'),
('便携卡式炉',     '户外烧水做饭都方便，火力稳定。',             89.00,  'https://images.unsplash.com/photo-1596797038530-2c107229654b?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/010', 'taobao', 'ACTIVE', '{"tags": ["露营"]}'),
('显示器增高支架', '腾出键盘空间，改善低头角度。',               49.00,  'https://images.unsplash.com/photo-1547082299-de196ea013d6?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/011', 'jd',     'ACTIVE', '{"tags": ["办公"]}'),
('桌面理线器',     '走线固定整齐，桌面瞬间清爽。',               15.90,  'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/012', 'pdd',    'ACTIVE', '{"tags": ["办公", "收纳"]}'),
('护眼台灯',       '无频闪、亮度可调，长时间使用不累眼。',       89.00,  'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/013', 'jd',     'ACTIVE', '{"tags": ["办公", "灯光"]}'),
('氨基酸洁面',     '温和清洁不紧绷，早晚都适用。',               39.00,  'https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/014', 'taobao', 'ACTIVE', '{"tags": ["护肤"]}'),
('保湿乳液',       '清爽质地，夏季使用无负担。',                 59.00,  'https://images.unsplash.com/photo-1556228578-8c89e6adf883?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/015', 'jd',     'ACTIVE', '{"tags": ["护肤"]}'),
('手冲壶',         '细口稳定水流，新手也容易控制。',             69.00,  'https://images.unsplash.com/photo-1521302080334-4bebac2763a6?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/016', 'taobao', 'ACTIVE', '{"tags": ["咖啡"]}'),
('V60 滤杯',       '经典锥形滤杯，入门首选。',                   29.00,  'https://images.unsplash.com/photo-1610889556528-9a770e32642f?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/017', 'jd',     'ACTIVE', '{"tags": ["咖啡"]}'),
('精品咖啡豆',     '中度烘焙，坚果与焦糖调性。',                 45.00,  'https://images.unsplash.com/photo-1447933601403-0c6688de566e?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/018', 'pdd',    'ACTIVE', '{"tags": ["咖啡", "耗材"]}'),
('幼猫全期试吃装', '小包装试吃，高肉含量，先试后囤不浪费。',     29.00,  'https://images.unsplash.com/photo-1518791841217-8f162f1e1131?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/019', 'jd',     'ACTIVE', '{"tags": ["宠物"]}'),
('半封闭猫砂盆',   '半封闭设计防带砂，幼猫到成猫都适用。',       59.00,  'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/020', 'taobao', 'ACTIVE', '{"tags": ["宠物"]}'),
('防水防滑鞋套',   '加厚 TPU 材质，防滑底纹，可重复水洗。',       12.90,  'https://images.unsplash.com/photo-1519692933481-e162a57d6721?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/021', 'pdd',    'ACTIVE', '{"tags": ["雨天"]}'),
('TPE 瑜伽垫',     '双面防滑，回弹支撑适中，附收纳背带。',       69.00,  'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/022', 'jd',     'ACTIVE', '{"tags": ["健身"]}'),
('弹力带套装',     '5 档阻力覆盖新手到进阶，附动作图册。',       39.90,  'https://images.unsplash.com/photo-1598289431512-b97b0917affc?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/023', 'pdd',    'ACTIVE', '{"tags": ["健身"]}'),
('迷你电煮锅',     '煮面煮粥煎蛋一锅搞定，宿舍可用。',           79.00,  'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=800&q=80', 'https://example.com/p/024', 'taobao', 'ACTIVE', '{"tags": ["早餐"]}');

INSERT INTO discovery_products (discovery_id, product_id, quantity, sort_order) VALUES
(1, 1, 1, 1), (1, 2, 1, 2), (1, 3, 1, 3),
(2, 4, 1, 1), (2, 5, 1, 2), (2, 6, 1, 3),
(3, 7, 1, 1), (3, 8, 2, 2), (3, 9, 1, 3), (3, 10, 1, 4),
(4, 11, 1, 1), (4, 12, 1, 2), (4, 13, 1, 3),
(5, 14, 1, 1), (5, 15, 1, 2),
(6, 16, 1, 1), (6, 17, 1, 2), (6, 18, 1, 3),
(7, 19, 1, 1), (7, 20, 1, 2),
(8, 21, 2, 1), (8, 5, 1, 2),
(9, 24, 1, 1),
(10, 22, 1, 1), (10, 23, 1, 2);
