package com.dailydiscover.constant;

/**
 * 系统常量
 */
public interface Constants {
    /**
     * 默认成功消息
     */
    String DEFAULT_SUCCESS_MESSAGE = "操作成功";

    /**
     * 默认失败消息
     */
    String DEFAULT_FAILURE_MESSAGE = "操作失败";

    /**
     * 默认未授权消息
     */
    String DEFAULT_UNAUTHORIZED_MESSAGE = "未认证或认证失败";

    /**
     * 默认未授权消息
     */
    String DEFAULT_FORBIDDEN_MESSAGE = "未授权";

    /**
     * 请求头中的Token名称
     */
    String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Token前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * 默认分页大小
     */
    int DEFAULT_PAGE_SIZE = 10;

    /**
     * 默认排序方向
     */
    String DEFAULT_ORDER_TYPE = "asc";

    /**
     * 用户状态：启用
     */
    int USER_STATUS_ENABLED = 1;

    /**
     * 用户状态：禁用
     */
    int USER_STATUS_DISABLED = 0;

    /**
     * 删除标志：已删除
     */
    int DELETED = 1;

    /**
     * 删除标志：未删除
     */
    int NOT_DELETED = 0;

    /**
     * 超级管理员角色编码
     */
    String SUPER_ADMIN_ROLE = "ROLE_SUPER_ADMIN";

    /**
     * 管理员角色编码
     */
    String ADMIN_ROLE = "ROLE_ADMIN";

    /**
     * 用户角色编码
     */
    String USER_ROLE = "ROLE_USER";

    /**
     * 验证码有效期（分钟）
     */
    int CAPTCHA_EXPIRATION = 5;

    /**
     * Redis缓存前缀
     */
    String REDIS_PREFIX = "daily_discover:";

    /**
     * 验证码缓存前缀
     */
    String CAPTCHA_PREFIX = REDIS_PREFIX + "captcha:";

    /**
     * 用户Token缓存前缀
     */
    String USER_TOKEN_PREFIX = REDIS_PREFIX + "token:";

    /**
     * 用户缓存前缀
     */
    String USER_CACHE_PREFIX = REDIS_PREFIX + "user:";

    /**
     * 产品缓存前缀
     */
    String PRODUCT_CACHE_PREFIX = REDIS_PREFIX + "product:";

    /**
     * 订单缓存前缀
     */
    String ORDER_CACHE_PREFIX = REDIS_PREFIX + "order:";
}