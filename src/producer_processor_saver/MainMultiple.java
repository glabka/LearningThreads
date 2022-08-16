package producer_processor_saver;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainMultiple {

    public static void main(String[] args) {
        LogicMultiple logic = new LogicMultiple(30, 5, 5, 500, 100, 1000);

        ExecutorService executor = Executors.newFixedThreadPool(9);

        long start = System.nanoTime();

        for (int i = 0; i < 3; i++) {
            executor.submit(() -> logic.loadAll());
            executor.submit(() -> logic.processAll());
            executor.submit(() -> logic.storeAll());
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            long end = System.nanoTime();
            System.out.println("execution time: " + ((end - start) / 1E6) + " ms");
        }

        System.out.println("program ended.");
    }
}
