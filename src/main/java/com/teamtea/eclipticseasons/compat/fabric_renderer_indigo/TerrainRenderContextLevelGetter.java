package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;

public interface TerrainRenderContextLevelGetter {
    BlockAndTintGetter eclipticseasons$get();

    BlockPos eclipticseasons$getPos();
}
