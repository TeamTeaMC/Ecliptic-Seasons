package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.teamtea.eclipticseasons.api.misc.client.ISeedProvider;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({RenderChunkRegion.class})
public abstract class MixinRenderChunkRegion_ISeedProvider implements ISeedProvider {

    @Unique
    private long eclipticseasons$cacheSeed = 0;

    @Override
    public void setCacheSeed(long seed) {
        eclipticseasons$cacheSeed = seed;
    }

    @Override
    public long getCacheSeed() {
        return eclipticseasons$cacheSeed;
    }
}
