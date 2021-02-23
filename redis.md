

## Nosql概述

> 单机mysql

> Memcache（缓存）+Mysql+垂直拆分（读写分离）
>
> 缓存解决读的压力

> 分库分表+水平拆分+mysql集群
>
> 分库分表解决写的压力；mysql推出了表分区，但是没多少人使用

> 大数据时代

> 1、商品的描述，评论（文字比较多）
>
> > 一般存储在文档型数据库中，如 MongoDb
>
> 2、图片
>
> > 分布式文件系统 FastDFS
> >
> > - 淘宝 TFS
> > - Google GFS
> > - Hadoop HDFS
> > - 阿里云 OSS
>
> 3、关键字（搜索）
>
> > 搜索引擎 solr、elasticsearch
> >
> > 淘宝 -ISearch （阿里 多隆开发的）
>
> 4、热门波段信息
>
> > 内存数据库
> >
> > - Redis、Tair、Memcache

数据源过多，大面积表重构，所以阿里增加了统一的数据处理层 UDSL

## NOSQL的四大分类

> KV键值对

- 新浪：Redis
- 美团：Redis+Tair
- 阿里百度：Redis+Memcache

> 文档型数据库（bson格式和json一样）

- MongoDB
- - 是一个基于分布式文件存储系统的数据库，C++编写，主要用来处理大量的文档
  - 介于关系型数据库和非关系型数据库之间，是非关系型数据库中最想关系型的数据库
- CouchDb

> 列存储数据库

- HBase
- 分布式文件系统

> 图关系数据库

- 不是村图形，放的是关系，比如：朋友圈社交网络，广告推荐
- - Neo4j 
  - infoGrid

![image-20210128140918916](/Users/luchang/Library/Application Support/typora-user-images/image-20210128140918916.png)

## Redis入门

> 远程字典服务 Remote Dictionary Server
>
> ANSI C语言编写

`Redis能干嘛`

> - 内存存储，持久化， 内存中是断电即失所以说持久化很重要（rdb、aof）
>
> - 效率高，用于高速缓存
>
> - 发布订阅系统
>
> - 地图信息分析
>
> - 计时器、计数器（浏览量！）

`Redis特性`

> - 多样的数据类型
> - 持久化
> - 集群
> - 事务

## 安装redis

> 官方建议在linux环境下部署

Linux 下 redis 默认安装路径：`/usr/local/bin`

``` bash
#前提，所有软件 解压到 /usr/local/software 下各自对应的文件夹
#redis解压到/usr/local/software/redis，并且也安装到此处，安装好之后，会多出一个bin文件夹

#1.官网下载：https://redis.io/download
#2.解压：
	tar -zxvf redis-xxx.tar.gz
#3. 编译安装到指定目录
  make PREFIX=/usr/local/software/redis install
#4.安装成功查看版本(解压目录下)：
	redis-server -v
#5.启动：
	#未指定配置文件，默认采用/usr/local/software/redis/redis.conf启动
	redis-server
#6.设置后台运行
	#/usr/local/software/redis创建conf文件夹，用于存放redis自定义配置文件
	mkdir conf
	#拷贝默认配置到创建的目录下
  cp redis.conf /usr/local/software/redis/conf
  #编辑配置文件：
  vim /usr/local/software/redis/conf/redis.conf
  #找到daemonize no 改为 yes
#7.指定配置文件启动即可后台运行
  redis-server /usr/local/software/redis/conf/redis.conf
#8.设置允许远程访问
	#低版本可以屏蔽掉如下命令即可
	# bind 127.0.0.1
	requirepass <密码> 
	#高版本 如果未设置密码，需要关闭自我保护模式才可以访问
	protected-mode yes
	#总结：3种方式远程连接
	# 1：开启远程保护、屏蔽bind 127.0.0.1、设置requirepass <password>
	# 2：开启远程保护、bind 服务器ip、可以不设置requirepass <password>
	# 3：关闭保护模式、屏蔽bind 127.0.0.1、可以不设置requirepass <password>
 #9.服务化
 # 1：创建服务脚本 
 # 2：创建软连接，即windows的快捷图标
 cd /lib/systemd/system
 touch redis.service
 vi redis.service
 
#####################步骤1#####################
 [Unit]
Description=Redis
# 网络服务启动之后
After=network.target

[Service]
# 此处daemonize 必须为no，否则将不起作用，不服你试试
ExecStart=/usr/local/software/redis/bin/redis-server /usr/local/software/redis/conf/redis.conf --daemonize no
ExecStop=/usr/local/bin/redis-cli -h 127.0.0.1 -p 6379 shutdown

[Install]
# 是以哪种方式启动：multi-user.target表明当系统以多用户方式（默认的运行级别）启动时，这个服务需要被自动运行
WantedBy=multi-user.target
#####################步骤2######################
#创建软连接
 ln -s /lib/systemd/system/redis.service  /etc/systemd/system/multi-user.target.wants/redis.service
#####################步骤3######################
# 刷新配置，是服务能被systemctl识别
systemctl daemon-reload
#开启服务
systemctl start redis
#停止服务
systemctl stop redis
#重启服务
systemctl restart redis
#开机启动
systemctl enable redis
#禁止开机启动
systemctl disable redis
  
#####################service 方式管理redis，老方法######################
#服务化，设置开机启动
  #进入解压目录，复制响应文件
  cp utils/redis_init_script /etc/init.d/redisd
  #编辑文件：
  vim /etc/init.d/redisd
  #第二行加入：表示开机启动
  #!/bin/sh
  # chkconfig: 2345 80 90
  #修改CONF为刚设置的redis.conf所在目录
	CONF="/usr/local/software/redis/conf/redis.conf"
#添加至服务：
	chkconfig --add redisd
#1开启关闭命令为
  service redisd start
  service redisd stop

```

