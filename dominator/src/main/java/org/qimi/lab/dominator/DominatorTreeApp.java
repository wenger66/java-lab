package org.qimi.lab.dominator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DominatorTreeApp {

    // 测试时间，单位秒
    private static final int TEST_TIME = 300;
    // 倒计时闩锁
    private static final CountDownLatch endGate = new CountDownLatch(TEST_TIME);
    // 定时线程池
    private static final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    // 倒计时间隔，单位秒
    private static final int TEST_COUNTDOWN_INTERVAL = 1;
    // 支配树缓存
    private static DominatorTree tree ;

    public static void main(String[] args) {
        initialize();
        stop();
    }

    private static void initialize() {
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            public void run() {
                endGate.countDown();
                System.out.println("count down:"+endGate.getCount());
            }
        }, 0, TEST_COUNTDOWN_INTERVAL, TimeUnit.SECONDS);

        tree = new DominatorTree();
        A a = new A();
        B b = new B();
        C c = new C();
        D d = new D();
        E e = new E();
        F f = new F();
        G g = new G();
        H h = new H();
        a.setC(c);
        b.setC(c);
        c.setD(d);
        c.setE(e);
        d.setF(f);
        e.setG(g);
        f.setD(d);
        f.setH(h);
        g.setH(h);
        tree.setA(a);
        tree.setB(b);
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
