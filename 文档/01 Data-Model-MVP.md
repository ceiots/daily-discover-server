已统一调整为**不使用数据库外键**。实体之间仍保留 ID 关联，但完整性由 Spring Boot 业务代码保证，数据库只负责字段、索引和基本约束。

# Data-Model-MVP｜每日发现

## 1. 目标

定义「每日发现」MVP需要保存的最少数据，让系统能够直接支撑：

&gt; **准备发现 → 展示发现 → 用户查看 → 用户判断 → 用户行动 → 保存行为 → 调整下一轮推荐**

数据库设计只服务当前 MVP 验证，不提前建设复杂用户画像、推荐特征和实时数据系统。

---

# 2. 核心数据

MVP只保留 5 类核心数据：

```text

User

Discovery

Product

DiscoveryProduct

Behavior

```

关系：

```text

User

  │

  ↓

Behavior ─────→ Discovery

                  │

                  ↓

           DiscoveryProduct

                  │

                  ↓

               Product

```

其中：

* `User`：知道这是哪个匿名用户

* `Discovery`：用户真正看到的一条每日发现

* `Product`：发现中涉及的具体商品

* `DiscoveryProduct`：一个发现包含哪些商品

* `Behavior`：用户对发现做了什么

**MVP不单独建立 Feedback 表。**

因为：

&gt; **“感兴趣 / 不感兴趣 / 跳过”本身就是用户反馈。**

---

# 3. User｜匿名用户

用于识别不登录的用户，并判断用户之后有没有再次回来。

```text

users

├─ id

├─ anonymous_id

├─ created_at

└─ updated_at

```

| 字段           | 类型          | 说明           |

| ------------ | ----------- | ------------ |

| id           | BIGSERIAL   | 主键           |

| anonymous_id | VARCHAR(64) | 手机端生成的匿名用户标识 |

| created_at   | TIMESTAMPTZ | 创建时间         |

| updated_at   | TIMESTAMPTZ | 最后更新时间       |

约束：

```sql

UNIQUE (anonymous_id)

```

MVP不做：

```text

手机号

密码

OAuth

头像

昵称

个人资料

登录体系

```

---

# 4. Discovery｜每日发现

这是 MVP 最核心的数据。

它表示：

&gt; **用户今天看到的一条完整生活发现。**

```text

discoveries

├─ id

├─ name

├─ scene

├─ reason

├─ suitable_for

├─ price

├─ cover_url

├─ action_title

├─ action_url

├─ status

├─ priority

├─ published_at

├─ expires_at

├─ created_at

└─ updated_at

```

| 字段           | 类型            | 说明          |

| ------------ | ------------- | ----------- |

| id           | BIGSERIAL     | 主键          |

| name         | VARCHAR(200)  | 发现名称        |

| scene        | TEXT          | 用户面对的具体生活场景 |

| reason       | TEXT          | 为什么值得关注     |

| suitable_for | TEXT          | 适合什么人、什么场景  |

| price        | NUMERIC(10,2) | 参考价格，可为空    |

| cover_url    | TEXT          | 发现封面        |

| action_title | VARCHAR(100)  | 下一步行动名称     |

| action_url   | TEXT          | 下一步行动地址     |

| status       | VARCHAR(32)   | 内容状态        |

| priority     | INT           | 人工设置的重要程度   |

| published_at | TIMESTAMPTZ   | 发布时间        |

| expires_at   | TIMESTAMPTZ   | 失效时间        |

| created_at   | TIMESTAMPTZ   | 创建时间        |

| updated_at   | TIMESTAMPTZ   | 最后更新时间      |

### status

```text

DRAFT

PUBLISHED

OFFLINE

```

### 为什么把 Scene 合进 Discovery

MVP中：

&gt; **一条发现就是一个完整生活方案。**

没有必要先拆成：

```text

Content

↓

Scene

```

否则只是增加一层数据库结构，却没有帮助当前验证。

以后真的出现“一条内容对应多个生活方案”时，再拆表。

---

# 5. Product｜商品

表示一条发现中涉及的具体商品。

