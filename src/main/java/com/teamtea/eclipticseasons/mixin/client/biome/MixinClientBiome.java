package com.teamtea.eclipticseasons.mixin.client.biome;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.misc.client.IBiomeColorHolder;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Biome.class})
public abstract class MixinClientBiome implements IBiomeColorHolder {

    @Unique
    private EnvironmentAttributeMap eclipticseasons$cacheEnvironmentAttributeMap;
    @Unique
    private boolean eclipticseasons$hasCachedEnvironmentAttributeMap;

    @ModifyReturnValue(at = {@At("RETURN")}, method = {"getAttributes"})
    public EnvironmentAttributeMap eclipticseasons$getSkyColor(EnvironmentAttributeMap original) {

        if (eclipticseasons$biomeColor != null) {
            if (!eclipticseasons$hasCachedEnvironmentAttributeMap
                    && eclipticseasons$cacheEnvironmentAttributeMap == null) {
                eclipticseasons$hasCachedEnvironmentAttributeMap = true;
                eclipticseasons$cacheEnvironmentAttributeMap = BiomeColorsHandler.buildEnvironmentAttributeMap(
                        original, (Biome) (Object) this);
            }
            if (eclipticseasons$cacheEnvironmentAttributeMap != null)
                original = eclipticseasons$cacheEnvironmentAttributeMap;
        }
        return original;
    }

    @ModifyReturnValue(method = "getWaterColor", at = @At("RETURN"))
    private int eclipticseasons$getWaterColor(int original) {
        return BiomeColorsHandler.getWaterColor(Biome.class.cast(this), original);
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
