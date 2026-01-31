package com.teamtea.eclipticseasons.mixin.compat.voxy.bigglobe;

import builderb0y.bigglobe.blocks.BlockStates;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BlockStates.class)
public abstract class MixinBGBlockStates {


    //@Inject(
    //        remap = false,
    //        method = "of",
    //        at = @At(value = "HEAD"),
    //        cancellable = true)
    //private static void eclipticseasons$voxy_bigglobe_of(String name, CallbackInfoReturnable<BlockState> cir) {
    //    VoxyTool.fixBigGlobeOfBlockStates(name,cir);
    //}


}