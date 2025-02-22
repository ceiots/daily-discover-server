package com.example.util;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.io.IOException;
import java.util.List;

/**
 * JSON类型处理器
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonTypeHandler<T> extends AbstractJsonTypeHandler<T> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    protected T parse(String json) {
        try {
            if (json == null || json.length() == 0) {
                return null;
            }
            if (List.class.isAssignableFrom(type)) {
                JavaType javaType = MAPPER.getTypeFactory().constructCollectionType(List.class, type);
                return MAPPER.readValue(json, javaType);
            }
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String toJson(T obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
} 