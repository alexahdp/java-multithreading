package com.alexahdp;

public class Example1 {
    static void run() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Hello from new thread");
//                System.out.println("Hello from new thread " + Thread.currentThread().getName());
//                System.out.println("Hello from new thread " + Thread.currentThread().getPriority());
            }
        });
        System.out.println("Hello from main thread" + Thread.currentThread().getName() + " before start");
        thread.setName("Worker");
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println("+++ Uncaught exception from thread " + t.getName() + " " + e);
            }
        });
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
        System.out.println("Hello from main thread" + Thread.currentThread().getName() + " after start");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
