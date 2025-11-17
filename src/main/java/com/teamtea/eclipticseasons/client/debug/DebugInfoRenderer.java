package com.teamtea.eclipticseasons.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.biome.Rainfall;
import com.teamtea.eclipticseasons.api.constant.biome.Temperature;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class DebugInfoRenderer {
    private final Minecraft mc;

    public DebugInfoRenderer(Minecraft mc) {

        this.mc = mc;
    }

    public void renderStatusBar(GuiGraphics matrixStack, int screenWidth, int screenHeight, LocalPlayer player, Holder<Biome> standBiome, SolarTerm solar, long dayTime, double env, double d, Humidity h, int solarTime) {

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        String solarS = "Solar Terms Day: " + solar;
        String dayS = "Day Time: " + dayTime;
        String envS = "Env Temp: " + env;
        String dS = "Downfall: " + d;
        String jS = "Humidity: " + h + "\n" + EclipticUtil.getHumidityLevelAt(player.level(), player.blockPosition());
        String solarTimeS = "Solar Time: " + solarTime;

        int index = 0;
        String biomesS = "Biome: " + Component.translatable(Util.makeDescriptionId("biome", standBiome.unwrapKey().get().location())).getString();

        drawInfo(matrixStack, screenWidth, screenHeight, solarS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, dayS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, biomesS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, envS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, dS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, jS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, solarTimeS, index++);

        // drawInfo(matrixStack,screenWidth,screenHeight, MapChecker.getSurfaceBiome(WeatherManager.getMainServerLevel(), ClientCon.agent.getCameraEntity().blockPosition()).unwrapKey().get().location()+"",index++);
        // drawInfo(matrixStack,screenWidth,screenHeight, MapChecker.getSurfaceBiome(ClientCon.getUseLevel(), ClientCon.agent.getCameraEntity().blockPosition()).unwrapKey().get().location()+"",index++);
        // drawInfo(matrixStack,screenWidth,screenHeight, ClientCon.getUseLevel().getBiome(ClientCon.agent.getCameraEntity().blockPosition()).unwrapKey().get().location()+"",index++);

        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.dimension() == Level.OVERWORLD && level instanceof ServerLevel) {
                {
                    ArrayList<WeatherManager.BiomeWeather> biomeWeathers = WeatherManager.getBiomeList(level);
                    if (biomeWeathers != null)
                        for (WeatherManager.BiomeWeather biomeWeather : biomeWeathers) {
                            if (((Holder.Reference<Biome>) biomeWeather.biomeHolder).key().location().equals(((Holder.Reference<Biome>) standBiome).key().location())) {
                                SolarDataManager saveData = SolarHolders.getSaveData(level);
                                if (saveData != null) {
                                    var solarTerm = saveData.getSolarTerm();
                                    String solarTermS = "Solar Term: " + solarTerm.getTranslation().getString();
                                    String biomeRainS = "Biome Rain: " + biomeWeather.getBiomeRain();
                                    String snowTermS = "Snow Term: " + SolarTerm.getSnowTerm(biomeWeather.biomeHolder.value(), false, EclipticUtil.getSnowTempChange(level));
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
                                }

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
