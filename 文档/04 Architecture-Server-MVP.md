下面按你当前 **Flow-MVP、Data-Model-MVP、API-MVP** 的最新约束统一收敛：**不使用外键、图片先用有明确使用许可的在线图片、MVP 不上 Redis/Kafka、服务端围绕一条核心闭环组织**。

# Architecture-Server-MVP

## 目标

确定每日发现 MVP 的最小服务端架构：

**提供内容 → 生成今日发现 → 接收用户行为 → 接收用户判断 → 沉淀数据**

服务端只解决当前产品验证所必需的问题，不提前建设复杂基础设施。

---

# 一、技术选型

| 项目         | 方案                        |

| ---------- | ------------------------- |

| 开发语言       | Java                      |

| 服务端框架      | Spring Boot               |

| API        | REST                      |

| 数据库        | PostgreSQL                |

| ORM / 数据访问 | Spring Data JPA / MyBatis |

| 缓存         | 暂不上 Redis                 |

| 构建         | Maven                     |

| 部署         | Docker                    |

| 反向代理       | Nginx                     |

| 运行环境       | Linux                     |

### 核心原则

**单体服务 + PostgreSQL + REST API**

MVP 阶段不拆微服务，不建设复杂中间件。

---

# 二、总体架构

```text

uni-app x Client

        ↓

      HTTPS

        ↓

      Nginx

        ↓

   Spring Boot

        ↓

 ┌──────┼────────────┐

 ↓      ↓            ↓

内容服务 推荐服务   行为/反馈服务

        ↓            ↓

        └──── PostgreSQL

```

核心数据流：

```text

内容

 ↓

推荐

 ↓

今日发现

 ↓

用户查看

 ↓

行为/判断

 ↓

PostgreSQL

 ↓

数据分析

 ↓

调整内容与推荐

```

MVP 只维护**一套服务、一套数据库、一条业务闭环**。

---

# 三、服务端分层

```text

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

* 调用 Service

* 返回统一 API 响应

* 不处理核心业务规则

## Service

负责：

* 内容查询

* 今日推荐

* 行为记录

* 反馈记录

* 内容状态与可用性判断

* 核心业务规则

## Repository

负责：

* 查询 PostgreSQL

* 写入 PostgreSQL

* 更新 PostgreSQL

* 不承载业务判断

### 一条请求的处理方式

```text

HTTP 请求

 ↓

Controller 校验参数

 ↓

Service 执行业务规则

 ↓

Repository 访问数据库

 ↓

Service 组织结果

 ↓

Controller 返回 JSON

```

---

# 四、核心业务模块

```text

server/

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

负责：

**发现内容的创建、发布、查询与下线。**

核心状态：

```text

DRAFT → PUBLISHED → OFFLINE

```

## Scene

负责：

**生活方案内容。**

一个发现内容对应一个生活方案。

## Product

负责：

**商品信息及商品与生活方案的关系。**

商品图片 MVP 阶段直接使用**有明确使用许可的在线图片 URL**，数据库只保存 URL；不在当前 MVP 建设对象存储。

## Recommendation

负责：

**从已发布内容中生成今日发现。**

不引入机器学习。

## Behavior

负责：

**记录用户实际行为。**

核心行为：

```text

VIEW

CLICK

LIKE

DISLIKE

SKIP

```

`SHARE` 暂保留数据模型扩展能力，MVP 不作为核心闭环。

## Feedback

负责：

**记录用户明确反馈。**

核心反馈：

```text

USEFUL

NOT_USEFUL

LIKE

DISLIKE

NOT_INTERESTED

```

## Common

负责：

* 统一响应

* 全局异常处理

* 参数校验

* 公共配置

* 公共工具

---

# 五、核心业务闭环

服务端必须围绕这一条链实现：

```text

内容

 ↓

推荐

 ↓

发现

 ↓

理解

 ↓

判断

 ↓

反馈

 ↓

行为数据

 ↓

调整内容与推荐

```

对应服务端职责：

```text

Content

  ↓

Recommendation

  ↓

Discover API

  ↓

Client 展示

  ↓

Behavior / Feedback API

  ↓

PostgreSQL

```

**这条链能够完整运行，就是 MVP 服务端最重要的完成标准。**

---

# 六、核心 API

MVP 只实现 4 个核心接口：

```text

GET  /api/v1/discover/today

GET  /api/v1/discover/{id}

POST /api/v1/behaviors

POST /api/v1/feedbacks

```

职责：

| API               | 作用       |

| ----------------- | -------- |

| `/discover/today` | 获取今日发现   |

| `/discover/{id}`  | 获取完整发现详情 |

| `/behaviors`      | 记录用户实际行为 |

| `/feedbacks`      | 记录用户明确判断 |

客户端不直接访问数据库。

API 具体字段与错误码以：

