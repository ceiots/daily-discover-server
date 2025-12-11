# MyBatis全面指南

## 一、MyBatis核心概念

### 1.1 什么是MyBatis？
MyBatis是一款优秀的持久层框架，它支持定制化SQL、存储过程以及高级映射。MyBatis避免了几乎所有的JDBC代码和手动设置参数以及获取结果集。

### 1.2 核心组件
- **SqlSessionFactory**: 每个MyBatis应用的核心，用于创建SqlSession
- **SqlSession**: 包含了执行SQL命令所需的所有方法
- **Mapper接口**: 数据访问接口，通过动态代理实现
- **Mapper XML**: SQL映射文件，定义SQL语句和结果映射

## 二、MyBatis架构与工作流程

### 2.1 架构层次
```
应用层 → Mapper接口 → MyBatis核心 → JDBC → 数据库
```

### 2.2 工作流程
1. 应用调用Mapper接口方法
2. MyBatis通过动态代理创建接口实现
3. 根据方法名在XML中查找对应的SQL语句
4. 执行SQL并处理结果集
5. 返回结果给应用层

## 三、Mapper层开发规范

### 3.1 文件命名规范
- **Java接口**: `XxxMapper.java`
- **XML映射**: `XxxMapper.xml`
- **实体类**: `Xxx.java`

### 3.2 目录结构
```
src/main/java/com/dailydiscover/mapper/    # Mapper接口
src/main/resources/mapper/                 # XML映射文件
src/main/java/com/dailydiscover/entity/   # 实体类
```

### 3.3 强制约束
1. **文件名一致性**: Java接口与XML文件必须同名
2. **namespace匹配**: XML中namespace必须对应Java接口全限定名
3. **方法名一致**: Java方法名 = XML中id属性

## 四、XML映射文件详解

### 4.1 基本结构
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.dailydiscover.mapper.UserMapper">
    
    <!-- 结果映射 -->
    <resultMap id="BaseResultMap" type="com.dailydiscover.entity.User">
        <id column="id" property="id" />
        <result column="username" property="username" />
        <result column="email" property="email" />
    </resultMap>
    
    <!-- SQL语句 -->
    <select id="selectById" resultMap="BaseResultMap">
        SELECT * FROM user WHERE id = #{id}
    </select>
    
</mapper>
```

### 4.2 常用SQL标签

#### 4.2.1 查询操作
```xml
<!-- 基本查询 -->
<select id="selectById" resultMap="BaseResultMap">
    SELECT * FROM table WHERE id = #{id}
</select>

