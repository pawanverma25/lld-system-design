package dev.pawan.systemdesign.cachedesign;

import dev.pawan.systemdesign.cachedesign.enums.EvictionPolicyType;
import dev.pawan.systemdesign.cachedesign.eviction.EvictionPolicy;
import dev.pawan.systemdesign.cachedesign.eviction.FIFOEvictionPolicy;
import dev.pawan.systemdesign.cachedesign.eviction.LFUEvictionPolicy;
import dev.pawan.systemdesign.cachedesign.eviction.LRUEvictionPolicy;
import dev.pawan.systemdesign.cachedesign.storage.HashMapStorage;
import dev.pawan.systemdesign.cachedesign.storage.Storage;

public class CacheFactory {
    public static <K, V> Cache<K,V> createCache(int capacity, EvictionPolicyType evictionPolicyType){
        EvictionPolicy<K> policy = switch (evictionPolicyType) {
            case LFU -> new LFUEvictionPolicy<K>();
            case LRU -> new LRUEvictionPolicy<K>();
            case FIFO -> new FIFOEvictionPolicy<K>();
            default -> throw new RuntimeException("Np such policy type");
        };
        Storage<K, V> storage = new HashMapStorage<K, V>(capacity);
        return new SimpleInMemoryCache<K, V>(storage, policy);
    }
}
