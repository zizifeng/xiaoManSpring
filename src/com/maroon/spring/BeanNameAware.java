package com.maroon.spring;

/**
 * 模拟提供Spring容器管理的bean对象的名字
 */
public interface BeanNameAware {
    public void setBeanName(String beanName);
}
