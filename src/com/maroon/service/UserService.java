package com.maroon.service;

import com.maroon.spring.*;

/**
 * @author xiaoman
 */
//9.创建一个UserService类 想要它的被容器管理 我们使用Compoent注解
@Component("userService")
@Scope("singleton")
public class UserService implements BeanNameAware ,InitializingBean,UserInterface{

    @Autowired
    private OrderService orderService;

    private String beanName;
    public void test(){

    }

    //这个方法并不是由我们去调用 而是由Spring调用 去给beanName赋值
    @Override
    public void setBeanName(String beanName) {
        this.beanName=beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("我是初始化方法你可以在这里做一些事");
    }
}
