# 用户模块架构设计

## 1. 模块概述

用户模块是每日发现系统的核心模块之一，负责用户注册、登录、会员管理、账户管理等功能。该模块采用DDD(领域驱动设计)架构，将业务逻辑与技术实现解耦，提高系统的可维护性和可扩展性。

## 2. 功能清单

### 2.1 用户管理
- 用户注册（账号密码、手机号、邮箱、第三方登录）
- 用户登录（账号密码、手机验证码、第三方登录）
- 用户信息管理（基本信息、详细资料）
- 用户认证（实名认证、手机绑定、邮箱绑定）
- 用户安全（密码修改、账号锁定、登录日志）

### 2.2 会员体系
- 会员等级管理
- 会员权益管理
- 成长值管理
- 积分管理
- 会员到期管理

### 2.3 账户管理
- 账户余额管理
- 账户流水记录
- 积分记录
- 成长值记录
- 账户安全

### 2.4 用户行为
- 用户收藏
- 用户关注
- 用户浏览历史
- 用户评价
- 用户互动

## 3. 架构设计

### 3.1 分层架构

用户模块采用DDD经典四层架构：

```
+----------------------------------------------------------+
|                       API层 (Interfaces)                  |
|  Controller, VO, API异常处理, 参数校验, 权限控制           |
+----------------------------------------------------------+
|                      应用层 (Application)                 |
|  应用服务, DTO, Assembler, 事务控制, 业务流程编排          |
+----------------------------------------------------------+
|                      领域层 (Domain)                      |
|  领域模型, 领域服务, 领域事件, 仓储接口, 业务规则          |
+----------------------------------------------------------+
|                   基础设施层 (Infrastructure)             |
|  仓储实现, ORM实体, 外部服务集成, 消息队列, 缓存           |
+----------------------------------------------------------+
```

### 3.2 目录结构

```
daily-discover-user/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── user/
│       │               ├── api/                   # API层：对外接口
│       │               │   ├── controller/        # REST控制器
│       │               │   ├── vo/                # 视图对象
│       │               │   └── exception/         # API层异常处理
│       │               ├── application/           # 应用层：业务流程编排
│       │               │   ├── service/           # 应用服务
│       │               │   │   └── impl/          # 应用服务实现
│       │               │   ├── assembler/         # DTO转换器
│       │               │   ├── command/           # 命令对象
│       │               │   └── dto/               # 数据传输对象
│       │               ├── domain/                # 领域层：核心业务逻辑
│       │               │   ├── model/             # 领域模型
│       │               │   │   ├── user/          # 用户相关模型
│       │               │   │   ├── member/        # 会员相关模型
│       │               │   │   ├── id/            # ID值对象
│       │               │   │   └── valueobject/   # 值对象
│       │               │   ├── service/           # 领域服务
│       │               │   │   └── impl/          # 领域服务实现
│       │               │   ├── event/             # 领域事件
│       │               │   └── repository/        # 仓储接口
│       │               └── infrastructure/        # 基础设施层
│       │                   ├── repository/        # 仓储实现
│       │                   │   └── impl/          # 仓储实现类
│       │                   ├── persistence/       # 持久化实体
│       │                   │   ├── entity/        # ORM实体
│       │                   │   ├── mapper/        # MyBatis Mapper接口
│       │                   │   └── converter/     # 实体转换器
│       │                   ├── cache/             # 缓存实现
│       │                   ├── mq/                # 消息队列
│       │                   └── config/            # 配置类
│       └── resources/
│           ├── mapper/                            # MyBatis映射文件
│           └── application.yml                    # 应用配置
```

### 3.3 层间调用关系

#### 3.3.1 优化的调用关系设计

```
+----------------+       +----------------+       +----------------+       +----------------+
|                |       |                |       |                |       |                |
|  Controller    |------>|  Application   |------>|    Domain      |<------|Infrastructure  |
|   (API层)      |       |   Service      |       |   Service      |       |  Repository    |
|                |       |                |       |                |       |                |
+----------------+       +----------------+       +----------------+       +----------------+
        |                        |                        |                        |
        v                        v                        v                        v
+----------------+       +----------------+       +----------------+       +----------------+
|                |       |                |       |                |       |                |
|      VO        |<----->|      DTO       |<----->|   Domain       |<----->|    Entity     |
|                |       |                |       |   Model        |       |               |
|                |       |                |       |                |       |               |
+----------------+       +----------------+       +----------------+       +----------------+
```

**关键优化点**：
1. **依赖倒置**：领域层不依赖基础设施层，而是通过接口定义仓储，由基础设施层实现
2. **数据转换**：各层之间通过专门的转换器(Assembler/Converter)进行数据对象转换
3. **单向依赖**：上层依赖下层，下层不依赖上层，减少耦合
4. **领域事件**：通过领域事件实现业务流程的解耦


### 3.4 领域模型

#### 3.4.1 聚合根
- User：用户聚合根，包含用户基本信息
- Member：会员聚合根，包含会员信息和权益

#### 3.4.2 实体
- UserProfile：用户详细资料
- UserAccount：用户账户
- UserAuth：用户认证信息
- MemberLevel：会员等级

#### 3.4.3 值对象
- UserId：用户ID
- MemberId：会员ID
- Email：邮箱
- Mobile：手机号
- Password：密码

#### 3.4.4 领域服务
- UserDomainService：用户领域服务
- MemberDomainService：会员领域服务
- UserAccountDomainService：账户领域服务

### 3.5 领域事件

领域事件用于解耦业务流程，实现异步通知和跨聚合的业务协作。

