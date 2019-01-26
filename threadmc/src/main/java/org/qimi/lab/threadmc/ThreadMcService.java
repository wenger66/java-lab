package org.qimi.lab.threadmc;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ThreadMcService {

    public static String BASIC_INFO = "\"{0}\" #{1} {2} prio={3} os_prio={4} tid={5} nid={6} {7} [{8}]\n";
    public static String MONITOR_LOCKED = "\\   - locked {0}\n";
    public static String NAME_STATE = "Name: {0}\nState: {1} on {2} owned by: {3}\n";
    public static String NAME_STATE_LOCK_NAME = "Name: {0}\nState: {1} on {2}\n";
    public static String NAME_STATE_LOCK_NAME_LOCK_OWNER = "Name: {0}\nState: {1} on {2} owned by: {3}\n";
    public static String STACK_TRACE = "\nStack trace: \n";
    public static String BLOCKED_COUNT_WAITED_COUNT = "Total blocked: {0}  Total waited: {1}\n";

    private static Map<Long, Thread> threadMap = new HashMap<Long, Thread>();

    static {
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

    private String format(String paramString, Object... paramVarArgs) {
        return MessageFormat.format(paramString, paramVarArgs);
    }

    public static Thread findThread(long threadId) {

        return null;
    }

    private String threadIsDaemon(long threadId) {
        Thread thread = threadMap.get(threadId);
        if(thread!=null && thread.isDaemon()) {
                return "daemon";
        }
        return "";
    }

    private String dumpThreadBasicInfo(ThreadInfo threadInfo) {
        Thread t = threadMap.get(threadInfo.getThreadId());
        String name = threadInfo.getThreadName();
        String id = threadInfo.getThreadId()+"";
        String daemon = t!=null?"daemon":"";
        String priority = t!=null?t.getPriority()+"":"5";
        String osPriority = t!=null?5-t.getPriority()+"":"0";
        String tid = "";
        String nid = "";
        String state = threadInfo.getThreadState().toString();
        String address = "";
        return  format(BASIC_INFO, name, id, daemon, priority, osPriority, tid, nid, state, address);
    }

    /**
     * Java thread priority	Linux nice value
     * 1	4
     * 2	3
     * 3	2
     * 4	1
     * 5	0
     * 6	-1
     * 7	-2
     * 8	-3
     * 9	-4
     * 10	-5
     */


    private String dumpThread(ThreadInfo threadInfo) {
        StringBuilder stringBuilder = new StringBuilder();

        if (threadInfo != null)
        {
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            MonitorInfo[] arrayOfMonitorInfos = null;
            if(threadMXBean.isObjectMonitorUsageSupported()) {
                arrayOfMonitorInfos = threadInfo.getLockedMonitors();
            }
            stringBuilder.append(dumpThreadBasicInfo(threadInfo));

            if (threadInfo.getLockName() == null) {
                stringBuilder.append(format(NAME_STATE, new Object[] {threadInfo
                        .getThreadName(), threadInfo
                        .getThreadState().toString() }));
            } else if (threadInfo.getLockOwnerName() == null) {
                stringBuilder.append(format(NAME_STATE_LOCK_NAME, new Object[] {threadInfo
                        .getThreadName(), threadInfo
                        .getThreadState().toString(), threadInfo
                        .getLockName() }));
            } else {
                stringBuilder.append(format(NAME_STATE_LOCK_NAME_LOCK_OWNER, new Object[] {threadInfo
                        .getThreadName(), threadInfo
                        .getThreadState().toString(), threadInfo
                        .getLockName(), threadInfo
                        .getLockOwnerName() }));
            }
            stringBuilder.append(format(BLOCKED_COUNT_WAITED_COUNT, new Object[] {
                    Long.valueOf(threadInfo.getBlockedCount()),
                    Long.valueOf(threadInfo.getWaitedCount()) }));
            stringBuilder.append(STACK_TRACE);
            int i = 0;
            for (StackTraceElement stackTraceElement : threadInfo.getStackTrace())
            {
                stringBuilder.append(stackTraceElement.toString() + "\n");
                if (arrayOfMonitorInfos != null) {
                    for (MonitorInfo localMonitorInfo : arrayOfMonitorInfos) {
                        if (localMonitorInfo.getLockedStackDepth() == i) {
                            stringBuilder.append(format(MONITOR_LOCKED, new Object[] { localMonitorInfo.toString() }));
                        }
                    }
                }
                i++;
            }
        }
        return stringBuilder.toString();
    }
}