## redis工具

![image-20210128173000331](/Users/luchang/Library/Application Support/typora-user-images/image-20210128173000331.png)

```bash
#redis-benchmark是一个官方提供的压力测试工具
cd /usr/local/software/redis/bin/
# 测试：100个并发连接  100000个请求
redis-benchmark -h localhost -p 6379 -c 100 -n 100000
```

![image-20210128173433571](/Users/luchang/Library/Application Support/typora-user-images/image-20210128173433571.png)

所有请求在3ms内处理完所有请求

## redis基础命令

默认有16个数据库，默认使用第0个数据库

```bash
#切换3号数据库
127.0.0.1:6379> select 3
#查看数据库大小
127.0.0.1:6379[3]> dbsize
```

```bash
#查看所有key
keys *
#设置key-value值
set name luchang
#获取key锁对应的值
get name
#清空数据库
flushdb
#清除所有数据库
flushall
#查看key是否存在
exists name
#移动，将当前库中的key移动到指定库
move name 1
#设置key的过期时间 ，设置10s后过期
expire name 10
#查看当前key的剩余时间 -2表示过期
ttl name
#查看当前key的类型
type name
```

## Redis是单线程的

> redis是基于内存操作的，所以redis的性能瓶颈不是CPU，而是内存和网络带宽，所以使用单线程

> Redis是c语言写的，官方提供数据为100000+的QPS（每秒处理的查询数）完全不比同为k-v的Memcache差

`为什么单线程还这么快`

> - 误区：高性能的服务器一定是多线程的？
> - 误区：多线程（CPU上下文切换）一定比单线程效率高
>
> 核心：redis是将所有数据放在内存的，没有上下文切换，多次读写都在一个CPU上的，在内存情况下，这个是最佳方案

## Redis五大数据类型

> Redis-Key
>
> ```bash
> 172.16.196.236:6379> keys *
> (empty list or set)
> 172.16.196.236:6379> set name luchang
> OK
> 172.16.196.236:6379> get name
> "luchang"
> 172.16.196.236:6379> EXPIRE name 10
> (integer) 1
> 172.16.196.236:6379> get name
> "luchang"
> (nil)
> 172.16.196.236:6379> ttl name
> (integer) -2
> 172.16.196.236:6379> ttl name1
> ```

### `String`

```bash
172.16.196.236:6379> set name luchang
OK
172.16.196.236:6379> type name
string
172.16.196.236:6379> APPEND name  hahaha #追加字符串，如果当前key不存在，就相当于set
(integer) 13
172.16.196.236:6379> get name
"luchanghahaha"
172.16.196.236:6379> APPEND name  " j"
(integer) 15
172.16.196.236:6379> get name
"luchanghahaha j"
172.16.196.236:6379> STRLEN name #获取key的value长度
(integer) 15
```

```bash
172.16.196.236:6379> set num 1
OK
172.16.196.236:6379> get num
"1"
172.16.196.236:6379> INCR num #自动+1
(integer) 2
172.16.196.236:6379> get num
"2"
172.16.196.236:6379> DECR num #自动-1
(integer) 1
172.16.196.236:6379> get num
"1"
172.16.196.236:6379> INCRBY num 10 #设置自增步长
(integer) 11
172.16.196.236:6379> get num
"11"
172.16.196.236:6379> DECRBY num 5 #设置自减步长
(integer) 6
172.16.196.236:6379> get num
"6"
```

```bash
172.16.196.236:6379> set key1 "hello,world"
OK
172.16.196.236:6379> get key1
"hello,world"
172.16.196.236:6379> getrange key1 0 3 #截取字符串，包含收尾
"hell"
172.16.196.236:6379> getrange key1 0 -1 #获取所有 等同get key
"hello,world"
172.16.196.236:6379> setrange key1 5 " " #替换字符串，从索引5开始，包含5，根据实际长度进行替换
(integer) 11
172.16.196.236:6379> get key1
"hello world"
```

>分布式锁相关命令

```bash
172.16.196.236:6379> setex key2 30 luchang #设置key2的值为luchang，30s过期
OK
172.16.196.236:6379> ttl key2
(integer) 26
172.16.196.236:6379> ttl key2
(integer) 24
172.16.196.236:6379> setnx key3 reids #如果不存在设置key3，如果不存在设置失败
(integer) 1
172.16.196.236:6379> setnx key3 mysql
(integer) 0
172.16.196.236:6379> get key3
"reids"
```

> 批量设置

```bash
172.16.196.236:6379> mset k1 v1 k2 v2 k3 v3 #批量设置值
OK
172.16.196.236:6379> keys *
1) "k3"
2) "k1"
3) "k2"
172.16.196.236:6379> mget k1 k2 k3 # 批量获取
1) "v1"
2) "v2"
3) "v3"
172.16.196.236:6379> msetnx k1 v1 k4 v4 # msetnx批量设置为原子操作，同时成功，同时失败
(integer) 0
172.16.196.236:6379> mget k1 v4 #k1存在，设置失败 k4不存在设置成功，但是整体失败
1) "v1"
2) (nil)
```

> key 的一个巧妙的设计： user:1:name zhangsan

```bash
172.16.196.236:6379> mset user:name luchang user:age 18 user:sex 1
OK
172.16.196.236:6379> mget user:name user:age user:sex
1) "luchang"
2) "18"
3) "1"
172.16.196.236:6379> 
```

