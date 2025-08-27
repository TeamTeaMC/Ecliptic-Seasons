package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Either;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.chunk.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin({ChunkMap.class})
public abstract class MixinChunkMap {

    // @Inject(
    //         at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/api/IEventBus;post(Lnet/minecraftforge/eventbus/api/Event;)Z"),
    //         method = "lambda$protoChunkToFullChunk$34"
    // )
    // public void eclipticseasons$setChunk(ChunkHolder p_214855_,
    //                                      ChunkAccess p_214856_,
    //                                      CallbackInfoReturnable<ChunkAccess> cir,
    //                                      @Local ProtoChunk protoChunk ,
    //                                      @Local LevelChunk levelChunk) {
    //     if (protoChunk instanceof IChunkBiomeHolder holderOld
    //             && levelChunk instanceof IChunkBiomeHolder holder) {
    //         holder.eclipticseasons$setBiomeHolder(holderOld.eclipticseasons$getBiomeHolder());
    //     }
    // }
}
