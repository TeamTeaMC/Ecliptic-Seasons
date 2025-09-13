package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import com.teamtea.eclipticseasons.common.core.snow.WeatherStatusKeeper;
import com.teamtea.eclipticseasons.common.handler.CustomRandomTickHandler;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin({ServerLevel.class})
public abstract class MixinServerLevel extends Level {

    @Shadow
    public abstract ServerLevel getLevel();

    protected MixinServerLevel(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates) {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

    @Inject(at = {@At("HEAD")}, method = {"setWeatherParameters"}, cancellable = true)
    public void eclipticseasons$setWeatherParameters(int pClearTime, int pWeatherTime, boolean pIsRaining, boolean pIsThundering, CallbackInfo ci) {
        if (EclipticUtil.hasLocalWeather(this)) {
            WeatherManager.onSetWeatherParameters(getLevel(), pClearTime, pWeatherTime, pIsRaining, pIsThundering);
            ci.cancel();
        }
    }

    @Inject(at = {@At("HEAD")}, method = {"resetWeatherCycle"}, cancellable = true)
    public void eclipticseasons$resetWeatherCycle(CallbackInfo ci) {
        if (EclipticUtil.hasLocalWeather(this))
            ci.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"advanceWeatherCycle"}, cancellable = true)
    public void eclipticseasons$advanceWeatherCycle(CallbackInfo ci) {
        boolean cancel = WeatherManager.agentAdvanceWeatherCycle(getLevel(), (getLevel()).getRandom());
        if (cancel && EclipticUtil.hasLocalWeather(this))
            ci.cancel();
    }

    @WrapOperation(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/valueproviders/IntProvider;sample(Lnet/minecraft/util/RandomSource;)I")
    )
    private int eclipticseasons$advanceWeatherCycle_sample_THUNDER_DELAY(IntProvider intProvider, RandomSource randomSource, Operation<Integer> original) {
        if (!EclipticUtil.hasLocalWeather(this)) {
            return VanillaWeather.replaceThunderDelay(this, original.call(intProvider, randomSource));
        }
        return original.call(intProvider, randomSource);
    }

    @WrapOperation(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/util/valueproviders/IntProvider;sample(Lnet/minecraft/util/RandomSource;)I")
    )
    private int eclipticseasons$advanceWeatherCycle_sample_RAIN_DELAY(IntProvider intProvider, RandomSource randomSource, Operation<Integer> original) {
        if (!EclipticUtil.hasLocalWeather(this)) {
            return VanillaWeather.replaceRainDelay(this, original.call(intProvider, randomSource));
        }
        return original.call(intProvider, randomSource);
    }

    // @Inject(
    //         method = "tickChunk",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isRandomlyTicking()Z")
    // )
    // private void eclipticseasons$tickChunk_handleRandomTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Local BlockState blockState, @Local BlockPos blockPos) {
    //     CropGrowthHandler.handleRandomTick(this, chunk, blockPos, blockState);
    // }

    @Inject(
            method = "tickChunk",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$tickChunk_handleRandomTick_clear(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        CropGrowthHandler.handleChunkTick(this, chunk);
    }


    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> eclipticseasons$tickPrecipitation_setBiome(ServerLevel instance, BlockPos pos, Operation<Holder<Biome>> original, @Local(ordinal = 1) BlockPos posAbove) {
        Holder<Biome> surfaceBiome = EclipticUtil.hasLocalWeather(this) ? MapChecker.getSurfaceBiome(this, posAbove) : null;
        if (surfaceBiome == null) {
            surfaceBiome = (original.call(instance, pos));
        }
        return surfaceBiome;
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation eclipticseasons$tickChunk_getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        if (EclipticUtil.hasLocalWeather(this))
            return WeatherManager.getPrecipitationAt(this, biome, pos);
        return VanillaWeather.handlePrecipitationAt(this, biome, pos);
    }


    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")
    )
    private boolean eclipticseasons$tickChunk_initIfThunder(ServerLevel serverLevel, Operation<Boolean> original) {
        if (EclipticUtil.hasLocalWeather(this)) {
            return true;
        }
        return original.call(serverLevel);
    }

