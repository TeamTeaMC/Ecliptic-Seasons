package com.teamtea.eclipticseasons.common.block;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.data.craft.HumidityControl;
import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.BlockInCopperGrateBlockEntity;
import com.teamtea.eclipticseasons.common.block.blockentity.QuestHangingSignBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockInGrateBlock extends SimpleEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BlockInGrateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE));
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
        // 1.20.1中缺乏本地信息
        if (level.isClientSide()) return InteractionResult.sidedSuccess(level.isClientSide());

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
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult interactionResult = getItemInteractionResult(stack, level, pos);
        if (interactionResult != null && !level.isClientSide()) return interactionResult;
        return super.use(state, level, pos, player, hand, hitResult);
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
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        if (!level.isClientSide() && player.isCreative()) {
            BlockInCopperGrateBlockEntity.removeBlock(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!movedByPiston && !level.isClientSide() && !newState.is(this)) {
            BlockInCopperGrateBlockEntity.popResource(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return Shapes.empty();
    }

    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }


    @javax.annotation.Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_313836_) {
        FluidState fluidstate = p_313836_.getLevel().getFluidState(p_313836_.getClickedPos());
        return super.getStateForPlacement(p_313836_).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.is(Fluids.WATER)));
    }

    @Override
    public BlockState updateShape(
            BlockState p_313906_, Direction p_313739_, BlockState p_313829_, LevelAccessor p_313692_, BlockPos p_313842_, BlockPos p_313843_
    ) {
        if (p_313906_.getValue(WATERLOGGED)) {
            p_313692_.scheduleTick(p_313842_, Fluids.WATER, Fluids.WATER.getTickDelay(p_313692_));
        }

        return super.updateShape(p_313906_, p_313739_, p_313829_, p_313692_, p_313842_, p_313843_);
    }

    @Override
    public FluidState getFluidState(BlockState p_313789_) {
        return p_313789_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(true) : super.getFluidState(p_313789_);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_313896_) {
        p_313896_.add(WATERLOGGED);
    }
}
