package com.teamtea.eclipticseasons.api.constant.solar.color.leaves;

public enum MangroveLeavesColor implements LeaveColor {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0x5dbe8a, 0.02f),
    RAIN_WATER(0x5dbe8a, 0.08f),
    INSECTS_AWAKENING(0x5dbe8a, 0.15f),
    SPRING_EQUINOX(0x5dbe8a, 0.2f),
    FRESH_GREEN(0x5dbe8a, 0.12f),
    GRAIN_RAIN(0x5dbe8a, 0.1f),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0x207f4c, 0.1f),
    LESSER_FULLNESS(0x207f4c, 0.06f),
    GRAIN_IN_EAR(0, 0),
    SUMMER_SOLSTICE(0x5bae23, 0.1f),
    LESSER_HEAT(0, 0),
    GREATER_HEAT(0, 0),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0x373834, 0.05f),
    END_OF_HEAT(0x373834, 0.1f),
    WHITE_DEW(0x373834, 0.02f),
    AUTUMNAL_EQUINOX(0x373834, 0.05f),
    COLD_DEW(0x373834, 0.08f),
    FIRST_FROST(0x373834, 0.12f),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(0x5b4913, 0.18f),
    LIGHT_SNOW(0x5b4913, 0.2f),
    HEAVY_SNOW(0x5b4913, 0.25f),
    WINTER_SOLSTICE(0x5b4913, 0.15f),
    LESSER_COLD(0x5b4913, 0.08f),
    GREATER_COLD(0x5b4913, 0.05f),


    NONE(0, 0);


    private final int color;
    private final float mix;

    MangroveLeavesColor(int color, float mix) {
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
