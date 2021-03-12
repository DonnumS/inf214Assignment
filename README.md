## INF214 Mandatory Assignment 2

### Sebastian Dønnum

# Task 1

## Description of Java concurrency classes/features used

For each row in the matrix we create a new thread. This thread will handle the multiplication of a row.

The `waitForThreads(threads);` will wait for 10 threads to finish before the list of threads is cleared.

The `RowMultiplierTask.java` implements `Runnable`. `Runnable` is used to make sure that instances of the class can be executed by threads.

Since the `RowMultiplierTask` implements Runnable it need a run() function. When the thread is started this method is executed.

## Report of speed up

Used `System.nanoTime()` to record the running time for serial multiplication and the row multiplication. Below is a table with avg runtime given different size amtrixes for both Serial and RowMultiplierTask.

| Size      | Serial Avg | RowMultiplier Avg |
| --------- | :--------: | ----------------: |
| 100x100   |   0.0157   |            0.0291 |
| 250x250   |   0.0444   |            0.0725 |
| 500x500   |   0.3244   |            0.2722 |
| 1000x1000 |   3.2744   |            2.5175 |

As we can see from this table, the concurrent solution for matrix multiplication is faster than the serial multiplication when we are working with bigger matrixes. The serial solution is faster when using smaller matrixes.

# Task 2

## 2.1

Here I can see that we use `invertedIndex` which is a `ConcurrentHashMap`. A `ConcurrentHashMap` is thread-safe, since multiple threads can operate on it without problems.

## 2.2

The first thing I can see is that the `IndexingTask` implements `Callable`. `Callable` is similar to `Runnable`, but it returns a result. We have a `call()`function instead of a `run()`function. In this case `call()` returns an object in the form of `Document`

## 2.3

`InvertedIndexingTask` implements `Runnable`. `Runnable` is used when an instance of a class is to be executed by a thread.

`run()` is needed since `InvertedIndexTask` implements the `Runnable` interface. This `run()` function is executed when the Thread is started.

At the start of the `run()`function we see `while(!Thread.interrupted())`. If the thread is not interrupted, the `completionService` will take the task. If the thread was interrupted, it will try to take a task using `Future<Document> future = completionService.poll();` and `Document document = future.get();`.

`Future` interface represents the value from an asynchronous computation. It has methods that check if this computation has been done.

In both cases when it has a task, it will update the invertedIndex using `updateInvertedIndex(document.getVoc(), document.getFileName());`.

## 2.4

Declaration of the `ConcurrentHashMap` written about earlier happens here.

`int numcores` gets the number of cores of the processor using `Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);`. So it is guaranteed to be at least 1.

`ThreadPoolExecutor executor` executes a given task using one of its pooled threads.

`completionService = new ExecutorCompletionService<>(executor);` is a CompletionService supplied with the executor above to handle/execute tasks. A CompletionService separates the production of asynchronous tasks from the consumption of results from tasks that have been completed.

Two different threads are started.

Then iterates over all files in the list. For each of these files we initialize a new `IndexingTask` and send this task to the completion service using `completionService.submit(task);`

Then we check the executor queue using `executor.getQueue().size() > 1000`. If there is queued more than a 1000 tasks, the thread sleeps

After this the `executor` is shutdown. `executor.awaitTermination(1, TimeUnit.DAYS);` blocks until all tasks have completed execution or wait for the timeout (in this case this is 1 day).

`thread1.interrupt(); thread2.interrupt();` interrupts both threads by setting the interrupted flag on the threads.

`thread1.join();` - waits for this thread to die.

A few places we can see `InterruptedException`. This is thrown when a thread is waiting, sleeping or interrupted.

# Task 3

## 3.1

For n processes executing m atomic actions we can compute the number of different executions using:

(n\*m)! / m! ^ n

In our case we have 3 processes each performing 2 atomic actions giving the following result:

(3\*2)! / 2! ^ 3

== 6! / 2 ^ 3

== 720 / 8

== 90

Therefore we know that we can get 90 different results.

## 3.2

Print the numbers in order. We need P2 to wait for P1 and P3 to wait for P2. Therefore we need two semaphores
(named sem1 and sem2)

Here is the program:

```
printer() {
    sem sem1 = 0;
    sem sem2 = 0;

    Process P1 {
      write("1"); write("2");
      V(sem1);
    }

    Process P2 {
      P(sem1);
      write("3"); write("4");
      V(sem2);
    }

    Process P3 {
      P(sem2);
      write("5"); write("6");
    }
}
```

# Task 4

## 4.2

```
printer() {
    Sem sem = new Sem(1) // Allow a maximum of 1 thread at the time

    Process P1 {
      sem.acquire();
      write("1"); write("2");
      sem.release();
    }

    Process P2 {
      sem.acquire();
      write("3"); write("4");
      sem.release();
    }

    Process P3 {
      sem.aqcuire();
      write("5"); write("6");
      sem.release();
    }
}
```

# Task 5

## Description of imports

- Cyclic Barrier

  A cyclic barrier is used to make threads wait for each other at specific points in execution before they continue. When all threads have met the barrier, they are released and the barrier can be used again (therefore cyclic)

- BrokenBarrierException

  A BrokenBarrierException is thrown when a thread is waiting at a barrier that gets broken or tries to wait at an already broken barrier. A barrier can enter the broken state if any thread waiting is interrupted, causing all waiting threads to throw the exception.
