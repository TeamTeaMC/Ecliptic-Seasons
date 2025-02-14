package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CalendarBlock extends SimpleHorizontalEntityBlock {

    protected final static VoxelShape shape_N = Shapes.box(0.1875, 0, 0.75, 0.8125, 0.875, 1);
    protected final static VoxelShape shape_S = Shapes.box(0.1875, 0, 0, 0.8125, 0.875, 0.25);
    protected final static VoxelShape shape_W = Shapes.box(0.75, 0, 0.1875, 1, 0.875, 0.8125);
    protected final static VoxelShape shape_E = Shapes.box(0, 0, 0.1875, 0.25, 0.875, 0.8125);
    protected final static VoxelShape[] shapes = new VoxelShape[]{
            shape_S, shape_W, shape_N, shape_E
    };

    public CalendarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
       return shapes[pState.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CalendarBlock::new);
    }

    // @Override
    // protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
    //     var facing = pState.getValue(FACING);
    //     var facePos = pPos.relative(pState.getValue(FACING).getOpposite());
    //     return pLevel.getBlockState(facePos).isFaceSturdy(pLevel, facePos, facing);
    // }
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }


    @Override
    protected BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pDirection == pState.getValue(FACING).getOpposite() && pNeighborState.isAir())
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityRegistry.calendar_entity_type.get().create(pPos, pState);
    }
}
