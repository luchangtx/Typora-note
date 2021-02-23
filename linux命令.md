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

