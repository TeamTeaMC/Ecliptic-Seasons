package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.BlockInCopperGrateBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockInWaxedCopperGrateBlock extends WaterloggedTransparentBlock implements EntityBlock {

    public BlockInWaxedCopperGrateBlock(Properties properties) {
        super(properties);
    }

    // @Override
    // public Item asItem() {
    //     if(this==BlockRegistry.block_in_wooden_grate_block.get())
    //         return ItemRegistry.block_in_wooden_grate_block_item.get();
    //     return BlockRegistry.getOriginalCopperGrateBlock(this).asItem();
    // }
    //
    // @Override
    // public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
    //     return BlockRegistry.getOriginalCopperGrateBlock(this).asItem().getDefaultInstance();
    // }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        InteractionResult result = BlockInCopperGrateBlock.getInteractionResult(stack, level, pos);
        if (result == InteractionResult.SUCCESS_SERVER) {
            if (!player.isCreative()) stack.shrink(1);
        }
        if (result != null) return result;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.block_in_copper_grate_block_entity_type.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldIn, BlockState state, BlockEntityType<T> blockEntityType) {
        // return null;
        return !worldIn.isClientSide() ?
                SimpleEntityBlock.createTickerHelper(blockEntityType, BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(), BlockInCopperGrateBlockEntity::tick) : null;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockState blockState = super.playerWillDestroy(level, pos, state, player);
        if (!level.isClientSide() && player.isCreative()) {
            BlockInCopperGrateBlockEntity.removeBlock(level, pos);
        }
        return blockState;
    }


}
