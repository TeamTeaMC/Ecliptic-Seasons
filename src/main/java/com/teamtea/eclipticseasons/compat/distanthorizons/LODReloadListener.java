package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class LODReloadListener {

    @SubscribeEvent
    public void onSolarTermChange(SolarTermChangeEvent event) {
        if (event.getLevel().isClientSide()) {
            DHTool.clearCaches();
            DHClientTool.forceReloadAll();
        }
    }
}