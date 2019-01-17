package org.qimi.lab.dominator;

import java.util.UUID;
import java.util.concurrent.*;

public class DumpOfConcurrentHashMap {
    // 测试元素的数量
    private static final int TEST_SIZE = 10000;
    // 测试的缓存
    private static ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();
    private static String[] array = new String[TEST_SIZE];
    // 测试时间，单位秒
    private static final int TEST_TIME = 300;
    // 倒计时闩锁
    private static final CountDownLatch endGate = new CountDownLatch(TEST_TIME);
    // 定时线程池
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    // 倒计时间隔，单位秒
    private static final int TEST_COUNTDOWN_INTERVAL = 1;

    public static void main(String[] args) {
        initialize();
        stop();
    }

    private static void initialize() {
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            public void run() {
                endGate.countDown();
                System.out.println("count down:" + endGate.getCount());
            }
        }, 0, TEST_COUNTDOWN_INTERVAL, TimeUnit.SECONDS);

        for(int i=0;i<TEST_SIZE;i++) {
            map.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            array[i] = UUID.randomUUID().toString();
        }
    }

    private static void stop() {
        try {
            endGate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduledThreadPool.shutdown();
    }
}
