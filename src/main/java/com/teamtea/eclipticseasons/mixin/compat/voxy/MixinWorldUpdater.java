package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import me.cortex.voxy.common.voxelization.VoxelizedSection;
import me.cortex.voxy.common.world.WorldEngine;
import me.cortex.voxy.common.world.WorldUpdater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldUpdater.class})
public class MixinWorldUpdater {
    @Inject(
            remap = false,
            method = "insertUpdate",
            at = @At(value = "HEAD")
    )
    private static void eclipticseasons$insertUpdate(WorldEngine into, VoxelizedSection section, CallbackInfo ci) {
        //if (into.isLive()) {
        //    if(!MapChecker.isLoaded(ClientCon.getUseLevel(),section.x
        //    ,section.z)){
        //        int y=0;
        //    }
        //}
    }


    @Unique
    public long eclipticSeasons$get(VoxelizedSection voxelizedSection, int lvl, int x, int y, int z) {
        int offset = lvl == 1 ? 4096 : 0;
        offset |= lvl == 2 ? 4608 : 0;
        offset |= lvl == 3 ? 4672 : 0;
        offset |= lvl == 4 ? 4680 : 0;
        return voxelizedSection.section[eclipticSeasons$getIdx(x, y, z, 0, 4 - lvl) + offset];
    }

    @Unique
    public void eclipticSeasons$set(VoxelizedSection voxelizedSection, int lvl, int x, int y, int z, long value) {
        int offset = lvl == 1 ? 4096 : 0;
        offset |= lvl == 2 ? 4608 : 0;
        offset |= lvl == 3 ? 4672 : 0;
        offset |= lvl == 4 ? 4680 : 0;
        voxelizedSection.section[eclipticSeasons$getIdx(x, y, z, 0, 4 - lvl) + offset] = value;
    }

    @Unique
    private static int eclipticSeasons$getIdx(int x, int y, int z, int shiftBy, int size) {
        int M = (1 << size) - 1;
        x = x >> shiftBy & M;
        y = y >> shiftBy & M;
        z = z >> shiftBy & M;
        return y << (size << 1) | z << size | x;
    }

}
