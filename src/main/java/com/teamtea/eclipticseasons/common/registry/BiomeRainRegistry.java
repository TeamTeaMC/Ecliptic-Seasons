package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Optional;

public class BiomeRainRegistry {
    public static final ResourceKey<CustomRainBuilder> BETTER_PLAINS = createKey("better_plains");

    private static ResourceKey<CustomRainBuilder> createKey(String name) {
        return ResourceKey.create(ESRegistries.BIOME_RAIN, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<CustomRainBuilder> context) {
        var holderGetter = context.lookup(Registries.BIOME);

        var holderGetter2 = context.lookup(ESRegistries.WEATHER_EFFECT);

        var solarTermValueMap2 = SolarTermValueMap.<List<CustomRainBuilder.Weather>>builder().solarTermMap(new Enum2ObjectMap<>(SolarTerm.class)).build();
        Enum2ObjectMap<SolarTerm, List<CustomRainBuilder.Weather>> map = solarTermValueMap2.solarTermMap().get();
        // map.put(SolarTerm.LIGHT_SNOW, List.of(
        //         CustomRainBuilder.Weather.builder().rainChance(TemperateRain.LIGHT_SNOW.getRainChance())
        //                 .weight(8)
        //                 .build(),
        //         CustomRainBuilder.Weather.builder().rainChance(TemperateRain.LIGHT_SNOW.getRainChance())
        //                 .timePeriod(List.of(TimePeriod.MIDNIGHT))
        //                 .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.SNOW).get()))
        //                 .build()
        // ));
        // map.put(SolarTerm.RAIN_WATER, List.of(
        //         CustomRainBuilder.Weather.builder().rainChance(TemperateRain.RAIN_WATER.getRainChance())
        //                 .weight(8)
        //                 .build(),
        //         CustomRainBuilder.Weather.builder().rainChance(TemperateRain.RAIN_WATER.getRainChance())
        //                 .timePeriod(List.of(TimePeriod.DAWN))
        //                 .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.JUST_FOG).get()))
        //                 .build()
        // ));

        // Spring
        map.put(SolarTerm.BEGINNING_OF_SPRING, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.3F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(12000, 20000)))
                        .rainDelay(Optional.of(UniformInt.of(40000, 72000)))
                        .snowMeltSpeed(0.75f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.RAIN_WATER, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.32F).weight(10)
                        .rain(Optional.of(UniformInt.of(16000, 24000)))
                        .rainDelay(Optional.of(UniformInt.of(32000, 50000)))
                        .thunder(Optional.of(UniformInt.of(6000, 12000)))
                        .snowMeltSpeed(0.85f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.INSECTS_AWAKENING, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.55F).thunderChance(0.6F).weight(10)
                        .rain(Optional.of(UniformInt.of(12000, 18000)))
                        .rainDelay(Optional.of(UniformInt.of(24000, 48000)))
                        .thunder(Optional.of(UniformInt.of(12000, 20000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.SPRING_EQUINOX, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.4F).weight(10)
                        .rain(Optional.of(UniformInt.of(12000, 24000)))
                        .rainDelay(Optional.of(UniformInt.of(32000, 64000)))
                        .thunder(Optional.of(UniformInt.of(6000, 12000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));

        map.put(SolarTerm.FRESH_GREEN, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.68F)
                        .thunderChance(0.15F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(18000, 36000)))
                        .rainDelay(Optional.of(UniformInt.of(6000, 18000)))
                        .thunder(Optional.of(UniformInt.of(4000, 8000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow()))
                        .build()
        ));

        map.put(SolarTerm.GRAIN_RAIN, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.78F)
                        .thunderChance(0.05F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(24000, 52000)))
                        .rainDelay(Optional.of(UniformInt.of(6000, 16000)))
                        .thunder(Optional.of(UniformInt.of(4000, 8000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow()))
                        .build()
        ));

