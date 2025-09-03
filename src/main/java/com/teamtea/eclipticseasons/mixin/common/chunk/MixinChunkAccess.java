package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ChunkAccess.class})
public abstract class MixinChunkAccess implements IChunkBiomeHolder {


    @Unique
    private BiomeHolder eclipticseasons$biomeHolder = null;

    @Override
    public BiomeHolder eclipticseasons$getBiomeHolder() {
        return this.eclipticseasons$biomeHolder;
    }

    @Override
    public void eclipticseasons$setBiomeHolder(BiomeHolder biomeHolder) {
        this.eclipticseasons$biomeHolder=biomeHolder;
    }

    @Override
    public BiomeHolder eclipticseasons$getBiomeHolder$1201() {
        return this.eclipticseasons$biomeHolder;
    }

    @Override
    public void eclipticseasons$setBiomeHolder$1201(BiomeHolder biomeHolder) {
        this.eclipticseasons$biomeHolder=biomeHolder;
    }
}
