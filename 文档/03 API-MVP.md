

# API-MVP｜每日发现

## 1. 目标

定义「每日发现」MVP的前后端接口，让客户端知道：

&gt; **什么时候调用什么接口、传什么数据、得到什么数据。**

让服务端能够直接实现：

&gt; **展示发现 → 查看详情 → 用户判断 → 用户行动 → 保存行为**

接口设计只服务当前 MVP，不提前建设复杂推荐、用户画像、历史记录和交易系统。

---

# 2. API基础规范

## Base URL

```text

https://api.dailydiscover.cloud/api/v1

```

## 协议

```text

HTTPS

JSON

REST

```

## 请求头

所有接口都携带：

```http

Content-Type: application/json

X-Anonymous-Id: {anonymous_id}

```

不使用登录 Token。

---

# 3. anonymous_id

客户端第一次启动时生成：

```text

anonymous_id

```

并保存到本地。

例如：

```text

a7f83d91-xxxx-xxxx-xxxx

```

之后每次请求都携带：

```http

X-Anonymous-Id: a7f83d91-xxxx-xxxx-xxxx

```

服务端收到后：

```text

收到 anonymous_id

↓

查询 users

↓

不存在 → 创建匿名用户

↓

继续处理请求

```

客户端不需要知道服务端是否刚创建用户。

---

# 4. 统一响应

## 成功

```json

{

  "code": 0,

  "message": "success",

  "data": {}

}

```

## 失败

```json

{

  "code": 4001,

  "message": "discovery not found",

  "data": null

}

```

约定：

```text

code = 0

→ 请求成功

code != 0

→ 请求失败

```

HTTP 状态码仍按实际情况返回：

```text

200

400

404

500

```

---

# 5. 获取今日发现

## GET /discoveries/today

获取今天应该展示给用户的发现。

### Request

无 Body。

请求头：

```http

X-Anonymous-Id: {anonymous_id}

```

### Response

```json

{

  "code": 0,

  "message": "success",

  "data": {

    "date": "2026-09-19",

    "items": [

      {

        "id": 1001,

        "name": "把阳台变成一个适合晚饭后放松的小空间",

        "scene": "下班后想在家放松，但阳台一直没有被真正使用起来。",

        "reason": "用少量预算增加灯光、桌椅和收纳，就能把原本闲置的阳台变成每天都能使用的休闲空间。",

        "suitableFor": "有阳台、希望增加居家休闲空间的人。",

        "price": 69.00,

        "coverUrl": "[https://example.com/cover.jpg](https://example.com/cover.jpg)",

        "actionTitle": "查看搭配",

        "actionUrl": "[https://example.com/action](https://example.com/action)"

      }

    ]

  }

}

```

### 服务端只返回

```text

status = PUBLISHED

+

已经到发布时间

+

没有超过失效时间

+

符合当前推荐规则

```

排序：

```text

priority DESC

```

第一版不做复杂个性化推荐。

---

# 6. 获取发现详情

## GET /discoveries/{id}

获取一条发现完成判断所需要的完整信息。

### Request

例如：

```http

GET /api/v1/discoveries/1001

```

### Response

```json

{

  "code": 0,

  "message": "success",

  "data": {

    "id": 1001,

    "name": "把阳台变成一个适合晚饭后放松的小空间",

    "scene": "下班后想在家放松，但阳台一直没有被真正使用起来。",

    "reason": "用少量预算增加灯光、桌椅和收纳，就能把原本闲置的阳台变成每天都能使用的休闲空间。",

    "suitableFor": "有阳台、希望增加居家休闲空间的人。",

    "price": 69.00,

    "coverUrl": "[https://example.com/cover.jpg](https://example.com/cover.jpg)",

    "actionTitle": "查看搭配",

    "actionUrl": "[https://example.com/action](https://example.com/action)",

    "products": [

      {

        "id": 2001,

        "name": "暖光露营灯",

        "description": "适合阳台和室内使用的小型暖光灯。",

        "price": 39.00,

        "imageUrl": "[https://example.com/product.jpg](https://example.com/product.jpg)",

        "purchaseUrl": "[https://example.com/product](https://example.com/product)",

        "platform": "taobao"

      }

    ]

  }

}

```

客户端直接使用返回的数据。

&gt; **客户端不负责自己拼接 Discovery、Product 之间的关系。**

---

# 7. 上报用户行为