> 先get值再set值   CAS:比较并交换

```bash
172.16.196.236:6379> getset key1 luc
(nil)
172.16.196.236:6379> getset key1 tang
"luc"
172.16.196.236:6379> get key1
"tang"
```

> String类型使用场景
>
> - 计数器
> - 统计数量 （浏览量）
> - 粉丝数
> - 对象缓存存储

### `List`

> 在redis里，我们可与把list完成：栈、队列、阻塞队列
>
> 所有的list命令都是 L 开头的

```bash
lpush list one #将一个值插入列表的头部
lpush list two
lpush list three
lrange list 0 -1 #输出three two one 倒序的

rpush list new #将一个值插入到列表的尾部

lpop list #将列表头部的第一个值移除
rpop list #将列表尾部的最后一个值移除

lindex list 0 #根据索引获取某一个值
llen list #获取list的长度

#移除指定的值
lrem list 1 one #移除list中指定个数的value值
lrem list 2 one
```

> 通过下标截取指定长度，改变元数据

```bash
101.133.164.156:6379> rpush list hello
(integer) 1
101.133.164.156:6379> rpush list hello1
(integer) 2
101.133.164.156:6379> rpush list hello2
(integer) 3
101.133.164.156:6379> rpush list hello3
(integer) 4
101.133.164.156:6379> lrange list 0 -1
1) "hello"
2) "hello1"
3) "hello2"
4) "hello3"
101.133.164.156:6379> ltrim list 1 2
OK
101.133.164.156:6379> lrange list 0 -1
1) "hello1"
2) "hello2"
```

> 移除列表中最后一个元素，移动到其他新的列表

```bash
101.133.164.156:6379> lrange list 0 -1
1) "hello1"
2) "hello2"
101.133.164.156:6379> rpoplpush list mylist
"hello2"
101.133.164.156:6379> lrange list 0 -1
1) "hello1"
101.133.164.156:6379> lrange mylist 0 -1
1) "hello2"
```

> lset命令修改索引值

```bash
101.133.164.156:6379> lset list 0 luchang
(error) ERR no such key
101.133.164.156:6379> rpush list v1
(integer) 1
101.133.164.156:6379> lrange list 0 -1
1) "v1"
101.133.164.156:6379> lset list 0 luchang
OK
101.133.164.156:6379> lrange list 0 -1
1) "luchang"
```

> 将某个具体的value插入到列表中某个具体的value的前面或者后面

```bash
101.133.164.156:6379> rpush list 1
(integer) 1
101.133.164.156:6379> rpush list 1
(integer) 2
101.133.164.156:6379> rpush list 2
(integer) 3
101.133.164.156:6379> rpush list 3
(integer) 4
101.133.164.156:6379> lrange list 0 -1
1) "1"
2) "1"
3) "2"
4) "3"
101.133.164.156:6379> linsert list before 1 new
(integer) 5
101.133.164.156:6379> lrange list 0 -1
1) "new"
2) "1"
3) "1"
4) "2"
5) "3"
101.133.164.156:6379> linsert list after 1 new
(integer) 6
101.133.164.156:6379> lrange list 0 -1
1) "new"
2) "1"
3) "new"
4) "1"
5) "2"
6) "3"
```

> 小结
>
> - 实际上是一个链表
> - 在两边插入或者改动值，效率最高！中间元素相对来说效率低
>
> 消息队列：左进 Lpush 右出 Rpop
>
> 栈：先进 Lpush 后出 Lpop

### `Set`

> 无序不重复集合
>
> 所有的set命令都是 s 开头的

```bash
172.16.196.236:6379> sadd myset luchang #sadd插入元素
(integer) 1
172.16.196.236:6379> sadd myset luchang #不可重复
(integer) 0
172.16.196.236:6379> smembers myset #查看所有
1) "luchang"
172.16.196.236:6379> sismember myset luc #判断是否存在元素
(integer) 0
172.16.196.236:6379> sismember myset luchang
(integer) 1
#######################################################
172.16.196.236:6379> scard myset #获取set集合中元素个数
(integer) 1
172.16.196.236:6379> srem myset luchang #移除指定元素
(integer) 1
172.16.196.236:6379> scard myset
(integer) 0
172.16.196.236:6379> smembers myset
(empty list or set)
#######################################################
172.16.196.236:6379> sadd set 1
(integer) 1
172.16.196.236:6379> sadd set 2
(integer) 1
172.16.196.236:6379> srandmember set #随机抽取出指定个数的元素
"2"
172.16.196.236:6379> srandmember set 2
1) "1"
2) "2"
#######################################################
172.16.196.236:6379> spop set #随机删除指定个数元素，默认1
"3"
172.16.196.236:6379> smembers set
1) "1"
2) "2"
3) "4"
172.16.196.236:6379> smove set myset 1 #移除set元素到指定元素到myset中
(integer) 1
172.16.196.236:6379> smembers set
1) "2"
2) "4"
172.16.196.236:6379> smembers myset
1) "1"
```

> 数字集合类
>
> - 差集
> - 交集
> - 并集
>
> 应用场景：微博A、b用户所有 关注各放入一个set；所有粉丝各放入另一个 set
>
> A、B共同关注、共同粉丝

```bash
172.16.196.236:6379> sadd set1 1 2 3
(integer) 3
172.16.196.236:6379> sadd set3 a b c
(integer) 3
172.16.196.236:6379> sdiff set1 set3 # 差集
1) "1"
2) "2"
3) "3"
172.16.196.236:6379> sunion set1 set3 # 并集
1) "a"
2) "2"
3) "3"
4) "c"
5) "1"
6) "b"
172.16.196.236:6379> sinter set1 set3 # 交集
(empty list or set)
```

