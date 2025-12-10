# MyBatis Mapper层AI编程规范指南

## 目录结构
- Java接口: `src/main/java/com/dailydiscover/mapper/XxxMapper.java`
- XML映射: `src/main/resources/mapper/XxxMapper.xml`

## 强制约束
1. **文件名一致**: `XxxMapper.java` ↔ `XxxMapper.xml`
2. **namespace匹配**: XML中namespace必须对应Java接口全限定名
3. **方法名一致**: Java方法名 = XML中id属性

## 代码模板

### Java接口
```java
@Mapper
public interface XxxMapper extends BaseMapper<Xxx> {
    ReturnType methodName(ParamType param);
}
```

### XML映射
```xml
<mapper namespace="com.dailydiscover.mapper.XxxMapper">
    <select id="methodName" resultMap="BaseResultMap">
        SQL语句使用 #{param}
    </select>
</mapper>
```

## 检查清单
- [ ] 文件名一致性
- [ ] namespace正确性
- [ ] 方法名与id匹配
- [ ] 参数占位符格式正确
- [ ] resultMap定义完整