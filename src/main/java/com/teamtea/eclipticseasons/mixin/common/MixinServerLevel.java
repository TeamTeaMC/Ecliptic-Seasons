package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin({ServerLevel.class})
public abstract class MixinServerLevel extends Level {

    protected MixinServerLevel(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

    // 早晨有可能继续下雨
    @Inject(at = {@At("HEAD")}, method = {"resetWeatherCycle"}, cancellable = true)
    public void ecliptic$resetWeatherCycle(CallbackInfo ci) {
        if (EclipticUtil.useSolarWeather())
            ci.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"advanceWeatherCycle"}, cancellable = true)
    public void ecliptic$advanceWeatherCycle(CallbackInfo ci) {
        boolean cancel = WeatherManager.agentAdvanceWeatherCycle((ServerLevel) (Object) this, null, null, ((ServerLevel) (Object) this).getRandom());
        if (cancel && EclipticUtil.useSolarWeather())
            ci.cancel();
    }

    @WrapOperation(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/valueproviders/IntProvider;sample(Lnet/minecraft/util/RandomSource;)I")
    )
    private int ecliptic$advanceWeatherCycle_sample_THUNDER_DELAY(IntProvider intProvider, RandomSource randomSource, Operation<Integer> original) {
        if (EclipticUtil.isSolarWeatherClosed()) {
            return VanillaWeather.replaceThunderDelay(this, original.call(intProvider, randomSource));
        }
        return original.call(intProvider, randomSource);
    }

    @WrapOperation(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/util/valueproviders/IntProvider;sample(Lnet/minecraft/util/RandomSource;)I")
    )
    private int ecliptic$advanceWeatherCycle_sample_RAIN_DELAY(IntProvider intProvider, RandomSource randomSource, Operation<Integer> original) {
        if (EclipticUtil.isSolarWeatherClosed()) {
            return VanillaWeather.replaceRainDelay(this, original.call(intProvider, randomSource));
        }
        return original.call(intProvider, randomSource);
    }


    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation ecliptic$tickChunk_getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        if (EclipticUtil.useSolarWeather())
            return WeatherManager.getPrecipitationAt(this, biome, pos);
        return VanillaWeather.handlePrecipitationAt(this, biome, pos);
    }


    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
    )
    private boolean ecliptic$tickChunk_isRaining(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        if (EclipticUtil.useSolarWeather()) {
            var chunkpos = levelChunk.getPos();
            int i = chunkpos.getMiddleBlockX();
            int j = chunkpos.getMiddleBlockZ();
            BlockPos blockpos1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(i, 0, j));
            return isRainingAt(blockpos1);
        }
        return original.call(serverLevel);
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")
    )
    private boolean ecliptic$tickChunk_isThundering(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        if (EclipticUtil.useSolarWeather()) {
            var chunkpos = levelChunk.getPos();
            int i = chunkpos.getMiddleBlockX();
            int j = chunkpos.getMiddleBlockZ();
            BlockPos blockpos1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(i, 0, j));
            return WeatherManager.isThunderAt(this, blockpos1);
        }
        return original.call(serverLevel);
    }
}
