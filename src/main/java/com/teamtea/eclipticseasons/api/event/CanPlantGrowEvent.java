package com.teamtea.eclipticseasons.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;


/**
 * Fired when a crop block seems to grow and need extra check since it can not fire forge event.
 *
 */
public class CanPlantGrowEvent extends BlockEvent.CropGrowEvent implements IESEvent{
    public CanPlantGrowEvent(Level level, BlockPos pos, BlockState state) {
        super(level, pos, state);
    }

}
