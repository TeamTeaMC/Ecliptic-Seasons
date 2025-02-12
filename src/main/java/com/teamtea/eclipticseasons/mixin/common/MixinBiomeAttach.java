package com.teamtea.eclipticseasons.mixin.common;


import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Biome.class)
public interface MixinBiomeAttach {
    @Accessor("climateSettings")
    Biome.Climate getBiomeClimateSettings();
}
