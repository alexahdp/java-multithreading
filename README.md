# Java multi-threading lessons

1. Example1 - running a thread and handling exceptions
2. Example2 - Extending Thread class
3. Example3 - Multiple threads execution
4. MultiExecutor - Running multiple threads
5. Interrupt - example of thread interruption
6. Interrupt2 - interrupt doesn't stop a sync job
7. ThroughtputHttpServer - using ThreadPoolExecutor
8. LockExample - using synchronized keyword.
   synchronized can be applied to a method:
```java
class SomeClass {
    public synchronized void someMethod() {
        // some code
    }
}
```
If there are many synchronized methods in a class, only one of them can be executed at a time,
because synchronized is applied to the entire object, not to the method.
   or to a block:
```java
class SomeClass {
    private final Object lock = new Object();
    public void someMethod() {
        synchronized(lock) {
            // some code
        }
    }
} 
```
In this case you can create many objects and each object will have its own lock.
It allows to have more granular control over synchronization.

Atomic operations:
 - getters and setters
 - primitive assignments, except long and double (because they are 64-bit)

`volatile` makes assignments to long atomic, however incrementing a volatile variable still involves multiple operations  

**Data race situation**

Also, `volatile` allows to avoid a situation with operations reordering.
E.g.:
```java
...
x += 1;
y += 1;
...
```
These two operations are independent, therefore, compiler may change the order of them.
If we read x and y from another thread, we may get x = 1 and y = 0, because the order of operations was changed.
We can fix this situation by making x and y volatile.

**Coarse-grained locking** - when we lock the entire object.
**Fine-grained locking** - when we lock only a part of the object.

**Deadlock** - when two threads are waiting for each other to release a lock.
Reason of deadlock - circular dependency.
Solutions:
 - consistent order of acquiring locks (easy to implement)
 - Deadlock detection (Watchdog)
 - Thread interruption (not possible with synchronized)
 - tryLock operations (not possible with synchronized)

**ReentrantLock approach**

```java
Lock lock = new ReentrantLock();

public int user() throws SomeException {
    lock.lock();
    try {
        // some code
       return value;
    } finally {
        // We must put it here to avoid a situation, when a lock wasn't released 
        lock.unlock();
    }
}
```
Why to use ReentrantLock:
 - getQueuedThreads() - returns a list of threads waiting for a lock
 - getOwner() - returns a thread that currently owns the lock
 - isHeldByCurrentThread() - checks if the current thread owns the lock
 - isLocked() - Queries if the lock is held by any thread

When we call lock.lock() and lock is already locked, the current thread will be blocked.
Thread will go sleep and will be woken up when the lock is released.
If we send an interrupt signal to the thread, it will ignore this signal and will continue to wait for the lock.
To avoid this situation, we can use `lockInterruptibly()` method, which can be interrupted.
But, it must be used in a try-catch block, because it throws an InterruptedException.
```java
Lock lock = new ReentrantLock();

public int user() throws SomeException {
    
    try {
        lock.lockInterruptibly();
        // some code
        return value;
    } catch (InterruptedException e) {
        // Clean up and exit
    } finally {
        // We must put it here to avoid a situation, when a lock wasn't released 
        lock.unlock();
    }
}
```

`tryLock()` method is used to avoid a situation, when a thread is blocked. Instead of waiting, it will return false if the lock is already taken.
Example:
```java
Lock lock = new ReentrantLock();
if (lock.tryLock()) {
    try {
        // some code
    } finally {
        lock.unlock();
    }
} else {
    // do something else
}
```

ReentrantReadWriteLock - allows to have multiple readers or one writer.

```java
import java.util.concurrent.locks.ReentrantReadWriteLock;

ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
Lock readLock = rwLock.readLock();
Lock writeLock = rwLock.writeLock();

public void read() {
    readLock.lock();
    try {
        // some code
    } finally {
        readLock.unlock();
    }
}
```
The idea of read-write lock is to allow multiple threads to read the data, but only one thread can write the data.
If a thread is writing the data, no other thread can read or write the data.
Many threads can read the data at the same time.

#### Semaphores

Semaphore is a counter, which is used to control the number of threads that can access a resource.
It is used to control the number of threads that can access a resource.
Semaphore with value 1 is similar to a lock, but there are some differences:
 - Semaphore doesn't have an owner  
 - Many threads can acquire a permit  
 - The same thread can acquire the semaphore multiple times  
 - The binary semaphore (initialized with 1) is not reentrant  

A non-buffered channel like in Go language can be implemented using a semaphore.
Note: you can call release() with not only 1 value. For example, if you have 10 permits, you can release 5 permits at once. 
This is how you can make a semaphore with a counter.

Condition variables - are used to make a thread wait until a certain condition is met.
Example:
```java
import java.util.concurrent.locks.Condition;
    
Condition condition = lock.newCondition();

public void someMethod() {
    lock.lock();
    try {
        while (!conditionIsMet()) {
            condition.await();
        }
        // some code
    } finally {
        lock.unlock();
    }
}
```

Object class in Java contains three methods that are used for thread communication:
 - wait
 - notify
 - notifyAll

Example of usage:
```java
public class MySharedClass {
    private boolean isComplete = false;
    public void waitUntilComplete() {
        synchronized (this) {
            while (!isComplete) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    // handle exception
                }
            }
        }
    }
    
    public void complete() {
        synchronized (this) {
            isComplete = true;
            this.notify();
        }
    }
}
```

### Problems of locks and algorithms with locks
 - **Deadlocks**
 - **Starvation** and low performance as a result
 - **Low priority inversion** - threads with low priority can block threads with high priority when they keep locks
 - **Context switching overhead**

### Atomic operations

```java
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger atomicInt = new AtomicInteger(0);

atomicInt.incrementAndGet();
atomicInt.decrementAndGet();
atomicInt.getAndIncrement();
...
```
Also, it worth to use copmareAndSet method to update values.
Example:

```java
import java.util.concurrent.locks.LockSupport;

StackNode<T> currentHeadNode = head.get();
StackNode<T> newHeadNode;

while (currentHeadNode != null) {  
    newHeadNode = currentHeadNode.next;
    if (head.compareAndSet(currentHeadNode, newHeadNode)){
        break;
    } else {
        LockSupport.parkNanos(1);
        currentHeadNode = head.get();
    }
}
```
Atomic operations may be significantly faster than locks, because they are implemented in hardware.

### Virtual threads 
Virtual threads are available cin Java 21.
Example of creation:
```java
Runnable runnable = () -> System.out.println("Hello, World!");

Thread virtualThread = Thread.ofVirtual.unstarted(runnable);
virtualThread.start();
virtualThread.join();
```
