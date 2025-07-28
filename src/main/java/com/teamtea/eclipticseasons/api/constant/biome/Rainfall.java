package com.teamtea.eclipticseasons.api.constant.biome;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.Locale;

public enum Rainfall implements ITranslatable {
    RARE(Float.NEGATIVE_INFINITY, 0.1F, 0F, 0.1F),
    SCARCE(0.1F, 0.3F),
    MODERATE(0.3F, 0.6F),
    ADEQUATE(0.6F, 0.8F),
    ABUNDANT(0.8F, Float.POSITIVE_INFINITY, 0.8F, 1F);

    private float min;
    private float max;
    private float limitMin;
    private float limitMax;

    Rainfall(float min, float max) {
        this.min = min;
        this.max = max;
        this.limitMin = min;
        this.limitMax = max;
    }

    Rainfall(float min, float max, float limitMin, float limitMax) {
        this.min = min;
        this.max = max;
        this.limitMin = limitMin;
        this.limitMax = limitMax;
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public boolean isInRainfall(float rainfall) {
        return min < rainfall && rainfall <= max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getLimitedMin() {
        return limitMin;
    }

    public float getLimitedMax() {
        return limitMax;
    }

    @Override
    public Component getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.rainfall." + getName());
    }

    private static final Rainfall[] rainfall = Rainfall.values();

    public static Rainfall[] collectValues() {
        return rainfall;
    }


    public static Rainfall getRainfallLevel(float rainfall) {
        for (Rainfall r : Rainfall.collectValues()) {
            if (r.isInRainfall(rainfall)) {
                return r;
            }
        }
        return Rainfall.RARE;
    }

    public static float getRainfall(float rainfall) {
        for (Rainfall r : Rainfall.collectValues()) {
            if (r.isInRainfall(rainfall)) {
                return Mth.clamp((rainfall - r.getLimitedMin()) / (r.getLimitedMax() - r.getLimitedMin()), 0, 1)
                        * 1 + r.ordinal();
            }
        }
        return 0f;
    }
}