### `Hash(哈希 散列)`

> Map集合，即Redis-Key中存的值 还是一个  k-v格式
>
> 所有命令以H开头
>
> 使用场景
>
> - 排行榜
> - 带权重进行判断
> - 重要消息

```bash
172.16.196.236:6379> hset mhash name luchang #set一个具体的kv
(integer) 1
172.16.196.236:6379> hset mhash age 18
(integer) 1
172.16.196.236:6379> hset mhash sex 男
(integer) 1
172.16.196.236:6379> hget mhash name #获取一个具体的kv
"luchang"
172.16.196.236:6379> hget mhash age
"18"
172.16.196.236:6379> hget mhash sex
"\xe7\x94\xb7"
172.16.196.236:6379> hmset mhash area SH phone 13838384338 #set多个具体的kv
OK
172.16.196.236:6379> hmget mhash name age sex area phone #获取多个具体的kv
1) "luchang"
2) "18"
3) "\xe7\x94\xb7"
4) "SH"
5) "13838384338"
172.16.196.236:6379> hdel mhash sex #删除hash中指定的k-v
(integer) 1
```

> 获取所有hash 的key value，一行key 一行value

```bash
172.16.196.236:6379> hgetall mhash
1) "name"
2) "luchang"
3) "age"
4) "18"
5) "area"
6) "SH"
7) "phone"
8) "13838384338"
```

> 获取hash表字段数据

```bash
172.16.196.236:6379> hlen mhash
(integer) 4
```

> 判断hash中的指定字段是否存在 

```bash
172.16.196.236:6379> hexists mhash name
(integer) 1
```

> 获取所有的字段、获取所有的值

```bash
172.16.196.236:6379> hkeys mhash
1) "name"
2) "age"
3) "area"
4) "phone"
172.16.196.236:6379> hvals mhash
1) "luchang"
2) "18"
3) "SH"
4) "13838384338"
```

> Incr decr hsetnx

```bash
172.16.196.236:6379> hset mhash num 0
(integer) 1
172.16.196.236:6379> hincrby mhash num 10
(integer) 11
172.16.196.236:6379> hincrby mhash num -10
(integer) 1
172.16.196.236:6379> hsetnx mhash email 1258
(integer) 1
172.16.196.236:6379> hsetnx mhash email 1258
(integer) 0
```

> hash存变更的数据，尤其是用户信息之类的
>
> hash  的value可以当string使用  h

```bash
#id可以为用户的真实id，可以动态
172.16.196.236:6379> hset user:id name luchang
(integer) 1
172.16.196.236:6379> hget user:id name
"luchang"
```

### `Zset`

> 在set的基础上增加了一个值，set k1 v1

```bash
172.16.196.236:6379> zadd zset 1 one
(integer) 1
172.16.196.236:6379> zadd zset 2 two 3 three 4 four #添加多个值
(integer) 2
172.16.196.236:6379> zrange zset 0 -1 #查看
1) "one"
2) "two"
3) "three"
4) "four"
```

> 排序

```bash
172.16.196.236:6379> zadd salary 2500 xiaohong
(integer) 1
172.16.196.236:6379> zadd salary 5000 zhangsan
(integer) 1
172.16.196.236:6379> zadd salary 300 luchang
(integer) 1
172.16.196.236:6379> zrangebyscore salary -inf +inf # 从负无穷到正无穷
1) "luchang"
2) "xiaohong"
3) "zhangsan"
172.16.196.236:6379> zrevrange salary 0 1 withscores # 从大到小进行排序
1) "zhangsan"
2) "5000"
3) "luchang"
4) "300"
172.16.196.236:6379> zrangebyscore salary -inf +inf withscores #带分数
1) "luchang"
2) "300"
3) "xiaohong"
4) "2500"
5) "zhangsan"
6) "5000"
172.16.196.236:6379> zrangebyscore salary -inf 2500 #小于等于2500
1) "luchang"
2) "xiaohong"
```

> 移除指定元素

```bash
172.16.196.236:6379> zrem salary xiaohong #是按值来移除的
(integer) 1
```

> 获取有序集合的个数

```bash
172.16.196.236:6379> zcard salary
(integer) 2
```

> 获取区间内的成员数量

```bash
172.16.196.236:6379> zcount salary 500 2500
(integer) 0
172.16.196.236:6379> zcount salary 500 5000
(integer) 1
```

## 总结

> 其余的api，如果工作中有需要，可以从官方文档查看

## geospatial地理位置

> 微信附近的人、打车距离计算
>
> 从城市经纬度查询 拔一些数据存到redis
>
> 只有6个命令

- GEOADD  '南极和北极是无法添加的'
- GEODIST
- GEOHASH
- GEOPOS
- GEORADIUS
- GEORADIUSBYMEMBER

> 添加经纬度

```bash
#(error) ERR invalid longitude,latitude pair 39.900000,116.400000 可能是经纬度写反了
172.16.196.236:6379> geoadd china:city 116.40 39.90 beijing
(integer) 1
172.16.196.236:6379> geoadd china:city 121.47 31.23 shanghai
(integer) 1
172.16.196.236:6379> geoadd china:city 106.50 29.53 chongqing 
(integer) 1
172.16.196.236:6379> geoadd china:city 114.05 22.52 shenzhen
(integer) 1
172.16.196.236:6379> geoadd china:city 120.16 30.24 hangzhou
(integer) 1
172.16.196.236:6379> geoadd china:city 108.96 34.26 changan
(integer) 1
```

> 获取指定的地区的经纬度

