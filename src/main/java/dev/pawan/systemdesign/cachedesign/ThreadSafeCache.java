package dev.pawan.systemdesign.cachedesign;

import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeCache<K, V> implements Cache<K, V> {
    private final Cache<K, V> delegate;
    private final ReentrantLock lock = new ReentrantLock();

    public ThreadSafeCache(Cache<K, V> delegate) { this.delegate = delegate; }

    @Override public V get(K key) {
        lock.lock(); try { return delegate.get(key); } finally { lock.unlock(); }
    }
    @Override public void put(K key, V value) {
        lock.lock(); try { delegate.put(key, value); } finally { lock.unlock(); }
    }
    @Override public void remove(K key) {
        lock.lock(); try { delegate.remove(key); } finally { lock.unlock(); }
    }
    @Override public int size() {
        lock.lock(); try { return delegate.size(); } finally { lock.unlock(); }
    }
}
