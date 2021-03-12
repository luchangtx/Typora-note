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

 

## Gateway

> 核心逻辑：路由转发+执行过滤链

路由、断言、过滤

![image-20210308171226564](/Users/luchang/Library/Application Support/typora-user-images/image-20210308171226564.png)

> Predicate

![image-20210308172333293](/Users/luchang/Library/Application Support/typora-user-images/image-20210308172333293.png)

> Gateway 中的过滤器

一般不用提供的 经典的过滤器，一般自己写自定义的过滤器

![image-20210308173256719](/Users/luchang/Library/Application Support/typora-user-images/image-20210308173256719.png)

## springcloud-config

>服务端

```yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      # 读取git中的配置文件
      server:
        git:
          username: lizelong
          password: sundear12345678..@
          uri: http://git.sundear.com:808/lm-ad/sundear-properties.git
          search-paths: dev
```

> 客户端

```yaml
# 会从配置中心读取 ${spring.application.name}-dev.yml的配置
cloud:
  config:
    discovery:
      enabled: true
      service-id: config-server
    fail-fast: true
    label: master
    name: ${spring.application.name}
    profile: dev
    username: luomaConfigUser
    password: luomaConfigUser..@
```

> 手动刷新配置，不需重启config客户端

引入actuator 监控依赖，在yml中增加 端点暴露配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always
```

在主启动类增加@RefreshScope 注解启动刷新功能

`客户端执行` curl -X POST "http://localhost:3355/actuator/refresh"

执行请求进行手动刷新

> 动态刷新

`springcloud bus` 目前仅支持 `rabbitmq`和`kafka`

 服务端、客户端增加消息总线的依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-bus</artifactId>
</dependency>
<dependency>
		<groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-stream-binder-rabbit</artifactId>
</dependency>
<!--或者-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

`服务端执行`curl -X POST "http://localhost:3344/actuator/bus-refresh"

> 刷新定点通知

服务端口执行：curl -X POST "http://localhost:3344/actuator/bus-refresh/{serverName}:3355"

## Springcloud Stream

> 消息驱动

消息提供者：定义消息的推送管道

```java
//自定义接口，增加一个send方法
//实现类增加如下
@EnableBinding(Source.class)
//注入
@Autowired
private MessageChannel output;
//重写方法,方法体内容
String message="消息内容，可以是其他格式";
output.send(MessageBuilder.withPayload(message).build);
return null;
```

消息消费者：

```java
@Component
@EnableBinding(Sink.class)
//自定义方法如下
@StreamListener
public void input(Message<String> message){
  //获取消息
  message.getpayload();
}
```

```yaml
spring:
  cloud:
    stream:
      binders:
       defaultRabbit: #表示定义的名称，用于bingding的整合
         type: rabbit
         environment:
           spring:
             rabbitmq:
               host:
               port:
               username:
               password:
      bindings:
        output: #生产者写output，消费者写input
          destination: studyExchange #表示自定义的消息 交换机（rabbitmq），kafaka对应Topic
          content-type: application/json
          binder: defaultRabbit #设置上述设置的消息服务
          group: groupA #设置分组可以解决重复消费的问题，以及持久化
