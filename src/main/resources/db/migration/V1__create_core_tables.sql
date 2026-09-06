-- 每日发现 MVP 核心表结构
-- PostgreSQL 版本

-- 用户表（匿名用户）
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    anonymous_id VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_anonymous_id ON "user"(anonymous_id);

-- 内容表
CREATE TABLE IF NOT EXISTS content (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    body TEXT,
    cover_image_url VARCHAR(512),
    content_type VARCHAR(32) NOT NULL DEFAULT 'ARTICLE',
    status VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED',
    priority INTEGER NOT NULL DEFAULT 0,
    publish_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expire_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_content_status ON content(status);
CREATE INDEX IF NOT EXISTS idx_content_publish_at ON content(publish_at);
CREATE INDEX IF NOT EXISTS idx_content_priority ON content(priority DESC);
CREATE INDEX IF NOT EXISTS idx_content_type ON content(content_type);

-- 场景表
CREATE TABLE IF NOT EXISTS scene (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    cover_image_url VARCHAR(512),
    display_order INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_scene_status ON scene(status);
CREATE INDEX IF NOT EXISTS idx_scene_display_order ON scene(display_order);

-- 商品表
CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY',
    image_url VARCHAR(512),
    product_url VARCHAR(512),
    source VARCHAR(32),
    source_id VARCHAR(128),
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_product_status ON product(status);
CREATE INDEX IF NOT EXISTS idx_product_source ON product(source, source_id);

-- 场景商品关联表
CREATE TABLE IF NOT EXISTS scene_product (
    id BIGSERIAL PRIMARY KEY,
    scene_id BIGINT NOT NULL REFERENCES scene(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(scene_id, product_id)
);

CREATE INDEX IF NOT EXISTS idx_scene_product_scene_id ON scene_product(scene_id);
CREATE INDEX IF NOT EXISTS idx_scene_product_product_id ON scene_product(product_id);

-- 内容场景关联表
CREATE TABLE IF NOT EXISTS content_scene (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL REFERENCES content(id) ON DELETE CASCADE,
    scene_id BIGINT NOT NULL REFERENCES scene(id) ON DELETE CASCADE,
    display_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(content_id, scene_id)
);

CREATE INDEX IF NOT EXISTS idx_content_scene_content_id ON content_scene(content_id);
CREATE INDEX IF NOT EXISTS idx_content_scene_scene_id ON content_scene(scene_id);

-- 行为表
CREATE TABLE IF NOT EXISTS behavior (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    content_id BIGINT NOT NULL REFERENCES content(id) ON DELETE CASCADE,
    behavior_type VARCHAR(16) NOT NULL,
    extra_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_behavior_user_id ON behavior(user_id);
CREATE INDEX IF NOT EXISTS idx_behavior_content_id ON behavior(content_id);
CREATE INDEX IF NOT EXISTS idx_behavior_type ON behavior(behavior_type);
CREATE INDEX IF NOT EXISTS idx_behavior_created_at ON behavior(created_at);
CREATE INDEX IF NOT EXISTS idx_behavior_user_content_type ON behavior(user_id, content_id, behavior_type);

-- 反馈表
CREATE TABLE IF NOT EXISTS feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    content_id BIGINT NOT NULL REFERENCES content(id) ON DELETE CASCADE,
    feedback_type VARCHAR(16) NOT NULL,
    reason VARCHAR(255),
    extra_data JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_feedback_user_id ON feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_feedback_content_id ON feedback(content_id);
CREATE INDEX IF NOT EXISTS idx_feedback_type ON feedback(feedback_type);
CREATE INDEX IF NOT EXISTS idx_feedback_created_at ON feedback(created_at);
CREATE INDEX IF NOT EXISTS idx_feedback_user_content_type ON feedback(user_id, content_id, feedback_type);