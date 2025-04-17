import java.util.ArrayList;
import java.util.List;

public class ThreadSafety {
  //  Deadlock Example
  //  Below is an example of a deadlock situation:
  //
  //  If thread 1 locks A, and tries to lock B, and thread 2 has already locked B, and tries to lock A, a deadlock arises. Thread 1 can never get B, and thread 2 can never get A. In addition, neither of them will ever know. They will remain blocked on each their object, A and B, forever. This situation is a deadlock.
  //
  //  The situation is illustrated below:
  //
  //  Thread 1  locks A, waits for B
  //  Thread 2  locks B, waits for A
  //  Here is an example of a TreeNode class that call synchronized methods in different instances:

  public class TreeNode {

    TreeNode parent = null;
    List children = new ArrayList();

    public synchronized void addChild(TreeNode child) {
      if (!this.children.contains(child)) {
        this.children.add(child);
        child.setParentOnly(this);
      }
    }

    public synchronized void addChildOnly(TreeNode child) {
      if (!this.children.contains(child)) {
        this.children.add(child);
      }
    }

    public synchronized void setParent(TreeNode parent) {
      this.parent = parent;
      parent.addChildOnly(this);
    }

    public synchronized void setParentOnly(TreeNode parent) {
      this.parent = parent;
    }
  }
  //  If a thread (1) calls the parent.addChild(child) method at the same time as another thread (2) calls the child.setParent(parent) method, on the same parent and child instances, a deadlock can occur. Here is some pseudo code that illustrates this:
  //
  //
  //  Thread 1: parent.addChild(child); //locks parent
  //          --> child.setParentOnly(parent);
  //
  //  Thread 2: child.setParent(parent); //locks child
  //          --> parent.addChildOnly()
  //  First thread 1 calls parent.addChild(child). Since addChild() is synchronized thread 1 effectively locks the parent object for access from other treads.
  //
  //  Then thread 2 calls child.setParent(parent). Since setParent() is synchronized thread 2 effectively locks the child object for acces from other threads.
  //
  //  Now both child and parent objects are locked by two different threads. Next thread 1 tries to call child.setParentOnly() method, but the child object is locked by thread 2, so the method call just blocks. Thread 2 also tries to call parent.addChildOnly() but the parent object is locked by thread 1, causing thread 2 to block on that method call. Now both threads are blocked waiting to obtain locks the other thread holds.
  //
  //      Note: The two threads must call parent.addChild(child) and child.setParent(parent) at the same time as described above, and on the same two parent and child instances for a deadlock to occur. The code above may execute fine for a long time until all of a sudden it deadlocks.
  //
  //  The threads really need to take the locks *at the same time*. For instance, if thread 1 is a bit ahead of thread2, and thus locks both A and B, then thread 2 will be blocked already when trying to lock B. Then no deadlock occurs. Since thread scheduling often is unpredictable there is no way to predict *when* a deadlock occurs. Only that it *can* occur.
  //
  //
  //  More Complicated Deadlocks
  //  Deadlock can also include more than two threads. This makes it harder to detect. Here is an example in which four threads have deadlocked:
  //
  //  Thread 1  locks A, waits for B
  //  Thread 2  locks B, waits for C
  //  Thread 3  locks C, waits for D
  //  Thread 4  locks D, waits for A
  //  Thread 1 waits for thread 2, thread 2 waits for thread 3, thread 3
  //
  //
  //  If a thread is not granted CPU time because other threads grab it all, it is called "starvation". The thread is "starved to death" because other threads are allowed the CPU time instead of it. The solution to starvation is called "fairness" - that all threads are fairly granted a chance to execute.
  //
  //  Causes of Starvation in Java
  //  The following three common causes can lead to starvation of threads in Java:
  //
  //  Threads with high priority swallow all CPU time from threads with lower priority.
  //
  //  Threads are blocked indefinately waiting to enter a synchronized block, because other threads are constantly allowed access before it.
  //
  //  Threads waiting on an object (called wait() on it) remain waiting indefinitely because other threads are constantly awakened instead of it.
  //  Threads with high priority swallow all CPU time from threads with lower priority
  //  You can set the thread priority of each thread individually. The higher the priority the more CPU time the thread is granted. You can set the priority of threads between 1 and 10. Exactly how this is interpreted depends on the operating system your application is running on. For most applications you are better off leaving the priority unchanged.
  //
  //  Threads are blocked indefinitely waiting to enter a synchronized block
  //  Java's synchronized code blocks can be another cause of starvation. Java's synchronized code block makes no guarantee about the sequence in which threads waiting to enter the synchronized block are allowed to enter. This means that there is a theoretical risk that a thread remains blocked forever trying to enter the block, because other threads are constantly granted access before it. This problem is called "starvation", that a thread is "starved to death" by because other threads are allowed the CPU time instead of it.
  //
  //  Threads waiting on an object (called wait() on it) remain waiting indefinitely
  //  The notify() method makes no guarantee about what thread is awakened if multiple thread have called wait() on the object notify() is called on. It could be any of the threads waiting. Therefore there is a risk that a thread waiting on a certain object is never awakened because other waiting threads are always awakened instead of it.
  //
  //  Implementing Fairness in Java
  //  While it is not possible to implement 100% fairness in Java we can still implement our synchronization constructs to increase fairness between threads.
  //
  //  First lets study a simple synchronized code block:

