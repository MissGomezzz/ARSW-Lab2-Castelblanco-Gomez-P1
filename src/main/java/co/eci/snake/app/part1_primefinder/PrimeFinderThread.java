package co.eci.snake.app.part1_primefinder;

import java.util.LinkedList;
import java.util.List;

/**
 * Calculates if number is prime or not and uses the counter to register the amount of prime numbers. 
 */
public class PrimeFinderThread extends Thread {

	private final int a,b;
	private List<Integer> primes;
	private final PauseController pauseController;
    private final PrimeCounter counter; 



	public PrimeFinderThread(int a, int b, PauseController pauseController, PrimeCounter counter) {

        this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
        this.pauseController = pauseController; 
        this.counter = counter; 
	}

        @Override
	public void run(){
        try { 
            for (int i= a;i < b;i++){						
                if (isPrime(i)){
                    primes.add(i);
                    counter.increment();
                }

                pauseController.checkpoint();
            }
	} catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}

	
	boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) { 
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}
	
}

