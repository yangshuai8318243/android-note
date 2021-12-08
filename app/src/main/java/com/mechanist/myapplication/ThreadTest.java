package com.mechanist.myapplication;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadTest {
    private static final String TAG = "ThreadTest";

    private void test() {
        final int totalThread = 10;
        final CountDownLatch countDownLatch = new CountDownLatch(totalThread);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < totalThread; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.print("run..");
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("end");
        executorService.shutdown();
    }

    private int index = 0;
    private int max = 10;

    private final Object lockObj = new Object();

    public void print1_1Or2() {

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lockObj) {
                    while (index < max) {
                        if (index % 2 == 0) {
                            Log.i(TAG, Thread.currentThread().getName() + "===>" + index);
                            index++;
                            try {
                                lockObj.notifyAll();
                                lockObj.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            lockObj.notifyAll();
                        }

                    }
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lockObj) {
                    while (index < max) {
                        if (index % 2 != 0) {
                            Log.i(TAG, Thread.currentThread().getName() + "===>" + index);
                            index++;
                            try {
                                lockObj.notifyAll();
                                lockObj.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            lockObj.notifyAll();
                        }
                    }
                }
            }
        });

        thread1.setName("thread1");
        thread2.setName("thread2");
        thread1.start();
        thread2.start();
    }

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void print2_1Or2() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                while (index < max) {
                    if (index % 2 == 0) {
                        Log.i(TAG, Thread.currentThread().getName() + "===>" + index);
                        index++;
                        try {
                            condition.signalAll();
                            condition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        condition.signalAll();
                    }
                }
                lock.unlock();

            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                while (index < max) {
                    if (index % 2 != 0) {
                        Log.i(TAG, Thread.currentThread().getName() + "===>" + index);
                        index++;
                        try {
                            condition.signalAll();
                            condition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        condition.signalAll();
                    }
                }
                lock.unlock();
            }
        });

        thread1.start();
        thread2.start();
    }

    private Thread join1;
    private Thread join2;

    public void print3_1Or2() {

        join1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (index < max) {
                    if (index % 2 == 0) {
                        Log.i(TAG, Thread.currentThread().getName() + "===>" + index);
                        index++;
                        try {
                            join2.start();
                            join2.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        join2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (index < max) {
                    if (index % 2 != 0) {
                        Log.i(TAG, Thread.currentThread().getName() + "===>" + index);
                        index++;
                        try {
                            join1.start();
                            join1.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        join1.start();
        join2.start();
    }


}
