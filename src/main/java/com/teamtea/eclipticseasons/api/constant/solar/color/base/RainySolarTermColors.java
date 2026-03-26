package com.teamtea.eclipticseasons.api.constant.solar.color.base;

import com.teamtea.eclipticseasons.client.util.ColorHelper;
import net.minecraft.world.level.FoliageColor;


public enum RainySolarTermColors implements SolarTermColor {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(),
    RAIN_WATER(),
    INSECTS_AWAKENING(),
    SPRING_EQUINOX(),
    FRESH_GREEN(0x7fb80e, 0.16F),
    GRAIN_RAIN(0x7fb80e, 0.32F),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0x7fb80e, 0.48F),
    LESSER_FULLNESS(0x7fb80e, 0.64F, 0x7fb80e, 0.1F),
    GRAIN_IN_EAR(0x7fb80e, 0.8F, 0x7fb80e, 0.2F),
    SUMMER_SOLSTICE(0x7fb80e, 0.8F, 0x7fb80e, 0.3F),
    LESSER_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.8F, 0xd1923f, 0.2F), 0.8F, 0x7fb80e, 0.4F),
    GREATER_HEAT(0x7fb80e, 0.5F, 0xffd400, 0.25F),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0x7fb80e, 0.5F, 0xffd400, 0.5F),
    END_OF_HEAT(0x7fb80e, 0.5F, 0xffd400, 0.75F),
    WHITE_DEW(0x7fb80e, 0.5F, 0xffd400, 1.0F),
    AUTUMNAL_EQUINOX(0x7fb80e, 0.4F, 0xffd400, 1.0F),
    COLD_DEW(0x7fb80e, 0.3F, 0xffd400, 1.0F),
    FIRST_FROST(0x7fb80e, 0.2F, ColorHelper.simplyMixColor(0xffd400, 0.8F, 0xc1a173, 0.2F), 1.0F),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(0x7fb80e, 0.1F, ColorHelper.simplyMixColor(0xffd400, 0.6F, 0xc1a173, 0.4F), 1.0F),
    LIGHT_SNOW(),
    HEAVY_SNOW(),
    WINTER_SOLSTICE(),
    LESSER_COLD(),
    GREATER_COLD(),

    NONE();


    private final int rainyColor;
    private final float rainyMix;
    private final int birchColor;

    RainySolarTermColors(int rainyColorIn, float rainyMixIn, int birchColorIn, float birchAlphaIn) {
        this.rainyColor = rainyColorIn;
        this.rainyMix = rainyMixIn;
        this.birchColor = ColorHelper.simplyMixColor(birchColorIn, birchAlphaIn, FoliageColor.FOLIAGE_DEFAULT, 1.0F - birchAlphaIn);
    }

    RainySolarTermColors(int rainyColorIn, float rainyMixIn) {
        this.rainyColor = rainyColorIn;
        this.rainyMix = rainyMixIn;
        this.birchColor = FoliageColor.FOLIAGE_DEFAULT;
    }

    RainySolarTermColors() {

        this.rainyColor = 0;
        this.rainyMix = 0;
        this.birchColor = FoliageColor.FOLIAGE_DEFAULT;
    }


    private static final RainySolarTermColors[] values = RainySolarTermColors.values();

    public static RainySolarTermColors[] collectValues() {
        return values;
    }

    public static RainySolarTermColors get(int index) {
        return collectValues()[index];
    }

    @Override
    public int getGrassColor() {
        return rainyColor;
    }

    @Override
    public float getMix() {
        return rainyMix;
    }

    @Override
    public int getLeaveColor() {
        return birchColor;
    }


}
