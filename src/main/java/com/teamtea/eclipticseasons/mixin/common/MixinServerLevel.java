package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
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
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin({ServerLevel.class})
public abstract class MixinServerLevel extends Level {

    @Shadow
    @Final
    private ServerLevelData serverLevelData;

    protected MixinServerLevel(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
        super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    }

    // 早晨有可能继续下雨
    @Inject(at = {@At("HEAD")}, method = {"resetWeatherCycle"}, cancellable = true)
    public void ecliptic$resetWeatherCycle(CallbackInfo ci) {
        if (CommonConfig.Weather.useSolarWeather.get())
            ci.cancel();
    }

    /**
     * 如果使用原版天气，那么会在天气循环时推演一下雪厚度
     **/
    @Inject(at = {@At("HEAD")}, method = {"advanceWeatherCycle"}, cancellable = true)
    public void ecliptic$advanceWeatherCycle(CallbackInfo ci) {
        boolean cancel = WeatherManager.agentAdvanceWeatherCycle((ServerLevel) (Object) this, serverLevelData, levelData, random);
        if (cancel && CommonConfig.Weather.useSolarWeather.get()) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/util/valueproviders/IntProvider;sample(Lnet/minecraft/util/RandomSource;)I")
    )
    private int ecliptic$advanceWeatherCycle_sample_THUNDER_DELAY(IntProvider intProvider, RandomSource randomSource, Operation<Integer> original) {
        if (!CommonConfig.Weather.useSolarWeather.get()) {
            return VanillaWeather.replaceThunderDelay(this, original.call(intProvider, randomSource));
        }
        return original.call(intProvider, randomSource);
    }

    @WrapOperation(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/util/valueproviders/IntProvider;sample(Lnet/minecraft/util/RandomSource;)I")
    )
    private int ecliptic$advanceWeatherCycle_sample_RAIN_DELAY(IntProvider intProvider, RandomSource randomSource, Operation<Integer> original) {
        if (!CommonConfig.Weather.useSolarWeather.get()) {
            return VanillaWeather.replaceRainDelay(this, original.call(intProvider, randomSource));
        }
        return original.call(intProvider, randomSource);
    }


    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
    )
    private boolean ecliptic$tickPrecipitation_isRaining(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0, argsOnly = true) BlockPos pos) {
        if (CommonConfig.Weather.useSolarWeather.get())
            return WeatherManager.isRainingAt(serverLevel, pos);
        else return original.call(serverLevel);
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation ecliptic$tickPrecipitation_getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        var serverLevel = (ServerLevel) (Object) this;
        if (CommonConfig.Weather.useSolarWeather.get())
            return WeatherManager.getPrecipitationAt(serverLevel, biome, pos);
        else {
            return VanillaWeather.handlePrecipitationAt(serverLevel, biome, pos);
        }
    }


    /*
     * Due to Current code, we don't need to check if there is rain or thunder first
     * */
    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
    )
    private boolean ecliptic$tickChunk_isRaining(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        if (CommonConfig.Weather.useSolarWeather.get())
            return true;
        else return original.call(serverLevel);
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")
    )
    private boolean ecliptic$tickChunk_isThundering(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        // var chunkpos = levelChunk.getPos();
        // int i = chunkpos.getMiddleBlockX();
        // int j = chunkpos.getMiddleBlockZ();
        // BlockPos blockpos1 = ((ServerLevel) (Object) this).getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(i, 0, j));
        if (CommonConfig.Weather.useSolarWeather.get())
            return true;
        else return original.call(serverLevel);
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRainingAt(Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean ecliptic$tickChunk_checkRainDifficulty(ServerLevel serverLevel, BlockPos pos, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        if (CommonConfig.Weather.useSolarWeather.get())
            return WeatherManager.isThunderAt(serverLevel, pos) && WeatherManager.isRainingAt(serverLevel, pos);
        else if (VanillaWeather.isInWinter(serverLevel)) {
            return false;
        } else return original.call(serverLevel, pos);
    }
}
