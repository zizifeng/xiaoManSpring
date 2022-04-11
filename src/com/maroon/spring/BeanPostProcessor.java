package com.maroon.spring;

/**
 * 55. 这个接口用于标注当我们创建bean对象的是 需要对bean对象进行一些处理
 * 由一个实现类实现这个接口 在加上@Component注解 并交给Spring管理操作
 */
public interface BeanPostProcessor {
    public Object postProcessBeforeInitialization(String beanName, Object bean);
    public Object postProcessAfterInitialization(String beanName, Object bean);
}
