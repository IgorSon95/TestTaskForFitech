package com.fitechsource.test;

import java.util.Set;
import java.util.concurrent.Semaphore;

public class TestThread implements Runnable {
    private final Set<Double> res;
    private Semaphore testSemaphore;
    private int id;

    TestThread(Semaphore lock, int currentId, Set<Double> res) {
        this.testSemaphore = lock;
        this.id = currentId;
        this.res = res;
    }

    @Override
    public void run() {
        try {
            testSemaphore.acquire();
            System.out.println("Thread with num " + id + " started");
            //check if it is relevant to calculate
            if (Test.relevant.get()) {
                try {
                    Set<Double> calcResult = TestCalc.calculate(id);
                    res.addAll(calcResult);
                    System.out.println("Added " + calcResult.size() + " elements");
                } catch (TestException e) {
                    //if calculation was interrupted, safely set flag to false, to prevent upcoming calculations
                    Test.relevant.compareAndSet(true, false);
                    e.printStackTrace();
                }
            } else  {
                System.out.println("Calculation is unavailable");
                throw new InterruptedException("Calculation threw TestException.");
            }
            testSemaphore.release();
            System.out.println("Thread with num " + id + " stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
