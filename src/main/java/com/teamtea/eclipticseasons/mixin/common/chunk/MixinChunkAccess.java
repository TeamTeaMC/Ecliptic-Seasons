package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.common.network.message.ChunkBiomeUpdateMessage;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ChunkAccess.class})
public abstract class MixinChunkAccess implements IChunkBiomeHolder {


    @Unique
    private ChunkBiomeUpdateMessage eclipticseasons$biomeHolder = null;

    @Override
    public ChunkBiomeUpdateMessage eclipticseasons$getBiomeHolder() {
        return this.eclipticseasons$biomeHolder;
    }

    @Override
    public void eclipticseasons$setBiomeHolder(ChunkBiomeUpdateMessage chunkBiomeUpdateMessage) {
        this.eclipticseasons$biomeHolder=chunkBiomeUpdateMessage;
    }
}
