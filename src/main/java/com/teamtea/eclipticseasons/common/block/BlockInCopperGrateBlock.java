package com.teamtea.eclipticseasons.common.block;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInCopperGrateBlock extends WeatheringCopperGrateBlock implements EntityBlock {

    public BlockInCopperGrateBlock(WeatheringCopperGrateBlock weatherState, Properties properties) {
        super(weatherState.getAge(), properties);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        return BlockRegistry.getOriginalCopperGrateBlock(this).asItem().getDefaultInstance();
    }

    public static boolean validItemAndBlock(Level level, ItemStack stack, BlockState state) {
        Pair<BlockItem, HumidityControl> match = BlockInCopperGrateBlock.getItemMatch(level, stack);
        return match != null && BlockRegistry.getCopperGrateBlockChange(state.getBlock()) != null;
    }

    public static Pair<BlockItem, HumidityControl> getItemMatch(Level level, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            for (HumidityControl humidityControl : ESSortInfo.sorted2(level.registryAccess().lookupOrThrow(ESRegistries.HUMIDITY_CONTROL))) {
                if (humidityControl.ingredient().test(stack)) {
                    return Pair.of(blockItem, humidityControl);
                }
            }
        }
        return null;
    }

    public static InteractionResult getInteractionResult(ItemStack stack, Level level, BlockPos pos, BlockState state) {
        Pair<BlockItem, HumidityControl> itemMatch = getItemMatch(level, stack);
        if (itemMatch != null) {
            setNewBlock(level, pos, state, itemMatch.getFirst());
            return InteractionResult.SUCCESS_SERVER;
        }
        return null;
    }

    public static void setNewBlock(Level level, BlockPos pos, BlockState state, BlockItem input) {
        if (!level.isClientSide()) {
            Block copperGrateBlockChange = BlockRegistry.getCopperGrateBlockChange(state.getBlock());
            if (copperGrateBlockChange != Blocks.AIR) {
                level.setBlockAndUpdate(pos, copperGrateBlockChange.withPropertiesOf(state));
                if (level.getBlockEntity(pos) instanceof BlockInCopperGrateBlockEntity blockEntity) {
                    blockEntity.setInnerBlock(input.getBlock());
                }
            }
        }
    }

    public static @Nullable InteractionResult getInteractionResult(ItemStack stack, Level level, BlockPos pos) {
        Pair<BlockItem, HumidityControl> itemMatch = getItemMatch(level, stack);
        if (itemMatch != null) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof BlockInCopperGrateBlockEntity blockEntity) {
                    blockEntity.setInnerBlock(itemMatch.getFirst().getBlock());
                }
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        if (stack.isEmpty()) {
            if (!level.isClientSide()) {
                if (level.getBlockEntity(pos) instanceof BlockInCopperGrateBlockEntity blockEntity) {
                    blockEntity.setInnerBlock(null);
                }
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        return null;
    }

    @Override
    protected InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        InteractionResult result = getInteractionResult(stack, level, pos);
        if (result == InteractionResult.SUCCESS_SERVER) {
            if (!player.isCreative()) stack.shrink(1);
        }
        if (result != null) return result;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return BlockEntityRegistry.block_in_copper_grate_block_entity_type.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldIn, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        // return null;
        return !worldIn.isClientSide()?
                SimpleEntityBlock.createTickerHelper(blockEntityType, BlockEntityRegistry.block_in_copper_grate_block_entity_type.get(), BlockInCopperGrateBlockEntity::tick) : null;
    }

    // note 这里必须要手动处理，因为NeoForge的datamap在此时不可用
    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return state.getBlock() != BlockRegistry.block_in_oxidized_copper_grate_block.get();
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
