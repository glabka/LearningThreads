package producer_processor_saver;

import java.util.LinkedList;
import java.util.Queue;

public class Logic {

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

    public Logic(int maxCount, int maxLoadStorage, int maxProcessedStorage, int loadTime, int processingTime,
                 int storeTime) {
        this.maxCount = maxCount;
        this.maxLoadStorage = maxLoadStorage;
        this.maxProcessedStorage = maxProcessedStorage;
        this.loadTime = loadTime;
        this.processingTime = processingTime;
        this.storeTime = storeTime;
    }

    public void loadAll() {
        int counter = 0;
        boolean doLoad = true;
        Integer loadedVal = null;
        while (counter < maxCount) {
            if (doLoad) {
                loadedVal = load();
            }

            // insert in loadingQueue
            synchronized(loadingQueue) {
                if (loadingQueue.size() < maxLoadStorage) {
                    loadingQueue.add(loadedVal);
                    doLoad = true;
                    counter++;
                    loadingQueue.notify(); // notify processing
                } else {
                    try {
                        loadingQueue.notify(); // notify processing
                        loadingQueue.wait(); // release the lock
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        doLoad = false;
                    }
                }
            }
        }
    }

    private int loadingCounter = 0;

    private int load() {
        System.out.println("Loading: " + loadingCounter);

        try {
            Thread.sleep(loadTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return loadingCounter++;
    }

    public void processAll() {
        int counter = 0;
        boolean doSave = false;
        String processedResult = null;

        while (counter < maxCount) {
            // check if loaded part & processing part
            Integer loadedData;
            if (!doSave) {
                synchronized (loadingQueue) {
                    if (!loadingQueue.isEmpty()) {
                        loadedData = loadingQueue.remove();
                        loadingQueue.notify();
                    } else {
                        loadingQueue.notify();
                        try {
                            loadingQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            continue;
                        }
                    }
                }

                // processing part
                processedResult = process(loadedData);
                doSave = true;
            }

            // write for storing part
            if (doSave) {
                synchronized (processedQueue) {
                    if (processedQueue.size() < maxProcessedStorage) {
                        processedQueue.add(processedResult);
                        doSave = false;
                        counter++;
                        processedQueue.notify();
                    } else {
                        processedQueue.notify();
                        try {
                            processedQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private String process(Integer num) {
        System.out.println("Processing: " + num);
        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return num.toString();
    }

    public void storeAll() {
        int counter = 0;
        while (counter < maxCount) {
            String valueToBeStored = null;
            synchronized (processedQueue) {
                if (!processedQueue.isEmpty()) {
                    valueToBeStored = processedQueue.remove();
                    counter++;
                    processedQueue.notify();
                } else {
                    processedQueue.notify();
                    try {
                        processedQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        continue;
                    }
                }
            }
            store(valueToBeStored);
        }
    }

    private void store(String str) {
        System.out.println("Storing: " + str);
        try {
            Thread.sleep(storeTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
