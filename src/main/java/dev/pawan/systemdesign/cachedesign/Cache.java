package dev.pawan.systemdesign.cachedesign;

import dev.pawan.systemdesign.cachedesign.config.CacheConfig;

public interface Cache<K, V>{
    V get(K key);
    void put(K key, V value);
    void remove(K key);
    int size();
}
