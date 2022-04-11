package com.maroon.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//指定注解写在字段上面
@Target(ElementType.FIELD)
//运行时生效
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
 
}
