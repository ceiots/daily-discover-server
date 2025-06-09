# Daily Discover系统数据库设计优化文档

## 优化原则

本次数据库设计优化遵循以下核心原则：

1. **精简表结构**：每表字段控制在18个以内，移除非必要字段
2. **无外键约束**：采用无外键约束的设计策略，避免过度复杂化
3. **高并发优化**：针对高并发、高可用场景优化表结构
4. **合理索引**：为常用查询场景设计合理的索引结构
5. **业务完整性**：平衡业务完整性与SQL查询效率，确保核心功能正常运行
6. **模块化设计**：按业务域划分表结构，提高系统维护性

## 系统架构总览

Daily Discover系统共划分为以下几个核心业务模块：

1. **用户会员系统** - 管理用户账号、会员等级、积分等
2. **商品系统** - 管理商品、分类、品牌、SKU等
3. **订单交易系统** - 管理订单、支付、购物车等
4. **物流退款系统** - 管理物流、退款、售后服务等
5. **营销促销系统** - 管理促销活动、优惠券、秒杀、拼团等
6. **内容管理系统** - 管理文章、视频、评论、审核等
7. **搜索推荐系统** - 管理搜索、推荐、用户画像等
8. **智能应用系统** - 管理AI对话、知识库等
9. **店铺财务系统** - 管理店铺账户、结算、佣金等
10. **供应链库存系统** - 管理供应商、库存、采购等
11. **分析报表系统** - 管理统计数据、报表、监控等

## 主要优化措施

### 1. 合并冗余表结构

- 将`user_browse_history`表和`user_interest_tag`表统一归属在搜索推荐系统中
- 整合了相关业务的表，如订单交易相关表统一在订单交易系统中

### 2. 精简表字段

- 所有表字段数量控制在18个以内
- 优先保留核心业务字段，移除冗余字段
- 例如：商品表移除了`product_subtitle`、`product_desc`、`virtual_sales`等非核心字段

### 3. 优化索引结构

- 针对高频查询场景设计索引
- 设置合理的复合索引减少索引数量
- 对索引列优先考虑使用合适的数据类型和长度

### 4. 采用无外键约束设计

- 不使用外键约束，通过应用程序逻辑确保数据一致性
- 减少锁争用，提高并发性能
- 简化数据库维护和分库分表潜力

## 各系统表结构概览

### 用户会员系统 (user_member_system.sql)

核心表：
- `user` - 用户基本信息
- `user_profile` - 用户详细资料
- `user_account` - 用户账户余额
- `member_level` - 会员等级定义
- `user_member` - 用户会员信息
- `user_points_log` - 积分变动记录

优化点：
- 移除了重复的`user_browse_history`和`user_interest_tag`表
- 精简了用户登录日志表字段

### 商品系统 (product_system.sql)

核心表：
- `product` - 商品基本信息
- `product_category` - 商品分类
- `product_brand` - 商品品牌
- `product_sku` - 商品SKU
- `product_spec` - 商品规格
- `product_detail` - 商品详情

优化点：
- 精简商品表字段，从25个字段优化到19个
- 移除了非核心字段如`virtual_sales`、`is_new`、`is_hot`等

### 订单交易系统 (order_transaction_system.sql)

核心表：
- `order` - 订单主表
- `order_item` - 订单商品项
- `order_payment` - 订单支付信息
- `payment_record` - 支付记录
- `cart` - 购物车

优化点：
- 订单表精简为18个字段
- 移除了订单接收时间等可推导字段

### 物流退款系统 (logistics_refund_system.sql)

核心表：
- `logistics_order` - 物流订单
- `logistics_tracking` - 物流跟踪
- `refund_apply` - 退款申请
- `return_logistics` - 退货物流

优化点：
- 物流订单表从29个字段精简到18个
- 退款申请表从20个字段精简到17个

### 营销促销系统 (marketing_promotion_system.sql)

核心表：
- `promotion_activity` - 促销活动
- `coupon` - 优惠券
- `user_coupon` - 用户优惠券
- `seckill_activity` - 秒杀活动
- `group_buy_activity` - 拼团活动

### 内容管理系统 (content_management_system.sql)

核心表：
- `content` - 内容基础表
- `comment` - 评论表
- `content_review` - 内容审核
- `content_tag` - 内容标签

优化点：
- 内容表从19个字段精简到17个
- 移除了`subtitle`和`share_count`等非核心字段

