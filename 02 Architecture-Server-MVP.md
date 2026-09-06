# Architecture-Server-MVP

## 目标

确定每日发现 MVP 的服务端技术方案，为客户端提供稳定的内容、推荐、行为与反馈能力，并支撑后续产品验证。

---

# 一、技术选型

| 项目         | 方案                        |
| ---------- | ------------------------- |
| 开发语言       | Java                      |
| 服务端框架      | Spring Boot               |
| API        | REST                      |
| 数据库        | PostgreSQL                |
| 缓存         | 暂不上 Redis                 |
| ORM / 数据访问 | Spring Data JPA / MyBatis |
| 构建         | Maven                     |
| 部署         | Docker                    |
| Web / 反向代理 | Nginx                     |
| 运行环境       | Linux                     |

---

# 二、总体架构

```text id="byk2kq"
uni-app x Client
        ↓
      HTTPS
        ↓
      Nginx
        ↓
   Spring Boot
        ↓
 ┌──────┼────────┐
 ↓      ↓        ↓
Content Recommend Behavior
        ↓
     PostgreSQL
```

MVP 采用：

**单体服务 + 单数据库**

不拆分微服务。

---

# 三、服务端分层

```text id="xd1d3n"
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

## Controller

负责：

* 接收 HTTP 请求
* 参数校验
* 返回统一响应

## Service

负责：

* 核心业务逻辑
* 推荐逻辑
* 行为处理
* 反馈处理

## Repository

负责：

* 数据查询
* 数据写入
* 数据更新

---

# 四、核心业务模块

```text id="p87z6k"
daily-discover-server/
│
├─ content
├─ scene
├─ product
├─ recommendation
├─ behavior
├─ feedback
└─ common
```

## Content

管理发现内容。

## Scene

管理生活方案。

## Product

管理商品及其关系。

## Recommendation

生成今日发现。

## Behavior

记录用户行为。

## Feedback

记录用户判断与反馈。

## Common

提供：

* API 响应
* 异常处理
* 参数校验
* 公共工具
* 配置

---

# 五、核心 API

MVP 首先提供：

```text id="0vv2xk"
GET  /api/v1/discover/today
GET  /api/v1/discover/{id}

POST /api/v1/behaviors
POST /api/v1/feedbacks
```

这 4 个接口必须支撑完整核心闭环。

---

# 六、匿名用户

服务端不要求登录。

客户端通过：

```text id="v1w2y1"
X-Anonymous-Id
```

提交匿名用户标识。

服务端不负责复杂账户体系。

MVP 只需要能够：

```text id="x6dbid"
识别匿名用户
 ↓
关联行为
 ↓
关联反馈
```

---

# 七、推荐服务

MVP 不使用机器学习推荐。

采用规则推荐：

```text id="26sg5h"
候选内容
 ↓
状态过滤
 ↓
时间过滤
 ↓
人工优先级
 ↓
简单行为排序
 ↓
生成今日结果
```

推荐服务必须能够独立替换。

当前重点不是“推荐算法先进”，而是获得真实用户反馈数据。

---

# 八、内容服务

内容服务负责：

```text id="9qu1i4"
内容查询
 ↓
内容详情
 ↓
场景
 ↓
商品
```

核心原则：

> 客户端拿到的是可以直接展示的完整内容，而不是要求客户端自行拼装复杂业务关系。

---

# 九、行为服务

用户每次核心行为都通过 API 上报。

例如：

```json id="jzdeq8"
{
  "contentId": 1001,
  "behaviorType": "VIEW"
}
```

支持：

```text id="x2e0nw"
VIEW
CLICK
LIKE
DISLIKE
SHARE
SKIP
```

服务端负责写入 PostgreSQL。

---

# 十、反馈服务

用户明确判断时提交反馈。

例如：

```json id="r3rb1r"
{
  "contentId": 1001,
  "feedbackType": "USEFUL"
}
```

反馈用于：

```text id="c8ex4p"
用户判断
 ↓
数据沉淀
 ↓
产品判断
 ↓
内容 / 推荐调整
```

---

# 十一、数据库

MVP 直接使用：

**PostgreSQL**

核心实体：

```text id="a4ps6t"
User
Content
Scene
Product
SceneProduct
Behavior
Feedback
```

数据库设计详见：

`Data-Model-MVP.md`

服务端不再单独维护另一套数据定义。

---

# 十二、缓存策略

MVP **暂不上 Redis**。

原因：

```text id="m9j89v"
数据量小
 ↓
查询压力低
 ↓
PostgreSQL 足够
 ↓
减少基础设施
 ↓
加快 MVP 开发
```

后续出现明确性能问题，再针对性增加：

* Redis
* 推荐结果缓存
* 热门内容缓存

而不是预先建设缓存系统。

---

# 十三、错误处理

统一返回：

```json id="e08ykn"
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

异常统一处理：

```text id="z6gk3o"
参数错误
资源不存在
业务错误
服务异常
```

客户端不直接依赖数据库或内部实现。

---

# 十四、服务端工程结构

```text id="mmh7sy"
daily-discovery-server/
│
├─ src/main/java/
│  └─ com/.../
│     ├─ content/
│     ├─ scene/
│     ├─ product/
│     ├─ recommendation/
│     ├─ behavior/
│     ├─ feedback/
│     └─ common/
│
├─ src/main/resources/
│  ├─ application.yml
│  └─ db/
│
├─ pom.xml
└─ Dockerfile
```

---

# 十五、运行方式

开发环境：

```text id="3dbf9l"
Spring Boot
    ↓
PostgreSQL
```

生产环境：

```text id="47vl6o"
Internet
 ↓
Nginx
 ↓
Spring Boot Container
 ↓
PostgreSQL
```

MVP 不引入：

* Kubernetes
* 微服务
* 消息队列
* Service Mesh
* 分布式事务

---

# 十六、服务端 MVP 完成标准

```text id="2ad4rl"
[ ] Spring Boot 工程建立
[ ] PostgreSQL 连接成功
[ ] 核心数据模型建立
[ ] 今日发现 API 完成
[ ] 详情 API 完成
[ ] 行为 API 完成
[ ] 反馈 API 完成
[ ] API 可被客户端正常调用
[ ] 行为与反馈成功落库
[ ] 核心闭环可以完整运行
```

---

# 十七、架构原则

> **单体优先、PostgreSQL 直连、API 简单、数据可追踪、推荐可替换、基础设施最小化。**

服务端当前唯一核心目标：

> **稳定支撑真实用户完成「发现 → 理解 → 判断 → 反馈」，并沉淀可用于产品判断的真实数据。**