## POST /behaviors

记录用户在发现过程中的实际行为。

### Request

```json

{

  "discoveryId": 1001,

  "behaviorType": "DETAIL_VIEW",

  "metadata": {}

}

```

### behaviorType

与 `Flow-MVPData-Model-MVP` 完全统一：

```text

IMPRESSION

DETAIL_VIEW

INTERESTED

NOT_INTERESTED

SKIP

ACTION_CLICK

```

### 各行为含义

#### IMPRESSION

用户真正看到发现卡片。

```text

进入今日

↓

发现卡片已经展示

↓

上报 IMPRESSION

```

#### DETAIL_VIEW

用户点击发现并进入详情。

#### INTERESTED

用户判断：

&gt; 感兴趣。

#### NOT_INTERESTED

用户判断：

&gt; 不感兴趣。

#### SKIP

用户主动跳过当前发现。

#### ACTION_CLICK

用户进一步执行下一步：

```text

查看商品

阅读内容

打开外部页面

```

---

# 8. 行为请求示例

## 用户打开详情

```http

POST /api/v1/behaviors

```

```json

{

  "discoveryId": 1001,

  "behaviorType": "DETAIL_VIEW"

}

```

---

## 用户表示感兴趣

```http

POST /api/v1/behaviors

```

```json

{

  "discoveryId": 1001,

  "behaviorType": "INTERESTED"

}

```

---

## 用户表示不感兴趣

```http

POST /api/v1/behaviors

```

```json

{

  "discoveryId": 1001,

  "behaviorType": "NOT_INTERESTED"

}

```

---

## 用户跳过

```http

POST /api/v1/behaviors

```

```json

{

  "discoveryId": 1001,

  "behaviorType": "SKIP"

}

```

---

## 用户点击下一步

```http

POST /api/v1/behaviors

```

```json

{

  "discoveryId": 1001,

  "behaviorType": "ACTION_CLICK",

  "metadata": {

    "action": "purchase"

  }

}

```

---

# 9. 行为接口返回

成功时：

```json

{

  "code": 0,

  "message": "success",

  "data": null

}

```

行为采用：

&gt; **新增一条记录，不修改过去的行为。**

例如用户连续打开详情两次：

```text

DETAIL_VIEW

DETAIL_VIEW

```

数据库保存两条行为。

这样以后才能知道真实发生过几次。

---

# 10. 用户判断不再单独建立反馈接口

MVP不使用：

```text

POST /feedbacks

```

也不建立独立 `Feedback` 数据表。

原因：

&gt; **“感兴趣 / 不感兴趣 / 跳过”本身就是用户对发现做出的判断，也是最重要的反馈。**

统一通过：

```text

POST /behaviors

```

记录：

```text

INTERESTED

NOT_INTERESTED

SKIP

```

这样：

```text

客户端

↓

只有一个行为接口

↓

服务端

↓

统一写入 behaviors

```

避免前端和后端维护两套重复定义。

---

# 11. 一次完整用户流程对应哪些 API

完整流程：

```text

进入今日

↓

GET /discoveries/today

↓

展示发现

↓

POST /behaviors

IMPRESSION

↓

用户打开详情

↓

GET /discoveries/{id}

↓

POST /behaviors

DETAIL_VIEW

↓

用户判断

↓

POST /behaviors

INTERESTED / NOT_INTERESTED / SKIP

↓

用户继续行动

↓

POST /behaviors

ACTION_CLICK

```

因此 API 和 Flow 完全对应：

```text

展示

→ GET /discoveries/today

理解

→ GET /discoveries/{id}

判断

→ POST /behaviors

行动

→ POST /behaviors

```

---

# 12. 今日推荐规则

`GET /discoveries/today` 内部执行：

```text

查询 discoveries

↓

只保留 PUBLISHED

↓

检查 published_at

↓

检查 expires_at

↓

按照 priority DESC 排序

↓

返回今天的发现

```

第一版只使用：

```text

内容状态

+

发布时间

+

失效时间

+

人工优先级

```

暂不加入：

```text

用户画像

机器学习

复杂推荐算法

实时推荐

```

---

# 13. API与数据库对应关系

| API                      | 主要数据                                          |

| ------------------------ | --------------------------------------------- |

| `GET /discoveries/today` | `discoveries`                                 |

| `GET /discoveries/{id}`  | `discoveries + products + discovery_products` |

