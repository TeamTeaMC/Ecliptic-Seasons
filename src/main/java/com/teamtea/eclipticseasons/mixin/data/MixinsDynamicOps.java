package com.teamtea.eclipticseasons.mixin.data;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.teamtea.eclipticseasons.data.datapack.CMapBuilder;
import net.minecraft.resources.DelegatingOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin({DynamicOps.class})
public interface MixinsDynamicOps {

    // @Inject(at = {@At("HEAD")}, method = {"mapBuilder"}, cancellable = true)
    // public default void eclipticseasons$mapBuilder(CallbackInfoReturnable<RecordBuilder> cir) {
    //    cir.setReturnValue(new CMapBuilder((DelegatingOps)(Object)this));
    // }

}
