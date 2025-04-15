package com.teamtea.eclipticseasons.mixin.common;


import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Heightmap.Types.class})
public abstract class MixinHeightmap_Types {

    //
    // @Final
    // @Shadow(remap = false)
    // public static Heightmap.Types MOTION_BLOCKING_NO_LEAVES;

    @Inject(at = {@At(value = "RETURN")}, method = {"sendToClient"}, cancellable = true)
    private void eclipticseasons$no_leaves_sendToClient(CallbackInfoReturnable<Boolean> cir) {
        // if ((Object) this == Heightmap.Types.MOTION_BLOCKING_NO_LEAVES && !cir.getReturnValue()) {
        //     cir.setReturnValue(true);
        // }
    }
}