```

## Nacos

dynamic Naming configuration service

derby内置数据库

> 命名空间、分组、dataId

>集群配置，分三个步骤

`第一步`

- 安装mysql，因为集群需要基于mysql进行持久化迁移

- 进入nacos conf目录，修改配置前先进性备份

- 修改application.properties，增加如下配置

- ```properties
  ############mysql数据库配置###############
  spring.datasource.platform=mysql
  db.num=1
  db.url.0=jdbc:mysql://172.16.196.236:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
  db.user=root
  db.password=123456
  ```

`第二步`

- 修改集群配置文件cluster.conf

- ```bash
  hostname -i #查看服务器识别的hostname
  ```

- 增加三个节点的 host:port配置如下

  - ```bash
    172.16.196.236:3333
    172.16.196.236:4444
    172.16.196.236:5555
    ```

`第三步`

- 修改启动脚本 startup.sh

- ```bash
  # 增加端口变量 t
  while getopts ":m:f:s:c:p:t:" opt
  do
      case $opt in
          m)
              MODE=$OPTARG;;
          f)
              FUNCTION_MODE=$OPTARG;;
          s)
              SERVER=$OPTARG;;
          c)
              MEMBER_LIST=$OPTARG;;
          p)
              EMBEDDED_STORAGE=$OPTARG;;
          t)
              PORT=$OPTARG;;
          ?)
          echo "Unknown parameter"
          exit 1;;
      esac
  done
  #在最后启动脚本增加参数
  nohup $JAVA -Dserver.port=${PORT} ${JAVA_OPT} nacos.nacos >> ${BASE_DIR}/logs/start.out 2>&1 &
  ```

- 然后使用脚本+端口号启动

  - ```bash
    ./startup.sh -t 3333
    ```

如果非集群启动，启动时需增加参数

```bash
./startup.sh -m standalone
```



## Sentinel

![image-20210310173734589](/Users/luchang/Library/Application Support/typora-user-images/image-20210310173734589.png)

### 流控规则

阈值设置分2种

- QPS
- 线程数

QPS+线程数：流控模式

- 直接
- 关联
- 链路

QPS 流控效果

- 快速失败
- warm up 预热
- 排队等待

`流控效果为快速失败`

> QPS - 直接 - 快速失败 报错是默认的错误信息，后续会增加自定义的处理
>
> 线程数 - 直接 - 快速失败 
>
> QPS - 关联 -快速失败 A关联B，B请求超过阈值，A挂掉（支付模块和订单模块）

`流控效果为预热`

冷加载因子 coldFactor默认是3，即请求的QPS从 阈值/3 开始，经过预热时长 逐渐升至设定的QPS阈值

> QPS 阈值设为10 ，流控模式  直接，流控效果 warmup，预热时长5s
>
> 意思是 初始 阈值将从 10/3 =3 开始，每秒3次访问，超过直接失败，5s之后，慢慢恢复为每秒10次访问

`流控效果为排队等待`

漏桶算法，让请求匀速通过，只允许 阈值 范文内的请求同时进来超过的进行匀速排队通过

### 降级规则

#### RT慢调用比例

RT平均响应时间 > 阈值，触发 断路器打开，进行服务降级，窗口期结束，在关闭断路器恢复 

#### 异常比例

QPS>=5 并且异常比例超出阈值，才会进行降级熔断保护

#### 异常数

按分钟统计，异常数大于阈值，进行降级熔断保护

### 热点规则

某一时刻，某个热点的key大流量访问，比如 `新冠肺炎`在某一时刻大流量访问，我们需要对其进行热点key防护

> 仅支持QPS模式，参数索引，即get请求参数的位置

```java
@GetMapping("hotkey")
//参数自定义,名称唯一即可
@SentinelResource(value = "hotkey", blockHandler = "dealHotKey")
public String hotkey(@RequestParam("p1") String p1, @RequestParam("p2") String p2) {
	return "-------hotkey";
}
public String dealHotKey(String p1, String p2, BlockException exception) {
	return "sorry,wait hotkey";
}
```

自定义fallback 从 HystrixCommand到SentinelResource

`上述热点配置如果配的是参数索引是0，阈值是1，那请求参数中只要带p1，并且超出阈值就会降级，但是如果请求参数没有p1只有p2，则不会降级`

旧版本可能还存在参数例外项

意思是 索引是配置的位置，但是值是具体的某个值时设置例外的阈值

`仅处理规则配置的错误，代码里的异常不做降级保护`

### 系统规则

针对整个参与监控的微服务都有效

### @SentinelResource

客户自定义限流，临时规则 持久化以及与业务代码解耦

自定义类CustomerBlockHandler，写多个兜底方法，在SentinelResource注解中指定类和方法名

## Sentinel整合Ribbon+openfeign+fallback

> @SentinelResource
>
> 仅配置blockhandler，如果规则不是异常，则java运行异常不做降级保护
>
> 仅配置fallback，异常也会走fallback
>
> 如果两个都配置，会以blockhandler的规则为准

## Sentinel 规则配置持久化

结合nacos

```yaml
spring:
  application:
    name: provider9001
  cloud:
    nacos:
      discovery:
        username: nacos
        password: nacos
        server-addr: 101.133.164.156:3333 # nacos的地址
    sentinel:
      transport:
        dashboard: localhost:8080 #配置sentinel dashboard 地址
        port: 8719 #服务端口,默认是8719,如果被占用,会+1尝试
        clientIp: 192.168.0.136 #需要制定sentinel所在服务器的ip,否则会报连接超时
      datasource:
        ds1:
          nacos:
            server-addr: 101.133.164.156:3333
            dataId: ${spring.application.name}
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: flow
```

服务关掉之后，限流规则消失？？？

只有重启之后再调用相关接口，才会再次出现

那关掉之后响应的限流降级保护 岂不是没有了

## 分布式事务 seata

一个ID，三个组件

> TC 维护全局和分支事务的状态，驱动全局事务回滚或提交
>
> TM 定义全局事务的范围，开始、提交、回滚全局事务
>
> RM 管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务的提交或回滚

提供了四种事务模式

默认是AT，分两阶段

第一阶段：各个事务参与方，在执行业务sql前，进行 before image 前置镜像处理，然后执行完之后再进行after image后置镜像处理，然后生成行锁

第二阶段提交：如果顺利，业务sql在第一阶段`已传至数据库`所以seata只需将一阶段保存的before 和after image及行锁删掉，完成数据清理即可提交

第二阶段回滚：利用之前的before image进行业务数据还原，但是之前要进行对比，防止脏写，对比当前数据库数据和after image，如果一致进行还原，不一致则说明已经有人操作了出现了脏写，需要转为人工处理（因为第一阶段执行完业务sql 加了行锁，所以其他线程进来是要阻塞等待）

> AT 写隔离

现有两个全局事务，tx1和tx2分别 操作a表，

现在tx1先开启本地事务，记录当前 镜像 before image

```mysql
select num from a where id=1;
```

然后执行业务sql

```mysql
update a set num=1 where id=1;
```

生成after image

```mysql
select num from a where id=1;
```

准备提交本地事务，在此之前要先获得全局锁，本地事务提交后释放本地锁

tx2 开启本地事务，拿到本地锁，经过before image、执行业务sql、after image 等操作，提交本地前尝试获取全局锁，但全局所tx1持有，进入等待

会有两种情况

- tx1 提交成功，释放全局锁，tx2获取全局锁提交本地事务
- tx1 全局回滚，需要获取本地所，进行反向补偿，但此时本地锁tx2持有，则tx1会回滚失败，并且一直尝试，直到tx2全局锁等待超时，释放全局锁并回滚本地事务释放本地锁，tx1才可以获取本地所进行事务回滚。

上述过程中tx1在结束之前一直持有全局锁，所以不会发生脏写

> AT读隔离

在数据库本地事务隔离级别是读已提交或以上的基础上，AT模式的全局事务隔离级别是读未提交，如果特定场景，需要全局事务是读已提交，目前seata的实现方式是通过 select for update 语句代理。