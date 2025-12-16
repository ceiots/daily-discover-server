# Daily-Discover Common 模块

## 项目概述

`daily-discover-common` 是一个双重用途的Spring Boot项目：
- **作为公共库**：被其他微服务项目引入使用
- **作为独立服务**：直接对外提供通用功能服务

## 架构特点

### 1. 双重运行模式

#### 独立服务模式 (Standalone Mode)
- 启动完整的Spring Boot应用
- 提供REST API接口
- 运行在指定端口（默认8090）
- 包含完整的Web、数据库、认证等功能

#### 库模式 (Library Mode) 
- 作为依赖包被其他项目引入
- 提供工具类、实体类、服务接口等
- 不启动Web服务器
- 配置由主应用控制

### 2. 配置管理

项目使用Spring Profiles管理不同运行模式：

- **默认模式**：`library`（作为公共库）
- **独立服务模式**：`standalone`（启动完整应用）

## 快速开始

### 作为独立服务运行

#### Windows
```bash
# 方式1：使用批处理脚本
start-standalone.bat

# 方式2：使用Maven命令
mvn spring-boot:run -Pstandalone
```

#### Linux/Mac
```bash
# 方式1：使用Shell脚本
chmod +x start-standalone.sh
./start-standalone.sh

# 方式2：使用Maven命令  
mvn spring-boot:run -Pstandalone
```

### 作为公共库使用

在其他项目的`pom.xml`中添加依赖：

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>daily-discover-common</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 功能模块

### 认证模块 (Auth)
- JWT Token生成和验证
- 用户登录/注册/登出
- Token刷新机制
- 权限验证

#### 独立服务模式下的API接口
- `POST /common/auth/api/login` - 用户登录
- `POST /common/auth/api/register` - 用户注册  
- `POST /common/auth/api/verify` - Token验证
- `POST /common/auth/api/refresh` - Token刷新
- `POST /common/auth/api/logout` - 用户登出

#### 库模式下的使用方式
```java
// 注入认证服务
@Autowired
private AuthService authService;

// 使用JWT工具类
@Autowired  
private JwtUtil jwtUtil;
```

### 通用工具模块
- 响应结果封装 (Result)
- 异常处理 (BusinessException)
- 工具类 (DateUtils等)
- 配置类 (Constants等)

## 配置说明

### 独立服务模式配置 (application.yml)
```yaml
spring:
  server:
    port: 8090
    servlet:
      context-path: /common

jwt:
  secret: your-secret-key
  expiration: 86400000
```

### 库模式配置 (application-library.yml)
```yaml
spring:
  main:
    web-application-type: none

daily-discover:
  common:
    mode: library
    auth:
      enabled: false
```

### 环境变量配置

支持通过环境变量覆盖配置：

```bash
# 设置运行模式
export COMMON_MODE=standalone

# 设置JWT密钥  
export JWT_SECRET=your-secret-key

# 设置服务端口
export SERVER_PORT=8091
```

## 开发指南

### 添加新功能模块

1. **创建模块包结构**
```
src/main/java/com/dailydiscover/common/
├── [模块名]/
│   ├── controller/     # 控制器（独立服务模式）
│   ├── service/        # 服务接口
│   ├── service/impl/   # 服务实现
│   ├── entity/         # 实体类
│   ├── dto/            # 数据传输对象
│   ├── config/         # 配置类
│   └── util/           # 工具类
```

2. **添加条件配置**
```java
@Configuration
@ConditionalOnProperty(name = "daily-discover.common.mode", havingValue = "standalone")
public class YourModuleConfig {
    // 独立服务模式下的配置
}
```

3. **更新POM依赖**
确保新功能在两种模式下都能正常工作

### 测试

#### 独立服务模式测试
```bash
# 启动测试服务
mvn spring-boot:run -Pstandalone

# 测试API接口
curl -X POST http://localhost:8090/common/auth/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

#### 库模式测试
在其他项目中引入依赖进行集成测试

## 部署说明

### 打包发布

#### 作为公共库发布
```bash
# 打包为JAR（库模式）
mvn clean package -Plibrary

# 发布到Maven仓库
mvn deploy
```

#### 作为独立服务部署
```bash
# 打包为可执行JAR
mvn clean package -Pstandalone

# 运行服务
java -jar target/daily-discover-common-1.0.0-SNAPSHOT.jar
```

### Docker部署

创建Dockerfile支持两种部署方式：

```dockerfile
# 多阶段构建，支持两种运行模式
FROM openjdk:8-jre-slim as standalone
COPY target/daily-discover-common-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM openjdk:8-jre-slim as library  
# 作为库使用时不需要ENTRYPOINT
```

## 最佳实践

1. **接口设计**：确保API接口在两种模式下行为一致
2. **配置分离**：将独立服务特有的配置与通用配置分离
3. **依赖管理**：避免循环依赖，保持模块独立性
4. **版本控制**：遵循语义化版本控制规范

## 故障排除

### 常见问题

1. **端口冲突**：修改`application.yml`中的端口配置
2. **依赖冲突**：检查与其他项目的依赖版本兼容性
3. **配置不生效**：确认Profile是否正确激活

### 日志查看

```bash
# 查看启动日志
tail -f logs/application.log

# 调试模式启动
mvn spring-boot:run -Pstandalone -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## 版本历史

- v1.0.0: 初始版本，支持双重运行模式
- 新增认证模块、通用工具模块
- 支持Profile配置管理