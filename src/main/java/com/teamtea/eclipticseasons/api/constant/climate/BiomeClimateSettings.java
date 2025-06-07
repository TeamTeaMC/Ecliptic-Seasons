package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.BiomesClimateSettings;
import com.teamtea.eclipticseasons.api.util.fast.Enum2FloatMap;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class BiomeClimateSettings {
    private final Enum2FloatMap<SolarTerm> seasonalTemperatures;
    private final Enum2FloatMap<SolarTerm> seasonalDownfalls;
    private float temperature;
    private float downfall;

    public BiomeClimateSettings(Biome biome, List<BiomesClimateSettings> biomesClimateSettingsList) {
        this();
        this.temperature = biome.getModifiedClimateSettings().temperature();
        this.downfall = biome.getModifiedClimateSettings().downfall();
        for (BiomesClimateSettings modifiers : biomesClimateSettingsList) {
            if (modifiers.temperature().isPresent()) {
                this.temperature = modifiers.temperature().get();
            }
            if (modifiers.downfall().isPresent()) {
                this.downfall = modifiers.downfall().get();
            }
        }
        this.seasonalTemperatures.fill(this.temperature);
        this.seasonalDownfalls.fill(this.downfall);
        boolean setTemp = false;
        for (BiomesClimateSettings modifiers : biomesClimateSettingsList) {
            if (modifiers.temperatureChanges().isPresent()) {
                modifiers.temperatureChanges().get().combine()
                        .forEach(this.seasonalTemperatures::add);
                setTemp = true;
            }
            if (modifiers.downfallChanges().isPresent()) {
                modifiers.downfallChanges().get().combine()
                        .forEach(this.seasonalDownfalls::add);
            }
        }
        if (!setTemp) {
            for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                seasonalTemperatures.add(solarTerm, solarTerm.getTemperatureChange());
            }
        }
    }

    public BiomeClimateSettings() {
        this.seasonalTemperatures = new Enum2FloatMap<>(SolarTerm.class, 0);
        this.seasonalDownfalls = new Enum2FloatMap<>(SolarTerm.class, 0);
    }


    public float getTemperature(SolarTerm solarTerm) {
        return this.seasonalTemperatures.get(solarTerm);
    }

    public float getTemperatureChange(SolarTerm solarTerm) {
        return this.seasonalTemperatures.get(solarTerm) - this.temperature;
    }

    public float getDownfall(SolarTerm solarTerm) {
        return this.seasonalDownfalls.get(solarTerm);
    }

    public float getDownfallChange(SolarTerm solarTerm) {
        return this.seasonalDownfalls.get(solarTerm) - this.downfall;
    }

}
