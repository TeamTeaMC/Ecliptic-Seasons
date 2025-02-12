package com.teamtea.eclipticseasons.api.constant.biome;


import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum Rainfall implements ITranslatable {
    RARE(Float.NEGATIVE_INFINITY, 0.1F),
    SCARCE(0.1F, 0.3F),
    MODERATE(0.3F, 0.6F),
    ADEQUATE(0.6F, 0.8F),
    ABUNDANT(0.8F, Float.POSITIVE_INFINITY);

    private float min;
    private float max;

    Rainfall(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase();
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

    @Override
    public ITextComponent getTranslation() {
        return new TranslationTextComponent("info.eclipticseasons.environment.rainfall." + getName());
    }

    private static final Rainfall[] rainfall = Rainfall.values();

    public static Rainfall[] collectValues() {
        return rainfall;
    }

    public static Rainfall getRainfallLevel(float rainfall) {
        for (Rainfall r : Rainfall.values()) {
            if (r.isInRainfall(rainfall)) {
                return r;
            }
        }
        return Rainfall.RARE;
    }
}
