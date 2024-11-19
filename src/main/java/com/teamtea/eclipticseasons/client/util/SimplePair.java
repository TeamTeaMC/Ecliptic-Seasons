package com.teamtea.eclipticseasons.client.util;

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
}
