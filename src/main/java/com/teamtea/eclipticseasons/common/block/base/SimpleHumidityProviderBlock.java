package com.teamtea.eclipticseasons.common.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface SimpleHumidityProviderBlock {
    float getHumidityModifiedLevel();

    float getHumidityModifiedRange();

    default boolean isValid(Level level, BlockPos pos, BlockState state) {
        return true;
    }
}
