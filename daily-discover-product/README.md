# Daily Discover Product Service

## 项目简介
Daily Discover Product Service 是一个基于 Spring Boot 的商品推荐服务，为 Daily Discover 应用提供商品数据 API 接口。

## 功能特性
- 基于时间段的智能商品推荐
- 商品分类管理
- 商品搜索功能
- RESTful API 接口
- MySQL 数据库支持
- 跨域请求支持

## 技术栈
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Maven
- Java 17

## 快速开始

### 1. 环境要求
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置
1. 创建 MySQL 数据库：
```sql
CREATE DATABASE daily_discover CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改数据库连接配置（如需要）：
编辑 `src/main/resources/application.properties` 文件，修改以下配置：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/daily_discover?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

### 3. 构建和运行

#### 使用 Maven 命令行：
```bash
# 进入项目目录
cd daily-discover-product

# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

#### 或者直接运行 JAR 文件：
```bash
# 打包
mvn clean package

# 运行
java -jar target/daily-discover-product-1.0.0.jar
```

### 4. 验证服务
服务启动后，访问以下地址验证：
- 健康检查：http://localhost:8081/actuator/health
- API 文档：http://localhost:8081/api/products

## API 接口文档

### 基础路径
所有 API 接口的基路径为：`/api/products`

### 主要接口

#### 1. 获取所有商品
```
GET /api/products
```

#### 2. 根据ID获取商品
```
GET /api/products/{id}
```

#### 3. 获取当前时间段推荐商品
```
GET /api/products/recommendations
```

#### 4. 根据时间段获取推荐商品
```
GET /api/products/recommendations/{timeSlot}
```
时间段参数：
- `morning`: 晨间 (6:00-12:00)
- `noon`: 午间 (12:00-14:00)
- `afternoon`: 午后 (14:00-18:00)
- `evening`: 夜晚 (18:00-6:00)

#### 5. 根据分类获取商品
```
GET /api/products/category/{category}
```

#### 6. 搜索商品
```
GET /api/products/search?keyword={keyword}
```

#### 7. 获取最新商品
```
GET /api/products/latest
```

## 数据库管理

### 数据库初始化
项目启动时会自动执行以下操作：
1. 创建完整的商品相关表结构
2. 插入丰富的示例数据
3. 建立表之间的业务关联关系

### 数据表结构模块
数据库采用模块化设计，分为4个核心模块：

#### 1. 商品核心信息模块
- `products` - 商品基础信息
- `product_details` - 商品详情
- `product_images` - 商品图片
- `product_specs` - 商品规格
- `product_categories` - 商品分类

#### 2. 商家与库存模块
- `sellers` - 商家信息
- `product_skus` - 商品SKU管理
- `product_inventory` - 库存管理
- `inventory_transactions` - 库存操作记录

#### 3. 评价与互动模块
- `user_reviews` - 用户评价
- `review_replies` - 评价回复
- `review_likes` - 评价点赞
- `review_stats` - 评价统计
- `product_actions` - 用户行为记录
- `user_favorites` - 用户收藏

#### 4. 商品关系与推荐模块
- `related_products` - 相关商品
- `time_based_products` - 时间维度数据
- `product_recommendations` - 商品推荐
- `product_search_keywords` - 搜索关键词
- `product_tags` - 商品标签
- `product_tag_relations` - 标签关联

### 数据库迁移脚本
迁移脚本位于 `src/main/resources/db/migration/` 目录：

```
migration/
├── 001_create_product_core_tables.sql      # 商品核心信息模块
├── 002_create_seller_inventory_tables.sql  # 商家与库存模块
├── 003_create_review_interaction_tables.sql # 评价与互动模块
└── 004_create_relationship_recommendation_tables.sql # 商品关系与推荐模块
```

每个文件包含完整的表结构和初始数据，按编号顺序执行。

## 前端集成
前端应用通过调用 `src/services/api/dailyApi.ts` 中的接口与本服务进行交互。

### 基础配置
前端 API 调用配置：
```typescript
const DAILY_API_BASE_URL = 'http://localhost:8081/api';
```

### 主要接口调用
```typescript
// 获取推荐商品
const recommendations = await getRecommendations();

// 根据时间段获取推荐商品
const morningProducts = await getRecommendationsByTimeSlot('morning');
```

## 开发指南

## 🏗️ 详细项目结构

