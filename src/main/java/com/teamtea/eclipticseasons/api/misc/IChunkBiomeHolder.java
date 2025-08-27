package com.teamtea.eclipticseasons.api.misc;

import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;

public interface IChunkBiomeHolder {
    ChunkBiomeUpdateMessage eclipticseasons$getBiomeHolder();

    void eclipticseasons$setBiomeHolder(ChunkBiomeUpdateMessage chunkBiomeUpdateMessage);
}
