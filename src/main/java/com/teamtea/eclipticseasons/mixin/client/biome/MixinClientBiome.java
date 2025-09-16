package com.teamtea.eclipticseasons.mixin.client.biome;


import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Biome.class})
public abstract class MixinClientBiome {

    @Inject(at = {@At("RETURN")}, method = {"getSkyColor"}, cancellable = true)
    public void eclipticseasons$getSkyColor(CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        int skyColor = BiomeColorsHandler.getSkyColor((Biome) (Object) this, returnValue);
        if (returnValue != skyColor) cir.setReturnValue(skyColor);
    }

    @Inject(at = {@At("RETURN")}, method = {"getWaterColor"}, cancellable = true)
    public void eclipticseasons$getWaterColor(CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        int waterColor = BiomeColorsHandler.getWaterColor((Biome) (Object) this, returnValue);
        if (returnValue != waterColor) cir.setReturnValue(waterColor);
    }

    @Inject(at = {@At("RETURN")}, method = {"getWaterFogColor"}, cancellable = true)
    public void eclipticseasons$getWaterFogColor(CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        int waterFogColor = BiomeColorsHandler.getWaterFogColor((Biome) (Object) this, returnValue);
        if (returnValue != waterFogColor) cir.setReturnValue(waterFogColor);
    }

    @Inject(at = {@At("RETURN")}, method = {"getFogColor"}, cancellable = true)
    public void eclipticseasons$getFogColor(CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        int fogColor = BiomeColorsHandler.getFogColor((Biome) (Object) this, returnValue);
        if (returnValue != fogColor) cir.setReturnValue(fogColor);
    }
}
