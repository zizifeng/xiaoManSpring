package com.maroon.spring;

import java.beans.Introspector;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xiaoman
 */
public class XiaoManApplicationContext {
    // 3.用来承装传入配置类的属性
    private Class configClass;
    //24.创建一个存储beanDefinition对象Map属性 key 为bean的名字
    private ConcurrentMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    //32.创建一个存储单例对象的单例池
    private ConcurrentMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    //58.创建一个ArrayList对象来存储需要执行
    private ArrayList<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /**
     * 11.加载容器方法
     * 一、扫描 通过@ComponentScan这个注解获取需要由spring容器管理的包路径
     * 二、再通过@Component注解获取到需要交给spring管理bean类
     * 三、获取到对应包名后我们不会在加载类配置文件的时候创建bean对象
     * 而是通过@scope注解获取需要管理bean对象类型和作用域
     * 四、生成一个BeanDefinition对象用来装需要管理bean对象类型和作用域
     * 五、将生成每个BeanDefinition对象装到 beanDefinitionMap集合里 这样我们就可以用 getBean()方法来创建对象了
     *
     * @param configClass
     */
    public XiaoManApplicationContext(Class configClass) {
        this.configClass = configClass;
        /**
         * 当spring获取到了传进来配置类后 我们先要判断是否需要扫描
         *判断的依据就是 是否有@ComponentScan这个注解
         */
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            //12.判断有这个注解之后 应该获取这个ComponentScan注解的信息 只需要获取指定的注解
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            //13.当我启动spring之后 我就能通过你传入进来的配置类 通过这个ComponentScan注解的value值拿到扫描路径
            String path = componentScanAnnotation.value();
            /**此时拿到的path路径为com.maroon.service 这是我们设置的并不是完整的路径
             * 我们真正要扫描的不是这个src目录下的未编译文件.java
             * 而是应该扫描编译后的目录下classpath的.class文件
             */
            //14.获取对应的.class文件 先获取对应的类加载器
            ClassLoader classLoader = XiaoManApplicationContext.class.getClassLoader();
            //使用getResource()传入路径要用/分割 不是.
            path = path.replace(".", "/");
            //通过传入一个path相对路径 获得一个资源的文件夹
            URL resource = classLoader.getResource(path);

            //  System.out.println(resource);
            File file = null;
            try {
                //如果你电脑路径为中文 用此方法使获取路径不乱码 否则会一直报空指针异常
                file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //
            //file:  F:\xiaomanSpring\out\production\xiaomanSpring\com\maroon\service
            //15.判断获取到路径对应目录是不是一个文件夹
            if (file.isDirectory()) {
                //如果是一个文件夹 我们就去拿到目录下所有文件
                File[] files = file.listFiles();
                /**
                 * 拿到所有的文件 我们需要进行判断 
                 * 因为下面可以有很多的文件 而我们只需要处理.class文件
                 */
                //筛选出.class文件
                for (File file1 : files) {
                    //获取当前目录下的文件的绝对路径
                    String file1Path = file1.getAbsolutePath();
                    //file1Path:F:\xiaomanSpring\out\production\xiaomanSpring\com\maroon\service\UserService.class
                    //16.通过末尾.class来判断是否是.class文件
                    if (file1Path.endsWith(".class")) {
                        /**
                         * 拿到.class文件 我们仍需要处理 因为不是所有的.class都需要处理
                         * 我们需要判断这个类上是否有对应的注解@Component
                         * 通过反射去判断 如果有Component注解 则代表这个类需要交给spring容器去管理
                         */
                        //通过类加载 传入对应类的全限定名获取到对应类对象 包名.类名
                        URL resource1 = this.getClass().getResource("/");
                        String oldPath = new File(resource1.getFile()).toString();
                        String old = file1Path.toString();
                        String index = new File(resource1.getFile()).toString();
                        //截取到包名下的字符串并用.显示  com/maroon/service/AppConfig.class  =>  com.maroon.service.AppConfig
                        String oldReplace = old.replace(index + "\\", "").replace("\\", ".");
                        String replace = oldReplace.substring(0, oldReplace.indexOf(".class"));
                        Class<?> clazz = null;
                        try {
                            clazz = classLoader.loadClass(replace);
                            //拿到类对象 可以对其进行判断 类上面是否有对应 @Component注解
                            if (clazz.isAnnotationPresent(Component.class)) {
                                /**走到这里我们可以拿到类上有@Compoent注解的bean类
                                 * 但是也有一个问题就是 bean对象可能是单例或者多例的
                                 * 我们如何去判断需要创建的对象是单例还是多例呢
                                 * spring在这里(生成容器时候)不会创建bean对象 而是创建一个BeanDefinition对象
                                 *17.我们先创建一个BeanDefinition类
                                 *19. 当spring在扫描的时候发现一个类上面定义@Component注解并不会生成一个bean对象
                                 * 而是先生成一个BeanDefinition获取一些类的类型的信息 如单例还是多例
                                 */
                                /**59.
                                 *当扫描到这里之后 会去判断是否是BeanPostProcessor的实现类
                                 * 如果是 则将其bean对象实例放入beanPostProcessorList中
                                 */
                                if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                    BeanPostProcessor instance = (BeanPostProcessor) clazz.newInstance();
                                    beanPostProcessorList.add(instance);
                                }


                                //21.设置bean的作用域 单例或者多例
                                /**如何来判断一个bean的作用域呢(单例或者多例)？
                                 * 我们可以创建一个@Scope注解来判断
                                 * 当bean对象有这个注解则是多例没有则为单例
                                 */
                                //25.获取bean的名字 从Component注解里面获取
                                Component annotation = clazz.getAnnotation(Component.class);
                                String beanName = annotation.value();
                                //41.当我获取到beanName如果为空的时候 需要设置一个默认名
                                if ("".equals(beanName)) {
                                    //42.Spring里会调用这个decapitalize方法 这个方法会将你传进来字符首字母小写输出
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                                //生成BeanDefinition对象
                                BeanDefinition beanDefinition = new BeanDefinition();
                                //20.设置bean的类型
                                beanDefinition.setType(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    //获取到@Scope注解并获取设置作用域属性设置进去
                                    Scope scope = clazz.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scope.value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                /**
                                 * 23.到这里我们获取到了一个beanDefinition对象
                                 * 里面存储了我们将来需要创建的bean的作用域和类类型
                                 * 所以我们需要创建一个map集合将他存储起来 后面使用
                                 */

                                beanDefinitionMap.put(beanName, beanDefinition);

                            }
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }


                    }

                }
            }
            //30.通过前面的扫描获取map里面的beanDefinition的Scope属性为singleton的
            for (String beanName : beanDefinitionMap.keySet()) {
                String scope = beanDefinitionMap.get(beanName).getScope();
                if ("singleton".equals(scope)) {
                    //通过createBean方法来创建对象 如何来保证获取到对象是单例呢 我们需要用一个map来装获取到对象
                    Object bean = createBean(beanName, beanDefinitionMap.get(beanName));
                    //33.利用map的不可重复属性来存放单例对象 单例池
                    singletonObjects.put(beanName, bean);
                }
            }

        }

    }

    /**
     * 31.
     *
     * @param beanName       需要创建对象名字
     * @param beanDefinition bean对象的属性
     * @return 创建的单例对象
     */
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        /**
         * 37.当我们需要去创建一个bean的时候 我们要知道需要创建bean的类型 可以从beanDefinition中获取
         * 再利用反射创建一个对应的实例化对象
         */
        Class clazz = beanDefinition.getType();
        //通过反射 利用无参的构造方法获取对象
        try {
            Object instance = clazz.getConstructor().newInstance();
            //38.写到这里我们可以测试一下 逻辑是否走通 是否能创建一个单例对象 我们可以回到Test类进行测试
            //45.获取需要创建的bean对象里面是否有@Autowired注解 用来自动创建
            for (Field f : clazz.getDeclaredFields()) {
                //46.如果有@Autowired注解 我们对其赋值 调用set方法
                if (f.isAnnotationPresent(Autowired.class)) {
                    //47.使用反射赋值需要打开 简易版依赖注入
                    f.setAccessible(true);
                    //48.利用反射机制给用@Autowired标注的属性赋值
                    f.set(instance, getBean(f.getName()));
                }
            }
            /**49.如果我们想获取到创建bean对象名字应该怎么办？
             * bean对象名字一般就是在使用被Spring创建 一般为当前类名首字母小写
             * 可有时候可能在其他操作会修改需要获取
             *Spring会在依赖注入完后 去判断前面生成bean是否是BeanNameAware类的实现类
             */
            /**
             *50.Aware回调接口 由Spring给你某个东西并返回某些值
             */
            if (instance instanceof BeanNameAware) {
                //51.由于instance是BeanNameAware的实现类*所以我们可以将其强制转换成BeanNameAware类*再调用setBeanName方法设置名字
                ((BeanNameAware) instance).setBeanName(beanName);
            }
            /**
             * 60.执行beanPostProcessor里的方法
             */
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(beanName, instance);
            }

            /**
             * 52.初始化方法 由Spring在创建bean对象后直接调用你需要初始化的方法。
             */
            if (instance instanceof BeanNameAware) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
            /**
             * 61.执行beanPostProcessor里的方法
             */
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(beanName, instance);
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 26.通过beanDefinitionMap集合里的beanDefinition对象创建对应的bean对象
     *
     * @param beanName 传入对应bean对象名字
     * @return 返回一个bean对象
     */
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        //27.如果不存在这个名字对应类 抛出异常
        if (beanDefinition == null) {
            throw new NullPointerException();
        }
        //28.如果有我们先判断需要创建bean对象的作用域
        String scope = beanDefinition.getScope();
        //判断是否单例
        if ("singleton".equals(scope)) {
            /**29.如果需要创建的对象是单例的，我们可以直接在加载容器的构造方法中创建
             * 通过beanDefinition中的type属性 获取对应Class类类型 利用反射创建实例对象
             * 在将创建的对象放入单例池 singletonObjects中
             * 34.在用传入beanName属性查找
             */
            Object bean = singletonObjects.get(beanName);
            //35.如果单例池中没有 而又确定是单例的对象 则需要自己创建 并放入单例池中
            if (bean == null) {
                Object bean1 = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean1);
                return bean1;
            }
            return bean;
        } else {
            //36.多例对象我们可以在这里创建
            return createBean(beanName, beanDefinition);
        }

    }
}
