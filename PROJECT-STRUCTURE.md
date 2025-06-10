# 每日发现应用 - 项目结构设计

本文档描述了每日发现应用的项目结构设计，采用领域驱动设计(DDD)和整洁架构(Clean Architecture)的思想进行组织。

## 目录结构

```
com.example
├── application           // 应用层，负责协调领域对象完成业务流程
│   ├── dto               // 数据传输对象
│   ├── assembler         // DTO与领域对象转换器
│   └── service           // 应用服务，编排业务流程
├── domain                // 领域层，包含业务核心逻辑
│   ├── model             // 领域模型
│   │   ├── product       // 产品相关模型
│   │   ├── order         // 订单相关模型
│   │   ├── user          // 用户相关模型
│   │   └── ...
│   ├── service           // 领域服务
│   ├── event             // 领域事件
│   └── repository        // 仓储接口
├── infrastructure        // 基础设施层，提供技术实现
│   ├── config            // 配置类
│   ├── util              // 工具类
│   ├── repository        // 仓储实现
│   │   ├── mapper        // MyBatis Mapper
│   │   └── impl          // 仓储接口实现
│   ├── integration       // 外部系统集成
│   │   ├── payment       // 支付集成
│   │   ├── logistics     // 物流集成
│   │   └── ai            // AI服务集成
│   └── security          // 安全相关
├── interfaces            // 接口层，负责与外部交互
│   ├── api               // API接口
│   │   ├── controller    // 控制器
│   │   ├── advice        // 全局异常处理
│   │   └── interceptor   // 拦截器
│   └── scheduler         // 定时任务
└── common                // 通用模块
    ├── constant          // 常量
    ├── enums             // 枚举
    ├── exception         // 异常
    └── result            // 返回结果
```

## 各层职责

### 接口层 (Interfaces)

负责与外部系统交互，包括处理HTTP请求、响应格式化、参数校验等。

- `controller`: REST API控制器
- `advice`: 全局异常处理
- `interceptor`: 请求拦截器
- `scheduler`: 定时任务

### 应用层 (Application)

协调领域对象以完成特定的应用任务，不包含业务规则。

- `dto`: 数据传输对象，用于接口层和应用层之间的数据交换
- `assembler`: DTO与领域对象的转换器
- `service`: 应用服务，编排业务流程

### 领域层 (Domain)

包含业务核心逻辑和规则，是系统的核心。

- `model`: 领域模型，表示业务概念和状态
- `service`: 领域服务，处理跨实体的业务逻辑
- `event`: 领域事件，用于领域模型间的解耦
- `repository`: 仓储接口，定义持久化操作

### 基础设施层 (Infrastructure)

提供技术实现，支持其他层的需求。

- `config`: 配置类
- `util`: 工具类
- `repository`: 仓储实现
- `integration`: 外部系统集成

### 通用模块 (Common)

包含整个系统通用的组件。

- `constant`: 常量定义
- `enums`: 枚举定义
- `exception`: 异常定义
- `result`: 统一返回结果

## 命名规范

1. **包命名**: 使用小写字母，采用领域名词
2. **类命名**: 使用PascalCase，根据职责命名
3. **接口命名**: 接口以"I"开头或以接口功能命名
4. **方法命名**: 使用camelCase，动词开头
5. **常量命名**: 使用大写字母，下划线分隔

## 最小MVP实现

为了实现最小MVP，我们将重点关注以下核心功能模块：

1. 用户模块: 注册、登录、个人信息管理
2. 产品模块: 产品展示、搜索、详情
3. 订单模块: 购物车、下单、支付
4. 推荐模块: 基础推荐功能

其他高级功能将在后续版本中逐步实现。 