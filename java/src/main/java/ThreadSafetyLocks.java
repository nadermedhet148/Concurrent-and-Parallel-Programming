import java.util.HashMap;
import java.util.Map;

public class ThreadSafetyLocks {

//  Understanding Critical Sections and Race Conditions in Java
//  Introduction
//  In concurrent programming, critical sections are segments of code that must be executed by only one thread at a time to prevent inconsistent results. This document explores two common types of critical sections—read-modify-write and check-then-act—and discusses how race conditions can arise, along with strategies to prevent them.
//
//  Read-Modify-Write Critical Sections
//  Example Code
//  Consider the following Java code for a simple counter:

  public class Counter {
    protected long count = 0;

    public void add(long value) {
      this.count = this.count + value;
    }
  }
//  Race Condition Scenario
//  If two threads, A and B, execute the add method concurrently on the same instance of Counter, a race condition can occur. The sequence of operations is not atomic, leading to potential inconsistencies. Here’s a breakdown of a problematic execution:
//
//  Initial State: this.count = 0;
//  Thread A: Reads this.count into a register (0).
//  Thread B: Reads this.count into a register (0).
//  Thread B: Adds 2 to its register (value now 2).
//  Thread B: Writes 2 back to memory. (this.count = 2)
//  Thread A: Adds 3 to its register (value now 3).
//  Thread A: Writes 3 back to memory. (this.count = 3)
//  Despite the intention for the final value to be 5, the result is 3 due to the interleaved execution of threads.
//
//  Understanding Race Conditions
//  A race condition occurs when multiple threads compete for the same resource, leading to unpredictable outcomes. The critical section in the add() method exemplifies this issue.
//
//      Check-Then-Act Critical Sections
//  Example Code
//  Another common scenario is the check-then-act pattern:

  public class CheckThenActExample {

    public void checkThenAct(Map<String, String> sharedMap) {
      if (sharedMap.containsKey("key")) {
        String val = sharedMap.remove("key");
        if (val == null) {
          System.out.println("Value for 'key' was null");
        }
      } else {
        sharedMap.put("key", "value");
      }
    }
  }
//  Race Condition Scenario
//  If multiple threads call checkThenAct() on the same object, they may simultaneously evaluate sharedMap.containsKey("key") as true. This can lead to multiple threads attempting to remove the same key, with only one succeeding, resulting in inconsistent behavior.
//
//  Preventing Race Conditions
//  To prevent race conditions, critical sections must be executed as atomic instructions. Here are some strategies:
//
//      1. Thread Synchronization
//  Using synchronized blocks ensures that only one thread can execute the critical section at a time:

  public class TwoSums {
    private int sum1 = 0;
    private int sum2 = 0;

    public void add(int val1, int val2) {
      synchronized (this) {
        this.sum1 += val1;
        this.sum2 += val2;
      }
    }
  }
//2. Reducing Contention
//  For larger critical sections, consider breaking them into smaller sections to allow greater concurrency:
//
//  Copy
  public class TwoSumsV2 {
    private int sum1 = 0;
    private int sum2 = 0;
    private final Object sum1Lock = new Object();
    private final Object sum2Lock = new Object();

    public void add(int val1, int val2) {
      synchronized (sum1Lock) {
        this.sum1 += val1;
      }
      synchronized (sum2Lock) {
        this.sum2 += val2;
      }
    }
  }
//  In this implementation, two threads can execute the add() method simultaneously, each operating on different locks, thus reducing contention.
//
//  Conclusion
//  Understanding critical sections and the potential for race conditions is crucial in concurrent programming. By employing proper thread synchronization techniques, developers can ensure data integrity and improve application performance.

//  Race conditions occur only if multiple threads are accessing the same resource, and one or more of the threads write to the resource. If multiple threads read the same resource race conditions do not occur.

//  We can make sure that objects shared between threads are never updated by any of the threads by making the shared objects immutable, and thereby thread safe. Here is an example:

  public class ImmutableValue{

    private int value = 0;

    public ImmutableValue(int value){
      this.value = value;
    }

    public int getValue(){
      return this.value;
    }
    public ImmutableValue add(int valueToAdd){
      return new ImmutableValue(this.value + valueToAdd);
    }
  }
//  Notice how the value for the ImmutableValue instance is passed in the constructor. Notice also how there is no setter method. Once an ImmutableValue instance is created you cannot change its value. It is immutable. You can read it however, using the getValue() method.

//  If you need to perform operations on the ImmutableValue instance you can do so by returning a new instance with the value resulting from the operation. Here is an example of an add operation:

//  Notice how the add() method returns a new ImmutableValue instance with the result of the add operation, rather than adding the value to itself.
//
//  The Reference is not Thread Safe!
//  It is important to remember, that even if an object is immutable and thereby thread safe, the reference to this object may not be thread safe. Look at this example:

