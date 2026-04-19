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

    protected MixinServerLevel(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
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
    private boolean eclipticseasons$wether(GameRules instance, GameRules.Key<GameRules.BooleanValue> key, Operation<Boolean> original) {
        if (key == GameRules.RULE_WEATHER_CYCLE)
            return !EclipticUtil.useSolarWeather();
        return original.call(instance, key);
    }

    @Inject(at = {@At("HEAD")}, method = {"advanceWeatherCycle"})
    public void eclipticseasons$advanceWeatherCycle(CallbackInfo ci) {
        if (EclipticUtil.useSolarWeather())
            WeatherManager.agentAdvanceWeatherCycle(getLevel(), random);
    }

    @Inject(
            method = "tickChunk",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$tickChunk_end(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        CropGrowthHandler.handleChunkTick(this, chunk);
    }

    @Inject(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private void eclipticseasons$tickPrecipitation_setBiome_before(BlockPos blockPos, CallbackInfo ci, @Share("biome_holder") LocalRef<Holder<Biome>> biome, @Local(ordinal = 1) BlockPos posAbove) {
        biome.set(MapChecker.getSurfaceBiome(this, posAbove));
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> eclipticseasons$tickPrecipitation_setBiome(ServerLevel instance, BlockPos pos, Operation<Holder<Biome>> original, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        if (biome.get() == null) {
            biome.set(original.call(instance, pos));
        }
        return biome.get();
    }

    @Inject(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isAreaLoaded(Lnet/minecraft/core/BlockPos;I)Z")
    )
    private void eclipticseasons$tickPrecipitation_melt(BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) BlockPos aboveGroundPos, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        if (CommonConfig.Temperature.iceMelt.get()) {
            // if((getLevel()).isAreaLoaded(blockPos, 1))
            {
                CustomRandomTickHandler.SNOW_MELT_2.tick(getLevel(), biome.get(), aboveGroundPos);
            }
        }
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickPrecipitation_freeze(Biome instance, LevelReader pLevel, BlockPos pPos, Operation<Boolean> original, @Local(ordinal = 2) BlockPos groundPos, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        return CustomRandomTickHandler.checkExtraFreezeCondition(getLevel(), biome.get(), groundPos);
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickPrecipitation_snow(Biome instance, LevelReader levelReader, BlockPos level, Operation<Boolean> original, @Local(ordinal = 1) BlockPos aboveGroundPos, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        return CustomRandomTickHandler.checkExtraSnowCondition(getLevel(), biome.get(), aboveGroundPos)
                ;
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation eclipticseasons$tickPrecipitation_getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        var serverLevel = getLevel();
        return WeatherManager.getPrecipitationAt(serverLevel, biome, pos);
    }

    @Inject(
            method = "tickChunk",
            at = @At(value = "HEAD")
    )
    private void eclipticseasons$tickChunk_prepare(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Share("snowy_status") LocalRef<SnowyStatusKeeper> keeper,
                                                   @Share("weather_status") LocalRef<WeatherStatusKeeper> keeperWeather, @Share("chunk_info_map") LocalRef<ChunkInfoMap> mapLocalRef) {
        keeper.set(SnowyMapChecker.getSnowyStatusKeeper(chunk));
        mapLocalRef.set(MapChecker.getChunkInfoMapOrCreate(getLevel(), chunk.getPos()));
        keeperWeather.set(SnowyMapChecker.getWeatherStatusKeeper(chunk));
    }

    @ModifyExpressionValue(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    ordinal = 1,
                    target = "Lnet/minecraft/server/level/ServerLevel;getBlockRandomPos(IIII)Lnet/minecraft/core/BlockPos;")
    )
    private BlockPos eclipticseasons$tickChunk_our_snow(BlockPos original, @Local(argsOnly = true) LevelChunk chunk, @Local ChunkPos chunkPos, @Share("snowy_status") LocalRef<SnowyStatusKeeper> keeper,
                                                        @Share("weather_status") LocalRef<WeatherStatusKeeper> keeperWeather, @Share("chunk_info_map") LocalRef<ChunkInfoMap> mapLocalRef) {
        SnowyStatusKeeper data = keeper.get();
        WeatherStatusKeeper weatherStatusKeeper = keeperWeather.get();
        if (data != null) {
            ChunkInfoMap chunkInfoMap = mapLocalRef.get();
            if (chunkInfoMap != null && weatherStatusKeeper != null)
                data.tickChunk(getLevel(), chunk, chunkPos, original, chunkInfoMap, weatherStatusKeeper);
        }
        return original;
    }

    @Inject(
            method = "tickChunk",
            at = @At(value = "RETURN")
    )
    private void eclipticseasons$tickChunk_sync(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Share("snowy_status") LocalRef<SnowyStatusKeeper> keeper,
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
