package co.eci.snake.core.engine;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import co.eci.snake.core.GameState;

public final class GameClock implements AutoCloseable {
  private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
  private final long periodMillis;
  private final Runnable tick;
  private final AtomicReference<GameState> state = new AtomicReference<>(GameState.STOPPED);
  private final PauseController pauseController;

  public GameClock(long periodMillis, Runnable tick, PauseController pauseController) {
    if (periodMillis <= 0) throw new IllegalArgumentException("periodMillis must be > 0");
    this.periodMillis = periodMillis;
    this.tick = Objects.requireNonNull(tick, "tick");
    this.pauseController = Objects.requireNonNull(pauseController, "pauseController");
  }

  public void start() {
    if (state.compareAndSet(GameState.STOPPED, GameState.RUNNING)) {
      scheduler.scheduleAtFixedRate(() -> {
        if (state.get() == GameState.RUNNING) tick.run();
      }, 0, periodMillis, TimeUnit.MILLISECONDS);
    }
  }

  public void pause()  {
    state.set(GameState.PAUSED);
    pauseController.pause();
  }

  public void resume() {
    state.set(GameState.RUNNING);
    pauseController.resume();
  }

  public void stop()   {
    state.set(GameState.STOPPED);
    pauseController.resume(); // frees the blocked snakes
  }

  public GameState state() {
    return state.get(); 
  }

  @Override public void close() { scheduler.shutdownNow(); }
}