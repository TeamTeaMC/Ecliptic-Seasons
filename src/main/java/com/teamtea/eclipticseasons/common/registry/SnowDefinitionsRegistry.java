package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.ClientModelDefinitions;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.NonNull;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SnowDefinitionsRegistry {
    private static final ResourceKey<SnowDefinition> OVERLAY = createKey("test/overlay");
    public static final ResourceKey<SnowDefinition> SNOWY_GRASS_BLOCK = createKey("snowy_grass_block");

    public static final ResourceKey<SnowDefinition> SNOWY_SWEET_BERRY_BUSH = createKey("snowy_sweet_berry_bush");
    public static final ResourceKey<SnowDefinition> SNOWY_DEAD_BUSH = createKey("snowy_dead_bush");
    public static final ResourceKey<SnowDefinition> SNOWY_SUGAR_CANE = createKey("snowy_sugar_cane");
    public static final ResourceKey<SnowDefinition> SNOWY_BAMBOO = createKey("snowy_bamboo");
    public static final ResourceKey<SnowDefinition> SNOWY_BAMBOO_SAPLING = createKey("snowy_bamboo_sapling");

    private static ResourceKey<SnowDefinition> createKey(String name) {
        return ResourceKey.create(ESRegistries.SNOW_DEFINITIONS, EclipticSeasons.rl(name));
    }

    private static ResourceKey<SnowDefinition> createKey(Block block) {
        return ResourceKey.create(ESRegistries.SNOW_DEFINITIONS, EclipticSeasons.rl("snowy_" + block.builtInRegistryHolder().key().identifier().getPath()));
    }

    public static void bootstrap2(BootstrapContext<SnowDefinition> context) {
        context.register(OVERLAY, SnowDefinition.builder()
                .blocks(HolderSet.empty())
                .info(SnowDefinition.Info.builder().mid(ClientModelDefinitions.OVERLAY).build())
                .build());
    }

    public static void bootstrap(BootstrapContext<SnowDefinition> context) {
        context.register(SNOWY_GRASS_BLOCK, SnowDefinition.builder()
                .blocks(set(Blocks.GRASS_BLOCK))
                .info(SnowDefinition.Info.builder().mid(ClientModelDefinitions.SNOWY_GRASS_BLOCK_OVERLAY).build())
                .build());

        addPlant(context, Blocks.SUGAR_CANE, MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE);
        addPlant(context, Blocks.DEAD_BUSH);
        addPlant(context, Blocks.SWEET_BERRY_BUSH);

        addPlant(context, Blocks.BAMBOO_SAPLING);
        register(context, Blocks.BAMBOO,
                (b, s) -> s.info(SnowDefinition.Info.builder().flag(MapChecker.FLAG_CUSTOM_JSON_WITH_TOP)
                        .snowPassable(true)
                        .mid2(getSnowModelPath(b))
                        .mid(getSnowModelPath(b).withSuffix("_top")).build()));
    }

    private static HolderSet.@NonNull Direct<Block> set(Block bamboo) {
        return HolderSet.direct(bamboo.builtInRegistryHolder());
    }

    public static void register(BootstrapContext<SnowDefinition> context, Block block, BiFunction<Block, SnowDefinition.SnowDefinitionBuilder, SnowDefinition.SnowDefinitionBuilder> function) {
        context.register(createSnowyKey(block), function.apply(block,
                SnowDefinition.builder()
        ).blocks(set(block)).build());
    }

    public static void addPlant(BootstrapContext<SnowDefinition> context, Block block) {
        addPlant(context, block, MapChecker.FLAG_CUSTOM_JSON_PLANTS);
    }

    public static void addPlant(BootstrapContext<SnowDefinition> context, Block block, int flag) {
        String path = path(block);
        context.register(createSnowyKey(path), SnowDefinition.builder()
                .blocks(set(block))
                .info(SnowDefinition.Info.builder().offset(1).flag(flag).mid(getSnowModelPath(path)).build())
                .build());
    }

    private static @NonNull ResourceKey<SnowDefinition> createSnowyKey(String path) {
        return createKey(
                "snowy_" + path
        );
    }

    private static @NonNull ResourceKey<SnowDefinition> createSnowyKey(Block block) {
        return createKey(
                "snowy_" + path(block)
        );
    }

    public static @NonNull String path(Block block) {
        return block.builtInRegistryHolder().key().identifier().getPath();
    }

    public static @NonNull Identifier getSnowModelPath(String path) {
        return EclipticSeasons.rl("snowy/" + path);
    }

    public static @NonNull Identifier getSnowModelPath(Block block) {
        return EclipticSeasons.rl("snowy/" + path(block));
    }

    public static @NonNull Identifier getSnowModelPath(String modid, Block block) {
        return Identifier.fromNamespaceAndPath(modid, "snowy/" + path(block));
    }

    public static void bootstrap_extra(BootstrapContext<SnowDefinition> context) {
        addPlant(context, Blocks.OAK_SAPLING);
        addPlant(context, Blocks.DARK_OAK_SAPLING);
        addPlant(context, Blocks.ACACIA_SAPLING);
        addPlant(context, Blocks.BIRCH_SAPLING);
        addPlant(context, Blocks.JUNGLE_SAPLING);
        addPlant(context, Blocks.SPRUCE_SAPLING);
        addPlant(context, Blocks.CHERRY_SAPLING);


        context.register(createSnowyKey("mangrove_propagule"), SnowDefinition.builder()
                .blocks(HolderSet.direct(Blocks.MANGROVE_PROPAGULE.builtInRegistryHolder()))
                // .map(List.of(SnowDefinition.PropertyTester.builder().name(MangrovePropaguleBlock.HANGING.getName()).matcher(SnowDefinition.ExactMatcher.builder().value(MangrovePropaguleBlock.HANGING.getName(false)).build()).build()))
                .info(SnowDefinition.Info.builder().offset(1).flag(MapChecker.FLAG_CUSTOM_JSON_PLANTS)
                        .mid(getSnowModelPath("mangrove_propagule")).build())
                .build());


        addPlant(context, Blocks.RED_MUSHROOM);
        addPlant(context, Blocks.BROWN_MUSHROOM);
        addPlant(context, Blocks.ALLIUM);
        addPlant(context, Blocks.AZURE_BLUET);
        addPlant(context, Blocks.BLUE_ORCHID);
        addPlant(context, Blocks.CORNFLOWER);
        addPlant(context, Blocks.DANDELION);
        addPlant(context, Blocks.LILY_OF_THE_VALLEY);
        addPlant(context, Blocks.ORANGE_TULIP);
        addPlant(context, Blocks.PINK_TULIP);
        addPlant(context, Blocks.WHITE_TULIP);
        addPlant(context, Blocks.RED_TULIP);
        addPlant(context, Blocks.OXEYE_DAISY);
        addPlant(context, Blocks.POPPY);
        addPlant(context, Blocks.WITHER_ROSE);

        addPlant(context, Blocks.SUNFLOWER, MapChecker.FLAG_CUSTOM_JSON_PLANTS);
        addPlant(context, Blocks.LILAC, MapChecker.FLAG_CUSTOM_JSON_PLANTS);
        addPlant(context, Blocks.PEONY, MapChecker.FLAG_CUSTOM_JSON_PLANTS);
        addPlant(context, Blocks.ROSE_BUSH, MapChecker.FLAG_CUSTOM_JSON_PLANTS);

        addPlant(context, Blocks.TORCHFLOWER);
        addPlant(context, Blocks.TORCHFLOWER_CROP);

        addPlant(context, Blocks.PITCHER_CROP, MapChecker.FLAG_CUSTOM_JSON_PLANTS);
        addPlant(context, Blocks.PITCHER_PLANT, MapChecker.FLAG_CUSTOM_JSON_PLANTS);

        addPlant(context, Blocks.BEETROOTS);
        addPlant(context, Blocks.CARROTS);
        addPlant(context, Blocks.POTATOES);
        addPlant(context, Blocks.WHEAT);


        addPlant(context, Blocks.MELON_STEM);
        addPlant(context, Blocks.ATTACHED_MELON_STEM);
        addPlant(context, Blocks.PUMPKIN_STEM);
        addPlant(context, Blocks.ATTACHED_PUMPKIN_STEM);
    }
}
