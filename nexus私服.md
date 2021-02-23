## 部署maven私服

> 下载nexus

https://help.sonatype.com/repomanager3/download/download-archives---repository-manager-3

选择unix版本，新版本有未知坑，可以不选择最新版本的

> 下载的包上传到服务器

```bash
#解压
tar -zxvf nexus-3.29.2-02-unix.tar.gz
#解压之后有两个文件夹
drwxr-xr-x 9 root root      4096 Feb 23 10:18 nexus-3.29.2-02
-rw-r--r-- 1 root root 162406548 Feb 22 18:20 nexus-3.29.2-02-unix.tar.gz
drwxr-xr-x 3 root root        20 Feb 22 18:21 sonatype-work
# 修改启动脚本
vim nexus-3.29.2-02/bin/nexus
# /run_as_root  命令在vim模式下查询关键字，修改true为false，允许root用户登录
# 后台启动
./nexus start
# 有日志启动，关闭即退出服务
./nexus run
# 上述步骤可能会报两个错
```

> <span style="color:red">服务器内存不足</span>

修改nexus所需内存大小

```bash
vim /usr/local/software/nexus/bin/nexus.vmoptions
-Xms256m #修改为256m
-Xmx256m #修改为256m
-XX:MaxDirectMemorySize=512m #修改为512m
-XX:+UnlockDiagnosticVMOptions
-XX:+LogVMOutput
-XX:LogFile=../sonatype-work/nexus3/log/jvm.log
-XX:-OmitStackTraceInFastThrow
-Djava.net.preferIPv4Stack=true
-Dkaraf.home=.
-Dkaraf.base=.
-Dkaraf.etc=etc/karaf
-Djava.util.logging.config.file=etc/karaf/java.util.logging.properties
-Dkaraf.data=../sonatype-work/nexus3
-Dkaraf.log=../sonatype-work/nexus3/log
-Djava.io.tmpdir=../sonatype-work/nexus3/tmp
-Dkaraf.startLocalConsole=false
```

> <span style="color:red">jdk版本不匹配</span>

修改启动脚本，设置启动时所依赖的jdk

```bash
vim /usr/local/software/nexus/bin/nexus
#!/bin/sh
# chkconfig:         2345 75 15
# description:       nexus
### BEGIN INIT INFO
# Provides:          nexus
# Required-Start:    $local_fs $network $remote_fs $syslog $time
# Required-Stop:     $local_fs $network $remote_fs $syslog $time
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: nexus
### END INIT INFO

# Uncomment the following line to override the JVM search sequence
# INSTALL4J_JAVA_HOME_OVERRIDE=
# 设置jdk位置
INSTALL4J_JAVA_HOME_OVERRIDE=/usr/local/software/java/jdk1.8.0_271
# Uncomment the following line to add additional VM parameters
# INSTALL4J_ADD_VM_PARAMS=
```

> 开启启动与服务化

```bash
#创建服务
vim /usr/lib/systemd/system/nexus.service
# 如下写入
[Unit]
Description=Nexus daemon

[Service]
Type=forking
LimitNOFILE=65536
ExecStart=/usr/local/software/nexus/nexus-3.9.0-01/bin/nexus start
ExecStop=/usr/local/software/nexus/nexus-3.9.0-01/bin/nexus stop
User=root #此处为启动用户，根据自己情况设置
Restart=on-abort

[Install]
WantedBy=multi-user.target
```

使用systemctl命令操作nexus

```bash
#使上述服务配置生效
systemctl daemon-reload
# 开机自启动
systemctl enable nexus.service
# 开启服务
systemctl start nexus.service
# 查看启动状态
systemctl status nexus.service
# 重启
systemctl restart nexus.service
# 如果防火墙开启，需要开放nexus端口
firewall-cmd --zone=public --permanent --add-port=8081/tcp
# 重新加载firewall
firewall-cmd --reload 
```

nexus默认端口

```bash
/usr/local/software/nexus/nexus-3.9.0-01/etc/nexus-default.properties

## DO NOT EDIT - CUSTOMIZATIONS BELONG IN $data-dir/etc/nexus.properties
##
# Jetty section
application-port=8081 #默认端口8081
application-host=0.0.0.0
nexus-args=${jetty.etc}/jetty.xml,${jetty.etc}/jetty-http.xml,${jetty.etc}/jetty-requestlog.xml
nexus-context-path=/

# Nexus section
nexus-edition=nexus-pro-edition
nexus-features=\
 nexus-pro-feature

nexus.hazelcast.discovery.isEnabled=true
```

> 访问页面 域名:8081

用户名：admin

```bash
#查看默认密码
cat /usr/local/software/sonatype-work/neuxs3/admin.password
```

登录进去之后，按照步骤提示进行操作，重新设置密码

> 禁用 outreach 功能，这些需要访问国外网站，可能会报错，步骤：  
>
> 1、以管理员身份登陆 Nexus  
>
> 2、打开 Administration ->  Capabilities 。 
>
> 3、右击， Outreach Management 功能，点击 disable 

> 添加阿里云仓库

- 点击Repositories
- 右边点击Create repository
- 在接下来的页面选择“**maven2 proxy**”
- 在具体配置页面取名aliyun-repository
- 配置public-repository
- 将刚刚添加的阿里云仓库排到最前面，确保优先从阿里云拉取依赖

## nginx反向代理nexus

```bash
server { 
listen 80; 
server_name www.maiask.com; #配置的域名
location / {
   proxy_pass  http://127.0.0.1:8081; #跳转到的地址
   proxy_redirect  off; 
   proxy_set_header Host $host; 
   proxy_set_header X-Real-IP $remote_addr; 
   proxy_set_header X-Forwarded-For 
   $proxy_add_x_forwarded_for; 
 }
} 
```

