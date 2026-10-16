package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;

import java.util.List;
import java.util.Optional;

public class BiomeRainRegistry {
    public static final ResourceKey<CustomRainBuilder> BETTER_PLAINS = createKey("better_plains");

    private static ResourceKey<CustomRainBuilder> createKey(String name) {
        return ResourceKey.create(ESRegistries.BIOME_RAIN, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstapContext<CustomRainBuilder> context) {
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
                        .rain(Optional.of(UniformInt.of(5400, 9000)))
                        .rainDelay(Optional.of(UniformInt.of(24000, 36000)))
                        .snowMeltSpeed(0.75f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.RAIN_WATER, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.32F).weight(10)
                        .rain(Optional.of(UniformInt.of(3600, 7200)))
                        .rainDelay(Optional.of(UniformInt.of(30000, 48000)))
                        .thunder(Optional.of(UniformInt.of(6000, 12000)))
                        .snowMeltSpeed(0.85f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.INSECTS_AWAKENING, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.55F).thunderChance(0.6F).weight(10)
                        .rain(Optional.of(UniformInt.of(4342, 7598)))
                        .rainDelay(Optional.of(UniformInt.of(29186, 46433)))
                        .thunder(Optional.of(UniformInt.of(12000, 20000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.SPRING_EQUINOX, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.4F).weight(10)
                        .rain(Optional.of(UniformInt.of(4800, 8400)))
                        .rainDelay(Optional.of(UniformInt.of(24000, 42000)))
                        .thunder(Optional.of(UniformInt.of(6000, 12000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));

        map.put(SolarTerm.FRESH_GREEN, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.68F)
                        .thunderChance(0.15F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(4116, 7409)))
                        .rainDelay(Optional.of(UniformInt.of(34986, 55977)))
                        .thunder(Optional.of(UniformInt.of(4000, 8000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow()))
                        .build()
        ));

        map.put(SolarTerm.GRAIN_RAIN, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.78F)
                        .thunderChance(0.05F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(4800, 8000)))
                        .rainDelay(Optional.of(UniformInt.of(32400, 54000)))
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
                        .rain(Optional.of(UniformInt.of(4000, 7200)))
                        .rainDelay(Optional.of(UniformInt.of(36000, 63000)))
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
                        .rain(Optional.of(UniformInt.of(4000, 6400)))
                        .rainDelay(Optional.of(UniformInt.of(39600, 64800)))
                        .snowMeltSpeed(1.2F)
                        .thunder(Optional.of(UniformInt.of(8000, 18000)))
                        .build()
        ));

        map.put(SolarTerm.GRAIN_IN_EAR, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.68F)
                        .thunderChance(0.55F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(3293, 5762)))
                        .rainDelay(Optional.of(UniformInt.of(41983, 66473)))
                        .snowMeltSpeed(1.2F)
                        .thunder(Optional.of(UniformInt.of(12000, 24000)))
                        .build()
        ));

        map.put(SolarTerm.SUMMER_SOLSTICE, List.of(
                CustomRainBuilder.Weather.builder()
                        .rainChance(0.70F)
                        .thunderChance(0.85F)
                        .weight(10)
                        .rain(Optional.of(UniformInt.of(2400, 4800)))
                        .rainDelay(Optional.of(UniformInt.of(45000, 72000)))
                        .snowMeltSpeed(1.2F)
                        .thunder(Optional.of(UniformInt.of(16000, 32000)))
                        .build()
        ));

        map.put(SolarTerm.LESSER_HEAT, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.65F).thunderChance(0.8F).weight(10)
                        .rain(Optional.of(UniformInt.of(2642, 5283)))
                        .rainDelay(Optional.of(UniformInt.of(45789, 73590)))
                        .snowMeltSpeed(1.25f)
                        .thunder(Optional.of(UniformInt.of(16000, 32000))).build()
        ));
        map.put(SolarTerm.GREATER_HEAT, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.2F).weight(10)
                        .rain(Optional.of(UniformInt.of(3600, 6000)))
                        .rainDelay(Optional.of(UniformInt.of(38400, 60000)))
                        .snowMeltSpeed(1.35f)
                        .thunder(Optional.of(UniformInt.of(8000, 16000))).build()
        ));

        // Autumn
        map.put(SolarTerm.BEGINNING_OF_AUTUMN, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.42F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(4231, 7051)))
                        .rainDelay(Optional.of(UniformInt.of(35740, 56164)))
                        .snowMeltSpeed(1.05f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.END_OF_HEAT, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.4F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(2939, 7348)))
                        .rainDelay(Optional.of(UniformInt.of(39192, 58788)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.WHITE_DEW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.35F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(3271, 6541)))
                        .rainDelay(Optional.of(UniformInt.of(39625, 57236)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.AUTUMNAL_EQUINOX, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.32F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(3499, 6997)))
                        .rainDelay(Optional.of(UniformInt.of(37044, 57624)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.COLD_DEW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.3F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(3600, 7200)))
                        .rainDelay(Optional.of(UniformInt.of(40000, 56000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.FIRST_FROST, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.25F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(3600, 7200)))
                        .rainDelay(Optional.of(UniformInt.of(40000, 60000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));

        // winter
        map.put(SolarTerm.BEGINNING_OF_WINTER, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.3F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(7200, 10800)))
                        .rainDelay(Optional.of(UniformInt.of(28000, 40000)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.LIGHT_SNOW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.4F).thunderChance(0.2F).weight(10)
                        .rain(Optional.of(UniformInt.of(7348, 11758)))
                        .rainDelay(Optional.of(UniformInt.of(27434, 41151)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.HEAVY_SNOW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.5F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(7200, 12000)))
                        .rainDelay(Optional.of(UniformInt.of(26400, 42000)))
                        .snowAccumulationSpeed(0.85f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.WINTER_SOLSTICE, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.45F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(7960, 13266)))
                        .rainDelay(Optional.of(UniformInt.of(26051, 41247))).build()
        ));
        map.put(SolarTerm.LESSER_COLD, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.4F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(7348, 13227)))
                        .rainDelay(Optional.of(UniformInt.of(25475, 39192)))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.MODERATE_RAIN_SNOW).orElseThrow())).build()
        ));
        map.put(SolarTerm.GREATER_COLD, List.of(
                CustomRainBuilder.Weather.builder().rainChance(0.2F).thunderChance(0.0F).weight(10)
                        .rain(Optional.of(UniformInt.of(7200, 12600)))
                        .rainDelay(Optional.of(UniformInt.of(25600, 40000)))
                        .snowAccumulationSpeed(0.85f)
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.LIGHT_RAIN_SNOW).orElseThrow())).build()
        ));

        context.register(BETTER_PLAINS, new CustomRainBuilder(
                HolderSet.direct(holderGetter.getOrThrow(Biomes.PLAINS)),
                solarTermValueMap2
        ));
    }
}
