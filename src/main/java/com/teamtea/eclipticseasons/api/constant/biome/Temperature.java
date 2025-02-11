package com.teamtea.eclipticseasons.api.constant.biome;

import net.minecraft.network.chat.Component;

public enum Temperature
{
    FREEZING(Float.NEGATIVE_INFINITY, 0.15F),
    COLD(0.15F, 0.4F),
    COOL(0.4F, 0.65F),
    WARM(0.65F, 0.9F),
    HOT(0.9F, 1.25F),
    HEAT(1.25F, Float.POSITIVE_INFINITY);

    private float min;
    private float max;

    Temperature(float min, float max)
    {
        this.min = min;
        this.max = max;
    }

    public int getId()
    {
        return this.ordinal() + 1;
    }

    public String getName()
    {
        return this.toString().toLowerCase();
    }

    public boolean isInTemperature(float temp)
    {
        return min < temp && temp <= max;
    }

    public float getMin()
    {
        return min;
    }

    public float getMax()
    {
        return max;
    }

    public float getWidth()
    {
        return max - min;
    }

    public Component getTranslation()
    {
        return Component.translatable("info.eclipticseasons.environment.temperature." + getName());
    }

    public static Temperature getTemperatureLevel(float temp)
    {
        for (Temperature t : Temperature.values())
        {
            if (t.isInTemperature(temp))
            {
                return t;
            }
        }
        return Temperature.FREEZING;
    }
}
