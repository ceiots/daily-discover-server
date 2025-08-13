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
1. 创建商品表（如果不存在）
2. 插入示例数据

### 数据表结构
```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    time_slot VARCHAR(50),
    category VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

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

### 项目结构
```
daily-discover-product/
├── src/main/java/com/dailydiscover/
│   ├── DailyDiscoverProductApplication.java    # 主应用类
│   ├── controller/                             # 控制器层
│   │   └── ProductController.java
│   ├── service/                               # 服务层
│   │   └── ProductService.java
│   ├── repository/                            # 数据访问层
│   │   └── ProductRepository.java
│   ├── model/                                # 数据模型
│   │   └── Product.java
│   └── config/                               # 配置类
│       └── DataInitializer.java
├── src/main/resources/
│   ├── application.properties                # 应用配置
│   └── db/migration/                         # 数据库迁移脚本
│       ├── V1__Create_Product_Table.sql
│       └── V2__Insert_Sample_Data.sql
└── pom.xml                                   # Maven 配置
```

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