package com.dailydiscover.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MyBatis类型处理器，用于处理JSON格式字段与Java对象之间的转换
 * 针对优化后的数据库Schema中的JSON字段进行处理
 */
public class JsonTypeHandler extends BaseTypeHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(JsonTypeHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final Class<?> type;
    
    public JsonTypeHandler() {
        this.type = Object.class;
    }
    
    public JsonTypeHandler(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = objectMapper.writeValueAsString(parameter);
            ps.setString(i, json);
        } catch (JsonProcessingException e) {
            logger.error("Error converting object to JSON string", e);
            throw new SQLException("Error converting object to JSON string", e);
        }
    }
    
    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }
    
    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }
    
    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }
    
    /**
     * 将JSON字符串解析为对应的Java对象
     * 对于不同的目标类型，使用不同的解析方式
     */
    private Object parse(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        
        try {
            if (type == Map.class) {
                return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
            } else if (type == Object.class) {
                // 默认处理为Map<String, String>
                try {
                    return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
                } catch (Exception e) {
                    // 如果无法解析为Map，则尝试解析为通用Object
                    return objectMapper.readValue(json, Object.class);
                }
            } else {
                return objectMapper.readValue(json, type);
            }
        } catch (Exception e) {
            logger.error("Error parsing JSON string: " + json, e);
            // 在解析失败时返回空对象而不是null，避免NPE
            if (type == Map.class || type == Object.class) {
                return new HashMap<>();
            }
            try {
                return type.newInstance();
            } catch (Exception ex) {
                logger.error("Error creating instance of " + type.getName(), ex);
                return null;
            }
        }
    }
}