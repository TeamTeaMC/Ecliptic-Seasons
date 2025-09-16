package com.teamtea.eclipticseasons.common.hook;

import com.teamtea.eclipticseasons.api.event.CanPlantGrowEvent;
import com.teamtea.eclipticseasons.api.event.ESClientEntityTickEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public class ESEventHook {

    /**
     * Checks if a plant can grow by firing {@link CanPlantGrowEvent}. It's an extra check for plants not support event.
     *
     * @param level The level the plant is in
     * @param pos   The position of the plant
     * @param state The state of the plant
     * @param def   The result of the default checks performed by the plant.
     * @return true if the plant can grow
     */
    public static boolean canExtraCropGrow(Level level, BlockPos pos, BlockState state, boolean def) {
        var ev = new CanPlantGrowEvent(level, pos, state);
        MinecraftForge.EVENT_BUS.post(ev);
        return (ev.getResult() == Event.Result.ALLOW || (ev.getResult() == Event.Result.DEFAULT && def));
    }

    public static void onClientEntityTick(Entity entity) {
        MinecraftForge.EVENT_BUS.post(new ESClientEntityTickEvent(entity));
    }
}
