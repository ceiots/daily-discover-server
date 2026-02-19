# 订单管理相关 API 接口

本文档为前端项目（daily-discover-ui）提供订单管理相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 订单管理相关接口

### 1. 订单核心功能

#### OrdersCoreController - 订单核心功能
**接口功能**: 订单CRUD操作、订单查询、订单状态管理

**API端点**:
- 获取所有订单核心信息: `GET ${API_BASE_URL}/orders/core`
- 根据ID获取订单核心信息: `GET ${API_BASE_URL}/orders/core/{id}`
- 根据用户ID获取订单列表: `GET ${API_BASE_URL}/orders/core/user/{userId}`
- 根据订单状态获取订单列表: `GET ${API_BASE_URL}/orders/core/status/{status}`
- 根据订单号获取订单: `GET ${API_BASE_URL}/orders/core/number/{orderNumber}`
- 创建订单核心信息: `POST ${API_BASE_URL}/orders/core`
- 更新订单核心信息: `PUT ${API_BASE_URL}/orders/core/{id}`
- 删除订单核心信息: `DELETE ${API_BASE_URL}/orders/core/{id}`

#### OrdersExtendController - 订单扩展功能
**接口功能**: 订单扩展信息管理、订单状态跟踪、订单操作记录

**API端点**:
- 获取所有订单扩展信息: `GET ${API_BASE_URL}/orders/extend`
- 根据ID获取订单扩展信息: `GET ${API_BASE_URL}/orders/extend/{id}`
- 根据订单ID获取扩展信息: `GET ${API_BASE_URL}/orders/extend/order/{orderId}`
- 根据订单状态获取扩展信息: `GET ${API_BASE_URL}/orders/extend/status/{status}`
- 创建订单扩展信息: `POST ${API_BASE_URL}/orders/extend`
- 更新订单扩展信息: `PUT ${API_BASE_URL}/orders/extend/{id}`
- 删除订单扩展信息: `DELETE ${API_BASE_URL}/orders/extend/{id}`

### 2. 订单项管理

#### OrderItemController - 订单项管理
**接口功能**: 订单商品项管理、订单项统计、金额计算、数量更新

**API端点**:
- 获取所有订单项: `GET ${API_BASE_URL}/order-items`
- 根据ID获取订单项: `GET ${API_BASE_URL}/order-items/{id}`
- 根据订单ID获取订单项: `GET ${API_BASE_URL}/order-items/order/{orderId}`
- 根据商品ID获取订单项: `GET ${API_BASE_URL}/order-items/product/{productId}`
- 创建订单项: `POST ${API_BASE_URL}/order-items`
- 更新订单项: `PUT ${API_BASE_URL}/order-items/{id}`
- 更新订单项数量: `PUT ${API_BASE_URL}/order-items/{id}/quantity`
- 删除订单项: `DELETE ${API_BASE_URL}/order-items/{id}`

### 3. 订单发票管理

#### OrderInvoiceController - 订单发票管理
**接口功能**: 发票信息管理、发票开具、发票状态跟踪、发票作废

**API端点**:
- 获取所有订单发票: `GET ${API_BASE_URL}/order-invoices`
- 根据ID获取订单发票: `GET ${API_BASE_URL}/order-invoices/{id}`
- 根据订单ID获取发票: `GET ${API_BASE_URL}/order-invoices/order/{orderId}`
- 根据发票状态获取发票: `GET ${API_BASE_URL}/order-invoices/status/{status}`
- 创建订单发票: `POST ${API_BASE_URL}/order-invoices`
- 更新订单发票: `PUT ${API_BASE_URL}/order-invoices/{id}`
- 更新发票状态: `PUT ${API_BASE_URL}/order-invoices/{orderId}/status`
- 开具发票: `PUT ${API_BASE_URL}/order-invoices/{orderId}/issue`
- 作废发票: `PUT ${API_BASE_URL}/order-invoices/{orderId}/void`
- 删除订单发票: `DELETE ${API_BASE_URL}/order-invoices/{id}`

### 4. 订单物流管理

#### OrderShippingController - 订单物流管理
**接口功能**: 物流信息管理、配送地址管理、物流状态跟踪

**API端点**:
- 获取所有订单物流信息: `GET ${API_BASE_URL}/order-shipping`
- 根据ID获取订单物流信息: `GET ${API_BASE_URL}/order-shipping/{id}`
- 根据订单ID获取物流信息: `GET ${API_BASE_URL}/order-shipping/order/{orderId}`
- 根据物流状态获取物流信息: `GET ${API_BASE_URL}/order-shipping/status/{status}`
- 创建订单物流信息: `POST ${API_BASE_URL}/order-shipping`
- 更新订单物流信息: `PUT ${API_BASE_URL}/order-shipping/{id}`
- 删除订单物流信息: `DELETE ${API_BASE_URL}/order-shipping/{id}`

