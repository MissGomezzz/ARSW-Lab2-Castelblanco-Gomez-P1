package co.eci.snake.core.engine;

public final class PauseController {
    private boolean paused = false;
    private int activeWorkers = 0;

    public synchronized void pause() {
        paused = true;

        while (activeWorkers > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    public synchronized void awaitIfPaused() throws InterruptedException {
        while (paused) {
            wait();
        }

        activeWorkers++;
    }

    public synchronized void checkpointFinished() {
        activeWorkers--;

        if (activeWorkers == 0) {
            notifyAll();
        }
    }
}