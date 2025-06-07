package com.teamtea.eclipticseasons.common.hook;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import com.teamtea.eclipticseasons.api.event.CanPlantGrowEvent;

public class ESEventHook {

    /**
     * Checks if a plant can grow by firing {@link CanPlantGrowEvent}. It's an extra check for plants not support event.
     *
     * @param level The level the plant is in
     * @param pos   The id of the plant
     * @param state The state of the plant
     * @param def   The result of the default checks performed by the plant.
     * @return true if the plant can grow
     */
    public static boolean canExtraCropGrow(Level level, BlockPos pos, BlockState state, boolean def) {
        var ev = new CanPlantGrowEvent(level, pos, state);
        NeoForge.EVENT_BUS.post(ev);
        return (ev.getResult() == CropGrowEvent.Pre.Result.GROW || (ev.getResult() == CropGrowEvent.Pre.Result.DEFAULT && def));
    }
}
