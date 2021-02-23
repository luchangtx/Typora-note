ELK 数据清洗 日志收集器 ElasticSearch、logstash、kibana

## ElasticSearch概述

全文检索功能

> Doug Cutting 美国工程师，开发了lucene、Nutch、hadoop
>
> | Google    | Hadoop    |
> | --------- | --------- |
> | GFS       | HDFS      |
> | MapReduce | MapReduce |
> | BigTable  | HBase     |
>
> 大数据：存储+计算  GFS+ MapReduce
>
> 安装
>
> 分词器 ik
>
> SpringBoot集成es
>
> <font color='red'>爬取数据，做模拟全文搜索</font>

## Lucene和ES的关系

>Shay Banon作者
>
>Lucene是一套信息检索工具包，jar包，不包含搜索引擎系统
>
>包含的：**索引结构**、**读写索引的工具**、**排序**、**搜索规则**...... 一开始用的 **solr**
>
>ElasticSearch 是基于Lucene 做了一些封装和增强

## ES和Solr的选择

## 解压目录

```bash
bin #启动文件目录
config #配置文件
	log4j2 #日志配置文件
	jvm.options #jvm配置
	elasticsearch.yml # es配置文件 默认9200端口！跨域问题配置！
lib #相关jar包
plugins #插件 ik分词器就在这里安装
```

<span style="font-weight:1000;color:red;">这里有一些小问题？？？</span>

> 如果是在服务器上安装，可能会遇到如下问题
>
> ```bash
> max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
> ```
>
> 需要设置config目录下的jvm.options ,将jvm的启动内存设置一下
>
> 再去**vim /etc/sysctl.conf**
>
> ```bash
> vim /etc/sysctl.conf
> #在最后一行添加
> vm.max_map_count=26144
> ```
>
> 然后执行如下命令立即生效
>
> ```bash
> sysctl -p
> ```
>
> 

> ```bash
> the default discovery settings are unsuitable for production use; at least one of [discovery.seed_hosts, discovery.seed_providers, cluster.initial_master_nodes] must be configured
> ```
>
> 默认发现设置不适合生产使用；必须配置[discovery.seed_hosts，discovery.seed_providers，cluster.initial_master_nodes]中的至少一个
>
> 在 elasticsearch.yml中添加树下配置
>
> ```bash
> cluster.initial_master_nodes: ["node-1"]
> ```



## 安装head

1、访问9200

2、安装可视化界面 es head 插件，需要npm node.js环境 github下载

3、启动：npm安装依赖，npm run start

​			连接测试存在跨域，配置  http.cors.enabled: true  http.cors.allow-origin: "*"

就把索引当成数据库

4、head当成数据展示工具，尽量不写命令，去kibana做

## 安装Kibana

ELK 工作：收集清洗数据-->搜索，存储-->Kibana

Kibana和ES版本保持一致

自己修改Kibana 的配置文件 i18n国际化配置

## ES核心概念

面向文档

| mysql  | es（json）                   |
| ------ | ---------------------------- |
| 数据库 | 索引                         |
| 表     | types 即将弃用，默认  (_doc) |
| 行     | documents（一条条数据）      |
| 字段   | fields                       |

> es一个人就是一个集群，默认集群名是 elasticsearch

> 倒排索引
>
> ![image-20210126145953468](/Users/luchang/Library/Application Support/typora-user-images/image-20210126145953468.png)

 ## IK分词器

如果使用中文，建议使用IK分词器 github下载，版本对应

> ik_smart（最少切分）、ik_max_word（最细粒度划分）、standard（标准的会使用分词器）、keyword（不会分析，一个整体的值）

> 查看不同的分词器效果
>
> GET _analyze{
>
> ​	"analyzer": "ik_smart",
>
> ​	"text": "超级喜欢肉比骨头有骨气"
>
> }
>
> GET _analyze{
>
> ​	"analyzer": "ik_max_word",
>
> ​	"text": "超级喜欢肉比骨头有骨气"
>
> }
>
> 如果发现问题，肉比骨头有骨气 被拆开了，即我们自己创造的词，解析不了，需要我们自己添加到ik分词器中
>
> 编写自己的配置文件 my.dic，配置到xml文件中

## RESTful风格说明

> Put、post、delete、get
>
> ![image-20210126153743687](/Users/luchang/Library/Application Support/typora-user-images/image-20210126153743687.png)

## 关于索引的操作

1. 创建索引

   > PUT /索引名/~类型名~/文档id
   >
   > {请求体}
   >
   > PUT /test1/type1/1
   >
   > {
   >
   > ​	"name": "肉比骨头有骨气",
   >
   > ​	"age": 3
   >
   > }

2. 数据类型

   > 字符串类型 text、keyword

   > 数值类型 byte、short、integer、long、half float、float、scaled float、double

   > 日期类型 date

   > te布尔类型 boolean

   > 二进制类型 binary

3. 指定字段类型

   > 创建具体的索引规则
   >
   > PUT /test2
   >
   > {
   >
   > ​	"mapping":{
   >
   > ​		"properties":{
   >
   > ​			"name":{
   >
   > ​				"type": "text"
   >
   > ​			},
   >
   > ​			"age":{
   >
   > ​				"type": "long"
   >
   > ​			},
   >
   > ​			"birthday":{
   >
   > ​				"type": "date"
   >
   > ​			}
   >
   > ​		}
   >
   > ​	}
   >
   > }

4. 获取规则

   > GET test2

