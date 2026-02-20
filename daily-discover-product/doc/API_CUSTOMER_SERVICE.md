# 客服系统相关 API 接口

本文档为前端项目（daily-discover-ui）提供客服系统相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 客服系统相关接口

### 1. 客服人员管理

#### CustomerServiceAgentController - 客服人员管理
**接口功能**: 客服人员信息、在线状态、工作分配

**API端点**:
- 获取所有客服人员: `GET ${API_BASE_URL}/customer-service-agents`
- 根据ID获取客服人员: `GET ${API_BASE_URL}/customer-service-agents/{id}`
- 获取在线客服人员: `GET ${API_BASE_URL}/customer-service-agents/online`
- 根据客服状态获取人员: `GET ${API_BASE_URL}/customer-service-agents/status/{status}`
- 创建客服人员: `POST ${API_BASE_URL}/customer-service-agents`
- 更新客服人员: `PUT ${API_BASE_URL}/customer-service-agents/{id}`
- 删除客服人员: `DELETE ${API_BASE_URL}/customer-service-agents/{id}`

#### CustomerServiceCategoryController - 客服分类管理
**接口功能**: 客服问题分类、工单分类、服务类型

**API端点**:
- 获取所有客服分类: `GET ${API_BASE_URL}/customer-service-categories`
- 根据ID获取客服分类: `GET ${API_BASE_URL}/customer-service-categories/{id}`
- 根据分类类型获取分类: `GET ${API_BASE_URL}/customer-service-categories/type/{categoryType}`
- 创建客服分类: `POST ${API_BASE_URL}/customer-service-categories`
- 更新客服分类: `PUT ${API_BASE_URL}/customer-service-categories/{id}`
- 删除客服分类: `DELETE ${API_BASE_URL}/customer-service-categories/{id}`

### 2. 客服会话管理

#### CustomerServiceConversationController - 客服会话管理
**接口功能**: 客服会话记录、会话状态、会话分配

**API端点**:
- 获取所有客服会话: `GET ${API_BASE_URL}/customer-service-conversations`
- 根据ID获取客服会话: `GET ${API_BASE_URL}/customer-service-conversations/{id}`
- 根据用户ID获取会话: `GET ${API_BASE_URL}/customer-service-conversations/user/{userId}`
- 根据客服ID获取会话: `GET ${API_BASE_URL}/customer-service-conversations/agent/{agentId}`
- 根据会话状态获取会话: `GET ${API_BASE_URL}/customer-service-conversations/status/{status}`
- 创建客服会话: `POST ${API_BASE_URL}/customer-service-conversations`
- 更新客服会话: `PUT ${API_BASE_URL}/customer-service-conversations/{id}`
- 更新会话状态: `PUT ${API_BASE_URL}/customer-service-conversations/{id}/status`
- 删除客服会话: `DELETE ${API_BASE_URL}/customer-service-conversations/{id}`

#### CustomerServiceMessageController - 客服消息管理
**接口功能**: 客服消息记录、消息状态、文件传输

**API端点**:
- 获取所有客服消息: `GET ${API_BASE_URL}/customer-service-messages`
- 根据ID获取客服消息: `GET ${API_BASE_URL}/customer-service-messages/{id}`
- 根据会话ID获取消息: `GET ${API_BASE_URL}/customer-service-messages/conversation/{conversationId}`
- 根据发送者ID获取消息: `GET ${API_BASE_URL}/customer-service-messages/sender/{senderId}`
- 根据消息类型获取消息: `GET ${API_BASE_URL}/customer-service-messages/type/{messageType}`
- 创建客服消息: `POST ${API_BASE_URL}/customer-service-messages`
- 更新客服消息: `PUT ${API_BASE_URL}/customer-service-messages/{id}`
- 更新消息已读状态: `PUT ${API_BASE_URL}/customer-service-messages/{id}/read`
- 删除客服消息: `DELETE ${API_BASE_URL}/customer-service-messages/{id}`