#### OrderShippingTrackController - 物流跟踪管理
**接口功能**: 物流轨迹跟踪、配送状态更新、物流信息查询、最新状态查询

**API端点**:
- 获取所有物流跟踪记录: `GET ${API_BASE_URL}/order-shipping-tracks`
- 根据ID获取物流跟踪记录: `GET ${API_BASE_URL}/order-shipping-tracks/{id}`
- 根据订单ID获取物流跟踪记录: `GET ${API_BASE_URL}/order-shipping-tracks/order/{orderId}`
- 根据物流单号获取跟踪记录: `GET ${API_BASE_URL}/order-shipping-tracks/tracking-number/{trackingNumber}`
- 查询最新的物流状态: `GET ${API_BASE_URL}/order-shipping-tracks/order/{orderId}/latest`
- 根据物流状态查询: `GET ${API_BASE_URL}/order-shipping-tracks/status/{status}`
- 创建物流跟踪记录: `POST ${API_BASE_URL}/order-shipping-tracks`
- 更新物流跟踪记录: `PUT ${API_BASE_URL}/order-shipping-tracks/{id}`
- 删除物流跟踪记录: `DELETE ${API_BASE_URL}/order-shipping-tracks/{id}`

### 5. 支付管理

#### PaymentMethodController - 支付方式管理
**接口功能**: 支付方式配置、支付渠道管理、支付设置、可用性验证

**API端点**:
- 获取所有支付方式: `GET ${API_BASE_URL}/payment-methods`
- 根据ID获取支付方式: `GET ${API_BASE_URL}/payment-methods/{id}`
- 获取可用的支付方式: `GET ${API_BASE_URL}/payment-methods/available`
- 根据支付方式类型获取支付方式: `GET ${API_BASE_URL}/payment-methods/type/{methodType}`
- 根据支付方式代码获取支付方式: `GET ${API_BASE_URL}/payment-methods/code/{methodCode}`
- 获取支持的支付方式类型: `GET ${API_BASE_URL}/payment-methods/supported-types`
- 验证支付方式是否可用: `GET ${API_BASE_URL}/payment-methods/{methodCode}/validate`
- 创建支付方式: `POST ${API_BASE_URL}/payment-methods`
- 更新支付方式: `PUT ${API_BASE_URL}/payment-methods/{id}`
- 启用/禁用支付方式: `PUT ${API_BASE_URL}/payment-methods/{id}/toggle`
- 更新支付方式配置: `PUT ${API_BASE_URL}/payment-methods/{id}/config`
- 删除支付方式: `DELETE ${API_BASE_URL}/payment-methods/{id}`

#### PaymentTransactionController - 支付交易管理
**接口功能**: 支付交易记录管理、交易状态跟踪、支付结果查询

**API端点**:
- 获取所有支付交易记录: `GET ${API_BASE_URL}/payment-transactions`
- 根据ID获取支付交易记录: `GET ${API_BASE_URL}/payment-transactions/{id}`
- 根据订单ID获取支付交易: `GET ${API_BASE_URL}/payment-transactions/order/{orderId}`
- 根据交易状态获取交易记录: `GET ${API_BASE_URL}/payment-transactions/status/{status}`
- 根据交易类型获取交易记录: `GET ${API_BASE_URL}/payment-transactions/type/{transactionType}`
- 创建支付交易记录: `POST ${API_BASE_URL}/payment-transactions`
- 更新支付交易记录: `PUT ${API_BASE_URL}/payment-transactions/{id}`
- 删除支付交易记录: `DELETE ${API_BASE_URL}/payment-transactions/{id}`

### 6. 库存交易管理

#### InventoryTransactionController - 库存交易管理
**接口功能**: 库存变动记录管理、库存交易跟踪、库存流水查询

**API端点**:
- 获取所有库存交易记录: `GET ${API_BASE_URL}/inventory-transactions`
- 根据ID获取库存交易记录: `GET ${API_BASE_URL}/inventory-transactions/{id}`
- 根据商品ID获取库存交易: `GET ${API_BASE_URL}/inventory-transactions/product/{productId}`
- 根据SKU ID获取库存交易: `GET ${API_BASE_URL}/inventory-transactions/sku/{skuId}`
- 根据交易类型获取交易记录: `GET ${API_BASE_URL}/inventory-transactions/type/{transactionType}`
- 创建库存交易记录: `POST ${API_BASE_URL}/inventory-transactions`
- 更新库存交易记录: `PUT ${API_BASE_URL}/inventory-transactions/{id}`
- 删除库存交易记录: `DELETE ${API_BASE_URL}/inventory-transactions/{id}`