package org.example;

/**
 * using Object wait/notify in synchronized block
 */
public class Birdman {

    public static void main(String[] args) {
        Object feeder = new Object();

        Bird bird1 = new Bird("tweety", feeder);
        Bird bird2 = new Bird("birdy", feeder);
        Bird bird3 = new Bird("elemelek", feeder);

        Farmer farmer = new Farmer(feeder);

        new Thread(bird1).start();
        new Thread(bird2).start();
        new Thread(bird3).start();
        new Thread(farmer).start();
    }

    static class Bird implements Runnable {
        private final String name;
        private final Object feeder;

        public Bird(String name, Object feeder) {
            this.name = name;
            this.feeder = feeder;
        }

        @Override
        public void run() {
            System.out.println("Bird " + name + " is hungry.");
            synchronized (feeder) {
                try {
                    feeder.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Bird " + name + " ate!\n");
        }
    }

    static class Farmer implements Runnable {
        private final Object feeder;

        public Farmer(Object feeder) {
            this.feeder = feeder;
        }

        @Override
        public void run() {
            int i = 10;
            while (i-- > 0) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                synchronized (feeder) {
                    System.out.println("Farmer made a meal!");
                    feeder.notify();
                }
            }
        }
    }
}