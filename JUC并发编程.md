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

