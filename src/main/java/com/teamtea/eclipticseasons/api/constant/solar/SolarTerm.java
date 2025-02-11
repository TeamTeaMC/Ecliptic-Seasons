package com.teamtea.eclipticseasons.api.constant.solar;

import com.teamtea.eclipticseasons.api.constant.climate.*;
import com.teamtea.eclipticseasons.api.constant.solar.color.*;
import com.teamtea.eclipticseasons.api.constant.tag.SeasonTypeBiomeTags;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public enum SolarTerm {
    // Spring Solar Terms
    BEGINNING_OF_SPRING(-0.25F, 10500),
    RAIN_WATER(-0.15F, 11000),
    INSECTS_AWAKENING(-0.1F, 11500),
    SPRING_EQUINOX(0, 12000),
    FRESH_GREEN(0, 12500),
    GRAIN_RAIN(0.05F, 13000),

    // Summer Solar Terms
    BEGINNING_OF_SUMMER(0.1F, 13500),
    LESSER_FULLNESS(0.15F, 14000),
    GRAIN_IN_EAR(0.15F, 14500),
    SUMMER_SOLSTICE(0.2F, 15000),
    LESSER_HEAT(0.2F, 14500),
    GREATER_HEAT(0.25F, 14000),

    // Autumn Solar Terms
    BEGINNING_OF_AUTUMN(0.15F, 13500),
    END_OF_HEAT(0.1F, 13000),
    WHITE_DEW(0.05F, 12500),
    AUTUMNAL_EQUINOX(0, 12000),
    COLD_DEW(-0.1F, 11500),
    FIRST_FROST(-0.2F, 11000),

    // Winter Solar Terms
    BEGINNING_OF_WINTER(-0.3F, 10500),
    LIGHT_SNOW(-0.35F, 10000),
    HEAVY_SNOW(-0.35F, 9500),
    WINTER_SOLSTICE(-0.4F, 9000),
    LESSER_COLD(-0.45F, 9500),
    GREATER_COLD(-0.4F, 10000),

    NONE(0.0F, 12000);

    private final float temperature;
    private final int dayTime;

    SolarTerm(float temperature, int dayTime) {
        this.temperature = temperature;
        this.dayTime = dayTime;
    }

    public String getName() {
        return this.toString().toLowerCase();
    }

    public ITextComponent getTranslation() {
        return new TranslationTextComponent("info.eclipticseasons.environment.solar_term." + getName());
    }

    public ITextComponent getAlternationText() {
        return new TranslationTextComponent("info.eclipticseasons.environment.solar_term.alternation." + getName()).withStyle(getSeason().getColor());
    }

    public static SolarTerm get(int index) {
        return values()[index];
    }


    public RainySolarTermColors getColorInfo() {
        return RainySolarTermColors.values()[this.ordinal()];
    }

    public SolarTermColor getSolarTermColor(BiomeDictionary.Type biomeHolder) {
        if ((SeasonTypeBiomeTags.RAINLESS).equals(biomeHolder)) {
            return NoneSolarTermColors.get(this.ordinal());
        } else if ((SeasonTypeBiomeTags.ARID).equals(biomeHolder)) {
            return NoneSolarTermColors.get(this.ordinal());
        } else if ((SeasonTypeBiomeTags.DROUGHTY).equals(biomeHolder)) {
            return SlightlySolarTermColors.get(this.ordinal());
        } else if ((SeasonTypeBiomeTags.SOFT).equals(biomeHolder)) {
            return SlightlySolarTermColors.get(this.ordinal());
        } else if ((SeasonTypeBiomeTags.RAINY).equals(biomeHolder)) {
            return SlightlySolarTermColors.get(this.ordinal());
        } else if ((SeasonTypeBiomeTags.MONSOONAL).equals(biomeHolder)) {
            return RainySolarTermColors.values()[this.ordinal()];
        } else if ((SeasonTypeBiomeTags.SEASONAL).equals(biomeHolder)) {
            return TemperateSolarTermColors.values()[this.ordinal()];
        } else {
            return NoneSolarTermColors.get(this.ordinal());
        }
    }

    public float getTemperatureChange() {
        return temperature;
    }

    public int getDayTime() {
        return dayTime;
    }

    public Season getSeason() {
        return Season.values()[this.ordinal() / 6];
    }


    public BiomeRain getBiomeRain(RegistryKey<Biome> biomeHolder) {
        if (!BiomeDictionary.getBiomes(BiomeDictionary.Type.OVERWORLD).contains(biomeHolder) || BiomeDictionary.getBiomes(SeasonTypeBiomeTags.RAINLESS).contains(biomeHolder)) {
            return FlatRain.RAINLESS;
        } else if (BiomeDictionary.getBiomes(SeasonTypeBiomeTags.ARID).contains(biomeHolder)) {
            return FlatRain.ARID;
        } else if (BiomeDictionary.getBiomes(SeasonTypeBiomeTags.DROUGHTY).contains(biomeHolder)) {
            return FlatRain.DROUGHTY;
        } else if (BiomeDictionary.getBiomes(SeasonTypeBiomeTags.SOFT).contains(biomeHolder)) {
            return FlatRain.SOFT;
        } else if (BiomeDictionary.getBiomes(SeasonTypeBiomeTags.RAINY).contains(biomeHolder)) {
            return FlatRain.RAINY;
        } else if (BiomeDictionary.getBiomes(SeasonTypeBiomeTags.MONSOONAL).contains(biomeHolder)) {
            return MonsoonRain.values()[this.ordinal()];
        } else {
            return TemperateRain.values()[this.ordinal()];
        }
    }

    public static SnowTerm getSnowTerm(Biome biome) {
        if (biome == null) return SnowTerm.T05;
        else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.95 + 0.001f) {
            return SnowTerm.T1;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.8 + 0.001f) {
            return SnowTerm.T08;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.6 + 0.001f) {
            return SnowTerm.T06;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.5 + 0.001f) {
            return SnowTerm.T05;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.4 + 0.001f) {
            return SnowTerm.T04;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.3 + 0.001f) {
            return SnowTerm.T03;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.2 + 0.001f) {
            return SnowTerm.T02;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.15 + 0.001f) {
            return SnowTerm.T015;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.1 + 0.001f) {
            return SnowTerm.T01;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.05 + 0.001f) {
            return SnowTerm.T05;
        } else if (BiomeClimateManager.getDefaultTemperature(biome) > 0.01 + 0.001f) {
            return SnowTerm.T001;
        }
        return SnowTerm.T0;
    }


    public boolean isInTerms(SolarTerm start, SolarTerm end) {
        if (start == NONE || end == NONE) return false;
        else if (start == end) return true;
        else if (start.ordinal() <= end.ordinal()) {
            return start.ordinal() <= this.ordinal() && this.ordinal() <= end.ordinal();
        } else
            return start.ordinal() <= this.ordinal() || this.ordinal() <= end.ordinal();
    }
}
