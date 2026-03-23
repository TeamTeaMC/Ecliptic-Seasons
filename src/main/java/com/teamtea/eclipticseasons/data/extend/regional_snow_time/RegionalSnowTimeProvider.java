package com.teamtea.eclipticseasons.data.extend.regional_snow_time;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RegionalSnowTimeProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder REGISTRY_SET_BUILDER = new RegistrySetBuilder()
            .add(ESRegistries.SNOW_TERM, (context -> {
                HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);
                context.register(ResourceKey.create(ESRegistries.SNOW_TERM, ClimateTypeBiomeTags.COLD_REGION.location()),
                        CustomSnowTerm.builder().biomes(lookup.getOrThrow(ClimateTypeBiomeTags.COLD_REGION))
                                .start(SnowTerm.T000.getStart())
                                .end(SnowTerm.T000.getEnd())
                                .tempEvents(List.of(
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(-0.18f)
                                                .start(SnowTerm.TN.getStart())
                                                .end(SnowTerm.TN.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0f)
                                                .start(SnowTerm.T000.getStart())
                                                .end(SnowTerm.T000.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0.05f)
                                                .start(SnowTerm.T005.getStart())
                                                .end(SnowTerm.T005.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0.1f)
                                                .start(SnowTerm.T01.getStart())
                                                .end(SnowTerm.T01.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0.3f)
                                                .start(SnowTerm.T015.getStart())
                                                .end(SnowTerm.T015.getEnd())
                                                .build()
                                ))
                                .build());

                context.register(ResourceKey.create(ESRegistries.SNOW_TERM, ClimateTypeBiomeTags.WARM_REGION.location()),
                        CustomSnowTerm.builder().biomes(lookup.getOrThrow(ClimateTypeBiomeTags.WARM_REGION))
                                .start(SnowTerm.T07.getStart())
                                .end(SnowTerm.T07.getEnd())
                                .tempEvents(List.of(
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(-0.1f)
                                                .start(SnowTerm.T05.getStart())
                                                .end(SnowTerm.T05.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0f)
                                                .start(SnowTerm.T06.getStart())
                                                .end(SnowTerm.T06.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0.05f)
                                                .start(SnowTerm.T08.getStart())
                                                .end(SnowTerm.T08.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0.1f)
                                                .start(SnowTerm.T095.getStart())
                                                .end(SnowTerm.T095.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0.3f)
                                                .start(SnowTerm.T1.getStart())
                                                .end(SnowTerm.T1.getEnd())
                                                .build()
                                ))
                                .build());

                context.register(ResourceKey.create(ESRegistries.SNOW_TERM, ClimateTypeBiomeTags.HOT_REGION.location()),
                        CustomSnowTerm.builder().biomes(lookup.getOrThrow(ClimateTypeBiomeTags.HOT_REGION))
                                .start(SnowTerm.T1.getStart())
                                .end(SnowTerm.T1.getEnd())
                                .tempEvents(List.of(
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(-0.18f)
                                                .start(SnowTerm.T095.getStart())
                                                .end(SnowTerm.T095.getEnd())
                                                .build(),
                                        CustomSnowTerm.TempEvent.builder()
                                                .tempOffset(0.3f)
                                                .start(SnowTerm.T1.getStart())
                                                .end(SnowTerm.T1.getEnd())
                                                .build()
                                ))
                                .build());
            }));

    public RegionalSnowTimeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REGISTRY_SET_BUILDER, Set.of(EclipticSeasonsApi.MODID));
    }

    @Override
    public @NotNull String getName() {
        return super.getName() + " Extra2";
    }
}