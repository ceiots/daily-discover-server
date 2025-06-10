package com.example.user.infrastructure.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;
    
    private Key key;
    private static final long EXPIRATION_TIME = 86400000; // 24小时

    @PostConstruct
    public void init() {
        try {
            // 使用配置文件中的密钥
            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalArgumentException("JWT密钥不能为空");
            }
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new RuntimeException("JWT密钥初始化失败", e);
        }
    }

    public String generateToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public Long extractUserId(String token) {
        try {
            // 尝试从subject中获取userId
            Claims claims = extractAllClaims(token);
            try {
                // 首先尝试直接从userId字段获取
                Integer userId = claims.get("userId", Integer.class);
                if (userId != null) {
                    return userId.longValue();
                }
            } catch (Exception e) {
                // 忽略错误，继续尝试从subject获取
            }
            
            // 然后尝试从subject获取
            String subject = claims.getSubject();
            if (subject != null) {
                return Long.parseLong(subject);
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("提取userId时出错: " + e.getMessage());
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("解析token失败: " + e.getMessage());
            throw e;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static void main(String[] args) {
        // 测试代码需要手动初始化key
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        // 手动设置一个测试密钥，仅用于测试
        String testSecret = "c8e6eca7e31d89e8e6eca7e31d89c8e6eca7e31d89c8e6eca7e31d89c8e6eca7";
        jwtTokenUtil.secret = testSecret;
        // 手动调用初始化方法
        jwtTokenUtil.init();
        // 生成token
        String token = jwtTokenUtil.generateToken(1L);
        System.out.println("token: " + token);
        Long userId = jwtTokenUtil.extractUserId(token);
        System.out.println("userId: " + userId);
        
        // 测试提供的token
        String providedToken = "eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOjEsInN1YiI6IjEiLCJpYXQiOjE3NDcxOTEzMjQsImV4cCI6MTc0NzI3NzcyNH0.6FP8AEnA8ol5cgHsyUaot38A2sDlHourtdiWj5wLXEvXq5ad5ETsQC7MxC3UkqphtVszbxGkVhuRVBVcodUGIw";
        try {
            Long extractedId = jwtTokenUtil.extractUserId(providedToken);
            System.out.println("从提供的token中提取的userId: " + extractedId);
        } catch (Exception e) {
            System.err.println("解析提供的token失败: " + e.getMessage());
        }
    }
}