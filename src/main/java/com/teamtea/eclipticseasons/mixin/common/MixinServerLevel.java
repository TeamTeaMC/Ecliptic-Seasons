package com.teamtea.eclipticseasons.mixin.common;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.handler.CustomRandomTickHandler;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerWorld.class})
public abstract class MixinServerLevel {

    // 早晨有可能继续下雨
    @Inject(at = {@At("HEAD")}, method = {"stopWeather"}, cancellable = true)
    public void eclipticseasons$resetWeatherCycle(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(at = {@At("HEAD")}, method = {"tick"})
    public void eclipticseasons$advanceWeatherCycle(CallbackInfo ci) {
        boolean cancel = WeatherManager.agentAdvanceWeatherCycle((ServerWorld) (Object) this, null, null, ((ServerWorld) (Object) this).getRandom());

    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;getPrecipitation()Lnet/minecraft/world/biome/Biome$RainType;")
    )
    private Biome.RainType eclipticseasons$tickChunk_getPrecipitationAt(Biome biome, Operation<Biome.RainType> original) {
        return WeatherManager.getPrecipitationAt((ServerWorld) (Object) this, biome,BlockPos.ZERO);
    }


    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;isRaining()Z")
    )
    private boolean eclipticseasons$tickChunk_isRaining(ServerWorld instance, Operation<Boolean> original, @Local(ordinal = 0, argsOnly = true) Chunk levelChunk) {
        ChunkPos chunkpos = levelChunk.getPos();
        int i = (chunkpos.getMaxBlockX()/2+chunkpos.getMinBlockX()/2);
        int j = (chunkpos.getMaxBlockZ()/2+chunkpos.getMinBlockZ()/2);
        BlockPos blockpos1 = ((ServerWorld) (Object) this).getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(i, 0, j));
        return WeatherManager.isRainingAt((ServerWorld) (Object) this, blockpos1);
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ServerWorld;isThundering()Z")
    )
    private boolean eclipticseasons$tickChunk_isThundering(ServerWorld instance, Operation<Boolean> original, @Local(ordinal = 0) Chunk levelChunk) {
        ChunkPos chunkpos = levelChunk.getPos();
        int i = (chunkpos.getMaxBlockX()/2+chunkpos.getMinBlockX()/2);
        int j = (chunkpos.getMaxBlockZ()/2+chunkpos.getMinBlockZ()/2);
        BlockPos blockpos1 = ((ServerWorld) (Object) this).getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, new BlockPos(i, 0, j));
        return WeatherManager.isThunderAt((ServerWorld) (Object) this, blockpos1);
    }


    @Inject(
            remap = false,
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/server/ServerWorld;isAreaLoaded(Lnet/minecraft/util/math/BlockPos;I)Z")
    )
    private void eclipticseasons$tickChunk_melt(Chunk pChunk, int pRandomTickSpeed, CallbackInfo ci, @Local Biome biome, @Local(ordinal = 0) BlockPos blockPos) {
        if (CommonConfig.Temperature.iceMelt.get()){
            // if(((ServerLevel) (Object) this).isAreaLoaded(blockPos, 1))
            {
                CustomRandomTickHandler.SNOW_MELT_2.tick((ServerWorld) (Object) this, biome, blockPos);
            };
        }
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/Biome;shouldFreeze(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickChunk_freeze(Biome instance, IWorldReader pLevel, BlockPos pPos, Operation<Boolean> original, @Local Biome biome, @Local(ordinal = 0) BlockPos blockPos) {
        eclipticseasons$snowDown((ServerWorld) (Object) this, biome, blockPos);
        return false;
    }

    @WrapOperation(
            method = "tickChunk",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/biome/Biome;shouldSnow(Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;)Z")
    )
    private boolean eclipticseasons$tickChunk_snow(Biome instance, IWorldReader iWorldReader, BlockPos pLevel, Operation<Boolean> original, @Local Biome biome, @Local(ordinal = 0) BlockPos blockPos) {
        eclipticseasons$snowDown((ServerWorld) (Object) this, biome, blockPos);
        return false;
    }

    @Unique
    private void eclipticseasons$snowDown(ServerWorld serverLevel,Biome biome, BlockPos blockPos) {
        if (CommonConfig.Temperature.snowDown.get()){
            {
                CustomRandomTickHandler.SNOW_MELT.tick(serverLevel, biome, blockPos);
            };
        }
    }
}
