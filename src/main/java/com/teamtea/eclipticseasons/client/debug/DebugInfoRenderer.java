package com.teamtea.eclipticseasons.client.debug;

import com.teamtea.eclipticseasons.api.constant.climate.WeatherMode;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DebugInfoRenderer {
    private final Minecraft mc;
    private int delay = 0;
    private Holder<Biome> cachedBiome;
    private Holder<Biome> e_cachedBiome;

    public DebugInfoRenderer(Minecraft mc) {
        this.mc = mc;
    }

    public void renderStatusBar(GuiGraphics guiGraphics, int screenWidth, int screenHeight, ClientLevel level, LocalPlayer player, String solarDay, long dayTime, double envTemp, int solarTime) {
        BlockPos pos = player.blockPosition();

        if (delay <= 0) {
            cachedBiome = level.getBiome(pos);
            e_cachedBiome = MapChecker.getSurfaceBiome(level, pos);
            delay = 20;
        } else {
            delay--;
        }

        List<String> infoLines = new ArrayList<>();
        infoLines.add("§6[Ecliptic Debug]§r");
        infoLines.add(String.format("Solar Day: §e%s§r", solarDay));
        infoLines.add(String.format("Solar Time: §b%d§r | Day Time: %d", solarTime, dayTime));
        infoLines.add(String.format("Humidity: §9%.2f§r", EclipticUtil.getHumidityLevelAt(level, pos)));
        infoLines.add(String.format("Rainfall: %s | Temp: §a%.2f§r", EclipticUtil.getRainfallAt(level, pos).getTranslation().getString(), envTemp));

        WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level, cachedBiome);
        if (biomeWeather != null) {
            SolarTerm currentTerm = ClientCon.nowSolarTerm;
            infoLines.add("");
            infoLines.add("Biome: " + getBiomeName(cachedBiome) + " (%s)".formatted(cachedBiome.unwrapKey().map(ResourceKey::location).orElse(null)));
            infoLines.add("Surface Biome: " + (e_cachedBiome != null ? (getBiomeName(e_cachedBiome) + " (%s)".formatted(cachedBiome.unwrapKey().map(ResourceKey::location).orElse(null))) : "Unknown"));
            infoLines.add("Snow Term: " + SolarTerm.getSnowTerm(biomeWeather.biomeHolder.value(), false, EclipticUtil.getSnowTempChange(level)));
            infoLines.add("Solar Term:§r §d" + currentTerm.getTranslation().getString() + "§r");
            infoLines.add(String.format("R/C/T Time: %d / %d / %d", biomeWeather.rainTime, biomeWeather.clearTime, biomeWeather.thunderTime));
            infoLines.add("Snow Depth: " + biomeWeather.getSnowDepth());
            infoLines.add("Map Height (y): " + MapChecker.getHeight(level, pos));

            infoLines.add("");
            //infoLines.add("§l[Weather Logic]");

            WeatherMode weatherMode = EclipticUtil.getWeatherMode(level);
            Holder<Biome> owner = null;
            if (weatherMode == WeatherMode.REGION) {
                owner = BiomeClimateManager.getWeatherRegionOnwer(biomeWeather.biomeHolder.value());
            }

            Holder<Biome> targetBiome = (owner != null) ? owner : e_cachedBiome;
            boolean isSlave = owner != null && !owner.equals(e_cachedBiome);

            if (!EclipticUtil.hasLocalWeather(level)) {
                infoLines.add("Mode: §cVanilla Sync§r");
            } else {


                WeatherManager.BiomeWeather weatherTarget = WeatherManager.getBiomeWeather(level, targetBiome);
                int size = Optional.ofNullable(WeatherManager.getBiomeList(level)).map(List::size).orElse(64);
                if (weatherTarget != null) {
                    infoLines.add("Biome Rain: " + weatherTarget.getBiomeRain().toString());
                    if (isSlave) {
                        infoLines.add("Owner: §e" + getBiomeName(owner) + "§r");
                    }
                    //if (biomeWeather.shouldRain()) {
                    //    infoLines.add("Rain: §aRaining§r");
                    //} else
                    {
                        float downfall = EclipticUtil.getDownfallFloatConstant(currentTerm, targetBiome.value(), false);
                        float rainWeight = weatherTarget.getBiomeRain().getRainChance()
                                * Math.max(0.01f, downfall)
                                * (CommonConfig.Weather.rainChanceMultiplier.get() / 100f);
                        infoLines.add(String.format("Rain Chance: §b%.2f%%§r", Math.min(rainWeight * 100, 100)));
                    }

                    //if (biomeWeather.shouldThunder()) {
                    //    infoLines.add("Thunder: §eThundering§r");
                    //} else
                    if (biomeWeather.shouldRain()) {
                        float thunderWeight = weatherTarget.getBiomeRain().getThunderChance()
                                * (CommonConfig.Weather.thunderChanceMultiplier.get() / 100f)
                                * size / 3000f;
                        infoLines.add(String.format("Thunder Chance: §e%.2f%%§r", Math.min(thunderWeight * 10000, 100)));
                    } else {
                        infoLines.add("Thunder: §8Waiting Rain§r");
                    }
                }
            }
        }

        renderList(guiGraphics, infoLines);
    }

    private void renderList(GuiGraphics guiGraphics, List<String> lines) {
        int x = 6;
        int y = 6;
        int bgPadding = 2;
        int alphaBackground = 0x90000000;

        for (String line : lines) {
            if (line.isEmpty()) {
                y += 5;
                continue;
            }

            int textWidth = mc.font.width(line);
            int textHeight = mc.font.lineHeight;

            guiGraphics.fill(x - bgPadding, y - bgPadding + 1, x + textWidth + bgPadding, y + textHeight, alphaBackground);

            guiGraphics.drawString(mc.font, line, x, y, 0xFFFFFF, true);

            y += textHeight + 2;
        }
    }

    private String getBiomeName(Holder<Biome> biomeHolder) {
        return Component.translatable(Util.makeDescriptionId("biome", biomeHolder.unwrapKey().map(ResourceKey::location).orElse(null))).getString();
    }
}