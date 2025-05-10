package com.teamtea.eclipticseasons.api.util.fast;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Enum2ObjectMap<K extends Enum<K>, V> implements Map<K, V> {
    private final V[] values;
    private final BitSet setFlags;
    private final Class<K> keyType;
    private final V defaultValue;

    public Enum2ObjectMap(Class<K> keyType) {
        this(keyType, null);
    }

    public Enum2ObjectMap(Class<K> keyType, V defaultValue) {
        this.keyType = keyType;
        this.defaultValue = defaultValue;
        K[] constants = keyType.getEnumConstants();
        this.values = (V[]) new Object[constants.length];
        this.setFlags = new BitSet(constants.length);
    }

    @Override
    public V put(K key, V value) {
        this.values[key.ordinal()] = value;
        this.setFlags.set(key.ordinal());
        return value;
    }

    @Override
    public V get(Object key) {
        if (!(keyType.isInstance(key))) return null;
        return get((K) key);
    }

    public V get(K key) {
        return this.setFlags.get(key.ordinal()) ? (V) this.values[key.ordinal()] : defaultValue;
    }

    @Override
    public boolean containsKey(Object key) {
        return keyType.isInstance(key) && this.setFlags.get(((K) key).ordinal());
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < values.length; i++) {
            if (setFlags.get(i) && Objects.equals(values[i], value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V remove(Object key) {
        if (!(keyType.isInstance(key))) return null;
        return remove((K) key);
    }

    public V remove(K key) {
        int ordinal = key.ordinal();
        if (this.setFlags.get(ordinal)) {
            V oldValue = this.values[ordinal];
            this.setFlags.clear(ordinal);
            this.values[ordinal] = null;
            return oldValue;
        }
        return this.defaultValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        this.setFlags.clear();
        Arrays.fill(this.values, null);
    }

    @Override
    public int size() {
        return this.setFlags.cardinality();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public @NotNull Set<K> keySet() {
        Set<K> keys = EnumSet.noneOf(keyType);
        for (K key : keyType.getEnumConstants()) {
            if (this.setFlags.get(key.ordinal())) {
                keys.add(key);
            }
        }
        return keys;
    }

    @Override
    public @NotNull Collection<V> values() {
        List<V> vals = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (this.setFlags.get(i)) {
                vals.add((V) values[i]);
            }
        }
        return vals;
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new HashSet<>();
        for (K key : keyType.getEnumConstants()) {
            if (this.setFlags.get(key.ordinal())) {
                entries.add(new AbstractMap.SimpleEntry<>(key, get(key)));
            }
        }
        return entries;
    }

    public void fill(V value) {
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = value;
            this.setFlags.set(i);
        }
    }

    public Class<K> getKeyType() {
        return this.keyType;
    }
}
