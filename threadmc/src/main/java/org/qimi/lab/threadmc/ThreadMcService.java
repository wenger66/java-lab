package org.qimi.lab.threadmc;

import java.lang.management.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ThreadMcService {

    private Map<Long, Thread> threadMap = new HashMap<Long, Thread>();

    /**
     * ThreadDump主方法
     * @return
     */
    public synchronized String dump() {
        initThreadMap();
        StringBuilder stringBuilder = new StringBuilder();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threads = threadMXBean.dumpAllThreads(true, false);
        printThreads(stringBuilder, threadMXBean, threads);
        clearThreadMap();
        return stringBuilder.toString();
    }

    /**
     * 初始化线程对象的Map，用来获取daemon和priority
     */
    private void initThreadMap() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for(int i = 0; i < count; i++) {
                Thread thread = threads[i];
                threadMap.put(thread.getId(), thread);
            }
            group = group.getParent();
        }
    }

    /**
     * 清理线程对象的Map
     */
    private void clearThreadMap() {
        threadMap.clear();
    }

    /**
     * 转储所有线程，参考visualvm中Jmx的实现
     * @param sb
     * @param threadMXBean
     * @param threads
     */
    private void printThreads(StringBuilder sb, ThreadMXBean threadMXBean, ThreadInfo[] threads) {
        for (ThreadInfo thread : threads) {
            if (thread != null) {
                printThread(sb, threadMXBean, thread);
            }
        }
    }

    /**
     * 转储单个线程
     * @param sb
     * @param threadMXBean
     * @param thread
     */
    private void printThread(StringBuilder sb, ThreadMXBean threadMXBean, ThreadInfo thread) {
        MonitorInfo[] monitors = null;
        if (threadMXBean.isObjectMonitorUsageSupported()) {
            monitors = thread.getLockedMonitors();
        }
//        sb.append("\n\"" + thread.getThreadName() + "\" - Thread t@" + thread.getThreadId() + "\n");
        printThreadBasicInfo(sb, thread);
        sb.append("   java.lang.Thread.State: " + thread.getThreadState());
        sb.append("\n");
        int index = 0;
        for (StackTraceElement st : thread.getStackTrace()) {
            LockInfo lock = thread.getLockInfo();
            String lockOwner = thread.getLockOwnerName();

            sb.append("\tat " + st.toString() + "\n");
            if (index == 0) {
                if (("java.lang.Object".equals(st.getClassName())) && ("wait".equals(st.getMethodName())))
                {
                    if (lock != null)
                    {
                        sb.append("\t- waiting on ");
                        printLock(sb, lock);
                        sb.append("\n");
                    }
                }
                else if (lock != null) {
                    if (lockOwner == null)
                    {
                        sb.append("\t- parking to wait for ");
                        printLock(sb, lock);
                        sb.append("\n");
                    }
                    else
                    {
                        sb.append("\t- waiting to lock ");
                        printLock(sb, lock);
                        sb.append(" owned by \"" + lockOwner + "\" t@" + thread.getLockOwnerId() + "\n");
                    }
                }
            }
            printMonitors(sb, monitors, index);
            index++;
        }
        StringBuilder jnisb = new StringBuilder();
        printMonitors(jnisb, monitors, -1);
        if (jnisb.length() > 0)
        {
            sb.append("   JNI locked monitors:\n");
            sb.append(jnisb);
        }
        if (threadMXBean.isSynchronizerUsageSupported())
        {
            sb.append("\n   Locked ownable synchronizers:");
            LockInfo[] synchronizers = thread.getLockedSynchronizers();
            if ((synchronizers == null) || (synchronizers.length == 0)) {
                sb.append("\n\t- None\n");
            } else {
                for (LockInfo li : synchronizers)
                {
                    sb.append("\n\t- locked ");
                    printLock(sb, li);
                    sb.append("\n");
                }
            }
        }
    }

    /**
     * 打印Monitor信息
     * @param sb
     * @param monitors
     * @param index
     */
    private void printMonitors(StringBuilder sb, MonitorInfo[] monitors, int index) {
        if (monitors != null) {
            for (MonitorInfo mi : monitors) {
                if (mi.getLockedStackDepth() == index)
                {
                    sb.append("\t- locked ");
                    printLock(sb, mi);
                    sb.append("\n");
                }
            }
        }
    }

    /**
     * 打印线程的基本信息，精简实现
     * Hotspot中线程转储中线程信息格式比较丰富，例如"JMX server connection timeout 42" #42 daemon prio=5 os_prio=0 tid=0x000000001a215800 nid=0x350 in Object.wait() [0x000000001ef0e000]
     * os_prio字段只做了linux系统的实现
     * tid字段有说是线程id的，有说是线程地址的
     * nid字段是对应操作系统的线程ID的16进制，比较难于实现
     * 最后中括号内的字段含义不明
     * 以上3个字段比较难于实现，标记为0x
     * 如果不带这些字段，fastthread网站无法解析
     * @param sb
     * @param thread
     * @return
     */
    private void printThreadBasicInfo(StringBuilder sb, ThreadInfo thread) {
        String BASIC_INFO = "\n\"{0}\" #{1} {2} prio={3} os_prio={4} tid={5} nid={6} [{7}]\n";
        Thread t = threadMap.get(thread.getThreadId());
        String name = thread.getThreadName();
        String id = thread.getThreadId()+"";
        String daemon = t!=null?"daemon":"";
        String priority = t!=null?t.getPriority()+"":"5";
        String osPriority = t!=null?5-t.getPriority()+"":"0";
        String tid = "0x";
        String nid = "0x";
        String address = "0x";
        sb.append(MessageFormat.format(BASIC_INFO, name, id, daemon, priority, osPriority, tid, nid, address));
    }

    /**
     * 打印Lock信息
     * @param sb
     * @param lock
     */
    private void printLock(StringBuilder sb, LockInfo lock) {
        String id = Integer.toHexString(lock.getIdentityHashCode());
        String className = lock.getClassName();
        sb.append("<" + id + "> (a " + className + ")");
    }
}
