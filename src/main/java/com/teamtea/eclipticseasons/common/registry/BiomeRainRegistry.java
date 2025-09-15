package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.TemperateRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biomes;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public class BiomeRainRegistry {
    public static final ResourceKey<CustomRainBuilder> PLAIN = createKey("plain");

    private static ResourceKey<CustomRainBuilder> createKey(String name) {
        return ResourceKey.create(ESRegistries.BIOME_RAIN, EclipticSeasons.rl(name));
    }

    public static void bootstrap2(BootstrapContext<CustomRainBuilder> context) {
        var holderGetter = context.lookup(Registries.BIOME);
        var solarTermValueMap = SolarTermValueMap.<List<CustomRainBuilder.Weather>>builder().solarTermMap(new Enum2ObjectMap<>(SolarTerm.class)).build();
        for (int i = 0; i < TemperateRain.collectValues().length; i++) {
            TemperateRain temperateRain = TemperateRain.collectValues()[i];
            SolarTerm solarTerm = temperateRain.getSolarTerm();
            boolean isSpring = solarTerm == SolarTerm.SPRING_EQUINOX;
            solarTermValueMap.solarTermMap().get().put(
                    solarTerm, List.of(new CustomRainBuilder.Weather(
                            isSpring ? Optional.of(ServerLevel.RAIN_DURATION) : Optional.empty(),
                            isSpring ? Optional.of(ServerLevel.RAIN_DELAY) : Optional.empty(),
                            isSpring ? Optional.of(ServerLevel.THUNDER_DURATION) : Optional.empty(),
                            temperateRain.getRainChance(), temperateRain.getThunderChance(),
                            List.of()
                    ))
            );
        }
        context.register(PLAIN, new CustomRainBuilder(
                HolderSet.direct(holderGetter.getOrThrow(Biomes.PLAINS)),
                solarTermValueMap
        ));
    }
}
