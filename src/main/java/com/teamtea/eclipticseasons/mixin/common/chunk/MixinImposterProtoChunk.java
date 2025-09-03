package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ImposterProtoChunk.class})
public abstract class MixinImposterProtoChunk implements IChunkBiomeHolder {

    @Shadow
    @Final
    private LevelChunk wrapped;

    @Override
    public BiomeHolder eclipticseasons$getBiomeHolder() {
        return ((IChunkBiomeHolder) wrapped).eclipticseasons$getBiomeHolder();
    }

    @Override
    public void eclipticseasons$setBiomeHolder(BiomeHolder biomeHolder) {
        ((IChunkBiomeHolder) wrapped).eclipticseasons$setBiomeHolder(biomeHolder);
    }

    @Override
    public BiomeHolder eclipticseasons$getBiomeHolder$1201() {
        return ((IChunkBiomeHolder) wrapped).eclipticseasons$getBiomeHolder$1201();
    }

    @Override
    public void eclipticseasons$setBiomeHolder$1201(BiomeHolder biomeHolder) {
        ((IChunkBiomeHolder) wrapped).eclipticseasons$setBiomeHolder$1201(biomeHolder);
    }
}
