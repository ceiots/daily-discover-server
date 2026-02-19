# Daily Discover 后端API调用指南

本文档为前端项目（daily-discover-ui）提供后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```


## 📊 API接口分类说明

### 1. 商品管理相关接口

#### ProductController - 商品基础信息
**接口功能**: 商品CRUD操作、商家商品查询、商品搜索、热门商品、新品推荐等

**API端点**:
- 获取所有商品: `GET ${API_BASE_URL}/products`
- 根据ID获取商品: `GET ${API_BASE_URL}/products/{id}`
- 根据商家ID获取商品: `GET ${API_BASE_URL}/products/seller/{sellerId}`
- 根据分类ID获取商品: `GET ${API_BASE_URL}/products/category/{categoryId}`
- 获取热门商品: `GET ${API_BASE_URL}/products/hot`
- 获取新品商品: `GET ${API_BASE_URL}/products/new`
- 获取推荐商品: `GET ${API_BASE_URL}/products/recommended`
- 获取每日上新商品: `GET ${API_BASE_URL}/products/daily-new`
- 获取实时热点: `GET ${API_BASE_URL}/products/hotspots`
- 获取明日内容: `GET ${API_BASE_URL}/products/tomorrow-contents`
- 创建商品: `POST ${API_BASE_URL}/products`
- 更新商品: `PUT ${API_BASE_URL}/products/{id}`
- 删除商品: `DELETE ${API_BASE_URL}/products/{id}`

#### ProductDetailController - 商品详情
**接口功能**: 商品详细信息、规格参数、图片等

**API端点**:
- 获取商品详情: `GET ${API_BASE_URL}/product-details/{productId}`
- 更新商品详情: `PUT ${API_BASE_URL}/product-details/{productId}`

#### ProductSkuController - 商品SKU管理
**接口功能**: SKU信息管理、商品规格选项

**API端点**:
- 获取所有SKU信息: `GET ${API_BASE_URL}/product/sku`
- 根据ID获取SKU信息: `GET ${API_BASE_URL}/product/sku/{id}`
- 根据商品ID获取SKU列表: `GET ${API_BASE_URL}/product/sku/product/{productId}`
- 根据SKU编码获取SKU信息: `GET ${API_BASE_URL}/product/sku/code/{skuCode}`
- 创建SKU信息: `POST ${API_BASE_URL}/product/sku`
- 更新SKU信息: `PUT ${API_BASE_URL}/product/sku/{id}`
- 删除SKU信息: `DELETE ${API_BASE_URL}/product/sku/{id}`

#### ProductSkuSpecController - SKU规格管理
**接口功能**: 商品规格定义、规格选项管理

**API端点**:
- 获取所有SKU规格: `GET ${API_BASE_URL}/product-sku-specs`
- 根据ID获取SKU规格: `GET ${API_BASE_URL}/product-sku-specs/{id}`
- 根据商品ID获取规格: `GET ${API_BASE_URL}/product-sku-specs/product/{productId}`
- 创建SKU规格: `POST ${API_BASE_URL}/product-sku-specs`
- 更新SKU规格: `PUT ${API_BASE_URL}/product-sku-specs/{id}`
- 删除SKU规格: `DELETE ${API_BASE_URL}/product-sku-specs/{id}`

#### ProductSkuSpecOptionController - 规格选项管理
**接口功能**: 规格选项CRUD操作

**API端点**:
- 获取所有SKU规格选项: `GET ${API_BASE_URL}/product-sku-spec-options`
- 根据ID获取SKU规格选项: `GET ${API_BASE_URL}/product-sku-spec-options/{id}`
- 根据规格ID获取选项: `GET ${API_BASE_URL}/product-sku-spec-options/spec/{specId}`
- 根据商品ID获取规格选项: `GET ${API_BASE_URL}/product-sku-spec-options/product/{productId}`
- 创建SKU规格选项: `POST ${API_BASE_URL}/product-sku-spec-options`
- 更新SKU规格选项: `PUT ${API_BASE_URL}/product-sku-spec-options/{id}`
- 删除SKU规格选项: `DELETE ${API_BASE_URL}/product-sku-spec-options/{id}`

#### ProductCategoryController - 商品分类管理
**接口功能**: 商品分类CRUD操作

**API端点**:
- 获取所有商品分类: `GET ${API_BASE_URL}/product-categories`
- 根据ID获取商品分类: `GET ${API_BASE_URL}/product-categories/{id}`
- 创建商品分类: `POST ${API_BASE_URL}/product-categories`
- 更新商品分类: `PUT ${API_BASE_URL}/product-categories/{id}`
- 删除商品分类: `DELETE ${API_BASE_URL}/product-categories/{id}`

#### ProductTagController - 商品标签管理
**接口功能**: 商品标签CRUD操作

**API端点**:
- 获取所有商品标签: `GET ${API_BASE_URL}/product-tags`
- 根据ID获取商品标签: `GET ${API_BASE_URL}/product-tags/{id}`
- 创建商品标签: `POST ${API_BASE_URL}/product-tags`
- 更新商品标签: `PUT ${API_BASE_URL}/product-tags/{id}`
- 删除商品标签: `DELETE ${API_BASE_URL}/product-tags/{id}`

#### ProductTagRelationController - 商品标签关系管理
**接口功能**: 商品与标签关联关系管理

**API端点**:
- 获取所有商品标签关系: `GET ${API_BASE_URL}/product-tag-relations`
- 根据ID获取商品标签关系: `GET ${API_BASE_URL}/product-tag-relations/{id}`
- 根据商品ID获取标签关系: `GET ${API_BASE_URL}/product-tag-relations/product/{productId}`
- 根据标签ID获取关系: `GET ${API_BASE_URL}/product-tag-relations/tag/{tagId}`
- 创建商品标签关系: `POST ${API_BASE_URL}/product-tag-relations`
- 更新商品标签关系: `PUT ${API_BASE_URL}/product-tag-relations/{id}`
- 删除商品标签关系: `DELETE ${API_BASE_URL}/product-tag-relations/{id}`

#### ProductInventoryCoreController - 商品库存核心管理
**接口功能**: 商品库存基础操作、库存查询

**API端点**:
- 获取所有商品库存: `GET ${API_BASE_URL}/product-inventory-core`
- 根据ID获取商品库存: `GET ${API_BASE_URL}/product-inventory-core/{id}`
- 根据商品ID获取库存: `GET ${API_BASE_URL}/product-inventory-core/product/{productId}`
- 根据SKU ID获取库存: `GET ${API_BASE_URL}/product-inventory-core/sku/{skuId}`
- 创建商品库存: `POST ${API_BASE_URL}/product-inventory-core`
- 更新商品库存: `PUT ${API_BASE_URL}/product-inventory-core/{id}`
- 删除商品库存: `DELETE ${API_BASE_URL}/product-inventory-core/{id}`

#### ProductInventoryConfigController - 商品库存配置管理
**接口功能**: 库存预警配置、库存策略管理

**API端点**:
- 获取所有库存配置: `GET ${API_BASE_URL}/product-inventory-config`
- 根据ID获取库存配置: `GET ${API_BASE_URL}/product-inventory-config/{id}`
- 根据库存ID获取配置: `GET ${API_BASE_URL}/product-inventory-config/inventory/{inventoryId}`
- 创建库存配置: `POST ${API_BASE_URL}/product-inventory-config`
- 更新库存配置: `PUT ${API_BASE_URL}/product-inventory-config/{id}`
- 删除库存配置: `DELETE ${API_BASE_URL}/product-inventory-config/{id}`

#### ProductSalesStatsController - 商品销售统计
**接口功能**: 商品销售数据分析、统计报表

**API端点**:
- 获取所有销售统计: `GET ${API_BASE_URL}/product-sales-stats`
- 根据ID获取销售统计: `GET ${API_BASE_URL}/product-sales-stats/{id}`
- 根据商品ID获取销售统计: `GET ${API_BASE_URL}/product-sales-stats/product/{productId}`
- 根据时间段获取销售统计: `GET ${API_BASE_URL}/product-sales-stats/period?startDate={startDate}&endDate={endDate}`
- 创建销售统计: `POST ${API_BASE_URL}/product-sales-stats`
- 更新销售统计: `PUT ${API_BASE_URL}/product-sales-stats/{id}`
- 删除销售统计: `DELETE ${API_BASE_URL}/product-sales-stats/{id}`

#### ProductSearchKeywordController - 商品搜索关键词管理
**接口功能**: 搜索关键词记录、热门搜索词统计

**API端点**:
- 获取所有搜索关键词: `GET ${API_BASE_URL}/product-search-keywords`
- 根据ID获取搜索关键词: `GET ${API_BASE_URL}/product-search-keywords/{id}`
- 获取热门搜索词: `GET ${API_BASE_URL}/product-search-keywords/hot`
- 根据用户ID获取搜索记录: `GET ${API_BASE_URL}/product-search-keywords/user/{userId}`
- 创建搜索关键词: `POST ${API_BASE_URL}/product-search-keywords`
- 更新搜索关键词: `PUT ${API_BASE_URL}/product-search-keywords/{id}`
- 删除搜索关键词: `DELETE ${API_BASE_URL}/product-search-keywords/{id}`

#### ProductRecommendationController - 商品推荐管理
**接口功能**: 个性化推荐、推荐算法管理

**API端点**:
- 获取所有商品推荐: `GET ${API_BASE_URL}/product-recommendations`
- 根据ID获取商品推荐: `GET ${API_BASE_URL}/product-recommendations/{id}`
- 根据用户ID获取个性化推荐: `GET ${API_BASE_URL}/product-recommendations/user/{userId}`
- 根据商品ID获取推荐: `GET ${API_BASE_URL}/product-recommendations/product/{productId}`
- 根据推荐类型获取推荐: `GET ${API_BASE_URL}/product-recommendations/type/{recommendationType}`
- 获取活跃推荐: `GET ${API_BASE_URL}/product-recommendations/active`
- 创建商品推荐: `POST ${API_BASE_URL}/product-recommendations`
- 更新商品推荐: `PUT ${API_BASE_URL}/product-recommendations/{id}`
- 删除商品推荐: `DELETE ${API_BASE_URL}/product-recommendations/{id}`

### 2. 购物车相关接口

#### CartController - 购物车管理
**接口功能**: 添加商品到购物车、获取购物车列表、更新数量、删除商品等

**API端点**:
- 加入购物车: `POST ${API_BASE_URL}/cart/add`
- 获取购物车商品列表: `GET ${API_BASE_URL}/cart/items`
- 更新购物车商品: `PUT ${API_BASE_URL}/cart/update`
- 删除购物车商品: `DELETE ${API_BASE_URL}/cart/remove/{itemId}`
- 清空购物车: `DELETE ${API_BASE_URL}/cart/clear`


### 3. 评价系统相关接口

#### UserReviewController - 用户评价
**接口功能**: 创建评价、获取评价列表、点赞、回复等

**API端点**:
- 创建评价: `POST ${API_BASE_URL}/reviews`
- 获取商品评价列表: `GET ${API_BASE_URL}/reviews/product/{productId}`
- 获取用户评价列表: `GET ${API_BASE_URL}/reviews/user/{userId}`
- 点赞评价: `POST ${API_BASE_URL}/reviews/{reviewId}/like`
- 取消点赞: `DELETE ${API_BASE_URL}/reviews/{reviewId}/like`
- 获取商家回复: `GET ${API_BASE_URL}/reviews/{reviewId}/seller-replies`

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

#### ReviewReplyController - 评价回复
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
PUT ${API_BASE_URL}/review-stats/{id}
DELETE ${API_BASE_URL}/review-stats/{id}

### 4. 订单管理相关接口

#### OrdersCoreController - 订单核心功能
**接口功能**: 创建订单、订单列表、订单详情、取消订单等
PUT ${API_BASE_URL}/orders-core/{id}
DELETE ${API_BASE_URL}/orders-core/{id}

#### OrdersExtendController - 订单扩展功能
**接口功能**: 订单扩展信息、订单状态管理、订单操作
PUT ${API_BASE_URL}/orders-extend/{id}
DELETE ${API_BASE_URL}/orders-extend/{id}

#### OrderItemController - 订单项管理
**接口功能**: 订单商品项管理、订单项统计、金额计算
PUT ${API_BASE_URL}/order-items/{id}
PUT ${API_BASE_URL}/order-items/{id}/quantity
DELETE ${API_BASE_URL}/order-items/{id}

#### OrderInvoiceController - 订单发票管理
**接口功能**: 发票信息管理、发票开具、发票状态跟踪
PUT ${API_BASE_URL}/order-invoices/{id}
PUT ${API_BASE_URL}/order-invoices/{orderId}/status
PUT ${API_BASE_URL}/order-invoices/{orderId}/issue
PUT ${API_BASE_URL}/order-invoices/{orderId}/void
DELETE ${API_BASE_URL}/order-invoices/{id}

#### OrderShippingController - 订单物流管理
**接口功能**: 物流信息管理、配送地址、物流状态跟踪
PUT ${API_BASE_URL}/order-shipping/{id}
DELETE ${API_BASE_URL}/order-shipping/{id}

#### OrderShippingTrackController - 物流跟踪管理
**接口功能**: 物流轨迹跟踪、配送状态更新、物流信息查询
PUT ${API_BASE_URL}/order-shipping-track/{id}
DELETE ${API_BASE_URL}/order-shipping-track/{id}

#### PaymentMethodController - 支付方式管理
**接口功能**: 支付方式配置、支付渠道管理、支付设置
PUT ${API_BASE_URL}/payment-methods/{id}
DELETE ${API_BASE_URL}/payment-methods/{id}

#### PaymentTransactionController - 支付交易管理
**接口功能**: 支付交易记录、交易状态管理、支付结果查询
PUT ${API_BASE_URL}/payment-transactions/{id}
DELETE ${API_BASE_URL}/payment-transactions/{id}

#### InventoryTransactionController - 库存交易管理
**接口功能**: 库存变动记录、库存交易跟踪、库存流水查询
PUT ${API_BASE_URL}/inventory-transactions/{id}
DELETE ${API_BASE_URL}/inventory-transactions/{id}

### 5. 客服系统相关接口

#### CustomerServiceAgentController - 客服人员管理
**接口功能**: 客服人员信息、在线状态、工作分配
PUT ${API_BASE_URL}/customer-service-agents/{id}
DELETE ${API_BASE_URL}/customer-service-agents/{id}

#### CustomerServiceCategoryController - 客服分类管理
**接口功能**: 客服问题分类、工单分类、服务类型
PUT ${API_BASE_URL}/customer-service-categories/{id}
DELETE ${API_BASE_URL}/customer-service-categories/{id}

#### CustomerServiceConversationController - 客服会话管理
**接口功能**: 客服会话记录、会话状态、会话分配
PUT ${API_BASE_URL}/customer-service-conversations/{id}
PUT ${API_BASE_URL}/customer-service-conversations/{id}/status
DELETE ${API_BASE_URL}/customer-service-conversations/{id}

#### CustomerServiceMessageController - 客服消息管理
**接口功能**: 客服消息记录、消息状态、文件传输
PUT ${API_BASE_URL}/customer-service-messages/{id}
PUT ${API_BASE_URL}/customer-service-messages/{id}/read
DELETE ${API_BASE_URL}/customer-service-messages/{id}

### 6. 优惠券系统相关接口

#### CouponController - 优惠券管理
**接口功能**: 优惠券CRUD操作、优惠券状态管理
PUT ${API_BASE_URL}/coupons/{id}
DELETE ${API_BASE_URL}/coupons/{id}

#### CouponUsageRecordController - 优惠券使用记录
**接口功能**: 优惠券使用记录、使用统计、用户优惠券历史
PUT ${API_BASE_URL}/coupon-usage-records/{id}
DELETE ${API_BASE_URL}/coupon-usage-records/{id}

### 7. 售后系统相关接口

#### AfterSalesApplicationController - 售后申请管理
**接口功能**: 售后申请处理、申请状态跟踪、退款退货管理
PUT ${API_BASE_URL}/after-sales/{id}
DELETE ${API_BASE_URL}/after-sales/{id}

#### RefundRecordController - 退款记录管理
**接口功能**: 退款记录管理、退款状态跟踪、退款统计
PUT ${API_BASE_URL}/refund-records/{id}
DELETE ${API_BASE_URL}/refund-records/{id}

### 8. 推荐系统相关接口

#### ProductRecommendationController - 商品推荐管理
**接口功能**: 个性化推荐、推荐算法管理
PUT ${API_BASE_URL}/product-recommendations/{id}
DELETE ${API_BASE_URL}/product-recommendations/{id}

#### RecommendationEffectController - 推荐效果追踪
**接口功能**: 推荐效果统计、点击转化率、推荐算法优化
PUT ${API_BASE_URL}/recommendation-effects/{id}
DELETE ${API_BASE_URL}/recommendation-effects/{id}

#### ScenarioRecommendationController - 场景推荐管理
**接口功能**: 场景化推荐、场景类型管理、场景推荐算法

**API端点**:
- 获取所有场景推荐: `GET ${API_BASE_URL}/scenario-recommendations`
- 根据ID获取场景推荐: `GET ${API_BASE_URL}/scenario-recommendations/{id}`
- 根据场景类型获取推荐: `GET ${API_BASE_URL}/scenario-recommendations/scenario/{scenarioType}`
- 根据用户ID获取场景推荐: `GET ${API_BASE_URL}/scenario-recommendations/user/{userId}`
- 获取活跃场景推荐: `GET ${API_BASE_URL}/scenario-recommendations/active`
- 创建场景推荐: `POST ${API_BASE_URL}/scenario-recommendations`
- 更新场景推荐: `PUT ${API_BASE_URL}/scenario-recommendations/{id}`
- 删除场景推荐: `DELETE ${API_BASE_URL}/scenario-recommendations/{id}`

### 9. 用户画像相关接口

#### UserInterestProfileController - 用户兴趣画像
**接口功能**: 用户兴趣标签、行为模式、个性化推荐基础

**API端点**:
- 根据用户ID更新兴趣画像: `PUT ${API_BASE_URL}/user-interest-profiles/user/{userId}`
- 更新用户兴趣标签: `PUT ${API_BASE_URL}/user-interest-profiles/user/{userId}/interest-tags`
- 更新用户行为模式: `PUT ${API_BASE_URL}/user-interest-profiles/user/{userId}/behavior-patterns`
- 删除用户兴趣画像: `DELETE ${API_BASE_URL}/user-interest-profiles/{id}`

#### UserBehaviorLogController - 用户行为日志
**接口功能**: 记录用户浏览、搜索、点击等行为

**API端点**:
- 更新用户行为日志: `PUT ${API_BASE_URL}/user-behavior-logs/{id}`
- 删除用户行为日志: `DELETE ${API_BASE_URL}/user-behavior-logs/{id}`

### 10. 商家管理相关接口

#### SellerController - 商家基础信息管理
**接口功能**: 商家信息管理、商家状态、商家资质

**API端点**:
- 更新商家信息: `PUT ${API_BASE_URL}/sellers/{id}`
- 删除商家信息: `DELETE ${API_BASE_URL}/sellers/{id}`

#### SellerProfileController - 商家资料管理
**接口功能**: 商家详细资料、商家评分、商家统计

**API端点**:
- 更新商家资料: `PUT ${API_BASE_URL}/seller-profiles/{id}`
- 更新商家评分: `PUT ${API_BASE_URL}/seller-profiles/{id}/rating`
- 删除商家资料: `DELETE ${API_BASE_URL}/seller-profiles/{id}`

### 11. 促销活动相关接口

#### PromotionActivityController - 促销活动管理
**接口功能**: 促销活动管理、商品促销查询等

**API端点**:
- 更新促销活动: `PUT ${API_BASE_URL}/promotion-activities/{id}`
- 删除促销活动: `DELETE ${API_BASE_URL}/promotion-activities/{id}`

### 12. 地区管理相关接口

#### RegionController - 地区信息
**接口功能**: 获取地区列表、地区树结构、地区路径等

**API端点**:
- 更新地区信息: `PUT ${API_BASE_URL}/regions/{regionId}`
- 删除地区信息: `DELETE ${API_BASE_URL}/regions/{id}`

**注意**: 地区ID和父级地区ID都是字符串类型（地区编码）

## 🔒 认证和授权

### JWT Token认证

所有需要认证的接口需要在请求头中添加Authorization：

```javascript
// 登录后保存token
localStorage.setItem('auth_token', response.data.token);

// 请求拦截器中自动添加
token = localStorage.getItem('auth_token');
if (token) {
  config.headers.Authorization = `Bearer ${token}`;
}
```

## 📊 性能优化建议

1. **请求缓存**: 对不经常变化的数据使用缓存
2. **请求合并**: 对多个相关请求进行合并
3. **懒加载**: 按需加载数据，减少初始请求
4. **分页加载**: 大数据集使用分页加载
5. **错误边界**: 使用React错误边界处理组件错误
