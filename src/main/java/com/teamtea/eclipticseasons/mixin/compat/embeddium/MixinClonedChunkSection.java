package com.teamtea.eclipticseasons.mixin.compat.embeddium;


import com.teamtea.eclipticseasons.api.misc.client.ISnowyGetter;
import com.teamtea.eclipticseasons.common.core.map.*;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;
import org.embeddedt.embeddium.impl.world.cloned.ClonedChunkSection;
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

    @Unique
    private Heightmap eclipticseasons$heightmap;

    @Unique
    private ChunkInfoMap eclipticseasons$chunkInfoMap;

    @Unique
    SnowyStatusKeeper eclipticseasons$snowyStatusKeeper;
    @Unique
    NoneSnowArea eclipticseasons$noneSnowArea;

    @Inject(
            method = "<init>",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$init(Level level, LevelChunk chunk, LevelChunkSection section, SectionPos pos, CallbackInfo ci) {
        eclipticseasons$snowyRemover = chunk.getData(AttachmentRegistry.SNOWY_REMOVER);
        eclipticseasons$biomeHolder = chunk.getData(AttachmentRegistry.BIOME_HOLDER);
        eclipticseasons$heightmap = chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING);
        eclipticseasons$chunkInfoMap = MapChecker.getChunkInfoMapOrCreate(level, chunk.getPos().getMiddleBlockPosition(64));
        eclipticseasons$snowyStatusKeeper = SnowyMapChecker.getSnowyStatusKeeperCopy(chunk);
        eclipticseasons$noneSnowArea = chunk.getData(AttachmentRegistry.NONE_SNOW_AREA);
    }

    @Override
    public SnowyRemover getSnowyRemover() {
        return eclipticseasons$snowyRemover;
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

    @Override
    public SnowyStatusKeeper getSnowyStatusKeeper() {
        return eclipticseasons$snowyStatusKeeper;
    }


    @Override
    public NoneSnowArea getNoneSnowArea() {
        return eclipticseasons$noneSnowArea;
    }
}
