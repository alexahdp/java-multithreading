package com.alexahdp;

public class Example2 {
    public static void run() {
        Thread thread = new NewThread();
        thread.start();
    }

    private static class NewThread extends Thread {
        @Override
        public void run() {
            System.out.println("Hello from new thread " + Thread.currentThread().getName());
            System.out.println("Hello from new thread " + Thread.currentThread().getPriority());
        }
    }
}
