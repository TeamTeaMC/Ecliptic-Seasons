package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.ApiStatus;

public interface ISnowyGetterProvider {
    @ApiStatus.Experimental
    Heightmap getSolidHeightMap();

    ChunkInfoMap getChunkInfoMap();
}
