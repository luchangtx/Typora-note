SpringCloud + SpringCloud alibaba

> 微服务架构的理解：

服务的注册与发现	  eureka

服务调用							feign	

服务熔断\降级				hystrix

负载均衡							ribbon

配置中心管理					config

服务网关								zuul

服务消息队列

服务监控

全链路追踪

自动化构建部署

服务定时任务调度

上述提到的技术落地实现在2020年2月之后都有停更与变动，请及时更新技术实现

## 技术选型 版本

> 版本选择

SpringBoot2.2.x版本 + SpringCloudH版

> SpringBoot  查看官网最新的稳定版

并不一定选择最新的稳定版，根据官网的推荐使用

> SpringCloud  查看官网最新的稳定版

选择最新稳定版，查看官网推荐的boot版本进行对应

> SpringBoot和SpringCloud之间存在依赖关系，需要在官网中查看依赖关系

cloud的官网可以查看对应表

> 更详细的版本对应信息 start.spring.io/actuator/info

格式化上述地址返回的json查看对应的Cloud版本对SpringBoot版本的要求

## 技术升级惨案

多个组件升级、停更、过时

> 注册中心

`Eureka停更`

`Zookeeper（不推荐）`

`Consul（Go语言，不推荐使用）`

`Nacos（强烈推荐）`

> 服务调用

`Ribbon` 轻度患者，可以继续使用

`LoadBalancer` 新方案，尚未成熟

> 服务调用2

`Feign`停更

`OpenFeign`推荐使用

> 服务降级

`Hystrix` 停更

`Resilience4j` 国外替代方式

`Sentinel` 国内替代，推荐

> 服务网关

`Zuul` 停更

`Zuul2` 自己分裂想出Zuul2，最后胎死腹中

gateway 推荐使用

> 服务配置

`config` 可以用，不推荐了

`Nacos` 推荐使用

> 服务总线

`Bus` 不推荐

`Nacos` 强烈推荐

![image-20210226110628705](/Users/luchang/Library/Application Support/typora-user-images/image-20210226110628705.png)

## 从0开始搭建

```xml
<!--1、锁定子项目版本，子项目不用指定版本，升级时只需更新父模块
		2、只声明依赖，子类还需要引入依赖-->
<dependencyManagement></dependencyManagement>
```

## Ribbon负载均衡

> 更换默认的轮训机制

跳出项目包创建新的包，增加替换后的Irule，注册替换后的IRule bean。

之后在启动类增加@RibbonClient，注明需要更换的服务名，以及配置的IRule

> 手写负载均衡规则

手写LoadBalancer接口，主要要在项目包目录下，区别于更换rule 

实现接口，提供一个原子变量，记录请求次数，自旋锁 处理请求次数超过AtomicInteger最大范围，

重写LoadBalancer中获取服务实例的方法

```java
ServiceInstance instance(list<ServiceInstance> instances);
```

## OpenFeign

从 ribbon+restTemplate 变为 openFeign

> OpenFeign超时：默认超时等待1秒钟

需要在yml文件中设置ribbon

```yaml
ribbon:
  ReadTimeout: 60000
  ConnectTimeout: 60000
  MaxAutoRetries: 0
  MaxAutoRetriesNextServer: 0
  OkToRetryOnAllOperations: false
```

> OpenFeign日志打印

```java
import feign.Logger;
@Configuration
public class FeignConfig{
	@Bean
  Logger.Level feignLoggerLevel(){
    return Logger.level.FULL;
  }
}
```

```yaml
logging:
  level:
  # feign日志以什么级别监控指定接口
  com.xxx.xxFeign: debug
```

## 中级部分

> Hystrix 降级、熔断、限流

是三个不同的概念

- 服务降级 fallback：服务器繁忙或者不可用，也需要立刻返回给调用端一个友好的提示

- 服务熔断 break：类比保险丝，直接拒绝访问，然后调用降级方法友好提示

- 服务限流 flowlimit：秒杀高并发等，严禁一窝蜂拥挤

### 服务降级

一个服务提供者：自己服务中 高并发打到某个服务，致使该服务大量资源被分配去解决当前接口，导致该服务下其他接口响应受到影响

其次，服务消费者：看热闹不嫌事大，也开始调用上述服务，导致本服务的响应速度也收到影响

我们要用Hystrix对两个端进行处理

>- 对方服务超时，调用者不能卡死，必须降级
>- 对方服务down机，调用者不能卡死，必须降级
>- 对方ok，调用者自己故障，或要求的等待时间小于业务处理时间，自行降级

<span style="color:red">实现方式</span>

> 1：老方式

服务提供者：提供兜底服务

在提供者对应接口上增加如下代码  `HystrixCommandProperties`

```java
//HystrixProperties参数内容 参考HystrixCommandProperties
@HystrixCommand(fallbackMethod="beiyongMethod",commandProperties={
@HystrixProperties(name="execution.isolation.thread.timeoutInMilliseconds",value="3000")
})
```

在同类中增加beiyongMethod 方法，写降级代码

在启动类增加激活Hystrix @EnableCircuitBreaker

对于服务的超时异常、和运行异常也都会执行降级代码

客户端，即feign调用端增加配置

```yaml
feign:
  hystrix:
    enabled: true
```

启动类增加

```java
@EnableHystrix
```

> 2：新方法 解决 降级方法重复问题

在类上增加 全局的fallback

```java
@DefaultProperties(defaultFallback="")
```

需要降级的服务还是需要@HystrixCommand

> 3：解决降级方法与业务耦合问题（只针对客户端）

未来我们要面对的问题有三个

- 运行是异常
- 超时
- 服务器宕机

在FeignClient中增加FallbackFactory

### 服务熔断

服务降级 ——> 进而熔断 ——> 恢复调用链路

@CommandPeoperties四个关于熔断的参数

开启熔断、请求总数阈值、时间范围、错误百分比阈值

`默认是10s内超过20次请求，并且失败率达到50%会打开断路器`

短路器一旦打开，一段时间内所有请求都不会进行转发，直接总降级代码，达到一定时间，默认为`5s`，断路器会进入半开状态，会让其中的一个请求进行转发，如果成功，会关闭断路器，若失败，继续开启，重复上述步骤

![image-20210305183827061](/Users/luchang/Library/Application Support/typora-user-images/image-20210305183827061.png)

![image-20210305183737652](/Users/luchang/Library/Application Support/typora-user-images/image-20210305183737652.png)

服务降级：

- 断路器打卡
- 无信号量线程池等可用资源
- 调用服务失败

### 服务限流

高级篇 用 `阿里巴巴 sentinel` 解决



### HystrixDashboard 监控

> 后面都用spring cloud alibaba

启动类增加@EnableHystrixDashboard

@EnableHystrix

引入 starter-hystrix-dashboard

springcloud升级后的坑

![image-20210307221424296](/Users/luchang/Library/Application Support/typora-user-images/image-20210307221424296.png)

 

