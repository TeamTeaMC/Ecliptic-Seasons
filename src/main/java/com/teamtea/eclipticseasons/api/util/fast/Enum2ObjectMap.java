package com.teamtea.eclipticseasons.api.util.fast;

import org.jspecify.annotations.NonNull;

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
        Arrays.fill(this.values, defaultValue);
        this.setFlags = new BitSet(constants.length);
    }

    public Enum2ObjectMap(EnumMap<K, V> m, Class<K> keyType) {
        this(keyType, null);
        this.putAll(m);
    }

    public Enum2ObjectMap(Enum2ObjectMap<K, V> m) {
        this(m.getKeyType());
        this.putAll(m);
    }


    public Enum2ObjectMap(Map<K, V> m) {
        this(m instanceof Enum2ObjectMap em ? em.getKeyType() : ((Enum) m.keySet().iterator().next()).getDeclaringClass());
        this.putAll(m);
    }

    @Override
    public V put(K key, V value) {
        this.values[key.ordinal()] = value;
        this.setFlags.set(key.ordinal());
        return value;
    }

    @Override
    public V get(Object key) {
        // if (!(keyType.isInstance(key))) return null;
        return get((K) key);
    }

    public V get(K key) {
        return this.values[key.ordinal()];
    }

    @Override
    public boolean containsKey(Object key) {
        return
                // keyType.isInstance(key) &&
                this.values[((K) key).ordinal()] != defaultValue;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < values.length; i++) {
            if (Objects.equals(values[i], value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V remove(Object key) {
        // if (!(keyType.isInstance(key))) return null;
        return remove((K) key);
    }

    public V remove(K key) {
        int ordinal = key.ordinal();
        V oldValue = this.values[ordinal];
        this.values[ordinal] = null;
        this.setFlags.clear(ordinal);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        Arrays.fill(this.values, defaultValue);
        this.setFlags.clear();
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
    public @NonNull Set<K> keySet() {
        Set<K> keys = EnumSet.noneOf(keyType);
        for (K key : keyType.getEnumConstants()) {
            if (this.values[key.ordinal()] != defaultValue) {
                keys.add(key);
            }
        }
        return keys;
    }

    @Override
    public @NonNull Collection<V> values() {
        List<V> vals = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (this.values[i] != defaultValue) {
                vals.add(values[i]);
            }
        }
        return vals;
    }

    @Override
    public @NonNull Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new LinkedHashSet<>();
        for (K key : keyType.getEnumConstants()) {
            if (this.values[key.ordinal()] != defaultValue) {
                entries.add(new AbstractMap.SimpleEntry<>(key, get(key)));
            }
        }
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enum2ObjectMap<?, ?> that = (Enum2ObjectMap<?, ?>) o;
        return Objects.deepEquals(values, that.values)
                && Objects.equals(setFlags, that.setFlags)
                && Objects.equals(keyType, that.keyType)
                && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(values), setFlags, keyType, defaultValue);
    }

    public Class<K> getKeyType() {
        return this.keyType;
    }
}
