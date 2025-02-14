package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.BiomeClimateModifier;
import com.teamtea.eclipticseasons.api.data.WetterStructure;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ESRegistries {
    public static final ResourceKey<Registry<WetterStructure>> WETTER = ResourceKey.createRegistryKey(EclipticSeasons.rl("wetter"));

    public static final ResourceKey<Registry<BiomeClimateModifier>> BIOME_CLIMATE = ResourceKey.createRegistryKey(EclipticSeasons.rl("biome_climate_modifier"));

    public static final ResourceKey<Registry<CropGrowControlBuilder>> CROP = ResourceKey.createRegistryKey(EclipticSeasons.rl("crop"));

}
