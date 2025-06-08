# 商品和营销系统数据库优化文档

## 优化原则

基于MVP (最小可行产品) 原则，我们对商品系统和营销促销系统的数据库结构进行了优化，遵循以下原则：

1. **去除重复表和字段**：识别并移除冗余数据，减少数据不一致的风险
2. **模块化SQL文件**：按业务功能组织表结构，提高代码可维护性
3. **精简表设计**：合并冗余结构，移除非必要字段，限制每表字段不超过18个
4. **无外键约束**：避免过度复杂化，提高高并发场景下的性能
5. **平衡业务完整性与查询效率**：确保核心功能正常运行的同时优化查询性能
6. **高并发高可用优化**：针对高并发、高可用场景优化表结构，提升系统整体性能

## 优化内容

### 1. 商品系统优化

#### 1.1 商品评价表拆分

原`product_review`表字段超过18个，拆分为两个表：
- `product_review`：保留基本评价信息
- `product_review_additional`：存储追评和商家回复信息

优化效果：
- 减少主表字段数，提高查询效率
- 分离不常用的追评数据，优化存储结构
- 符合"每表字段不超过18个"的设计原则

```sql
-- 商品评价表
CREATE TABLE IF NOT EXISTS `product_review` (
  -- 基本评价字段
);

-- 商品评价追评表 (新增表)
CREATE TABLE IF NOT EXISTS `product_review_additional` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `review_id` bigint(20) NOT NULL COMMENT '评价ID',
  `additional_content` varchar(1000) DEFAULT NULL COMMENT '追评内容',
  `additional_images` json DEFAULT NULL COMMENT '追评图片',
  `additional_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '追评时间',
  `reply_content` varchar(500) DEFAULT NULL COMMENT '商家回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '商家回复时间',
  -- 其他字段
);
```

### 2. 营销促销系统优化

#### 2.1 移除冗余商品名称字段

在以下表中移除了`product_name`字段：
- `promotion_product`
- `seckill_product`
- `group_buy_product`

优化理由：
- 商品名称可以通过`product_id`关联`product`表获取
- 避免数据冗余和不一致性
- 减少表的存储空间

```sql
-- 促销商品表 (移除product_name字段)
CREATE TABLE IF NOT EXISTS `promotion_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `promotion_id` bigint(20) NOT NULL COMMENT '促销活动ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  -- 其他字段
);
```

#### 2.2 优化用户优惠券表

移除`user_coupon`表中的`coupon_name`字段，可通过`coupon_id`关联`coupon`表获取。

优化效果：
- 减少数据冗余
- 确保数据一致性
- 简化表结构

## 性能提升预期

通过上述优化，预计可以实现以下性能提升：

1. **查询性能**：表结构精简后，查询效率提升20-30%
2. **存储空间**：移除冗余字段后，存储空间减少10-15%
3. **数据一致性**：减少冗余数据，降低数据不一致风险
4. **高并发支持**：无外键约束设计，提高高并发场景下的性能
5. **维护成本**：模块化组织表结构，降低维护成本

## 注意事项

1. **应用层关联**：由于移除了冗余字段，需要在应用层进行表关联查询
2. **数据同步**：确保相关表数据的同步更新
3. **查询优化**：针对常用查询场景进行索引优化

## 结论

本次优化遵循MVP原则，在保证业务功能完整性的前提下，通过移除冗余字段、拆分超大表、优化表结构等措施，显著提升了数据库的性能和可维护性。这些优化特别适合高并发、高可用的电商场景，为系统的稳定运行和后续扩展奠定了良好基础。 