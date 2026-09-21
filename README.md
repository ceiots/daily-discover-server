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

## 运行（Docker，推荐）

前置：本机 PostgreSQL 运行在 `192.168.1.63:5432`（即本机），Docker 守护进程已配置代理（见下方"网络注意"）。

```bash
./mvnw clean package -DskipTests        # 先本地构建 JAR（Dockerfile 直接复制 target/*.jar）
docker compose up -d --build           # 构建镜像并启动
docker compose ps                      # 查看状态（health=healthy 即成功）
docker compose logs -f                 # 跟踪日志
docker compose restart                 # 重启
docker compose down                    # 停止并删除容器
```

- 服务端口：`8080`（对外 `https://api.dailydiscover.cloud/api`）
- 日志文件：`/home/sshuser/logs/daily-discover/server.log`（已挂载出容器）
- 健康检查：`curl http://localhost:8080/api/actuator/health`
- 容器内通过 `host.docker.internal`（compose 中 `extra_hosts: host-gateway`）访问宿主机 PostgreSQL

### 网络注意（镜像拉取）

`openjdk` 官方镜像已从 Docker Hub 下架，基础镜像改用 `eclipse-temurin:17-jre`。
国内直连 Docker Hub 不通，dockerd 需走本机 clash 代理：

```bash
# /etc/systemd/system/docker.service.d/proxy.conf
[Service]
Environment="HTTP_PROXY=http://127.0.0.1:7893"
Environment="HTTPS_PROXY=http://127.0.0.1:7893"
Environment="NO_PROXY=localhost,127.0.0.1,::1"
```

改后 `sudo systemctl daemon-reload && sudo systemctl restart docker`。
`/etc/docker/daemon.json` 的 `registry-mirrors` 保持为空数组（baidu/163/ustc 等公共镜像源已停服，配置它们会导致 pull 失败）。

## 运行（本地直跑，开发调试用）

```bash
./mvnw spring-boot:run
# 或
./mvnw clean package -DskipTests
java -jar target/daily-discover-server-1.0.0-SNAPSHOT.jar
# 默认 http://127.0.0.1:8080/api，与 Docker 容器互斥（都占用 8080）
```

## P0 接口（所有请求需携带 X-Anonymous-Id 请求头）


| 接口                              | 说明                                                                                      |
| ------------------------------- | --------------------------------------------------------------------------------------- |
| `GET /api/v1/discoveries/today` | 今日发现（PUBLISHED + 未过期，priority DESC）                                                     |
| `GET /api/v1/discoveries/{id}`  | 发现详情（含商品列表）                                                                             |
| `POST /api/v1/behaviors`        | 上报行为（IMPRESSION / DETAIL_VIEW / INTERESTED / NOT_INTERESTED / SKIP / ACTION_CLICK，追加记录） |
| `GET /api/v1/health`            | 健康检查                                                                                    |


## 统一响应与错误码

```json
{ "code": 0, "message": "success", "data": {} }
```


| 错误码  | 含义                      | HTTP |
| ---- | ----------------------- | ---- |
| 4000 | INVALID_PARAMETER       | 400  |
| 4001 | DISCOVERY_NOT_FOUND     | 404  |
| 4002 | DISCOVERY_NOT_AVAILABLE | 404  |
| 4003 | INVALID_BEHAVIOR        | 400  |
| 5000 | INTERNAL_ERROR          | 500  |    