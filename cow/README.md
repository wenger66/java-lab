#COW
##背景
团队遇到一次内存溢出的性能问题，解决这个问题的同时，我们观察到Javadump(OpenJ9)中有多个线程都在使用CopyOnWriteArrayList的add方法

    3XMTHREADINFO      "dw-378 - GET /api/res/v1/subnetworks/06670f87-1e4f-49df-9910-858028199616/fddv3.eutrancellfdds?includeAttr=userLabel,nbiIdDn&queryDn=true" J9VMThread:0x00000000013A0B00, omrthread_t:0x00007F3578004468, java/lang/Thread:0x000000078255C300, state:R, prio=5
    3XMJAVALTHREAD            (java/lang/Thread getId:0x17A, isDaemon:false)
    3XMTHREADINFO1            (native thread ID:0x190, native priority:0x5, native policy:UNKNOWN, vmstate:CW, vm thread flags:0x00001001)
    3XMTHREADINFO2            (native stack address range from:0x00007F36E645F000, to:0x00007F36E649F000, size:0x40000)
    3XMCPUTIME               CPU usage total: 1.016090595 secs, current category="Application"
    3XMHEAPALLOC             Heap bytes allocated since last GC cycle=0 (0x0)
    3XMTHREADINFO3           Java callstack:
    4XESTACKTRACE                at java/util/Arrays.copyOf(Arrays.java:3210(Compiled Code))
    4XESTACKTRACE                at java/util/Arrays.copyOf(Arrays.java:3181(Compiled Code))
    4XESTACKTRACE                at java/util/concurrent/CopyOnWriteArrayList.add(CopyOnWriteArrayList.java:440(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/cache/RmTreeCache.recursiveSubtree2Locations(RmTreeCache.java:453(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/cache/RmTreeCache.recursiveSubtree2Locations(RmTreeCache.java:454(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/cache/RmTreeCache.recursiveSubtree2Locations(RmTreeCache.java:454(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/cache/RmTreeCache.subtree2Locations(RmTreeCache.java:366(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/service/RmService.getDescendantsRid(RmService.java:914(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/api/RmApi.getCommChildrenInResp(RmApi.java:1459(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/api/RmApi.getChildrenInResp(RmApi.java:1416(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/resources/BasicResource.getChildrenEnt(BasicResource.java:298(Compiled Code))
    4XESTACKTRACE                at com/zte/ums/em/rm/resources/RmResource.getChildren(RmResource.java:351(Compiled Code))

CopyOnWriteList的核心存储是Object[]

        private transient volatile Object[] array;    

CopyOnWriteList的add方法简明易懂。获取旧数组(Object[])，复制到一个新数组，新数组元素数量多1个，将新增元素放到新数组上，将CopyOnWriteList
的核心存储指向新数组

        public boolean add(E e) {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                Object[] elements = getArray();
                int len = elements.length;
                Object[] newElements = Arrays.copyOf(elements, len + 1);
                newElements[len] = e;
                setArray(newElements);
                return true;
            } finally {
                lock.unlock();
            }
        }
        
由这个问题引出了几个疑问
* COW在复制新数组时是复制了对象，还是复制了引用？
* COW在容量较大时，并发读写会导致CPU冲高风险吗？
* COW在容量较大时，并发读写会有内存溢出风险吗？
* COW在新增元素时，add方法有没有性能问题？能不能使用？

##实验
核心思路就是制造一个大容量的COW，并发读写
 1.初始化COW容器
 2.一个定时器负责启动读线程，一个定时器负责启动写线程
 3.读定时器调度到时，从线程池申请线程，使用多个线程并发读COW容器。读逻辑是循环最多10000个元素
 4.写定时器调度到时，从线程池申请线程，使用多个线程并发写COW容器。写逻辑是增加1个元素，由于有并发读，因此会导致COW
 5.整体采用倒计时闩锁控制应用的结束
 6.倒计时结束后，关闭线程池
 
 [实验代码](https://github.com/wenger66/java-lab/tree/master/cow)
 
 ##结论
 * COW在写时的复制，新数组引用了老数组中元素，并没有拷贝元素
 
 通过jvisualvm多次观察内存dump的快照，每次都只有一个大数据量的Object[]，还有一些空的父亲是CopyOnWriteList的Object[],
 这些对象就是往COW新增元素留下的还没有被GC回收的旧数组，他们只有占用很小的自身大小的内存，而且很容易被回收

![](./1.png)
![](./2.png) 
