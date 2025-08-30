package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.season.definition.*;
import com.teamtea.eclipticseasons.api.data.season.definition.condition.EmptyAboveCondition;
import com.teamtea.eclipticseasons.api.data.season.definition.selector.BlockSelector;
import com.teamtea.eclipticseasons.api.data.season.definition.selector.MultiBlockSelector;
import com.teamtea.eclipticseasons.api.util.backport.FakeBlockPredicate;
import com.teamtea.eclipticseasons.api.util.backport.FakeStatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
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

    
    public static void bootstrap2(BootstapContext<SeasonDefinition> context) {
        var holderGetter = context.lookup(Registries.BIOME);

        HolderSet.Direct<Biome> plains = HolderSet.direct(holderGetter.getOrThrow(Biomes.THE_VOID));
        Vec3i above = new Vec3i(0, 1, 0);
        List<EmptyAboveCondition> condition = List.of(EmptyAboveCondition.builder().above(true).build());
        context.register(test, new SeasonDefinition(
                Optional.of(plains),
                SolarTermValueMap.<List<ChangeMode>>builder()
                        .putSeason(Season.SPRING, List.of(
                                ChangeMode.builder()
                                        .original(FakeBlockPredicate.Builder.block().of(Blocks.GRASS_BLOCK).build())
                                        .fixedSeed(true)
                                        .chance(1 / 16f)
                                        .selector(BlockSelector.builder().conditions(condition).state(Optional.of(Blocks.GRASS.defaultBlockState())).weight(22).offset(Optional.of(above)).build())
                                        .selector(BlockSelector.builder().conditions(condition).state(Optional.of(Blocks.DANDELION.defaultBlockState())).weight(1).offset(Optional.of(above)).build())
                                        .selector(BlockSelector.builder().conditions(condition).state(Optional.of(Blocks.OXEYE_DAISY.defaultBlockState())).weight(1).offset(Optional.of(above)).build())
                                        .build()
                        ))
                        .putSeason(Season.SUMMER, List.of(
                                ChangeMode.builder()
                                        .original(FakeBlockPredicate.Builder.block().of(Blocks.GRASS).build())
                                        .fixedSeed(true)
                                        .chance(1 / 16f)
                                        .selector(MultiBlockSelector.builder()
                                                .conditions(condition)
                                                .multiBlock(MultiBlockSelector.Part.builder()
                                                        .state( Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
                                                        .build())
                                                .multiBlock(MultiBlockSelector.Part.builder()
                                                        .state( Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                                                        .build())
                                                .build())
                                        .build()
                        ))
                        .putSeason(Season.AUTUMN, List.of(
                                ChangeMode.builder()
                                        .original(FakeBlockPredicate.Builder.block().of(Blocks.TALL_GRASS).setProperties(FakeStatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .selector(BlockSelector.builder().state(Optional.of(Blocks.GRASS.defaultBlockState())).build())
                                        .build(),
                                ChangeMode.builder()
                                        .original(FakeBlockPredicate.Builder.block().of(Blocks.TALL_GRASS).setProperties(FakeStatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .selector(BlockSelector.builder().build())
                                        .build())
                        )
                        .putSeason(Season.WINTER, List.of(
                                ChangeMode.builder()
                                        .original(FakeBlockPredicate.Builder.block().of(Blocks.GRASS).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .build(),
                                ChangeMode.builder()
                                        .original(FakeBlockPredicate.Builder.block().of(Blocks.DANDELION).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .build(),
                                ChangeMode.builder()
                                        .original(FakeBlockPredicate.Builder.block().of(Blocks.OXEYE_DAISY).build())
                                        .fixedSeed(false)
                                        .chance(1 / 16f)
                                        .build()
                        ))
                        .build()
        ));
    }

}
