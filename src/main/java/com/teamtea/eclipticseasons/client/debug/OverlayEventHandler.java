package com.teamtea.eclipticseasons.client.debug;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.registry.KeyMappingRegistry;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = EclipticSeasonsApi.MODID)
public final class OverlayEventHandler {
    private static DebugInfoRenderer RENDERER;

    @SubscribeEvent
    public static void onEvent(RenderGuiEvent.Post event) {
        if (RENDERER == null) RENDERER = new DebugInfoRenderer(Minecraft.getInstance());
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        var level = mc.level;

        if (player != null && level != null && !mc.options.hideGui) {
            if (KeyMappingRegistry.DEBUG_KEY.consumeClick()) {
                ClientConfig.Debug.debugInfo.set(!ClientConfig.Debug.debugInfo.getAsBoolean());
            }

            if (ClientConfig.Debug.debugInfo.get() || ClientConfig.GUI.simpleSeasonHud.get()) {
                BlockPos pos = player.blockPosition();

                var solarTermsDay = EclipticUtil.getNowSolarDay(level);
                long dayTime = Math.floorMod(level.getDefaultClockTime(), EclipticUtil.getDayLengthInMinecraft(level));
                double envTemp = EclipticUtil.getTemperatureFloat(level, level.getBiome(pos).value(), pos);
                int solarTime = -1;
                solarTime = level.dimensionType().defaultClock().<Integer>map(
                        clockHolder ->
                                SolarAngelHelper.getSolarAngelTime(clockHolder, dayTime, EclipticUtil.getDayLengthInMinecraft(level))
                ).orElse(0);

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