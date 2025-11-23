package com.teamtea.eclipticseasons.api.constant.crop;


import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CropHumidityInfo {
    private final Humidity min;
    private final Humidity max;

    public CropHumidityInfo(Humidity min, Humidity max) {
        this.min = min;
        this.max = max;
    }

    public CropHumidityInfo(Humidity env) {
        this.min = env;
        this.max = env;
    }

    public boolean isSuitable(Humidity env) {
        return min.getId() <= env.getId() && env.getId() <= max.getId();
    }

    public float getGrowChance(Humidity env) {
        float mul = 1 / (0.25f);
        if (isSuitable(env)) {
            return 1.0F;
        } else if (env.getId() < min.getId()) {
            return Math.max(0, 1.0F / (mul * (min.getId() - env.getId()) * (min.getId() - env.getId())));
        } else {
            return Math.max(0, 1.0F / (mul * (env.getId() - max.getId()) * (env.getId() - max.getId())));
        }
    }

    public List<Component> getTooltip() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("info.eclipticseasons.environment.humidity").withStyle(ChatFormatting.GRAY));
        if (min != max) {
            list.add(((MutableComponent) min.getTranslation()).append(Component.literal(" - ").withStyle(ChatFormatting.GRAY)).append(max.getTranslation()));
        } else {
            list.add(min.getTranslation());
        }
        return list;
    }

    public static List<Component> getTooltip(Humidity min, Humidity max) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("info.eclipticseasons.environment.humidity").withStyle(ChatFormatting.GRAY));
        if (min != max) {
            list.add(((MutableComponent) min.getTranslation()).append(Component.literal(" - ").withStyle(ChatFormatting.GRAY)).append(max.getTranslation()));
        } else {
            list.add(min.getTranslation());
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CropHumidityInfo that = (CropHumidityInfo) o;
        return min == that.min && max == that.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}
