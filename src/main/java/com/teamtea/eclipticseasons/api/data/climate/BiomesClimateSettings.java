package com.teamtea.eclipticseasons.api.data.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;

import java.util.Optional;

public record BiomesClimateSettings(
        HolderSet<Biome> biomes,
        Optional<Float> temperature,
        Optional<Float> downfall,
        Optional<SolarTermValueMap<Float>> temperatureChanges,
        Optional<SolarTermValueMap<Float>> downfallChanges
) {
    public static final Codec<BiomesClimateSettings> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            SolarTermValueMap.FLOAT_CODEC.optionalFieldOf("temperature_changes").forGetter(BiomesClimateSettings::temperatureChanges),
            SolarTermValueMap.FLOAT_CODEC.optionalFieldOf("downfall_changes").forGetter(BiomesClimateSettings::downfallChanges),
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(BiomesClimateSettings::biomes),
            Codec.FLOAT.optionalFieldOf("temperature").forGetter(BiomesClimateSettings::temperature),
            Codec.FLOAT.optionalFieldOf("downfall").forGetter(BiomesClimateSettings::downfall)
    ).apply(ins, (tempChanges, rainChanges, biomes, temp, rain) ->
            new BiomesClimateSettings(biomes, temp, rain, tempChanges, rainChanges)
    ));

    public static final Codec<BiomesClimateSettings> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            SolarTermValueMap.FLOAT_CODEC.optionalFieldOf("temperature_changes").forGetter(BiomesClimateSettings::temperatureChanges),
            SolarTermValueMap.FLOAT_CODEC.optionalFieldOf("downfall_changes").forGetter(BiomesClimateSettings::downfallChanges),
            Codec.FLOAT.optionalFieldOf("temperature").forGetter(BiomesClimateSettings::temperature),
            Codec.FLOAT.optionalFieldOf("downfall").forGetter(BiomesClimateSettings::downfall)
    ).apply(ins, (tempChanges, rainChanges,  temp, rain) ->
            new BiomesClimateSettings(HolderSet.direct(), temp, rain, tempChanges, rainChanges)
    ));
}
