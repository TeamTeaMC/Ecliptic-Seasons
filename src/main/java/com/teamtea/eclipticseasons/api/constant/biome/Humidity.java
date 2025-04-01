package com.teamtea.eclipticseasons.api.constant.biome;


import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

import java.util.Locale;

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
        return this.toString().toLowerCase(Locale.ROOT);
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

    @Deprecated(forRemoval = true, since = "0.11")
    public Humidity above(int levelAttach) {
        return cycle(levelAttach);
    }

    public Humidity cycle(int levelAttach) {
        int ordinal = ordinal();
        if (ordinal + levelAttach < 0) {
            return ARID;
        }
        if (ordinal + levelAttach >= collectValues().length) {
            return HUMID;
        }
        return collectValues()[ordinal + levelAttach];
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

    public static Humidity getHumid(SolarTerm solarTerm, Holder<Biome> biomeHolder) {
        Biome biome = biomeHolder.value();
        boolean ignore = true;
        float t = EclipticUtil.getTemperatureFloatConstant(solarTerm, biome, ignore);
        BiomeRain biomeRain = solarTerm.getBiomeRain(biomeHolder);
        float r = (EclipticUtil.getDownfallFloatConstant(solarTerm, biome, ignore) * 1.5f + biomeRain.getRainChane() * 0.5f) / 2f;
        Humidity h = Humidity.getHumid(r, t);
        return h;
    }
}
