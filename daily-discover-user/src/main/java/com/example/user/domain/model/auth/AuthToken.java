package com.example.user.domain.model.auth;

import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 认证令牌领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthToken implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Setter
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 刷新令牌过期时间
     */
    private LocalDateTime refreshExpireTime;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备类型
     */
    private Integer deviceType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建认证令牌
     *
     * @param userId 用户ID
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param expireIn 过期时间（秒）
     * @param refreshExpireIn 刷新令牌过期时间（秒）
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @return 认证令牌
     */
    public static AuthToken create(UserId userId, String accessToken, String refreshToken, 
                                  Integer expireIn, Integer refreshExpireIn,
                                  String deviceId, Integer deviceType) {
        AuthToken token = new AuthToken();
        token.userId = userId;
        token.accessToken = accessToken;
        token.refreshToken = refreshToken;
        token.tokenType = "Bearer";
        token.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        token.refreshExpireTime = LocalDateTime.now().plusSeconds(refreshExpireIn);
        token.deviceId = deviceId;
        token.deviceType = deviceType;
        token.createTime = LocalDateTime.now();
        token.updateTime = LocalDateTime.now();
        return token;
    }

    /**
     * 刷新令牌
     *
     * @param accessToken 新的访问令牌
     * @param refreshToken 新的刷新令牌
     * @param expireIn 过期时间（秒）
     * @param refreshExpireIn 刷新令牌过期时间（秒）
     */
    public void refresh(String accessToken, String refreshToken, Integer expireIn, Integer refreshExpireIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
        this.refreshExpireTime = LocalDateTime.now().plusSeconds(refreshExpireIn);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否过期
     *
     * @return 是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 刷新令牌是否过期
     *
     * @return 是否过期
     */
    public boolean isRefreshExpired() {
        return LocalDateTime.now().isAfter(refreshExpireTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthToken authToken = (AuthToken) o;
        return Objects.equals(id, authToken.id) || Objects.equals(accessToken, authToken.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accessToken);
    }
}