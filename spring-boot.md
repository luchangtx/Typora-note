1.定制Banner，在resource下创建banner.txt文件，然后在网址http://www.network-science.de/ascii/一键生成
  关闭banner功能，在启动类中增加 SpringApplication app.setBannerMode(Mode.OFF);
2.自定义属性值，在application.yml里面配置
  a.创建配置类 @Component @Value("${test.name}")
  b.属性过多，创建配置类 @ConfigurationProperties(prefix="test")
  c.自定义配置文件，在resource下创建test.properties
    创建配置类 @Configuration @PropertySource("classpath:test.properties") @ConfigurationProperties(prefix="test") @Component

  > b和c要在启动类加 @EnableConfigurationProperties({配置类.class})
  > 属性间相互引用 test.title=${test.name}--${test.age}
3.通过命令设置属性值 
  比如：java -jar air.jar --server.port=8088来改变端口
  如果不希望通过命令行修改配置 SpringApplication app.setAddCommandLineProperties(false);
4.使用xml配置，启动类增加 @ImportResource({"classpath:some-application.xml"})
5.profile配置
  多环境配置命名规则必须位 application-dev.yml等，在application.yml中通过属性spring.profiles.active=dev指定
  可在运行jar包时使用 java -jar air.jar --spring.profiles.active=dev 来切换不同环境配置
6.可以使用 mvn dependency:tree 查看jar包的依赖情况
7.安装离线下载的jar包到本地仓库
  mvn install:install-file -Dfile=D:/ojdbc6.jar -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=6.0 -Dpackaging=jar -DgeneratePom=true

## spring的理解

spring 是一个框架，也是一套完整的生态，我们现在开发过程中用到的包几乎都依赖于spring，提供了ioc容器存储bean对象，他帮我们管理了对象从创建到销毁的整个生命周期，我们在使用的时候可以通过配置文件或者注解的方式来进行相关实现。

当我们程序开始启动，我们要把注解或者配置文件配置的那些对象处理成BeanDefinition，然后根据这些定义通过BeanFactory来进行实例化bean操作，最简单的方式就是通过反射机制进行创建，这其中还可以实现BeanFactoryPostProcessor接口进行扩展，完成对象创建只是在堆里开辟了空间，并未完成后续一些列的初始化操作，然后还会实现aware接口、init 方法操作，如果要实现aop，还需要实现BeanPostProcessor接口进行扩展操作，等着一系列操作都完成之后，我们就可以进行bean的使用了

## AOP

> 实现非业务功能的思路
>
> - 手动修改class或者使用ASM（是一个java字节码操控框架）
> - 重新生成或者在原来的class文件基础上增加日志处理
>
> 同样的代码，有一部分需要增加日志功能，另一部分不需要
>
> - 增加表达式，包括方法名字、方法参数、返回值、完全限定名来匹配对应
>
> 对应springAOP 通知（Advice）
>
> - Before
> - After
> - Arond
> - afterThrowing
> - afterReturning
>
> 责任链：CallbackInfo

MethodInterceptor：通知想要被执行的时候需要实现，进行相关的拦截

InvocationHandler：jdk动态代理

> aop概念

切面

切点

连接点

通知

织入

> aop执行过程
>
> - IOC容器进行实例化和初始化操作，将生成的完整对象存到容器中
>
>   > 容器需要的对象
>
>   - BeanFactoryPostProcessor
>   - BeanPostProcessor
>   - AbstractAutoProxyCreator
>
>   > 用户需要的对象
>
>   - 用户自定义
>
>   > advice 通知
>
>   - MethodInterceptor
>
> - 从创建的容器中获取需要的对象
>
> - 调用具体的方法开始进行调用
>
> - 我们可能定义了多个通知，都需要执行，因此我们需要引入责任链，放入一个list
>
>   - around
>   - before
>   - after
>   - afterReturning
>   - afterThrowing
>
>   exposeInvcationInterceptor

![image-20210201172127286](/Users/luchang/Library/Application Support/typora-user-images/image-20210201172127286.png)

每次回到chain对象上是为了找到一下个执行通知是谁

advice会有一次拓扑排序，每次排序出的结果不一样

![image-20210201171714283](/Users/luchang/Library/Application Support/typora-user-images/image-20210201171714283.png)

只有当所有的通知执行完毕之后，才会调用实际的方法，而之前

