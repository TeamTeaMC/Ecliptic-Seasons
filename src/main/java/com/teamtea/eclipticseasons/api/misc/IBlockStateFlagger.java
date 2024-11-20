package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockStateFlagger {
    int getBlockTypeFlag( BlockGetter level, BlockPos pos);
}
