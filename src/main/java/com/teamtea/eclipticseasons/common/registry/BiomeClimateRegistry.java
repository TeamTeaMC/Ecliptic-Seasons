package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.*;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Optional;

public class BiomeClimateRegistry {
    public static final ResourceKey<BiomeClimateModifier> base = createKey("test");

    private static ResourceKey<BiomeClimateModifier> createKey(String name) {
        return ResourceKey.create(ESRegistries.BIOME_CLIMATE, EclipticSeasons.rl(name));
    }


    public static void bootstrap(BootstrapContext<BiomeClimateModifier> context) {
        HolderGetter<Biome> holderGetter = context.lookup(Registries.BIOME);
        context.register(base, new BiomeClimateModifier(true, holderGetter.getOrThrow(ClimateTypeBiomeTags.SEASONAL),List.of(new BiomeClimate(24,Optional.empty(),Optional.empty()))));
    }
}
