## synchronized实现原理

当一个线程需要访问某段代码时，首选需要获取锁才可以执行同步代码，当退出或者抛出异常时释放锁

普通同步方法：锁是当前实例的对象

静态同步方法：锁是当前类的class对象

同步代码块：锁是括号里面的对象

> 代码块

- 在同步代码前后插入指令 monitorenter、monitorexit

- 任何对象都有一个Monitor对象，当一个 Monitor 被持有之后，他将处于锁定状态

- 线程执行到 `monitorenter` 指令时，会尝试获取对象所对应的 Monitor 所有权，即尝试获取对象的锁

> 同步方法

- 方法会被翻译成普通的方法调用和返回指令：invokevirtual、areturn，jvm层面并没有指令实现synchronized修饰的方法，而是在 Class 文件的方法表中将该方法的 `access_flags` 字段中的 `synchronized` 标志位置设置为 1，表示该方法是同步方法，并使用**调用该方法的对象**或**该方法所属的 Class 在 JVM 的内部对象表示 Klass** 作为锁对象





## 线程

> 线程状态

```java
NEW,

RUNNABLE,

BLOCKED,

WAITING,

TIMED_WAITING,

TERMINATED;
```

> wait/sleep

- 来自不同的类

  wait=>Object

  sleep=>Thread

- 关于锁的释放

  wait释放锁，sleep不释放

- 使用范围不同

  wait必须在同步代码块中；sleep可以在任何地方

## Lock锁

Lock ——>ReentrantLock

默认是非公平锁

公平锁：必须排队

不公平锁：可以插队

> Synchronized 和Lock

- 内置关键字，后者是java类
- 前者无法判断获取锁的状态，Lock锁可以
- 前者自动释放，后者必须手动，如果不释放，会死锁
- 前者如果有一个线程获取锁之后阻塞，则线程2会一直等待，后者不会
- 前者可重入、不可中断，非公平；后者可重入，可判断所，可以设置公平或不公平
- 前者适合锁少量的代码同步问题，后者适合锁大量的同步代码

> 如何判断锁的 是谁

## 生产者 消费者

> 老版

Synchornized关键字

判断等待wait，业务，通知notifyAll

用while条件替代if判断是否等待，来解决虚假唤醒的问题，>2个线程同时操作一个资源类

> juc

Lock

Condition (await  、 singalAll)

> 为何选择Lock锁和Condition

juc实现生产者消费者问题存在问题：随机执行，而我们希望有序的执行

可以创建多个condition，singal指定需要唤醒的condition

ReadWriteLock？？？

## 八锁-即关于锁的八个问题

- 1、

![image-20210223182654291](/Users/luchang/Library/Application Support/typora-user-images/image-20210223182654291.png)

两个方法用的是同一个锁，synchronized锁的是对象phone，两个方法是同一个phone，谁先获取到锁，谁就先执行，所以是先发短信

- 2、

> 如果在资源类增加一个普通非同步方法

那么先执行普通方法，因为没有锁，不用等待 