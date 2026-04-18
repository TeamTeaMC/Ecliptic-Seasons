package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.season.definition.ChangeMode;
import com.teamtea.eclipticseasons.api.data.season.definition.SeasonDefinition;
import com.teamtea.eclipticseasons.api.data.season.definition.condition.EmptyAboveCondition;
import com.teamtea.eclipticseasons.api.data.season.definition.selector.BlockSelector;
import com.teamtea.eclipticseasons.api.data.season.definition.selector.FeatureSelector;
import com.teamtea.eclipticseasons.api.data.season.definition.selector.MultiBlockSelector;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBedBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.Optional;

public class SeasonDefinitionRegistry {
    public static final ResourceKey<SeasonDefinition> test = createKey("test");

    private static ResourceKey<SeasonDefinition> createKey(String name) {
        return ResourceKey.create(ESRegistries.SEASON_DEFINITION, EclipticSeasons.rl(name));
    }

    public static void bootstrap2(BootstrapContext<SeasonDefinition> context) {
        var holderGetter = context.lookup(Registries.BIOME);
        var blockHolderGetter = context.lookup(Registries.BLOCK);
        var placedFeatureHolderGetter = context.lookup(Registries.CONFIGURED_FEATURE);

        var plains =holderGetter.get(Tags.Biomes.IS_TEMPERATE_OVERWORLD).get();
        Vec3i above = new Vec3i(0, 1, 0);
        EmptyAboveCondition empty = EmptyAboveCondition.builder().above(true).build();
        List<EmptyAboveCondition> condition = List.of(empty);
        context.register(test, new SeasonDefinition(
                Optional.of(plains),
                SolarTermValueMap.<List<ChangeMode>>builder()
                        .putSeason(Season.SPRING, List.of(
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.GRASS_BLOCK).build())
                                        .fixedSeed(true)
                                        .chance(0.755f)
                                        .selector(BlockSelector.builder().conditions(condition)
                                                .state(Optional.of(Blocks.SHORT_GRASS.defaultBlockState())).weight(22).offset(Optional.of(above)).build())
                                        // .selector(FeatureSelector.builder().conditions(condition).feature(placedFeatureHolderGetter.getOrThrow(VegetationFeatures.PUMPKIN)).weight(1).offset(Optional.of(above)).build())
                                        // .selector(FeatureSelector.builder().conditions(condition).feature(placedFeatureHolderGetter.getOrThrow(VegetationFeatures.BAMBOO_VEGETATION)).weight(1).offset(Optional.of(above)).build())
                                        .selector(BlockSelector.builder().conditions(condition).state(Optional.of(Blocks.WILDFLOWERS.defaultBlockState())).weight(10).offset(Optional.of(above)).build())
                                        .build(),
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.WILDFLOWERS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FlowerBedBlock.AMOUNT, 1)).build())
                                        .fixedSeed(true)
                                        .chance(1 / 3f)
                                        .selector(BlockSelector.builder().conditions(condition).state(Optional.of(Blocks.WILDFLOWERS.defaultBlockState().setValue(FlowerBedBlock.AMOUNT, 2))).weight(5).replace(Optional.of(true)).build())
                                        .build(),
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.WILDFLOWERS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FlowerBedBlock.AMOUNT, 2)).build())
                                        .fixedSeed(true)
                                        .chance(1 / 2f)
                                        .selector(BlockSelector.builder().conditions(condition).state(Optional.of(Blocks.WILDFLOWERS.defaultBlockState().setValue(FlowerBedBlock.AMOUNT, 3))).weight(5).replace(Optional.of(true)).build())
                                        .build(),
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.WILDFLOWERS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FlowerBedBlock.AMOUNT, 3)).build())
                                        .fixedSeed(true)
                                        .chance(1 / 2f)
                                        .selector(BlockSelector.builder().conditions(condition).state(Optional.of(Blocks.WILDFLOWERS.defaultBlockState().setValue(FlowerBedBlock.AMOUNT, 4))).weight(5).replace(Optional.of(true)).build())
                                        .build()
                        ))
                        .putSeason(Season.SUMMER, List.of(
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.SHORT_GRASS).build())
                                        .fixedSeed(true)
                                        .chance(1 / 6f)
                                        .selector(MultiBlockSelector.builder()
                                                .conditions(condition)
                                                .multiBlock(MultiBlockSelector.Part.builder()
                                                        .state(Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                                                        .build())
                                                .multiBlock(MultiBlockSelector.Part.builder()
                                                        .offset(Optional.of(new Vec3i(0,1,0)))
                                                        .state(Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
                                                        .build())
                                                .build())
                                        .build()
                        ))
                        .putSeason(Season.AUTUMN, List.of(
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.TALL_GRASS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .selector(BlockSelector.builder().state(Optional.of(Blocks.SHORT_GRASS.defaultBlockState())).build())
                                        .build(),
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.TALL_GRASS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .selector(BlockSelector.builder().build())
                                        .build())
                        )
                        .putSeason(Season.WINTER, List.of(
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.SHORT_GRASS).build())
                                        .fixedSeed(false)
                                        .selector(BlockSelector.builder()
                                                .state(Optional.of(Blocks.AIR.defaultBlockState()))
                                                .replace(Optional.of(true))
                                                .build())
                                        .chance(1 / 16f)
                                        .build(),
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.WILDFLOWERS).build())
                                        .fixedSeed(false)
                                        .selector(BlockSelector.builder()
                                                .state(Optional.of(Blocks.AIR.defaultBlockState()))
                                                .replace(Optional.of(true))
                                                .build())
                                        .chance(1 / 16f)
                                        .build(),
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.TALL_GRASS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .selector(BlockSelector.builder().state(Optional.of(Blocks.SHORT_GRASS.defaultBlockState())).build())
                                        .build(),
                                ChangeMode.builder()
                                        .original(BlockPredicate.Builder.block().of(blockHolderGetter, Blocks.TALL_GRASS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .selector(BlockSelector.builder().build())
                                        .build()
                        ))
                        .build()
        ));
    }
}
