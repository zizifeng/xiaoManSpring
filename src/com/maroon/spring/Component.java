package com.maroon.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xiaoman
 */
//指定注解写在类上面
@Target(ElementType.TYPE)
//运行时生效
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    //给你当前管理的bean 取一个名字的
    String value() default "";
}
