package com.teamtea.eclipticseasons.api.constant.solar.color.base.seasonal;

import com.teamtea.eclipticseasons.api.constant.solar.color.base.SolarTermColor;
import com.teamtea.eclipticseasons.client.util.ColorHelper;
import net.minecraft.world.level.FoliageColor;


public enum ColdSolarTermColors implements SolarTermColor {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0xc1a173, 0.2F, 0xc1a173, 0.2F),
    RAIN_WATER(0xc1a173, 0.15F, 0xc1a173, 0.15F),
    INSECTS_AWAKENING(0, 0),
    SPRING_EQUINOX(0, 0),
    FRESH_GREEN(0xc1a173, 0.1F, 0xc1a173, 0.1F),
    GRAIN_RAIN(0xc1a173, 0.16F, 0xc1a173, 0.16F),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0, 0),
    LESSER_FULLNESS(0x7fb80e, 0.16F),
    GRAIN_IN_EAR(0x7fb80e, 0.4F, 0x7fb80e, 0.2F),
    SUMMER_SOLSTICE(0x7fb80e, 0.4F, 0x7fb80e, 0.25F),
    LESSER_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.8F, 0xd1923f, 0.2F), 0.4F, 0x7fb80e, 0.4F),
    GREATER_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.4F, 0xd1923f, 0.6F), 0.4F, 0xffd400, 0.5F),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0xd1923f, 0.4F, 0xffd400, 0.5F),
    END_OF_HEAT(ColorHelper.simplyMixColor(0xd1923f, 0.8F, 0xc1a173, 0.2F), 0.4F, 0xffd400, 0.5F),
    WHITE_DEW(ColorHelper.simplyMixColor(0xd1923f, 0.6F, 0xc1a173, 0.4F), 0.4F, ColorHelper.simplyMixColor(0xffd400, 0.8F, 0xc1a173, 0.2F), 0.5F),
    AUTUMNAL_EQUINOX(ColorHelper.simplyMixColor(0xd1923f, 0.5F, 0xc1a173, 0.5F), 0.4F, ColorHelper.simplyMixColor(0xffd400, 0.6F, 0xc1a173, 0.4F), 0.5F),
    COLD_DEW(ColorHelper.simplyMixColor(0xd1923f, 0.4F, 0xc1a173, 0.6F), 0.4F, ColorHelper.simplyMixColor(0xffd400, 0.58F, 0xc1a173, 0.42F), 0.5F),
    FIRST_FROST(ColorHelper.simplyMixColor(0xd1923f, 0.35F, 0xc1a173, 0.65F), 0.4F, ColorHelper.simplyMixColor(0xffd400, 0.55F, 0xc1a173, 0.45F), 0.5F),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(ColorHelper.simplyMixColor(0xd1923f, 0.32F, 0xc1a173, 0.68F), 0.4F, ColorHelper.simplyMixColor(0xffd400, 0.5F, 0xc1a173, 0.5F), 0.5F),
    LIGHT_SNOW(0xc1a173, 0.4F, 0xc1a173, 0.4F),
    HEAVY_SNOW(0xc1a173, 0.35F, 0xc1a173, 0.35F),
    WINTER_SOLSTICE(0xc1a173, 0.32F, 0xc1a173, 0.32F),
    LESSER_COLD(0xc1a173, 0.3F, 0xc1a173, 0.3F),
    GREATER_COLD(0xc1a173, 0.25F, 0xc1a173, 0.25F),

    NONE(-1,0,-1,0);

    private final int temperateColor;
    private final float temperateMix;
    private final int birchColor;


    ColdSolarTermColors(int temperateColorIn, float temperateMixIn, int birchColorIn, float birchAlphaIn) {
        this.temperateColor = temperateColorIn;
        this.temperateMix = temperateMixIn;
        this.birchColor = ColorHelper.simplyMixColor(birchColorIn, birchAlphaIn,FoliageColor.FOLIAGE_DEFAULT, 1.0F - birchAlphaIn);
    }


    ColdSolarTermColors(int temperateColorIn, float temperateMix) {
        this.temperateColor = temperateColorIn;
        this.temperateMix = temperateMix;
        // this.birchColor = FoliageColor.getBirchColor();
        this.birchColor =FoliageColor.FOLIAGE_DEFAULT;
    }


    public static ColdSolarTermColors get(int index) {
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

    private static final ColdSolarTermColors[] values = ColdSolarTermColors.values();

    public static ColdSolarTermColors[] collectValues() {
        return values;
    }
}
