package com.teamtea.eclipticseasons.api.misc;


import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public interface CustomRandomTick
{
    void tick(BlockState state, ServerWorld worldIn, BlockPos pos);
}
