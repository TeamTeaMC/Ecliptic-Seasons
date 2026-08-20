package com.teamtea.eclipticseasons.mixin.client.biome;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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

    @ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
    private int eclipticseasons$getSkyColor(int original) {
        return BiomeColorsHandler.getSkyColor((Biome) (Object) this, original);
    }

    @ModifyReturnValue(method = "getWaterColor", at = @At("RETURN"))
    private int eclipticseasons$getWaterColor(int original) {
        return BiomeColorsHandler.getWaterColor((Biome) (Object) this, original);
    }

    @ModifyReturnValue(method = "getWaterFogColor", at = @At("RETURN"))
    private int eclipticseasons$getWaterFogColor(int original) {
        return BiomeColorsHandler.getWaterFogColor((Biome) (Object) this, original);
    }

    @ModifyReturnValue(method = "getFogColor", at = @At("RETURN"))
    private int eclipticseasons$getFogColor(int original) {
        return BiomeColorsHandler.getFogColor((Biome) (Object) this, original);
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
