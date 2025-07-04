package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.data.weather.CustomRainBuilder;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;

public class SnowTermRegistry {
    public static final ResourceKey<CustomSnowTerm> PLAIN = createKey("plain");

    private static ResourceKey<CustomSnowTerm> createKey(String name) {
        return ResourceKey.create(ESRegistries.SNOW_TERM, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstapContext<CustomSnowTerm> context) {
        var holderGetter = context.lookup(Registries.BIOME);
        context.register(PLAIN, new CustomSnowTerm(
                HolderSet.direct(holderGetter.getOrThrow(Biomes.PLAINS)),
                SnowTerm.T06.getStart(), SnowTerm.T06.getEnd()
        ));
    }
}
