package com.teamtea.eclipticseasons.common.registry;

import com.google.common.collect.ImmutableMap;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.BiomeClimateModifier;
import com.teamtea.eclipticseasons.api.data.BlockStatePropertyCondition;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControlBuilder;
import com.teamtea.eclipticseasons.api.data.crop.CropSeasonControl;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;

public class CropRegistry {
    public static final ResourceKey<CropGrowControlBuilder> base = createKey("wheat");

    private static ResourceKey<CropGrowControlBuilder> createKey(String name) {
        return ResourceKey.create(ESRegistries.CROP, EclipticSeasons.rl(name));
    }
    public static void bootstrap(BootstrapContext<CropGrowControlBuilder> context) {

        var blockHolderGetter = context.lookup(Registries.BLOCK);

        context.register(base,new CropGrowControlBuilder(
                new BlockPredicate(Optional.of(HolderSet.direct(Blocks.WHEAT.builtInRegistryHolder())),StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE,5).build(),Optional.empty()),
                Optional.empty(),
                new IdentityHashMap<>(ImmutableMap.of(
                        SolarTerm.WINTER_SOLSTICE, new CropSeasonControl( SolarTerm.BEGINNING_OF_SPRING,0.2f,0f,0.8f,Optional.of(Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE,4)))
                )),
                List.of()

        ));
    }
}
