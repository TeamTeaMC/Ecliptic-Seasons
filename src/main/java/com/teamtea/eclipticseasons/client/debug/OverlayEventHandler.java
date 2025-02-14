package com.teamtea.eclipticseasons.client.debug;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import net.minecraft.client.Minecraft;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasonsApi.MODID)
public final class OverlayEventHandler {
    public final static ResourceLocation DEFAULT = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");
    private final static DebugInfoRenderer BAR_4 = new DebugInfoRenderer(Minecraft.getInstance());

    @SubscribeEvent(receiveCanceled = true)
    public static void onEvent(RenderGuiEvent.Post event) {

        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        var level = Minecraft.getInstance().level;
        // int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        // int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        // int f= 12;
        // event.getGuiGraphics().fillGradient(-width / 2,
        //         -height / 2,
        //         width,
        //         height,
        //         Color.WHITE.getRGB()+((f/5*4) <<24),
        //         Color.WHITE.getRGB()+f<<24);


        if (clientPlayer != null && level != null) {
            // if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
            // if(event.getOverlay().id().getPath().equals("all"))
            {
                if ((ClientConfig.Debug.debugInfo.get()
                        // || !FMLEnvironment.production
                )
                        && !Minecraft.getInstance().options.hideGui
                ) {
                    var solar = SolarHolders.getSaveDataLazy(clientPlayer.level()).get().getSolarTermsDay();
                    long dayTime = clientPlayer.level().getDayTime();
                    double env = clientPlayer.level().getBiome(clientPlayer.getOnPos()).value().getTemperature(clientPlayer.getOnPos());
                    int solarTime = SolarAngelHelper.getSolarAngelTime(clientPlayer.level(), clientPlayer.level().getDayTime());

                    BAR_4.renderStatusBar(event.getGuiGraphics(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight(), level, clientPlayer, solar + "", dayTime, env, solarTime);
                }
            }
        }
    }

}
