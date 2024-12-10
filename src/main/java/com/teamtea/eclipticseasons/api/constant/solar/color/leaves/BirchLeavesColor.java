package com.teamtea.eclipticseasons.api.constant.solar.color.leaves;


import com.teamtea.eclipticseasons.api.constant.solar.color.base.TemperateSolarTermColors;
import com.teamtea.eclipticseasons.client.util.ColorHelper;

public enum BirchLeavesColor implements LeaveColor {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0x43b244, 0),
    RAIN_WATER(0x43b244, 0.1f),
    INSECTS_AWAKENING(0x43b244, 0.2f),
    SPRING_EQUINOX(0x43b244, 0.25f),
    FRESH_GREEN(0x43b244, 0.3f),
    GRAIN_RAIN(0x43b244, 0.4f),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0xd2d97a, 0.4f),
    LESSER_FULLNESS(0xd2d97a, 0.5f),
    GRAIN_IN_EAR(0xd2d97a, 0.6f),
    SUMMER_SOLSTICE(0xd2d97a, 0.65f),
    LESSER_HEAT(0xd2d97a, 0.55f),
    GREATER_HEAT(0xd2d97a, 0.4f),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0xfba414, 0.42f),
    END_OF_HEAT(0xfba414, 0.7f),
    WHITE_DEW(0xfba414, 0.9f),
    AUTUMNAL_EQUINOX(0xfba414, 0.95f),
    COLD_DEW(ColorHelper.simplyMixColor(0xf26b1f,0.5f,0xfba414,0.5f), 0.8f),
    FIRST_FROST(0xf26b1f, 0.9f),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(ColorHelper.simplyMixColor(0xf26b1f,0.7f,0xad9e5f,0.3f), 0.7f),
    LIGHT_SNOW(ColorHelper.simplyMixColor(0xf26b1f,0.5f,0xad9e5f,0.5f), 0.4f),
    HEAVY_SNOW(0xad9e5f, 0.8f),
    WINTER_SOLSTICE(0xad9e5f, 0.7f),
    LESSER_COLD(0xad9e5f, 0.6f),
    GREATER_COLD(0xad9e5f, 0.3f),


    NONE(0, 0);


    private final int color;
    private final float mix;

    BirchLeavesColor(int color, float mix) {
        this.color=color;
        this.mix=mix;
    }


    @Override
    public int getColor() {
        return color;
        // return 0xec9bad;
    }

    @Override
    public float getMix() {
        return mix;
        // return 1;
    }

    private static final BirchLeavesColor[] values = BirchLeavesColor.values();
    public static BirchLeavesColor[] collectValues() {
        return values;
    }
}
