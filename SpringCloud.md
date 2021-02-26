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