```bash
172.16.196.236:6379> geopos china:city beijing
1) 1) "116.39999896287918091"
   2) "39.90000009167092543"
```

> 两人之间的距离

```bash
172.16.196.236:6379> geodist china:key beijing shanghai #查看上海到北京的直线距离
(nil)
172.16.196.236:6379> geodist china:city beijing shanghai
"1067378.7564"
172.16.196.236:6379> geodist china:city beijing shanghai km
"1067.3788"
172.16.196.236:6379> geodist china:city changan shanghai km
"1216.9307"
```

> 附近的人（获取附近的人的地址，定位），通过半径来查询

```bash
#以 110 30 经纬度坐标为中心，搜索500 km 之内的数据
172.16.196.236:6379> georadius china:city 110 30 500 km
1) "chongqing"
2) "changan"
#带出经纬度和 距中心距离
172.16.196.236:6379> georadius china:city 110 30 500 km withcoord withdist
1) 1) "chongqing"
   2) "341.9374"
   3) 1) "106.49999767541885376"
      2) "29.52999957900659211"
2) 1) "changan"
   2) "483.8340"
   3) 1) "108.96000176668167114"
      2) "34.25999964418929977"
#只查出一个数据
172.16.196.236:6379> georadius china:city 110 30 500 km withcoord withdist count 1
1) 1) "chongqing"
   2) "341.9374"
   3) 1) "106.49999767541885376"
      2) "29.52999957900659211"
```

> 找出距离指定元素周围半径的数据

```bash
172.16.196.236:6379> georadiusbymember china:city beijing 1000 km
1) "beijing"
2) "changan"
```

> 将二维数据转换为一维的字符串，字符串越接近，距离越接近

```bash
172.16.196.236:6379> geohash china:city changan beijing shanghai
1) "wqj6zky6bn0"
2) "wx4fbxxfke0"
3) "wtw3sj5zbj0"
```

> 可以使用zset命令来操作geo

```bash
172.16.196.236:6379> zrange china:city 0 -1 withscores
 1) "chongqing"
 2) "4026042091628984"
 3) "changan"
 4) "4040115445396757"
 5) "shenzhen"
 6) "4046432193584628"
 7) "hangzhou"
 8) "4054133997236782"
 9) "shanghai"
10) "4054803462927619"
11) "beijing"
12) "4069885360207904"
```

## HyperLogLog

> 什么是基数
>
> A：{1,3,5,7,8,7}
>
> B：{1,3,5,7,8}
>
> 基数：不重复的元素=5，可以接受误差
>
> <span style="color:red;font-weight:700">HyperLogLog：是做基数统计的算法</span>
>
> 应用场景：
>
> - 网页的UV（一个人访问多次，仍然算作一个人）
> - - 传统方式：用set集合保存用户id，统计set中元素数据
>   - - 如果保存大量的用户id，就会比较麻烦，我们的目的是为了计数，而不是保存id
>   - HyperLogLog：占用的内存是固定的，2^64不同元素只需12kb内存，有0.81%的错误率

```bash
172.16.196.236:6379> pfadd num 1 2 3 4 5 6 7 #创建一组元素num
(integer) 1
172.16.196.236:6379> pfadd num1 2 3 4 8 9 a #创建一组元素num1
(integer) 1
172.16.196.236:6379> pfcount num #统计num元素的基数
(integer) 7
172.16.196.236:6379> pfcount num1 #统计num1元素的基数
(integer) 6
172.16.196.236:6379> pfmerge num2 num num1 #合并两组num和num1到num2 并集
OK
172.16.196.236:6379> pfcount num2 #看并集的基数
(integer) 10
172.16.196.236:6379> pfcount num1
(integer) 6
172.16.196.236:6379> pfcount num
(integer) 7
```

## Bitmap

> 位存储

疫情感染人数：0 1 0 1 0 （感染用1，未感染用0）

统计活跃用户、登录未登录、打卡……两个状态的都可以使用

>Bitmap是位图，数据结构，都是操作二进制位来进行记录
>
>365天=365bit 1字节8bit，46个字节左右就可以存一个用户一年的数据

```bash
#模拟一周的打卡情况
172.16.196.236:6379> setbit clock 1 1
(integer) 0
172.16.196.236:6379> setbit clock 0 1
(integer) 0
172.16.196.236:6379> setbit clock 2 1
(integer) 0
172.16.196.236:6379> setbit clock 3 1
(integer) 0
172.16.196.236:6379> setbit clock 4 1
(integer) 0
172.16.196.236:6379> setbit clock 5 0
(integer) 0
172.16.196.236:6379> setbit clock 6 0
(integer) 0
########################################
172.16.196.236:6379> getbit clock 0 #获取星期一的打卡情况
(integer) 1
172.16.196.236:6379> getbit clock 1
(integer) 1
172.16.196.236:6379> getbit clock 5
(integer) 0
172.16.196.236:6379> bitcount clock #统计打卡的天数
```

## redis事务

原子性：同时成功、同时失败  <span style="color:red">Redis事务不保证原子性</span>

一个事务中所有命令都会被序列化，在事务执行过程中，会按照顺序执行

`一次性` `顺序性` `排他性`

> 没有隔离级别的概念
>
> 所有命令在事务中，并没有直接被执行，只有发起执行命令才会执行！ Exec
>
> Redis单条命令是保证原子性，但是事务不保证原子性

```
----队列 set set set 执行----
```

Redis事务

- 开启事务
- 命令入队
- 执行事务

> 正常的事务执行

