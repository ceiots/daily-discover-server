# 区域管理相关 API 接口

本文档为前端项目（daily-discover-ui）提供区域管理相关后端API的调用说明和示例。

### API基础地址配置

```javascript
// 本地开发环境
VITE_API_BASE_URL=https://ceiots.tailb3fdd6.ts.net/v1/products

// 线上生产环境
VITE_API_BASE_URL=https://myapp.dailydiscover.top/v1/products
```

## 区域管理相关接口

### 1. 区域管理

#### RegionController - 区域管理
**接口功能**: 区域信息管理、行政区划、配送区域设置

**API端点**:
- 获取所有区域信息: `GET ${API_BASE_URL}/regions`
- 根据ID获取区域信息: `GET ${API_BASE_URL}/regions/{id}`
- 根据父级区域ID获取子区域: `GET ${API_BASE_URL}/regions/parent/{parentId}`
- 根据区域类型获取区域: `GET ${API_BASE_URL}/regions/type/{regionType}`
- 根据区域代码获取区域: `GET ${API_BASE_URL}/regions/code/{regionCode}`
- 获取支持的配送区域: `GET ${API_BASE_URL}/regions/shipping`
- 创建区域信息: `POST ${API_BASE_URL}/regions`
- 更新区域信息: `PUT ${API_BASE_URL}/regions/{id}`
- 删除区域信息: `DELETE ${API_BASE_URL}/regions/{id}`