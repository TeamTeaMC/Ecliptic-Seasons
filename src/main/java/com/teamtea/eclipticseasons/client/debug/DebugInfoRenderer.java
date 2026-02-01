package com.teamtea.eclipticseasons.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.lwjgl.opengl.GL11;

public final class DebugInfoRenderer {
    private final Minecraft mc;
    private long delay = 0;
    private net.minecraft.core.Holder<Biome> biomeHolder;

    public DebugInfoRenderer(Minecraft mc) {

        this.mc = mc;
    }

    public void renderStatusBar(GuiGraphics matrixStack, int screenWidth, int screenHeight, ClientLevel clientLevel, LocalPlayer player, String solar, long dayTime, double env, int solarTime) {

        BlockPos pos = player.blockPosition();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // RenderSystem.enableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        String solarS = "Solar Terms Day: " + solar;
        String dayS = "Day Time: " + dayTime;
        String envS = "Env Temp: " + env;
        String solarTimeS = "Solar Time: " + solarTime;
        //Holder<Biome> biome = clientLevel.getBiome(player.getOnPos());

        String dS = "Downfall: " + EclipticUtil.getRainfallAt(clientLevel, pos);
        String jS = "Humidity: " + EclipticUtil.getHumidityAt(clientLevel, pos) + "\n" + EclipticUtil.getHumidityLevelAt(player.level(), player.blockPosition());
        String ys = "y: " + MapChecker.getHeight(clientLevel, pos);

        int index = 0;

        drawInfo(matrixStack, screenWidth, screenHeight, solarS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, dayS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, envS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, dS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, jS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, solarTimeS, index++);
        drawInfo(matrixStack, screenWidth, screenHeight, ys, index++);

        Level level1;

        // level1 = level1 != null ? level1 : Minecraft.getInstance().level;
        level1 = Minecraft.getInstance().level;
        {
            Holder<Biome> standBiome =
                    biomeHolder == null || delay == 0 ?
                            level1.getBiome(player.getOnPos()) : biomeHolder;
            if (delay == 0) {
                delay = 100;
            }
            WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level1, standBiome);
            if (biomeWeather != null) {
                SolarTerm solarTerm = ClientCon.nowSolarTerm;
                String biomesS = "Biome: " + Component.translatable(Util.makeDescriptionId("biome", standBiome.unwrapKey().get().location())).getString();
                String biomesSS = "E-Biome: " + Component.translatable(Util.makeDescriptionId("biome", MapChecker.getSurfaceBiome(level1, pos).unwrapKey().get().location())).getString();

                String solarTermS = "Solar Term: " + solarTerm.getTranslation().getString();
                String biomeRainS = "Biome Rain: " + biomeWeather.getBiomeRain();
                String snowTermS = "Snow Term: " + SolarTerm.getSnowTerm(biomeWeather.biomeHolder.value(), false,EclipticUtil.getSnowTempChange(level1));
                drawInfo(matrixStack, screenWidth, screenHeight, "", index++);
                drawInfo(matrixStack, screenWidth, screenHeight, biomesS, index++);
                drawInfo(matrixStack, screenWidth, screenHeight, biomesSS, index++);

                drawInfo(matrixStack, screenWidth, screenHeight, solarTermS, index++);
                drawInfo(matrixStack, screenWidth, screenHeight, biomeRainS, index++);
                drawInfo(matrixStack, screenWidth, screenHeight, snowTermS, index++);

                drawInfo(matrixStack, screenWidth, screenHeight, "", index++);

                String rainTimeS = "Rain Time: " + biomeWeather.rainTime;
                String clearTimeS = "Clear Time: " + biomeWeather.clearTime;
                String thunderTimeS = "Thunder Time: " + biomeWeather.thunderTime;
                String snowDepthS = "Snow Depth: " + biomeWeather.getSnowDepth();

                drawInfo(matrixStack, screenWidth, screenHeight, rainTimeS, index++);
                drawInfo(matrixStack, screenWidth, screenHeight, clearTimeS, index++);
                drawInfo(matrixStack, screenWidth, screenHeight, thunderTimeS, index++);
                drawInfo(matrixStack, screenWidth, screenHeight, snowDepthS, index++);
            }
        }


        RenderSystem.enableBlend();
        // RenderSystem.disableAlphaTest();
        // mc.getTextureManager().bindForSetup(OverlayEventHandler.DEFAULT);

    }

    private void drawInfo(GuiGraphics matrixStack, int screenWidth, int screenHeight, String s, int index) {
        if (s.isEmpty()) return;
        // matrixStack.fill(screenWidth / 2 - mc.font.width(s) / 2 - 2,
        //         index * 9 + 3,
        //         screenWidth / 2 + mc.font.width(s) / 2 + 2,
        //         index * 9 + 3 + mc.font.lineHeight,
        //         Color.decode("#baccd9").getRGB());
        matrixStack.drawString(mc.font, s, screenWidth / 2 - mc.font.width(s) / 2, index * 9 + 3, 0xFFFFFF);
    }

}
