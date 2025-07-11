package com.teamtea.eclipticseasons.api.misc.util;

public interface Mergable<T> {
    T merge(T next);
}
