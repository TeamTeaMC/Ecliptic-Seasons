package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;

public class SnowTermRegistry {
    public static final ResourceKey<CustomSnowTerm> PLAIN = createKey("plain");
    public static final ResourceKey<CustomSnowTerm> SUNFLOWER_PLAINS = createKey("sunflower_plains");

    private static ResourceKey<CustomSnowTerm> createKey(String name) {
        return ResourceKey.create(ESRegistries.SNOW_TERM, EclipticSeasons.rl(name));
    }

    public static void bootstrap2(BootstrapContext<CustomSnowTerm> context) {
        var holderGetter = context.lookup(Registries.BIOME);
        context.register(PLAIN, new CustomSnowTerm(
                HolderSet.direct(holderGetter.getOrThrow(Biomes.PLAINS)),
                SnowTerm.T07.getStart(), SnowTerm.T07.getEnd(),
                List.of()
        ));

        context.register(SUNFLOWER_PLAINS, new CustomSnowTerm(
                HolderSet.direct(holderGetter.getOrThrow(Biomes.SUNFLOWER_PLAINS)),
                SnowTerm.T07.getStart(), SnowTerm.T07.getEnd(),
                List.of(new CustomSnowTerm.TempEvent(0.1f, SnowTerm.T1.getStart(), SnowTerm.T1.getEnd()))
        ));
    }
}
