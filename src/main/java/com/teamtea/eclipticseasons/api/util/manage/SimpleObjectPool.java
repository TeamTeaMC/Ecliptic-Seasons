package com.teamtea.eclipticseasons.api.util.manage;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleObjectPool<T extends AutoCloseable> {
    private final ConcurrentLinkedQueue<T> pool = new ConcurrentLinkedQueue<>();
    private final ObjectFactory<T> factory;
    private final int maxSize;

    public interface ObjectFactory<T> {
        T create() throws Exception;
    }

    public SimpleObjectPool(ObjectFactory<T> factory, int maxSize) {
        this.factory = factory;
        this.maxSize = maxSize;
    }

    public T acquire() throws Exception {
        T obj = pool.poll();
        return (obj == null) ? factory.create() : obj;
    }

    public void release(T obj) {
        if (obj == null) return;
        if (pool.size() < maxSize) {
            pool.offer(obj);
        } else {
            try {
                obj.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int size() {
        return pool.size();
    }

    public void closeAll() {
        while (!pool.isEmpty()) {
            try {
                pool.poll().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
