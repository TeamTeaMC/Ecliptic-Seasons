package com.teamtea.eclipticseasons.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.lwjgl.opengl.GL11;

public final class DebugInfoRenderer {
    private final Minecraft mc;

    public DebugInfoRenderer(Minecraft mc) {

        this.mc = mc;
    }

    public void renderStatusBar(GuiGraphics matrixStack, int screenWidth, int screenHeight,ClientLevel clientLevel, LocalPlayer player, SolarTerm solar, long dayTime, double env, int solarTime) {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        String solarS = "Solar Terms Day: " + solar;
        String dayS = "Day Time: " + dayTime;
        String envS = "Env Temp: " + env;
        String solarTimeS = "Solar Time: " + solarTime;

        int index = 0;

        drawInfo(matrixStack, screenWidth, screenHeight, solarS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, dayS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, envS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, solarTimeS, index++);

        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.dimension() == Level.OVERWORLD && level instanceof ServerLevel) {
                {
                    var standBiome = level.getBiome(player.getOnPos());
                    for (WeatherManager.BiomeWeather biomeWeather : WeatherManager.getBiomeList(level)) {
                        if (((Holder.Reference<Biome>) biomeWeather.biomeHolder).key().location().equals(((Holder.Reference<Biome>) standBiome).key().location())) {
                            var solarTerm = SolarHolders.getSaveData(level).getSolarTerm();
                            String solarTermS = "Solar Term: " + solarTerm.getTranslation().getString();
                            String biomeRainS = "Biome Rain: " + solarTerm.getBiomeRain(biomeWeather.biomeHolder);
                            String snowTermS = "Snow Term: " + SolarTerm.getSnowTerm(biomeWeather.biomeHolder.get());
                            drawInfo(matrixStack, screenWidth, screenHeight, "", index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, solarTermS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, biomeRainS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, snowTermS, index++);

                            drawInfo(matrixStack, screenWidth, screenHeight, "", index++);

                            String rainTimeS = "Rain Time: " + biomeWeather.rainTime;
                            String clearTimeS = "Clear Time: " + biomeWeather.clearTime;
                            String thunderTimeS = "Thunder Time: " + biomeWeather.thunderTime;
                            String snowDepthS = "Snow Depth: " + biomeWeather.snowDepth;

                            drawInfo(matrixStack, screenWidth, screenHeight, rainTimeS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, clearTimeS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, thunderTimeS, index++);
                            drawInfo(matrixStack, screenWidth, screenHeight, snowDepthS, index++);

                            break;
                        }
                    }

                }
            }
        }


        RenderSystem.enableBlend();
        // RenderSystem.disableAlphaTest();
        mc.getTextureManager().bindForSetup(OverlayEventHandler.DEFAULT);

    }

    private void drawInfo(GuiGraphics matrixStack, int screenWidth, int screenHeight, String s, int index) {
        matrixStack.drawString(mc.font, s, screenWidth / 2 - mc.font.width(s) / 2, index * 9 + 3, 0xFFFFFF);
    }

}
