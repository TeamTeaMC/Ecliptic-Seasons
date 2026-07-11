package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
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
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
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

    @Inject(at = {@At("TAIL")}, method = {"setWeatherParameters"})
    public void eclipticseasons$setWeatherParameters(int pClearTime, int pWeatherTime, boolean pIsRaining, boolean pIsThundering, CallbackInfo ci) {
        WeatherManager.onSetWeatherParameters(getLevel(), pClearTime, pWeatherTime, pIsRaining, pIsThundering);
    }

    @Inject(at = {@At("HEAD")}, method = {"resetWeatherCycle"}, cancellable = true)
    public void eclipticseasons$resetWeatherCycle(CallbackInfo ci) {
        if (!CommonConfig.Weather.clearAfterSleep.get())
            ci.cancel();
    }

    @WrapOperation(at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")},
            method = {"advanceWeatherCycle"})
    private boolean eclipticseasons$wether(GameRules instance, GameRules.Key<GameRules.BooleanValue> pKey, Operation<Boolean> original) {
        if (pKey == GameRules.RULE_WEATHER_CYCLE)
            return !EclipticUtil.useSolarWeather();
        return original.call(instance, pKey);
    }

    @Inject(at = {@At("HEAD")}, method = {"advanceWeatherCycle"})
    public void eclipticseasons$advanceWeatherCycle(CallbackInfo ci) {
        // if (EclipticUtil.useSolarWeather())
            WeatherManager.agentAdvanceWeatherCycle(getLevel(), random);
    }

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
        Holder<Biome> surfaceBiome = MapChecker.getSurfaceBiome(this, posAbove);
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
        return WeatherManager.getPrecipitationAt(this, biome, pos);
    }


    // @Inject(
    //         remap = false,
    //         method = "tickChunk",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isAreaLoaded(Lnet/minecraft/core/BlockPos;I)Z")
    // )
    // private void eclipticseasons$tickChunk_setIfRain(LevelChunk pChunk, int pRandomTickSpeed, CallbackInfo ci, @Local(ordinal = 0) BlockPos blockPos, @Local Biome biome, @Local LocalBooleanRef booleanRef) {
    //     booleanRef.set(WeatherManager.getRainOrSnow(getLevel(), biome, blockPos) != Biome.Precipitation.NONE);
    // }

    @ModifyExpressionValue(
            remap = false,
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> eclipticseasons$tickChunk_getBiome(Holder<Biome> original, @Share("eclipticseasons$biome") LocalRef<Holder<Biome>> biome) {
        biome.set(original);
        return original;
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isAreaLoaded(Lnet/minecraft/core/BlockPos;I)Z")
    )
    private boolean eclipticseasons$tickChunk_melt(boolean original, @Share("eclipticseasons$biome") LocalRef<Holder<Biome>> biome, @Local(ordinal = 0) BlockPos blockPos) {
        if (original && CommonConfig.Temperature.iceMelt.get()) {
            // if((getLevel()).isAreaLoaded(blockPos, 1))
            {
                Holder<Biome> biomeHolder = biome.get();
                if (biomeHolder != null)
                    CustomRandomTickHandler.SNOW_MELT_2.tick(getLevel(), biomeHolder, blockPos);
            }
        }
        return original;
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickChunk_freeze(Biome instance, LevelReader pLevel, BlockPos pPos, Operation<Boolean> original, @Share("eclipticseasons$biome") LocalRef<Holder<Biome>> biome) {
        Holder<Biome> biomeHolder = biome.get();
        return biomeHolder != null ? CustomRandomTickHandler.checkExtraFreezeCondition(getLevel(), biomeHolder, pPos) : original.call(instance, pLevel, pPos);
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
        if (!EclipticUtil.canSnowyBlockInteract()) return;

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
        BlockPos call = original.call(instance, i, k, j, mask);
        if (!EclipticUtil.canSnowyBlockInteract()) return call;

        SnowyStatusKeeper data = keeper.get();
        WeatherStatusKeeper weatherStatusKeeper = keeperWeather.get();
        if (data != null) {
            ChunkInfoMap chunkInfoMap = mapLocalRef.get();
            if (chunkInfoMap != null && weatherStatusKeeper != null) {
                // patch for 1.20.1
                for (int i1 = 0; i1 < Math.max(1, pRandomTickSpeed >> 3); i1++) {
                    data.tickChunk(getLevel(), chunk, chunkPos,
                            i1 == 0 ? call : original.call(instance, i, k, j, mask),
                            chunkInfoMap, weatherStatusKeeper);
                }
            }
        }
        return call;
    }

    @Inject(
            method = "tickChunk",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$tickChunk_sync(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci,
                                                @Share("snowy_status") LocalRef<SnowyStatusKeeper> keeper,
                                                @Share("weather_status") LocalRef<WeatherStatusKeeper> keeperWeather) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;

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
