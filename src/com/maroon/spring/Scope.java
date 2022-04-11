package com.maroon.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**22.标注是否需要创建的类的作用域 单例或者多例
 * @author xiaoman
 */
//指定注解写在类上面
@Target(ElementType.TYPE)
//运行时生效
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    //获取bean的作用域
    String value() default "";
}
