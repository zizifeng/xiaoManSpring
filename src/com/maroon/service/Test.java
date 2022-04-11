package com.maroon.service;

import com.maroon.spring.XiaoManApplicationContext;

/**
 * @author xiaoman
 */
public class Test {
    //zheg s
    public static void main(String[] args) {
        //1.创建一个Spring容器实例对象，一般传入一个配置文件(xx.xml)或者配置类，这里传入一个配置类。
        //2.Spring容器根据传入的配置类去扫描对应的文件
        XiaoManApplicationContext applicationContext =new XiaoManApplicationContext(AppConfig.class);
      //  Object userService = context.getBean("userService");

        /**
         * 40.测试是否简易创建单例bean的逻辑是否走通
         * 我们再UserService类上加上@Scope注解设置为singleton或不加@Scope注解
         *我们创建多个bean看返回的地址值是否相同
         * com.maroon.service.UserService@60e53b93
         * com.maroon.service.UserService@60e53b93
         * com.maroon.service.UserService@60e53b93
         * com.maroon.service.UserService@60e53b93
         */
      /*  System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));
        System.out.println(applicationContext.getBean("userService"));*/

        /**
         * 41.我们再在UserService类上加上@Scope注解并设置成prototype 看看是否会创建多个
         * com.maroon.service.UserService@5e2de80c
         * com.maroon.service.UserService@1d44bcfa
         * com.maroon.service.UserService@266474c2
         * com.maroon.service.UserService@6f94fa3e
         */
        //43.测试是否在没有设置Component的beanName的时候是否能设置一个默认名字 ：com.maroon.service.OrderService@5e2de80c
     //   System.out.println(applicationContext.getBean("orderService"));
        /**
         * 44.做到这里我们实现了创建bean对象 下一步做实现Spring自动注入@Autowired
         * 先创建一个Autowired注解 用这个注解去标记需要创建一个bean对象的时候注入的一个属性
         * 所以我们在创建bean对象的时候 就是在createBean()方法的时候 我们现在需要在加上一个步骤
         * 去判断一下 用@Component标准的注解是里面的属性是否有用@Autowired标注 如果有我们进行注入
         */

        //48.测试一下@Autowired依赖注入是否成功
       // UserService userService = (UserService) applicationContext.getBean("userService");
      //  userService.test();
     //   userService.afterPropertiesSet();
// 53.测试一下前置方式是否成功
     //   UserService userService = (UserService)applicationContext.getBean("userService");
        /**
         * 54. 当我们创建bean的时候 AOP 创建一个 BeanPostProcessor接口
          */

        /**
         * 62.测试BeanPostProcessor
         */
        UserInterface userService = (UserInterface)applicationContext.getBean("userService");
        userService.test();
    }

}
