package com.example.util;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JavaType javaType;

    protected JsonTypeHandler(Class<?> parametrized, Class<?> parameterClass) {
        this.javaType = objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClass);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toObject(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toObject(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toObject(cs.getString(columnIndex));
    }

    private String toJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    private T toObject(String content) {
        if (content != null) {
            try {
                return objectMapper.readValue(content, javaType);
            } catch (Exception e) {
                throw new RuntimeException("Error converting JSON to object", e);
            }
        }
        return null;
    }
}
