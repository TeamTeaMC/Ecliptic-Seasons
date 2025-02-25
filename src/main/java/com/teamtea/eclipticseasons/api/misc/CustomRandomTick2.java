package com.teamtea.eclipticseasons.api.misc;



import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;

public interface CustomRandomTick2
{
    void tick(ServerWorld worldIn, Biome biome, BlockPos pos);
}
