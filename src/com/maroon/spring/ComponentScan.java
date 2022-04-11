package com.maroon.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  xiaoman
 *
 * 获取扫描路径的注解 写在类上面
 */
//指定注解写在类上面
@Target(ElementType.TYPE)
//运行时生效
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {
    //7.指定扫描路径
    String value() default "";
}