### 搜索推荐系统 (search_recommend_system.sql)

核心表：
- `search_keyword` - 搜索关键词
- `search_history` - 搜索历史
- `user_browse_history` - 用户浏览历史
- `user_interest_tag` - 用户兴趣标签
- `recommend_result` - 推荐结果

优化点：
- 内容埋点表从13个字段精简到12个
- 统一了用户浏览历史和兴趣标签表的管理

### 智能应用系统 (intelligent_application_system.sql)

核心表：
- `ai_conversation` - AI对话会话
- `ai_message` - AI对话消息
- `ai_knowledge_base` - AI知识库
- `ai_knowledge_chunk` - 知识库文档块

优化点：
- AI知识库表移除了`shared_with`字段，精简为12个字段

### 店铺财务系统 (shop_finance_system.sql)

核心表：
- `shop_account` - 店铺账户
- `shop_settlement` - 店铺结算
- `shop_transaction` - 店铺交易
- `shop_withdraw` - 店铺提现

### 供应链库存系统 (supply_chain_inventory_system.sql)

核心表：
- `supplier` - 供应商
- `inventory` - 库存
- `warehouse` - 仓库
- `purchase_order` - 采购订单

### 分析报表系统 (analytics_reporting_system.sql)

核心表：
- `sales_statistics` - 销售统计
- `user_behavior_statistics` - 用户行为统计
- `search_keyword_statistics` - 搜索关键词统计
- `platform_monitor` - 平台监控

## 高并发优化设计

1. **适当冗余**：在必要场景下适当冗余数据，避免过多JOIN操作
2. **分区策略**：为大表预留分区可能性
3. **慎用大字段**：文本类型使用text而非varchar(max)
4. **合理索引**：为高频查询场景创建覆盖索引
5. **适当非规范化**：在必要场景下采用非规范化设计提高查询效率

## 策略设计

表分区策略：时间范围分区、哈希分区、列表分区
读写分离架构：主从复制拓扑、读写流量分配
数据归档策略：时间阈值归档、冷热数据分离
多级缓存策略：应用层缓存、分布式缓存
分布式数据库设计：垂直分库、水平分表


## 扩展性考虑

1. **表结构预留**：核心表预留了状态字段和扩展字段
2. **避免硬编码**：业务类型等使用tinyint并添加明确注释
3. **版本控制**：部分关键表增加版本字段，便于乐观锁实现


主要优化包括：

1. **表分区策略**：
   - 为内容基础表(`content`)添加了基于发布时间的范围分区，将数据分为近期(30天内)、3个月内、6个月内和历史数据，便于历史数据归档管理
   - 为评论表(`comment`)添加了基于内容ID的哈希分区(8个分区)，避免热点内容评论集中，提高并发写入性能
   - 为标签关联表(`content_tag_relation`)添加了基于标签ID的哈希分区(8个分区)，提高热门标签的查询效率
   - 为用户行为表(`user_like`, `user_favorite`, `user_follow`)添加了基于用户ID的哈希分区(16个分区)，分散高频用户的写入压力
   - 为内容审核表(`content_review`)和内容举报表(`content_report`)添加了基于状态的列表分区，便于快速处理待审核/待处理的内容
   - 为敏感词表(`sensitive_word`)添加了基于类型的列表分区，提高审核时的查询效率
   - 为用户反馈表(`user_feedback`)添加了基于反馈类型的列表分区，便于分类处理不同类型的反馈
   - 为热门排行表(`content_rank`)添加了基于排行日期的范围分区，便于管理不同时间范围的热门数据

2. **数据归档策略**：
   - 添加了三个数据归档存储过程：`archive_content`、`archive_comment`、`archive_content_rank`
   - 创建了定期执行的归档事件，分别在不同时间自动执行归档操作：
     - 每天凌晨3:00归档超过180天的已发布/已下线/已删除的内容
     - 每周一凌晨2:00归档超过90天的评论
     - 每周日凌晨1:00归档超过30天的排行榜数据

3. **冷热数据分离**：
   - 通过分区策略实现了冷热数据分离，热数据(近期数据)保留在主分区，冷数据(历史数据)可以归档到专门的表中

4. **无外键约束**：
   - 保持了原有的无外键约束设计，适合高并发场景

这些优化措施可以显著提升高并发场景下的数据库性能，减少锁争用，提高查询和写入效率，并便于数据管理和扩展。

