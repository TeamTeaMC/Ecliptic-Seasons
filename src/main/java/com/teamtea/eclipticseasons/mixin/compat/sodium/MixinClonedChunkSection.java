package com.teamtea.eclipticseasons.mixin.compat.sodium;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetter;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.compat.sodium.SodiumBoard;
import com.teamtea.eclipticseasons.compat.sodium.SodiumStatus;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClonedChunkSection.class})
public abstract class MixinClonedChunkSection implements ISnowyGetter {

    @Unique
    private SnowyRemover eclipticSeasons$snowyRemover;


    @Inject(
            method = "<init>",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$init(Level level, LevelChunk chunk, LevelChunkSection section, SectionPos pos, CallbackInfo ci) {
        eclipticSeasons$snowyRemover=chunk.getData(EclipticSeasons.ModContents.SNOWY_REMOVER);
    }

    @Override
    public SnowyRemover getSnowyRemover() {
        return eclipticSeasons$snowyRemover;
    }
}