```text

products

├─ id

├─ name

├─ description

├─ price

├─ image_url

├─ purchase_url

├─ platform

├─ status

├─ metadata

├─ created_at

└─ updated_at

```

| 字段           | 类型            | 说明            |

| ------------ | ------------- | ------------- |

| id           | BIGSERIAL     | 主键            |

| name         | VARCHAR(200)  | 商品名称          |

| description  | TEXT          | 商品说明          |

| price        | NUMERIC(10,2) | 当前价格          |

| image_url    | TEXT          | 商品图片          |

| purchase_url | TEXT          | 商品链接          |

| platform     | VARCHAR(32)   | 商品所在平台        |

| status       | VARCHAR(32)   | 商品是否还能使用      |

| metadata     | JSONB         | 暂时不值得单独建字段的信息 |

| created_at   | TIMESTAMPTZ   | 创建时间          |

| updated_at   | TIMESTAMPTZ   | 最后更新时间        |

### status

```text

ACTIVE

INACTIVE

```

---

# 6. DiscoveryProduct｜发现与商品

一个发现可以包含多个商品，一个商品以后也可能出现在多个发现中。

```text

discovery_products

├─ discovery_id

├─ product_id

├─ quantity

└─ sort_order

```

| 字段           | 类型     | 说明    |

| ------------ | ------ | ----- |

| discovery_id | BIGINT | 发现 ID |

| product_id   | BIGINT | 商品 ID |

| quantity     | INT    | 需要几个  |

| sort_order   | INT    | 展示顺序  |

主键：

```sql

PRIMARY KEY (discovery_id, product_id)

```

关系：

```text

Discovery N ─── N Product

```

这里的 `discovery_id` 和 `product_id` **只作为普通字段保存，不建立数据库外键**。

---

# 7. Behavior｜用户行为

记录用户真正做过什么。

```text

behaviors

├─ id

├─ anonymous_id

├─ discovery_id

├─ behavior_type

├─ metadata

└─ created_at

```

| 字段            | 类型          | 说明            |

| ------------- | ----------- | ------------- |

| id            | BIGSERIAL   | 主键            |

| anonymous_id  | VARCHAR(64) | 匿名用户标识        |

| discovery_id  | BIGINT      | 哪条发现          |

| behavior_type | VARCHAR(32) | 用户做了什么        |

| metadata      | JSONB       | 暂时不需要单独建字段的信息 |

| created_at    | TIMESTAMPTZ | 行为发生时间        |

这里的 `anonymous_id` 和 `discovery_id` **只保存业务 ID，不建立数据库外键**。

---

# 8. Behavior类型

与 `Flow-MVP` 完全统一，只保留真正需要验证的行为：

```text

IMPRESSION

DETAIL_VIEW

INTERESTED

NOT_INTERESTED

SKIP

ACTION_CLICK

```

### IMPRESSION

用户真正看到发现卡片。

### DETAIL_VIEW

用户打开详情。

### INTERESTED

用户明确表示感兴趣。

### NOT_INTERESTED

用户明确表示不感兴趣。

### SKIP

用户主动跳过。

### ACTION_CLICK

用户进一步点击：

```text

查看商品

阅读内容

打开外部页面

```

---

# 9. 为什么不单独建立 Feedback 表

原来的：

```text

Behavior

+

Feedback

```

存在重复。

例如：

```text

LIKE

DISLIKE

NOT_INTERESTED

USEFUL

```

既可以作为行为，也可以作为反馈。

MVP统一为：

```text

用户判断

↓

Behavior

↓

INTERESTED / NOT_INTERESTED / SKIP

```

这样数据库、API、前端三边只维护一套定义。

---

# 10. 数据之间怎么关联

数据库不使用外键。

业务上仍然通过 ID 关联：

```text

[users.id](http://users.id)

   ↑

anonymous_id

   │

behaviors.anonymous_id

[discoveries.id](http://discoveries.id)

   ↑

behaviors.discovery_id

[discoveries.id](http://discoveries.id)

   ↑

discovery_products.discovery_id

[products.id](http://products.id)

   ↑

discovery_products.product_id

```

