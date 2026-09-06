package com.dailydiscover.common.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 匿名用户 ID 参数注解
 * 用于标记 Controller 方法参数需要从请求头获取匿名用户 ID
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnonymousId {
}