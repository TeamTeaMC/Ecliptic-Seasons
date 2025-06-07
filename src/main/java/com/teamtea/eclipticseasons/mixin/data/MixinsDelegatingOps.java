package com.teamtea.eclipticseasons.mixin.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.RecordBuilder;
import com.teamtea.eclipticseasons.data.datapack.CMapBuilder;
import net.minecraft.resources.DelegatingOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin({DelegatingOps.class})
public class MixinsDelegatingOps {

    // @Inject(at = {@At("HEAD")}, method = {"mapBuilder"}, cancellable = true)
    // public void eclipticseasons$mapBuilder(CallbackInfoReturnable<RecordBuilder> cir) {
    //    cir.setReturnValue(new CMapBuilder((DelegatingOps)(Object)this));
    // }

}
