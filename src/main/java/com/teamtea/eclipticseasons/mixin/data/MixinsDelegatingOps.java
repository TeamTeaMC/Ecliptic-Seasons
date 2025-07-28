package com.teamtea.eclipticseasons.mixin.data;

import net.minecraft.resources.DelegatingOps;
import org.spongepowered.asm.mixin.Mixin;


@Mixin({DelegatingOps.class})
public class MixinsDelegatingOps {

    // @Inject(at = {@At("HEAD")}, method = {"mapBuilder"}, cancellable = true)
    // public void eclipticseasons$mapBuilder(CallbackInfoReturnable<RecordBuilder> cir) {
    //    cir.setReturnValue(new CMapBuilder((DelegatingOps)(Object)this));
    // }

}
