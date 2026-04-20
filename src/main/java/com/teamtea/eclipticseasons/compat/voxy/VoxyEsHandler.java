package com.teamtea.eclipticseasons.compat.voxy;

import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.compat.CompatModule;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class VoxyEsHandler {

    public static final VoxyEsHandler INSTANCE = new VoxyEsHandler();

    @SubscribeEvent
    public void onSolarTermChangeEvent(SolarTermChangeEvent event) {
        if (event.getLevel() == Minecraft.getInstance().level
                && CompatModule.CommonConfig.voxyReloadWhenSeasonChanged.get()) {
            try {
                IGetVoxyRenderSystem levelRenderer = (IGetVoxyRenderSystem)
                        Minecraft.getInstance().levelRenderer;
                levelRenderer.voxy$shutdownRenderer();
                levelRenderer.voxy$createRenderer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
