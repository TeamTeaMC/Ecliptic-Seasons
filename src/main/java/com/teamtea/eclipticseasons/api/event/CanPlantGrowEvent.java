package com.teamtea.eclipticseasons.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

/**
 * Fired when a crop block seems to grow and need extra check since it not fire neoforge event.
 *
 */
public class CanPlantGrowEvent extends CropGrowEvent implements IESEvent{
    private Pre.Result result = Pre.Result.DEFAULT;

    public CanPlantGrowEvent(Level level, BlockPos pos, BlockState state) {
        super(level, pos, state);
    }

    public void setResult(Pre.Result result) {
        this.result = result;
    }

    /**
     * {@return the result of this event, which controls if the click will be treated as handled}
     */
    public Pre.Result getResult() {
        return this.result;
    }

}