        // Summer
        map.put(SolarTerm.BEGINNING_OF_SUMMER, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.72F)
                        .thunderChance(0.15F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(22000, 44000)))
                        .rainDelay(Optional.of(UniformInt.of(10000, 24000)))
                        .snowMeltSpeed(1.2F)
                        .thunder(Optional.of(UniformInt.of(4000, 10000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow()))
                        .build()
        ));

        map.put(SolarTerm.LESSER_FULLNESS, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.72F)
                        .thunderChance(0.35F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(20000, 42000)))
                        .rainDelay(Optional.of(UniformInt.of(12000, 28000)))
                        .snowMeltSpeed(1.2F)
                        .thunder(Optional.of(UniformInt.of(8000, 18000)))
                        .build()
        ));

        map.put(SolarTerm.GRAIN_IN_EAR, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.68F)
                        .thunderChance(0.55F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(18000, 36000)))
                        .rainDelay(Optional.of(UniformInt.of(12000, 30000)))
                        .snowMeltSpeed(1.2F)
                        .thunder(Optional.of(UniformInt.of(12000, 24000)))
                        .build()
        ));

        map.put(SolarTerm.SUMMER_SOLSTICE, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.70F)
                        .thunderChance(0.85F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(12000, 28000)))
                        .rainDelay(Optional.of(UniformInt.of(18000, 42000)))
                        .snowMeltSpeed(1.2F)
                        .thunder(Optional.of(UniformInt.of(16000, 32000)))
                        .build()
        ));
        map.put(SolarTerm.LESSER_HEAT, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.65F).thunderChance(0.8F).weight(10)
                        .rain(Optional.of(UniformInt.of(16000, 32000)))
                        .rainDelay(Optional.of(UniformInt.of(32000, 64000)))
                        .snowMeltSpeed(1.25f)
                        .thunder(Optional.of(UniformInt.of(16000, 32000))).build()
        ));
        map.put(SolarTerm.GREATER_HEAT, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.2F).weight(10)
                        .rain(Optional.of(UniformInt.of(12000, 24000)))
                        .rainDelay(Optional.of(UniformInt.of(48000, 96000)))
                        .snowMeltSpeed(1.35f)
                        .thunder(Optional.of(UniformInt.of(8000, 16000))).build()
        ));

        // Autumn
        map.put(SolarTerm.BEGINNING_OF_AUTUMN, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.42F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(8000, 16000)))
                        .rainDelay(Optional.of(UniformInt.of(64000, 128000)))
                        .snowMeltSpeed(1.05f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.END_OF_HEAT, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.4F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(6000, 12000)))
                        .rainDelay(Optional.of(UniformInt.of(72000, 144000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.WHITE_DEW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.35F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(4000, 8000)))
                        .rainDelay(Optional.of(UniformInt.of(96000, 150000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.AUTUMNAL_EQUINOX, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.32F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(4000, 8000)))
                        .rainDelay(Optional.of(UniformInt.of(96000, 150000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.COLD_DEW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.3F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(2000, 6000)))
                        .rainDelay(Optional.of(UniformInt.of(100000, 140000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.FIRST_FROST, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.25F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(2000, 4000)))
                        .rainDelay(Optional.of(UniformInt.of(120000, 150000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));

        // winter
        map.put(SolarTerm.BEGINNING_OF_WINTER, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.3F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(2000, 4000)))
                        .rainDelay(Optional.of(UniformInt.of(90000, 120000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.LIGHT_SNOW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.4F).thunderChance(0.2F).weight(10)
                        .rain(Optional.of(UniformInt.of(8000, 16000)))
                        .rainDelay(Optional.of(UniformInt.of(72000, 90000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.HEAVY_SNOW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(16000, 32000)))
                        .rainDelay(Optional.of(UniformInt.of(48000, 80000)))
                        .snowAccumulationSpeed(0.85f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.WINTER_SOLSTICE, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.45F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(8000, 16000)))
                        .rainDelay(Optional.of(UniformInt.of(72000, 96000))).build()
        ));
        map.put(SolarTerm.LESSER_COLD, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.4F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(4000, 8000)))
                        .rainDelay(Optional.of(UniformInt.of(72000, 96000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.GREATER_COLD, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.2F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(2000, 5000)))
                        .rainDelay(Optional.of(UniformInt.of(96000, 144000)))
                        .snowAccumulationSpeed(0.85f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));

        context.register(BETTER_PLAINS, new CustomRainBuilder(
                holderGetter.getOrThrow(Tags.Biomes.IS_TEMPERATE_OVERWORLD),
                solarTermValueMap2
        ));
    }
}
