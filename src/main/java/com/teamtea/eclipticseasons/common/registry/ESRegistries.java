package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.climate.CropClimateType;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ESRegistries {

    public static final ResourceKey<Registry<CropClimateType>> CROP_CLIMATE = ResourceKey.createRegistryKey(EclipticSeasons.rl("crop_climate"));

    public static final ResourceKey<Registry<CropGrowControlBuilder>> CROP = ResourceKey.createRegistryKey(EclipticSeasons.rl("crop"));

}
