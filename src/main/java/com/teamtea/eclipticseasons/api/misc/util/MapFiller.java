package com.teamtea.eclipticseasons.api.misc.util;

import java.util.Map;

public interface MapFiller<K, V> {
    void fillMap(Map<K, V> map);
}
