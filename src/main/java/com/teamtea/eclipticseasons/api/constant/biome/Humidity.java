package com.teamtea.eclipticseasons.api.constant.biome;


import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;
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

    @Deprecated(forRemoval = true, since = "0.12")
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

    public static Humidity getHumid(float humid) {
        return Humidity.collectValues()[Mth.floor(Mth.clamp(humid, 0, Humidity.collectValues().length - 1))];
    }

    @Deprecated
    public static Humidity getHumid(Rainfall rainfall, Temperature temperature) {
        int rOrder = rainfall.ordinal();
        int tOrder = temperature.ordinal();
        int level = Math.max(0, rOrder - Math.abs(rOrder - tOrder) / 2);
        return Humidity.collectValues()[level];
    }

    public static Humidity getHumid(float rainfall, float temperature) {
        if (CommonConfig.isCropHumidityTransition())
            return getHumid(getFloatHumidLevel(rainfall, temperature));
        return Humidity.getHumid(Rainfall.getRainfallLevel(rainfall), Temperature.getTemperatureLevel(temperature));
    }

    // public static Humidity getHumid(SolarTerm solarTerm, Holder<Biome> biomeHolder) {
    //     Biome biome = biomeHolder.value();
    //     boolean ignore = true;
    //     float t = EclipticUtil.getTemperatureFloatConstant(solarTerm, biome, ignore);
    //     BiomeRain biomeRain = solarTerm.getBiomeRain(biomeHolder);
    //     float r = (EclipticUtil.getDownfallFloatConstant(solarTerm, biome, ignore) * 1.5f + biomeRain.getRainChance() * 0.5f) / 2f;
    //     return Humidity.getHumid(r, t);
    // }

    public static float getFloatHumidLevel(float rainfall, float temperature) {
        if (!CommonConfig.isCropHumidityTransition())
            return getHumid(rainfall, temperature).ordinal();
        float t = Temperature.getTemperature(temperature);
        float r = Rainfall.getRainfall(rainfall);
        float rNorm = Mth.clamp(r / Rainfall.collectValues().length, 0f, 1f);
        float tNorm = Mth.clamp(t / Temperature.collectValues().length, 0f, 1f);
        return Math.max(0, rNorm - Math.abs(rNorm - tNorm) / 2f) * Humidity.collectValues().length;
    }

    public static Environment getEnvironment(float humidLevel) {
        if (!CommonConfig.isCropHumidityTransition())
            return Environment.collectValues()[getHumid(humidLevel).ordinal()];
        if (humidLevel < 0.75f)
            return Environment.ARID;
        if (humidLevel > Humidity.collectValues().length - 0.75f)
            return Environment.HUMID;
        float v = humidLevel % 1;
        int original = Mth.floor(humidLevel);
        Humidity now = Humidity.collectValues()[original];
        List<Composition> compositions = new ArrayList<>();
        if (v < 0.25f) {
            float v1 = v * 4;
            compositions.add(new Composition(now, 0.5f + 0.5f * v1));
            compositions.add(new Composition(now.cycle(-1), 0.5f - 0.5f * v1));
        } else if (v <= 0.75f) {
            compositions.add(new Composition(now, 1));
            return Environment.collectValues()[original];
        } else {
            float v1 = (v - 0.75f) * 4;
            compositions.add(new Composition(now, 1 - 0.5f * v1));
            compositions.add(new Composition(now.cycle(1), 0.5f * v1));
        }
        return new Environment(now, compositions);
    }

    public record Environment(Humidity base, List<Composition> compositions) {
        public static final Environment ARID = new Environment(Humidity.ARID, List.of(new Composition(Humidity.ARID, 1f)));
        public static final Environment DRY = new Environment(Humidity.DRY, List.of(new Composition(Humidity.DRY, 1f)));
        public static final Environment AVERAGE = new Environment(Humidity.AVERAGE, List.of(new Composition(Humidity.AVERAGE, 1f)));
        public static final Environment MOIST = new Environment(Humidity.MOIST, List.of(new Composition(Humidity.MOIST, 1f)));
        public static final Environment HUMID = new Environment(Humidity.HUMID, List.of(new Composition(Humidity.HUMID, 1f)));

        private static final Environment[] values = new Environment[]{ARID, DRY, AVERAGE, MOIST, HUMID};

        public static Environment[] collectValues() {
            return values;
        }
    }

    public record Composition(Humidity humidity, float percent) {
    }
}
