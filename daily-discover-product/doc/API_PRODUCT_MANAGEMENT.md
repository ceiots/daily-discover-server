# 商品管理相关 API 接口

本文档为前端项目（daily-discover-ui）提供商品管理相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 商品管理相关接口

### 1. 商品基础信息管理

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
**接口功能**: 商品图片、规格参数、特性、相关商品推荐等

**API端点**:
- 获取商品图片列表: `GET ${API_BASE_URL}/{productId}/images`
- 获取商品规格参数: `GET ${API_BASE_URL}/{productId}/specifications`
- 获取商品特性: `GET ${API_BASE_URL}/{productId}/features`
- 获取相关商品推荐: `GET ${API_BASE_URL}/{productId}/related`
- 获取完整商品详情: `GET ${API_BASE_URL}/{productId}/full-detail`

### 2. 商品SKU管理

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

### 3. 商品分类与标签管理

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

#### ProductTagRelationController - 商品标签关联管理
**接口功能**: 商品与标签关联关系管理

**API端点**:
- 获取所有商品标签关联: `GET ${API_BASE_URL}/product-tag-relations`
- 根据ID获取商品标签关联: `GET ${API_BASE_URL}/product-tag-relations/{id}`
- 根据商品ID获取标签关联: `GET ${API_BASE_URL}/product-tag-relations/product/{productId}`
- 根据标签ID获取商品关联: `GET ${API_BASE_URL}/product-tag-relations/tag/{tagId}`
- 创建商品标签关联: `POST ${API_BASE_URL}/product-tag-relations`
- 更新商品标签关联: `PUT ${API_BASE_URL}/product-tag-relations/{id}`
- 删除商品标签关联: `DELETE ${API_BASE_URL}/product-tag-relations/{id}`

### 4. 商品搜索与推荐

#### ProductSearchKeywordController - 商品搜索关键词管理
**接口功能**: 搜索关键词管理、热门搜索词、搜索历史

**API端点**:
- 获取所有搜索关键词: `GET ${API_BASE_URL}/product-search-keywords`
- 根据ID获取搜索关键词: `GET ${API_BASE_URL}/product-search-keywords/{id}`
- 获取热门搜索词: `GET ${API_BASE_URL}/product-search-keywords/hot`
- 获取用户搜索历史: `GET ${API_BASE_URL}/product-search-keywords/user/{userId}`
- 创建搜索关键词: `POST ${API_BASE_URL}/product-search-keywords`
- 更新搜索关键词: `PUT ${API_BASE_URL}/product-search-keywords/{id}`
- 删除搜索关键词: `DELETE ${API_BASE_URL}/product-search-keywords/{id}`

#### ProductRecommendationController - 商品推荐管理
**接口功能**: 个性化推荐、推荐算法管理

**API端点**:
- 获取所有商品推荐: `GET ${API_BASE_URL}/product-recommendations`
- 根据ID获取商品推荐: `GET ${API_BASE_URL}/product-recommendations/{id}`
- 获取个性化推荐: `GET ${API_BASE_URL}/product-recommendations/user/{userId}`
- 获取热门商品推荐: `GET ${API_BASE_URL}/product-recommendations/hot`
- 获取基于商品的推荐: `GET ${API_BASE_URL}/product-recommendations/product/{productId}`
- 创建商品推荐: `POST ${API_BASE_URL}/product-recommendations`
- 更新商品推荐: `PUT ${API_BASE_URL}/product-recommendations/{id}`
- 删除商品推荐: `DELETE ${API_BASE_URL}/product-recommendations/{id}`

#### ScenarioRecommendationController - 场景推荐管理
**接口功能**: 场景化推荐、场景配置管理

**API端点**:
- 获取所有场景推荐: `GET ${API_BASE_URL}/scenario-recommendations`
- 根据ID获取场景推荐: `GET ${API_BASE_URL}/scenario-recommendations/{id}`
- 根据场景类型获取推荐: `GET ${API_BASE_URL}/scenario-recommendations/type/{scenarioType}`
- 创建场景推荐: `POST ${API_BASE_URL}/scenario-recommendations`
- 更新场景推荐: `PUT ${API_BASE_URL}/scenario-recommendations/{id}`
- 删除场景推荐: `DELETE ${API_BASE_URL}/scenario-recommendations/{id}`

#### RecommendationEffectController - 推荐效果管理
**接口功能**: 推荐效果统计、推荐算法优化

**API端点**:
- 获取所有推荐效果统计: `GET ${API_BASE_URL}/recommendation-effects`
- 根据ID获取推荐效果统计: `GET ${API_BASE_URL}/recommendation-effects/{id}`
- 根据推荐ID获取效果统计: `GET ${API_BASE_URL}/recommendation-effects/recommendation/{recommendationId}`
- 创建推荐效果统计: `POST ${API_BASE_URL}/recommendation-effects`
- 更新推荐效果统计: `PUT ${API_BASE_URL}/recommendation-effects/{id}`
- 删除推荐效果统计: `DELETE ${API_BASE_URL}/recommendation-effects/{id}`

### 5. 商品库存管理

#### ProductInventoryCoreController - 商品库存核心管理
**接口功能**: 库存CRUD操作、库存查询、库存预警

**API端点**:
- 获取所有商品库存: `GET ${API_BASE_URL}/product-inventories`
- 根据ID获取商品库存: `GET ${API_BASE_URL}/product-inventories/{id}`
- 根据商品ID获取库存: `GET ${API_BASE_URL}/product-inventories/product/{productId}`
- 根据SKU ID获取库存: `GET ${API_BASE_URL}/product-inventories/sku/{skuId}`
- 获取低库存预警: `GET ${API_BASE_URL}/product-inventories/low-stock`
- 创建商品库存: `POST ${API_BASE_URL}/product-inventories`
- 更新商品库存: `PUT ${API_BASE_URL}/product-inventories/{id}`
- 删除商品库存: `DELETE ${API_BASE_URL}/product-inventories/{id}`

#### ProductInventoryConfigController - 库存配置管理
**接口功能**: 库存配置、库存策略、安全库存设置

**API端点**:
- 获取所有库存配置: `GET ${API_BASE_URL}/product-inventory-configs`
- 根据ID获取库存配置: `GET ${API_BASE_URL}/product-inventory-configs/{id}`
- 根据商品ID获取库存配置: `GET ${API_BASE_URL}/product-inventory-configs/product/{productId}`
- 创建库存配置: `POST ${API_BASE_URL}/product-inventory-configs`
- 更新库存配置: `PUT ${API_BASE_URL}/product-inventory-configs/{id}`
- 删除库存配置: `DELETE ${API_BASE_URL}/product-inventory-configs/{id}`

### 6. 商品销售统计

#### ProductSalesStatsController - 商品销售统计
**接口功能**: 销售数据统计、销售排行、销售趋势分析

**API端点**:
- 获取所有商品销售统计: `GET ${API_BASE_URL}/product-sales-stats`
- 根据ID获取商品销售统计: `GET ${API_BASE_URL}/product-sales-stats/{id}`
- 根据商品ID获取销售统计: `GET ${API_BASE_URL}/product-sales-stats/product/{productId}`
- 获取销售排行榜: `GET ${API_BASE_URL}/product-sales-stats/ranking`
- 获取销售趋势数据: `GET ${API_BASE_URL}/product-sales-stats/trend`
- 创建商品销售统计: `POST ${API_BASE_URL}/product-sales-stats`
- 更新商品销售统计: `PUT ${API_BASE_URL}/product-sales-stats/{id}`
- 删除商品销售统计: `DELETE ${API_BASE_URL}/product-sales-stats/{id}`