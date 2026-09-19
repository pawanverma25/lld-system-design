package dev.pawan.systemdesign.cachedesign.eviction;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {
    private final Map<K, Integer> keyToFreq = new HashMap<>();
    // LinkedHashSet preserves insertion order => LRU tie-break within a frequency
    private final Map<Integer, LinkedHashSet<K>> freqToKeys = new HashMap<>();
    private int minFreq = 0;

    @Override
    public void onInsert(K key) {
        if (keyToFreq.containsKey(key)) {
            onAccess(key);
            return;
        }
        keyToFreq.put(key, 1);
        freqToKeys.computeIfAbsent(1, f -> new LinkedHashSet<>()).add(key);
        minFreq = 1;
    }

    @Override
    public void onAccess(K key) {
        Integer freq = keyToFreq.get(key);
        if (freq == null) return;
        detach(key, freq);
        int next = freq + 1;
        keyToFreq.put(key, next);
        freqToKeys.computeIfAbsent(next, f -> new LinkedHashSet<>()).add(key);
        if (minFreq == freq && !freqToKeys.containsKey(freq)) minFreq = next;
    }

    @Override
    public void onRemove(K key) {
        Integer freq = keyToFreq.remove(key);
        if (freq == null) return;
        detach(key, freq);
        recomputeMinFreqIfStale();
    }

    @Override
    public K evict() {
        recomputeMinFreqIfStale();
        LinkedHashSet<K> bucket = freqToKeys.get(minFreq);
        if (bucket == null || bucket.isEmpty()) return null;
        K victim = bucket.iterator().next(); // oldest among the least frequent
        bucket.remove(victim);
        if (bucket.isEmpty()) freqToKeys.remove(minFreq);
        keyToFreq.remove(victim);
        return victim;
    }

    private void detach(K key, int freq) {
        LinkedHashSet<K> bucket = freqToKeys.get(freq);
        if (bucket == null) return;
        bucket.remove(key);
        if (bucket.isEmpty()) freqToKeys.remove(freq);
    }

    /**
     * Only needed after explicit removes/evicts; the hot path stays O(1).
     */
    private void recomputeMinFreqIfStale() {
        if (freqToKeys.containsKey(minFreq)) return;
        minFreq = freqToKeys.isEmpty() ? 0 : Collections.min(freqToKeys.keySet());
    }
}
