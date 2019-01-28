
## ABA问题
ABA问题是多线程并发的经典问题，用一个例子来说明
![1](1.png)
现有一个用单向链表实现的堆栈，栈顶为A，这时线程T1已经知道A.next为B，然后希望用CAS将栈顶替换为B

    head.compareAndSet(A,B);

在T1执行上面这条指令之前，线程T2介入，pull A、B，push D、C、A，此时堆栈结构如下图，而对象B此时处于游离状态：
![2](2.png)  

此时轮到线程T1执行CAS操作，检测发现栈顶仍为A，所以CAS成功，栈顶变为B，但实际上B.next为null，所以此时的情况如下图
![3](3.png)     
其中堆栈中只有B一个元素，C和D组成的链表不再存在于堆栈中，平白无故就把C、D丢掉了。

## 结果
AtomicInteger会成功执行CAS操作，返回结果true

加上版本戳的AtomicStampedReference对于ABA问题会执行CAS失败，返回结果false