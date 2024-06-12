package com.alexahdp;

import java.math.BigInteger;

public class FactorialThread extends Thread {
    private Long intNumber;
    private BigInteger result = BigInteger.ZERO;
    private boolean isFinished = false;

    public FactorialThread(Long intNumber) {
        this.intNumber = intNumber;
    }

    @Override
    public void run() {
        this.result = factorial(intNumber);
        this.isFinished = true;
    }

    public BigInteger factorial(Long n) {
        BigInteger tmpResult = BigInteger.ONE;
        tmpResult.m
        for (long i = n; i > 0; i--) {
            tmpResult = tmpResult.multiply(BigInteger.valueOf(i));
        }
        return tmpResult;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public BigInteger getResult() {
        return result;
    }
}
