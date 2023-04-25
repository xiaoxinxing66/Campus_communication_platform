package com.nowcoder.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//表示该注解用来描述方法
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * 自定义注解，用来登录确认。
 */
public @interface LoginRequire {
}
