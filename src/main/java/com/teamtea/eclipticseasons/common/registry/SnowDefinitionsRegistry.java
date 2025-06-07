package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;

public class SnowDefinitionsRegistry {
    public static final ResourceKey<SnowDefinition> OVERLAY = createKey("test/overlay");

    public static final ResourceKey<SnowDefinition> SNOWY_GRASS_BLOCK = createKey("snowy_grass_block");

    public static final ResourceKey<SnowDefinition> LEAVE_TEST = createKey("test/leave");


    private static ResourceKey<SnowDefinition> createKey(String name) {
        return ResourceKey.create(ESRegistries.SNOW_DEFINITIONS, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<SnowDefinition> context) {
        var blockHolderGetter = context.lookup(Registries.BLOCK);
        context.register(OVERLAY,SnowDefinition.builder()
                        .blocks(HolderSet.empty())
                        .info(SnowDefinition.Info.builder().mid(ClientModelDefinitions.OVERLAY).build())
                .build());
        context.register(SNOWY_GRASS_BLOCK,SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.GRASS_BLOCK.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().mid(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).build())
                .build());
        context.register(LEAVE_TEST,SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.OAK_LEAVES.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().flag(MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES).mid(ClientModelDefinitions.SNOWY_LEAVES_TOP).mid2(ClientModelDefinitions.SNOWY_LEAVES_ATTACH).build())
                .build());
    }
}
