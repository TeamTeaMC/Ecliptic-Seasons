package com.teamtea.eclipticseasons.mixin.compat.journeymap;


import com.teamtea.eclipticseasons.common.misc.MapColorReplacer;
import journeymap.client.JourneymapClient;
import journeymap.client.model.BlockMD;
import journeymap.client.model.ChunkMD;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockMD.class})
public abstract class MixinBlockMD {


    @Shadow(remap = false)
    @Final
    private BlockState blockState;

    @Inject(at = {@At(value = "HEAD")},
            method = {"getBlockColor"},
            cancellable = true,
            remap = false)
    private void ecliptic$getBlockColor(ChunkMD chunkMD, BlockPos blockPos, CallbackInfoReturnable<Integer> cir) {
        if (MapColorReplacer.getTopSnowColor(chunkMD.getWorld(), blockState, blockPos)
                == MapColor.SNOW) {
            cir.setReturnValue(MapColor.SNOW.col);

        }
    }

}
