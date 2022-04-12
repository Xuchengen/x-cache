package com.github.xuchengen.cache;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache<K, V> {

    private Map<K, V> keyMappingValue;
    private Map<K, Integer> keyMappingCount;
    private Map<Integer, LinkedHashSet<K>> countMappingKey;
    private int capacity;
    private int minCount;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.keyMappingValue = new HashMap<>();
        this.keyMappingCount = new HashMap<>();
        this.countMappingKey = new HashMap<>();

        this.minCount = 1;
        this.countMappingKey.putIfAbsent(1, new LinkedHashSet<>());
    }

    public V get(K key) {
        if (!keyMappingValue.containsKey(key)) {
            return null;
        }
        update(key);
        return keyMappingValue.get(key);
    }

    private void update(K key) {
        int count = keyMappingCount.get(key);
        int newCount = count + 1;
        keyMappingCount.put(key, newCount);
        countMappingKey.putIfAbsent(newCount, new LinkedHashSet<>());
        countMappingKey.get(newCount).add(key);

        LinkedHashSet<K> keySet = countMappingKey.get(count);
        keySet.remove(key);

        if (keySet.isEmpty()) {
            countMappingKey.remove(count);
            if (minCount == count) {
                minCount++;
            }
        }
    }

    public void put(K key, V value) {
        if (keyMappingValue.containsKey(key)) {
            keyMappingValue.put(key, value);
            update(key);
            return;
        }

        if (capacity <= keyMappingValue.size()) {
            LinkedHashSet<K> minKeySet = countMappingKey.get(minCount);
            K minKey = minKeySet.iterator().next();
            minKeySet.remove(minKey);
            keyMappingValue.remove(minKey);
            keyMappingCount.remove(minKey);
            if (minKeySet.isEmpty()) {
                countMappingKey.remove(minCount);
            }
        }

        keyMappingValue.put(key, value);
        keyMappingCount.put(key, 1);
        countMappingKey.putIfAbsent(1, new LinkedHashSet<>());
        countMappingKey.get(1).add(key);
    }

    public static void main(String[] args) {
        LFUCache<Integer, Integer> cache = new LFUCache<Integer, Integer>(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(2));
        cache.put(3, 3);
        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
    }

}
