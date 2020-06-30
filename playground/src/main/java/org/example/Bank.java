package org.example;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * using Lock
 */
public class Bank {
    private int funds = 100;
    private final Lock lock = new ReentrantLock();

    public void deposit(int money) {
        lock.lock();
        try {
            funds += money;
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int money) {
        lock.lock();
        try {
            funds -= money;
        } finally {
            lock.unlock();
        }
    }
}
