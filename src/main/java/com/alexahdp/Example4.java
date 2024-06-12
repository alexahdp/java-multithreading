package com.alexahdp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Example4 {

    public static void run() {
        List<Long> inputNumbers = Arrays.asList(0L, 2354L, 12344L, 24241L);
        List<FactorialThread> threads = new ArrayList<>();
        for (long inputNumber : inputNumbers) {
            threads.add(new FactorialThread(inputNumber));
        }
        for (Thread thread : threads) {
            thread.setDaemon(true);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                // because calc may take a long time, kill the thread when the main thread exits

                // param - timeout in milliseconds
                thread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        for (int i = 0; i < inputNumbers.size(); i++) {
            FactorialThread thread = threads.get(i);
            if (thread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + thread.getResult());
            } else {
                System.out.println("The calculation for " + inputNumbers.get(i) + " is still in progress");
            }
        }
    }
}
