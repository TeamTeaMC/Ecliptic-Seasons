package com.teamtea.eclipticseasons.mixin.client;


import com.teamtea.eclipticseasons.api.data.client.BiomeColor;
import com.teamtea.eclipticseasons.api.misc.client.IBiomeColorHolder;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Biome.class})
public abstract class MixinClientBiome implements IBiomeColorHolder {

    @Unique
    private  BiomeColor.Instance eclipticseasons$biomeColor = null;

    @Override
    public  BiomeColor.Instance getBiomeColor() {
        return eclipticseasons$biomeColor;
    }

    @Override
    public void setBiomeColor( BiomeColor.Instance biomeColor) {
        this.eclipticseasons$biomeColor = biomeColor;
    }
}