  public class Calculator{
    private ImmutableValue currentValue = null;

    public ImmutableValue getValue(){
      return currentValue;
    }

    public void setValue(ImmutableValue newValue){
      this.currentValue = newValue;
    }

    public void add(int newValue){
      this.currentValue = this.currentValue.add(newValue);
    }
  }
//  The Calculator class holds a reference to an ImmutableValue instance. Notice how it is possible to change that reference through both the setValue() and add() methods. Therefore, even if the Calculator class uses an immutable object internally, it is not itself immutable, and therefore not thread safe. In other words: The ImmutableValue class is thread safe, but the use of it is not. This is something to keep in mind when trying to achieve thread safety through immutability.
//
//  To make the Calculator class thread safe you could have declared the getValue(), setValue(), and add() methods synchronized. That would have done the trick.

//
//  The Java synchronized Keyword
//  Synchronized blocks in Java are marked with the synchronized keyword. A synchronized block in Java is synchronized on some object. All synchronized blocks synchronized on the same object can only have one thread executing inside them at the same time. All other threads attempting to enter the synchronized block are blocked until the thread inside the synchronized block exits the block.
//
//  The synchronized keyword can be used to mark four different types of blocks:
//
//  Instance methods
//  Static methods
//  Code blocks inside instance methods
//  Code blocks inside static methods
//  These blocks are synchronized on different objects. Which type of synchronized block you need depends on the concrete situation. Each of these synchronized blocks will be explained in more detail below.

//  Synchronized Instance Methods
//  Here is a synchronized instance method:

//  Notice the use of the synchronized keyword in the add() method declaration. This tells Java that the method is synchronized.
//
//  A synchronized instance method in Java is synchronized on the instance (object) owning the method. Thus, each instance has its synchronized methods synchronized on a different object: the owning instance.
//
//  Only one thread per instance can execute inside a synchronized instance method. If more than one instance exist, then one thread at a time can execute inside a synchronized instance method per instance. One thread per instance.
//
//  This is true across all synchronized instance methods for the same object (instance). Thus, in the following example, only one thread can execute inside either of of the two synchronized methods. One thread in total per instance:

  public class MyCounter {

    private int count = 0;

    public synchronized void add(int value){
      this.count += value;
    }
    public synchronized void subtract(int value){
      this.count -= value;
    }

//    ynchronized Block Limitations and Alternatives
//    Synchronized blocks in Java have several limitations. For instance, a synchronized block in Java only allows a single thread to enter at a time. However, what if two threads just wanted to read a shared value, and not update it? That might be safe to allow. As alternative to a synchronized block you could guard the code with a Read / Write Lock which as more advanced locking semantics than a synchronized block. Java actually comes with a built in ReadWriteLock class you can use.
//
//    What if you want to allow N threads to enter a synchronized block, and not just one? You could use a Semaphore to achieve that behaviour. Java actually comes with a built-in Java Semaphore class you can use.
//
//    Synchronized blocks do not guarantee in what order threads waiting to enter them are granted access to the synchronized block. What if you need to guarantee that threads trying to enter a synchronized block get access in the exact sequence they requested access to it? You need to implement Fairness yourself.
//
//    What if you just have one thread writing to a shared variable, and other threads only reading that variable? Then you might be able to just use a volatile variable without any synchronization around.
//
//    Synchronized Block Performance Overhead
//    There is a small performance overhead associated with entering and exiting a synchronized block in Java. As Jave have evolved this performance overhead has gone down, but there is still a small price to pay.
//
//    The performance overhead of entering and exiting a synchronized block is mostly something to worry about if you enter and exit a synchronized block lots of times within a tight loop or so.
//
//    Also, try not to have larger synchronized blocks than necessary. In other words, only synchronize the operations that are really necessary to synchronize - to avoid blocking other threads from executing operations that do not have to be synchronized. Only the absolutely necessary instructions in synchronized blocks. That should increase parallelism of your code.

  }

//  A Simple Lock
//  Let's start out by looking at a synchronized block of Java code:

  public class CounterV2 {

    private int count = 0;

