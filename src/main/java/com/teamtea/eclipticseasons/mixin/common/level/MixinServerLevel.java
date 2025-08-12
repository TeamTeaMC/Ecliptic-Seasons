package com.teamtea.eclipticseasons.mixin.common.level;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.api.misc.CustomRandomTick;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
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
    public void eclipticseasons$resetWeatherCycle(CallbackInfo ci) {
        if (EclipticUtil.hasLocalWeather(this))
            ci.cancel();
    }

    /**
     * 如果使用原版天气，那么会在天气循环时推演一下雪厚度
     **/
    @Inject(at = {@At("HEAD")}, method = {"advanceWeatherCycle"}, cancellable = true)
    public void eclipticseasons$advanceWeatherCycle(CallbackInfo ci) {
        boolean cancel = WeatherManager.agentAdvanceWeatherCycle((ServerLevel) (Object) this, serverLevelData, levelData, random);
        if (cancel && EclipticUtil.hasLocalWeather(this)) {
            ci.cancel();
        }
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
    //         at = @At(value = "HEAD")
    // )
    // private void eclipticseasons$tickChunk_handleRandomTick_start(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Share("shouldTick") LocalBooleanRef shouldTick) {
    //     shouldTick.set(CropGrowthHandler.shouldTick(this, chunk));
    // }

    // @Inject(
    //         method = "tickChunk",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isRandomlyTicking()Z")
    // )
    // private void ecliptic$tickChunk_handleRandomTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Local BlockState blockState, @Local BlockPos blockPos, @Share("shouldTick") LocalBooleanRef shouldTick) {
    //     if (shouldTick.get())
    //         CropGrowthHandler.handleRandomTick((ServerLevel) (Object) this, chunk, blockPos, blockState);
    // }

    @Inject(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isRandomlyTicking()Z")
    )
    private void ecliptic$tickChunk_customRandomTick(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci, @Local BlockState blockState, @Local BlockPos blockPos) {
        if (CommonConfig.Debug.seasonDefinition.get())
            NaturalPlantHandler.tickBlock((ServerLevel) (Object) this, blockPos, blockState);
        // if (blockState instanceof CustomRandomTick customRandomTick) {
        //     customRandomTick.eclipticseasons$tick(blockState, (ServerLevel) (Object) this, blockPos);
        // }
    }

    @Inject(
            method = "tickChunk",
            at = @At(value = "TAIL")
    )
    private void eclipticseasons$tickChunk_end(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        CropGrowthHandler.handleChunkTick(this, chunk);
    }

    /*
     * Due to Current code, we don't need to check if there is rain or thunder first
     * */
    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
    )
    private boolean eclipticseasons$tickChunk_isRaining(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        if (EclipticUtil.hasLocalWeather(this))
            return true;
        else return original.call(serverLevel);
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")
    )
    private boolean eclipticseasons$tickChunk_isThundering(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        if (EclipticUtil.hasLocalWeather(this))
            return true;
        else return original.call(serverLevel);
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRainingAt(Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickChunk_checkRainDifficulty(ServerLevel serverLevel, BlockPos pos, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        if (EclipticUtil.hasLocalWeather(this))
            return WeatherManager.isThunderAt(serverLevel, pos) && serverLevel.isRainingAt(pos);
        else if (VanillaWeather.isInWinter(serverLevel)) {
            return false;
        } else return original.call(serverLevel, pos);
    }

    @ModifyExpressionValue(
            remap = false,
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
    )
    private Holder<Biome> eclipticseasons$tickPrecipitation_setBiome(Holder<Biome> original, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        biome.set(original);
        return original;
    }

    @Inject(
            remap = false,
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;isAreaLoaded(Lnet/minecraft/core/BlockPos;I)Z")
    )
    private void eclipticseasons$tickPrecipitation_melt(BlockPos blockPos, CallbackInfo ci, @Local(ordinal = 1) BlockPos aboveGroundPos, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        if (CommonConfig.Temperature.iceMelt.get()) {
            // if(((ServerLevel) (Object) this).isAreaLoaded(blockPos, 1))
            {
                CustomRandomTickHandler.SNOW_MELT_2.tick((ServerLevel) (Object) this, biome.get(), aboveGroundPos);
            }
            ;
        }
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;shouldFreeze(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickPrecipitation_freeze(Biome instance, LevelReader pLevel, BlockPos pPos, Operation<Boolean> original, @Local(ordinal = 2) BlockPos groundPos, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        return CustomRandomTickHandler.checkExtraFreezeCondition((ServerLevel) (Object) this, biome.get(), groundPos)
                ;
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/biome/Biome;shouldSnow(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickPrecipitation_snow(Biome instance, LevelReader levelReader, BlockPos level, Operation<Boolean> original, @Local(ordinal = 1) BlockPos aboveGroundPos, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        return CustomRandomTickHandler.checkExtraSnowCondition((ServerLevel) (Object) this, biome.get(), aboveGroundPos)
                ;
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
    )
    private boolean eclipticseasons$tickPrecipitation_isRaining(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 1) BlockPos aboveGroundPos, @Share("biome_holder") LocalRef<Holder<Biome>> biome) {
        if (EclipticUtil.hasLocalWeather(this))
            return WeatherManager.getRainOrSnow(serverLevel, biome.get().value(), aboveGroundPos) != Biome.Precipitation.NONE;
        else return original.call(serverLevel);
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation eclipticseasons$tickPrecipitation_getPrecipitationAt(Biome biome, BlockPos pos, Operation<Biome.Precipitation> original) {
        var serverLevel = (ServerLevel) (Object) this;
        if (EclipticUtil.hasLocalWeather(this))
            return WeatherManager.getPrecipitationAt(serverLevel, biome, pos);
        else {
            return VanillaWeather.handlePrecipitationAt(serverLevel, biome, pos);
        }
    }

}
