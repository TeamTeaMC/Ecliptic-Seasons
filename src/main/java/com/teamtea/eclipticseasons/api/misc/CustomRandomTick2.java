package com.teamtea.eclipticseasons.api.misc;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomRandomTick2
{
    void tick(ServerLevel worldIn, Biome biome, BlockPos pos);
}
