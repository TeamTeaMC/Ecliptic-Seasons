package com.teamtea.eclipticseasons.client.debug;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.ISolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.data.season.SpecialDays;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarTermHelper;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;

public class DebugInfoRenderer {
    private final Style DEFAULT = Style.EMPTY.withFont(ResourceLocation.withDefaultNamespace("default"));

    private final Minecraft mc;
    private int delay = 0;
    private Holder<Biome> cachedBiome;
    private Holder<Biome> e_cachedBiome;

    public DebugInfoRenderer(Minecraft mc) {
        this.mc = mc;
    }


    public static class InfoList extends ArrayList<Object> {
        public void addHeader(String title) {
            this.add("§6[" + title + "]§r");
        }

        public void addKV(String key, Object value, String color) {
            this.add(String.format("%s: " + color + "%s§r", key, value.toString()));
        }

        public void addEmpty() {
            this.add("");
        }

        public void addDoubleKV(String k1, Object v1, String c1, String k2, Object v2, String c2) {
            this.add(String.format("§f%s: %s%s§r | §f%s: %s%s§r", k1, c1, v1, k2, c2, v2));
        }

        public void addComponent(Component component) {
            this.add(component);
        }
    }

    public void renderStatusBar(GuiGraphics guiGraphics, int screenWidth, int screenHeight, ClientLevel level, LocalPlayer player, String solarDay, long dayTime, double envTemp, int solarTime) {
        boolean showDebug = ClientConfig.Debug.debugInfo.get();
        boolean showSimple = ClientConfig.GUI.simpleSeasonHud.get();

        if (!showDebug && !showSimple) return;

        BlockPos pos = player.blockPosition();
        if (delay <= 0) {
            cachedBiome = level.getBiome(pos);
            e_cachedBiome = MapChecker.getSurfaceBiome(level, pos);
            delay = 20;
        } else {
            delay--;
        }

        InfoList infoLines = new InfoList();

        ISolarTerm currentTerm = SolarTermHelper.get(level, pos);
        MutableComponent termName = currentTerm.getTranslation().copy().withStyle(currentTerm.getColor())
                .append(" (")
                .append(currentTerm.getSeason().getTranslation())
                .append(") ");

        MutableComponent iconAndTerm = SimpleUtil.addSolarIconBefore(currentTerm, termName);

        MutableComponent fullLine = iconAndTerm
                .append(Component.literal(", ").withStyle(DEFAULT).withStyle(ChatFormatting.GRAY))
                .append(Component.translatable("ui.info.eclipticseasons.days", solarDay).withStyle(DEFAULT).withStyle(ChatFormatting.YELLOW));

        infoLines.addComponent(fullLine);

        if (showDebug) {
            infoLines.addEmpty();
            infoLines.addEmpty();
        }

        if (showDebug) {
            infoLines.addHeader("Ecliptic Debug");
            Season.Sub subSeason = EclipticSeasonsApi.getInstance().getSubSeason(level);
            infoLines.addKV("Sub Season", subSeason.getTranslation().getString(), subSeason.getSeason().getColor().toString());
            infoLines.addKV("Month", Component.translatable("debug_info.eclipticseasons.standard_year_month_day",
                    ClientConfig.GUI.showGregorianYear.getAsBoolean() ? EclipticSeasonsApi.getInstance().getGregorianYear(level) : EclipticSeasonsApi.getInstance().getSolarYear(level),
                    EclipticSeasonsApi.getInstance().getGregorianMonth(level).getTranslation().getString(),
                    EclipticSeasonsApi.getInstance().getDayOfMonth(level)
            ).getString(), "§e");
            StringBuilder specialDays = new StringBuilder();
            for (Holder<SpecialDays> specialDay : EclipticSeasonsApi.getInstance().getSpecialDays(level, pos)) {
                specialDays.append(getBiomeName(specialDay, level.registryAccess().registryOrThrow(ESRegistries.SPECIAL_DAYS)));
            }
            if (!specialDays.isEmpty()) {
                infoLines.addKV("SpecialDays", specialDays.toString(), "§g");
            }
            infoLines.addKV("Solar Time", solarTime, "§b");
            infoLines.addKV("Day Time", dayTime, "§e");
            infoLines.addKV("Humidity", String.format("%.2f", EclipticUtil.getHumidityLevelAt(level, pos)), "§9");
            infoLines.addDoubleKV(
                    "Rainfall", String.format("%.2f", EclipticUtil.getDownfallFloat(level, cachedBiome.value(), pos)), "§b",
                    "Temp", String.format("%.2f", EclipticUtil.getTemperatureFloat(level, cachedBiome.value(), pos)), "§a"
            );

            WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level, cachedBiome);
            if (biomeWeather != null) {
                infoLines.addEmpty();
                var agroClimaticZones = level.registryAccess().registry(ESRegistries.AGRO_CLIMATE);
                if (agroClimaticZones.isPresent()) {
                    Holder<AgroClimaticZone> agroClimaticZoneHolder = CropGrowthHandler.getclimateTypeHolder(cachedBiome);
                    infoLines.add("Agro: " + (agroClimaticZoneHolder != null ? (getBiomeName(agroClimaticZoneHolder, agroClimaticZones.get()) + " §2(" + getBiomeId(agroClimaticZoneHolder) + ")§r") : "Unknown"));
                }

                var biomes = level.registryAccess().registry(Registries.BIOME);
                if (biomes.isPresent()) {
                    infoLines.add("Biome: " + getBiomeName(cachedBiome, biomes.get()) + " §2(" + getBiomeId(cachedBiome) + ")§r");
                    infoLines.add("Surface: " + (e_cachedBiome != null ? (getBiomeName(e_cachedBiome, biomes.get()) + " §2(" + getBiomeId(e_cachedBiome) + ")§r") : "Unknown"));
                }
                infoLines.add(String.format("R/C/T Time: §e%d§r / §e%d§r / §e%d§r",
                        level.isRaining() ? 10000 : 0, level.isRaining() ? 0 : 10000, level.isThundering() ? 10000 : 0));
                ISnowTerm snowTerm = SolarTerm.getSnowTerm(e_cachedBiome != null ? e_cachedBiome.value() : biomeWeather.biomeHolder.value(), false, EclipticUtil.getSnowTempChange(level));
                SolarTerm start = snowTerm.getStart();
                SolarTerm end = snowTerm.getEnd();
                infoLines.addComponent(Component.literal("Snow Term%s: ".formatted(e_cachedBiome == cachedBiome ? "" : " (Surface) "))
                        .append(Component.translatable("debug_info.eclipticseasons.snow_term",
                                start.getTranslation().withStyle(start.getColor()),
                                end.getTranslation().withStyle(end.getColor()),
                                !start.isValid() || start.ordinal() <= end.ordinal() ? Component.empty() :
                                        Component.translatable("debug_info.eclipticseasons.snow_term.second_year").withStyle(end.getColor()))));
                infoLines.addKV("Snow Depth", biomeWeather.getSnowDepth(), "§f");
                infoLines.addKV("Map Height", MapChecker.getHeight(level, pos), "");

                infoLines.addEmpty();
                // WeatherMode weatherMode = EclipticUtil.getWeatherMode(level);
                // if (!EclipticUtil.hasLocalWeather(level)) {
                //     infoLines.addKV("Mode", "Vanilla Sync", "§c");
                // } else
                {
                    Holder<Biome> owner = WeatherManager.getOwner(ClientCon.getUseLevel(), biomeWeather.biomeHolder);
                    Holder<Biome> targetBiome = (owner != null) ? owner : e_cachedBiome;
                    WeatherManager.BiomeWeather weatherTarget = WeatherManager.getBiomeWeather(level, targetBiome);

                    if (weatherTarget != null) {
                        // infoLines.addKV("Biome Rain", weatherTarget.getBiomeRain().name(), "§f");
                        infoLines.add("Biome Rain: §2" + weatherTarget.getBiomeRain().name() + " §3(" + (weatherTarget.getBiomeRain().ordinal() + 1) + ")§r");
                        if (weatherTarget.effect != null && weatherTarget.effect.getKey() != null)
                            infoLines.add("Weather Effect: §2" + weatherTarget.effect.getKey().location());
                        if (owner != null && !owner.equals(e_cachedBiome) && biomes.isPresent()) {
                            infoLines.addKV("Owner", getBiomeName(owner, biomes.get()), "§e");
                        }

                        float downfall = EclipticUtil.getDownfallFloatConstant(ClientCon.nowSolarTerm, targetBiome.value(), false);
                        float rainChance = weatherTarget.getBiomeRain().getRainChance()
                                * Math.max(0.01f, downfall)
                                * (CommonConfig.Weather.rainChanceMultiplier.get() / 100f);
                        infoLines.addKV("Rain Chance", String.format("%.2f%%", rainChance * 100), "§b");

                        if (level.isRaining()) {
                            int size = WeatherManager.getWeatherTickFactor(level);
                            float thunderChance = weatherTarget.getBiomeRain().getThunderChance()
                                    * (CommonConfig.Weather.thunderChanceMultiplier.get() / 100f)
                                    * size / 3000f;
                            infoLines.addKV("Thunder Chance", String.format("%.2f%%", thunderChance * 100), "§e");
                        } else {
                            infoLines.addKV("Thunder", "Waiting Rain", "");
                        }
                    }
                }
            }
        }

