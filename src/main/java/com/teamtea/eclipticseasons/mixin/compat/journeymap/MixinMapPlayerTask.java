package com.teamtea.eclipticseasons.mixin.compat.journeymap;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import journeymap.client.cartography.ChunkRenderController;
import journeymap.client.data.DataCache;
import journeymap.client.model.ChunkMD;
import journeymap.client.model.MapType;
import journeymap.client.task.multi.BaseMapTask;
import journeymap.client.task.multi.MapPlayerTask;
import journeymap.client.task.multi.RenderSpec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Mixin({MapPlayerTask.class})
public abstract class MixinMapPlayerTask extends BaseMapTask {


    public MixinMapPlayerTask(ChunkRenderController renderController, Level world, MapType mapType, Collection<ChunkPos> chunkCoords, boolean flushCacheWhenDone, boolean asyncFileWrites, int elapsedLimit) {
        super(renderController, world, mapType, chunkCoords, flushCacheWhenDone, asyncFileWrites, elapsedLimit);
        // this.mapType=mapType;
        // this.chunkCoords=chunkCoords;
    }
    //
    // // @Shadow(remap = false)
    // // private MapType mapType;
    // // @Shadow(remap = false)
    // // private Collection<ChunkPos> chunkCoords;
    // @Shadow(remap = false)
    // private int scheduledChunks;
    //
    // @WrapOperation(at = {@At(value = "INVOKE", target = "Ljava/util/List;removeIf(Ljava/util/function/Predicate;)Z")},
    //         method = {"initTask"},
    //         remap = false)
    // private boolean ecliptic$initTask(List<ChunkPos> instance, Predicate<ChunkPos> predicate, Operation<Boolean> original,
    //                                   @Local RenderSpec renderSpec) {
    //     // if (mapType.isSurface()) {
    //     //     this.chunkCoords.addAll(chunkCoords);
    //     //     this.scheduledChunks = this.chunkCoords.size();
    //     //     ci.cancel();
    //     // }
    //     if (renderSpec == RenderSpec.getSurfaceSpec()) {
    //         return instance.removeIf((chunkPos) -> {
    //             ChunkMD chunkMD = DataCache.INSTANCE.getChunkMD(chunkPos);
    //             if (chunkMD != null && chunkMD.hasChunk()) {
    //                 return !chunkMD.getDimension().equals(ClientCon.useLevel.dimension());
    //             }
    //             return false;
    //         });
    //     }
    //     return original.call(instance, predicate);
    // }

}
