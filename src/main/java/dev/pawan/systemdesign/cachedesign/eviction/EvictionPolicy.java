package dev.pawan.systemdesign.cachedesign.eviction;

public interface EvictionPolicy<K> {
    void onInsert(K key);
    void onAccess(K key);
    void onRemove(K key);
    K evict();
}
