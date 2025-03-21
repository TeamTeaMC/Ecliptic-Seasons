package com.teamtea.eclipticseasons.common.block;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.BlockInCopperGrateBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInCopperGrateBlock extends SimpleEntityBlock {

    public BlockInCopperGrateBlock(Properties properties) {
        super( properties);
    }

    public static Pair<BlockItem, HumidityControl> getItemMatch(Level level, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            for (HumidityControl humidityControl : level.registryAccess().registryOrThrow(ESRegistries.HUMIDITY_CONTROL)) {
                if (humidityControl.ingredient().test(stack)) {
                    return Pair.of(blockItem, humidityControl);
                }
            }
        }
        return null;
    }


    public static @Nullable InteractionResult getItemInteractionResult(ItemStack stack, Level level, BlockPos pos) {
        Pair<BlockItem, HumidityControl> itemMatch = getItemMatch(level, stack);
        if (itemMatch != null) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof BlockInCopperGrateBlockEntity blockEntity) {
                    blockEntity.setInnerBlock(itemMatch.getFirst().getBlock());
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (stack.isEmpty()) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof BlockInCopperGrateBlockEntity blockEntity) {
                    blockEntity.setInnerBlock(null);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
         ItemStack stack=player.getItemInHand(hand);
        InteractionResult level1 = getItemInteractionResult(stack, level, pos);
        if (level1 != null) return level1;
        return super.use( state, level, pos, player, hand, hitResult);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return BlockEntityRegistry.block_in_copper_grate_block_entity_type.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldIn, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        // return null;
        return !worldIn.isClientSide ?
                SimpleEntityBlock.createTickerHelper(blockEntityType, BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(), BlockInCopperGrateBlockEntity::tick) : null;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!movedByPiston && !level.isClientSide() && !newState.is(this)) {
            BlockInCopperGrateBlockEntity.popResource(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
