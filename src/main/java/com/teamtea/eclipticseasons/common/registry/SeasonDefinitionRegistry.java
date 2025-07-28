package com.teamtea.eclipticseasons.common.registry;

import com.mojang.datafixers.util.Either;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.season.SeasonDefinition;
import com.teamtea.eclipticseasons.api.data.weather.CustomSnowTerm;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;
import java.util.Optional;

public class SeasonDefinitionRegistry {
    public static final ResourceKey<SeasonDefinition> test = createKey("test");

    private static ResourceKey<SeasonDefinition> createKey(String name) {
        return ResourceKey.create(ESRegistries.SEASON_DEFINITION, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<SeasonDefinition> context) {
        var holderGetter = context.lookup(Registries.BIOME);
        var cropClimateTypeHolderGetter = context.lookup(ESRegistries.AGRO_CLIMATE);

        HolderSet.Direct<AgroClimaticZone> temperate = HolderSet.direct(cropClimateTypeHolderGetter.getOrThrow(AgroClimateRegistry.TEMPERATE));
        HolderSet.Direct<Biome> plains = HolderSet.direct(holderGetter.getOrThrow(Biomes.PLAINS));
        context.register(test, new SeasonDefinition(
                Optional.empty(),Optional.of(plains),
                SolarTermValueMap.<List<SeasonDefinition.ChangeMode>>builder()
                        .putSeason(Season.SPRING, List.of(new SeasonDefinition.ChangeMode(BlockPredicate.Builder.block().of(Blocks.GRASS_BLOCK).build(),
                                List.of(new SeasonDefinition.PlaceContent(List.of(
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.SHORT_GRASS.defaultBlockState(),
                                        Blocks.DANDELION.defaultBlockState(),
                                        Blocks.OXEYE_DAISY.defaultBlockState()
                                ),
                                        Optional.of(new Vec3i(0, 1, 0)),
                                        false
                                )), 1/16f
                        )))
                        .putSeason(Season.SUMMER, List.of(new SeasonDefinition.ChangeMode(BlockPredicate.Builder.block().of(Blocks.SHORT_GRASS).build(),
                                List.of(new SeasonDefinition.PlaceContent(Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER),
                                                Optional.empty(),
                                                false),
                                        new SeasonDefinition.PlaceContent(Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER),
                                                Optional.of(new Vec3i(0, 1, 0)),
                                                false)
                                ), 1/16f
                        )))
                        .putSeason(Season.AUTUMN, List.of(new SeasonDefinition.ChangeMode(
                                BlockPredicate.Builder.block().of(Blocks.TALL_GRASS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)).build(),
                                List.of(new SeasonDefinition.PlaceContent(Blocks.SHORT_GRASS.defaultBlockState(),
                                        Optional.empty(),
                                        false
                                )), 1/16f
                        ),new SeasonDefinition.ChangeMode(
                                BlockPredicate.Builder.block().of(Blocks.TALL_GRASS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)).build(),
                                List.of(new SeasonDefinition.PlaceContent(Blocks.AIR.defaultBlockState(),
                                        Optional.empty(),
                                        false
                                )), 1/16f
                        )))
                        .putSeason(Season.WINTER, List.of(
                                new SeasonDefinition.ChangeMode(BlockPredicate.Builder.block().of(Blocks.SHORT_GRASS).build(),
                                List.of(new SeasonDefinition.PlaceContent(Blocks.AIR.defaultBlockState(),
                                        Optional.empty(),
                                        true
                                )), 1/16f
                        ),
                                new SeasonDefinition.ChangeMode(BlockPredicate.Builder.block().of(Blocks.DANDELION).build(),
                                        List.of(new SeasonDefinition.PlaceContent(Blocks.AIR.defaultBlockState(),
                                                Optional.empty(),
                                                true
                                        )), 1/16f
                                ),
                                new SeasonDefinition.ChangeMode(BlockPredicate.Builder.block().of(Blocks.OXEYE_DAISY).build(),
                                        List.of(new SeasonDefinition.PlaceContent(Blocks.AIR.defaultBlockState(),
                                                Optional.empty(),
                                                true
                                        )), 1/16f
                                )
                        ))
                        .build()
        ));
    }
}
