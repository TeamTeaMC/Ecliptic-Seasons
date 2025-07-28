package com.teamtea.eclipticseasons.mixin.data;

import com.mojang.serialization.DynamicOps;
import org.spongepowered.asm.mixin.Mixin;


@Mixin({DynamicOps.class})
public interface MixinsDynamicOps {

    // @Inject(at = {@At("HEAD")}, method = {"mapBuilder"}, cancellable = true)
    // public default void eclipticseasons$mapBuilder(CallbackInfoReturnable<RecordBuilder> cir) {
    //    cir.setReturnValue(new CMapBuilder((DelegatingOps)(Object)this));
    // }

}
