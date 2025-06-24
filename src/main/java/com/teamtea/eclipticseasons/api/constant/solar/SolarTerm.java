package com.teamtea.eclipticseasons.api.constant.solar;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.*;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.*;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.ITranslatableWithPlaceholder;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SolarTerm implements ITranslatableWithPlaceholder {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(-0.25F, 10500),    // 立春
    RAIN_WATER(-0.15F, 11000),             // 雨水
    INSECTS_AWAKENING(-0.1F, 11500),       // 惊蛰
    SPRING_EQUINOX(0, 12000),              // 春分
    FRESH_GREEN(0, 12500),                 // 清明
    GRAIN_RAIN(0.05F, 13000),              // 谷雨

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0.1F, 13500),      // 立夏
    LESSER_FULLNESS(0.15F, 14000),         // 小满
    GRAIN_IN_EAR(0.15F, 14500),            // 芒种
    SUMMER_SOLSTICE(0.2F, 15000),          // 夏至
    LESSER_HEAT(0.2F, 14500),              // 小暑
    GREATER_HEAT(0.25F, 14000),            // 大暑

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0.15F, 13500),     // 立秋
    END_OF_HEAT(0.1F, 13000),              // 处暑
    WHITE_DEW(0.05F, 12500),               // 白露
    AUTUMNAL_EQUINOX(0, 12000),            // 秋分
    COLD_DEW(-0.1F, 11500),                // 寒露
    FIRST_FROST(-0.2F, 11000),             // 霜降

    // Winter Solar Terms
    BEGINNING_OF_WINTER(-0.3F, 10500),     // 立冬
    LIGHT_SNOW(-0.35F, 10000),             // 小雪
    HEAVY_SNOW(-0.35F, 9500),              // 大雪
    WINTER_SOLSTICE(-0.4F, 9000),          // 冬至
    LESSER_COLD(-0.45F, 9500),             // 小寒
    GREATER_COLD(-0.4F, 10000),            // 大寒

    NONE(0.0F, 12000);

    private final float temperature;
    private final int dayTime;

    SolarTerm(float temperature, int dayTime) {
        this.temperature = temperature;
        this.dayTime = dayTime;
    }

    public String getName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public MutableComponent getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.solar_term." + getName());
    }

    public MutableComponent getAlternationText() {
        return Component.translatable("info.eclipticseasons.environment.solar_term.alternation." + getName()).withStyle(getSeason().getColor());
    }

    private static final SolarTerm[] solarTerms = SolarTerm.values();

    public static SolarTerm[] collectValues() {
        return solarTerms;
    }

    public static SolarTerm get(int index) {
        return collectValues()[index];
    }

    public static ResourceLocation getFont() {
        return EclipticSeasons.rl("solar_icons");
    }

    public String getFontLabel() {
        // return new String(new byte[]{(byte) (ordinal() + 97)});
        return String.valueOf((char) (ordinal() + 97));
    }

    public static ResourceLocation getFullIcon() {
        return EclipticSeasons.rl("font/" + "seasons_icons");
    }

    public static ResourceLocation getFontIcon() {
        return EclipticSeasons.rl("font/" + "seasons_icons_font");
    }

    public SimplePair<Integer, Integer> getIconPosition() {
        return SimplePair.of(this.ordinal() % 6, this.ordinal() / 6);
    }

    public RainySolarTermColors getColorInfo() {
        return RainySolarTermColors.collectValues()[this.ordinal()];
    }

    public SolarTermColor getSolarTermColor(TagKey<Biome> biomeTagKey) {
        if (biomeTagKey.equals(ClimateTypeBiomeTags.RAINLESS)) {
            return NoneSolarTermColors.get(this.ordinal());
        } else if (biomeTagKey.equals(ClimateTypeBiomeTags.ARID)) {
            return NoneSolarTermColors.get(this.ordinal());
        } else if (biomeTagKey.equals(ClimateTypeBiomeTags.DROUGHTY)) {
            return SlightlySolarTermColors.get(this.ordinal());
        } else if (biomeTagKey.equals(ClimateTypeBiomeTags.SOFT)) {
            return SlightlySolarTermColors.get(this.ordinal());
        } else if (biomeTagKey.equals(ClimateTypeBiomeTags.RAINY)) {
            return SlightlySolarTermColors.get(this.ordinal());
        } else if (biomeTagKey.equals(ClimateTypeBiomeTags.MONSOONAL)) {
            return RainySolarTermColors.collectValues()[this.ordinal()];
        } else if (biomeTagKey.equals(ClimateTypeBiomeTags.SEASONAL)) {
            return TemperateSolarTermColors.collectValues()[this.ordinal()];
        } else {
            return NoneSolarTermColors.get(this.ordinal());
        }
    }

    public float getTemperatureChange() {
        return temperature;
    }

    public int getDayTime() {
        if (CommonConfig.isUseDayTimes()) {
            return CommonConfig.getDayTimesForSeason()[ordinal()];
        }
        return getOriginalDayTime();
    }

    public int getOriginalDayTime(){
        return dayTime;
    }

    public Season getSeason() {
        return Season.collectValues()[this.ordinal() / 6];
    }

    @Override
    public @NotNull String getSerializedName() {
        return getName();
    }

    public BiomeRain getBiomeRain(Holder<Biome> biomeHolder) {
        TagKey<Biome> tag = BiomeClimateManager.getTag(biomeHolder.value());
        if (tag == ClimateTypeBiomeTags.RAINLESS)
            return FlatRain.RAINLESS;
        if (tag == ClimateTypeBiomeTags.ARID)
            return FlatRain.ARID;
        if (tag == ClimateTypeBiomeTags.DROUGHTY)
            return FlatRain.DROUGHTY;
        if (tag == ClimateTypeBiomeTags.SOFT)
            return FlatRain.SOFT;
        if (tag == ClimateTypeBiomeTags.RAINY)
            return FlatRain.RAINY;
        if (tag == ClimateTypeBiomeTags.MONSOONAL)
            return MonsoonRain.collectValues()[this.ordinal()];
        return TemperateRain.collectValues()[this.ordinal()];
    }

    public static SnowTerm getSnowTerm(Biome biome) {
        if (biome == null) return SnowTerm.T05;
        // float t = BiomeClimateManager.agent$GetBaseTemperature(biome);
        float t = biome.getModifiedClimateSettings().temperature();

        BiomeClimateSettings biomeClimateSettings = BiomeClimateManager.getBiomeClimateSettings(biome, true);
        t = biomeClimateSettings == BiomeClimateManager.EMPTY ? t : biomeClimateSettings.getTemperature();

        if (t > 0.95 + 0.001f) {
            return SnowTerm.T1;
        } else if (t > 0.8 + 0.001f) {
            return SnowTerm.T08;
        } else if (t > 0.6 + 0.001f) {
            return SnowTerm.T06;
        } else if (t > 0.5 + 0.001f) {
            return SnowTerm.T05;
        } else if (t > 0.4 + 0.001f) {
            return SnowTerm.T04;
        } else if (t > 0.3 + 0.001f) {
            return SnowTerm.T03;
        } else if (t > 0.2 + 0.001f) {
            return SnowTerm.T02;
        } else if (t > 0.15 + 0.001f) {
            return SnowTerm.T015;
        } else if (t > 0.1 + 0.001f) {
            return SnowTerm.T01;
        } else if (t > 0.05 + 0.001f) {
            return SnowTerm.T05;
        } else if (t > 0.01 + 0.001f) {
            return SnowTerm.T001;
        }
        return SnowTerm.T0;
    }

    @Deprecated
    public static SnowTerm getSnowTerm(Biome biome, boolean isServer) {
        if (biome == null) return SnowTerm.T05;
        // float t = BiomeClimateManager.getDefaultTemperature(biome, isServer);
        float t = biome.getModifiedClimateSettings().temperature();

        BiomeClimateSettings biomeClimateSettings = BiomeClimateManager.getBiomeClimateSettings(biome, isServer);
        t = biomeClimateSettings == BiomeClimateManager.EMPTY ? t : biomeClimateSettings.getTemperature();

        if (t > 0.95 + 0.001f) {
            return SnowTerm.T1;
        } else if (t > 0.8 + 0.001f) {
            return SnowTerm.T08;
        } else if (t > 0.6 + 0.001f) {
            return SnowTerm.T06;
        } else if (t > 0.5 + 0.001f) {
            return SnowTerm.T05;
        } else if (t > 0.4 + 0.001f) {
            return SnowTerm.T04;
        } else if (t > 0.3 + 0.001f) {
            return SnowTerm.T03;
        } else if (t > 0.2 + 0.001f) {
            return SnowTerm.T02;
        } else if (t > 0.15 + 0.001f) {
            return SnowTerm.T015;
        } else if (t > 0.1 + 0.001f) {
            return SnowTerm.T01;
        } else if (t > 0.05 + 0.001f) {
            return SnowTerm.T05;
        } else if (t > 0.01 + 0.001f) {
            return SnowTerm.T001;
        }
        return SnowTerm.T0;
    }


    public boolean isInTerms(SolarTerm start, SolarTerm end) {
        if (start == NONE || end == NONE) return false;
        else if (start == end) return this == start;
        else if (start.ordinal() <= end.ordinal()) {
            return start.ordinal() <= this.ordinal() && this.ordinal() <= end.ordinal();
        } else
            return start.ordinal() <= this.ordinal() || this.ordinal() <= end.ordinal();
    }

    @Override
    public boolean isValid() {
        return this != NONE;
    }
}
