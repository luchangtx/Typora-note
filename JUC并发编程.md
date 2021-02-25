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

## ArrayList线程不安全的解决方案

- Vector Synchronized同步方法实现

- synchronizedList 集合工具类Collections.synchronizedList(list)转换为同步方法

- CopyOnWrite，写入时复制，cow计算机程序设计领域的一种优化策略

  多线程调用list时，读取是固定的，写入时避免覆盖，造成数据问题

<span style="color:red">set的本质是hashMap，map的key不可重复，所以set是不可重复的，map的值存的是一个不变的值</span>

 ## Map

加载因子、初始容量

默认 0.75 、16（使用的位运算 1<<4）

## Callable

1、FutureTask的get方法可能会产生阻塞，把他放到最后，或者使用异步通信 

2、结果有缓存，两个线程，只会打印一次返回值

## 常用辅助类

CountDownLatch  计数器（减，比如倒计时，学校门卫关门必须等所有人离校）

```java
countDownLatch.countDown();//调用会减一
countDownLatch.await();//等待计数器归零，然后再向下执行；每次计数器变为0，就会被唤醒，执行
```

CyclicBarrier 加法计数器（七龙珠召唤神龙）

```java

```

Semaphore 信号量（停车位），限流的时候可以用

多个共享资源互斥使用，并发限流，控制最大线程数

```java
acquire()   //得到，如果已经满了，等待，知道被释放
release()		//释放，会将当前信号量+1，然后唤醒等待线程
```

## 读写锁

ReadWriteLock  读读（共享锁）  独占锁（写锁）

读可以多个线程同时读，写只能一个线程写 ，更细粒度加锁

## 阻塞队列 BlockingQueue

队列满了，就必须阻塞等待

队列是空的，必须阻塞等待生产，无法读取

多线程并发处理、线程池会使用到阻塞队列

<span style="color:red;font-weight:700">四组API</span>

抛出异常

- add //增加元素
- remove //移除元素
- blockingQueue.element() //查看队首元素

返回值

- offer //增加元素  boolean
- poll //移除元素 null
- blockingQueue.peek() //查看队首元素

等待、阻塞

- blockingQueue.put(x);//存元素  一直阻塞
- blockingQueue.take();//取元素  一直阻塞

超时退出

- blockingQueue.offer("xx",2,TimeUnit.SECONDS); //等待超过两秒就退出
- blockingQueue.poll("xx",2,TimeUnit.SECONDS);//等待超过两秒就退出

> SynchronizedQueue 同步队列

没有容量，进去一个元素，必须等取出来之后才可以放另一个元素

## 线程池

> 池化技术：事先准备好一些资源

线程池、连接池、内存池、对象池

线程池三大方法、七大参数、4种拒绝策略

> 三大方法

```java
				//单个线程
        ExecutorService threadPool=Executors.newSingleThreadExecutor();
        //固定线程池大小
        Executors.newFixedThreadPool(5);
        //可伸缩
        Executors.newCachedThreadPool();
        
        try {
            for (int i = 0; i < 10; i++) {
                threadPool.execute(()->{
                    System.err.println(Thread.currentThread().getName()+" OK");
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //线程池用完,关闭
            threadPool.shutdown();
        }
```

![image-20210224154735383](/Users/luchang/Library/Application Support/typora-user-images/image-20210224154735383.png)

![image-20210224155022432](/Users/luchang/Library/Application Support/typora-user-images/image-20210224155022432.png)

