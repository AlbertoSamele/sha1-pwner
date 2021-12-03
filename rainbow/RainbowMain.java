import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RainbowMain {
    public static void main(String[] args) {
        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        System.out.println("Generation of RainbowTables has started, please wait........................");
        Instant b = Instant.now();
        Runnable worker = new RainbowTable();
        executor.execute(worker);
        executor.shutdown();
        while (!executor.isTerminated()) {
        };
        Instant e = Instant.now();
        Duration timeElapsed = Duration.between(b, e);
        System.out.println("Generation completed. It took "
                + (timeElapsed.toMillis())/1000.0+" Seconds");
    }

}