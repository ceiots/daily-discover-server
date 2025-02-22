package com.example.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedTypes(List.class)
public class ListTypeHandler<T> extends BaseTypeHandler<List<T>> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Class<T> type;

    public ListTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromJson(rs.getString(columnName));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromJson(rs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromJson(cs.getString(columnIndex));
    }

    private String toJson(List<T> list) throws SQLException {
        try {
            return mapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new SQLException("Error converting list to JSON", e);
        }
    }

    private List<T> fromJson(String json) throws SQLException {
        try {
            JavaType javaType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            return mapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to list", e);
        }
    }
}