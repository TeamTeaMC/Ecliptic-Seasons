package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.MonsoonRain;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.data.climate.SolarTermValueMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;

import java.util.Optional;

public class BiomeClimateSettingsRegistry {
    public static final ResourceKey<BiomesClimateSettings> SAVANNA = createKey("savanna");

    private static ResourceKey<BiomesClimateSettings> createKey(String name) {
        return ResourceKey.create(ESRegistries.BIOME_CLIMATE_SETTING, EclipticSeasons.rl(name));
    }


    public static void bootstrap(BootstapContext<BiomesClimateSettings> context) {
        HolderGetter<Biome> holderGetter = context.lookup(Registries.BIOME);

        Optional<Float> empty = Optional.empty();
        Optional<SolarTermValueMap<Float>> emptyMap = Optional.empty();
        SolarTermValueMap.Builder<Float> builder = SolarTermValueMap.builder();
        for (MonsoonRain monsoonRain : MonsoonRain.collectValues()) {
            if ((monsoonRain.getRainChane() > 0)) {
                builder.putSolarTerm(monsoonRain.getSolarTerm(), Math.round(monsoonRain.getRainChane() / 1.5f * 1000.0f) / 1000.0f);
            }
        }

        context.register(SAVANNA, new BiomesClimateSettings(holderGetter.getOrThrow(BiomeTags.IS_SAVANNA),
                empty, empty, emptyMap, Optional.of(builder.build())));
        // context.register(base, new BiomeClimateModifier(true, holderGetter.getOrThrow(ClimateTypeBiomeTags.SEASONAL),
        //         ImmutableMap.of(Either.left(Season.SUMMER),new BiomeClimate(Optional.empty(),Optional.empty())
        // )));
    }
}
