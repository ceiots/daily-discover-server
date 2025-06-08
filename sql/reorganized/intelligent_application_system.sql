-- 智能应用系统表结构（整合版）
-- 设计原则: 每表字段不超过18个，无外键约束，针对高并发高可用场景优化
-- 整合自: ai_conversation_tables.sql

-- AI对话会话表 (记录用户与AI的对话会话)
CREATE TABLE IF NOT EXISTS `ai_conversation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `conversation_title` varchar(100) DEFAULT NULL COMMENT '会话标题',
  `conversation_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '会话类型:1-普通对话,2-内容创作,3-问题解答,4-商品咨询',
  `model_id` varchar(50) NOT NULL COMMENT '使用的模型标识',
  `context_length` int(11) NOT NULL DEFAULT '0' COMMENT '上下文长度',
  `total_tokens` int(11) NOT NULL DEFAULT '0' COMMENT '累计消耗的token',
  `custom_settings` json DEFAULT NULL COMMENT '自定义会话设置',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-进行中,2-已结束,3-异常中断',
  `is_pinned` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否置顶:0-否,1-是',
  `last_message_time` datetime DEFAULT NULL COMMENT '最后消息时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_last_message` (`last_message_time`),
  KEY `idx_pinned_time` (`user_id`, `is_pinned`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话会话表';

-- AI对话消息表 (记录会话中的具体消息)
CREATE TABLE IF NOT EXISTS `ai_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` bigint(20) NOT NULL COMMENT '会话ID',
  `message_index` int(11) NOT NULL COMMENT '消息在会话中的序号',
  `role` varchar(20) NOT NULL COMMENT '角色:user-用户,assistant-AI,system-系统',
  `content` text NOT NULL COMMENT '消息内容',
  `content_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '内容类型:1-文本,2-图片,3-音频,4-视频,5-文件',
  `token_count` int(11) DEFAULT '0' COMMENT '消息token数',
  `media_urls` json DEFAULT NULL COMMENT '媒体文件URL',
  `reference_ids` json DEFAULT NULL COMMENT '引用的知识库/资源ID',
  `feedback_type` tinyint(4) DEFAULT NULL COMMENT '反馈类型:1-点赞,2-点踩,3-举报',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_idx` (`conversation_id`, `message_index`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话消息表';

-- AI模型配置表 (存储可用的AI模型配置)
CREATE TABLE IF NOT EXISTS `ai_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模型ID',
  `model_code` varchar(50) NOT NULL COMMENT '模型标识码',
  `model_name` varchar(100) NOT NULL COMMENT '模型名称',
  `model_version` varchar(20) NOT NULL COMMENT '模型版本',
  `provider` varchar(50) NOT NULL COMMENT '服务提供商',
  `api_endpoint` varchar(255) DEFAULT NULL COMMENT 'API端点',
  `max_token_limit` int(11) NOT NULL COMMENT '最大token限制',
  `input_price` decimal(10,6) NOT NULL DEFAULT '0.000000' COMMENT '输入价格(元/千tokens)',
  `output_price` decimal(10,6) NOT NULL DEFAULT '0.000000' COMMENT '输出价格(元/千tokens)',
  `default_params` json DEFAULT NULL COMMENT '默认参数设置',
  `capabilities` json DEFAULT NULL COMMENT '能力标签',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-可用,0-不可用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_model_code_version` (`model_code`, `model_version`),
  KEY `idx_status` (`status`),
  KEY `idx_provider` (`provider`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI模型配置表';

-- AI对话模板表 (预设的对话模板)
CREATE TABLE IF NOT EXISTS `ai_conversation_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_code` varchar(50) NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_category` varchar(50) NOT NULL COMMENT '模板分类',
  `description` varchar(500) DEFAULT NULL COMMENT '模板描述',
  `system_prompt` text COMMENT '系统提示词',
  `first_prompt` text COMMENT '首条提示词',
  `suggested_questions` json DEFAULT NULL COMMENT '建议问题',
  `model_code` varchar(50) DEFAULT NULL COMMENT '推荐使用的模型',
  `model_params` json DEFAULT NULL COMMENT '模型参数',
  `usage_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '使用次数',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-启用,0-禁用',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_category_status` (`template_category`, `status`),
  KEY `idx_usage_count` (`usage_count`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话模板表';

-- AI知识库表 (用于存储知识库信息)
CREATE TABLE IF NOT EXISTS `ai_knowledge_base` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
  `knowledge_name` varchar(100) NOT NULL COMMENT '知识库名称',
  `knowledge_code` varchar(50) NOT NULL COMMENT '知识库编码',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `owner_id` bigint(20) NOT NULL COMMENT '所有者ID',
  `owner_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '所有者类型:1-用户,2-系统,3-商家',
  `total_documents` int(11) NOT NULL DEFAULT '0' COMMENT '文档总数',
  `embedding_model` varchar(50) DEFAULT NULL COMMENT '向量模型',
  `access_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '访问类型:1-私有,2-共享,3-公开',
  `shared_with` json DEFAULT NULL COMMENT '共享用户/组织ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_knowledge_code` (`knowledge_code`),
  KEY `idx_owner` (`owner_id`, `owner_type`),
  KEY `idx_access_status` (`access_type`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库表';

-- AI知识库文档表 (知识库中的文档)
CREATE TABLE IF NOT EXISTS `ai_knowledge_document` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `knowledge_id` bigint(20) NOT NULL COMMENT '知识库ID',
  `document_title` varchar(200) NOT NULL COMMENT '文档标题',
  `document_type` tinyint(4) NOT NULL COMMENT '文档类型:1-文本,2-网页,3-PDF,4-Word,5-Excel,6-图片',
  `content_hash` varchar(64) DEFAULT NULL COMMENT '内容哈希',
  `document_url` varchar(500) DEFAULT NULL COMMENT '文档URL',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(bytes)',
  `chunk_count` int(11) NOT NULL DEFAULT '0' COMMENT '分块数量',
  `embedding_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '向量化状态:0-未处理,1-处理中,2-已完成,3-失败',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_knowledge_id` (`knowledge_id`),
  KEY `idx_embedding_status` (`embedding_status`),
  KEY `idx_content_hash` (`content_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库文档表';

-- AI知识库文档块表 (文档分块)
CREATE TABLE IF NOT EXISTS `ai_knowledge_chunk` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分块ID',
  `document_id` bigint(20) NOT NULL COMMENT '文档ID',
  `knowledge_id` bigint(20) NOT NULL COMMENT '知识库ID',
  `chunk_index` int(11) NOT NULL COMMENT '分块序号',
  `chunk_content` text NOT NULL COMMENT '分块内容',
  `token_count` int(11) NOT NULL DEFAULT '0' COMMENT 'token数量',
  `embedding_vector` mediumtext COMMENT '向量数据(BASE64编码)',
  `metadata` json DEFAULT NULL COMMENT '元数据',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态:1-正常,0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doc_chunk` (`document_id`, `chunk_index`),
  KEY `idx_knowledge_id` (`knowledge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识库文档块表';

-- AI对话标签表 (用户对会话的分类标签)
CREATE TABLE IF NOT EXISTS `ai_conversation_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `color` varchar(20) DEFAULT NULL COMMENT '标签颜色',
  `conversation_count` int(11) NOT NULL DEFAULT '0' COMMENT '关联的会话数',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`, `tag_name`),
  KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话标签表';

-- AI会话标签关联表 (会话与标签多对多关系)
CREATE TABLE IF NOT EXISTS `ai_conversation_tag_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `conversation_id` bigint(20) NOT NULL COMMENT '会话ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_conv_tag` (`conversation_id`, `tag_id`),
  KEY `idx_tag_user` (`tag_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话标签关联表';
