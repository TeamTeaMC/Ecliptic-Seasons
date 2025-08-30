package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.season.definition.*;
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
        ChangeCondition condition = ChangeCondition.of(true);
        context.register(test, new SeasonDefinition(
                Optional.of(plains),
                SolarTermValueMap.<List<ChangeMode>>builder()
                        .putSeason(Season.SPRING, List.of(new ChangeMode(FakeBlockPredicate.Builder.block().of(Blocks.GRASS_BLOCK).build(),
                                List.of(
                                        ChangeSelector.of(Blocks.GRASS.defaultBlockState(), 22, above, condition, false),
                                        ChangeSelector.of(Blocks.DANDELION.defaultBlockState(), 1, above, condition, false),
                                        ChangeSelector.of(Blocks.OXEYE_DAISY.defaultBlockState(), 1, above, condition, false)
                                ), 1 / 16f, true
                        )))
                        .putSeason(Season.SUMMER, List.of(new ChangeMode(FakeBlockPredicate.Builder.block().of(Blocks.GRASS).build(),
                                List.of(ChangeSelector.of(
                                        MultiBlockPart.ofList(
                                                Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER),
                                                Blocks.TALL_GRASS.defaultBlockState().setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
                                        , above))
                                , 1 / 16f, true
                        )))
                        .putSeason(Season.AUTUMN, List.of(new ChangeMode(
                                        FakeBlockPredicate.Builder.block().of(Blocks.TALL_GRASS).setProperties(FakeStatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER)).build(),
                                        List.of(ChangeSelector.of(Blocks.GRASS.defaultBlockState())),
                                        1 / 16f, false),
                                new ChangeMode(
                                        FakeBlockPredicate.Builder.block().of(Blocks.TALL_GRASS).setProperties(FakeStatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER)).build(),
                                        List.of(ChangeSelector.of()), 1 / 16f, false
                                )))
                        .putSeason(Season.WINTER, List.of(
                                new ChangeMode(FakeBlockPredicate.Builder.block().of(Blocks.GRASS).build(),
                                        List.of(ChangeSelector.of()), 1 / 16f, false
                                ),
                                new ChangeMode(FakeBlockPredicate.Builder.block().of(Blocks.DANDELION).build(),
                                        List.of(ChangeSelector.of()), 1 / 16f, false
                                ),
                                new ChangeMode(FakeBlockPredicate.Builder.block().of(Blocks.OXEYE_DAISY).build(),
                                        List.of(ChangeSelector.of()), 1 / 16f, false
                                )
                        ))
                        .build()
        ));
    }
}
