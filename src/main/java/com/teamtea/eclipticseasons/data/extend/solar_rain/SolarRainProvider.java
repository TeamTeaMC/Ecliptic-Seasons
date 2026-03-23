package com.teamtea.eclipticseasons.data.extend.solar_rain;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.climate.TemperateRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffects;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.common.registry.WeatherEffectRegistry;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class SolarRainProvider extends DatapackBuiltinEntriesProvider {

    public static final ResourceLocation SEASONAL_RAIN = EclipticSeasons.rl("seasonal_rain");
    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ESRegistries.WEATHER_EFFECT, WeatherEffectRegistry::bootstrap2)
            .add(ESRegistries.BIOME_RAIN, (context -> {
                HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);

                var weatherEffectHolderGetter = context.lookup(ESRegistries.WEATHER_EFFECT);

                context.register(ResourceKey.create(ESRegistries.BIOME_RAIN,
                                SEASONAL_RAIN),
                        CustomRainBuilder.builder()
                                .biomes(HolderSet.direct(lookup.getOrThrow(Biomes.PLAINS)))
                                .weathers(
                                        SolarTermValueMap.<List<CustomRainBuilder.Weather>>builder()

                                                // Spring
                                                .putSolarTerm(SolarTerm.BEGINNING_OF_SPRING, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(500, 1000)))
                                                                .rainDelay(Optional.of(UniformInt.of(30000, 60000)))
                                                                .rainChance(0.3f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.RAIN_WATER, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(1500, 3000)))
                                                                .rainDelay(Optional.of(UniformInt.of(8000, 12000)))
                                                                .rainChance(0.5F).build()
                                                ))
                                                .putSolarTerm(SolarTerm.INSECTS_AWAKENING, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(2500, 4500)))
                                                                .thunder(Optional.of(UniformInt.of(1000, 3000)))
                                                                .rainDelay(Optional.of(UniformInt.of(10000, 20000)))
                                                                .thunderChance(0.3f)
                                                                .rainChance(0.55F).build()
                                                ))
                                                .putSolarTerm(SolarTerm.SPRING_EQUINOX, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(3000, 5500)))
                                                                .rainDelay(Optional.of(UniformInt.of(6000, 15000)))
                                                                .rainChance(0.6f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.FRESH_GREEN, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(1000, 2500)))
                                                                .rainDelay(Optional.of(UniformInt.of(4000, 8000)))
                                                                .rainChance(0.75f)
                                                                .specialEffect(Optional.of(weatherEffectHolderGetter.getOrThrow(WeatherEffectRegistry.THIN_FOG)))
                                                                .timePeriod(List.of(TimePeriod.DAWN)).build()
                                                ))
                                                .putSolarTerm(SolarTerm.GRAIN_RAIN, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(5000, 8500)))
                                                                .rainDelay(Optional.of(UniformInt.of(3000, 7000)))
                                                                .rainChance(0.85f).build()
                                                ))

                                                // Summer
                                                .putSolarTerm(SolarTerm.BEGINNING_OF_SUMMER, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(5000, 8000)))
                                                                .rainDelay(Optional.of(UniformInt.of(15000, 30000)))
                                                                .rainChance(0.45f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.LESSER_FULLNESS, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(8000, 12000)))
                                                                .rainDelay(Optional.of(UniformInt.of(8000, 15000)))
                                                                .rainChance(0.65f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.GRAIN_IN_EAR, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(10000, 18000)))
                                                                .thunder(Optional.of(UniformInt.of(5000, 10000)))
                                                                .rainDelay(Optional.of(UniformInt.of(3000, 6000)))
                                                                .rainChance(0.8f)
                                                                .thunderChance(0.4f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.SUMMER_SOLSTICE, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(25000, 45000)))
                                                                .thunder(Optional.of(UniformInt.of(20000, 40000)))
                                                                .rainDelay(Optional.of(UniformInt.of(2000, 5000)))
                                                                .rainChance(0.95f)
                                                                .thunderChance(0.85f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.LESSER_HEAT, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(15000, 30000)))
                                                                .rainDelay(Optional.of(UniformInt.of(10000, 20000)))
                                                                .rainChance(0.7f)
                                                                .rainChance(0.6f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.GREATER_HEAT, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(12000, 25000)))
                                                                .rainDelay(Optional.of(UniformInt.of(12000, 25000)))
                                                                .rainChance(0.7f).build()
                                                ))

                                                // Autumn
                                                .putSolarTerm(SolarTerm.BEGINNING_OF_AUTUMN, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(4000, 8000)))
                                                                .rainDelay(Optional.of(UniformInt.of(25000, 50000)))
                                                                .rainChance(0.45f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.WHITE_DEW, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rainChance(0.15f)
                                                                .timePeriod(List.of(TimePeriod.DAWN))
                                                                .build()
                                                ))
                                                .putSolarTerm(SolarTerm.AUTUMNAL_EQUINOX, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(1000, 3000)))
                                                                .rainDelay(Optional.of(UniformInt.of(40000, 80000)))
                                                                .rainChance(0.3f)
                                                                .build()
                                                ))
                                                .putSolarTerm(SolarTerm.COLD_DEW, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rainChance(0.1f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.FIRST_FROST, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rainChance(0.01f).build()
                                                ))

                                                // Winter
                                                .putSolarTerm(SolarTerm.BEGINNING_OF_WINTER, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rainChance(0.05f)
                                                                .rainDelay(Optional.of(UniformInt.of(100000, 200000))).build()
                                                ))
                                                .putSolarTerm(SolarTerm.HEAVY_SNOW, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rainChance(0.35f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.WINTER_SOLSTICE, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rainChance(0f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.LESSER_COLD, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rainChance(0.05f).build()
                                                ))
                                                .putSolarTerm(SolarTerm.GREATER_COLD, List.of(
                                                        CustomRainBuilder.Weather.builder()
                                                                .rain(Optional.of(UniformInt.of(200, 800)))
                                                                .rainChance(0.1f).build()
                                                ))
                                                .build()
                                )
                                .build()
                );


            }));

    public SolarRainProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REGISTRY_SET_BUILDER, Set.of(EclipticSeasonsApi.MODID));
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + " SolarRainProvider";
    }
}