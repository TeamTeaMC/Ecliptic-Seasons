package com.teamtea.eclipticseasons.client.debug;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasonsApi.MODID)
public final class OverlayEventHandler {
    private final static DebugInfoRenderer RENDERER = new DebugInfoRenderer(Minecraft.getInstance());

    @SubscribeEvent
    public static void onEvent(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        var level = mc.level;

        if (player != null && level != null && !mc.options.hideGui) {
            if (ClientConfig.Debug.debugInfo.get()) {
                BlockPos pos = player.blockPosition();

                var solarTermsDay = EclipticUtil.getNowSolarDay(level);
                long dayTime = level.getDayTime();
                double envTemp = EclipticUtil.getTemperatureFloat(level, level.getBiome(pos).value(), pos);
                int solarTime = SolarAngelHelper.getSolarAngelTime(level, dayTime);

                RENDERER.renderStatusBar(
                        event.getGuiGraphics(),
                        mc.getWindow().getGuiScaledWidth(),
                        mc.getWindow().getGuiScaledHeight(),
                        level,
                        player,
                        String.valueOf(solarTermsDay),
                        dayTime,
                        envTemp,
                        solarTime
                );
            }
        }
    }
}