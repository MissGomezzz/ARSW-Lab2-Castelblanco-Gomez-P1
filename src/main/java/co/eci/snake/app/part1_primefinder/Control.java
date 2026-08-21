package co.eci.snake.app.part1_primefinder;

/**
 * Distributes the ranges of the numbers among the 3 threads
 */
public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 300_000_000;
    private final int NDATA = MAXVALUE / NTHREADS;

    private final PrimeFinderThread[] pft = new PrimeFinderThread[NTHREADS]; 
        public Control(PauseController pauseController, PrimeCounter counter) {
        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            pft[i] = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA, pauseController, counter);
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1, pauseController, counter);
    }
    
    public void startAll() { 
        for (PrimeFinderThread t: pft) { 
            t.start();
        }
    }

    public PrimeFinderThread[] getThreads() {
        return pft; 
    }
}

