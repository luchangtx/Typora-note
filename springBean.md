```java
        long start = System.currentTimeMillis();
        long duration = 0L;
        int bitRate = 0, width = 0, height = 0;
        File video = new File(absPath);
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(video);
        try {
            ff.start();
            duration = ff.getLengthInTime() / 1000;
            bitRate = ff.getVideoBitrate() / 1000;
            width = ff.getImageWidth();
            height = ff.getImageHeight();
            ff.stop();
        } catch (FrameGrabber.Exception e) {
            log.error(e.getMessage());
        } finally {
            try {
                ff.close();
            } catch (FrameGrabber.Exception e) {
                log.error(e.getMessage());
            }
        }
        log.info("时长 (ms): " + duration);
        log.info("码率: " + bitRate);
        log.info("宽高比：" + width + "：" + height);

        long end = System.currentTimeMillis();
        log.info((end - start) / 1000 + "  秒");
```

![image-20210122142621502](/Users/luchang/Library/Application Support/typora-user-images/image-20210122142621502.png)

![image-20210125111737160](/Users/luchang/Library/Application Support/typora-user-images/image-20210125111737160.png)

```java
/**
* Spring是一个完整的生态，不单单是一个技术框架，最重要的是保持扩展性
 * bean的配置有多种方式包括 xml、注解、其他形式
 * 统一处理bean定义的类：BeanDefinitionReader，处理成spring可以解析的对象
 * bean的定义信息类：BeanDefinition
 * bean的工厂类：BeanFactory
 * 生成的对象存放的Map集合——>即为实例化对象
 
 * PostProcessor 处理器/增强器
 * BeanFactoryPostProcessor 可以动态修改
 * BeanPostProcessor 分为两个阶段：先实例化、再初始化
 *	postProcessBeforeInitialization 初始化前完成
 * 	postProcessAfterInitialization 初始化后完成
 * 
 * 
 * 
 * FactoryBean 举例：Feign就是通过FactoryBean方式创建
 * 	getObject()、 getObjectType() 、isSingleton()
 * 
 * Environment  StandardEnvironment 系统环境+系统配置
 * 
 * 观察者模式：在spring容器中，有一系列的监听器，用来完成在不同的阶段做不同的处理 Listener
 */

/**
 * refresh()方法
 * 1、创建BeanFactory （DefaultListableBeanFactory）
 * 2、BeanDefinitionReader读取配置信息 （loadBeanDefinitions()）
 * 3、读取到BeanDefinition
 * 4、拿到BeanDefinition信息进行BeanFactoryPostProcessor处理
 * 		postProcessBeanFactory() （留给我们扩展)
 * 		invokeBeanFactoryPostProcessor() 执行BeanFactoryPostProcessor
 * 5、BeanFactory进行实例化操作 registerBeanPostProcessors() 实例化并且注册BeanPostProcessors
 * 6、初始化 initApplicationEventMulticaster() 初始化多播器或者广播器 -> registerListeners() 给广播器注册监听器
 * 7、初始化所有非来加载的单例模式 finishBeanFactoryInitialization()
 *  	populateBean() 填充属性
 * 		invokeAwareMethod() 处理aware接口的实现 设置对应的属性，方便在后续使用的时候直接获取相对的属性值 ？？？？
 * 		applyBeanPostProcessorsBeforeInitialization
 * 		invokeInitMethod() -> 初始化bean
 * 		applyBeanPostProcessorsAfterInitialization
 * 
 * 
 * 
 * BeanPostProcessor 实现类 
 * 	PlaceholderConfigurerSupport 处理配置文件中的路径、变量替换
 * 	AbstractAutoProxyCreator AOP
/*

//BeanFactoy中主要操作
//获取构造器
con=clazz.getConstructor();
//实例化对象
con.newInstance();
//不可能每个bean都需要获取构造器然后实例化，我们采用工厂模式
//所以我们需要套一层

public class BeanFactory{
  
}
```

## BeanPostProcessor 可以实现AOP

> 动态代理 jdk、cglib
>
> BeanPostProcessor实现类AbstractAutoProxyCreator

## bean的生命周期

> 创建过程：实例化bean、填充属性、前置处理、初始化bean、后置处理、完整bean对象
>
> 一堆aware接口填充属性、postProcessBeforeInitialization、初始化bean方法initBean、postProcessAfterInitialization

问题：如果需要在bean的创建过程中,详细了解每一个步骤完成的进度???

​			在不同的阶段要做不同的处理应该怎么做???

回答：观察者模式，监听器



![image-20210125143459857](/Users/luchang/Library/Application Support/typora-user-images/image-20210125143459857.png)

![image-20210125143527540](/Users/luchang/Library/Application Support/typora-user-images/image-20210125143527540.png)

![image-20210125143549642](/Users/luchang/Library/Application Support/typora-user-images/image-20210125143549642.png)

