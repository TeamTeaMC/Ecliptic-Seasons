package com.teamtea.eclipticseasons.mixin.client.chunk;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.IMapSlice;
import com.teamtea.eclipticseasons.api.misc.client.ISeedProvider;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.caffeinemc.mods.sodium.client.world.cloned.ChunkRenderContext;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SectionCompiler.Results.class})
public abstract class MixinSectionCompiler_Results implements ISeedProvider {

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
