package org.example;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumer {
    private final static int PRODUCTION_CAPACITY = 20;
    private final static int SUPPLY_CHAIN_CAPACITY = 2;

    public static void main(String[] args) {
        BlockingQueue<Object> supplyChain = new ArrayBlockingQueue<>(SUPPLY_CHAIN_CAPACITY);

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
        private final BlockingQueue<Object> supplyChain;

        public Producer(BlockingQueue<Object> supplyChain) {
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
        private final BlockingQueue<Object> supplyChain;

        public Consumer(String id, BlockingQueue<Object> supplyChain) {
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
