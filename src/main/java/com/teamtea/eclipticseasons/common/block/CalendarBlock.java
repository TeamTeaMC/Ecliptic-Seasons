package com.teamtea.eclipticseasons.common.block;


import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class CalendarBlock extends SimpleHorizontalEntityBlock {

    protected final static VoxelShape shape_N = VoxelShapes.box(0.1875, 0, 0.75, 0.8125, 0.875, 1);
    protected final static VoxelShape shape_S = VoxelShapes.box(0.1875, 0, 0, 0.8125, 0.875, 0.25);
    protected final static VoxelShape shape_W = VoxelShapes.box(0.75, 0, 0.1875, 1, 0.875, 0.8125);
    protected final static VoxelShape shape_E = VoxelShapes.box(0, 0, 0.1875, 0.25, 0.875, 0.8125);
    protected final static VoxelShape[] shapes = new VoxelShape[]{
            shape_S, shape_W, shape_N, shape_E
    };

    public CalendarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return shapes[pState.getValue(FACING).get2DDataValue()];
    }

    // @Override
    // protected boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
    //     var facing = pState.getValue(FACING);
    //     var facePos = pPos.relative(pState.getValue(FACING).getOpposite());
    //     return pLevel.getBlockState(facePos).isFaceSturdy(pLevel, facePos, facing);
    // }
    @Override
    public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
        Direction direction = state.getValue(FACING).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }


    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, IWorld pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pDirection == pState.getValue(FACING).getOpposite() && pNeighborState.isAir())
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader reader) {
        return BlockEntityRegistry.calendar_entity_type.get().create();
    }

}
