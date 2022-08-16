package producer_processor_saver;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogicMultiple {

    /**
     * Queue to emulate loading from disk.
     */
    private Queue<Integer> loadingQueue = new LinkedList<>();

    /**
     * Queue to emulate processed data ready for storing.
     */
    private Queue<String> processedQueue = new LinkedList<>();

    private int maxCount;
    private int maxLoadStorage;
    private int maxProcessedStorage;
    private int loadTime;
    private int processingTime;
    private int storeTime;

    private Lock loadedResourcesLock = new ReentrantLock();
    private Lock processedDataLock = new ReentrantLock();
    private Object maxCountLoadingLock = new Object();
    private Object maxCountProcessingLock = new Object();
    private Object maxCountStoreLock = new Object();

    private Condition nonEmptyLoadCondition = loadedResourcesLock.newCondition();
    private Condition notFullLoadCondition = loadedResourcesLock.newCondition();
    private Condition nonEmptyProcessedCondtion = processedDataLock.newCondition();
    private Condition notFullProcessedCodntion = processedDataLock.newCondition();


    public LogicMultiple(int maxCount, int maxLoadStorage, int maxProcessedStorage, int loadTime, int processingTime,
                 int storeTime) {
        this.maxCount = maxCount;
        this.maxLoadStorage = maxLoadStorage;
        this.maxProcessedStorage = maxProcessedStorage;
        this.loadTime = loadTime;
        this.processingTime = processingTime;
        this.storeTime = storeTime;
    }

    private Integer loadNext() {
        synchronized (maxCountLoadingLock) {
            if (counterLoading >= maxCount) {
                return null;
            } else {
                counterLoading++;
            }
        }
        return load(counterLoading - 1);
    }

    private volatile int counterLoading = 0;

    public void loadAll() {
        boolean doLoad = false;
        Integer firstLoadedVal= loadNext();
        Integer loadedVal = firstLoadedVal;
        while (firstLoadedVal != null) {
            if (doLoad && (loadedVal = loadNext()) == null) {
                break;
            }

            // insert in loadingQueue
            loadedResourcesLock.lock();
            if (loadingQueue.size() < maxLoadStorage) {
                loadingQueue.add(loadedVal);
                doLoad = true;
                nonEmptyLoadCondition.signalAll();
                loadedResourcesLock.unlock();
                continue;
            } else {
                doLoad = false;
                nonEmptyLoadCondition.signalAll();
                loadedResourcesLock.unlock();
            }
        }
    }

    private int load(int i) {
        System.out.println("Thread (" + Thread.currentThread().getName() + ") Loading: " + i);

        try {
            Thread.sleep(loadTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return i;
    }

    private boolean isMaxProcessingCount() {
        synchronized (maxCountProcessingLock) {
            if (counterProcessing < maxCount) {
                counterProcessing++;
                return false;
            } else {
                return true;
            }
        }
    }

    private volatile int counterProcessing = 0;
    public void processAll() {
        boolean doSave = false;
        String processedResult = null;
        boolean skipCount = false;

        while (skipCount || !isMaxProcessingCount()) {
            // check if loaded part & processing part
            Integer loadedData;
            if (!doSave) {
                loadedResourcesLock.lock();
                if (!loadingQueue.isEmpty()) {
                    loadedData = loadingQueue.remove();
                    notFullLoadCondition.signalAll();
                    loadedResourcesLock.unlock();
                } else {
                    notFullLoadCondition.signalAll();
                    try {
                        nonEmptyLoadCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        loadedResourcesLock.unlock();
                        skipCount = true;
                        continue;
                    }
                }

                // processing part
                processedResult = process(loadedData);
                doSave = true;
            }

            // write for storing part
            if (doSave) {
                processedDataLock.lock();
                if (processedQueue.size() < maxProcessedStorage) {
                    processedQueue.add(processedResult);
                    doSave = false;
                    nonEmptyProcessedCondtion.signalAll();
                    skipCount = false;
                } else {
                    nonEmptyProcessedCondtion.signalAll();
                    try {
                        notFullProcessedCodntion.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    skipCount = true;
                }
                processedDataLock.unlock();
            }
        }
    }

    private String process(Integer num) {
        System.out.println("Thread (" + Thread.currentThread().getName() + ") Processing: " + num);
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return num.toString();
    }

    private boolean isMaxStoreCount() {
        synchronized (maxCountStoreLock) {
            if (counterStoring < maxCount) {
                counterStoring++;
                return false;
            } else {
                return true;
            }
        }
    }

    private volatile int counterStoring = 0;
    public void storeAll() {
        boolean skipCount = false;
        while (skipCount || !isMaxStoreCount()) {
            String valueToBeStored;
            processedDataLock.lock();
            if (!processedQueue.isEmpty()) {
                valueToBeStored = processedQueue.remove();
                notFullProcessedCodntion.signalAll();
                processedDataLock.unlock();
                store(valueToBeStored);
                skipCount = false;
            } else {
                notFullProcessedCodntion.signalAll();
                try {
                    nonEmptyProcessedCondtion.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                processedDataLock.unlock();
                skipCount = true;
            }
        }
    }

    private void store(String str) {
        System.out.println("Thread (" + Thread.currentThread().getName() + ") Storing: " + str);
        try {
            Thread.sleep(storeTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
