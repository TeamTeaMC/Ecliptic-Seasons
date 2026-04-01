package com.teamtea.eclipticseasons.mixin.client.biome;


import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.misc.client.IBiomeColorHolder;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mixin({Biome.class})
public abstract class MixinClientBiome implements IBiomeColorHolder {

    @Unique
    private EnvironmentAttributeMap eclipticseasons$cacheEnvironmentAttributeMap;
    @Unique
    private boolean eclipticseasons$hasCachedEnvironmentAttributeMap;

    @Inject(at = {@At("RETURN")}, method = {"getAttributes"}, cancellable = true)
    public void eclipticseasons$getSkyColor(CallbackInfoReturnable<EnvironmentAttributeMap> cir) {

        if (eclipticseasons$biomeColor != null) {
            if (!eclipticseasons$hasCachedEnvironmentAttributeMap
                    && eclipticseasons$cacheEnvironmentAttributeMap == null) {
                eclipticseasons$hasCachedEnvironmentAttributeMap = true;
                eclipticseasons$cacheEnvironmentAttributeMap = BiomeColorsHandler.buildEnvironmentAttributeMap(
                        cir.getReturnValue(),(Biome) (Object) this);
            }
            if (eclipticseasons$cacheEnvironmentAttributeMap != null)
                cir.setReturnValue(eclipticseasons$cacheEnvironmentAttributeMap);
        }
    }

    @Inject(at = {@At("RETURN")}, method = {"getWaterColor"}, cancellable = true)
    public void eclipticseasons$getWaterColor(CallbackInfoReturnable<Integer> cir) {
        int returnValue = cir.getReturnValue();
        int waterColor = BiomeColorsHandler.getWaterColor((Biome) (Object) this, returnValue);
        if (returnValue != waterColor) cir.setReturnValue(waterColor);
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
        setSeasonChanged();
    }

    @Override
    public void setSeasonChanged() {
        this.eclipticseasons$cacheEnvironmentAttributeMap = null;
        this.eclipticseasons$hasCachedEnvironmentAttributeMap = false;
    }
}
