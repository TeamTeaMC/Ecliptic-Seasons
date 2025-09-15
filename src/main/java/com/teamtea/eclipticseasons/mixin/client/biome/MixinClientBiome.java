package com.teamtea.eclipticseasons.mixin.client.biome;


import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.misc.client.IBiomeColorHolder;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Biome.class})
public abstract class MixinClientBiome implements IBiomeColorHolder {

    @Inject(at = {@At("RETURN")}, method = {"getSkyColor"}, cancellable = true)
    public void eclipticseasons$getSkyColor(CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        int skyColor = BiomeColorsHandler.getSkyColor((Biome) (Object) this, returnValue);
        if (returnValue != skyColor) cir.setReturnValue(skyColor);
    }


    // ======================================================

    @Unique
    private BiomeColor.Instance eclipticseasons$biomeColor = null;

    @Override
    public BiomeColor.Instance getBiomeColor() {
        return eclipticseasons$biomeColor;
    }

    @Override
    public void setBiomeColor(BiomeColor.Instance biomeColor) {
        this.eclipticseasons$biomeColor = biomeColor;
    }


}