    @ModifyExpressionValue(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRainingAt(Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickChunk_setIfThunder(boolean original, @Local(ordinal = 0) BlockPos blockPos) {
        if (original && EclipticUtil.hasLocalWeather(this)) {
            original = WeatherManager.isThunderAt(getLevel(), blockPos);
        }
        return original;
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
    )
    private boolean eclipticseasons$tickChunk_initIfRain(ServerLevel serverLevel, Operation<Boolean> original) {
        if (EclipticUtil.hasLocalWeather(this)) {
            return true;
        }
        return original.call(serverLevel);
    }

    @Inject(
            remap = false,
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isAreaLoaded(Lnet/minecraft/core/BlockPos;I)Z")
    )
    private void eclipticseasons$tickChunk_setIfRain(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci, @Local(ordinal = 0) BlockPos blockPos, @Local Biome biome, @Local LocalBooleanRef booleanRef) {
        if (EclipticUtil.hasLocalWeather(this)) {
            booleanRef.set(WeatherManager.getRainOrSnow(getLevel(), biome, blockPos) != Biome.Precipitation.NONE);
        }
    }

    @Inject(
            remap = false,
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isAreaLoaded(Lnet/minecraft/core/BlockPos;I)Z")
    )
    private void eclipticseasons$tickChunk_melt(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci, @Local Biome biome, @Local(ordinal = 0) BlockPos blockPos) {
        if (CommonConfig.Temperature.iceMelt.get()) {
            // if((getLevel()).isAreaLoaded(blockPos, 1))
            {
                CustomRandomTickHandler.SNOW_MELT_2.tick(getLevel(), biome, blockPos);
            }
        }
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickChunk_freeze(Biome instance, LevelReader pLevel, BlockPos pPos, Operation<Boolean> original) {
        return CustomRandomTickHandler.checkExtraFreezeCondition(getLevel(), instance, pPos);

    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickChunk_snow(Biome instance, LevelReader pLevel, BlockPos pPos, Operation<Boolean> original) {
        return CustomRandomTickHandler.checkExtraSnowCondition(getLevel(), instance, pPos);
    }


    @Inject(
            method = "tickChunk",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$tickChunk_prepare(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci,
                                                   @Share("snowy_status") LocalRef<SnowyStatusKeeper> keeper,
                                                   @Share("weather_status") LocalRef<WeatherStatusKeeper> keeperWeather,
                                                   @Share("chunk_info_map") LocalRef<ChunkInfoMap> mapLocalRef) {
        keeper.set(SnowyMapChecker.getSnowyStatusKeeper(chunk));
        mapLocalRef.set(MapChecker.getChunkInfoMapOrCreate(getLevel(), chunk.getPos()));
        keeperWeather.set(SnowyMapChecker.getWeatherStatusKeeper(chunk));
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    ordinal = 1,
                    target = "Lnet/minecraft/server/level/ServerLevel;getBlockRandomPos(IIII)Lnet/minecraft/core/BlockPos;")
    )
    private BlockPos eclipticseasons$tickChunk_our_snow(ServerLevel instance, int i, int k, int j, int mask, Operation<BlockPos> original, @Local(argsOnly = true) LevelChunk chunk, @Local ChunkPos chunkPos,
                                                        @Share("snowy_status") LocalRef<SnowyStatusKeeper> keeper,
                                                        @Share("weather_status") LocalRef<WeatherStatusKeeper> keeperWeather,
                                                        @Share("chunk_info_map") LocalRef<ChunkInfoMap> mapLocalRef,
                                                        @Local(ordinal = 0, argsOnly = true) int pRandomTickSpeed) {
        SnowyStatusKeeper data = keeper.get();
        WeatherStatusKeeper weatherStatusKeeper = keeperWeather.get();
        if (data != null) {
            ChunkInfoMap chunkInfoMap = mapLocalRef.get();
            if (chunkInfoMap != null && weatherStatusKeeper != null) {
                // patch for 1.20.1
                for (int i1 = 0; i1 < Math.max(1, pRandomTickSpeed >> 3); i1++) {
                    var pos = original.call(instance, i, k, j, mask);
                    data.tickChunk(getLevel(), chunk, chunkPos, pos, chunkInfoMap, weatherStatusKeeper);
                }
            }
        }
        return original.call(instance, i, k, j, mask);
    }

    @Inject(
            method = "tickChunk",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$tickChunk_sync(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci,
                                                @Share("snowy_status") LocalRef<SnowyStatusKeeper> keeper,
                                                @Share("weather_status") LocalRef<WeatherStatusKeeper> keeperWeather) {
        WeatherStatusKeeper weatherStatusKeeper = keeperWeather.get();
        if (weatherStatusKeeper != null) {
            weatherStatusKeeper.updateAndSend(getLevel(), chunk);
        }

        SnowyStatusKeeper data = keeper.get();
        if (data != null) {
            data.updateAndSend(getLevel(), chunk);
        }
    }
}
