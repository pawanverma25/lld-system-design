package dev.pawan.systemdesign.cachedesign.eviction;

import dev.pawan.systemdesign.cachedesign.util.DoublyLinkedList;

import java.util.HashMap;
import java.util.Map;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {
    protected final DoublyLinkedList<K> list = new DoublyLinkedList<>();
    protected final Map<K, DoublyLinkedList.Node<K>> nodes = new HashMap<>();

    @Override
    public void onAccess(K key) {
        DoublyLinkedList.Node<K> node = nodes.get(key);
        if (node != null) list.moveToFirst(node);
    }

    @Override public void onInsert(K key) {
        if (nodes.containsKey(key)) { onAccess(key); return; }
        nodes.put(key, list.addFirst(key));
    }

    @Override public void onRemove(K key) {
        DoublyLinkedList.Node<K> node = nodes.remove(key);
        if (node != null) list.remove(node);
    }

    @Override public K evict() {
        DoublyLinkedList.Node<K> victim = list.removeLast();
        if (victim == null) return null;
        nodes.remove(victim.key);
        return victim.key;
    }
}