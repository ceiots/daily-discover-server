package com.dailydiscover.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 自定义类型处理器，用于处理数据库中的列表类型
 * @param <T> 列表元素类型
 */
public class ListTypeHandler<T> extends BaseTypeHandler<List<T>> {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaType javaType;

    public ListTypeHandler(Class<T> clazz) {
        this.javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
    }

    // 默认构造函数，用于处理简单的List<String>
    public ListTypeHandler() {
        this.javaType = objectMapper.getTypeFactory().constructParametricType(List.class, Object.class);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将列表转换为 JSON 字符串
            String value = objectMapper.writeValueAsString(parameter);
            ps.setString(i, value);
        } catch (IOException e) {
            throw new SQLException("Error converting list to JSON", e);
        }
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从结果集中获取 JSON 字符串并转换为列表
        String value = rs.getString(columnName);
        return fromJson(value);
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return fromJson(value);
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return fromJson(value);
    }

    private List<T> fromJson(String json) throws SQLException {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new SQLException("Error converting JSON to list", e);
        }
    }
}