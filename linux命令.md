## firewall

> 打开服务、关闭服务、重启服务、开机启动

```bash
systemctl start firewall
systemctl stop firewall
systemctl restart firewall
systemctl enable firewall
```

> <span style="font-weight:700;color:red;">--zone指定区域，--permanent 没有此参数重启后失效</span>

> 开放端口

```bash
 firewall-cmd --zone=public --add-port=8080/tcp --permanent
```

> 重启firewall

```bash
firewall-cmd --reload  
```

>查看

```bash
 firewall-cmd --zone=public --query-port=8080/tcp
```

>删除端口开放

```bash
firewall-cmd --zone=public --remove-port=8080/tcp --permanent
```



## 服务器攻击维护

> 查看网络连接情况

```bash
netstat -napt
```

> Shodan网站查看异常ip

> 查看端口使用情况

```bash
lsof -i:8080
```



## 内存占用

```bash
# 查看内存占用情况
free -mth
#如果buff/cache占用内存较多，清楚缓存即可
#查看缓存情况
cat /proc/sys/vm/drop_caches
#上述命令结果可能是0
#表示清除pagecache。
echo 1 > /proc/sys/vm/drop_caches 
#表示清除回收slab分配器中的对象（包括目录项缓存和inode缓存）。slab分配器是内核中管理内存的一种机制，其中很多缓存数据实现都是用的pagecache。
echo 2 > /proc/sys/vm/drop_caches 
#表示清除pagecache和slab分配器中的缓存对象。
echo 3 > /proc/sys/vm/drop_caches 
```

