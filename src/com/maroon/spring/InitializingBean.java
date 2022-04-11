package com.maroon.spring;
//53.初始化方法的接口 实现其就可以调用
public interface InitializingBean {
    public void afterPropertiesSet();
}
