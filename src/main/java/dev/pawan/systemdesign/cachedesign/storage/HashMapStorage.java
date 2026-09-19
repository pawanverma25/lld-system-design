package dev.pawan.systemdesign.cachedesign.storage;

import dev.pawan.systemdesign.cachedesign.exception.KeyNotFoundException;
import dev.pawan.systemdesign.cachedesign.exception.StorageFullException;

import java.util.HashMap;
import java.util.Map;

public class HashMapStorage<K, V> implements Storage<K, V> {
    private final Map<K, V> map;
    private final int capacity;

    public HashMapStorage(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
        this.capacity = capacity;
        this.map = new HashMap<>();
    }

    @Override public void add(K key, V value) {
        if (!map.containsKey(key) && isFull())
            throw new StorageFullException("Storage is full");
        map.put(key, value);
    }

    @Override public V get(K key) {
        if (!map.containsKey(key)) throw new KeyNotFoundException("Key not found: " + key);
        return map.get(key);
    }

    @Override public void remove(K key) { map.remove(key); }
    @Override public boolean contains(K key) { return map.containsKey(key); }
    @Override public boolean isFull() { return map.size() >= capacity; }
    @Override public int size() { return map.size(); }
}