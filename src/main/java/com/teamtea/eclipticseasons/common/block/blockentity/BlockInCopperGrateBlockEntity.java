package com.teamtea.eclipticseasons.common.block.blockentity;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.common.block.BlockInCopperGrateBlock;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class BlockInCopperGrateBlockEntity extends HumidityControlBlockEntity {

    protected Block innerBlock = null;
    protected final GrateItemStackHandler itemStackHandler = new GrateItemStackHandler();

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

    protected void popBlock(Level level, BlockPos pos) {
        if (canBlockObtained(level)) {
            Block.popResource(level, pos, getInnerBlock().asItem().getDefaultInstance());
        }
    }

    private boolean canBlockObtained(Level level) {
        return level != null && !level.isClientSide() && getInnerBlock() != Blocks.AIR
                && (this.humidityControl == null
                || this.humidityControl.lasting_time() * 0.6 < this.time
                || humidityControl.noCost());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("inner_block", BuiltInRegistries.BLOCK.getKey(getInnerBlock()).toString());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inner_block")) {
            Block block = BuiltInRegistries.BLOCK.get(EclipticSeasons.parse(tag.getString("inner_block")));
            if (!block.defaultBlockState().isAir())
                setBlockAndItemNotSync(block);
            else {
                setBlockAndItemNotSync(null);
            }
        }
    }

    protected void setBlockNotSync(Block block) {
        this.innerBlock = block;
    }

    protected void setBlockAndItemNotSync(Block block) {
        setBlockNotSync(block);
        this.itemStackHandler.setStackInSlotNotSync(0,
                (block == null ? Items.AIR : block.asItem()).getDefaultInstance());
    }

    public void setInnerBlock(Block innerBlock) {
        popBlock(getLevel(), getBlockPos());
        setBlockAndItemNotSync(innerBlock);
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


    public GrateItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public class GrateItemStackHandler extends ItemStackHandler {
        public GrateItemStackHandler() {
            super(1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return BlockInCopperGrateBlock.getItemMatch(level, stack) != null;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setBlockNotSync(Block.byItem(getStackInSlot(0).getItem()));
            inventoryChanged();
        }

        public void setStackInSlotNotSync(int slot, @NotNull ItemStack stack) {
            this.validateSlotIndex(slot);
            this.stacks.set(slot, stack);
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return super.getStackInSlot(slot);
        }
    }
}
