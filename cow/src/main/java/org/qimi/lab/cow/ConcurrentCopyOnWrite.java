package org.qimi.lab.cow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentCopyOnWrite {

    /**
     * TODO 定时器好像是类似单线程阻塞的，如果在定时器内sleep不会阻塞下个时间的任务
     * TODO printf为什么会阻塞，println好像可以并发
     */
    // 测试对象的个数
    private static final int TEST_OBJECT_SIZE = 1000000;
    // 测试使用线程数
    private static final int TEST_THREAD_SIZE = 20;
    // 读线程占总线程数的比例，默认80%
    private static final float TEST_READ_THREAD_SIZE = 0.8f;
    // 写线程占总线程数的比例，默认20%
    private static final float TEST_WRITE_THREAD_SIZE = 0.2f;
    // 读批次间隔时间，单位秒
    private static final int TEST_BATCH_READ_INTERVAL = 1;
    // 写批次间隔时间，单位秒
    private static final int TEST_BATCH_WRITE_INTERVAL = 1;
    // 测试时间，单位秒
    private static final int TEST_TIME = 300;
    // 倒计时闩锁
    private static final CountDownLatch endGate = new CountDownLatch(TEST_TIME);
    // 测试对象缓存列表
    private static List<Element> elements = new CopyOnWriteArrayList<Element>();
    // 读写线程池
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(TEST_THREAD_SIZE);
    // 批次线程池，读一批，写一批
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(2);

    /**
     * 测试方案：
     * 1.启动一个读批次定时器（每隔TEST_BATCH_READ_INTERVAL秒），启动一个写批次定时器（每隔TEST_BATCH_WRITE_INTERVAL秒）
     * 2.每批次读从线程池申请线程，使用多个线程并发读elements。读逻辑是循环最多10000个元素
     * 3.每批次写从线程池申请线程，使用多个线程并发写elements。写逻辑是增加1个元素，由于有并发读，因此会导致cow
     * 4.整个逻辑倒计时TEST_TIME批，每读1批倒计时1次
     * 5.倒计时结束后，关闭线程池
     * @param args
     */
    public static void main(String[] args) {
        initialize();
        start();
        stop();
    }

    private static void initialize() {
        List<Element> list = new ArrayList<Element>();
        for(int i=0;i< TEST_OBJECT_SIZE; i++) {
            list.add(new Element());
        }
        elements.addAll(list);
        System.out.println("success to initialize");
    }

    private static void start() {
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            public void run() {
                int readTaskSize = (int)(TEST_THREAD_SIZE * TEST_READ_THREAD_SIZE);
                for(int i=0;i<readTaskSize;i++) {
                    threadPool.submit(new ReadTask());
                }
                endGate.countDown();
            }
        }, 0, TEST_BATCH_READ_INTERVAL, TimeUnit.SECONDS);

        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            public void run() {
                int writeTaskSize = (int)(TEST_THREAD_SIZE * TEST_WRITE_THREAD_SIZE);
                for(int i=0;i<writeTaskSize;i++) {
                    threadPool.submit(new WriteTask());
                }
            }
        }, 0, TEST_BATCH_WRITE_INTERVAL, TimeUnit.SECONDS);
    }

    private static void stop() {
        try {
            endGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduledThreadPool.shutdown();
        threadPool.shutdown();
    }

    private static class WriteTask implements Runnable{
        public void run() {
            Element element = new Element();
            elements.add(element);
            System.out.println("success to write copyonwrite list, thread:"+ Thread.currentThread().getName());
        }
    }

    private static class ReadTask implements Runnable{
        public void run() {
            int totalSize = elements.size();
            // 最多只循环10000个元素，防止因为循环而导致CPU冲高
            int cycleSize = 10000;
            if(totalSize < 10000) {
                cycleSize = totalSize;
            }
            for(int i=0;i<cycleSize;i++) {
                elements.get(i);
            }
            System.out.println("success to read copyonwrite list, thread:"+Thread.currentThread().getName());
        }
    }

}
