package com.teamtea.eclipticseasons.api.constant.biome;


import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum Humidity {
    ARID(ChatFormatting.RED, 0.9F),
    DRY(ChatFormatting.GOLD, 0.95F),
    AVERAGE(ChatFormatting.WHITE, 1.0F),
    MOIST(ChatFormatting.BLUE, 1.1F),
    HUMID(ChatFormatting.DARK_GREEN, 1.2F);

    private final ChatFormatting color;
    private final float tempCoefficient;

    Humidity(ChatFormatting color, float tempCoefficient) {
        this.color = color;
        this.tempCoefficient = tempCoefficient;
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    public String getName() {
        return this.toString().toLowerCase();
    }

    public Component getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.humidity." + getName()).withStyle(color);
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
