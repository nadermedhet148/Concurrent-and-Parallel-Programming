import static java.lang.Thread.sleep;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class VThreads {

  // https://gist.github.com/matteobertozzi/a6715afc1fe878045a8e1e861e122c12
  // https://rockthejvm.com/articles/the-ultimate-guide-to-java-virtual-threads
  // https://medium.com/@fuad.valiyev01/what-happens-on-the-cpu-threads-and-core-side-when-we-run-java-1e2db05f22f5#:~:text=CPU%20Cores%20and%20Context%20Switching,threads%2C%20allowing%20for%20parallel%20execution.
  private static Thread virtualThread(String name, Runnable runnable) {
    return Thread.ofVirtual()
        .name(name)
        .start(runnable);
  }
  public class ThreadPerformanceBenchmark {
    public static void main(String[] args) throws InterruptedException {
      int n = 1000000000; // Number for which factorial is to be calculated
      int numThreads = 10; // Number of threads
      // Benchmark traditional threads
      long startTime = System.nanoTime();
      computeFactorialTraditional(n, numThreads);
      long endTime = System.nanoTime();
      long traditionalTime = endTime - startTime;
      // Benchmark virtual threads
      startTime = System.nanoTime();
      computeFactorialVirtual(n, numThreads);
      endTime = System.nanoTime();
      long virtualTime = endTime - startTime;
      // Print results
      System.out.println("Traditional Threads Execution Time: " + traditionalTime + " ns");
      System.out.println("Virtual Threads Execution Time: " + virtualTime + " ns");
      System.out.println("Performance Difference: " + (traditionalTime - virtualTime) + " ns");
    }
    // Task implementation with traditional threads
    private static void computeFactorialTraditional(int n, int numThreads) throws InterruptedException {
      ExecutorService executor = Executors.newFixedThreadPool(numThreads);
      long[] result = new long[numThreads];
      // Divide task into subtasks for each thread
      int blockSize = n / numThreads;
      for (int i = 0; i < numThreads; i++) {
        int start = i * blockSize + 1;
        int end = (i == numThreads - 1) ? n : (i + 1) * blockSize;
        int finalI = i;
        executor.submit(() -> {
          result[finalI] = computeFactorialInRange(start, end);
        });
      }
      executor.shutdown();
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
    // Task implementation with virtual threads
    private static void computeFactorialVirtual(int n, int numThreads) throws InterruptedException {
      Thread[] threads = new Thread[numThreads];
      long[] result = new long[numThreads];
      // Divide task into subtasks for each thread
      int blockSize = n / numThreads;
      for (int i = 0; i < numThreads; i++) {
        int start = i * blockSize + 1;
        int end = (i == numThreads - 1) ? n : (i + 1) * blockSize;
        int finalI = i;
        threads[i] = Thread.startVirtualThread(() -> {
          result[finalI] = computeFactorialInRange(start, end);
        });
      }
      // Wait for all threads to complete
      for (Thread thread : threads) {
        thread.join();
      }
    }
    // Compute factorial for a given range
    private static long computeFactorialInRange(int start, int end) {
      long factorial = 1;
      for (int i = start; i <= end; i++) {
        factorial *= i;
      }
      return factorial;
    }
  }


  static Thread bathTime() {
    return virtualThread(
        "Bath time",
        () -> {
          System.out.println("Bath time");
          try {
            sleep(Duration.ofMillis(500L));
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          System.out.println("Bath time done");
        });
  }

  static void concurrentMorningRoutineUsingExecutors() throws ExecutionException, InterruptedException {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      var bathTime =
          executor.submit(
              () -> {
                System.out.println("Bath time");
                try {
                  sleep(Duration.ofMillis(500L));
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
                System.out.println("Bath time done");
              });
      var boilingWater =
          executor.submit(
              () -> {
                System.out.println("Bath time");
                try {
                  sleep(Duration.ofMillis(500L));
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
                System.out.println("Bath time done");
              });
      bathTime.get();
      boilingWater.get();
    }
  }

//    Then, we can create a program that makes the desired number of virtual threads, i.e., the number of logical cores plus one:

    static void viewCarrierThreadPoolSize() {
      final ThreadFactory factory = Thread.ofVirtual().name("routine-", 0).factory();
      try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
        IntStream.range(0, numberOfCores() + 1)
            .forEach(i -> executor.submit(() -> {
              log("Hello, I'm a virtual thread number " + i);
              sleep(Duration.ofSeconds(1L));
            }));
      }
    }

}
