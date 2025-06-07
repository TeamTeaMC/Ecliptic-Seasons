package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetter;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.caffeinemc.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClonedChunkSection.class})
public abstract class MixinClonedChunkSection implements ISnowyGetter {

    @Unique
    private SnowyRemover eclipticseasons$snowyRemover;

    @Unique
    private BiomeHolder eclipticseasons$biomeHolder;
    @Inject(
            method = "<init>",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$init(Level level, LevelChunk chunk, LevelChunkSection section, SectionPos pos, CallbackInfo ci) {
        eclipticseasons$snowyRemover=chunk.getData(AttachmentRegistry.SNOWY_REMOVER);
        eclipticseasons$biomeHolder=chunk.getData(AttachmentRegistry.BIOME_HOLDER);
    }

    @Override
    public SnowyRemover getSnowyRemover() {
        return eclipticseasons$snowyRemover;
    }

    @Override
    public BiomeHolder getBiomeHolder() {
        return eclipticseasons$biomeHolder;
    }
}
