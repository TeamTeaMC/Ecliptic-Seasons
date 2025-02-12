package com.teamtea.eclipticseasons.api.constant.biome;


import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum Humidity implements ITranslatable {
    ARID(ChatFormatting.RED, 0.9F),
    DRY(ChatFormatting.GOLD, 0.95F),
    AVERAGE(ChatFormatting.GREEN, 1.0F),
    MOIST(ChatFormatting.BLUE, 1.1F),
    HUMID(ChatFormatting.DARK_BLUE, 1.2F);

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

    @Override
    public Component getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.humidity." + getName()).withStyle(color);
    }

    public ChatFormatting getColor() {
        return color;
    }

    public float getCoefficient() {
        return tempCoefficient;
    }

    private static final Humidity[] humidity = Humidity.values();

    public static Humidity[] collectValues() {
        return humidity;
    }

    public Humidity above(int levelAttach) {
        int ordinal = ordinal();
        if (ordinal + levelAttach < 0) {
            return ARID;
        }
        if (ordinal + levelAttach >= collectValues().length) {
            return HUMID;
        }
        return collectValues()[ordinal + 1];
    }

    @Deprecated
    public static Humidity getHumid(Rainfall rainfall, Temperature temperature) {
        int rOrder = rainfall.ordinal();
        int tOrder = temperature.ordinal();
        int level = Math.max(0, rOrder - Math.abs(rOrder - tOrder) / 2);
        return Humidity.values()[level];
    }

    @Deprecated
    public static Humidity getHumid(float rainfall, float temperature) {
        return Humidity.getHumid(Rainfall.getRainfallLevel(rainfall), Temperature.getTemperatureLevel(temperature));
    }

}