        renderList(guiGraphics, infoLines);
    }

    private void renderList(GuiGraphics guiGraphics, InfoList lines) {
        int x = 6;
        int y = 6;
        int bgPadding = 2;

        for (int i = 0; i < lines.size(); i++) {
            Object obj = lines.get(i);
            if (obj instanceof String s && s.isEmpty()) {
                y += 5;
                continue;
            }

            Component lineComponent = (obj instanceof Component c) ? c : Component.literal(obj.toString());

            int textWidth = mc.font.width(lineComponent);
            int textHeight = mc.font.lineHeight;

            guiGraphics.pose().pushPose();
            if (!(obj instanceof Component) || i > 5) {
                guiGraphics.fill(x - bgPadding, y - bgPadding + 1, x + textWidth + bgPadding, y + textHeight, 0x90000000);
            } else {
                guiGraphics.pose().scale(0.9f, 0.9f, 0.9f);
            }
            guiGraphics.drawString(mc.font, lineComponent.getVisualOrderText(), x, y, 0xFFFFFFFF, true);

            guiGraphics.pose().popPose();

            y += textHeight + 2;
        }
    }

    private <T> String getBiomeName(Holder<T> biomeHolder, Registry<T> type) {
        // if (true) {
        //     return Util.makeDescriptionId(type, biomeHolder.value());
        // }
        String[] split = type.key().location().getPath().split("/");
        return Component.translatable(Util.makeDescriptionId(split[split.length - 1], biomeHolder.unwrapKey().map(ResourceKey::location).orElse(null))).getString();
    }

    private <T> String getBiomeId(Holder<T> biomeHolder) {
        return biomeHolder.unwrapKey().map(key -> key.location().toString()).orElse("null");
    }
}