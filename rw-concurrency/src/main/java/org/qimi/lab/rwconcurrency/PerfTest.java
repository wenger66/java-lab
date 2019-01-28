package org.qimi.lab.rwconcurrency;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PerfTest
{
    private static final long TEST_COOL_OFF_MS = 10;
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static final Spaceship[] SPACESHIPS =
        {
            new SynchronizedSpaceship(),
            new ReadWriteLockSpaceShip(),
            new ReentrantLockSpaceship(),
            new StampedLockSpaceship(),
            new StampedLockWithRetriesSpaceship(),
            new LockFreeSpaceship(),
        };
    // 写线程个数
    private static int NUM_WRITERS;
    // 读线程个数
    private static int NUM_READERS;
    // 测试时间
    private static long TEST_DURATION_MS;

    public static void main(final String[] args) throws Exception
    {
        NUM_READERS = Integer.parseInt(args[0]);
        NUM_WRITERS = Integer.parseInt(args[1]);
        TEST_DURATION_MS = Long.parseLong(args[2]);

        // 运行5次，求平均值
        for (int i = 0; i < 5; i++)
        {
            System.out.println("*** Run - " + i);
            for (final Spaceship spaceship : SPACESHIPS)
            {
                System.gc();
                Thread.sleep(TEST_COOL_OFF_MS);

                perfRun(spaceship);
            }
        }

        EXECUTOR.shutdown();
    }

    private static void perfRun(final Spaceship spaceship) throws Exception
    {
        final Results results = new Results();
        // 开始关卡，给定数量的线程集中的关卡点，Barrier等待的是线程，Latch等待的是事件
        final CyclicBarrier startBarrier = new CyclicBarrier(NUM_READERS + NUM_WRITERS + 1);
        // 结束闩锁，每个线程处理完就CountDown
        final CountDownLatch finishLatch = new CountDownLatch(NUM_READERS + NUM_WRITERS);
        // 统一的结束无限循环的标记，保证每个线程一起可见
        final AtomicBoolean runningFlag = new AtomicBoolean(true);

        for (int i = 0; i < NUM_WRITERS; i++)
        {
            EXECUTOR.execute(new WriterRunner(i, results, spaceship, runningFlag, startBarrier, finishLatch));
        }

        for (int i = 0; i < NUM_READERS; i++)
        {
            EXECUTOR.execute(new ReaderRunner(i, results, spaceship, runningFlag, startBarrier, finishLatch));
        }

        awaitBarrier(startBarrier);
        // 此时开始关卡被突破，所有读写线程开始运行，运行TEST_DURATION_MS时间后，停止读写的测试
        Thread.sleep(TEST_DURATION_MS);
        runningFlag.set(false);

        finishLatch.await();

        System.out.format(
            "%d readers %d writers %22s %s%n",
            NUM_READERS, NUM_WRITERS,
            spaceship.getClass().getSimpleName(),
            results);
    }

    private static void awaitBarrier(final CyclicBarrier barrier)
    {
        try
        {
            barrier.await();
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    static class Results
    {
        final long[] reads = new long[NUM_READERS];
        final long[] moves = new long[NUM_WRITERS];

        final long[] readAttempts = new long[NUM_READERS];
        final long[] observedMoves = new long[NUM_READERS];
        final long[] moveAttempts = new long[NUM_WRITERS];

        public String toString()
        {
            long totalReads = 0;
            for (final long v : reads)
            {
                totalReads += v;
            }
            final String readsSummary = String.format("%,d:", totalReads);

            long totalMoves = 0;
            for (final long v : moves)
            {
                totalMoves += v;
            }
            final String movesSummary = String.format("%,d:", totalMoves);

            return
                "reads=" + readsSummary + Arrays.toString(reads) +
                " moves=" + movesSummary + Arrays.toString(moves) +
                " readAttempts=" + Arrays.toString(readAttempts) +
                " moveAttempts=" + Arrays.toString(moveAttempts) +
                " observedMoves=" + Arrays.toString(observedMoves);
        }
    }

    static class WriterRunner implements Runnable
    {
        private final int id;
        private final Results results;
        private final Spaceship spaceship;
        private final AtomicBoolean runningFlag;
        private final CyclicBarrier barrier;
        private final CountDownLatch latch;

        WriterRunner(
            final int id,
            final Results results,
            final Spaceship spaceship,
            final AtomicBoolean runningFlag,
            final CyclicBarrier barrier,
            final CountDownLatch latch)
        {
            this.id = id;
            this.results = results;
            this.spaceship = spaceship;
            this.runningFlag = runningFlag;
            this.barrier = barrier;
            this.latch = latch;
        }

        public void run()
        {
            awaitBarrier(barrier);

            long movesCounter = 0;
            long movedAttemptsCount = 0;

            while (runningFlag.get())
            {
                movedAttemptsCount += spaceship.move(1, 1);

                ++movesCounter;
            }

            results.moveAttempts[id] = movedAttemptsCount;
            results.moves[id] = movesCounter;

            latch.countDown();
        }
    }

    static class ReaderRunner implements Runnable
    {
        private final int id;
        private final Results results;
        private final Spaceship spaceship;
        private final AtomicBoolean runningFlag;
        private final CyclicBarrier barrier;
        private final CountDownLatch latch;

        ReaderRunner(
            final int id,
            final Results results,
            final Spaceship spaceship,
            final AtomicBoolean runningFlag,
            final CyclicBarrier barrier,
            final CountDownLatch latch)
        {
            this.id = id;
            this.results = results;
            this.spaceship = spaceship;
            this.runningFlag = runningFlag;
            this.barrier = barrier;
            this.latch = latch;
        }

        public void run()
        {
            awaitBarrier(barrier);

            int[] currentCoordinates = new int[]{0, 0};
            int[] lastCoordinates = new int[]{0, 0};

            long readsCount = 0;
            long readAttemptsCount = 0;
            long observedMoves = 0;

            while (runningFlag.get())
            {
                readAttemptsCount += spaceship.readPosition(currentCoordinates);

                if (lastCoordinates[0] != currentCoordinates[0] ||
                    lastCoordinates[1] != currentCoordinates[1])
                {
                    ++observedMoves;
                    lastCoordinates[0] = currentCoordinates[0];
                    lastCoordinates[1] = currentCoordinates[1];
                }

                ++readsCount;
            }

            results.reads[id] = readsCount;
            results.readAttempts[id] = readAttemptsCount;
            results.observedMoves[id] = observedMoves;

            latch.countDown();
        }
    }
}