<!-- 条件查询 -->
<select id="selectByCondition" resultMap="BaseResultMap">
    SELECT * FROM table 
    WHERE 1=1
    <if test="name != null and name != ''">
        AND name LIKE CONCAT('%', #{name}, '%')
    </if>
    <if test="status != null">
        AND status = #{status}
    </if>
</select>

<!-- 分页查询 -->
<select id="selectPage" resultMap="BaseResultMap">
    SELECT * FROM table 
    LIMIT #{offset}, #{pageSize}
</select>
```

#### 4.2.2 插入操作
```xml
<!-- 插入单条 -->
<insert id="insert" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO table (col1, col2) 
    VALUES (#{col1}, #{col2})
</insert>

<!-- 批量插入 -->
<insert id="batchInsert">
    INSERT INTO table (col1, col2) VALUES
    <foreach collection="list" item="item" separator=",">
        (#{item.col1}, #{item.col2})
    </foreach>
</insert>
```

#### 4.2.3 更新操作
```xml
<!-- 更新操作 -->
<update id="update">
    UPDATE table 
    SET 
        col1 = #{col1},
        col2 = #{col2}
    WHERE id = #{id}
</update>

<!-- 动态更新 -->
<update id="updateSelective">
    UPDATE table
    <set>
        <if test="col1 != null">col1 = #{col1},</if>
        <if test="col2 != null">col2 = #{col2},</if>
    </set>
    WHERE id = #{id}
</update>
```

#### 4.2.4 删除操作
```xml
<!-- 删除操作 -->
<delete id="deleteById">
    DELETE FROM table WHERE id = #{id}
</delete>

<!-- 批量删除 -->
<delete id="batchDelete">
    DELETE FROM table WHERE id IN
    <foreach collection="ids" item="id" open="(" close=")" separator=",">
        #{id}
    </foreach>
</delete>
```

## 五、参数传递与结果映射

### 5.1 参数传递方式

#### 5.1.1 单个参数
```java
// Java接口
User selectById(Long id);

// XML使用
#{参数名}  // 如：#{id}
```

#### 5.1.2 多个参数（使用@Param）
```java
// Java接口
User selectByUsernameAndEmail(@Param("username") String username, 
                              @Param("email") String email);

// XML使用
#{username}, #{email}
```

#### 5.1.3 对象参数
```java
// Java接口
int insert(User user);

// XML使用
#{属性名}  // 如：#{username}, #{email}
```

### 5.2 结果映射

#### 5.2.1 自动映射
```xml
<!-- 列名与属性名一致时自动映射 -->
<select id="selectUser" resultType="com.dailydiscover.entity.User">
    SELECT id, username, email FROM user
</select>
```

#### 5.2.2 自定义映射
```xml
<resultMap id="UserResultMap" type="User">
    <id property="id" column="user_id" />
    <result property="username" column="user_name" />
    <result property="email" column="user_email" />
    
    <!-- 一对一关联 -->
    <association property="department" javaType="Department">
        <id property="id" column="dept_id" />
        <result property="name" column="dept_name" />
    </association>
    
    <!-- 一对多关联 -->
    <collection property="roles" ofType="Role">
        <id property="id" column="role_id" />
        <result property="name" column="role_name" />
    </collection>
</resultMap>
```

## 六、动态SQL

### 6.1 条件判断
```xml
<select id="findUsers" resultMap="BaseResultMap">
    SELECT * FROM users
    <where>
        <if test="username != null">
            AND username = #{username}
        </if>
        <if test="email != null">
            AND email = #{email}
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
    </where>
</select>
```

### 6.2 循环遍历
```xml
<select id="findByIds" resultMap="BaseResultMap">
    SELECT * FROM users
    WHERE id IN
    <foreach collection="ids" item="id" open="(" close=")" separator=",">
        #{id}
    </foreach>
</select>
```

### 6.3 选择分支
```xml
<select id="findByStatus" resultMap="BaseResultMap">
    SELECT * FROM users
    <choose>
        <when test="status == 'active'">
            WHERE status = 1
        </when>
        <when test="status == 'inactive'">
            WHERE status = 0
        </when>
        <otherwise>
            WHERE status IS NOT NULL
        </otherwise>
    </choose>
</select>
```

## 七、高级特性

### 7.1 缓存机制
- **一级缓存**: SqlSession级别，默认开启
- **二级缓存**: Mapper级别，需要手动配置

### 7.2 插件开发
通过Interceptor接口实现自定义插件，可用于：
- SQL日志记录
- 分页功能
- 性能监控
- 数据权限控制

### 7.3 类型处理器
自定义类型转换，如：
- 枚举类型处理
- JSON字段处理
- 日期格式转换

## 八、最佳实践

### 8.1 性能优化
1. **合理使用缓存**: 根据业务场景配置缓存策略
2. **避免N+1查询**: 使用关联查询代替多次单表查询
3. **批量操作**: 使用批量插入、更新提高性能
4. **索引优化**: SQL语句要充分利用数据库索引

### 8.2 代码规范
1. **命名规范**: 方法名要见名知意
2. **参数校验**: 在Service层进行参数校验
3. **异常处理**: 统一异常处理机制
4. **日志记录**: 关键操作要有日志记录

### 8.3 安全注意事项
1. **SQL注入**: 使用#{}而不是${}进行参数传递
2. **权限控制**: 数据库操作要有适当的权限控制
3. **数据脱敏**: 敏感信息在查询时要进行脱敏处理

## 九、常见问题与解决方案

### 9.1 映射问题
- **问题**: 属性名与列名不一致导致映射失败
- **解决**: 使用@Result注解或resultMap进行显式映射

### 9.2 缓存问题
- **问题**: 数据更新后缓存未及时清除
- **解决**: 在更新操作后手动清除相关缓存

### 9.3 性能问题
- **问题**: 复杂查询性能较差
- **解决**: 优化SQL语句，添加适当索引

## 十、总结

MyBatis作为一款优秀的ORM框架，提供了灵活的SQL编写方式和强大的映射功能。掌握MyBatis的核心概念和最佳实践，能够显著提高开发效率和代码质量。

**关键要点回顾：**
- 理解MyBatis的工作机制和核心组件
- 掌握XML映射文件的编写规范
- 熟练使用动态SQL处理复杂业务逻辑
- 遵循最佳实践确保代码质量和性能