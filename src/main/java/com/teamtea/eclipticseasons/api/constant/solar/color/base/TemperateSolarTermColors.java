package com.teamtea.eclipticseasons.api.constant.solar.color.base;

import com.teamtea.eclipticseasons.client.util.ColorHelper;
import net.minecraft.world.level.FoliageColor;


public enum TemperateSolarTermColors  implements SolarTermColor
{
    // Spring Solar Terms
    BEGINNING_OF_SPRING(0xc1a173, 0.32F, 0xc1a173, 0.32F),
    RAIN_WATER(0xc1a173, 0.16F, 0xc1a173, 0.16F),
    INSECTS_AWAKENING(0, 0),
    SPRING_EQUINOX(0, 0),
    FRESH_GREEN(0x7fb80e, 0.16F),
    GRAIN_RAIN(0x7fb80e, 0.32F),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0x7fb80e, 0.48F),
    LESSER_FULLNESS(0x7fb80e, 0.64F, 0x7fb80e, 0.1F),
    GRAIN_IN_EAR(0x7fb80e, 0.8F, 0x7fb80e, 0.2F),
    SUMMER_SOLSTICE(0x7fb80e, 0.8F, 0x7fb80e, 0.3F),
    LESSER_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.8F, 0xd1923f, 0.2F), 0.8F, 0x7fb80e, 0.4F),
    GREATER_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.6F, 0xd1923f, 0.4F), 0.8F,  0xffd400, 0.25F),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(ColorHelper.simplyMixColor(0x7fb80e, 0.4F, 0xd1923f, 0.6F), 0.8F,  0xffd400, 0.5F),
    END_OF_HEAT(ColorHelper.simplyMixColor(0x7fb80e, 0.2F, 0xd1923f, 0.8F), 0.8F, 0xffd400, 0.75F),
    WHITE_DEW(0xd1923f, 0.8F,  0xffd400, 1.0F),
    AUTUMNAL_EQUINOX(0xd1923f, 0.8F,  0xffd400, 1.0F),
    COLD_DEW(ColorHelper.simplyMixColor(0xd1923f, 0.8F, 0xc1a173, 0.2F), 0.8F,  0xffd400, 1.0F),
    FIRST_FROST(ColorHelper.simplyMixColor(0xd1923f, 0.6F, 0xc1a173, 0.4F), 0.8F,  ColorHelper.simplyMixColor(0xffd400, 0.8F, 0xc1a173, 0.2F), 1.0F),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(ColorHelper.simplyMixColor(0xd1923f, 0.4F, 0xc1a173, 0.6F), 0.8F,  ColorHelper.simplyMixColor(0xffd400, 0.6F, 0xc1a173, 0.4F), 1.0F),
    LIGHT_SNOW(ColorHelper.simplyMixColor(0xd1923f, 0.2F, 0xc1a173, 0.8F), 0.8F, ColorHelper.simplyMixColor(0xffd400, 0.4F, 0xc1a173, 0.6F), 1.0F),
    HEAVY_SNOW(0xc1a173, 0.8F, ColorHelper.simplyMixColor(0xffd400, 0.2F, 0xc1a173, 0.8F), 1.0F),
    WINTER_SOLSTICE(0xc1a173, 0.8F, 0xc1a173, 0.8F),
    LESSER_COLD(0xc1a173, 0.64F, 0xc1a173, 0.64F),
    GREATER_COLD(0xc1a173, 0.48F, 0xc1a173, 0.48F),

    NONE(-1,0,-1,0);

    private final int temperateColor;
    private final float temperateMix;
    private final int birchColor;


    TemperateSolarTermColors(int temperateColorIn, float temperateMixIn,int birchColorIn, float birchAlphaIn)
    {
        this.temperateColor = temperateColorIn;
        this.temperateMix = temperateMixIn;
        this.birchColor = ColorHelper.simplyMixColor(birchColorIn, birchAlphaIn,FoliageColor.FOLIAGE_DEFAULT, 1.0F - birchAlphaIn);
    }
    

    TemperateSolarTermColors(int temperateColorIn, float temperateMix)
    {
        this.temperateColor = temperateColorIn;
        this.temperateMix = temperateMix;
        // this.birchColor = FoliageColor.getBirchColor();
        this.birchColor =FoliageColor.FOLIAGE_DEFAULT;
    }
    
    
    public static TemperateSolarTermColors get(int index)
    {
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
    public int getLeaveColor()
    {
        return birchColor;
    }

    private static final TemperateSolarTermColors[] values = TemperateSolarTermColors.values();
    public static TemperateSolarTermColors[] collectValues() {
        return values;
    }
}
