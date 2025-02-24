package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin({ServerLevel.class})
public abstract class MixinServerLevel {

    // @Shadow @Final private ServerLevelData serverLevelData;
    //
    //
    // protected MixinServerLevel(WritableLevelData p_270739_, ResourceKey<Level> p_270683_, RegistryAccess p_270200_, Holder<DimensionType> p_270240_, Supplier<ProfilerFiller> p_270692_, boolean p_270904_, boolean p_270470_, long p_270248_, int p_270466_) {
    //     super(p_270739_, p_270683_, p_270200_, p_270240_, p_270692_, p_270904_, p_270470_, p_270248_, p_270466_);
    // }

    // 早晨有可能继续下雨
    @Inject(at = {@At("HEAD")}, method = {"resetWeatherCycle"}, cancellable = true)
    public void eclipticseasons$resetWeatherCycle(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"advanceWeatherCycle"}, cancellable = true)
    public void eclipticseasons$advanceWeatherCycle(CallbackInfo ci) {
        boolean cancel = WeatherManager.agentAdvanceWeatherCycle((ServerLevel) (Object) this, null, null, ((ServerLevel) (Object) this).getRandom());
        if (cancel)
            ci.cancel();
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitation()Lnet/minecraft/world/level/biome/Biome$Precipitation;")
    )
    private Biome.Precipitation eclipticseasons$tickChunk_getPrecipitationAt(Biome biome, Operation<Biome.Precipitation> original) {
        return WeatherManager.getPrecipitationAt((ServerLevel) (Object) this, biome,BlockPos.ZERO);
    }


    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isRaining()Z")
    )
    private boolean eclipticseasons$tickChunk_isRaining(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        var chunkpos = levelChunk.getPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        BlockPos blockpos1 = ((ServerLevel) (Object) this).getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(i, 0, j));
        return WeatherManager.isRainingAt((ServerLevel) (Object) this, blockpos1);
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;isThundering()Z")
    )
    private boolean eclipticseasons$tickChunk_isThundering(ServerLevel serverLevel, Operation<Boolean> original, @Local(ordinal = 0) LevelChunk levelChunk) {
        var chunkpos = levelChunk.getPos();
        int i = chunkpos.getMiddleBlockX();
        int j = chunkpos.getMiddleBlockZ();
        BlockPos blockpos1 = ((ServerLevel) (Object) this).getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(i, 0, j));
        return WeatherManager.isThunderAt((ServerLevel) (Object) this, blockpos1);
    }
}