  //  If more than one thread call the doSynchronized() method, some of them will be blocked until the first thread granted access has left the method. If more than one thread are blocked waiting for access there is no guarantee about which thread is granted access next.
  //
  //  Using Locks Instead of Synchronized Blocks
  //  To increase the fairness of waiting threads first we will change the code block to be guarded by a lock rather than a synchronized block:

  public class Synchronizer {

    Lock lock = new Lock();

    public void doSynchronized() throws InterruptedException {
      this.lock.lock();
      //critical section, do a lot of work which takes a long time
      this.lock.unlock();
    }

  }
  //  Notice how the doSynchronized() method is no longer declared synchronized. Instead the critical section is guarded by the lock.lock() and lock.unlock() calls.
  //
  //  A simple implementation of the Lock class could look like this:

  public class Lock {

    private boolean isLocked = false;
    private Thread lockingThread = null;

    public synchronized void lock() throws InterruptedException {
      while (isLocked) {
        wait();
      }
      isLocked = true;
      lockingThread = Thread.currentThread();
    }

    public synchronized void unlock() {
      if (this.lockingThread != Thread.currentThread()) {
        throw new IllegalMonitorStateException(
            "Calling thread has not locked this lock");
      }
      isLocked = false;
      lockingThread = null;
      notify();
    }
  }
  //  If you look at the Synchronizer class above and look into this Lock implementation you will notice that threads are now blocked trying to access the lock() method, if more than one thread calls lock() simultanously. Second, if the lock is locked, the threads are blocked in the wait() call inside the while(isLocked) loop in the lock() method. Remember that a thread calling wait() releases the synchronization lock on the Lock instance, so threads waiting to enter lock() can now do so. The result is that multiple threads can end up having called wait() inside lock().
  //
  //  If you look back at the doSynchronized() method you will notice that the comment between lock() and unlock() states, that the code in between these two calls take a "long" time to execute. Let us further assume that this code takes long time to execute compared to entering the lock() method and calling wait() because the lock is locked. This means that the majority of the time waited to be able to lock the lock and enter the critical section is spent waiting in the wait() call inside the lock() method, not being blocked trying to enter the lock() method.
  //
  //  As stated earlier synchronized blocks makes no guarantees about what thread is being granted access if more than one thread is waiting to enter. Nor does wait() make any guarantees about what thread is awakened when notify() is called. So, the current version of the Lock class makes no different guarantees with respect to fairness than synchronized version of doSynchronized(). But we can change that.
  //
  //  The current version of the Lock class calls its own wait() method. If instead each thread calls wait() on a separate object, so that only one thread has called wait() on each object, the Lock class can decide which of these objects to call notify() on, thereby effectively selecting exactly what thread to awaken.
  //
  //  A Fair Lock
  //  Below is shown the previous Lock class turned into a fair lock called FairLock. You will notice that the implementation has changed a bit with respect to synchronization and wait() / notify() compared to the Lock class shown earlier.
  //
  //  Exactly how I arrived at this design beginning from the previous Lock class is a longer story involving several incremental design steps, each fixing the problem of the previous step: Nested Monitor Lockout, Slipped Conditions, and Missed Signals. That discussion is left out of this text to keep the text short, but each of the steps are discussed in the appropriate texts on the topic ( see the links above). What is important is, that every thread calling lock() is now queued, and only the first thread in the queue is allowed to lock the FairLock instance, if it is unlocked. All other threads are parked waiting until they reach the top of the queue.