```java
public static void main(String[] args) {
        //最大的承载数量为 最大线程数+阻塞队列长度=5+3=8,即当第九个线程执行时会抛出异常
        ExecutorService threadPool=new ThreadPoolExecutor(
                2,//核心线程数
                5,//最大线程数
                3,//无连接超时断开
                TimeUnit.SECONDS,
                //阻塞队列
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                //默认的拒绝策略,如果满了,还有人进来,不处理这个人的,抛出异常
                new ThreadPoolExecutor.AbortPolicy());
        try {
            for (int i = 0; i < 5; i++) {
                threadPool.execute(()->{
                    System.err.println(Thread.currentThread().getName()+" OK");
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            threadPool.shutdown();
        }
    }
/**
 * 拒绝策略
 * AbortPolicy：抛出异常
 * CallerRunsPolicy：谁创建的返回给谁执行
 * DiscardPolicy：丢掉任务
 * DiscardOldestPolicy：会尝试跟第一个执行的竞争，如果失败丢掉任务
 */
```

最大线程到底该如何定义？？？

- CPU密集型：几核就定义为几，保证CPU效率最高

- IO密集型：IO占用资源，判断程序中十分耗IO的线程数，大于它即可

## 四大函数式接口

- Lambda表达式
- 链式变成
- <span style="color:red">函数式接口</span>：可以用Lambda简化
- stream流

> 函数式接口：只有一个方法的接口，简化编程，在新版本的框架底层大量应用

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

```java
//函数型接口：Function<T,R> 传入参数T，返回R 
				Function<String, String> function = new Function<String, String>() {
            @Override
            public String apply(String o) {
                return o;
            }
        };
//断定型接口：Predicate<T> 传入参数T，返回boolean
        Predicate<String> predicate = new Predicate<String>() {
            @Override
            public boolean test(String o) {
                return false;
            }
        };
//消费型接口：Consumer<T> 传入参数T
        Consumer<String> consumer=new Consumer<String>() {
            @Override
            public void accept(String s) {
                
            }
        };
//供给型接口：Supplier<T> 没有参数，只要返回值
        Supplier<String> supplier=new Supplier<String>() {
            @Override
            public String get() {
                return null;
            }
        };
```

## Stream流式计算

## ForkJoin

> 并行执行任务，提高效率
>
> 工作窃取：某个线程执行完毕，会窃取其他线程的任务进行执行

ForkJoinPool

ForkJoinTask ——> RecursiveTask（递归）

fork：将线程压入队列

join：获取值

## 异步回调Future

```
CompletableFuture.runAsync() //无返回值
CompletableFuture.supplyAsync(); //有返回值
```

![image-20210224180809175](/Users/luchang/Library/Application Support/typora-user-images/image-20210224180809175.png)

## JMM

关于JMM的约定

- 线程解锁前，必须把共享变量立刻刷回主存

- 线程加锁前，必须读取主存中的最新值到工作内存中

- 加锁解锁是同一把锁

线程、工作内存、

八个操作：（必须成对出现）

- lock、unlock

- 读取主存（read）、加载到工作内存（load）

- 执行引擎使用（use）、执行完返回给工作内存（assign）

- 作用在主存，保存（store）、写入（write）

![image-20210224181713686](/Users/luchang/Library/Application Support/typora-user-images/image-20210224181713686.png)

线程B修改值，线程A没有及时改变，即程序不知道主内存的值已经发生了变化

## Volatile

Volatile：java虚拟机提供的轻量的同步机制

- 保证可见性 ：如何保持可见性
  - JMM：java内存模型（概念、约定）
- 不保证原子性
- 禁止指令重排

> 1、保证可见性



> 2、不保证原子性

原子性：不可分割，同时成功，同时失败

![image-20210225102104519](/Users/luchang/Library/Application Support/typora-user-images/image-20210225102104519.png)

不能保证计算结果是20000，那如何保证原子性呢？？？

可以使用javap 命令查看编译之后的字节码文件，可以看出 num++ 分为三个步骤

- 获取值、执行+操作、写回值 可以看出并不是原子操作

我们应该使用原子类，来解决原子性问题

比如int 换为 AtomicInteger num=new AtomicInteger();

num++ 换为 num.getAndIncrement();  这个方法的底层调用native方法，依靠cpu的CAS算法

> 这些类的底层都直接和操作系统挂钩，在内存中修改值，Unsafe类是一个特殊的存在

