# 用户行为分析相关 API 接口

本文档为前端项目（daily-discover-ui）提供用户行为分析相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 用户行为分析相关接口

### 1. 用户行为日志管理

#### UserBehaviorLogController - 用户行为日志管理
**接口功能**: 用户行为记录、行为分析、用户画像构建

**API端点**:
- 获取所有用户行为日志: `GET ${API_BASE_URL}/user-behavior-logs`
- 根据ID获取用户行为日志: `GET ${API_BASE_URL}/user-behavior-logs/{id}`
- 根据用户ID获取行为日志: `GET ${API_BASE_URL}/user-behavior-logs/user/{userId}`
- 根据行为类型获取日志: `GET ${API_BASE_URL}/user-behavior-logs/type/{behaviorType}`
- 根据商品ID获取行为日志: `GET ${API_BASE_URL}/user-behavior-logs/product/{productId}`
- 创建用户行为日志: `POST ${API_BASE_URL}/user-behavior-logs`
- 更新用户行为日志: `PUT ${API_BASE_URL}/user-behavior-logs/{id}`
- 删除用户行为日志: `DELETE ${API_BASE_URL}/user-behavior-logs/{id}`

### 2. 用户兴趣画像管理

#### UserInterestProfileController - 用户兴趣画像管理
**接口功能**: 用户兴趣分析、兴趣标签、个性化推荐基础

**API端点**:
- 获取所有用户兴趣画像: `GET ${API_BASE_URL}/user-interest-profiles`
- 根据ID获取用户兴趣画像: `GET ${API_BASE_URL}/user-interest-profiles/{id}`
- 根据用户ID获取兴趣画像: `GET ${API_BASE_URL}/user-interest-profiles/user/{userId}`
- 根据兴趣标签获取用户: `GET ${API_BASE_URL}/user-interest-profiles/tag/{interestTag}`
- 创建用户兴趣画像: `POST ${API_BASE_URL}/user-interest-profiles`
- 更新用户兴趣画像: `PUT ${API_BASE_URL}/user-interest-profiles/{id}`
- 删除用户兴趣画像: `DELETE ${API_BASE_URL}/user-interest-profiles/{id}`