    public int inc(){
      synchronized(this){
        return ++count;
      }
    }
  }
//  Notice the synchronized(this) block in the inc() method. This block makes sure that only one thread can execute the return ++count at a time. The code in the synchronized block could have been more advanced, but the simple ++count suffices to get the point across.
//
//  The Counter class could have been written like this instead, using a Lock instead of a synchronized block:

  public class CounterLock{

    private Lock lock = new Lock();
    private int count = 0;

    public int inc() throws InterruptedException {
      lock.lock();
      int newCount = ++count;
      lock.unlock();
      return newCount;
    }
  }
//  The lock() method locks the Lock instance so that all threads calling lock() are blocked until unlock() is executed.
//
//  Here is a simple Lock implementation:

  public class Lock{

    private boolean isLocked = false;

    public synchronized void lock()
        throws InterruptedException{
      while(isLocked){
        wait();
      }
      isLocked = true;
    }

    public synchronized void unlock(){
      isLocked = false;
      notify();
    }
  }

//  Lock Reentrance
//  Synchronized blocks in Java are reentrant. This means, that if a Java thread enters a synchronized block of code, and thereby take the lock on the monitor object the block is synchronized on, the thread can enter other Java code blocks synchronized on the same monitor object. Here is an example:

//  public class Reentrant{
//
//    public synchronized outer(){
//      inner();
//    }
//
//    public synchronized inner(){
//      //do something
//    }
//  }
//  Notice how both outer() and inner() are declared synchronized, which in Java is equivalent to a synchronized(this) block. If a thread calls outer() there is no problem calling inner() from inside outer(), since both methods (or blocks) are synchronized on the same monitor object ("this"). If a thread already holds the lock on a monitor object, it has access to all blocks synchronized on the same monitor object. This is called reentrance. The thread can reenter any block of code for which it already holds the lock.
//
//  Read / Write Lock Java Implementation
//  First let's summarize the conditions for getting read and write access to the resource:
//
//  Read Access   	If no threads are writing, and no threads have requested write access.
//  Write Access   	If no threads are reading or writing.
//  If a thread wants to read the resource, it is okay as long as no threads are writing to it, and no threads have requested write access to the resource. By up-prioritizing write-access requests we assume that write requests are more important than read-requests. Besides, if reads are what happens most often, and we did not up-prioritize writes, starvation could occur. Threads requesting write access would be blocked until all readers had unlocked the ReadWriteLock. If new threads were constantly granted read access the thread waiting for write access would remain blocked indefinately, resulting in starvation. Therefore a thread can only be granted read access if no thread has currently locked the ReadWriteLock for writing, or requested it locked for writing.
//
//  A thread that wants write access to the resource can be granted so when no threads are reading nor writing to the resource. It doesn't matter how many threads have requested write access or in what sequence, unless you want to guarantee fairness between threads requesting write access.
//
//  With these simple rules in mind we can implement a ReadWriteLock as shown below:

  public class ReadWriteLock{

    private int readers       = 0;
    private int writers       = 0;
    private int writeRequests = 0;

    public synchronized void lockRead() throws InterruptedException{
      while(writers > 0 || writeRequests > 0){
        wait();
      }
      readers++;
    }

