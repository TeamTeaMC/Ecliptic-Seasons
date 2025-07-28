package com.teamtea.eclipticseasons.common.block.base;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.common.block.CalendarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WallPlacedBlock extends SimpleHorizontalEntityBlock {


    public WallPlacedBlock(Properties properties) {
        super(properties);
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        if (!Direction.Plane.HORIZONTAL.test(clickedFace)) {
            clickedFace = context.getHorizontalDirection().getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, clickedFace);
    }

}