5. 查看默认的信息

   > PUT /test3/_doc/1
   >
   > {
   >
   > ​	"name": "肉比骨头有骨气",
   >
   > ​	"age": 18,
   >
   > ​	"birth": "1997-01-05"
   >
   > }
   >
   > GET test3

   扩展：通过 GET _cat命令获得es当前很多信息

   > GET _cat/health
   >
   > GET _cat/indices?v

6. 修改索引

   > 曾经的办法，全量修改  跟创建同样的PUT命令，版本号会变化，但是数据不全，之前的数据会丢失

   > POST /test3/_ doc/1/_update
   >
   > {
   >
   > ​	"doc":{
   >
   > ​		"name": "法外狂徒张三"
   >
   > ​	}
   >
   > }

7. 删除索引

   > DELETE test3
   >
   > DELETE /test3/_doc/1

## 关于文档的操作（重）

> 基本操作

1. 添加数据

   > 使用如下命令添加多条数据
   >
   > PUT /luc/user/1
   >
   > {
   >
   > ​	"name":"张三",
   >
   > ​	"age":18,
   >
   > ​	"desc":"法外狂徒",
   >
   > ​	"tags":["交友","旅游","渣男"],
   >
   > }

2. 获取数据

   > GET luc/user/1

3. 更新数据

   > PUT /luc/user/1
   >
   > {
   >
   > ​	"name":"张三222",
   >
   > ​	"age":18,
   >
   > ​	"desc":"法外狂徒",
   >
   > ​	"tags":["交友","旅游","渣男"],
   >
   > }
   >
   > version+result会改变
   >
   > 
   >
   > POST /luc/user/1/<span style="color:red">_update</span>
   >
   > {
   >
   > ​	"doc":{
   >
   > ​		"name": "法外狂徒张三"
   >
   > ​	}
   >
   > }

4. 查询

   > 简单查询name=张三
   >
   > GET luc/user/_search?q=name:张三

   > 复杂查询 推荐使用
   >
   > GET luc/user/_search
   >
   > {
   >
   > ​	"query":{
   >
   > ​		"match":{
   >
   > ​			"name":"张三"		
   >
   > ​		}
   >
   > ​	},
   >
   > ​	"_source":["name","desc"],
   >
   > ​	"sort":[
   >
   > ​		{
   >
   > ​			"age":{
   >
   > ​				"order": "asc"
   >
   > ​			}
   >
   > ​		}
   >
   > ​	],
   >
   > ​	"form":0,
   >
   > ​	"size": 2
   >
   > }
   >
   > _source：结果的过滤
   >
   > sort：排序
   >
   > form：从第几个数据开始
   >
   > size：返回多少条数据（单页面的数据）

   > 多条件查询	
   >
   > "query":{
   >
   > ​		"bool":{
   >
   > ​			"must":[
   >
   > ​				{
   >
   > ​					"match":{
   >
   > ​						"name":"张三"
   >
   > ​					}
   >
   > ​				},
   >
   > ​				{
   >
   > ​					"match":{
   >
   > ​						"age":"3"
   >
   > ​					}
   >
   > ​				}
   >
   > ​			]
   >
   > ​		}
   >
   > ​	}

   >should：相当于sql中的 or条件，满足一个即可
   >
   >"query":{
   >
   >​		"bool":{
   >
   >​			"should":[
   >
   >​				{
   >
   >​					"match":{
   >
   >​						"name":"张三"
   >
   >​					}
   >
   >​				},
   >
   >​				{
   >
   >​					"match":{
   >
   >​						"age":"3"
   >
   >​					}
   >
   >​				}
   >
   >​			]
   >
   >​		}
   >
   >​	}

   > must_not：sql的is not 或者 !=
   >
   > 跟must同级还有个filter，可以进行数据的过滤
   >
   > > gt、gte、lt、lte
   >
   > "filter":{
   >
   > ​	"range":{
   >
   > ​		"age":{
   >
   > ​			"gt": 10,
   >
   > ​			"lte": 20
   >
   > ​		}
   >
   > ​	}
   >
   > }

   > <span style="font-weight:700;color:red;">匹配多个条件</span>
   >
   > **多个条件用空格 隔开 **，满足一个条件就会匹配到，可以根据分支进行判断
   >
   > GET luc/user/_search
   >
   > {
   >
   > ​	"query":{
   >
   > ​		"match":{
   >
   > ​			"tags": "男 技术"
   >
   > ​		}
   >
   > ​	}
   >
   > }

   term查询是直接通过 倒排索引 指定的词条精确的查找

   >"query":{
   >
   >​	"term":{
   >
   >​		"name": "张三"
   >
   >​	}
   >
   >}
   >
   >keyword类型的字段，不会被分词器解析

   关于分词 

   term：直接查询精确的

   match：会使用分词器解析（先分析文档，在进行查询）

5. 高亮查询

   > 跟query同级 highlight
   >
   > "highlight":{
   >
   > ​	"pre_tags": "<p class='key' style='color:red'>",
   >
   > ​	"post_tags": "</p>",
   >
   > ​	"fields":{
   >
   > ​		"name":{}
   >
   > ​	}
   >
   > }
   >
   > 搜索相关的结果会加一个html标签高亮显示

   这些其实mysql也能做，只是效率比较低

   > 匹配

   > 按照条件匹配

   > 精确匹配

   > 区间范围匹配

   > 字段过滤

   > 多条件匹配

   > 高亮查询

   

## 集成springboot



## 爬虫

jsoup简单抓取数据而已

tika爬视频爬音乐

## 搜索高亮