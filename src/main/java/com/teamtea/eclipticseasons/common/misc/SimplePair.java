package com.teamtea.eclipticseasons.common.misc;

import java.util.Objects;

public class SimplePair<K,V> {

    private K key;
    private V value;

    private SimplePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }


    public static <K,V> SimplePair<K,V> of(K k, V v){
        return new SimplePair<>(k,v);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePair<?, ?> that = (SimplePair<?, ?>) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
