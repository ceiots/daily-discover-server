# AI对话功能表结构设计文档 (MVP版本)

## MVP设计原则

- 每表字段严格控制在20个以内，消除冗余字段
- 完全避免使用外键约束，由应用层保证数据一致性
- 仅保留核心业务功能，避免过度设计
- 针对高并发、高可用和高性能场景进行优化
- 采用合理的索引策略提升查询效率
- 选择适当的字段类型和长度，节省存储空间

## 核心表结构说明与优化思路

### 1. AI对话会话表 (ai_conversation)

**MVP优化**：
- 移除次要字段：device_info、ip_address等非核心信息
- 调整字段顺序，将重要信息放在前面，便于快速浏览和理解
- 保留关键索引，特别是用户ID和状态的组合索引，优化会话列表查询

**表用途**：记录用户与AI的对话会话基本信息，是整个对话系统的核心表。

**主要字段**：
- `id`: 会话唯一标识
- `user_id`: 用户ID，关联用户
- `conversation_type`: 会话类型(普通对话/内容创作等)
- `model_id`: 使用的模型标识
- `context_length`/`total_tokens`: 上下文长度和Token统计
- `status`: 会话状态(进行中/已结束/异常中断)

### 2. AI对话消息表 (ai_message)

**MVP优化**：
- 移除duration_ms、feedback_time等非必要字段
- 保留content_type以支持多模态对话
- 将media_urls和reference_ids设为JSON类型，方便扩展

**表用途**：存储会话中的具体消息内容，包括用户输入和AI回复。

**主要字段**：
- `conversation_id`/`message_index`: 关联会话和消息顺序
- `role`: 角色(用户/AI/系统)
- `content`: 消息内容
- `content_type`: 内容类型(文本/图片等)
- `token_count`: 消息token数

### 3. AI模型配置表 (ai_model)

**MVP优化**：
- 保留完整结构，因为模型配置对于AI对话至关重要
- 通过JSON字段default_params和capabilities支持灵活的模型能力配置

**表用途**：管理系统支持的AI模型配置信息。

**主要字段**：
- `model_code`: 模型标识码
- `model_name`: 模型名称
- `model_version`: 模型版本
- `provider`: 服务提供商
- `api_endpoint`: API端点
- `max_token_limit`: 最大token限制
- `input_price`/`output_price`: 输入/输出价格

### 4. AI对话模板表 (ai_conversation_template)

**MVP优化**：
- 移除icon_url等非核心字段
- 移除update_time，仅保留create_time简化设计

**表用途**：存储预设的对话模板，如不同场景的对话引导。

**主要字段**：
- `template_id`: 模板唯一标识
- `template_name`: 模板名称
- `template_content`: 模板内容
- `create_time`: 创建时间

### 5. 知识库相关表

**MVP优化**：
- AI知识库表(ai_knowledge_base)：保留核心字段，支持基本的知识库管理
- 知识库文档表(ai_knowledge_document)：移除error_message和last_embedding_time等细节字段
- 知识库文档块表(ai_knowledge_chunk)：保持精简设计，聚焦于向量检索

**表用途**：支持基于知识库的AI问答功能。

**主要字段**：
- `knowledge_base_id`: 知识库唯一标识
- `knowledge_base_name`: 知识库名称
- `owner_id`: 知识库拥有者ID
- `access_level`: 访问权限

**知识库文档表(ai_knowledge_document)**：
- 存储上传到知识库的文档信息
- 记录文档类型、文件大小、向量化状态等

**知识库文档块表(ai_knowledge_chunk)**：
- 存储文档分块后的内容片段
- 记录每个块的向量表示，用于相似度检索

### 6. 标签与关联表

**MVP优化**：
- 保持设计简洁，仅支持用户对会话的基本标签管理
- ai_conversation_tag和ai_conversation_tag_relation两表分开设计，避免一个表字段过多

**表用途**：支持用户对会话进行分类整理。

**主要字段**：
- `tag_id`: 标签唯一标识
- `tag_name`: 标签名称
- `tag_description`: 标签描述

**ai_conversation_tag_relation表**：
- 存储会话与标签的关联关系

**主要字段**：
- `relation_id`: 关联关系唯一标识
- `conversation_id`: 关联的会话ID
- `tag_id`: 关联的标签ID

### 7. 新增的简化统计表

**MVP优化**：
- 新增ai_conversation_daily_stats表替代原复杂统计表
- 仅保留核心统计维度：会话次数、消息条数、token数
- 按用户和日期聚合，便于查询和统计

**表用途**：提供基本的用户使用统计信息。

**主要字段**：
- `stats_id`: 统计唯一标识
- `user_id`: 用户ID
- `date`: 日期
- `conversation_count`: 会话次数
- `message_count`: 消息条数
- `token_count`: token数

### 8. 新增的简化配额表

**MVP优化**：
- 新增ai_user_quota表替代原复杂配额设计
- 简化配额类型，只关注每日会话次数和token数限制
- 内置重置机制，通过reset_date字段控制

**表用途**：实现基本的用户使用限制功能。

**主要字段**：
- `quota_id`: 配额唯一标识
- `user_id`: 用户ID
- `daily_limit`: 每日会话次数限制
- `token_limit`: token数限制
- `reset_date`: 重置日期

## 性能优化设计要点

1. **索引策略优化**
   - 针对高频查询场景设计索引：
     - 用户会话列表查询: (user_id, status)
     - 会话内消息按序显示: (conversation_id, message_index)
     - 置顶会话查询: (user_id, is_pinned, create_time)
   - 避免过多索引影响写入性能

2. **高并发设计考虑**
   - 使用乐观锁而非悲观锁
   - 避免使用外键约束，减少锁竞争
   - 不依赖数据库级联操作，由应用层控制数据一致性

3. **空间优化设计**
   - 合理使用JSON类型存储非结构化或半结构化数据
   - 向量数据使用mediumtext类型，支持大容量向量存储
   - 适当减少索引数量，只为高频查询路径创建索引

4. **分区和缓存策略**
   - 会话和消息表可按用户ID范围或时间范围进行分区
   - 统计表按时间分区，利于历史数据归档
   - 热门会话和模板可考虑使用Redis缓存

## 后续可扩展方向

在MVP基础上，后续可根据业务需求扩展以下功能：

1. 重新添加AI工具调用相关表，支持AI调用外部工具
2. 增加更复杂的用户配额管理，支持多种配额类型和周期
3. 添加更详细的统计分析维度
4. 引入专用向量数据库，优化知识库检索性能 