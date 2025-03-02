package com.teamtea.eclipticseasons.compat.touhou_little_maid;

import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.Iterator;
import java.util.Map;

public class LittleMaid {
    public static final LittleMaid INSTANCE = new LittleMaid();

    @SubscribeEvent
    public void stopServer(ServerStoppingEvent event) {
        synchronized (CleanSnowTask.hasCleanedPos) {
            CleanSnowTask.hasCleanedPos.clear();
        }
    }

    @SubscribeEvent
    public void tickQuery(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase.equals(TickEvent.Phase.END)) {
            Level level = event.level;
            if (level.getGameTime() % 200 == level.getRandom().nextInt(16)) {
                Iterator<Map.Entry<GlobalPos, Long>> iterator = CleanSnowTask.hasCleanedPos.entrySet().iterator();
                long currentTime = level.getGameTime();
                long threshold = currentTime - 20L * 60 * 5;
                ResourceKey<Level> dimension = level.dimension();
                while (iterator.hasNext()) {
                    Map.Entry<GlobalPos, Long> entry = iterator.next();
                    if (dimension.equals(entry.getKey().dimension()) && entry.getValue() < threshold) {
                        iterator.remove();
                    }
                }
            }
        }
    }
}
