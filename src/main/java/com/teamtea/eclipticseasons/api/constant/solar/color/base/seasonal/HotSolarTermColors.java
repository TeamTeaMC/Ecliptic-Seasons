package com.teamtea.eclipticseasons.api.constant.solar.color.base.seasonal;

import com.teamtea.eclipticseasons.api.constant.solar.color.base.SolarTermColor;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import net.minecraft.world.level.FoliageColor;


public enum HotSolarTermColors implements SolarTermColor {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0xc1a173, 0.1F, 0xc1a173, 0.1F),
    RAIN_WATER(0, 0),
    INSECTS_AWAKENING(0x7fb80e, 0.08F),
    SPRING_EQUINOX(0x7fb80e, 0.16F),
    FRESH_GREEN(0x7fb80e, 0.2F),
    GRAIN_RAIN(0x7fb80e, 0.25F),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0x7fb80e, 0.3F, 0x7fb80e, 0.05F),
    LESSER_FULLNESS(0x7fb80e, 0.35F, 0x7fb80e, 0.1F),
    GRAIN_IN_EAR(0x7fb80e, 0.4F, 0x7fb80e, 0.15F),
    SUMMER_SOLSTICE(0x7fb80e, 0.4F, 0x7fb80e, 0.2F),
    LESSER_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.8F, 0xd1923f, 0.2F), 0.4F, 0x7fb80e, 0.2F),
    GREATER_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.8F, 0xd1923f, 0.2F), 0.45F, 0xffd400, 0.25F),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(ColorHelper.simplyMixColor(0x7fb80e, 0.7F, 0xd1923f, 0.3F), 0.4F, 0xffd400, 0.2F),
    END_OF_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.6F, 0xd1923f, 0.4F), 0.4F, 0xffd400, 0.2F),
    WHITE_DEW(ColorHelper.simplyMixColor(0x7fb80e, 0.58F, 0xd1923f, 0.42F), 0.4F, 0xffd400, 0.2F),
    AUTUMNAL_EQUINOX(ColorHelper.simplyMixColor(0x7fb80e, 0.55F, 0xd1923f, 0.45F), 0.4F, 0xffd400, 0.15F),
    COLD_DEW(ColorHelper.simplyMixColor(0x7fb80e, 0.52F, 0xd1923f, 0.48F), 0.4F, 0xffd400, 0.12F),
    FIRST_FROST(ColorHelper.simplyMixColor(0xd1923f, 0.5F, 0xc1a173, 0.5F), 0.35F, ColorHelper.simplyMixColor(0xffd400, 0.8F, 0xc1a173, 0.2F), 0.2F),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(ColorHelper.simplyMixColor(0xd1923f, 0.5F, 0xc1a173, 0.5F), 0.35F,  ColorHelper.simplyMixColor(0xffd400, 0.6F, 0xc1a173, 0.4F), 0.25F),
    LIGHT_SNOW(ColorHelper.simplyMixColor(0xd1923f, 0.4F, 0xc1a173, 0.6F), 0.35F,  ColorHelper.simplyMixColor(0xffd400, 0.5F, 0xc1a173, 0.5F), 0.35F),
    HEAVY_SNOW(ColorHelper.simplyMixColor(0xd1923f, 0.3F, 0xc1a173, 0.7F), 0.35F, ColorHelper.simplyMixColor(0xffd400, 0.2F, 0xc1a173, 0.8F), 0.35F),
    WINTER_SOLSTICE(0xc1a173, 0.35F, 0xc1a173, 0.35F),
    LESSER_COLD(0xc1a173, 0.35F, 0xc1a173, 0.3F),
    GREATER_COLD(0xc1a173, 0.2F, 0xc1a173, 0.2F),

    NONE(-1,0,-1,0);

    private final int temperateColor;
    private final float temperateMix;
    private final int birchColor;


    HotSolarTermColors(int temperateColorIn, float temperateMixIn, int birchColorIn, float birchAlphaIn) {
        this.temperateColor = temperateColorIn;
        this.temperateMix = temperateMixIn;
        this.birchColor = ColorHelper.simplyMixColor(birchColorIn, birchAlphaIn,FoliageColor.FOLIAGE_DEFAULT, 1.0F - birchAlphaIn);
    }


    HotSolarTermColors(int temperateColorIn, float temperateMix) {
        this.temperateColor = temperateColorIn;
        this.temperateMix = temperateMix;
        // this.birchColor = FoliageColor.getBirchColor();
        this.birchColor =FoliageColor.FOLIAGE_DEFAULT;
    }


    public static HotSolarTermColors get(int index) {
        return collectValues()[index];
    }

    @Override
    public int getGrassColor() {
        return temperateColor;
    }

    @Override
    public float getMix() {
        return temperateMix;
    }

    @Override
    public int getLeaveColor() {
        return birchColor;
    }

    private static final HotSolarTermColors[] values = HotSolarTermColors.values();

    public static HotSolarTermColors[] collectValues() {
        return values;
    }
}
