package com.teamtea.eclipticseasons.api.constant.solar.color.leaves;

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
    LESSER_FULLNESS(0xd2d97a, 0.4f),
    GRAIN_IN_EAR(0xd2d97a, 0.4f),
    SUMMER_SOLSTICE(0xd2d97a, 0.4f),
    LESSER_HEAT(0xd2d97a, 0.4f),
    GREATER_HEAT(0xd2d97a, 0.4f),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0xfb8b05, 0.42f),
    END_OF_HEAT(0xfb8b05, 0.45f),
    WHITE_DEW(0xfb8b05, 0.48f),
    AUTUMNAL_EQUINOX(0xfb8b05, 0.6f),
    COLD_DEW(0xfb8b05, 0.5f),
    FIRST_FROST(0xfb8b05, 0.4f),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(0xad9e5f, 0.4f),
    LIGHT_SNOW(0xad9e5f, 0.3f),
    HEAVY_SNOW(0xad9e5f, 0.25f),
    WINTER_SOLSTICE(0xad9e5f, 0.15f),
    LESSER_COLD(0xad9e5f, 0.12f),
    GREATER_COLD(0xad9e5f, 0.1f),


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
    }

    @Override
    public float getMix() {
        return mix;
    }
}
