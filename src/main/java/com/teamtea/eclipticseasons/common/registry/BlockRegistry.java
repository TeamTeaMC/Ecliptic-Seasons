package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.CalendarBlock;
import com.teamtea.eclipticseasons.common.block.PinWheelBlock;
import com.teamtea.eclipticseasons.common.block.WindChimesBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER = DeferredRegister.create(Registries.BLOCK, EclipticSeasonsApi.MODID);
    public static final DeferredHolder<Block, Block> bamboo_wind_chimes = BLOCK_DEFERRED_REGISTER.register("bamboo_wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.BAMBOO).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> paper_wind_chimes = BLOCK_DEFERRED_REGISTER.register("paper_wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    // wind_chimes 风铃
    public static final DeferredHolder<Block, Block> wind_chimes = BLOCK_DEFERRED_REGISTER.register("wind_chimes", () -> new WindChimesBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> pinwheel_orange = BLOCK_DEFERRED_REGISTER.register("pinwheel_orange", () -> new PinWheelBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static final DeferredHolder<Block, Block> pinwheel_lime = BLOCK_DEFERRED_REGISTER.register("pinwheel_lime", () -> new PinWheelBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    // paper_wind_mill 纸风车
    public static final DeferredHolder<Block, Block> pinwheel_blue = BLOCK_DEFERRED_REGISTER.register("pinwheel_blue", () -> new PinWheelBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    // calendar 日历
    public static final DeferredHolder<Block, Block> calendar = BLOCK_DEFERRED_REGISTER.register("calendar", () -> new CalendarBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));
    public static DeferredHolder<Block, Block> snowyLeaves = BLOCK_DEFERRED_REGISTER.register("snowy_leaves", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyBlock = BLOCK_DEFERRED_REGISTER.register("snowy_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowyStairs = BLOCK_DEFERRED_REGISTER.register("snowy_stairs", () -> new StairBlock(Blocks.OAK_PLANKS.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
    public static DeferredHolder<Block, Block> snowySlab = BLOCK_DEFERRED_REGISTER.register("snowy_slab", () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
}