> 3、指令重排

指令重排：你写的程序，计算机并不是按照你写的那样去执行

![image-20210225103912482](/Users/luchang/Library/Application Support/typora-user-images/image-20210225103912482.png)

![image-20210225104221939](/Users/luchang/Library/Application Support/typora-user-images/image-20210225104221939.png)

内存屏障，CPU指令，作用：

- 保证特定的操作的执行顺序
- 保证某些变量的内存可见性

![image-20210225104424167](/Users/luchang/Library/Application Support/typora-user-images/image-20210225104424167.png)

总结：Volatile可以保证可见性，不能保证原子性，由于内存屏障可以保证避免指令重排现象

<span style="color:red">在单例模式中使用较多</span>

## 单例模式

> 饿汉式

```java
/**
 * @author luchang on 2021/2/25 10:47 上午
 * @description 饿汉式单例
 * 由于一上来就创建占用空间,浪费资源
 */
public class Hungry {
    private Hungry() {

    }
    private final static Hungry HUNGRY = new Hungry();
    public static Hungry getInstance(){
        return HUNGRY;
    }
}
```

> 懒汉式

```java
package com.luc.demo.singleton;

import java.lang.reflect.Constructor;

/**
 * @author luchang on 2021/2/25 10:51 上午
 * @description 懒汉式
 */
public class LazyMan {
    private LazyMan() {
    }

    private volatile static LazyMan lazyMan;

    /**
     * 单线程下ok,但多线程下不安全
     *
     * @return LazyMan
     */
    public static LazyMan getInstance() {
        if (lazyMan == null) {
            lazyMan = new LazyMan();
        }
        return lazyMan;
    }

    /**
     * DCL(double check lock双重锁)
     *
     * @return LazyMan
     */
    public static LazyMan getInstanceSync() {
        if (lazyMan == null) {
            synchronized (LazyMan.class) {
                if (lazyMan == null) {
                    lazyMan = new LazyMan();
                    /**
                     * new LazyMan()并非原子操作
                     * 1.分配内存空间
                     * 2.执行构造方法,初始化对象
                     * 3.把这个对象指向这个空间
                     *
                     * 我们期待执行顺序是123,但由于指令重排,可能出现132,
                     * 导致在未执行构造的情况下就指向了内存空间,让其他线程误认为lazyMan不为空
                     * 所需需要加volatile
                     */
                }
            }
        }
        return lazyMan;
    }


    /**
     * 反射会破坏这种单例
     * 1.可以在构造方法中加锁判断如果不为null,抛出异常,不允许再次进行实例化
     *      如果都是通过反射进行创建,根本没有创建lazyMan对象,依然可以用反射破坏单例
     * 2.增加私有字段做标志位在构造中加判断
     *      依然可以反编译拿到标志位字段名,利用反射进行破坏
     * 3.枚举类
     *      查看Constructor类的newInstance方法源码得知如果是枚举类,则不允许使用反射进行实例化对象
     *
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args) throws Exception {
        LazyMan instance = LazyMan.getInstance();
        Constructor<LazyMan> constructor = LazyMan.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        LazyMan instance2 = constructor.newInstance();
        //会发现是两个不同的对象,破坏了单例
        System.err.println(instance);
        System.err.println(instance2);
    }
}
```

> 静态内部类

```java
package com.luc.demo.singleton;

/**
 * @author luchang on 2021/2/25 11:04 上午
 * @description 自动生成
 */
public class Holder {
    public Holder() {
    }

    public static class InnerClass{
        private static final Holder HOLDER =new Holder();
    }

    public static Holder getInstance(){
        return InnerClass.HOLDER;
    }
}
```

<span style="color:red">上述单例模式都不安全</span>

> 枚举

