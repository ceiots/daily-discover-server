# 售后系统相关 API 接口

本文档为前端项目（daily-discover-ui）提供售后系统相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 售后系统相关接口

### 1. 售后申请管理

#### AfterSalesApplicationController - 售后申请管理
**接口功能**: 售后申请处理、申请状态跟踪、退款退货管理

**API端点**:
- 获取所有售后申请: `GET ${API_BASE_URL}/after-sales`
- 根据ID获取售后申请: `GET ${API_BASE_URL}/after-sales/{id}`
- 根据用户ID获取售后申请: `GET ${API_BASE_URL}/after-sales/user/{userId}`
- 根据订单ID获取售后申请: `GET ${API_BASE_URL}/after-sales/order/{orderId}`
- 根据申请状态获取申请: `GET ${API_BASE_URL}/after-sales/status/{status}`
- 根据申请类型获取申请: `GET ${API_BASE_URL}/after-sales/type/{applicationType}`
- 创建售后申请: `POST ${API_BASE_URL}/after-sales`
- 更新售后申请: `PUT ${API_BASE_URL}/after-sales/{id}`
- 删除售后申请: `DELETE ${API_BASE_URL}/after-sales/{id}`

#### RefundRecordController - 退款记录管理
**接口功能**: 退款记录管理、退款状态跟踪、退款统计

**API端点**:
- 获取所有退款记录: `GET ${API_BASE_URL}/refund-records`
- 根据ID获取退款记录: `GET ${API_BASE_URL}/refund-records/{id}`
- 根据售后申请ID获取退款记录: `GET ${API_BASE_URL}/refund-records/application/{applicationId}`
- 根据订单ID获取退款记录: `GET ${API_BASE_URL}/refund-records/order/{orderId}`
- 根据退款状态获取记录: `GET ${API_BASE_URL}/refund-records/status/{status}`
- 创建退款记录: `POST ${API_BASE_URL}/refund-records`
- 更新退款记录: `PUT ${API_BASE_URL}/refund-records/{id}`
- 删除退款记录: `DELETE ${API_BASE_URL}/refund-records/{id}`