```bash
172.16.196.236:6379> multi #开启事务
OK
172.16.196.236:6379> set k1 v1 #命令入队
QUEUED
172.16.196.236:6379> set k2 v2
QUEUED
172.16.196.236:6379> get k2
QUEUED
172.16.196.236:6379> set k3 v3
QUEUED
172.16.196.236:6379> exec #执行事务
1) OK
2) OK
3) "v2"
4) OK
```

> 放弃事务

```bash
172.16.196.236:6379> discard #取消事务，之后上述入队的所有事务都不会执行
OK
```

> 编译型异常（代码有问题，命令有错），事务所有命令都不会执行

```bash
172.16.196.236:6379> multi
OK
172.16.196.236:6379> set k1 v1
QUEUED
172.16.196.236:6379> set k2 v2
QUEUED
172.16.196.236:6379> set k3 v3
QUEUED
172.16.196.236:6379> getset k3 #命令有错
(error) ERR wrong number of arguments for 'getset' command
172.16.196.236:6379> set k5 v5
QUEUED
172.16.196.236:6379> exec #执行事务也报错。所有命令都不会执行
(error) EXECABORT Transaction discarded because of previous errors.
172.16.196.236:6379> get k1
(nil)
```

> 运行时异常，如果事务队列存在语法性，那执行命令的时候，其他命令正常执行，错误命令抛出异常

```bash
172.16.196.236:6379> set k1 "v1" #字符串类型数据
OK
172.16.196.236:6379> multi #开启事务
OK
172.16.196.236:6379> incr k1 #自增，执行时会有异常
QUEUED
172.16.196.236:6379> set k2 v2
QUEUED
172.16.196.236:6379> set k3 v3
QUEUED
172.16.196.236:6379> get k3
QUEUED
172.16.196.236:6379> exec #有一条执行失败
1) (error) ERR value is not an integer or out of range
2) OK
3) OK
4) "v3"
172.16.196.236:6379> get k2
"v2"
```

## 监控

<span style="color:red;font-weight:700">面试常问</span>

悲观锁

- 无论做什么都要加锁

乐观锁

- 假设不会出现问题，不会加锁，在更新数据的时候判断，在此期间是否有人修改过
- - 类似mysql中增加version

> redis的监控测试

```bash
172.16.196.236:6379> set money 100 # 存款100
OK
172.16.196.236:6379> set out 0 #消费0
OK
172.16.196.236:6379> watch money #监控money
OK
172.16.196.236:6379> multi #事务正常结束，数据期间未发生变动，此时正常执行
OK
172.16.196.236:6379> decrby money 20
QUEUED
172.16.196.236:6379> incrby out 20
QUEUED
172.16.196.236:6379> exec
1) (integer) 80
2) (integer) 20
```

> 测试多线程修改值，使用watch做乐观锁

```bash
#线程A
172.16.196.236:6379> set money 100
OK
172.16.196.236:6379> set out 0
OK
172.16.196.236:6379> watch money #监控money
OK
172.16.196.236:6379> multi
OK
172.16.196.236:6379> decrby money 10
QUEUED
172.16.196.236:6379> incrby out 10
QUEUED
172.16.196.236:6379> exec #执行之前另一个事务修改了money的值
(nil)
#线程B在A执行前修改money的值，导致A事务执行失败
172.16.196.236:6379> get money
"100"
172.16.196.236:6379> set money 1000
OK
#A线程使用unwatch解锁，重新加锁开启事务
172.16.196.236:6379> unwatch #解锁
OK
172.16.196.236:6379> watch money #重新加锁
OK
172.16.196.236:6379> multi #重新开启事务
OK
172.16.196.236:6379> decrby money 10
QUEUED
172.16.196.236:6379> incrby out 10
QUEUED
172.16.196.236:6379> exec
1) (integer) 990
2) (integer) 10
```

## Jedis

我们要使用java来操作Redis

> Jedis是官网推荐的java连接开发工具，如果要使用java操作redis，一定要对jedis十分熟悉

## lettuce

> Springboot2.x之后，默认的jedis替换为lettuce
>
> - jedis采用直连，多个线程操作的话是不安全的，如果想要避免，使用jedis pool更像BIO
> - lettuce采用netty，实例可以在多个线程共享，不存在线程不安全，可以减少线程数量，更像NIO模式

```java
 @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate();

        //序列化
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        serializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //配置具体的序列化
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
//配置序列化 用于解决默认jdk序列化 导致key 中文乱码
```

## redis.conf

> 启动的时候就通过配置文件启动的

> 单位：对大小写不敏感

> 包含include：可以将别的配置文件包含进来

> 网络

```bash
bind 127.0.0.1
protected-mode yes #
port 6379
```

> 通用

```bash
daemonize yes #以守护进程的方式后台运行，默认是no，我们需要改为yes
pidfile /var/run/redis_6379.pid #如果以后台方式运行，就需要指定一个pid文件
loglevel notice #日志级别
logfile "" #默认生成的日志文件位置名
database 16 #默认有16个数据库
always-show-logo yes #是否总是显示logo
```

> 快照

持久化，在规定的时间内，执行了多少次操作，则会持久化到文件 .rdb、.aof

redis是内存数据库，如果没有持久化，就会丢失

```bash
#如果900秒内，如果至少有1个key进行修改，我们就进行持久化操作
save 900 1
#如果300秒内，如果至少有10个key进行修改，我们就进行持久化操作
save 300 10
#如果60秒内，如果至少有10000个key进行修改，我们就进行持久化操作
save 60 10000

#我们之后学习持久化， 会自己定义这个测试

#持久化出错是否需要继续工作，默认为true
stop-writes-on-bgsave-error yes
#是否压缩rdb文件，需要消耗一些cpu资源
rdbcompression yes
#保存rdb文件的时候，进行错误校验
rdbchecksum yes
#rbd文件保存的目录
dir ./
```

