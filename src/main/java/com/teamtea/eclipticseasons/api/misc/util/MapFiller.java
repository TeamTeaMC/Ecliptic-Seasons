package com.teamtea.eclipticseasons.api.misc.util;

import java.util.List;
import java.util.Map;

public interface MapFiller<K, V> {
    void fillMap(Map<K, List<V>> map);
}
