package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetter;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClonedChunkSection.class})
public abstract class MixinClonedChunkSection implements ISnowyGetter {


    @Unique
    private BiomeHolder eclipticseasons$biomeHolder;

    @Unique
    private Heightmap eclipticseasons$heightmap;

    @Unique
    private ChunkInfoMap eclipticseasons$chunkInfoMap;

    @Inject(
            method = "<init>",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$init(Level level, LevelChunk chunk, LevelChunkSection section, SectionPos pos, CallbackInfo ci) {
        eclipticseasons$biomeHolder = ((IChunkBiomeHolder)chunk).eclipticseasons$getBiomeHolder();
        eclipticseasons$heightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING);
        eclipticseasons$chunkInfoMap = MapChecker.getChunkInfoMapOrCreate(level, chunk.getPos().getMiddleBlockPosition(64));
    }

    @Override
    public BiomeHolder getBiomeHolder() {
        return eclipticseasons$biomeHolder;
    }

    @Override
    public Heightmap getSolidHeightMap() {
        return eclipticseasons$heightmap;
    }

    @Override
    public ChunkInfoMap getChunkInfoMap() {
        return eclipticseasons$chunkInfoMap;
    }
}
