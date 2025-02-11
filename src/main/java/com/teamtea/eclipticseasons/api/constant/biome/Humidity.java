package com.teamtea.eclipticseasons.api.constant.biome;



import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public enum Humidity {
    ARID(TextFormatting.RED, 0.9F),
    DRY(TextFormatting.GOLD, 0.95F),
    AVERAGE(TextFormatting.WHITE, 1.0F),
    MOIST(TextFormatting.BLUE, 1.1F),
    HUMID(TextFormatting.DARK_GREEN, 1.2F);

    private final TextFormatting color;
    private final float tempCoefficient;

    Humidity(TextFormatting color, float tempCoefficient) {
        this.color = color;
        this.tempCoefficient = tempCoefficient;
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    public String getName() {
        return this.toString().toLowerCase();
    }

    public ITextComponent getTranslation() {
        return new TranslationTextComponent("info.eclipticseasons.environment.humidity." + getName()).withStyle(color);
    }

    public float getCoefficient() {
        return tempCoefficient;
    }

    public static Humidity getHumid(Rainfall rainfall, Temperature temperature) {
        int rOrder = rainfall.ordinal();
        int tOrder = temperature.ordinal();
        int level = Math.max(0, rOrder - Math.abs(rOrder - tOrder) / 2);
        return Humidity.values()[level];
    }

    public static Humidity getHumid(float rainfall, float temperature) {
        return Humidity.getHumid(Rainfall.getRainfallLevel(rainfall), Temperature.getTemperatureLevel(temperature));
    }
}
