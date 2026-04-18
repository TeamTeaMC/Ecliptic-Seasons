package com.teamtea.eclipticseasons.api.constant.solar;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.*;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.seasonal.ColdSolarTermColors;
import com.teamtea.eclipticseasons.api.constant.solar.color.base.seasonal.HotSolarTermColors;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.ITranslatableWithPlaceholder;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.Locale;

public enum SolarTerm implements ITranslatableWithPlaceholder, ISolarTerm {
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

    public SolarTerm getNextSolarTerm() {
        if (!isValid()) return SolarTerm.NONE;
        return SolarTerm.get((this.ordinal() + 1) % 24);
    }

    public SolarTerm getLastSolarTerm() {
        if (!isValid()) return SolarTerm.NONE;
        return SolarTerm.get((this.ordinal() + 24 - 1) % 24);
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public MutableComponent getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.solar_term." + getName());
    }

    @Override
    public MutableComponent getTittleTranslation() {
        return Component.translatable("info.eclipticseasons.environment.solar_term.hint");
    }

    @Override
    public MutableComponent getPatternTranslation() {
        return this == SolarTerm.NONE ? this.getTranslation() : Component.literal(this.getTranslation().getString() +
                (" (%s)".formatted(this.getSeason().getTranslation().getString())));
    }

    @Override
    public MutableComponent getAlternationText() {
        return Component.translatable("info.eclipticseasons.environment.solar_term.alternation." + getName()).withStyle(getSeason().getColor());
    }

    @Override
    public ChatFormatting getColor() {
        return getSeason().getColor();
    }

    @Override
    public Identifier getIconFont() {
        return EclipticSeasons.rl("solar_icons");
    }

    @Override
    public Identifier getIcon() {
        return getFullIcon();
    }

    public static Identifier getFont() {
        return EclipticSeasons.rl("solar_icons");
    }

    @Override
    public String getFontLabel() {
        // return new String(new byte[]{(byte) (ordinal() + 97)});
        return String.valueOf((char) (ordinal() + 97));
    }

    public static Identifier getFullIcon() {
        return EclipticSeasons.rl("font/" + "seasons_icons");
    }

    public static Identifier getFontIcon() {
        return EclipticSeasons.rl("font/" + "seasons_icons_font");
    }

    @Override
    public Pair<Integer, Integer> getIconPosition() {
        return Pair.of(this.ordinal() % 6, this.ordinal() / 6);
    }

    @Override
    public int getIconAtlasSize() {
        return 30;
    }

    @Override
    public int getIconWidth() {
        return 180;
    }

    @Override
    public int getIconHeight() {
        return 120;
    }

    private static final SolarTerm[] solarTerms = SolarTerm.values();

    public static SolarTerm[] collectValues() {
        return solarTerms;
    }

    private static final SolarTerm[] validSolarTerms = Arrays.stream(SolarTerm.values())
            .filter(SolarTerm::isValid).toArray(SolarTerm[]::new);

    public static SolarTerm[] collectValidValues() {
        return validSolarTerms;
    }

    public static SolarTerm get(int index) {
        return collectValues()[index];
    }


    public RainySolarTermColors getColorInfo() {
        return RainySolarTermColors.collectValues()[this.ordinal()];
    }

    public SolarTermColor getSolarTermColor(TagKey<Biome> biomeTagKey) {
        if (biomeTagKey == (ClimateTypeBiomeTags.NONE_COLOR_CHANGE)) {
            return NoneSolarTermColors.get(this.ordinal());
        } else if (biomeTagKey == (ClimateTypeBiomeTags.SLIGHTLY_COLOR_CHANGE)) {
            return SlightlySolarTermColors.get(this.ordinal());
        } else if (biomeTagKey == (ClimateTypeBiomeTags.MONSOONAL_COLOR_CHANGE)) {
            return RainySolarTermColors.collectValues()[this.ordinal()];
        } else if (biomeTagKey == (ClimateTypeBiomeTags.SEASONAL_HOT_COLOR_CHANGE)) {
            return HotSolarTermColors.collectValues()[this.ordinal()];
        } else if (biomeTagKey == (ClimateTypeBiomeTags.SEASONAL_COLD_COLOR_CHANGE)) {
            return ColdSolarTermColors.collectValues()[this.ordinal()];
        } else if (biomeTagKey == (ClimateTypeBiomeTags.SEASONAL_COLOR_CHANGE)) {
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

    public int getOriginalDayTime() {
        return dayTime;
    }

    public Season getSeason() {
        return Season.collectValues()[this.ordinal() / 6];
    }


    public boolean isInTerms(SolarTerm start, SolarTerm end) {
        if (start == NONE || end == NONE) return false;
        else if (start == end)
            return this == start; // es patch: if A is B then use single if B is next to A ,then means all
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