[`API-MVP.md`](http://API-MVP.md)

为唯一契约。

---

# 七、匿名用户

MVP 不要求登录。

客户端首次运行生成：

```text

anonymous_id

```

之后每次请求通过：

```text

X-Anonymous-Id: {anonymous_id}

```

发送。

服务端：

```text

收到 anonymous_id

 ↓

查询 users

 ↓

不存在 → 创建用户

 ↓

继续处理请求

```

用户体系只承担一件事：

**把同一个人的行为与反馈串起来。**

不建设：

* 注册

* 登录

* 密码

* OAuth

* 用户资料

* 权限体系

* 社交关系

---

# 八、推荐服务

MVP 推荐采用规则方式：

```text

全部内容

 ↓

状态过滤

 ↓

时间过滤

 ↓

人工优先级

 ↓

简单行为排序

 ↓

今日推荐

```

## 第一层：状态过滤

只允许：

```text

PUBLISHED

```

进入推荐。

## 第二层：时间过滤

过滤已经过期的内容：

```text

expires_at &lt; 当前时间

```

不再推荐。

## 第三层：人工优先级

根据：

```text

priority

```

确定基础推荐顺序。

## 第四层：简单行为排序

根据已经产生的真实行为数据进行基础调整。

MVP 不建立复杂推荐模型，只验证：

**用户是否愿意持续接受“帮他筛选好的发现”。**

---

# 九、推荐服务可替换

推荐模块与其他业务模块保持清晰边界：

```text

Recommendation

      ↓

输入：候选内容 + 行为数据

      ↓

输出：今日内容列表

```

当前实现：

```text

规则推荐

```

未来可以替换为：

```text

规则推荐

   ↓

个性化推荐

   ↓

算法推荐

```

但客户端不需要因为推荐方式变化而修改核心业务流程。

---

# 十、内容服务

内容服务提供客户端可以**直接展示和理解**的数据。

核心链：

```text

Content

 ↓

Scene

 ↓

Product

```

详情 API 直接返回完整结构：

```text

发现内容

 ├─ 基本信息

 ├─ 生活方案

 └─ 商品信息

```

客户端不负责：

* 查询数据库

* 拼接业务关系

* 判断内容是否发布

* 判断内容是否过期

* 执行推荐规则

### 内容原则

**服务端组织业务数据，客户端负责展示与交互。**

---

# 十一、图片策略

MVP 阶段暂不建设对象存储。

图片采用：

```text

有明确使用许可的在线图片

        ↓

数据库保存 image_url

        ↓

API 返回 image_url

        ↓

客户端直接加载

```

当前不建设：

* R2

* OSS

* COS

* OBS

* 图片上传系统

* 图片处理服务

这样可以先验证产品，而不是提前建设图片基础设施。

如果产品验证成功，后续只需将图片 URL 替换为自有对象存储地址，核心数据模型无需改变。

---

# 十二、行为服务

所有核心用户行为通过 API 上报。

例如：

```json

{

  "contentId": 1001,

  "behaviorType": "VIEW"

}

```

核心行为：

```text

VIEW

CLICK

LIKE

DISLIKE

SKIP

```

服务端采用**追加记录**方式保存行为：

```text

用户行为

 ↓

POST /behaviors

 ↓

Service

 ↓

PostgreSQL

```

不在 MVP 阶段引入：

* Kafka

* Flink

* 实时数仓

* 消息队列

* 复杂事件流

因为当前数据量和验证目标都不需要这些基础设施。

---

# 十三、反馈服务

用户形成明确判断后提交反馈。

例如：

```json

{

  "contentId": 1001,

  "feedbackType": "USEFUL"

}

```

处理链：

```text

用户判断

 ↓

POST /feedbacks

 ↓

校验内容与反馈类型

 ↓

写入 PostgreSQL

 ↓

进入产品分析

```

反馈用于判断：

```text

哪些内容值得继续

哪些内容需要调整

哪些推荐方向应该减少

```

反馈不是单独存在的功能，而是验证产品价值的重要数据入口。

---

# 十四、数据库

MVP 使用：

**PostgreSQL**

核心表：

```text

users

contents

scenes

products

scene_products

behaviors

feedbacks

```

完整字段、索引、状态和数据关系统一维护在：

[`Data-Model-MVP.md`](http://Data-Model-MVP.md)

服务端不重复定义第二套数据模型。

### 数据关系

数据库**不使用外键**。

关系通过：

```text

业务 ID

+

Service 业务校验

```

维护。

例如：

```text

scenes.content_id

behaviors.content_id

feedbacks.content_id

scene_products.scene_id

scene_products.product_id

```

只保存关联 ID，不建立 PostgreSQL Foreign Key。

---

# 十五、缓存策略

MVP：

**不上 Redis。**

判断链：

```text

数据量小

 ↓

查询压力低

 ↓

PostgreSQL 足够

 ↓

减少基础设施

 ↓

加快验证

```

只有出现明确性能瓶颈后，才针对具体问题增加缓存。

可能增加：

```text

Redis

 ↓

今日推荐缓存

热门内容缓存

```

而不是为了“以后可能需要”提前建设。

---

# 十六、事务边界

MVP 只在需要保证一次业务操作完整性的地方使用数据库事务。

例如：

```text

创建/发布内容

 ↓

保存对应生活方案

 ↓

保存商品关系

```

或者：

```text

处理一次反馈

 ↓

写入反馈记录

```

不建立跨服务、分布式事务体系。

---

# 十七、错误处理

统一成功响应：

```json

{

  "code": 0,

  "message": "success",

  "data": {}

}

```

统一错误结构：

```json

{

  "code": 4001,

  "message": "content not found",

  "data": null

}

```

核心错误码：

```text

4000 INVALID_PARAMETER

4001 CONTENT_NOT_FOUND

4002 CONTENT_NOT_AVAILABLE

4003 INVALID_BEHAVIOR

4004 INVALID_FEEDBACK

5000 INTERNAL_ERROR

```

异常统一由 Common 层处理：

```text

参数错误

 ↓

业务异常

 ↓

资源不存在

 ↓

系统异常

 ↓

统一 JSON 返回

```

客户端不依赖 Java 异常类型或数据库错误。

---

# 十八、工程结构

```text

daily-discovery-server/

│

├─ src/main/java/

│  └─ com/.../

│     ├─ content/

│     │  ├─ controller/

│     │  ├─ service/

│     │  ├─ repository/

│     │  └─ entity/

│     │

│     ├─ scene/

│     ├─ product/

│     ├─ recommendation/

│     ├─ behavior/

│     ├─ feedback/

│     └─ common/

│        ├─ response/

│        ├─ exception/

│        ├─ validation/

│        └─ config/

│

├─ src/main/resources/

│  ├─ application.yml

│  └─ db/

│

├─ pom.xml

└─ Dockerfile

```

模块内部统一：

```text

Controller

 ↓

Service

 ↓

Repository

 ↓

Entity

```

不为了 MVP 强行拆出更多层。

---

# 十九、开发顺序

服务端不要按照“先把所有后端做完”推进，而是直接跑通一条**纵向闭环**：

```text

数据库

 ↓

今日发现

 ↓

详情

 ↓

行为

 ↓

反馈

 ↓

客户端完整调用

```

具体顺序：

```text

1. 建 PostgreSQL

      ↓

2. 建 7 张核心表

      ↓

3. 准备可发布内容与商品数据

      ↓

4. 实现今日发现 API

      ↓

5. 实现详情 API

      ↓

6. 实现行为 API

      ↓

7. 实现反馈 API

      ↓

8. 客户端接入

      ↓

9. 跑通完整闭环

```

不要先建设：

```text

Redis

Kafka

微服务

复杂推荐

用户中心

CMS

```

---

# 二十、运行方式

## 开发环境

```text

Spring Boot

     ↓

PostgreSQL

```

客户端直接调用 Spring Boot。

## 生产环境

```text

Internet

   ↓

 Nginx

   ↓

Spring Boot Container

   ↓

PostgreSQL

```

Docker 只负责稳定运行服务，不增加复杂容器编排。

---

# 二十一、MVP 不建设

当前明确不做：

```text

微服务

Kubernetes

Redis

Kafka

Flink

Service Mesh

分布式事务

机器学习推荐

复杂用户画像

实时数据流

AI 自动生成内容

社区

社交

登录注册

复杂 CMS

交易系统

营销系统

```

判断标准只有一个：

&gt; **如果它不能直接帮助验证“用户是否愿意持续获得每日发现”，就暂不进入 MVP。**

---

# 二十二、服务端 MVP 完成标准

```text

[ ] Spring Boot 工程建立

[ ] PostgreSQL 连接成功

[ ] 7 张核心表建立

[ ] 测试内容与商品数据准备完成

[ ] 今日发现 API 完成

[ ] 详情 API 完成

[ ] 行为 API 完成

[ ] 反馈 API 完成

[ ] anonymous_id 能正常识别

[ ] 行为成功落库

[ ] 反馈成功落库

[ ] 客户端可以正常调用

[ ] 发现 → 理解 → 判断 → 反馈完整跑通

[ ] 能从数据库看到完整行为数据

```

---

# 二十三、架构原则

**单体优先 → 数据直存 → API 简单 → 业务集中 → 行为可追踪 → 推荐可替换 → 基础设施最小化**

服务端当前唯一核心目标：

&gt; **稳定支撑真实用户完成「发现 → 理解 → 判断 → 反馈」，并把真实行为沉淀为下一轮产品判断依据。**

