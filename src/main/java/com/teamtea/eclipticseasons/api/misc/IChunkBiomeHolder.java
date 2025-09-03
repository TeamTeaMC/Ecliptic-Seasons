package com.teamtea.eclipticseasons.api.misc;

import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;

public interface IChunkBiomeHolder {
    BiomeHolder eclipticseasons$getBiomeHolder();

    // ===========================
    // 1.20 use

    void eclipticseasons$setBiomeHolder(BiomeHolder biomeHolder);

    default BiomeHolder eclipticseasons$getBiomeHolder$1201() {
        return null;
    }

    default void eclipticseasons$setBiomeHolder$1201(BiomeHolder biomeHolder){

    };
}
