package com.teamtea.eclipticseasons.api.misc;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

public interface CustomRandomTick2
{
    void tick(ServerLevel worldIn, Biome biome, BlockPos pos);
}
