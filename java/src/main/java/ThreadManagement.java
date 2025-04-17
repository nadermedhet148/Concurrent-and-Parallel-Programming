import java.util.concurrent.*;


public class ThreadManagement {

  ///  talk about https://jenkov.com/tutorials/java-concurrency/amdahls-law.html
  static class MyThread extends Thread {
    public void run() {
      for (int i = 0; i < 5; i++) {
        System.out.println(Thread.currentThread().getName() + " - Count: " + i);
        try {
          Thread.sleep(500); // Sleep for 500 milliseconds
        } catch (InterruptedException e) {
          System.out.println(e);
        }
      }
    }
  }

  public class ThreadExample {
    public static void main(String[] args) {
      MyThread thread1 = new MyThread();
      MyThread thread2 = new MyThread();

      thread1.start(); // Start thread1
      thread2.start(); // Start thread2
    }
  }

  static  class MyRunnable implements Runnable {
    public void run() {
      for (int i = 0; i < 5; i++) {
        System.out.println(Thread.currentThread().getName() + " - Count: " + i);
        try {
          Thread.sleep(500); // Sleep for 500 milliseconds
        } catch (InterruptedException e) {
          System.out.println(e);
        }
      }
    }
  }

  public class RunnableExample {
    public static void main(String[] args) {
      MyRunnable myRunnable = new MyRunnable();
      Thread thread1 = new Thread(myRunnable);
      Thread thread2 = new Thread(myRunnable);

      thread1.start(); // Start thread1
      thread2.start(); // Start thread2
    }
  }

//  2. Using ScheduledExecutorService
//  The ScheduledExecutorService is part of the java.util.concurrent package and is a more robust way to schedule tasks.

  public class ScheduledExecutorExample {
    public static void main(String[] args) {
      ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

      Runnable task = () -> {
        System.out.println("Task executed at: " + System.currentTimeMillis());
      };

      // Schedule the task to run after 2 seconds and then every 1 second
      scheduler.scheduleAtFixedRate(task, 2, 1, TimeUnit.SECONDS);
    }
  }


  public class FutureExample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
      ExecutorService executor = Executors.newSingleThreadExecutor();

      Callable<String> task = () -> {
        Thread.sleep(2000); // Simulate a long-running task
        return "Task completed!";
      };

      Future<String> future = executor.submit(task);

      // Do some other work while the task is running
      System.out.println("Doing other work...");

      // Get the result of the task (this will block until the task is complete)
      String result = future.get();
      System.out.println(result);

      executor.shutdown();
    }
  }

  public class FixedThreadPoolExample {
    public static void main(String[] args) {
      // Create a fixed thread pool with 3 threads
      ExecutorService executor = Executors.newFixedThreadPool(3);

      // Create and submit tasks
      for (int i = 1; i <= 5; i++) {
        final int taskId = i;
        executor.submit(() -> {
          System.out.println("Task " + taskId + " is running on " + Thread.currentThread().getName());
          try {
            // Simulate some work with sleep
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            System.out.println(e);
          }
          System.out.println("Task " + taskId + " completed on " + Thread.currentThread().getName());
        });
      }

      // Shutdown the executor
      executor.shutdown();
    }
  }

  ///  add example for thread usage vs norm execu

  public static void main(String[] args) {

  }

}
