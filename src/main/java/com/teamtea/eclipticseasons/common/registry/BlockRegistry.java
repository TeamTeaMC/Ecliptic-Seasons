package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.block.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockRegistry {
    public static final DeferredRegister.Blocks BLOCK_DEFERRED_REGISTER = DeferredRegister.createBlocks(EclipticSeasonsApi.MODID);
    // wind_chimes 风铃
    public static final DeferredHolder<Block, Block> bamboo_wind_chimes = BLOCK_DEFERRED_REGISTER.registerBlock("bamboo_wind_chimes", (p) -> new WindChimesBlock(p.strength(0.5f).sound(SoundType.BAMBOO).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> paper_wind_chimes = BLOCK_DEFERRED_REGISTER.registerBlock("paper_wind_chimes", (p) -> new WindChimesBlock(p.strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> wind_chimes = BLOCK_DEFERRED_REGISTER.registerBlock("wind_chimes", (p) -> new WindChimesBlock(p.strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY).randomTicks()));

    // paper_wind_mill 纸风车
    public static final DeferredHolder<Block, Block> pinwheel_orange = BLOCK_DEFERRED_REGISTER.registerBlock("pinwheel_orange", (p) -> new PinWheelBlock(p.strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> pinwheel_lime = BLOCK_DEFERRED_REGISTER.registerBlock("pinwheel_lime", (p) -> new PinWheelBlock(p.strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> pinwheel_blue = BLOCK_DEFERRED_REGISTER.registerBlock("pinwheel_blue", (p) -> new PinWheelBlock(p.strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // calendar 日历
    public static final DeferredHolder<Block, Block> calendar = BLOCK_DEFERRED_REGISTER.registerBlock("calendar", (p) -> new CalendarBlock(p.strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // 湿度计
    public static final DeferredHolder<Block, Block> hygrometer = BLOCK_DEFERRED_REGISTER.registerBlock("hygrometer", (p) -> new HygrometerBlock(p.strength(0.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY).randomTicks()));

    // greenhouse_core 温室核心
    public static final DeferredHolder<Block, Block> spring_greenhouse_core = BLOCK_DEFERRED_REGISTER.registerBlock("spring_greenhouse_core", (p) -> new GreenHouseCoreBlock(Season.SPRING, p.strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> summer_greenhouse_core = BLOCK_DEFERRED_REGISTER.registerBlock("summer_greenhouse_core", (p) -> new GreenHouseCoreBlock(Season.SUMMER, p.strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> autumn_greenhouse_core = BLOCK_DEFERRED_REGISTER.registerBlock("autumn_greenhouse_core", (p) -> new GreenHouseCoreBlock(Season.AUTUMN, p.strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> winter_greenhouse_core = BLOCK_DEFERRED_REGISTER.registerBlock("winter_greenhouse_core", (p) -> new GreenHouseCoreBlock(Season.WINTER, p.strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> greenhouse_core_container = BLOCK_DEFERRED_REGISTER.registerBlock("greenhouse_core_container", (p) -> new GreenHouseFrameBlock(p.strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // season_quest_sign 季节任务
    public static final DeferredHolder<Block, Block> season_quest_ceiling_hanging_sign = BLOCK_DEFERRED_REGISTER.registerBlock("season_quest_ceiling_hanging_sign", QuestCeilingHangingSignBlock::new, () -> (BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> season_quest_wall_hanging_sign = BLOCK_DEFERRED_REGISTER.registerBlock("season_quest_wall_hanging_sign", QuestWallHangingSignBlock::new, () -> (BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // humidity_control 湿度调节
    public static final DeferredHolder<Block, Block> block_in_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("block_in_copper_grate_block", (p) -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.COPPER_GRATE, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_GRATE));
    public static final DeferredHolder<Block, Block> block_in_exposed_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("block_in_exposed_copper_grate_block", (p) -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.EXPOSED_COPPER_GRATE, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER_GRATE));
    public static final DeferredHolder<Block, Block> block_in_weathered_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("block_in_weathered_copper_grate_block", (p) -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.WEATHERED_COPPER_GRATE, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER_GRATE));
    public static final DeferredHolder<Block, Block> block_in_oxidized_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("block_in_oxidized_copper_grate_block", (p) -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.OXIDIZED_COPPER_GRATE, p), () -> BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER_GRATE));

    public static final DeferredHolder<Block, Block> block_in_waxed_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("waxed_block_in_copper_grate_block", BlockInWaxedCopperGrateBlock::new, () -> BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_GRATE));
    public static final DeferredHolder<Block, Block> block_in_waxed_exposed_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("waxed_block_in_exposed_copper_grate_block", BlockInWaxedCopperGrateBlock::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_waxed_weathered_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("waxed_block_in_weathered_copper_grate_block", BlockInWaxedCopperGrateBlock::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_waxed_oxidized_copper_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("waxed_block_in_oxidized_copper_grate_block", BlockInWaxedCopperGrateBlock::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER_GRATE)));

    public static final DeferredHolder<Block, Block> block_in_wooden_grate_block = BLOCK_DEFERRED_REGISTER.registerBlock("block_in_wooden_grate_block", BlockInWaxedCopperGrateBlock::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_GRATE)
            .sound(SoundType.BAMBOO_WOOD)
            .mapColor(MapColor.WOOD)));

    public static final DeferredHolder<Block, Block> snow_cauldron = BLOCK_DEFERRED_REGISTER.registerBlock("snow_cauldron", IceOrSnowCauldronBlock::new, () -> (BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, Block> ice_cauldron = BLOCK_DEFERRED_REGISTER.registerBlock("ice_cauldron", IceOrSnowCauldronBlock::new, () -> (BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON)));

    public static DeferredHolder<Block, Block> snowyLeaves = BLOCK_DEFERRED_REGISTER.registerBlock("snowy_leaves", Block::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyBlock = BLOCK_DEFERRED_REGISTER.registerBlock("snowy_block", Block::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyStairs = BLOCK_DEFERRED_REGISTER.registerBlock("snowy_stairs", (p) -> new StairBlock(Blocks.OAK_PLANKS.defaultBlockState(), p), () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowySlab = BLOCK_DEFERRED_REGISTER.registerBlock("snowy_slab", SlabBlock::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyVine = BLOCK_DEFERRED_REGISTER.registerBlock("snowy_vine", VineBlock::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.VINE).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> thinIce = BLOCK_DEFERRED_REGISTER.registerBlock("thin_ice", ThinIceBlock::new, () -> (BlockBehaviour.Properties.ofFullCopy(Blocks.ICE).noOcclusion()));

    public static DeferredHolder<Block, Block> humidity_tank = BLOCK_DEFERRED_REGISTER.registerBlock(
            "humidity_tank",
            HumidityTankBlock::new,
            p -> p
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD)
                    .randomTicks()
                    .noOcclusion()
    );

    public static final DeferredHolder<Block, Block> dehumidifier =
            BLOCK_DEFERRED_REGISTER.registerBlock(
                    "dehumidifier",
                    DehumidifierBlock::new,
                    p -> p
                            .mapColor(MapColor.WOOD)
                            .strength(2.0F, 3.0F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()
                            .randomTicks()
            );

    public static final DeferredBlock<SeasonSensorBlock> season_sensor =
            BLOCK_DEFERRED_REGISTER.registerBlock(
                    "season_sensor",
                    SeasonSensorBlock::new,
                    p -> p
                            .strength(0.5F)
                            .sound(SoundType.WOOD)
                            .noOcclusion()
                            .randomTicks()
            );

    private static Map<Block, Supplier<Block>> COPPER_GRATE_MAP;

    private static Map<Block, Supplier<Block>> REVERSE_COPPER_GRATE_MAP;

    public static void initCopperGrateMap() {
        Map<Block, Supplier<Block>> map = new LinkedHashMap<>();
        map.put(Blocks.COPPER_GRATE, block_in_copper_grate_block);
        map.put(Blocks.EXPOSED_COPPER_GRATE, block_in_exposed_copper_grate_block);
        map.put(Blocks.WEATHERED_COPPER_GRATE, block_in_weathered_copper_grate_block);
        map.put(Blocks.OXIDIZED_COPPER_GRATE, block_in_oxidized_copper_grate_block);
        map.put(Blocks.WAXED_COPPER_GRATE, block_in_waxed_copper_grate_block);
        map.put(Blocks.WAXED_EXPOSED_COPPER_GRATE, block_in_waxed_exposed_copper_grate_block);
        map.put(Blocks.WAXED_WEATHERED_COPPER_GRATE, block_in_waxed_weathered_copper_grate_block);
        map.put(Blocks.WAXED_OXIDIZED_COPPER_GRATE, block_in_waxed_oxidized_copper_grate_block);
        map.put(block_in_wooden_grate_block.get(), block_in_wooden_grate_block);

        COPPER_GRATE_MAP = Map.copyOf(map);

        REVERSE_COPPER_GRATE_MAP = map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getValue().get(),
                        e -> e::getKey,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public static Block getCopperGrateBlockChange(Block origin) {
        if (COPPER_GRATE_MAP == null) initCopperGrateMap();
        return COPPER_GRATE_MAP.getOrDefault(origin, () -> Blocks.AIR).get();
    }

    public static Block getOriginalCopperGrateBlock(Block transformed) {
        if (REVERSE_COPPER_GRATE_MAP == null) initCopperGrateMap();
        return REVERSE_COPPER_GRATE_MAP.getOrDefault(transformed, () -> Blocks.AIR).get();
    }

    public static Block getOriginalCopperGrateBlockNotWaxed(Block transformed) {
        Block block = getOriginalCopperGrateBlock(transformed);
        if (block == Blocks.WAXED_COPPER_GRATE)
            block = Blocks.COPPER_GRATE;
        else if (block == Blocks.WAXED_EXPOSED_COPPER_GRATE)
            block = Blocks.EXPOSED_COPPER_GRATE;
        else if (block == Blocks.WAXED_WEATHERED_COPPER_GRATE)
            block = Blocks.WEATHERED_COPPER_GRATE;
        else if (block == Blocks.WAXED_OXIDIZED_COPPER_GRATE)
            block = Blocks.OXIDIZED_COPPER_GRATE;
        return block;
    }

    public static List<Block> getAllChangedGrateBlocks() {
        if (REVERSE_COPPER_GRATE_MAP == null) initCopperGrateMap();
        return new ArrayList<>(REVERSE_COPPER_GRATE_MAP.keySet());
    }

    public static List<Block> getAllGrateBlocks() {
        if (COPPER_GRATE_MAP == null) initCopperGrateMap();
        return new ArrayList<>(COPPER_GRATE_MAP.keySet());
    }
}
