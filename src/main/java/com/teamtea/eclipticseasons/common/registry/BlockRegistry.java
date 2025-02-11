package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.block.*;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {
    public static final DeferredRegister<Block> ModBlocks = DeferredRegister.create(ForgeRegistries.BLOCKS, EclipticSeasons.MODID);
    public static RegistryObject<Block> snowySlab = ModBlocks.register("snowy_slab", () -> new SlabBlock(AbstractBlock.Properties.copy(Blocks.OAK_SLAB).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyStairs = ModBlocks.register("snowy_stairs", () -> new StairsBlock(Blocks.OAK_PLANKS::defaultBlockState, AbstractBlock.Properties.copy(Blocks.OAK_STAIRS).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyBlock = ModBlocks.register("snowy_block", () -> new Block(AbstractBlock.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
    public static RegistryObject<Block> snowyLeaves = ModBlocks.register("snowy_leaves", () -> new Block(AbstractBlock.Properties.copy(Blocks.SNOW_BLOCK).dynamicShape().noOcclusion()));
}
