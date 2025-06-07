package com.teamtea.eclipticseasons.api.util.fast;

import java.util.BitSet;


public class Enum2FloatMap<K extends Enum<K>> {

    private final float[] values;
    private final BitSet setFlags;
    private final Class<K> keyType;
    private final float defaultValue;

    public Enum2FloatMap(Class<K> keyType) {
        this(keyType, Float.NaN);
    }

    public Enum2FloatMap(Class<K> keyType, float defaultValue) {
        this.keyType = keyType;
        this.defaultValue = defaultValue;
        K[] constants = keyType.getEnumConstants();
        this.values = new float[constants.length];
        this.setFlags = new BitSet(constants.length);
    }

    public void put(K key, float value) {
        this.values[key.ordinal()] = value;
        this.setFlags.set(key.ordinal());
    }

    public float get(K key) {
        if (this.setFlags.get(key.ordinal())) {
            return this.values[key.ordinal()];
        }
        return this.defaultValue;
    }

    public boolean contains(K key) {
        return this.setFlags.get(key.ordinal());
    }

    public void put(int index, float value) {
        this.values[index] = value;
        this.setFlags.set(index);
    }

    public float get(int index) {
        if (index >= 0 && index < values.length && this.setFlags.get(index)) {
            return this.values[index];
        }
        return this.defaultValue;
    }

    public float getOrDefault(K key, float defaultValue) {
        return this.setFlags.get(key.ordinal()) ? this.values[key.ordinal()] : defaultValue;
    }

    public float getOrDefault(int index, float defaultValue) {
        return (index >= 0 && index < this.values.length && this.setFlags.get(index)) ? this.values[index] : defaultValue;
    }

    public int size() {
        return this.setFlags.cardinality();
    }

    public Class<K> getKeyType() {
        return this.keyType;
    }

    public float remove(K key) {
        int ordinal = key.ordinal();
        if (this.setFlags.get(ordinal)) {
            float oldValue = this.values[ordinal];
            this.setFlags.clear(ordinal);
            return oldValue;
        }
        return this.defaultValue;
    }

    public float remove(int index) {
        if (index >= 0 && index < this.values.length && this.setFlags.get(index)) {
            float oldValue = this.values[index];
            this.setFlags.clear(index);
            return oldValue;
        }
        return this.defaultValue;
    }

    public void fill(float value) {
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = value;
            this.setFlags.set(i);
        }
    }

    public void add(K key, float addV) {
        int ordinal = key.ordinal();
        if (this.setFlags.get(ordinal)) {
            this.values[ordinal] += addV;
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }
}