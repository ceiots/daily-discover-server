# 用户评价相关 API 接口

本文档为前端项目（daily-discover-ui）提供用户评价相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 用户评价相关接口

### 1. 用户评价管理

#### UserReviewController - 用户评价管理
**接口功能**: 评价CRUD操作、评价查询、评价点赞、商家回复等

**API端点**:
- 获取所有用户评价: `GET ${API_BASE_URL}/user-reviews`
- 根据ID获取用户评价: `GET ${API_BASE_URL}/user-reviews/{id}`
- 根据商品ID获取评价列表: `GET ${API_BASE_URL}/user-reviews/product/{productId}`
- 根据用户ID获取评价列表: `GET ${API_BASE_URL}/user-reviews/user/{userId}`
- 根据评价状态获取评价: `GET ${API_BASE_URL}/user-reviews/status/{status}`
- 创建用户评价: `POST ${API_BASE_URL}/user-reviews`
- 更新用户评价: `PUT ${API_BASE_URL}/user-reviews/{id}`
- 删除用户评价: `DELETE ${API_BASE_URL}/user-reviews/{id}`

#### UserReviewDetailController - 评价详情管理
**接口功能**: 评价详细信息、图片、视频等附件管理

**API端点**:
- 获取所有评价详情: `GET ${API_BASE_URL}/user-review-details`
- 根据ID获取评价详情: `GET ${API_BASE_URL}/user-review-details/{id}`
- 根据评价ID获取详情: `GET ${API_BASE_URL}/user-review-details/review/{reviewId}`
- 创建评价详情: `POST ${API_BASE_URL}/user-review-details`
- 更新评价详情: `PUT ${API_BASE_URL}/user-review-details/{id}`
- 删除评价详情: `DELETE ${API_BASE_URL}/user-review-details/{id}`

#### UserReviewStatsController - 评价统计管理
**接口功能**: 评价统计数据、评分分析、统计报表

**API端点**:
- 获取所有评价统计: `GET ${API_BASE_URL}/user-review-stats`
- 根据ID获取评价统计: `GET ${API_BASE_URL}/user-review-stats/{id}`
- 根据商品ID获取评价统计: `GET ${API_BASE_URL}/user-review-stats/product/{productId}`
- 创建评价统计: `POST ${API_BASE_URL}/user-review-stats`
- 更新评价统计: `PUT ${API_BASE_URL}/user-review-stats/{id}`
- 删除评价统计: `DELETE ${API_BASE_URL}/user-review-stats/{id}`

### 2. 评价回复与互动

#### ReviewReplyController - 评价回复管理
**接口功能**: 回复评价、获取回复列表、删除回复等

**API端点**:
- 获取所有评价回复: `GET ${API_BASE_URL}/review-replies`
- 根据ID获取评价回复: `GET ${API_BASE_URL}/review-replies/{id}`
- 根据评价ID获取回复列表: `GET ${API_BASE_URL}/review-replies/review/{reviewId}`
- 根据回复者ID获取回复: `GET ${API_BASE_URL}/review-replies/replier/{replierId}`
- 获取评价回复数量: `GET ${API_BASE_URL}/review-replies/review/{reviewId}/count`
- 创建评价回复: `POST ${API_BASE_URL}/review-replies`
- 更新评价回复: `PUT ${API_BASE_URL}/review-replies/{id}`
- 删除评价回复: `DELETE ${API_BASE_URL}/review-replies/{id}`

#### ReviewStatsController - 评价统计数据
**接口功能**: 评价统计信息、评分分布、热门评价等

**API端点**:
- 获取所有评价统计: `GET ${API_BASE_URL}/review-stats`
- 根据ID获取评价统计: `GET ${API_BASE_URL}/review-stats/{id}`
- 根据商品ID获取评价统计: `GET ${API_BASE_URL}/review-stats/product/{productId}`
- 创建评价统计: `POST ${API_BASE_URL}/review-stats`
- 更新评价统计: `PUT ${API_BASE_URL}/review-stats/{id}`
- 删除评价统计: `DELETE ${API_BASE_URL}/review-stats/{id}`