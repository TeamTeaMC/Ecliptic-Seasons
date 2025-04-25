package com.teamtea.eclipticseasons.compat.iris;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.minecraft.world.level.block.state.BlockState;

public interface IIrisShaderAccesor {
    void eclipticseasons$setSnowy(ChunkBuildContext renderContext, BlockState blockState);
}
