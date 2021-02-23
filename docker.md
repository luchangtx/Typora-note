CL/CD/Jenkins

## Docker概述

项目带上环境安装打包、跨平台

> 核心思想：打包装箱（镜像），相互隔离

## Docker历史

> 2010年，几个年轻人成立了dotCloud，做一些pass云计算服务！LUX有关的容器技术
>
> 将自己的容器化技术命名为Docker
>
> 2013年将Docker开源，越来越多人发现Docker的优点，每个月更新一个版本
>
> 2014年4月，Docker1.0发布

> 优点

十分轻巧，在此之前都用的是虚拟机技术

- 虚拟机：笨重

- 容器：也是虚拟化技术

  > Docker隔离，镜像（最核心的环境），十分小巧

> Docker是go语言开发
>
> Docker的文档是超级详细的
>
> 仓库地址：https://hub.docker.com/

## Docker能干嘛

> 虚拟机

- 资源占用多

- 冗余步骤多

- 启动很慢

  虚拟出一条硬件，运行一个完整的操作系统，然后在这个系统上安装运行

> 容器化技术

- 不是模拟一个完整的操作系统

  直接运行在宿主机，容器没有自己的内核，也没有虚拟的硬件，所以轻便

> DevOps（开发、运维）

> 镜像

好比一个模板，可以通过模板创建容器服务，tomcat镜像===>run===>tomcat1容器

通过镜像可以创建多个容器

> 容器

Docker利用容器技术，独立运行一个或一组应用，通过镜像来创建

启动、停止、删除基本命令

> 仓库

仓库就是存放镜像的地方：分为公有仓库和私有仓库

Docker Hub（默认是国外的）、阿里云

## 安装Docker

> 官网地址：https://www.docker.com/
>
> Product manuals > install 选择centos

```bash
# 卸载老版本的Docker
$ sudo yum remove docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine
```



77r2nknx