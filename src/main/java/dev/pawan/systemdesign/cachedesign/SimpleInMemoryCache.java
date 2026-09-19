package dev.pawan.systemdesign.cachedesign;

import dev.pawan.systemdesign.cachedesign.eviction.EvictionPolicy;
import dev.pawan.systemdesign.cachedesign.exception.StorageFullException;
import dev.pawan.systemdesign.cachedesign.storage.Storage;

class SimpleInMemoryCache<K, V> implements Cache<K, V> {
    private final Storage<K, V> storage;
    private final EvictionPolicy<K> evictionPolicy;

    public SimpleInMemoryCache(Storage<K, V> storage, EvictionPolicy<K> evictionPolicy) {
        this.storage = storage;
        this.evictionPolicy = evictionPolicy;
    }

    /** Returns null on miss instead of throwing — a miss is normal, not exceptional. */
    @Override public V get(K key) {
        if (!storage.contains(key)) return null;
        V value = storage.get(key);
        evictionPolicy.onAccess(key);
        return value;
    }

    @Override public void put(K key, V value) {
        if (storage.contains(key)) {          // update path
            storage.add(key, value);
            evictionPolicy.onAccess(key);
            return;
        }
        if (storage.isFull()) {               // make room first
            K victim = evictionPolicy.evict();
            if (victim == null) throw new StorageFullException("Nothing to evict");
            storage.remove(victim);
        }
        storage.add(key, value);
        evictionPolicy.onInsert(key);
    }

    @Override public void remove(K key) {
        if (!storage.contains(key)) return;
        storage.remove(key);
        evictionPolicy.onRemove(key);
    }

    @Override public int size() { return storage.size(); }
}
