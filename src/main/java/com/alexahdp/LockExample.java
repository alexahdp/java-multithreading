package com.alexahdp;

public class LockExample {
    private static Integer counter = 0;
    private static Object lock = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            incrementCounter();
        });
        Thread t2 = new Thread(() -> {
            decrementCounter();
        });
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(counter);
    }

    private static void incrementCounter() {
        for (int i = 0; i < 1000; i++) {
            synchronized (lock) {
                counter++;
            }
        }
    }

    private static void decrementCounter() {
        for (int i = 0; i < 1000; i++) {
            synchronized (lock) {
                counter--;
            }
        }
    }
}
