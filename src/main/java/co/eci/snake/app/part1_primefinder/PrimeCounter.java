package co.eci.snake.app.part1_primefinder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Uses an atomic variable to ensure the correct counting of prime numbers foumd. 
 * This helps managing possible race conditions. 
 * 
 */
public class PrimeCounter {

    private final AtomicInteger total = new AtomicInteger(0);


    public void increment() {
        total.incrementAndGet();
    }

public int get() { 
    return total.get();
}
}