这些关联由 **Spring Boot 业务代码**负责检查。

例如创建 `Behavior` 前：

```text

收到 discovery_id

↓

检查 discovery 是否存在

↓

检查 discovery 是否允许记录行为

↓

写入 behaviors

```

这样：

&gt; **数据库保持简单，业务规则集中在服务端。**

---

# 11. 核心数据关系

逻辑关系：

```text

User 1 ─── N Behavior

Discovery 1 ─── N Behavior

Discovery N ─── N Product

```

实际数据库只保存普通 ID 字段，不建立外键约束。

---

# 12. 索引

只建立当前查询真正需要的索引。

## users

```sql

CREATE UNIQUE INDEX uk_users_anonymous_id

ON users (anonymous_id);

```

## discoveries

每天主要查询：

&gt; 已发布、仍然有效、优先级较高的发现。

```sql

CREATE INDEX idx_discoveries_status_time_priority

ON discoveries (

    status,

    published_at,

    expires_at,

    priority

);

```

## discovery_products

```sql

CREATE INDEX idx_discovery_products_product_id

ON discovery_products (product_id);

```

## behaviors

查询某个用户的行为：

```sql

CREATE INDEX idx_behaviors_user_time

ON behaviors (

    anonymous_id,

    created_at

);

```

查询某条发现的表现：

```sql

CREATE INDEX idx_behaviors_discovery_type_time

ON behaviors (

    discovery_id,

    behavior_type,

    created_at

);

```

---

# 13. 数据库只承担什么

数据库负责：

```text

保存数据

+

保证主键唯一

+

保证 anonymous_id 唯一

+

提供查询和索引

```

数据库不负责：

```text

发现是否存在

发现是否已经下线

商品是否还能使用

用户是否允许重复操作

业务流程是否合法

```

这些统一由：

&gt; **Spring Boot 服务端负责。**

---

# 14. 时间

数据库统一使用：

```text

TIMESTAMPTZ

```

服务端统一保存 UTC 时间。

客户端根据用户所在时区显示时间。

---

# 15. ID

MVP使用：

```text

BIGSERIAL

```

暂不引入：

```text

UUID

Snowflake

分布式 ID

```

当前只有一个 Spring Boot 服务和一个 PostgreSQL 数据库，没有提前引入复杂 ID 方案的必要。

---

# 16. 今日发现需要查询什么

服务端查询今日发现时：

```text

status = PUBLISHED

```

同时检查：

```text

published_at &lt;= 当前时间

```

以及：

```text

expires_at 为空

或

expires_at &gt; 当前时间

```

最后：

```text

priority DESC

```

得到今天可以展示的发现。

---

# 17. MVP初始化数据

开发阶段至少准备：

```text

10 条真实发现

+

每条发现对应的商品

```

每条发现必须写完整：

```text

发现名称

+

生活场景

+

为什么值得

+

适合谁

+

下一步行动

```

这样前端接入 API 后，不需要长期依赖假数据。

---

# 18. 最小建库顺序

```text

users

↓

discoveries

↓

products

↓

discovery_products

↓

behaviors

```

注意：

&gt; **这里只是建表顺序，不代表数据库存在外键依赖。**

---

# 19. Data Model完成标准

```text

[ ] users 建立

[ ] discoveries 建立

[ ] products 建立

[ ] discovery_products 建立

[ ] behaviors 建立

[ ] 主键建立

[ ] anonymous_id 唯一约束建立

[ ] 核心索引建立

[ ] 内容状态统一

[ ] 行为类型统一

[ ] 不使用数据库外键

[ ] 今日发现可以查询

[ ] 详情需要的数据可以查询

[ ] 用户判断可以写入

[ ] 用户点击可以写入

[ ] 可以统计每条发现的实际表现

```

最终要求：

&gt; **数据库能够直接支撑一条真实 MVP 链路：发现可以发布，用户可以看到、查看、判断、继续行动，所有行为都能保存下来。**

这样 `Data-Model-MVP` 与你前面确定的 **Flow-MVP** 已经统一，并且数据库完全不依赖外键。

