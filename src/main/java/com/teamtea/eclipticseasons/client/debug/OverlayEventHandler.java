package com.teamtea.eclipticseasons.client.debug;


import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import net.minecraft.client.Minecraft;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
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
                if (ClientConfig.Debug.debugInfo.get()
                        && !Minecraft.getInstance().options.hideGui
                    // || !FMLEnvironment.production
                ) {
                    Level level1 = clientPlayer.level();
                    BlockPos onPos = clientPlayer.blockPosition();
                    var solar = EclipticUtil.getNowSolarDay(level1);
                    long dayTime = level1.getDayTime();
                    double env = EclipticUtil.getTemperatureFloat(level1, level1.getBiome(onPos).value(), onPos);
                    int solarTime = SolarAngelHelper.getSolarAngelTime(level1, level1.getDayTime());

                    BAR_4.renderStatusBar(event.getGuiGraphics(), Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight(), (ClientLevel) level1, clientPlayer, solar + "", dayTime, env, solarTime);
                }
            }
        }
    }
}
