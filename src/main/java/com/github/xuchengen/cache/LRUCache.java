package com.github.xuchengen.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LRUCache<K, V> {

    private Entry<K, V> head;
    private Entry<K, V> tail;
    private Map<K, Entry<K, V>> cache;
    private int capacity;
    private int size;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        this.cache = new HashMap<>();
        this.head = new Entry<>();
        this.tail = new Entry<>();

        head.next = tail;
        tail.pre = head;
    }

    public V get(K key) {
        Entry<K, V> entry = cache.get(key);
        if (Objects.isNull(entry)) {
            return null;
        }

        moveToHead(entry);
        return entry.value;
    }

    public void put(K key, V value) {
        Entry<K, V> entry = cache.get(key);
        if (Objects.nonNull(entry)) {
            entry.value = value;
            cache.put(key, entry);
            moveToHead(entry);
            return;
        }

        if (capacity <= size) {
            Entry<K, V> lastEntry = tail.pre;
            cache.remove(lastEntry.key);
            remove(lastEntry);
            size--;
        }

        Entry<K, V> newEntry = new Entry<>(key, value);
        cache.put(key, newEntry);
        add(newEntry);
        size++;
    }

    private void moveToHead(Entry<K, V> entry) {
        remove(entry);
        add(entry);
    }

    private void add(Entry<K, V> entry) {
        head.next.pre = entry;
        entry.next = head.next;

        head.next = entry;
        entry.pre = head;
    }

    private void remove(Entry<K, V> entry) {
        entry.pre.next = entry.next;
        entry.next.pre = entry.pre;
    }

    private static class Entry<K, V> {
        private Entry<K, V> pre;
        private Entry<K, V> next;
        private K key;
        private V value;

        public Entry() {
        }

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public static void main(String[] args) {
        LRUCache<Integer, Integer> cache = new LRUCache<>(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(2));
        cache.put(3, 3);
        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
    }
}
