package com.dailydiscover.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 数据脱敏工具类
 * 用于日志记录时的敏感信息脱敏
 */
@Slf4j
@Component
public class DataMasker {
    
    private static final Set<String> DEFAULT_SENSITIVE_FIELDS = Set.of(
        "password", "phone", "idCard", "email", "bankCard", 
        "idNumber", "mobile", "tel", "address", "realName"
    );
    
    /**
     * 脱敏敏感数据
     */
    public static String maskSensitiveData(Object data, Set<String> sensitiveFields) {
        if (data == null) {
            return "null";
        }
        
        try {
            if (data instanceof String) {
                return maskString((String) data, sensitiveFields);
            } else {
                return maskObject(data, sensitiveFields);
            }
        } catch (Exception e) {
            log.warn("数据脱敏失败: {}", e.getMessage());
            return "[脱敏失败: " + data.getClass().getSimpleName() + "]";
        }
    }
    
    /**
     * 脱敏字符串
     */
    private static String maskString(String str, Set<String> sensitiveFields) {
        // 检查是否包含敏感关键词
        for (String field : sensitiveFields) {
            if (str.toLowerCase().contains(field.toLowerCase())) {
                return maskCommonSensitiveData(str);
            }
        }
        
        // 检查常见敏感数据格式
        if (isPhoneNumber(str)) {
            return maskPhoneNumber(str);
        } else if (isIdCard(str)) {
            return maskIdCard(str);
        } else if (isEmail(str)) {
            return maskEmail(str);
        }
        
        return str;
    }
    
    /**
     * 脱敏对象
     */
    private static String maskObject(Object obj, Set<String> sensitiveFields) {
        StringBuilder result = new StringBuilder();
        result.append(obj.getClass().getSimpleName()).append("{");
        
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                
                String fieldName = field.getName();
                Object fieldValue = field.get(obj);
                
                // 检查是否为敏感字段
                if (isSensitiveField(fieldName, sensitiveFields)) {
                    result.append(fieldName).append("=").append(maskFieldValue(fieldValue));
                } else {
                    result.append(fieldName).append("=").append(fieldValue);
                }
                
                if (i < fields.length - 1) {
                    result.append(", ");
                }
            }
        } catch (Exception e) {
            // 如果反射失败，使用安全toString
            return obj.toString();
        }
        
        result.append("}");
        return result.toString();
    }
    
    /**
     * 检查是否为敏感字段
     */
    private static boolean isSensitiveField(String fieldName, Set<String> sensitiveFields) {
        return sensitiveFields.stream()
                .anyMatch(field -> fieldName.toLowerCase().contains(field.toLowerCase()));
    }
    
    /**
     * 脱敏字段值
     */
    private static String maskFieldValue(Object value) {
        if (value == null) return "null";
        
        String strValue = value.toString();
        if (isPhoneNumber(strValue)) {
            return maskPhoneNumber(strValue);
        } else if (isIdCard(strValue)) {
            return maskIdCard(strValue);
        } else if (isEmail(strValue)) {
            return maskEmail(strValue);
        } else {
            return "******";
        }
    }
    
    /**
     * 脱敏常见敏感数据
     */
    private static String maskCommonSensitiveData(String data) {
        // 简单的关键词替换
        return data.replaceAll("(?i)(password|phone|idCard|email|bankCard)=[^,}]*", "$1=******");
    }
    
    // 手机号脱敏：138****1234
    private static String maskPhoneNumber(String phone) {
        if (phone.length() >= 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return "******";
    }
    
    // 身份证脱敏：110**********1234
    private static String maskIdCard(String idCard) {
        if (idCard.length() >= 15) {
            return idCard.substring(0, 3) + "**********" + idCard.substring(idCard.length() - 4);
        }
        return "******";
    }
    
    // 邮箱脱敏：te****@example.com
    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String prefix = email.substring(0, Math.min(2, atIndex));
            return prefix + "****" + email.substring(atIndex);
        }
        return "******";
    }
    
    // 验证手机号格式
    private static boolean isPhoneNumber(String str) {
        return Pattern.matches("^1[3-9]\\d{9}$", str);
    }
    
    // 验证身份证格式
    private static boolean isIdCard(String str) {
        return Pattern.matches("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", str);
    }
    
    // 验证邮箱格式
    private static boolean isEmail(String str) {
        return Pattern.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$", str);
    }
    
    /**
     * 获取默认敏感字段集合
     */
    public static Set<String> getDefaultSensitiveFields() {
        return new HashSet<>(DEFAULT_SENSITIVE_FIELDS);
    }
    
    /**
     * 从配置字符串解析敏感字段集合
     */
    public static Set<String> parseSensitiveFields(String config) {
        if (config == null || config.trim().isEmpty()) {
            return getDefaultSensitiveFields();
        }
        
        Set<String> fields = new HashSet<>();
        String[] parts = config.split(",");
        for (String part : parts) {
            String field = part.trim();
            if (!field.isEmpty()) {
                fields.add(field);
            }
        }
        
        return fields;
    }
}