package com.teamtea.eclipticseasons.common.block;

import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.NonNull;

public class ThinIceBlock extends IceBlock implements SimpleWaterloggedBlock {
    public ThinIceBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull FluidState getFluidState(@NonNull BlockState state) {
        return Fluids.WATER.getSource().defaultFluidState();
    }
}
