# Multi-Threading
* good examples from multi-threading course on coursera are here https://github.com/Beerkay/JavaMultiThreading
* intrinsic lock or monitor lock - in java API just monitor
* don't call Thread.sleep but rather wait() method -> https://stackoverflow.com/questions/10663920/calling-thread-sleep-from-synchronized-context-in-java
## missed signal
* when notify call has no effect because the thread supposed to be notified is not sleeping yet.
* one way to avoid this is to use semaphore.
* other way how to avoid it is by changing and checking of state of the object (changing a variable).

## deadlock
* definition: any situation in which no member of some group of entities can proceed because each waits for another member, including itself, to take action, such as sending a message or, more commonly, releasing a lock.
* If you make sure that all locks are always taken in the same order by any thread, deadlocks cannot occur.

## volatile
* this modifier guarantees that any thread that reads a field will see the most recently written value.

## Synchronize keyword
* by itself lock on current object or class in case of static methods
* by itself when more methods doesn't need to have exclusive access to resources they will -> lower performance -> it's better for unrelated methods to lock with different parameter (object to lock on) for each different synchronized block thus unrelated methods being able to be executed simultaneously
* any object or class (static context) can be locked on
* is unfair (doesn't guarantee that longest waiting thread is granted access when wait/notify methods are used)
* prettier code then with Lock class

## Thread pools
* ExecutorService executor = Executors.newFixedThreadPool(2); // max 2 Threads running
* executor.shutdown // prevents clients to send more work to the executor service; the thread pool won't exit until shutdown has been explicitly called.
* executor.awaitTermination(1, TimeUnit.DAYS); // execution wait until task is done. It is called after shutdown.

## CountDownLatch
* method countDown can be used from different threads to decrease latch's internal value
* latch.await(); // the code waits until latch's value is zero

## BlockingQueue
* thread safe queue with 4 options of adding (add, offer, offer with timeout, put) and removing (remove, poll, poll with timeout, take) values.

## wait and notify methods (synchronized block)
* low-level multi-threading methods of class Object
* can be used to avoid polling loops
### wait
* can be used only in synchronized block/method
* gives away the intrinsic lock and wait until it is notified / interrupted
### notify
* can be used only in synchronized block / method
* awakens one of threads in wait state that has the same lock as synchronized block / method it is used in
* after being called the synchronized block / method it was used in has to be finished to free the lock for previously waiting method (or call wait and freeing it that way)
* it is not guaranteed that the waiting thread will be first to enter the synchronized block after being notified

## ReentrantLock (is of Lock class)
* alternative to synchronized block (start by lock ends by unlock method)
* has ability to make lock fair (granting access to longest waiting thread)
* ability to check if lock is being held
* always put unlock into finally statement
* tryLock method - tries to acquire lock and returns boolean as result
* tryLock(timeout) method - tries to acquire lock for certain amount of time only
* newCondition method - returns Condition object -> has await and signal methods (corresponding to wait and notify methods)
### Condition class
* can be more than one per monitor (difference from synchronized block)
* await (don't  mix up with wait)

## Semaphore class
* semaphore limits number of simultaneous threads that can run
* sempahore with limit one is mutex
* acquire and release methods are used

## Callable and Future (interface and class)
### Callable
* added in java 5 to overcome inablity of Runnable to return value and throw checked exceptions
* call method
### Future
* Future class can be obtained as returned value of submit(Callable) method of Executor service
* allows you to control your threads, checking to see if they’re running or not, waiting for results and even interrupting them or de-scheduling them.
* get waits to call method of Callable to finish and returns its value


## Interrupting Threads
* thread has method interrupt - it will set a flag of thread that has to be checked therefore it doesn't force the thread to quit
* there were methods stop and Thread.stop (wold force thread to stop) but are now deprecated now
* checking for flag - Thread.currentThread().isInterrupted()
* Thread.sleep - when called and thread has been interrupted -> throws InterruptedException
