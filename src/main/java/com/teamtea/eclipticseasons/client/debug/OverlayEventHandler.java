package com.teamtea.eclipticseasons.client.debug;


import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import net.minecraft.client.Minecraft;

import net.minecraft.client.entity.player.ClientPlayerEntity;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.teamtea.eclipticseasons.EclipticSeasons;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasons.MODID)
public final class OverlayEventHandler {
    public final static ResourceLocation DEFAULT = new ResourceLocation("minecraft", "textures/gui/icons.png");
    private static DebugInfoRenderer BAR_4;

    @SubscribeEvent(receiveCanceled = true)
    public static void onEvent(RenderGameOverlayEvent.Pre event) {
        if (BAR_4 == null) BAR_4 = new DebugInfoRenderer(Minecraft.getInstance());

        ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
        ClientWorld level = Minecraft.getInstance().level;
        if (clientPlayer != null && level != null) {
            // if (event.getType() == RenderGameOverlayEvent.ElementType.ALL)
            // if(event.getOverlay().id().getPath().equals("all"))
            {
                if (ClientConfig.GUI.debugInfo.get()
                    // || !FMLEnvironment.production
                ) {
                    SolarTerm solar = SolarHolders.getSaveDataLazy(clientPlayer.level).orElse(new SolarDataManager(clientPlayer.level)).getSolarTerm();
                    long dayTime = clientPlayer.level.getDayTime();
                    float temp = clientPlayer.level.getBiome(clientPlayer.blockPosition()).getTemperature(clientPlayer.blockPosition());
                    Humidity h = Humidity.getHumid(clientPlayer.level.getBiome(clientPlayer.blockPosition()).getDownfall(), temp);
                    double env = clientPlayer.level.getBiome(clientPlayer.blockPosition()).getTemperature(clientPlayer.blockPosition());
                    int solarTime = SolarAngelHelper.getSolarAngelTime(clientPlayer.level, clientPlayer.level.getDayTime());


                    BAR_4.renderStatusBar(event.getMatrixStack(), event.getWindow().getGuiScaledWidth(), event.getWindow().getGuiScaledHeight(), level, clientPlayer, solar, dayTime, env, solarTime);
                }
            }
        }
    }
}
