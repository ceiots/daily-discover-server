# 用户模块架构设计 (简化版)

## 1. 模块概述

用户模块是每日发现系统的核心模块之一，负责用户注册、登录、会员管理、账户管理等功能。该模块采用简化的DDD(领域驱动设计)架构，在保持业务逻辑与技术实现解耦的同时，简化了层次结构，提高开发效率。

## 2. 功能清单

### 2.1 用户管理 (MVP)
- 用户注册（账号密码、手机号注册）
- 用户登录（账号密码、手机验证码）
- 用户信息管理（基本信息）
- 用户安全（密码修改）

### 2.2 会员体系 (后续迭代)
- 会员等级管理
- 会员权益管理
- 成长值管理
- 积分管理

### 2.3 账户管理 (后续迭代)
- 账户余额管理
- 账户流水记录
- 积分记录

## 3. 架构设计

### 3.1 简化分层架构

用户模块采用简化的DDD分层架构：

```
+----------------------------------------------------------+
|                       API层 (Interfaces)                  |
|  Controller, VO, 参数校验                                 |
+----------------------------------------------------------+
|                      应用层 (Application)                 |
|  应用服务, DTO, Assembler, 事务控制                        |
+----------------------------------------------------------+
|                      领域层 (Domain)                      |
|  领域模型, 领域服务, 仓储接口                              |
+----------------------------------------------------------+
|                   基础设施层 (Infrastructure)             |
|  仓储实现, 外部服务集成, 缓存                              |
+----------------------------------------------------------+
```

### 3.2 简化目录结构

```
daily-discover-user/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── user/
│       │               ├── api/                   # API层
│       │               │   ├── controller/        # 控制器
│       │               │   ├── vo/                # 视图对象
│       │               │   └── advice/            # 全局异常处理
│       │               │
│       │               ├── application/           # 应用层
│       │               │   ├── dto/               # 数据传输对象
│       │               │   ├── assembler/         # DTO转换器
│       │               │   └── service/           # 应用服务
│       │               │
│       │               ├── domain/                # 领域层
│       │               │   ├── model/             # 领域模型
│       │               │   │   ├── user/          # 用户聚合
│       │               │   │   ├── member/        # 会员聚合
│       │               │   │   └── account/       # 账户聚合
│       │               │   ├── valueobject/       # 值对象
│       │               │   ├── service/           # 领域服务
│       │               │   ├── repository/        # 仓储接口
│       │               │   └── event/             # 领域事件
│       │               │
│       │               └── infrastructure/        # 基础设施层
│       │                   ├── persistence/       # 持久化相关
│       │                   │   ├── entity/        # 数据库实体
│       │                   │   ├── mapper/        # MyBatis Mapper
│       │                   │   ├── converter/     # 实体转换器
│       │                   │   └── reposi
tory/    # 仓储实现
│       │                   │
│       │                   ├── cache/             # 缓存实现
│       │                   ├── integration/       # 外部系统集成
│       │                   └── config/            # 基础设施配置
│       │
│       └── resources/
│           ├── mapper/                            # MyBatis映射文件
│           └── application.yml                    # 应用配置
```

### 3.3 层间调用关系

```
Controller -> ApplicationService -> DomainService -> Repository
    ↓               ↓                  ↓                 ↓
    VO  <->        DTO  <->         Domain   <->      Entity
                                     Model
```

**关键设计原则**：
1. **依赖倒置**：领域层不依赖基础设施层，而是通过接口定义仓储，由基础设施层实现
2. **数据转换**：各层之间通过转换器(Assembler)进行数据对象转换
3. **单向依赖**：上层依赖下层，下层不依赖上层，减少耦合
4. **领域事件**：通过领域事件实现业务流程的解耦
5. **CQRS模式**：简化实现读写分离，频繁查询可以直接从基础设施层获取数据

## 4. 领域模型 (简化版)

### 4.1 聚合
- User：用户聚合根，包含用户基本信息
- Member：会员聚合根（MVP后续迭代）
- Account：账户聚合根（MVP后续迭代）

### 4.2 值对象
- UserId：用户ID
- Email：邮箱
- Mobile：手机号
- Password：密码

### 4.3 领域服务
- UserDomainService：用户领域服务
- MemberDomainService：会员领域服务（MVP后续迭代）
- AccountDomainService：账户领域服务（MVP后续迭代）

## 5. 技术实践

### 5.1 聚合内一致性
使用事务确保聚合内一致性，领域模型包含业务规则和不变量。

```java
@Transactional
public User registerUser(RegisterDTO registerDTO) {
    // 创建用户
    User user = User.create(registerDTO.getUsername(), registerDTO.getPassword());
    
    // 保存用户
    userRepository.save(user);
    
    // 发布领域事件
    eventPublisher.publishEvent(new UserRegisteredEvent(user.getId()));
    
    return user;
}
```

### 5.2 领域事件
使用Spring事件机制实现领域事件，实现聚合间的最终一致性。

```java
// 领域事件
public class UserRegisteredEvent {
    private final UserId userId;
    
    public UserRegisteredEvent(UserId userId) {
        this.userId = userId;
    }
    
    public UserId getUserId() {
        return userId;
    }
}

// 事件监听器
@Component
public class UserRegistrationListener {
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        // 处理用户注册事件
    }
}
```

### 5.3 CQRS简化实现
读操作可以直接查询数据库，绕过领域模型，提高性能。

```java
@Service
public class UserQueryService {
    @Autowired
    private UserMapper userMapper;
    
    public UserDTO getUserById(Long userId) {
        // 直接从数据库查询，跳过领域模型
        UserEntity entity = userMapper.selectById(userId);
        return convertToDTO(entity);
    }
}
```

## 6. 性能与扩展性

### 6.1 缓存策略
采用多级缓存策略提高性能：

```java
@Service
public class UserCacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    public User getUserById(Long userId) {
        // 从缓存获取
        String key = "user:" + userId;
        User user = (User) redisTemplate.opsForValue().get(key);
        
        if (user == null) {
            // 缓存未命中，从数据库获取
            user = userRepository.findById(userId).orElse(null);
            
            if (user != null) {
                // 存入缓存
                redisTemplate.opsForValue().set(key, user, 30, TimeUnit.MINUTES);
            }
        }
        
        return user;
    }
}
```

### 6.2 并发控制
使用乐观锁处理并发更新：

```java
@Data
public class User {
    private Long id;
    private String username;
    private Integer version;
    
    public void updateUsername(String newUsername) {
        this.username = newUsername;
        // 版本号自增
        this.version++;
    }
}

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Autowired
    private UserMapper userMapper;
    
    public boolean update(User user) {
        // 使用版本号控制并发
        return userMapper.updateWithVersion(user) > 0;
    }
}
```

### 6.3 水平扩展
- 无状态服务设计：所有服务无状态，支持水平扩展
- 分库分表预留：预留分库分表接口，便于后续扩展
- 异步处理：非核心流程采用异步处理提高吞吐量

## 7. 接口设计

### 7.1 RESTful API
- `/api/v1/users`：用户管理
- `/api/v1/members`：会员管理（MVP后续迭代）
- `/api/v1/accounts`：账户管理（MVP后续迭代）

### 7.2 安全设计
- JWT令牌认证
- 密码加密存储
- 接口幂等性
- 参数校验

## 8. 实施路径

### 8.1 MVP阶段 (第一阶段)
- 用户注册
- 用户登录
- 用户信息管理
- 密码修改

### 8.2 后续迭代
- 会员体系
- 账户管理
- 用户行为追踪
- 社交关系 