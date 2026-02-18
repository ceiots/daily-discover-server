# Daily Discover Product Service

## 项目简介
Daily Discover Product Service 是一个基于 Spring Boot 的商品推荐服务，为 Daily Discover 应用提供商品数据 API 接口。


#### 本地访问（推荐开发环境）
```bash
# 测试服务健康状态和商品接口
curl -X GET http://localhost:8092/actuator/health && echo "" && curl -X GET http://localhost:8092
```


**curl 命令：**
```bash
# 获取晨间推荐
curl -X GET http://localhost:8092/products/recommendations/morning

# 获取夜晚推荐
curl -X GET http://localhost:8092/products/recommendations/evening
```



### 数据表结构模块
数据库采用模块化设计，分为9个核心模块：

#### 1. 商品核心信息模块
- `products` - 商品基础信息表（SPU - 标准化产品单元）
- `product_categories` - 商品分类表（优化树形结构）
- `product_details` - 商品详情表（电商简化版，包含图片/视频统一管理）
- `product_skus` - SKU表（电商核心 - 可销售最小单位）
- `product_sku_specs` - 商品规格定义表（购买选择型规格）
- `product_sku_spec_options` - 商品规格选项表（规格具体值）
- `shopping_cart` - 购物车表（支持多规格购买）

#### 2. 商家与库存模块
- `sellers` - 商家基础信息表
- `seller_profiles` - 商家资料表
- `product_inventory_core` - 库存核心表（高频读写，最小化字段）
- `product_inventory_config` - 库存配置表（低频读写，扩展信息）
- `inventory_transactions` - 库存操作记录表

#### 3. 评价与互动模块
- `user_reviews` - 用户评价表（核心信息）
- `user_review_details` - 用户评价详情表（大字段单独存储）
- `user_review_stats` - 用户评价统计表（实时统计字段）
- `review_replies` - 评价回复表
- `review_stats` - 商品评价统计表（聚合统计）

#### 4. 商品关系与推荐模块
- `product_recommendations` - 商品推荐表（统一推荐表，合并相关商品和推荐功能）
- `product_sales_stats` - 销量统计表（支持多种时间粒度）
- `user_behavior_logs` - 用户行为表（记录浏览、点击、购买等行为）
- `user_interest_profiles` - 用户兴趣画像表
- `scenario_recommendations` - 场景推荐表（基于场景的推荐）
- `recommendation_effects` - 推荐效果跟踪表
- `product_search_keywords` - 搜索关键词表
- `product_tags` - 商品标签表
- `product_tag_relations` - 商品标签关联表

#### 5. 订单管理模块
- `orders_core` - 订单核心信息（高频查询字段）
- `orders_extend` - 订单扩展信息（低频查询字段）
- `order_items` - 订单商品项
- `order_invoices` - 订单发票信息
- `after_sales_applications` - 售后申请

#### 6. 物流和地区管理模块
- `regions` - 地区表（国家标准行政区划）
- `order_shipping` - 订单物流信息
- `order_shipping_tracks` - 物流跟踪记录

#### 7. 支付管理模块
- `payment_methods` - 支付方式表
- `payment_transactions` - 支付记录表
- `refund_records` - 退款记录表

#### 8. 营销促销管理模块
- `promotion_activities` - 促销活动表
- `coupons` - 优惠券表
- `coupon_usage_records` - 优惠券使用记录

#### 9. 客户服务管理模块
- `customer_service_agents` - 客服坐席表
- `customer_service_categories` - 客服分类表
- `customer_service_conversations` - 客服会话表
- `customer_service_messages` - 客服消息表

### 数据库迁移脚本
迁移脚本位于 `src/main/resources/db/migration/` 目录：

```
migration/
├── 001_create_product_core_tables.sql      # 商品核心信息模块
├── 002_create_seller_inventory_tables.sql  # 商家与库存模块
├── 003_create_review_interaction_tables.sql # 评价与互动模块
├── 004_create_relationship_recommendation_tables.sql # 商品关系与推荐模块
├── 005_create_order_tables.sql             # 订单管理模块
├── 006_create_shipping_region_tables.sql   # 物流和地区管理模块
├── 007_create_payment_tables.sql           # 支付管理模块
├── 008_create_promotion_tables.sql         # 营销促销管理模块
└── 009_create_customer_service_tables.sql  # 客户服务管理模块
```

### 手动执行数据库迁移

```bash
# 执行所有迁移脚本（推荐）
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\001_create_product_core_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\002_create_seller_inventory_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\003_create_review_interaction_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\004_create_relationship_recommendation_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\005_create_order_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\006_create_shipping_region_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\007_create_payment_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\008_create_promotion_tables.sql"
mysql -u root -p -h localhost -P 3306 daily_discover < "d:\daily-discover\daily-discover-server\daily-discover-product\src\main\resources\db\migration\009_create_customer_service_tables.sql"

```

## 开发指南

### 核心目录结构

项目采用标准的 Spring Boot 分层架构，主要目录结构如下：

```
daily-discover-product/
├── src/main/java/com/dailydiscover/
│   ├── controller/           # API 接口层 - 商品、订单、购物车等业务接口
│   ├── service/              # 业务逻辑层 - 核心业务处理逻辑
│   ├── mapper/               # 数据访问层 - MyBatis 数据操作
│   ├── model/                # 数据模型层 - 实体类定义
│   └── config/               # 配置类 - 安全、数据库等配置
├── src/main/resources/
│   ├── db/migration/         # 数据库迁移脚本（9个业务模块）
│   ├── mapper/               # MyBatis XML 映射文件
│   └── application.properties # 应用配置
└── pom.xml                   # Maven 依赖管理
```

**核心业务模块**：
- **商品管理**：SPU/SKU、分类、详情、评价
- **订单系统**：购物车、订单、购买流程
- **商家服务**：商家信息、商品管理

### 项目架构

项目采用标准的 Spring Boot 分层架构：

- **控制器层**：提供 RESTful API 接口，处理商品、订单、评价等业务请求
- **服务层**：实现核心业务逻辑，包括商品推荐、订单管理、用户行为处理
- **数据访问层**：基于 MyBatis 框架，支持复杂查询和性能优化
- **数据模型层**：定义商品、订单、用户等核心业务实体
- **资源配置**：包含数据库配置、迁移脚本和 MyBatis 映射文件


### 添加新功能
1. 在 `model` 包中创建实体类
2. 在 `repository` 包中创建数据访问接口
3. 在 `service` 包中实现业务逻辑
4. 在 `controller` 包中添加 REST 接口
5. 创建相应的数据库迁移脚本
