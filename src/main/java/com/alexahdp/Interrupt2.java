package com.alexahdp;

import java.math.BigInteger;

public class Interrupt2 {
    public static void run() {
        Thread thread = new Thread(new BlockingTask());
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        thread.interrupt();
    }

    private static class BlockingTask implements Runnable {
        @Override
        public void run() {
            BigInteger base = BigInteger.valueOf(2);
            BigInteger exp = BigInteger.valueOf(1000000);
            BigInteger result = pow(base, exp);
            System.out.println(result);
        }

        private BigInteger pow(BigInteger base, BigInteger exp) {
            BigInteger result = BigInteger.ONE;
            for (BigInteger i = BigInteger.ZERO; i.compareTo(exp) != 0; i = i.add(BigInteger.ONE)) {
                result = result.multiply(base);
            }
            return result;
        }
    }
}
