# daily-discover-server｜每日发现服务端 MVP

依据《01 Data-Model-MVP》《03 API-MVP》《04 Architecture-Server-MVP》实现的单体服务端。

## 技术栈

- Java 17 + Spring Boot 3.2（Web / Validation / Data JPA / Actuator）
- PostgreSQL（192.168.1.63:5432/daily_discover）
- Maven（`./mvnw`）

## 数据库初始化

```bash
PGPASSWORD='<密码>' psql -h 192.168.1.63 -p 5432 -U postgres -d postgres \
  -c "CREATE DATABASE daily_discover;"
PGPASSWORD='<密码>' psql -h 192.168.1.63 -p 5432 -U postgres \
  -d daily_discover -f sql/mvp_init.sql
```

5 张核心表（无数据库外键）：`users` / `discoveries` / `products` / `discovery_products` / `behaviors`。
内置 10 条真实发现 + 24 件商品种子数据。

## 运行

```bash
./mvnw clean package -DskipTests
java -jar target/daily-discover-server-1.0.0-SNAPSHOT.jar
# 默认 http://127.0.0.1:8080/api
```

## P0 接口（所有请求需携带 X-Anonymous-Id 请求头）

| 接口 | 说明 |
| --- | --- |
| `GET /api/v1/discoveries/today` | 今日发现（PUBLISHED + 未过期，priority DESC） |
| `GET /api/v1/discoveries/{id}` | 发现详情（含商品列表） |
| `POST /api/v1/behaviors` | 上报行为（IMPRESSION / DETAIL_VIEW / INTERESTED / NOT_INTERESTED / SKIP / ACTION_CLICK，追加记录） |
| `GET /api/v1/health` | 健康检查 |

## 统一响应与错误码

```json
{ "code": 0, "message": "success", "data": {} }
```

| 错误码 | 含义 | HTTP |
| --- | --- | --- |
| 4000 | INVALID_PARAMETER | 400 |
| 4001 | DISCOVERY_NOT_FOUND | 404 |
| 4002 | DISCOVERY_NOT_AVAILABLE | 404 |
| 4003 | INVALID_BEHAVIOR | 400 |
| 5000 | INTERNAL_ERROR | 500 |
