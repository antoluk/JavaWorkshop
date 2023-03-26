package com.example.demo.counter;

public class CounterThread extends Thread {
    @Override
    public void start() {
        Counter.increment();
    }
}