```
daily-discover-product/
├── src/                        # 源代码目录
│   ├── main/                   # 主要源代码目录
│   │   ├── java/              # Java 源代码
│   │   │   └── com/dailydiscover/ # 主包结构
│   │   │       ├── DailyDiscoverProductApplication.java # 应用程序主类
│   │   │       ├── config/     # 配置类
│   │   │       │   └── StringListTypeHandler.java # 字符串列表类型处理器
│   │   │       ├── controller/ # 控制器层
│   │   │       │   ├── ArticleController.java # 文章控制器
│   │   │       │   ├── ProductController.java # 商品控制器
│   │   │       │   └── TopicController.java # 话题控制器
│   │   │       ├── mapper/     # 数据访问层
│   │   │       │   ├── ArticleMapper.java # 文章数据访问接口
│   │   │       │   ├── ProductMapper.java # 商品数据访问接口
│   │   │       │   └── TopicMapper.java # 话题数据访问接口
│   │   │       ├── model/     # 数据模型层
│   │   │       │   ├── Article.java # 文章实体类
│   │   │       │   ├── Product.java # 商品实体类
│   │   │       │   └── Topic.java # 话题实体类
│   │   │       └── service/   # 服务层
│   │   │           ├── ArticleService.java # 文章服务接口
│   │   │           ├── ArticleServiceImpl.java # 文章服务实现
│   │   │           ├── ProductService.java # 商品服务接口
│   │   │           ├── ProductServiceImpl.java # 商品服务实现
│   │   │           ├── TopicService.java # 话题服务接口
│   │   │           └── TopicServiceImpl.java # 话题服务实现
│   │   └── resources/         # 资源文件目录
│   │       ├── application.properties # 应用配置文件
│   │       ├── db/             # 数据库相关资源
│   │       │   └── migration/  # 数据库迁移脚本
│   │       │       ├── V1__Create_initial_tables.sql # 初始表创建脚本
│   │       │       ├── V2__Add_product_images.sql # 商品图片表添加脚本
│   │       │       ├── V3__Add_article_content.sql # 文章内容表添加脚本
│   │       │       ├── V4__Add_topic_related.sql # 话题相关表添加脚本
│   │       │       ├── V5__Add_user_system.sql # 用户系统表添加脚本
│   │       │       └── V6__Add_recommendation_system.sql # 推荐系统表添加脚本
│   │       ├── logback-spring.xml # 日志配置文件
│   │       └── mapper/         # MyBatis 映射文件
│   │           ├── ArticleMapper.xml # 文章映射配置
│   │           ├── ProductMapper.xml # 商品映射配置
│   │           └── TopicMapper.xml # 话题映射配置
├── pom.xml                     # Maven 项目配置文件
└── README.md                   # 项目说明文档
```

### 目录结构说明

#### 核心目录作用

