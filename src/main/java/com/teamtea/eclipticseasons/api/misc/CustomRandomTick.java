package com.teamtea.eclipticseasons.api.misc;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomRandomTick
{
    void tick(BlockState state, ServerLevel worldIn, BlockPos pos);
}