| `POST /behaviors`        | `users + behaviors`                           |

客户端不需要直接知道数据库结构。

服务端负责把数据库中的数据整理成客户端真正需要的数据。

---

# 14. 参数校验

服务端必须检查：

### GET /discoveries/{id}

```text

id 是否有效

↓

发现是否存在

↓

发现是否允许展示

```

如果发现不存在：

```text

404

```

如果发现已经下线或过期：

```text

404

```

---

### POST /behaviors

检查：

```text

anonymous_id 是否存在

discoveryId 是否填写

discovery 是否存在

behaviorType 是否属于允许值

```

不合法：

```text

400

```

---

# 15. 核心错误码

```text

4000  INVALID_PARAMETER

4001  DISCOVERY_NOT_FOUND

4002  DISCOVERY_NOT_AVAILABLE

4003  INVALID_BEHAVIOR

5000  INTERNAL_ERROR

```

### 4000

请求参数缺失或格式错误。

### 4001

指定的发现不存在。

### 4002

发现存在，但当前不能展示，例如已经下线或过期。

### 4003

`behaviorType` 不在允许范围内。

### 5000

服务端出现未预期错误。

---

# 16. 重复行为

所有行为默认采用追加记录：

```text

IMPRESSION

DETAIL_VIEW

ACTION_CLICK

```

允许重复。

例如：

```text

DETAIL_VIEW

DETAIL_VIEW

ACTION_CLICK

```

都正常保存。

用户判断也统一采用追加记录：

```text

INTERESTED

NOT_INTERESTED

```

不在数据库中覆盖旧记录。

以后统计“当前判断”时：

&gt; **按时间取最近一次判断。**

这样既保留真实历史，又不增加额外的数据结构。

---

# 17. MVP接口范围

## P0：必须完成

```text

GET  /discoveries/today

GET  /discoveries/{id}

POST /behaviors

```

只有这 3 个接口，就已经可以完整跑通：

```text

发现

↓

理解

↓

判断

↓

行动

```

## P1：暂不实现

```text

GET  /discoveries/history

GET  /products/{id}

POST /reports

GET  /users/me

GET  /recommendations

```

只有真实需求出现后再增加。

---

# 18. 为什么暂时不单独做 Product API

发现详情已经直接返回：

```json

{

  "products": [

    {

      "id": 2001,

      "name": "暖光露营灯",

      "price": 39.00,

      "purchaseUrl": "[https://example.com/product](https://example.com/product)"

    }

  ]

}

```

当前用户流程是：

&gt; **先理解一个发现，再决定是否继续行动。**

因此首版没有必要：

```text

GET /products/{id}

```

否则只是把一次完整展示拆成更多网络请求。

以后商品需要独立搜索、商品详情或单独管理时，再增加 Product API。

---

# 19. API完成标准

```text

[ ] anonymous_id 可以由客户端生成

[ ] 今日发现可以获取

[ ] 发现详情可以获取

[ ] IMPRESSION 可以记录

[ ] DETAIL_VIEW 可以记录

[ ] INTERESTED 可以记录

[ ] NOT_INTERESTED 可以记录

[ ] SKIP 可以记录

[ ] ACTION_CLICK 可以记录

[ ] 行为能够写入 PostgreSQL

[ ] 发现不存在时可以正确返回错误

[ ] 发现过期 / 下线时不会继续展示

[ ] Android / HarmonyOS 客户端可以正常调用

```

最终必须真实跑通：

```text

客户端生成 anonymous_id

↓

获取今日发现

↓

展示发现

↓

记录 IMPRESSION

↓

打开详情

↓

记录 DETAIL_VIEW

↓

用户判断

↓

记录 INTERESTED / NOT_INTERESTED / SKIP

↓

继续行动

↓

记录 ACTION_CLICK

↓

数据保存到 PostgreSQL

```

&gt; **这 3 个 P0 接口跑通，就形成了「每日发现」MVP 的最小 API 闭环。**

现在这 4 份核心文件已经基本形成一条一致的工程链：

```text

BMVP-产品方案

↓

05 Flow-MVP

↓

03 Data-Model-MVP

↓

04 API-MVP

```

下一份 `02 [Architecture-Server-MVP.md](http://Architecture-Server-MVP.md)` 就应该严格根据这三个文件落下来，**不要再自行增加业务对象和接口**。