> REPLICATION 复制，后面学习主从复制的时候需要 掌握

replicaof <masterip> <masterport>

> SECURITY 模块，可以设置密码 

```bash
requirepass foobared<为你自己的设置的密码>
#也可以在redis客户端用命令修改
config set requirepass 123456
```

> CLIENTS 客户端限制

```bash
#最大的客户端连接数
maxclients 10000
maxmemory <bytes> #redis最大的内存容量
maxmemory-policy noeviction #内存达到上限之后的处理策略
    # 1、volatile-lru：支队设置了过期时间的key进行LRU.
    # 2、allkeys-lru：删除LRU算法的key.
    # 3、volatile-lfu：Evict using approximated LFU among the keys with an expire set.
    # 4、allkeys-lfu -> Evict any key using approximated LFU.
    # 5、volatile-random：随机删除即将过期的key.
    # 6、allkeys-random：随机删除key.
    # 7、volatile-ttl：删除即将过期的.
    # 8、noeviction：永不过期，返回错误.
```

> APPEND ONLY MODE aof配置

```bash
appendonly no #默认不开启aof，因为默认使用rdb持久化，在大部分情况下rdb就够用了
appendfilename "appendonly.aof" #持久化文件的名字 rdb文件默认为 .rdb

# appendfsync always #每次修改都会sync，消耗性能
appendfsync everysec #每秒执行一次 sync，可能会丢失这一秒的数据
# appendfsync no #不同步，这个时候操作系统自己同步数据，速度最快
```

具体的配置在持久化中配置

## redis持久化

### RDB模式

创建一个fork子进程进行持久化，先写到一个临时rdb文件，持久化结束了，再用这个临时文件替换上次持久化好的文件默认情况下就是rdb，一般不需要改配置

rdb文件就是dump.rdb文件

> 出发机制

1、save命令的规则满足的情况下，会自动出发rdb的规则，生成rdb文件

2、执行flushall命令，也会出发我们的rdb

3、退出redis，也会产生rdb文件

备份就会自动生成一个备份文件 dump。rdb

> 如何恢复rdb文件？

只需要将rbd文件放到我们redis启动的目录下，就会自动检查恢复数据

```bash
172.16.196.236:6379> config get dir
1) "dir"
2) "/" #如果在这个目录下存在dump.rdb文件，启动redis就会自动恢复其中的数据
```

> 几乎redis自己的默认配置就够用了，但是我们还是需要学习

优点：

1、适合大规模的数据恢复

2、如果你对数据的完整性要求不高！

![image-20210131101446965](/Users/luchang/Library/Application Support/typora-user-images/image-20210131101446965.png)

缺点：

1、需要一定的时间间隔进行操作！如果redis意外宕机，这个最后一次修改的数据就没了

2、fork进程备份的时候，会占用一定的内存空间！！！

总结：生产环境，我们会对dump.rdb进行备份

### AOF模式

将我们所有命令都记录下来，即history，恢复的时候就把这个文件的命令在执行一遍

也是开一个子进程，以日志的形式来记录每个写操作，将redis执行的所有指令记录下来，读操作不记录，只追加文件，但是不可以改写文件，redis启动之初会读取该文件重新构建数据，换言之，redis重启的话就根据日志文件的内容将写指令从前到后再执行一次就可以完成数据的恢复工作

AOF保存的是appendonly.aof

默认是不开启的，我们需要手动进行配置，我们只需要将

```bash
appendonyl no # 改为yes即可开启aof持久化
```

redis 安装bin目录下有 

> redis-check-aof ：aof文件有问题，会影响redis的启动，可以用这个工具进行修复

```bash
redis-check-aof --fix appendonly.aof
```

> 优点和缺点

一种是全丢，一种是只丢错误数据

优点：

1、每一次修改都同步，文件完整性会更好

```bash
appendonly no #默认不开启aof，因为默认使用rdb持久化，在大部分情况下rdb就够用了
appendfilename "appendonly.aof" #持久化文件的名字 rdb文件默认为 .rdb

# appendfsync always #每次修改都会sync，消耗性能
appendfsync everysec #每秒执行一次 sync，可能会丢失这一秒的数据
# appendfsync no #不同步，这个时候操作系统自己同步数据，速度最快
#rewrite #重写规则说明
```

2、每秒同步一次，了能会丢失一秒

3、从不同步，效率最高

缺点：

1、相对于数据文件来说aof远大于rdb，比rdb慢

2、aof运行效率也比rbd慢，所以默认rdb

![image-20210131102200694](/Users/luchang/Library/Application Support/typora-user-images/image-20210131102200694.png)

## 发布和订阅

![image-20210131102607923](/Users/luchang/Library/Application Support/typora-user-images/image-20210131102607923.png)

> 命令

![image-20210131102648631](/Users/luchang/Library/Application Support/typora-user-images/image-20210131102648631.png)

> 订阅段

```bash
172.16.196.236:6379> subscribe cctv #订阅一个频道cctv
Reading messages... (press Ctrl-C to quit)
1) "subscribe"
2) "cctv"
3) (integer) 1
#等待读取推送消息
1) "message" #消息
2) "cctv" #频道
3) "hello,world" #消息的具体内容
```

> 发布端

```bash
172.16.196.236:6379> publish cctv "hello,world" #发布者发布消息到频道
(integer) 1
```

![image-20210131104345354](/Users/luchang/Library/Application Support/typora-user-images/image-20210131104345354.png)

