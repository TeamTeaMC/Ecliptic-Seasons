package com.teamtea.eclipticseasons.mixin.compat.distanthorizons;


import com.teamtea.eclipticseasons.compat.distanthorizons.DHTool;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;


// 这个类缺乏必要的查询手段
@Mixin({ClientLevel.class})
public abstract class MixinClientLevel extends Level {
    protected MixinClientLevel(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

    @Inject(at = {@At("HEAD")}, method = {"tick"})
    public void ecliptic$tick_refresh_dh(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        if (ClientConfig.Renderer.forceChunkRenderUpdate.get() &&
                ClientConfig.Renderer.enhancementChunkRenderUpdate.get()) {
            if (getGameTime() % (20 * 15) == 0) {
                DHTool.forceReloadAll();
            }
        }
    }
}