**src/main/java/com/dailydiscover/** - Java 源代码主包

**DailyDiscoverProductApplication.java** - 应用程序主类
- Spring Boot 应用程序入口点
- 包含应用程序启动配置
- 定义应用程序级别的 Bean

**config/** - 配置类目录
- **StringListTypeHandler.java**: MyBatis 类型处理器，用于处理数据库中的字符串列表类型
- 负责字符串与列表类型之间的转换
- 支持数据库字段与 Java 对象的映射

**controller/** - 控制器层 (MVC 中的 Controller)
- **ArticleController.java**: 文章相关 API 接口控制器
  - 处理文章的 CRUD 操作
  - 提供文章列表、详情、搜索等接口
  - 实现文章推荐功能
- **ProductController.java**: 商品相关 API 接口控制器
  - 处理商品的 CRUD 操作
  - 提供商品列表、详情、分类等接口
  - 实现商品推荐和搜索功能
- **TopicController.java**: 话题相关 API 接口控制器
  - 处理话题的 CRUD 操作
  - 提供话题列表、详情、热门话题等接口
  - 实现话题推荐功能

**mapper/** - 数据访问层 (MyBatis)
- **ArticleMapper.java**: 文章数据访问接口
  - 定义文章相关的数据库操作方法
  - 使用 MyBatis 注解或 XML 映射文件
  - 支持复杂的查询条件和分页
- **ProductMapper.java**: 商品数据访问接口
  - 定义商品相关的数据库操作方法
  - 支持商品分类、价格区间查询
  - 实现商品推荐算法的数据访问
- **TopicMapper.java**: 话题数据访问接口
  - 定义话题相关的数据库操作方法
  - 支持话题热度统计
  - 实现话题关联查询

**model/** - 数据模型层 (Entity)
- **Article.java**: 文章实体类
  - 定义文章数据结构
  - 包含文章标题、内容、作者、发布时间等字段
  - 实现 JPA 实体注解
- **Product.java**: 商品实体类
  - 定义商品数据结构
  - 包含商品名称、价格、描述、图片等字段
  - 支持商品分类和标签
- **Topic.java**: 话题实体类
  - 定义话题数据结构
  - 包含话题标题、描述、热度等字段
  - 支持话题与文章、商品的关联

**service/** - 服务层 (Business Logic)
- **ArticleService.java/ArticleServiceImpl.java**: 文章服务接口及实现
  - 实现文章业务逻辑
  - 提供文章推荐算法
  - 处理文章缓存和性能优化
- **ProductService.java/ProductServiceImpl.java**: 商品服务接口及实现
  - 实现商品业务逻辑
  - 提供商品搜索和过滤功能
  - 处理商品库存和价格管理
- **TopicService.java/TopicServiceImpl.java**: 话题服务接口及实现
  - 实现话题业务逻辑
  - 提供话题热度计算
  - 处理话题关联推荐

**src/main/resources/** - 资源文件目录

**application.properties** - 应用配置文件
- 数据库连接配置
- 服务器端口配置
- 日志级别配置
- 应用自定义配置

**db/migration/** - 数据库迁移脚本
- **001_create_product_core_tables.sql**: 商品核心信息模块
  - 创建商品分类、商品基础信息、商品详情、商品图片、商品规格表
  - 包含完整的初始测试数据
- **002_create_seller_inventory_tables.sql**: 商家与库存模块
  - 创建商家信息、商品SKU、库存管理、库存操作记录表
  - 包含商家数据和库存管理数据
- **003_create_review_interaction_tables.sql**: 评价与互动模块
  - 创建用户评价、评价回复、评价点赞、评价统计、用户行为记录、用户收藏表
  - 包含真实的用户评价和互动数据
- **004_create_relationship_recommendation_tables.sql**: 商品关系与推荐模块
  - 创建相关商品、时间维度数据、商品推荐、搜索关键词、商品标签、标签关联表
  - 包含推荐算法和标签系统数据

**mapper/** - MyBatis 映射文件
- **ArticleMapper.xml**: 文章映射配置
  - 定义文章相关的 SQL 查询
  - 支持复杂查询和联表操作
  - 实现文章推荐查询
- **ProductMapper.xml**: 商品映射配置
  - 定义商品相关的 SQL 查询
  - 支持商品分类和筛选
  - 实现商品搜索功能
- **TopicMapper.xml**: 话题映射配置
  - 定义话题相关的 SQL 查询
  - 支持话题热度统计
  - 实现话题关联查询

**logback-spring.xml** - 日志配置文件
- 定义日志输出格式
- 配置日志文件存储位置
- 设置不同环境的日志级别
- 支持日志滚动和归档

#### 架构设计特点

1. **分层架构**: 采用经典的 MVC 分层架构，清晰分离控制器、服务、数据访问层
2. **RESTful API**: 提供标准的 RESTful API 接口，支持前后端分离
3. **数据库版本管理**: 使用 Flyway 进行数据库版本控制，支持平滑升级
4. **缓存策略**: 在服务层实现缓存机制，提高系统性能
5. **推荐系统**: 内置个性化推荐算法，支持基于用户行为的内容推荐
6. **日志管理**: 完善的日志系统，支持多级别日志输出和文件管理
7. **类型安全**: 使用 MyBatis 类型处理器，确保数据类型安全转换
8. **扩展性**: 模块化设计，易于扩展新功能和业务模块

### 添加新功能
1. 在 `model` 包中创建实体类
2. 在 `repository` 包中创建数据访问接口
3. 在 `service` 包中实现业务逻辑
4. 在 `controller` 包中添加 REST 接口
5. 创建相应的数据库迁移脚本

## 故障排除

### 常见问题

#### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 验证数据库连接配置是否正确
- 确认数据库用户权限

#### 2. 端口被占用
- 修改 `application.properties` 中的 `server.port` 配置
- 或者停止占用 8081 端口的进程

#### 3. 数据库初始化失败
- 检查 MySQL 数据库是否已创建
- 确认数据库用户有创建表和插入数据的权限
- 查看 `application.properties` 中的数据库配置

### 日志查看
应用日志会输出到控制台，包含以下信息：
- SQL 查询语句
- HTTP 请求日志
- 错误信息

## 部署说明

### 生产环境配置
1. 修改 `application.properties` 中的数据库连接信息
2. 设置合适的服务器端口
3. 配置日志级别为生产环境级别

### Docker 部署（可选）
```dockerfile
FROM openjdk:17-jre-slim
COPY target/daily-discover-product-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 许可证
本项目采用 MIT 许可证。