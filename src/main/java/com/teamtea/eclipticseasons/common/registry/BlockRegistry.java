package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> ModBlocks = DeferredRegister.create(ForgeRegistries.BLOCKS, EclipticSeasons.MODID);
    public static RegistryObject<Block> snowySlab = ModBlocks.register("snowy_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyStairs = ModBlocks.register("snowy_stairs", () -> new StairBlock(Blocks.OAK_PLANKS::defaultBlockState, BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyBlock = ModBlocks.register("snowy_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyLeaves = ModBlocks.register("snowy_leaves", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
}
