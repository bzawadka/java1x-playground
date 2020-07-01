package org.example;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue<T> {
    private final static int PRODUCTION_CAPACITY = 20;
    private final static int QUEUE_CAPACITY = 2;

    private final Queue<T> storage;
    private final int maxSize;
    private final Lock lock = new ReentrantLock();
    private final Condition added = lock.newCondition();
    private final Condition removed = lock.newCondition();

    public MyBlockingQueue(int size) {
        maxSize = size;
        storage = new LinkedList<T>();
    }

    private void put(T item) throws InterruptedException {
        lock.lock();
        try {
            // if storage capacity reached, wait for removing an item
            if (storage.size() == maxSize) {
                removed.await();
            }
            storage.add(item);
            added.signal();

        } finally {
            lock.unlock();
        }
    }

    private T take() throws InterruptedException {
        lock.lock();
        try {
            // if storage is empty, wait for adding new element
            if (storage.isEmpty()) {
                added.await();
            }
            T item = storage.remove();
            removed.signal();
            return item;

        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        MyBlockingQueue<Object> supplyChain = new MyBlockingQueue<>(QUEUE_CAPACITY);

        Thread producer = new Thread(new Producer(supplyChain));
        Thread[] consumers = {
                new Thread(new Consumer("consumer1", supplyChain)),
                new Thread(new Consumer("consumer2", supplyChain)),
                new Thread(new Consumer("consumer3", supplyChain))
        };

        producer.start();
        Arrays.stream(consumers).forEach(Thread::start);
    }

    public static class Producer implements Runnable {
        private final MyBlockingQueue<Object> supplyChain;

        public Producer(MyBlockingQueue<Object> supplyChain) {
            this.supplyChain = supplyChain;
        }

        @Override
        public void run() {
            int i = PRODUCTION_CAPACITY;
            while (i-- > 0) {
                try {
                    Thread.sleep(500);
                    String item = "mask" + i;
                    System.out.println(item + " produced");
                    supplyChain.put(item);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Consumer implements Runnable {
        private final String id;

        private final MyBlockingQueue<Object> supplyChain;

        public Consumer(String id, MyBlockingQueue<Object> supplyChain) {
            this.id = id;
            this.supplyChain = supplyChain;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Object item = supplyChain.take();
                    System.out.println(item + " received by " + id + "\n");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
