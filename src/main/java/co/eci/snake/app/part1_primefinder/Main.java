package co.eci.snake.app.part1_primefinder;

import java.util.Scanner;

/**
 * Used to pause, print the prime numbers found and resume the count. 
 */
public class Main {

    private static final long T_MILLIS = 5000;

    public static void main(String[] args) throws InterruptedException {
        PauseController pauseController = new PauseController();
        PrimeCounter counter = new PrimeCounter();

        Control control = new Control(pauseController, counter);
        control.startAll();

        Scanner scanner = new Scanner(System.in);
 
        
        while (isAnyAlive(control)) {
            // Uses sleep as it already knows how much time it has to be asleep - 5 seconds. 
            Thread.sleep(T_MILLIS);

            pauseController.pause();

            System.out.println("== PAUSE ==");
            System.out.println("Prime numbers found till now: " + counter.get());
            System.out.println("Press ENTER to continue...");
            scanner.nextLine();

            pauseController.resume();
        }

        System.out.println("Ended. Prime numbers found: " + counter.get());
    }

    private static boolean isAnyAlive(Control control) {
        for (PrimeFinderThread t : control.getThreads()) {
            if (t.isAlive()) return true;
        }
        return false;
    }
}