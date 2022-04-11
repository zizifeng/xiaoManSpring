package com.maroon.service;
//4.配置类的重要功能就是 提供扫描路径 让我们可以去获取类路径进行实例化 交给Spring容器管理
//5.我们应该用spring提供一个注解用来扫描路径 @ComponentScan来扫描路径 创建一个ComponentScan注解

import com.maroon.spring.ComponentScan;

/**
 * @author xiaoman
 */
//8.添加CompoentScan注解 指定扫描路径 扫描service下的内容 创建一个XxxService类

@ComponentScan("com.maroon.service")
public class AppConfig {
}