```java
package com.luc.demo.singleton;

/**
 * @author luchang on 2021/2/25 11:29 上午
 * @description 枚举单例, 本身也是一个类
 */
public enum EnumSingle {
    INSTANCE;

    public EnumSingle getInstance() {
        return INSTANCE;
    }
}
```

尝试反射破坏枚举单例，用javap 查看字节码文件，发现有空参构造，尝试反射破解无果

用jad反编译得到有一个有参构造器，尝试利用反射破坏，提示枚举无法用反射实例化对象

## 深入理解CAS

![image-20210225135539107](/Users/luchang/Library/Application Support/typora-user-images/image-20210225135539107.png)

![image-20210225135653471](/Users/luchang/Library/Application Support/typora-user-images/image-20210225135653471.png)

![image-20210225135745263](/Users/luchang/Library/Application Support/typora-user-images/image-20210225135745263.png)

![image-20210225135757170](/Users/luchang/Library/Application Support/typora-user-images/image-20210225135757170.png)

CAS总结：比较当前工作内存中的值和主存的值，如果是期望值，执行操作，否则一直循环（自旋锁）

缺点：

- 循环耗时
- 一次只能保证一个共享变量的原子性
- 会出现ABA问题

> CAS的ABA 问题

![image-20210225140216120](/Users/luchang/Library/Application Support/typora-user-images/image-20210225140216120.png)

## 原子引用

AtomicReference ——> AtomicStampedReference 初始值+时间戳两个参数

![image-20210225140954248](/Users/luchang/Library/Application Support/typora-user-images/image-20210225140954248.png)

![image-20210225141723020](/Users/luchang/Library/Application Support/typora-user-images/image-20210225141723020.png)

Integer使用了对象缓存机制，默认范围是 -128~127，推荐使用静态工厂方法valueof获取对象实例，而不是new，因为valueOf使用缓存，而new一定会创建新的对象分配新的内存空间

如果泛型是包装类，注意对象的引用问题，将上述例子中的2020改到-128~127 范围内，操作的才是同一个对象

## 各种锁

- 公平锁
  - 非常公平，不能插队，先来限制性
- 非公平锁
  - 非常不公平，可以插队（默认的）
  - ![image-20210225142057049](/Users/luchang/Library/Application Support/typora-user-images/image-20210225142057049.png)
- 可重入锁
  - 拿到了外面的锁，就可以拿到里面的锁

![image-20210225143957036](/Users/luchang/Library/Application Support/typora-user-images/image-20210225143957036.png)

> lock版

![image-20210225144045143](/Users/luchang/Library/Application Support/typora-user-images/image-20210225144045143.png)

- 自旋锁
  - spinlock，不断循环，直到执行
  - ![image-20210225144418375](/Users/luchang/Library/Application Support/typora-user-images/image-20210225144418375.png)
- 死锁
  - 两个线程已经各自占用自己的锁，有都在抢占对方的锁

解决问题：

1、查看日志 

2、jps定位查看堆栈信息

- 命令行进入代码目录

- 输入命令查看进程号：jps -l
- 使用进程号获取堆栈信息： jstack 11444 （进程号）

```java
public class DeadLock {
    public static void main(String[] args) {
        String lockA = "lockA";
        String lockB = "lockB";
        new Thread(new MyLock(lockA, lockB), "T1").start();
        new Thread(new MyLock(lockB, lockA), "T2").start();
    }
    static class MyLock implements Runnable {
        private final String lockA;
        private final String lockB;
        public MyLock(String lockA, String lockB) {
            this.lockA = lockA;
            this.lockB = lockB;
        }
        @Override
        public void run() {
            synchronized (lockA) {
                System.err.println(Thread.currentThread().getName() + "获取到锁" + lockA);
                //休息2秒,保证两个线程都能获取到各自的锁
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println(Thread.currentThread().getName() + "尝试获取锁" + lockB);
                synchronized (lockB) {
                    System.err.println(Thread.currentThread().getName() + "尝试获取锁" + lockB);
                }
            }
        }
    }
}
```

