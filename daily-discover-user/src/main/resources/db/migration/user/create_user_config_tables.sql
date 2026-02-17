-- 用户配置表迁移脚本
-- 创建用户相关的静态配置、帮助中心、FAQ等扩展属性表

-- 使用数据库
USE daily_discover;

-- 1. 帮助中心FAQ分类表
CREATE TABLE IF NOT EXISTS help_faq_categories (
    category_id VARCHAR(50) PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮助中心FAQ分类表';

-- 2. 帮助中心FAQ表
CREATE TABLE IF NOT EXISTS help_faqs (
    faq_id VARCHAR(50) PRIMARY KEY COMMENT 'FAQ ID',
    category_id VARCHAR(50) NOT NULL COMMENT '分类ID',
    question TEXT NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '答案',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮助中心FAQ表';

-- 3. 反馈类型表
CREATE TABLE IF NOT EXISTS feedback_types (
    type_id VARCHAR(50) PRIMARY KEY COMMENT '类型ID',
    type_name VARCHAR(100) NOT NULL COMMENT '类型名称',
    description TEXT COMMENT '描述',
    icon VARCHAR(20) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈类型表';

-- 4. 账户设置配置表
CREATE TABLE IF NOT EXISTS account_settings (
    setting_id VARCHAR(50) PRIMARY KEY COMMENT '设置项ID',
    setting_name VARCHAR(100) NOT NULL COMMENT '设置项名称',
    setting_type ENUM('basic', 'security', 'notification', 'privacy') NOT NULL COMMENT '设置类型',
    description TEXT COMMENT '设置项描述',
    icon VARCHAR(50) COMMENT '图标名称',
    route_path VARCHAR(200) COMMENT '路由路径',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_setting_type (setting_type),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户设置配置表';

-- 5. 隐私政策内容表
CREATE TABLE IF NOT EXISTS privacy_policy_sections (
    section_id VARCHAR(50) PRIMARY KEY COMMENT '章节ID',
    section_title VARCHAR(200) NOT NULL COMMENT '章节标题',
    section_content TEXT NOT NULL COMMENT '章节内容',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='隐私政策内容表';

-- 6. 用户协议内容表
CREATE TABLE IF NOT EXISTS user_agreement_sections (
    section_id VARCHAR(50) PRIMARY KEY COMMENT '章节ID',
    section_title VARCHAR(200) NOT NULL COMMENT '章节标题',
    section_content TEXT NOT NULL COMMENT '章节内容',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户协议内容表';

-- 7. 帮助中心快速操作表
CREATE TABLE IF NOT EXISTS help_quick_actions (
    action_id VARCHAR(50) PRIMARY KEY COMMENT '操作ID',
    action_name VARCHAR(100) NOT NULL COMMENT '操作名称',
    action_description TEXT COMMENT '操作描述',
    icon VARCHAR(50) COMMENT '图标名称',
    route_path VARCHAR(200) COMMENT '路由路径',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮助中心快速操作表';

-- 8. 帮助中心联系方式表
CREATE TABLE IF NOT EXISTS help_contact_methods (
    method_id VARCHAR(50) PRIMARY KEY COMMENT '联系方式ID',
    method_name VARCHAR(100) NOT NULL COMMENT '联系方式名称',
    method_description TEXT COMMENT '联系方式描述',
    contact_info VARCHAR(200) COMMENT '联系信息',
    icon VARCHAR(50) COMMENT '图标名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮助中心联系方式表';

-- 9. 用户权限配置表
CREATE TABLE IF NOT EXISTS user_permission_configs (
    level_name VARCHAR(50) PRIMARY KEY COMMENT '等级名称',
    can_upload_avatar BOOLEAN DEFAULT TRUE COMMENT '可上传头像',
    can_edit_profile BOOLEAN DEFAULT TRUE COMMENT '可编辑资料',
    can_create_post BOOLEAN DEFAULT TRUE COMMENT '可发布内容',
    can_comment BOOLEAN DEFAULT TRUE COMMENT '可发表评论',
    can_send_message BOOLEAN DEFAULT TRUE COMMENT '可发送消息',
    can_create_collection BOOLEAN DEFAULT TRUE COMMENT '可创建收藏夹',
    can_delete_content BOOLEAN DEFAULT FALSE COMMENT '可删除内容',
    can_report_content BOOLEAN DEFAULT TRUE COMMENT '可举报内容',
    can_access_premium BOOLEAN DEFAULT FALSE COMMENT '可访问高级功能',
    can_invite_friends BOOLEAN DEFAULT TRUE COMMENT '可邀请好友',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限配置表';

-- 初始化数据
-- 1. 初始化FAQ分类
INSERT INTO help_faq_categories (category_id, category_name, sort_order) VALUES
('account', '账号相关', 1),
('payment', '支付相关', 2),
('technical', '技术问题', 3);

-- 2. 初始化FAQ数据
INSERT INTO help_faqs (category_id, faq_id, question, answer, sort_order) VALUES
('account', 'account-1', '如何注册账号？', '您可以通过手机号或邮箱注册账号，具体步骤请参考注册指南。', 1),
('account', 'account-2', '忘记密码怎么办？', '您可以在登录页面点击"忘记密码"，通过手机验证码或邮箱重置密码。', 2),
('payment', 'payment-1', '支持哪些支付方式？', '我们支持微信支付、支付宝、银行卡等多种支付方式。', 1),
('payment', 'payment-2', '支付失败怎么办？', '请检查网络连接和支付账户余额，如问题持续请联系客服。', 2),
('technical', 'technical-1', '应用闪退怎么办？', '请尝试重启应用或重新安装，如问题持续请反馈给我们。', 1),
('technical', 'technical-2', '如何清除缓存？', '您可以在设置页面找到清除缓存功能。', 2);

-- 3. 初始化反馈类型
INSERT INTO feedback_types (type_id, type_name, description, icon, sort_order) VALUES
('bug', '功能异常', '应用出现错误、崩溃或功能异常', '🐛', 1),
('suggestion', '功能建议', '对应用功能的改进建议', '💡', 2),
('content', '内容问题', '内容错误、侵权或不当内容', '📝', 3),
('performance', '性能问题', '应用运行缓慢、卡顿等问题', '⚡', 4),
('ui', '界面问题', '界面显示异常或用户体验问题', '🎨', 5),
('other', '其他问题', '其他未分类的问题或建议', '❓', 6);

-- 4. 初始化账户设置配置
INSERT INTO account_settings (setting_id, setting_name, setting_type, description, icon, route_path, sort_order) VALUES
('profile', '个人资料', 'basic', '管理您的个人资料信息', 'user', '/profile', 1),
('security', '账号安全', 'security', '修改密码、绑定手机等安全设置', 'lock', '/security', 2),
('notification', '消息通知', 'notification', '管理推送、邮件等通知设置', 'bell', '/notification', 3),
('privacy', '隐私设置', 'privacy', '管理个人隐私和数据权限', 'shield', '/privacy', 4),
('help', '帮助中心', 'basic', '常见问题和使用指南', 'help-circle', '/help', 5),
('about', '关于我们', 'basic', '应用信息和版本信息', 'info', '/about', 6);

-- 5. 初始化隐私政策内容
INSERT INTO privacy_policy_sections (section_id, section_title, section_content, sort_order) VALUES
('introduction', '引言', '欢迎使用我们的服务。本隐私政策说明了我们如何收集、使用、存储和保护您的个人信息。', 1),
('collection', '信息收集', '我们收集的信息包括：注册信息、使用数据、设备信息等，用于提供更好的服务。', 2),
('usage', '信息使用', '您的信息将用于：账户管理、个性化推荐、客户服务、安全保护等目的。', 3),
('sharing', '信息共享', '我们仅在必要时与第三方共享信息，如服务提供商、法律要求等。', 4),
('security', '信息安全', '我们采取严格的安全措施保护您的信息，防止未经授权的访问和使用。', 5),
('rights', '您的权利', '您可以访问、更正、删除您的个人信息，或要求我们停止处理您的信息。', 6);

-- 6. 初始化用户协议内容
INSERT INTO user_agreement_sections (section_id, section_title, section_content, sort_order) VALUES
('acceptance', '接受条款', '使用本服务即表示您同意遵守本用户协议的所有条款和条件。', 1),
('account', '账户责任', '您有责任保护账户安全，并对账户下的所有活动负责。', 2),
('content', '内容规范', '您发布的内容应符合法律法规，不得包含违法、侵权或不当内容。', 3),
('intellectual', '知识产权', '本服务的内容受知识产权保护，未经授权不得复制、修改或分发。', 4),
('liability', '责任限制', '我们对服务的可用性、准确性不提供保证，责任限于法律规定范围。', 5),
('termination', '服务终止', '我们有权在您违反协议时终止服务，您也可以随时停止使用。', 6);

-- 7. 初始化帮助中心快速操作
INSERT INTO help_quick_actions (action_id, action_name, action_description, icon, route_path, sort_order) VALUES
('faq', '常见问题', '查看用户常见问题解答', 'help-circle', '/help/faq', 1),
('contact', '联系我们', '通过多种方式联系客服', 'message-square', '/help/contact', 2),
('feedback', '意见反馈', '向我们提出宝贵意见', 'edit-3', '/help/feedback', 3),
('tutorial', '使用指南', '了解应用功能和使用方法', 'book-open', '/help/tutorial', 4);

-- 8. 初始化帮助中心联系方式
INSERT INTO help_contact_methods (method_id, method_name, method_description, contact_info, icon, sort_order) VALUES
('email', '邮箱支持', '通过邮件联系我们', 'support@dailydiscover.com', 'mail', 1),
('phone', '电话支持', '拨打客服热线', '400-123-4567', 'phone', 2),
('chat', '在线客服', '实时在线沟通', '点击在线客服', 'message-circle', 3),
('wechat', '微信公众号', '关注公众号获取帮助', 'DailyDiscover', 'message-square', 4);

-- 9. 初始化用户权限配置
INSERT INTO user_permission_configs (level_name, can_upload_avatar, can_edit_profile, can_create_post, can_comment, can_send_message, can_create_collection, can_delete_content, can_report_content, can_access_premium, can_invite_friends) VALUES
('普通会员', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE, TRUE, FALSE, TRUE),
('银牌会员', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),
('金牌会员', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE),
('钻石会员', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);

-- 完成迁移
COMMIT;