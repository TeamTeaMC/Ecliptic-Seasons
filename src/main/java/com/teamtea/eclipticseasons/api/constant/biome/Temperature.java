package com.teamtea.eclipticseasons.api.constant.biome;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public enum Temperature  implements ITranslatable {
    FREEZING(Float.NEGATIVE_INFINITY, 0.15F),
    COLD(0.15F, 0.4F),
    COOL(0.4F, 0.65F),
    WARM(0.65F, 0.9F),
    HOT(0.9F, 1.25F),
    HEAT(1.25F, Float.POSITIVE_INFINITY);

    private float min;
    private float max;

    Temperature(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public boolean isInTemperature(float temp) {
        return min < temp && temp <= max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getWidth() {
        return max - min;
    }

    private static final Temperature[] temperature = Temperature.values();

    public static Temperature[] collectValues() {
        return temperature;
    }

    @Override
    public Component getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.temperature." + getName());
    }

    public static Temperature getTemperatureLevel(float temp) {
        for (Temperature t : Temperature.collectValues()) {
            if (t.isInTemperature(temp)) {
                return t;
            }
        }
        return Temperature.FREEZING;
    }
}
