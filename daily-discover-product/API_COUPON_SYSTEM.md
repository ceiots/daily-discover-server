# 优惠券系统相关 API 接口

本文档为前端项目（daily-discover-ui）提供优惠券系统相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 优惠券系统相关接口

### 1. 优惠券管理

#### CouponController - 优惠券管理
**接口功能**: 优惠券CRUD操作、优惠券状态管理、优惠券查询

**API端点**:
- 获取所有优惠券: `GET ${API_BASE_URL}/coupons`
- 根据ID获取优惠券: `GET ${API_BASE_URL}/coupons/{id}`
- 获取有效优惠券: `GET ${API_BASE_URL}/coupons/active`
- 获取可用优惠券: `GET ${API_BASE_URL}/coupons/available`
- 根据优惠券类型获取优惠券: `GET ${API_BASE_URL}/coupons/type/{couponType}`
- 根据优惠券代码获取优惠券: `GET ${API_BASE_URL}/coupons/code/{couponCode}`
- 获取支持的支付方式类型: `GET ${API_BASE_URL}/coupons/supported-types`
- 验证支付方式是否可用: `GET ${API_BASE_URL}/coupons/{methodCode}/validate`
- 创建优惠券: `POST ${API_BASE_URL}/coupons`
- 更新优惠券: `PUT ${API_BASE_URL}/coupons/{id}`
- 启用/禁用支付方式: `PUT ${API_BASE_URL}/coupons/{id}/toggle`
- 更新支付方式配置: `PUT ${API_BASE_URL}/coupons/{id}/config`
- 删除优惠券: `DELETE ${API_BASE_URL}/coupons/{id}`

#### CouponUsageRecordController - 优惠券使用记录
**接口功能**: 优惠券使用记录、使用统计、用户优惠券历史

**API端点**:
- 获取所有优惠券使用记录: `GET ${API_BASE_URL}/coupon-usage-records`
- 根据ID获取优惠券使用记录: `GET ${API_BASE_URL}/coupon-usage-records/{id}`
- 根据优惠券ID获取使用记录: `GET ${API_BASE_URL}/coupon-usage-records/coupon/{couponId}`
- 根据用户ID获取使用记录: `GET ${API_BASE_URL}/coupon-usage-records/user/{userId}`
- 根据订单ID获取使用记录: `GET ${API_BASE_URL}/coupon-usage-records/order/{orderId}`
- 创建优惠券使用记录: `POST ${API_BASE_URL}/coupon-usage-records`
- 更新优惠券使用记录: `PUT ${API_BASE_URL}/coupon-usage-records/{id}`
- 删除优惠券使用记录: `DELETE ${API_BASE_URL}/coupon-usage-records/{id}`