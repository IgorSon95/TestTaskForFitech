package com.fitechsource.test;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Should be improved to reduce calculation time.
 * <p>
 * Change it or create new one. (max threads count is com.fitechsource.test.TestConsts#MAX_THREADS)
 */
public class Test {
    static AtomicBoolean relevant = new AtomicBoolean(true);

    public static void main(String[] args) throws TestException {
        Set<Double> res = new HashSet<>();

        long startTime = System.currentTimeMillis();

        //calcByIteration(res);
        calcByThreads(res);

        long stopTime = System.currentTimeMillis();

        System.out.println(stopTime - startTime);
        System.out.println(res);

    }

    private static void calcByIteration(Set<Double> res) throws TestException {
        for (int i = 0; i < TestConsts.N; i++) {
            res.addAll(TestCalc.calculate(i));
        }
    }

    private static void calcByThreads(Set<Double> res) {
        Semaphore semaphore = new Semaphore(TestConsts.MAX_THREADS, true);
        List<Thread> allThreads = new ArrayList<>();
        for (int i = 0; i < TestConsts.N; i++) {
            Thread thread = new Thread(new TestThread(semaphore, i, res));
            thread.start();
            allThreads.add(thread);
        }

        //wait for all threads to finish
        allThreads.forEach(it -> {
            try {
                it.join();
            } catch (InterruptedException e) {
                System.out.println("Thread " + it.getName() + " was interrupted");
                e.printStackTrace();
            }
        });
    }
}
