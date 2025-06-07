package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.block.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, EclipticSeasons.MODID);

    public static final RegistryObject<Block> calendar = BLOCK_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> hygrometer = BLOCK_DEFERRED_REGISTER.register("hygrometer", () -> new HygrometerBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY).randomTicks()));

    // greenhouse_core 温室核心
    public static final RegistryObject<Block> spring_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("spring_greenhouse_core", () -> new GreenHouseCoreBlock(Season.SPRING, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> summer_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("summer_greenhouse_core", () -> new GreenHouseCoreBlock(Season.SUMMER, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> autumn_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("autumn_greenhouse_core", () -> new GreenHouseCoreBlock(Season.AUTUMN, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> winter_greenhouse_core = BLOCK_DEFERRED_REGISTER.register("winter_greenhouse_core", () -> new GreenHouseCoreBlock(Season.WINTER, BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> greenhouse_core_container = BLOCK_DEFERRED_REGISTER.register("greenhouse_core_container", () -> new GreenHouseFrameBlock(BlockBehaviour.Properties.of().strength(1.5f).sound(SoundType.AMETHYST).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // season_quest_sign 季节任务
    public static final RegistryObject<Block> season_quest_ceiling_hanging_sign = BLOCK_DEFERRED_REGISTER.register("season_quest_ceiling_hanging_sign", () -> new QuestCeilingHangingSignBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> season_quest_wall_hanging_sign = BLOCK_DEFERRED_REGISTER.register("season_quest_wall_hanging_sign", () -> new QuestWallHangingSignBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    // humidity_control 湿度调节
    public static final RegistryObject<Block> block_in_wooden_grate_block = BLOCK_DEFERRED_REGISTER.register("block_in_wooden_grate_block", () -> new BlockInGrateBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion() .sound(SoundType.BAMBOO_WOOD)
            .mapColor(MapColor.WOOD)));

    public static RegistryObject<Block> snowyLeaves = BLOCK_DEFERRED_REGISTER.register("snowy_leaves", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyBlock = BLOCK_DEFERRED_REGISTER.register("snowy_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyStairs = BLOCK_DEFERRED_REGISTER.register("snowy_stairs", () -> new StairBlock(Blocks.OAK_PLANKS::defaultBlockState, BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowySlab = BLOCK_DEFERRED_REGISTER.register("snowy_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));


}
