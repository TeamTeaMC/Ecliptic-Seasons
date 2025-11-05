package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.TemperateRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.Tags;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public class BiomeRainRegistry {
    public static final ResourceKey<CustomRainBuilder> PLAIN = createKey("plain");
    public static final ResourceKey<CustomRainBuilder> BETTER_PLAINS = createKey("better_plains");

    private static ResourceKey<CustomRainBuilder> createKey(String name) {
        return ResourceKey.create(ESRegistries.BIOME_RAIN, EclipticSeasons.rl(name));
    }

    public static void bootstrap2(BootstapContext<CustomRainBuilder> context) {
        var holderGetter = context.lookup(Registries.BIOME);
        var holderGetter2 = context.lookup(ESRegistries.WEATHER_EFFECT);
        var solarTermValueMap = SolarTermValueMap.<List<CustomRainBuilder.Weather>>builder().solarTermMap(new Enum2ObjectMap<>(SolarTerm.class)).build();
        for (int i = 0; i < TemperateRain.collectValues().length; i++) {
            TemperateRain temperateRain = TemperateRain.collectValues()[i];
            SolarTerm solarTerm = temperateRain.getSolarTerm();
            boolean isSpring = solarTerm == SolarTerm.SPRING_EQUINOX;
            solarTermValueMap.solarTermMap().get().put(
                    solarTerm, List.of(
                            CustomRainBuilder.Weather.builder()
                                    .rainChance(temperateRain.getRainChance())
                                    .thunderChance(temperateRain.getThunderChance())
                                    .rainDelay(isSpring ? Optional.of(ServerLevel.RAIN_DELAY) : Optional.empty())
                                    .rain(isSpring ? Optional.of(ServerLevel.RAIN_DURATION) : Optional.empty())
                                    .thunder(isSpring ? Optional.of(ServerLevel.THUNDER_DURATION) : Optional.empty())
                                    .build()
                    )
            );
        }
        context.register(PLAIN, new CustomRainBuilder(
                HolderSet.direct(holderGetter.getOrThrow(Biomes.PLAINS)),
                solarTermValueMap
        ));

        var solarTermValueMap2 = SolarTermValueMap.<List<CustomRainBuilder.Weather>>builder().solarTermMap(new Enum2ObjectMap<>(SolarTerm.class)).build();
        solarTermValueMap2.solarTermMap().get().put(SolarTerm.LIGHT_SNOW, List.of(
                CustomRainBuilder.Weather.builder().rainChance(TemperateRain.LIGHT_SNOW.getRainChance())
                        .timePeriod(List.of(TimePeriod.DAWN, TimePeriod.DAY, TimePeriod.DUSK, TimePeriod.NIGHT))
                        .build(),
                CustomRainBuilder.Weather.builder().rainChance(TemperateRain.LIGHT_SNOW.getRainChance())
                        .timePeriod(List.of(TimePeriod.MIDNIGHT))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.SNOW).get()))
                        .build()
        ));
        solarTermValueMap2.solarTermMap().get().put(SolarTerm.RAIN_WATER, List.of(
                CustomRainBuilder.Weather.builder().rainChance(TemperateRain.RAIN_WATER.getRainChance())
                        .timePeriod(List.of(TimePeriod.MIDNIGHT, TimePeriod.DAY, TimePeriod.DUSK, TimePeriod.NIGHT))
                        .build(),
                CustomRainBuilder.Weather.builder().rainChance(TemperateRain.RAIN_WATER.getRainChance())
                        .timePeriod(List.of(TimePeriod.DAWN))
                        .specialEffect(Optional.of(holderGetter2.get(WeatherEffectRegistry.JUST_FOG).get()))
                        .build()
        ));
        context.register(BETTER_PLAINS, new CustomRainBuilder(
                holderGetter.getOrThrow(Tags.Biomes.IS_DESERT),
                solarTermValueMap2
        ));
    }
}
