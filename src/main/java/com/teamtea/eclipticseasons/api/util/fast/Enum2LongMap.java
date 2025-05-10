package com.teamtea.eclipticseasons.api.util.fast;

import java.util.BitSet;

public class Enum2LongMap<K extends Enum<K>> {

    private final long[] values;
    private final BitSet setFlags;
    private final Class<K> keyType;
    private final long defaultValue;

    public Enum2LongMap(Class<K> keyType, long defaultValue) {
        this.keyType = keyType;
        this.defaultValue = defaultValue;
        K[] constants = keyType.getEnumConstants();
        this.values = new long[constants.length];
        this.setFlags = new BitSet(constants.length);
    }

    public void put(K key, long value) {
        this.values[key.ordinal()] = value;
        this.setFlags.set(key.ordinal());
    }

    public long get(K key) {
        return this.setFlags.get(key.ordinal()) ? this.values[key.ordinal()] : defaultValue;
    }

    public boolean contains(K key) {
        return this.setFlags.get(key.ordinal());
    }

    public void put(int index, long value) {
        this.values[index] = value;
        this.setFlags.set(index);
    }

    public long get(int index) {
        return (index >= 0 && index < values.length && this.setFlags.get(index)) ? this.values[index] : defaultValue;
    }

    public long getOrDefault(K key, long defaultValue) {
        return this.setFlags.get(key.ordinal()) ? this.values[key.ordinal()] : defaultValue;
    }

    public long getOrDefault(int index, long defaultValue) {
        return (index >= 0 && index < this.values.length && this.setFlags.get(index)) ? this.values[index] : defaultValue;
    }

    public int size() {
        return this.setFlags.cardinality();
    }

    public Class<K> getKeyType() {
        return this.keyType;
    }

    public long remove(K key) {
        int ordinal = key.ordinal();
        if (this.setFlags.get(ordinal)) {
            long oldValue = this.values[ordinal];
            this.setFlags.clear(ordinal);
            return oldValue;
        }
        return this.defaultValue;
    }

    public long remove(int index) {
        if (index >= 0 && index < this.values.length && this.setFlags.get(index)) {
            long oldValue = this.values[index];
            this.setFlags.clear(index);
            return oldValue;
        }
        return this.defaultValue;
    }

    public void fill(long value) {
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = value;
            this.setFlags.set(i);
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}
