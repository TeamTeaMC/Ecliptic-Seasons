package com.teamtea.eclipticseasons.api.util.fast;

import org.jspecify.annotations.NonNull;

import java.util.BitSet;

public class Enum2IntMap<K extends Enum<K>> {

    private final int[] values;
    private final BitSet setFlags;
    private final Class<K> keyType;
    private final int defaultValue;

    public Enum2IntMap(Class<K> keyType) {
        this(keyType, -1);
    }

    public Enum2IntMap(Class<K> keyType, int defaultValue) {
        this.keyType = keyType;
        this.defaultValue = defaultValue;
        K[] constants = keyType.getEnumConstants();
        this.values = new int[constants.length];
        this.setFlags = new BitSet(constants.length);
    }

    public void put(K key, int value) {
        this.values[key.ordinal()] = value;
        this.setFlags.set(key.ordinal());
    }

    public int get(K key) {
        return this.setFlags.get(key.ordinal()) ? this.values[key.ordinal()] : defaultValue;
    }

    public boolean contains(K key) {
        return this.setFlags.get(key.ordinal());
    }

    public void put(int index, int value) {
        this.values[index] = value;
        this.setFlags.set(index);
    }

    public int get(int index) {
        return (index >= 0 && index < values.length && this.setFlags.get(index)) ? this.values[index] : defaultValue;
    }

    public int getOrDefault(K key, int defaultValue) {
        return this.setFlags.get(key.ordinal()) ? this.values[key.ordinal()] : defaultValue;
    }

    public int getOrDefault(int index, int defaultValue) {
        return (index >= 0 && index < this.values.length && this.setFlags.get(index)) ? this.values[index] : defaultValue;
    }

    public int size() {
        return this.setFlags.cardinality();
    }

    public Class<K> getKeyType() {
        return this.keyType;
    }

    public int remove(K key) {
        int ordinal = key.ordinal();
        if (this.setFlags.get(ordinal)) {
            int oldValue = this.values[ordinal];
            this.setFlags.clear(ordinal);
            return oldValue;
        }
        return this.defaultValue;
    }

    public int remove(int index) {
        if (index >= 0 && index < this.values.length && this.setFlags.get(index)) {
            int oldValue = this.values[index];
            this.setFlags.clear(index);
            return oldValue;
        }
        return this.defaultValue;
    }

    public void fill(int value) {
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = value;
            this.setFlags.set(i);
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void putAll(@NonNull Enum2IntMap<K> newWeights) {
        for (int i = newWeights.setFlags.nextSetBit(0); i >= 0; i = newWeights.setFlags.nextSetBit(i + 1)) {
            this.put(i, newWeights.values[i]);
        }
    }
}
