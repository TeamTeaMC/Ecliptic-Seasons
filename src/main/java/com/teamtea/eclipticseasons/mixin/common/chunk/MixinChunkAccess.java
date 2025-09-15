package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ChunkAccess.class})
public abstract class MixinChunkAccess implements IChunkBiomeHolder {

    @Shadow
    public abstract boolean hasData(AttachmentType<?> type);

    @Shadow
    public abstract <T> T getData(AttachmentType<T> type);

    @Unique
    private BiomeHolder eclipticseasons$biomeHolder = null;

    @Override
    public BiomeHolder eclipticseasons$getBiomeHolder() {
        if (this.eclipticseasons$biomeHolder == null) {
            if (hasData(AttachmentRegistry.BIOME_HOLDER.get())) {
                BiomeHolder biomeHolder = getData(AttachmentRegistry.BIOME_HOLDER.get());
                if (biomeHolder.hasUpdated()) {
                    this.eclipticseasons$biomeHolder = biomeHolder;
                }
            }
        }
        return this.eclipticseasons$biomeHolder;
    }
}
