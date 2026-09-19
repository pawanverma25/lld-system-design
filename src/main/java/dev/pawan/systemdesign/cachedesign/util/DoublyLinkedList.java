package dev.pawan.systemdesign.cachedesign.util;

public class DoublyLinkedList<K> {
    public static class Node<K> {
        public final K key;
        public Node<K> prev, next;
        Node(K key) { this.key = key; }
    }

    private final Node<K> head = new Node<>(null); // sentinel
    private final Node<K> tail = new Node<>(null); // sentinel

    public DoublyLinkedList() { head.next = tail; tail.prev = head; }

    public Node<K> addFirst(K key) {
        Node<K> node = new Node<>(key);
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
        return node;
    }

    public void remove(Node<K> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = node.next = null;
    }

    public void moveToFirst(Node<K> node) {
        remove(node);
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public Node<K> removeLast() {
        if (isEmpty()) return null;
        Node<K> last = tail.prev;
        remove(last);
        return last;
    }

    boolean isEmpty() { return head.next == tail; }
}