```java
// 领域事件定义
public class UserRegisteredEvent extends DomainEvent {
    private final UserId userId;
    private final String username;
    
    // 构造函数、getter等
}

// 领域模型中发布事件
public class User {
    // ...
    
    public static User register(String username, String password) {
        User user = new User();
        // 设置属性
        // ...
        
        // 发布领域事件
        DomainEventPublisher.publish(new UserRegisteredEvent(user.getId(), username));
        
        return user;
    }
}

// 事件订阅者
@Component
public class UserRegistrationListener {
    private final AccountService accountService;
    
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        // 创建用户账户
        accountService.createAccount(event.getUserId());
    }
}
```

## 4. 接口设计

### 4.1 内部接口

#### 4.1.1 用户服务接口
- 用户注册接口
- 用户登录接口
- 用户信息查询接口
- 用户信息更新接口

#### 4.1.2 会员服务接口
- 会员信息查询接口
- 会员等级升级接口
- 会员权益查询接口
- 积分管理接口

#### 4.1.3 账户服务接口
- 账户余额查询接口
- 账户余额变更接口
- 账户流水查询接口
- 积分记录查询接口

### 4.2 外部接口

#### 4.2.1 RESTful API
- `/api/v1/users`：用户相关API
- `/api/v1/members`：会员相关API
- `/api/v1/accounts`：账户相关API
- `/api/v1/auth`：认证相关API

#### 4.2.2 第三方集成接口
- 微信登录接口
- 支付宝登录接口
- 短信验证码接口
- 邮箱验证接口

## 5. 安全设计

### 5.1 认证与授权
- JWT令牌认证
- 基于角色的权限控制
- 接口权限校验
- 数据权限过滤

### 5.2 数据安全
- 密码加密存储
- 敏感信息脱敏
- 数据访问控制
- 操作日志审计

### 5.3 接口安全
- 接口幂等性设计
- 防重放攻击
- 接口限流
- 参数校验

## 6. 高性能设计

### 6.1 缓存策略
- 多级缓存：本地缓存 -> Redis分布式缓存 -> 数据库
- 缓存预热：系统启动时加载热点数据
- 缓存更新：采用更新数据库+失效缓存的策略
- 缓存穿透防护：布隆过滤器
- 缓存击穿防护：互斥锁
- 缓存雪崩防护：过期时间随机化

```java
// 缓存实现示例
@Service
public class UserCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final LoadingCache<Long, User> localCache;
    
    public UserCacheService(RedisTemplate<String, Object> redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        
        // 本地缓存配置
        this.localCache = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<Long, User>() {
                @Override
                public User load(Long userId) {
                    // 从Redis获取
                    User user = getUserFromRedis(userId);
                    if (user != null) {
                        return user;
                    }
                    
                    // Redis没有，从数据库获取
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isEmpty()) {
                        throw new BusinessException(ResultCode.USER_NOT_FOUND);
                    }
                    
                    // 存入Redis
                    User foundUser = userOpt.get();
                    saveUserToRedis(foundUser);
                    
                    return foundUser;
                }
            });
    }
    
    public User getUser(Long userId) {
        try {
            return localCache.get(userId);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "获取用户信息失败");
        }
    }
    
    // Redis相关操作方法
    // ...
}
```

### 6.2 数据库优化
- 分库分表：按用户ID哈希分库，大表按时间范围分表
- 读写分离：主库写入，从库读取
- 索引优化：合理设计索引，避免索引失效
- 慢SQL优化：定期分析和优化慢SQL

### 6.3 并发控制
- 乐观锁：使用版本号机制处理并发更新
- 分布式锁：使用Redis实现分布式锁
- 线程池隔离：不同业务使用不同线程池
- 限流：令牌桶算法实现接口限流

```java
// 乐观锁实现示例
@Transactional
public boolean updateUserAccount(Long accountId, BigDecimal amount) {
    // 查询账户
    UserAccount account = userAccountRepository.findById(accountId)
        .orElseThrow(() -> new BusinessException(ResultCode.ACCOUNT_NOT_FOUND));
    
    // 更新余额
    account.addBalance(amount);
    
    // 使用版本号控制并发
    int updated = userAccountRepository.updateWithVersion(
        accountId, account.getBalance(), account.getVersion());
    
    return updated > 0;
}
```

## 7. 扩展性设计

### 7.1 插件化设计
- 认证方式扩展：支持多种认证方式的动态扩展
- 会员权益扩展：支持会员权益的动态配置和扩展
- 积分规则扩展：支持积分规则的动态配置和扩展
- 账户类型扩展：支持多种账户类型的扩展

### 7.2 配置化设计
- 会员等级配置：支持会员等级的动态配置
- 积分规则配置：支持积分规则的动态配置
- 权益规则配置：支持权益规则的动态配置
- 安全策略配置：支持安全策略的动态配置

## 8. 部署架构

### 8.1 集群部署
- 用户服务多实例部署
- 负载均衡策略
- 会话共享方案
- 服务注册与发现

### 8.2 数据库部署
- 主从复制
- 数据分片
- 备份策略
- 灾难恢复

## 9. 监控与运维

### 9.1 监控指标
- 接口响应时间
- 并发用户数
- 登录成功率
- 注册转化率

### 9.2 告警策略
- 接口异常告警
- 业务异常告警
- 安全风险告警
- 性能瓶颈告警

## 10. 演进规划

### 10.1 短期规划
- 完善基础用户功能
- 优化会员体系
- 提升安全性
- 增强性能

### 10.2 中长期规划
- 社交化功能整合
- AI个性化推荐
- 多端数据同步
- 全球化用户体系 