  public class FairLock {

    private boolean isLocked = false;
    private Thread lockingThread = null;
    private List<QueueObject> waitingThreads =
        new ArrayList<QueueObject>();

    public void lock() throws InterruptedException {
      QueueObject queueObject = new QueueObject();
      boolean isLockedForThisThread = true;
      synchronized (this) {
        waitingThreads.add(queueObject);
      }

      while (isLockedForThisThread) {
        synchronized (this) {
          isLockedForThisThread =
              isLocked || waitingThreads.get(0) != queueObject;
          if (!isLockedForThisThread) {
            isLocked = true;
            waitingThreads.remove(queueObject);
            lockingThread = Thread.currentThread();
            return;
          }
        }
        try {
          queueObject.doWait();
        } catch (InterruptedException e) {
          synchronized (this) {waitingThreads.remove(queueObject);}
          throw e;
        }
      }
    }

    public synchronized void unlock() {
      if (this.lockingThread != Thread.currentThread()) {
        throw new IllegalMonitorStateException(
            "Calling thread has not locked this lock");
      }
      isLocked = false;
      lockingThread = null;
      if (waitingThreads.size() > 0) {
        waitingThreads.get(0).doNotify();
      }
    }
  }

  public class QueueObject {

    private boolean isNotified = false;

    public synchronized void doWait() throws InterruptedException {
      while (!isNotified) {
        this.wait();
      }
      this.isNotified = false;
    }

    public synchronized void doNotify() {
      this.isNotified = true;
      this.notify();
    }

    public boolean equals(Object o) {
      return this == o;
    }
  }
  //  First you might notice that the lock() method is no longer declared synchronized. Instead only the blocks necessary to synchronize are nested inside synchronized blocks.
  //
  //  FairLock creates a new instance of QueueObject and enqueue it for each thread calling lock(). The thread calling unlock() will take the top QueueObject in the queue and call doNotify() on it, to awaken the thread waiting on that object. This way only one waiting thread is awakened at a time, rather than all waiting threads. This part is what governs the fairness of the FairLock.
  //
  //  Notice how the state of the lock is still tested and set within the same synchronized block to avoid slipped conditions.
  //
  //  Also notice that the QueueObject is really a semaphore. The doWait() and doNotify() methods store the signal internally in the QueueObject. This is done to avoid missed signals caused by a thread being preempted just before calling queueObject.doWait(), by another thread which calls unlock() and thereby queueObject.doNotify(). The queueObject.doWait() call is placed outside the synchronized(this) block to avoid nested monitor lockout, so another thread can actually call unlock() when no thread is executing inside the synchronized(this) block in lock() method.
  //
  //  Finally, notice how the queueObject.doWait() is called inside a try - catch block. In case an InterruptedException is thrown the thread leaves the lock() method, and we need to dequeue it.
  //
  //  A Note on Performance
  //  If you compare the Lock and FairLock classes you will notice that there is somewhat more going on inside the lock() and unlock() in the FairLock class. This extra code will cause the FairLock to be a sligtly slower synchronization mechanism than Lock. How much impact this will have on your application depends on how long time the code in the critical section guarded by the FairLock takes to execute. The longer this takes to execute, the less significant the added overhead of the synchronizer is. It does of course also depend on how often this code is called.
  //
  //      Next: Nested Monitor Lockout
}
