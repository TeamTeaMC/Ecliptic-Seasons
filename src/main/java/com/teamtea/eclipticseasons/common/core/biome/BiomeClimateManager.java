package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.api.util.EclipticTagTool;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.SeasonTypeBiomeTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.mixin.common.MixinBiomeAttach;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.server.ServerLifecycleHooks;


import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class BiomeClimateManager {
    public final static Map<Biome, Float> BIOME_DEFAULT_TEMPERATURE_MAP = new IdentityHashMap<>();


    public static void resetBiomeTemps() {

        BIOME_DEFAULT_TEMPERATURE_MAP.clear();
        DynamicRegistries.builtin().registry(Registry.BIOME_REGISTRY).ifPresent(b -> b.forEach(biome ->
        {

            BIOME_DEFAULT_TEMPERATURE_MAP.put(biome, ((MixinBiomeAttach) (Object) biome).getBiomeClimateSettings().temperature);
        }));
    }

    public static float getDefaultTemperature(Biome biome) {
        return BiomeClimateManager.BIOME_DEFAULT_TEMPERATURE_MAP.getOrDefault(biome, 0.6F);
    }

    public static final float SNOW_LEVEL = 0.15F;
    public static final float FROZEN_OCEAN_MELT_LEVEL = 0.1F;

    public static void updateTemperature(World level, SolarTerm solarTermIndex) {
        {
            level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).forEach(biome ->
            {
                float temperature1 = ((MixinBiomeAttach) (Object) biome).getBiomeClimateSettings().temperature;
                float temperature = temperature1 > SNOW_LEVEL ?
                        Math.max(SNOW_LEVEL + 0.001F, temperature1 + solarTermIndex.getTemperatureChange()) :
                        Math.min(SNOW_LEVEL,temperature1+ solarTermIndex.getTemperatureChange());
                BIOME_DEFAULT_TEMPERATURE_MAP.put(biome,temperature);
                // var oldClimateSettings = biome.climateSettings;
                // biome.climateSettings = new Biome.ClimateSettings(
                //         oldClimateSettings.hasPrecipitation(),
                //         temperature,
                //         oldClimateSettings.temperatureModifier(),
                //         oldClimateSettings.downfall());
            });
        }
    }

    public static float fixTemp(World level, Biome biome, float temp) {
        SolarTerm solarTermIndex = EclipticUtil.getNowSolarTerm(level);
        float temperatureBiome = ((MixinBiomeAttach) (Object) biome).getBiomeClimateSettings().temperature;
        float temperatureGround = temperatureBiome > SNOW_LEVEL ?
                Math.max(SNOW_LEVEL + 0.001F, temperatureBiome + solarTermIndex.getTemperatureChange()) :
                Math.min(SNOW_LEVEL, temperatureBiome + solarTermIndex.getTemperatureChange());
        temp += -temperatureGround + temperatureBiome;
        return temp;
    }


    public static float agent$GetBaseTemperature(Biome biome) {
        return getDefaultTemperature(biome);
    }

    public static boolean agent$hasPrecipitation(Biome biome) {
        return !EclipticTagTool.getTag(biome).equals(SeasonTypeBiomeTags.RAINLESS);
    }
}