    public synchronized void unlockRead(){
      readers--;
      notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException{
      writeRequests++;

      while(readers > 0 || writers > 0){
        wait();
      }
      writeRequests--;
      writers++;
    }

    public synchronized void unlockWrite() throws InterruptedException{
      writers--;
      notifyAll();
    }
  }
//  The ReadWriteLock has two lock methods and two unlock methods. One lock and unlock method for read access and one lock and unlock for write access.
//
//  The rules for read access are implemented in the lockRead() method. All threads get read access unless there is a thread with write access, or one or more threads have requested write access.
//
//  The rules for write access are implemented in the lockWrite() method. A thread that wants write access starts out by requesting write access (writeRequests++). Then it will check if it can actually get write access. A thread can get write access if there are no threads with read access to the resource, and no threads with write access to the resource. How many threads have requested write access doesn't matter.
//
//  It is worth noting that both unlockRead() and unlockWrite() calls notifyAll() rather than notify(). To explain why that is, imagine the following situation:
//
//  Inside the ReadWriteLock there are threads waiting for read access, and threads waiting for write access. If a thread awakened by notify() was a read access thread, it would be put back to waiting because there are threads waiting for write access. However, none of the threads awaiting write access are awakened, so nothing more happens. No threads gain neither read nor write access. By calling noftifyAll() all waiting threads are awakened and check if they can get the desired access.
//
//  Calling notifyAll() also has another advantage. If multiple threads are waiting for read access and none for write access, and unlockWrite() is called, all threads waiting for read access are granted read access at once - not one by one.
//
//  Read / Write Lock Reentrance
//  The ReadWriteLock class shown earlier is not reentrant. If a thread that has write access requests it again, it will block because there is already one writer - itself. Furthermore, consider this case:
//
//  Thread 1 gets read access.
//
//      Thread 2 requests write access but is blocked because there is one reader.
//
//      Thread 1 re-requests read access (re-enters the lock), but is blocked because there is a write request
//  In this situation the previous ReadWriteLock would lock up - a situation similar to deadlock. No threads requesting neither read nor write access would be granted so.
//
//  To make the ReadWriteLock reentrant it is necessary to make a few changes. Reentrance for readers and writers will be dealt with separately.
//
//  Read Reentrance
//  To make the ReadWriteLock reentrant for readers we will first establish the rules for read reentrance:
//
//  A thread is granted read reentrance if it can get read access (no writers or write requests), or if it already has read access (regardless of write requests).
//  To determine if a thread has read access already a reference to each thread granted read access is kept in a Map along with how many times it has acquired read lock. When determing if read access can be granted this Map will be checked for a reference to the calling thread. Here is how the lockRead() and unlockRead() methods looks after that change:


//  As you can see read reentrance is only granted if no threads are currently writing to the resource. Additionally, if the calling thread already has read access this takes precedence over any writeRequests.
//
//      Write Reentrance
//  Write reentrance is granted only if the thread has already write access. Here is how the lockWrite() and unlockWrite() methods look after that change:

  public class ReadWriteLockV2{

    private Map<Thread, Integer> readingThreads =
        new HashMap<Thread, Integer>();

    private int writeAccesses    = 0;
    private int writeRequests    = 0;
    private Thread writingThread = null;

    public synchronized void lockWrite() throws InterruptedException{
      writeRequests++;
      Thread callingThread = Thread.currentThread();
      while(! canGrantWriteAccess(callingThread)){
        wait();
      }
      writeRequests--;
      writeAccesses++;
      writingThread = callingThread;
    }

    public synchronized void unlockWrite() throws InterruptedException{
      writeAccesses--;
      if(writeAccesses == 0){
        writingThread = null;
      }
      notifyAll();
    }

    private boolean canGrantWriteAccess(Thread callingThread){
      if(hasReaders())             return false;
      if(writingThread == null)    return true;
      if(!isWriter(callingThread)) return false;
      return true;
    }

    private boolean hasReaders(){
      return readingThreads.size() > 0;
    }

    private boolean isWriter(Thread callingThread){
      return writingThread == callingThread;
    }
  }
//  Notice how the thread currently holding the write lock is now taken into account when determining if the calling thread can get write access.
//
//  Read to Write Reentrance
//  Sometimes it is necessary for a thread that have read access to also obtain write access. For this to be allowed the thread must be the only reader. To achieve this the writeLock() method should be changed a bit. Here is what it would look like:

//  Reentrance Lockout is a security mechanism primarily used in the context of smart contracts and decentralized applications (dApps) on blockchain platforms, particularly Ethereum. It is designed to prevent a specific type of attack known as a reentrancy attack.
//
//      Key Concepts
//  Reentrancy Attack:
//
//  This occurs when an external contract calls back into the original contract before the first invocation has completed. This can lead to unexpected behavior, like draining funds or altering state variables.
//  Lockout Mechanism:
//
//  To mitigate the risk of reentrancy attacks, developers implement a lockout mechanism. This typically involves using a boolean variable (often called locked) to track whether a function is currently executing.
//  When a function is called, it checks the lock status:
//  If it is not locked, it sets the lock and proceeds with execution.
//  If it is already locked, the function exits or throws an error, preventing reentrancy.
//      Example Implementation
//  Here’s a simplified example of how a reentrance lockout might be implemented in Solidity (the programming language for Ethereum smart contracts):
//
//
//  Benefits
//  Prevention of Exploits: By ensuring that a function cannot be re-entered while it is still executing, the contract is safeguarded against certain types of attacks.
//  Increased Security: It adds an extra layer of security to smart contracts, making them less vulnerable to malicious actors.
//  Conclusion
//  Reentrance Lockout is an essential practice in smart contract development to protect against reentrancy vulnerabilities. Proper implementation can significantly enhance the security and reliability of decentralized applications.

  ///  https://jenkov.com/tutorials/java-concurrency/read-write-locks.html
}
