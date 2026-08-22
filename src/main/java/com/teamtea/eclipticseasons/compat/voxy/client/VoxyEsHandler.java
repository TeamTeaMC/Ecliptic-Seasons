package com.teamtea.eclipticseasons.compat.voxy.client;

import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraft.client.Minecraft;

public class VoxyEsHandler {

    public static final VoxyEsHandler INSTANCE = new VoxyEsHandler();

    public void onSolarTermChangeEvent(SolarTermChangeEvent event) {
        if (event.getLevel() != Minecraft.getInstance().level) return;

        // Auto reload consumes termChange/snowChange together every 15 seconds.
        // if (CompatModule.CommonConfig.voxyLODAutoReload.get())
        //     VoxyGeometryRefreshManager.refreshAll();
        if (CompatModule.CommonConfig.voxyReloadWhenSeasonChanged.get())
            VoxyTintManager.refreshAll();
    }
}
