package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.compat.CompatModule;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;

public class VoxyEsHandler {

    public static final VoxyEsHandler INSTANCE = new VoxyEsHandler();

    @SubscribeEvent
    public void onSolarTermChangeEvent(SolarTermChangeEvent event) {
        if (event.getLevel() == Minecraft.getInstance().level
                && CompatModule.CommonConfig.voxyReloadWhenSeasonChanged.get()) {
            try {
                IGetVoxyRenderSystem levelRenderer = (IGetVoxyRenderSystem)
                        Minecraft.getInstance().levelRenderer;
                levelRenderer.shutdownRenderer();
                levelRenderer.createRenderer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
