package com.teamtea.eclipticseasons.client.debug;


import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import net.minecraft.client.Minecraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.teamtea.eclipticseasons.EclipticSeasons;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasons.MODID)
public final class OverlayEventHandler {
    public final static ResourceLocation DEFAULT = new ResourceLocation("minecraft", "textures/gui/icons.png");
    private final static DebugInfoRenderer BAR_4 = new DebugInfoRenderer(Minecraft.getInstance());

    @SubscribeEvent(receiveCanceled = true)
    public static void onEvent(RenderGuiOverlayEvent.Pre event) {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null) {
            // if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
            // if(event.getOverlay().id().getPath().equals("all"))
            {
                if (ClientConfig.GUI.debugInfo.get()
                        // || !FMLEnvironment.production
                )
                {
                    Holder<Biome> biome = clientPlayer.level().getBiome(clientPlayer.getOnPos());
                    var solar = SolarHolders.getSaveDataLazy(clientPlayer.level()).orElse(new SolarDataManager(clientPlayer.level())).getSolarTerm();
                    long dayTime = clientPlayer.level().getDayTime();
                    float temp = biome.get().getTemperature(clientPlayer.getOnPos());
                    float downfall = biome.get().getModifiedClimateSettings().downfall();
                    Humidity h = Humidity.getHumid(downfall, temp);
                    double env = biome.get().getTemperature(clientPlayer.getOnPos());
                    int solarTime = SolarAngelHelper.getSolarAngelTime(clientPlayer.level(), clientPlayer.level().getDayTime());

                    BAR_4.renderStatusBar(event.getGuiGraphics(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight(),clientPlayer,biome, solar, dayTime, env,downfall,h, solarTime);
                }
            }
        }
    }
}