> 使用场景

1、实时消息系统

2、实习聊天（频道当做聊天室）

3、订阅，关注系统

## 主从复制

只能由主节点写入从节点，主节点负责写，从节点负责读，最低配：一主二从

![image-20210201113332501](/Users/luchang/Library/Application Support/typora-user-images/image-20210201113332501.png)

### 环境配置

> 只配置从库，不配置主机

```bash
172.16.196.236:6379> info replication #查看基本信息
# Replication
role:master
connected_slaves:0
master_replid:6a18f48e405d19b23d6d92fcb638876995f1a9be
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:0
second_repl_offset:-1
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0
```

复制三个配置文件，修改对应的信息

1、端口

2、pid名字

3、log文件名

4、dump.rdb名字

默认情况下，每一台都是主节点

> 从机配置

主机6379、从一6389、从二6381

```bash
172.16.196.236:6380> slaveof 127.0.0.1 6379  #从机配置主机
```

上述命令配置是暂时的，真实的应该在配置文件中配置才是悠久生效的

```bash
replicaof <masterip> <masterport>
```

> 细节了解

主机可以写，从机只能读，主机中所有信息都会被从机自动保存

> 测试：

主机：断开连接，从机依旧连接到主机，但是没有写操作，如果主机重启，从机依旧能连接获取写的信息（没有配置哨兵模式，是不会进行新的主机选举的）

从机：如果是使用命令行配置的主从，这个时候，如果重启，从机会变成主机，但是只要变为从机，立马就会从主机获得数据

![image-20210201115431028](/Users/luchang/Library/Application Support/typora-user-images/image-20210201115431028.png)

只要重新连接到主机，一定会进行一次全量复制

1、6379（主） ——> 6380（从）

​		  				  ——> 6381（从）

2、6379（主） ——> 6380（从）——> 6381（从）

> 第二种情况的主从复制模式，6380依旧是从节点，不能进行写操作

? 如果主机宕机，是否能继续选举出一个节点作为主机呢？（哨兵模式）

```bash
172.16.196.236:6380> slaveof on one #如果没有主机，让自己变成主机
```

如果这个时候 原来的主机重新连接，需要重新配置主从关系

## 哨兵模式

> 主机宕机之后，自动选举 主机

![image-20210201132344224](/Users/luchang/Library/Application Support/typora-user-images/image-20210201132344224.png)

哨兵是一个独立的进程

> 哨兵的两个作用

![image-20210201132533362](/Users/luchang/Library/Application Support/typora-user-images/image-20210201132533362.png)

![image-20210201132720552](/Users/luchang/Library/Application Support/typora-user-images/image-20210201132720552.png)

> 测试

以一主二从为例

在redis配置目录下增加哨兵配置文件

> sentinel.conf

```bash
vim sentinel.conf
#myredis 被监控的名称，后面的1代表主机挂了，让从机投票看谁能成为主机
sentinel monitor myredis 127.0.0.1 6379 1
#启动哨兵  bin目录下
redis.sentinel ../conf/sentinel.conf
```

如果master宕机，会从从机中票举一台成为主机，如果主机重新连接，只能当做新主机的从机

> 哨兵集群

优点：

1、哨兵集群基于主从复制模式，所有主从的优点都具备

2、主从可以切换，故障可以转移，可用性好

3、哨兵模式就是主从模式的升级，手动变为自动，更加健壮

缺点：

1、redis不好在线扩容，集群容量一旦达到上限，在线扩容十分麻烦

2、哨兵模式全部配置很麻烦

> 全部配置

- 如果有哨兵集群，还需配置哨兵端口

![image-20210201134713735](/Users/luchang/Library/Application Support/typora-user-images/image-20210201134713735.png)

![image-20210201134841939](/Users/luchang/Library/Application Support/typora-user-images/image-20210201134841939.png)

![image-20210201135053135](/Users/luchang/Library/Application Support/typora-user-images/image-20210201135053135.png)

## 缓存穿透

> 不会详细分析解决方案的底层

> 布隆过滤器
>
> - 一种数据结构，对所有可能查询的参数以hash形式存储，在控制层先进行校验，不符合则丢弃，从而避免了对底层存储系统的查询压力

> 缓存空对象
>
> - 空值缓存起来，意味着需要更多的空间存储更多的键， 而这些有没有意义
> - 空值设置了过期时间，还是会存在缓存和存储层的数据有一端时间窗口的不一致，对需要保持一致的业务有影响

## 缓存击穿

> 缓存击穿
>
> - 指一个热点key，在不停的扛着大并发，大并发集中对一个key进行访问，当key失效的瞬间，持续的大并发就击穿缓存，直接请求数据库，就像击穿了屏幕一样，导致数据存储层压力瞬间上升以致击垮

> 解决方案
>
> - 设置热点数据不过期
> - 加互斥锁：加锁，保证只有一个线程可以进入数据存储层请求数据，其余的进行等待

## 缓存雪崩

> 在某一时间段，缓存集体过期，redis宕机等
>
> > 产生原因：
> >
> > - 比如双12前一批数据集中写入了缓存，刚好到12点整缓存集中过期，导致所有对该批数据的访问都落到了数据库

> 解决方案
>
> - redis高可用，增设几台redis服务器，即搭建redis集群
> - 限流降级：缓存失效后，通过分布式锁控制进入数据库的线程数
> - 数据预热：在正式部署之前，先把可能的数据预先访问一遍，加载到缓存，在即将放生大并发前对不同的key进行却别设置，另失效时间尽量均匀（比如双十一 预售）

## 总结











