package org.qimi.lab.threadmc;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadMcService {

    /**
     * "RMI TCP Connection(2)-10.43.91.153" #38 daemon prio=5 os_prio=0 tid=0x000000001a150800 nid=0x2954 runnable [0x000000001e7ee000]
     *    java.lang.Thread.State: RUNNABLE
     * 	at java.net.SocketInputStream.socketRead0(Native Method)
     * 	at java.net.SocketInputStream.socketRead(SocketInputStream.java:116)
     * 	at java.net.SocketInputStream.read(SocketInputStream.java:170)
     * 	at java.net.SocketInputStream.read(SocketInputStream.java:141)
     * 	at java.io.BufferedInputStream.fill(BufferedInputStream.java:246)
     * 	at java.io.BufferedInputStream.read(BufferedInputStream.java:265)
     * 	- locked <0x00000000edb203b8> (a java.io.BufferedInputStream)
     * @return
     */
    public synchronized String dump() {
        StringBuilder stringBuilder = new StringBuilder();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] arrayOfThreadInfo = threadMXBean.dumpAllThreads(true, false);
        for (ThreadInfo threadInfo : arrayOfThreadInfo) {
            stringBuilder.append(dumpThread(threadInfo));
        }
        return stringBuilder.toString();
    }

    private String dumpThread(ThreadInfo threadInfo) {
        StringBuilder stringBuilder = new StringBuilder();

        if (threadInfo != null)
        {
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            MonitorInfo[] arrayOfMonitorInfos = null;
            if(threadMXBean.isObjectMonitorUsageSupported()) {
                arrayOfMonitorInfos = threadInfo.getLockedMonitors();
            }
            if (threadInfo.getLockName() == null) {
                stringBuilder.append(Resources.format(Messages.NAME_STATE, new Object[] {threadInfo
                        .getThreadName(), threadInfo
                        .getThreadState().toString() }));
            } else if (threadInfo.getLockOwnerName() == null) {
                stringBuilder.append(Resources.format(Messages.NAME_STATE_LOCK_NAME, new Object[] {threadInfo
                        .getThreadName(), threadInfo
                        .getThreadState().toString(), threadInfo
                        .getLockName() }));
            } else {
                stringBuilder.append(Resources.format(Messages.NAME_STATE_LOCK_NAME_LOCK_OWNER, new Object[] {threadInfo
                        .getThreadName(), threadInfo
                        .getThreadState().toString(), threadInfo
                        .getLockName(), threadInfo
                        .getLockOwnerName() }));
            }
            stringBuilder.append(Resources.format(Messages.BLOCKED_COUNT_WAITED_COUNT, new Object[] {
                    Long.valueOf(threadInfo.getBlockedCount()),
                    Long.valueOf(threadInfo.getWaitedCount()) }));
            stringBuilder.append(Messages.STACK_TRACE);
            int i = 0;
            for (StackTraceElement stackTraceElement : threadInfo.getStackTrace())
            {
                stringBuilder.append(stackTraceElement.toString() + "\n");
                if (arrayOfMonitorInfos != null) {
                    for (MonitorInfo localMonitorInfo : arrayOfMonitorInfos) {
                        if (localMonitorInfo.getLockedStackDepth() == i) {
                            stringBuilder.append(Resources.format(Messages.MONITOR_LOCKED, new Object[] { localMonitorInfo.toString() }));
                        }
                    }
                }
                i++;
            }
        }
        return stringBuilder.toString();
    }
}
