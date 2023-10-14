package org.develop.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyIDGenerator {

    private static MyIDGenerator instance;
    private static long id = 0;

    private static final Lock locker = new ReentrantLock(true);

    private MyIDGenerator(){}

    public static MyIDGenerator getInstance(){
        if (instance == null){
            instance = new MyIDGenerator();
        }
        return instance;
    }

    public Long getIDandIncrement(){
        locker.lock();
        id++;
        locker.unlock();
        return id;
    }
}
