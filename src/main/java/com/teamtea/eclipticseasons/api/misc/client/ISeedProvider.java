package com.teamtea.eclipticseasons.api.misc.client;

public interface ISeedProvider {

    void setCacheSeed(long seed);

    long getCacheSeed();
}
