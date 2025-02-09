package com.teamtea.eclipticseasons.api.constant.biome;


import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

public enum Humidity {
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

    public Component getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.humidity." + getName()).withStyle(color);
    }

    public float getCoefficient() {
        return tempCoefficient;
    }

    private static final Humidity[] humidity = Humidity.values();

    public static Humidity[] collectValues() {
        return humidity;
    }

    public Humidity above() {
        if (this != HUMID) {
            return collectValues()[ordinal() + 1];
        }
        return HUMID;
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
        float t = biomeHolder.value().getModifiedClimateSettings().temperature() + solarTerm.getTemperatureChange();
        BiomeRain biomeRain = solarTerm.getBiomeRain(biomeHolder);
        float r = (biomeHolder.value().getModifiedClimateSettings().downfall() * 1.5f + biomeRain.getRainChane() * 0.5f) / 2f;
        if (biomeHolder.is(BiomeTags.IS_SAVANNA) && biomeRain.getRainChane() > 0) {
            r += 0.15f;
        }
        // float r = biomeHolder.value().getModifiedClimateSettings().downfall();
        // if(biomeHolder.is(BiomeTags.IS_SAVANNA)&&biomeRain.getRainChane()>0){
        //     r+=0.2f;
        // }
        // r*=(1 + biomeRain.getRainChane());
        Humidity h = Humidity.getHumid(r, t);
        return h;
    }
}
