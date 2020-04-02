package org.example;

public class Birdman {

    public static void main(String[] args) {
        Object feeder = new Object();

        Thread bird1 = new Thread(new Bird("tweety", feeder));
        Thread bird2 = new Thread(new Bird("birdy", feeder));
        Thread bird3 = new Thread(new Bird("elemelek", feeder));

        bird1.start();
        bird2.start();
        bird3.start();

        Thread farmer = new Thread(new Farmer(feeder));
        farmer.start();
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