<span style="color:red;">调用的advice只是为了指定通知的执行顺序，把琏中的执行逻辑都罗列清楚，按照具体的方法进行返回操作?</span>

after、afterReturning、afterThrowing都是在实际的逻辑代码之后执行的

around、before是在具体的代码执行之前执行的

> 所以最终执行顺序 是：

around --> before --> afterThrowing --> afterReturning --> after --> around

> 用AOP实现声明式事务

![image-20210201180421032](/Users/luchang/Library/Application Support/typora-user-images/image-20210201180421032.png)

ConfigClassPrase 处理@Configuration、@Component、@ComponentScan等注解

CommonBeanPostProcessor处理@PostContructor、@PreDestory

动态代理类，是在bean实例化后，执行beanPostProcessor的after的时候生成的 AbstractAutoProxyCreator





**##** **SpringBoot**

1、为什么选择springboot？？

​	因为能让我们快速的开发spring项目

2、为什么能快速的构建spring项目

​	使用约定优于配置的方式省去了繁琐的配置从而快速的构建spring 项目

​	开发规范 可以不遵守。只是一种约定，能让后续的开发和维护变得方便

3、举个例子（体现约定优于配置）

​	》默认的application.yml

​	》spring-boot-starter-web,默认集成tomcat，默认会在resource/ ，template、static

​	》spring启动时候扫描META-INF/spring-factories

4、为什么做简化？

​	（applicationContext.xml配置文件一定需要——IOC、DI）而大部分描述的是应用内部的bean

​	bean的维护问题：如果配置文件非常多怎么办？

5、springboot依赖于 spring Framework、如果是web应用还依赖于spring mvc

​	spring mvc依赖servlet

​	webflux 是netty自己实现的一个事件响应框架

6、springboot有哪些真正意义的创新

​	Spring Boot Starter开箱即用的组件

​	EnableAutoConfiguration自动装配

​	Actuator应用监控

​	Spring Boot CLI命令行工具 ，基于groovy脚本快速运行spring boot

7、spring boot要解决的痛点，因何而生？？？

bean 的管理问题：有多个applicationContext.xml配置文件，有很多bean配置，维护特别困难



\-----------------------------------------------------------------------------------

**##** **spring**注解驱动的发展历史

spring1.x

​	bean管理问题

​	@Transaction

spring2.x（2.5时代，注解并没有发挥价值，很多团队没有采用注解，依然选择xml）

​	bean管理问题:大部分描述的是应用内部的bean

​	@Repository dao层

​	@Component 组件层

​	@Service 服务层

​	@RequestMapping

​	@Qualifier 给bean加了个标记



总结：仍然在解决bean 的装载问题（注入）

问题：为什么没有完全脱离xml？？？

​	context:commponent-scan 这种注解还是存在（开启包扫描）

spring3.x时代

​	@Configuration -> JavaConfig -> 通过注解方式配置bean

​	@ComponentScan

​	@Import 导入一个配置

​	@ImportResource 导入一个资源文件（可以是xml文件，import导入的是一个 类）

xml完全被注解取代，spring进入无配置化时代，还是在解决bean的装载问题

​	@Enable 模块驱动，完成bean的自动装配

​		>>@EnableScheduling 完成了和@Scheduling注解解析相关的bean的注入

​		>>完成某个固定功能 的bean的自动装载



spring4.x时代 （决定了哪些类可以被装载）

​	@Conditional 注解 -> 条件装配，需要自定义一个类实现Condition接口，并重写方法

在装载一个bean 的时候，需要判断上下文信息，如果存在某个bean，才要做装载 



spring5.x时代 -->基于注解的优化，不再是bean的装载

​	@Index spring启动过程中需要装载很多bean，引入该注解进行优化



\-----------------------------------------------------------------------------------

spring注解驱动往spring boot发展



Springboot时代

不再 需要关心bean的配置！

bean要不要装载，什么时候装载？

SPI：SpringFactoriesLoader（读取spring-factories）

![image-20210225191401280](/Users/luchang/Library/Application Support/typora-user-images/image-20210225191401280.png)

Spring动态装载bean 的方式：ImportSelector接口、Registration接口

------------------------------------作业---------------------------------------

@EnableAutoConfiguration+Starter

自动转配+约定优于组件=开箱即用的组件（starter）

SpringFactoriesLoader SPI机制

SpringFactoriesLoader.loadFactoryByName()   