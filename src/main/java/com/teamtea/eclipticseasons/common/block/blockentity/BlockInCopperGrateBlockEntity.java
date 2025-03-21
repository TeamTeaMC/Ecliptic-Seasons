package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockInCopperGrateBlockEntity extends HumidityControlBlockEntity {

    protected Block innerBlock = null;

    public BlockInCopperGrateBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(), pos, state);
    }

    public static void popResource(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BlockInCopperGrateBlockEntity blockEntity) {
            blockEntity.popBlock(level, pos);
        }
    }

    public static void removeBlock(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BlockInCopperGrateBlockEntity blockEntity) {
            blockEntity.setInnerBlock(null);
        }
    }

    private void popBlock(Level level, BlockPos pos) {
        if (!level.isClientSide() && getInnerBlock() != Blocks.AIR
                && (this.humidityControl == null || this.humidityControl.lasting_time() * 0.8 < this.time)) {
            Block.popResource(level, pos, getInnerBlock().asItem().getDefaultInstance());
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("inner_block", BuiltInRegistries.BLOCK.getKey(getInnerBlock()).toString());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inner_block")) {
            Block block = BuiltInRegistries.BLOCK.get(new ResourceLocation(tag.getString("inner_block")));
            if (!block.defaultBlockState().isAir())
                this.innerBlock = block;
            else {
                this.innerBlock = null;
            }
        }
    }

    public void setInnerBlock(Block innerBlock) {
        popBlock(level, worldPosition);
        this.innerBlock = innerBlock;
        inventoryChanged();
    }

    public Block getInnerBlock() {
        return innerBlock == null ? Blocks.AIR : innerBlock;
    }


    public static void tick(Level level, BlockPos blockPos, BlockState blockState, BlockInCopperGrateBlockEntity blockEntity) {
        HumidityControlBlockEntity.tick(level, blockPos, blockState, blockEntity);
    }

    @Override
    protected boolean isRecipeCacheValid(@NotNull HumidityControl humidityControl) {
        if (innerBlock == null
                || !humidityControl.ingredient().test(innerBlock.asItem().getDefaultInstance()))
            return false;

        return super.isRecipeCacheValid(humidityControl);
    }


    @Override
    protected void endRecipe() {
        if (!hasNoRecipe() && this.time <= 0) {
            Item item = humidityControl.result().getItem();
            if (item instanceof BlockItem blockItem)
                setInnerBlock(blockItem.getBlock());
        }
        super.endRecipe();
    }

    @Override
    protected void resetRecipe() {
        super.resetRecipe();
        setInnerBlock(null);
    }
}
