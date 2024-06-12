package com.alexahdp;

public class Interrupt {
    public static void run() {
        Thread thread = new Thread(new BlockingTasks());
        thread.start();
        thread.interrupt();
    }

    private static class BlockingTasks implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        }
    }
}
