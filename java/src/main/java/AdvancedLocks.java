import java.util.concurrent.*;

public class AdvancedLocks {
//  A Semaphore is a thread synchronization construct that can be used either to send signals between threads to avoid missed signals, or to guard a critical section like you would with a lock. Java 5 comes with semaphore implementations in the java.util.concurrent package so you don't have to implement your own semaphores. Still, it can be useful to know the theory behind their implementation and use.
//
//  Java 5 comes with a built-in Semaphore so you don't have to implement your own. You can read more about it in the java.util.concurrent.Semaphore text, in my java.util.concurrent tutorial.
//
//  Simple Semaphore
//  Here is a simple Semaphore implementation:

  public class Semaphore {
    private boolean signal = false;

    public synchronized void take() {
      this.signal = true;
      this.notify();
    }

    public synchronized void release() throws InterruptedException{
      while(!this.signal) wait();
      this.signal = false;
    }

  }
//  The take() method sends a signal which is stored internally in the Semaphore. The release() method waits for a signal. When received the signal flag is cleared again, and the release() method exited.
//
//  Using a semaphore like this you can avoid missed signals. You will call take() instead of notify() and release() instead of wait(). If the call to take() happens before the call to release() the thread calling release() will still know that take() was called, because the signal is stored internally in the signal variable. This is not the case with wait() and notify().
//
//  The names take() and release() may seem a bit odd when using a semaphore for signaling. The names origin from the use of semaphores as locks, as explained later in this text. In that case the names make more sense.
//
//  Using Semaphores for Signaling
//  Here is a simplified example of two threads signaling each other using a Semaphore:

//  Semaphore semaphore = new Semaphore();
//
//  SendingThread sender = new SendingThread(semaphore);
//
//  ReceivingThread receiver = new ReceivingThread(semaphore);
//
//receiver.start();
//sender.start();
  public class SendingThread {
    Semaphore semaphore = null;

    public SendingThread(Semaphore semaphore){
      this.semaphore = semaphore;
    }

    public void run(){
      while(true){
        //do something, then signal
        this.semaphore.take();

      }
    }
  }
  public class ReceivingThread {
    Semaphore semaphore = null;

    public ReceivingThread(Semaphore semaphore){
      this.semaphore = semaphore;
    }

    public void run() throws InterruptedException {
      while(true){
        this.semaphore.release();
        //receive signal, then do something...
      }
    }
  }
//  Counting Semaphore
//  The Semaphore implementation in the previous section does not count the number of signals sent to it by take() method calls. We can change the Semaphore to do so. This is called a counting semaphore. Here is a simple implementation of a counting semaphore:

  public class CountingSemaphore {
    private int signals = 0;

    public synchronized void take() {
      this.signals++;
      this.notify();
    }

    public synchronized void release() throws InterruptedException{
      while(this.signals == 0) wait();
      this.signals--;
    }

  }
//  Bounded Semaphore
//  The CoutingSemaphore has no upper bound on how many signals it can store. We can change the semaphore implementation to have an upper bound, like this:

  public class BoundedSemaphore {
    private int signals = 0;
    private int bound   = 0;

    public BoundedSemaphore(int upperBound){
      this.bound = upperBound;
    }

    public synchronized void take() throws InterruptedException{
      while(this.signals == bound) wait();
      this.signals++;
      this.notify();
    }

    public synchronized void release() throws InterruptedException{
      while(this.signals == 0) wait();
      this.signals--;
      this.notify();
    }
  }
//  Notice how the take() method now blocks if the number of signals is equal to the upper bound. Not until a thread has called release() will the thread calling take() be allowed to deliver its signal, if the BoundedSemaphore has reached its upper signal limit.
//
//  Using Semaphores as Locks
//  It is possible to use a bounded semaphore as a lock. To do so, set the upper bound to 1, and have the call to take() and release() guard the critical section. Here is an example:
//
//  BoundedSemaphore semaphore = new BoundedSemaphore(1);
//
//...

//    semaphore.take();
//
//try{
//    //critical section
//  } finally {
//    semaphore.release();
//  }
//  In contrast to the signaling use case the methods take() and release() are now called by the same thread. Since only one thread is allowed to take the semaphore, all other threads calling take() will be blocked until release() is called. The call to release() will never block since there has always been a call to take() first.
//
//
//
//      Introduction
//  A barrier is a synchronization mechanism that allows multiple threads to wait until a certain condition is met before proceeding. It is particularly useful in scenarios where a group of threads must work together and synchronize at specific points in their execution.
//
//  Types of Barriers
//      CyclicBarrier
//  CountDownLatch
//1. CyclicBarrier
//  The CyclicBarrier class in Java is used to make a group of threads wait until a set number of threads have reached a common barrier point. Once all threads reach the barrier, they can proceed.
//
//  Key Features:
//  Reusable: After all threads have been released, the barrier can be reused.
//  Allows a specified number of threads to wait.


  public class CyclicBarrierExample {
    private static final int NUMBER_OF_THREADS = 3;

    public static void main(String[] args) {
      CyclicBarrier barrier = new CyclicBarrier(NUMBER_OF_THREADS, () -> {
        System.out.println("All threads have reached the barrier. Proceeding...");
      });

      for (int i = 0; i < NUMBER_OF_THREADS; i++) {
        final int threadNumber = i + 1;
        new Thread(() -> {
          try {
            System.out.println("Thread " + threadNumber + " is waiting at the barrier.");
            barrier.await(); // Wait for other threads
            System.out.println("Thread " + threadNumber + " has crossed the barrier.");
          } catch (Exception e) {
            e.printStackTrace();
          }
        }).start();
      }
    }
  }
//2. CountDownLatch
//  The CountDownLatch class is used to make one or more threads wait until a set of operations being performed in other threads completes. It is not reusable once the count reaches zero.
//
//  Key Features:
//  Countdown mechanism: Decreases the count when a thread completes its task.
//  Once the count reaches zero, all waiting threads are released.
//

  public class CountDownLatchExample {
    private static final int NUMBER_OF_THREADS = 3;

    public static void main(String[] args) throws InterruptedException {
      CountDownLatch latch = new CountDownLatch(NUMBER_OF_THREADS);

      for (int i = 0; i < NUMBER_OF_THREADS; i++) {
        final int threadNumber = i + 1;
        new Thread(() -> {
          try {
            System.out.println("Thread " + threadNumber + " is performing its task.");
            Thread.sleep(1000); // Simulate work
            latch.countDown(); // Decrease the count
            System.out.println("Thread " + threadNumber + " has completed its task.");
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }).start();
      }

      latch.await(); // Wait for all threads to finish
      System.out.println("All threads have completed their tasks. Proceeding...");
    }
  }
}
