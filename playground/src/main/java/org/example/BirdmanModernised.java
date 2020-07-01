package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * intra-thread communication - feeding birds
 * using Lock instead of synchronized
 * using Condition wait/signal instead of Object wait/notify
 */
public class BirdmanModernised {

    public static void main(String[] args) {
        final Lock lock = new ReentrantLock();
        final Condition feeder = lock.newCondition();

        Bird bird1 = new Bird("tweety", lock, feeder);
        Bird bird2 = new Bird("birdy", lock, feeder);
        Bird bird3 = new Bird("elemelek", lock, feeder);

        Farmer farmer = new Farmer(lock, feeder);

        new Thread(bird1).start();
        new Thread(bird2).start();
        new Thread(bird3).start();
        new Thread(farmer).start();
    }

    static class Bird implements Runnable {
        private final String name;
        private final Lock lock;
        private final Condition feeder;

        public Bird(String name, Lock lock, Condition feeder) {
            this.name = name;
            this.lock = lock;
            this.feeder = feeder;
        }

        @Override
        public void run() {
            System.out.println("Bird " + name + " is hungry.");
            lock.lock();
            try {
                feeder.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println("Bird " + name + " ate!\n");
        }
    }

    static class Farmer implements Runnable {
        private final Lock lock;
        private final Condition feeder;

        public Farmer(Lock lock, Condition feeder) {
            this.lock = lock;
            this.feeder = feeder;
        }

        @Override
        public void run() {
            int i = 5;
            while (i-- > 0) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                lock.lock();
                try {
                    System.out.println("Farmer made a meal!");
                    feeder.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}