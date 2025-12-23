package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK, EclipticSeasonsApi.MODID);
    // wind_chimes 风铃
    public static final DeferredHolder<Block, Block> bamboo_wind_chimes = BLOCK_DEFERRED_REGISTER.register("bamboo_wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.BAMBOO).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> paper_wind_chimes = BLOCK_DEFERRED_REGISTER.register("paper_wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> wind_chimes = BLOCK_DEFERRED_REGISTER.register("wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY).randomTicks()));

    // paper_wind_mill 纸风车
    public static final DeferredHolder<Block, Block> pinwheel_orange = BLOCK_DEFERRED_REGISTER.register("pinwheel_orange", () -> new PinWheelBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> pinwheel_lime = BLOCK_DEFERRED_REGISTER.register("pinwheel_lime", () -> new PinWheelBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> pinwheel_blue = BLOCK_DEFERRED_REGISTER.register("pinwheel_blue", () -> new PinWheelBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // calendar 日历
    public static final DeferredHolder<Block, Block> calendar = BLOCK_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // 湿度计
    public static final DeferredHolder<Block, Block> hygrometer = BLOCK_DEFERRED_REGISTER.register("hygrometer", () -> new HygrometerBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY).randomTicks()));

    // greenhouse_core 温室核心
    public static final DeferredHolder<Block, Block> spring_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("spring_greenhouse_core", () -> new GreenHouseCoreBlock(Season.SPRING, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> summer_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("summer_greenhouse_core", () -> new GreenHouseCoreBlock(Season.SUMMER, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> autumn_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("autumn_greenhouse_core", () -> new GreenHouseCoreBlock(Season.AUTUMN, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> winter_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("winter_greenhouse_core", () -> new GreenHouseCoreBlock(Season.WINTER, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> greenhouse_core_container = BLOCK_DEFERRED_REGISTER.register("greenhouse_core_container", () -> new GreenHouseFrameBlock(BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // season_quest_sign 季节任务
    public static final DeferredHolder<Block, Block> season_quest_ceiling_hanging_sign = BLOCK_DEFERRED_REGISTER.register("season_quest_ceiling_hanging_sign", () -> new QuestCeilingHangingSignBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> season_quest_wall_hanging_sign = BLOCK_DEFERRED_REGISTER.register("season_quest_wall_hanging_sign", () -> new QuestWallHangingSignBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // humidity_control 湿度调节
    public static final DeferredHolder<Block, Block> block_in_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("block_in_copper_grate_block", () -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.COPPER_GRATE, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_exposed_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("block_in_exposed_copper_grate_block", () -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.EXPOSED_COPPER_GRATE, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_weathered_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("block_in_weathered_copper_grate_block", () -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.WEATHERED_COPPER_GRATE, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_oxidized_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("block_in_oxidized_copper_grate_block", () -> new BlockInCopperGrateBlock((WeatheringCopperGrateBlock) Blocks.OXIDIZED_COPPER_GRATE, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER_GRATE)));

    public static final DeferredHolder<Block, Block> block_in_waxed_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("waxed_block_in_copper_grate_block", () -> new BlockInWaxedCopperGrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_waxed_exposed_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("waxed_block_in_exposed_copper_grate_block", () -> new BlockInWaxedCopperGrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_waxed_weathered_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("waxed_block_in_weathered_copper_grate_block", () -> new BlockInWaxedCopperGrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER_GRATE)));
    public static final DeferredHolder<Block, Block> block_in_waxed_oxidized_copper_grate_block = BLOCK_DEFERRED_REGISTER.register("waxed_block_in_oxidized_copper_grate_block", () -> new BlockInWaxedCopperGrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER_GRATE)));

    public static final DeferredHolder<Block, Block> block_in_wooden_grate_block = BLOCK_DEFERRED_REGISTER.register("block_in_wooden_grate_block", () -> new BlockInWaxedCopperGrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_GRATE)
            .sound(SoundType.BAMBOO_WOOD)
            .mapColor(MapColor.WOOD)));

    public static final DeferredHolder<Block, Block> snow_cauldron = BLOCK_DEFERRED_REGISTER.register("snow_cauldron", () -> new IceOrSnowCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, Block> ice_cauldron = BLOCK_DEFERRED_REGISTER.register("ice_cauldron", () -> new IceOrSnowCauldronBlock(BlockBehaviour.Properties.ofLegacyCopy(Blocks.CAULDRON)));

    public static DeferredHolder<Block, Block> snowyLeaves = BLOCK_DEFERRED_REGISTER.register("snowy_leaves", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyBlock = BLOCK_DEFERRED_REGISTER.register("snowy_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyStairs = BLOCK_DEFERRED_REGISTER.register("snowy_stairs", () -> new StairBlock(Blocks.OAK_PLANKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowySlab = BLOCK_DEFERRED_REGISTER.register("snowy_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyVine = BLOCK_DEFERRED_REGISTER.register("snowy_vine", () -> new VineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.VINE).dynamicShape().noOcclusion()));


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

    public static List<Block> getAllChangedGrateBlocks() {
        if (REVERSE_COPPER_GRATE_MAP == null) initCopperGrateMap();
        return new ArrayList<>(REVERSE_COPPER_GRATE_MAP.keySet());
    }

    public static List<Block> getAllGrateBlocks() {
        if (COPPER_GRATE_MAP == null) initCopperGrateMap();
        return new ArrayList<>(COPPER_GRATE_MAP.keySet());
    }
}
