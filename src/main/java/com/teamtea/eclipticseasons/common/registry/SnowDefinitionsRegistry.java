package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class SnowDefinitionsRegistry {
    public static final ResourceKey<SnowDefinition> OVERLAY = createKey("test/overlay");

    public static final ResourceKey<SnowDefinition> SNOWY_GRASS_BLOCK = createKey("snowy_grass_block");

    public static final ResourceKey<SnowDefinition> LEAVE_TEST = createKey("test/leave");

    public static final ResourceKey<SnowDefinition> SNOWY_SWEET_BERRY_BUSH = createKey("snowy_sweet_berry_bush");
    public static final ResourceKey<SnowDefinition> SNOWY_DEAD_BUSH = createKey("snowy_dead_bush");
    public static final ResourceKey<SnowDefinition> SNOWY_SUGAR_CANE = createKey("snowy_sugar_cane");


    private static ResourceKey<SnowDefinition> createKey(String name) {
        return ResourceKey.create(ESRegistries.SNOW_DEFINITIONS, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstapContext<SnowDefinition> context) {
        var blockHolderGetter = context.lookup(Registries.BLOCK);
        context.register(OVERLAY, SnowDefinition.builder()
                .blocks(HolderSet.direct())
                .info(SnowDefinition.Info.builder().mid(ClientModelDefinitions.OVERLAY).build())
                .build());
        context.register(SNOWY_GRASS_BLOCK, SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.GRASS_BLOCK.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().mid(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).build())
                .build());
        context.register(LEAVE_TEST, SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.OAK_LEAVES.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().flag(MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES).mid(ClientModelDefinitions.SNOWY_LEAVES_TOP).mid2(ClientModelDefinitions.SNOWY_LEAVES_ATTACH).build())
                .build());

        context.register(SNOWY_SWEET_BERRY_BUSH, SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.SWEET_BERRY_BUSH.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().flag(MapChecker.FLAG_CUSTOM_JSON_PLANTS).offset(1).mid(ClientModelDefinitions.SNOWY_SWEET_BERRY_BUSH).build())
                .build());
        context.register(SNOWY_DEAD_BUSH, SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.DEAD_BUSH.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().flag(MapChecker.FLAG_CUSTOM_JSON_PLANTS).offset(1).mid(ClientModelDefinitions.SNOWY_DEAD_BUSH).build())
                .build());
        context.register(SNOWY_SUGAR_CANE, SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.SUGAR_CANE.builtInRegistryHolder()))
                .info(SnowDefinition.Info.builder().flag(MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE).mid(ClientModelDefinitions.SNOWY_SUGAR_CANE).build())
                .build());
    }

    private static HolderSet.@NotNull Direct<Block> set(Block bamboo) {
        return HolderSet.direct(bamboo.builtInRegistryHolder());
    }

    public static void register(BootstapContext<SnowDefinition> context, Block block, BiFunction<Block, SnowDefinition.SnowDefinitionBuilder, SnowDefinition.SnowDefinitionBuilder> function) {
        context.register(createSnowyKey(block), function.apply(block,
                SnowDefinition.builder()
        ).blocks(set(block)).build());
    }

    public static void addPlant(BootstapContext<SnowDefinition> context, Block block) {
        addPlant(context, block, MapChecker.FLAG_CUSTOM_JSON_PLANTS);
    }

    public static void addPlant(BootstapContext<SnowDefinition> context, Block block, int flag) {
        String path = path(block);
        context.register(createSnowyKey(path), SnowDefinition.builder()
                .blocks(set(block))
                .info(SnowDefinition.Info.builder().offset(1).flag(flag).mid(getSnowModelPath(path)).build())
                .build());
    }

    private static @NotNull ResourceKey<SnowDefinition> createSnowyKey(String path) {
        return createKey(
                "snowy_" + path
        );
    }

    private static @NotNull ResourceKey<SnowDefinition> createSnowyKey(Block block) {
        return createKey(
                "snowy_" + path(block)
        );
    }

    public static @NotNull String path(Block block) {
        return block.builtInRegistryHolder().key().location().getPath();
    }

    public static @NotNull ResourceLocation getSnowModelPath(String path) {
        return EclipticSeasons.rl("snowy/" + path);
    }

    public static @NotNull ResourceLocation getSnowModelPath(Block block) {
        return EclipticSeasons.rl("snowy/" + path(block));
    }

    public static @NotNull ResourceLocation getSnowModelPath(String modid, Block block) {
        return new ResourceLocation(modid, "snowy/" + path(block));
    }
}
