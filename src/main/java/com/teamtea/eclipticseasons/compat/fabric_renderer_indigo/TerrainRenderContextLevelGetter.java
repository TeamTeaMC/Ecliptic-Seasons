package com.teamtea.eclipticseasons.compat.fabric_renderer_indigo;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

public interface TerrainRenderContextLevelGetter {
    BlockAndTintGetter eclipticSeasons$get();

    BlockPos eclipticSeasons$getPos();
}
