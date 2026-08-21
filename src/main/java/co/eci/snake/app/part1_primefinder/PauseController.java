package co.eci.snake.app.part1_primefinder;


/**
 * Monitor used to pause or resume the working threads. 
 * The paused state is protected by the lock of this instance. 
 * 
 */
public class PauseController {
    
    private boolean paused = false; 

    public synchronized void pause() {
        paused = true; 
    }

    public synchronized void resume() { 
        paused = false;
        // we need to notify all 3 threads. 
        notifyAll();
    }

    /**
     * If thread is paused, it gets blocked here until resume() wakes it up.
     * In this way, we don't use busy-wait and don't overuse the CPU. 
     * @throws InterruptedException
     */
    public synchronized void checkpoint() throws InterruptedException {
        while (paused) {
            wait(); 
        }
    }



}
