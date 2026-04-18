package com.teamtea.eclipticseasons.api.data.client;


import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.misc.util.HolderMappable;
import com.teamtea.eclipticseasons.api.misc.util.Mergable;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public record BiomeColor(
        HolderSet<Biome> biomes,
        Optional<SolarTermValueMap<ColorMode>> grassColor,
        Optional<SolarTermValueMap<ColorMode>> foliageColor,
        Optional<SolarTermValueMap<ColorMode>> skyColor,
        Optional<SolarTermValueMap<ColorMode>> waterColor,
        Optional<SolarTermValueMap<ColorMode>> waterFogColor,
        Optional<SolarTermValueMap<ColorMode>> fogColor
) implements HolderMappable<HolderSet<Biome>, BiomeColor.Instance> {

    public BiomeColor(HolderSet<Biome> biomes, Optional<SolarTermValueMap<ColorMode>> grassColor, Optional<SolarTermValueMap<ColorMode>> foliageColor) {
        this(biomes, grassColor, foliageColor, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static final Codec<BiomeColor> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes").forGetter(BiomeColor::biomes),
            SolarTermValueMap.codec(ColorMode.CODEC).optionalFieldOf("grass_colors").forGetter(BiomeColor::grassColor),
            SolarTermValueMap.codec(ColorMode.CODEC).optionalFieldOf("foliage_colors").forGetter(BiomeColor::foliageColor),
            SolarTermValueMap.codec(ColorMode.CODEC).optionalFieldOf("sky_colors").forGetter(BiomeColor::skyColor),
            SolarTermValueMap.codec(ColorMode.CODEC).optionalFieldOf("water_colors").forGetter(BiomeColor::waterColor),
            SolarTermValueMap.codec(ColorMode.CODEC).optionalFieldOf("water_fog_colors").forGetter(BiomeColor::waterFogColor),
            SolarTermValueMap.codec(ColorMode.CODEC).optionalFieldOf("fog_colors").forGetter(BiomeColor::fogColor)
    ).apply(ins, BiomeColor::new));

    private static final SolarTermValueMap<ColorMode> EMPTY_MODE_MAP = new SolarTermValueMap<>(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
    );


    public @NonNull Instance toInstance() {
        Enum2ObjectMap<SolarTerm, ColorMode> grassMap = grassColor.orElse(EMPTY_MODE_MAP).combine();
        Enum2ObjectMap<SolarTerm, ColorMode> foliageMap = foliageColor.orElse(EMPTY_MODE_MAP).combine();
        Enum2ObjectMap<SolarTerm, ColorMode> skyMap = skyColor.orElse(EMPTY_MODE_MAP).combine();
        Enum2ObjectMap<SolarTerm, ColorMode> waterMap = waterColor.orElse(EMPTY_MODE_MAP).combine();
        Enum2ObjectMap<SolarTerm, ColorMode> waterFogMap = waterFogColor.orElse(EMPTY_MODE_MAP).combine();
        Enum2ObjectMap<SolarTerm, ColorMode> fogMap = fogColor.orElse(EMPTY_MODE_MAP).combine();

        if (grassMap.isEmpty() && foliageMap.isEmpty() && skyMap.isEmpty()
                && waterMap.isEmpty() && waterFogMap.isEmpty() && fogMap.isEmpty()) {
            return Instance.EMPTY;
        }

        Enum2ObjectMap<SolarTerm, ColorMode.Instance> grassColorMap = SolarTermValueMap.convertToEnum2ObjectMap(SolarTerm.class, grassMap, ColorMode::toInstance);
        Enum2ObjectMap<SolarTerm, ColorMode.Instance> foliageColorMap = SolarTermValueMap.convertToEnum2ObjectMap(SolarTerm.class, foliageMap, ColorMode::toInstance);
        Enum2ObjectMap<SolarTerm, ColorMode.Instance> skyColorMap = SolarTermValueMap.convertToEnum2ObjectMap(SolarTerm.class, skyMap, ColorMode::toInstance);
        Enum2ObjectMap<SolarTerm, ColorMode.Instance> waterColorMap = SolarTermValueMap.convertToEnum2ObjectMap(SolarTerm.class, waterMap, ColorMode::toInstance);
        Enum2ObjectMap<SolarTerm, ColorMode.Instance> waterFogColorMap = SolarTermValueMap.convertToEnum2ObjectMap(SolarTerm.class, waterFogMap, ColorMode::toInstance);
        Enum2ObjectMap<SolarTerm, ColorMode.Instance> fogColorMap = SolarTermValueMap.convertToEnum2ObjectMap(SolarTerm.class, fogMap, ColorMode::toInstance);

        if (grassColorMap.isEmpty() && foliageColorMap.isEmpty() && skyColorMap.isEmpty()
                && waterColorMap.isEmpty() && waterFogColorMap.isEmpty() && fogColorMap.isEmpty()) {
            return Instance.EMPTY;
        }

        return new Instance(grassColorMap, foliageColorMap, skyColorMap, waterColorMap, waterFogColorMap, fogColorMap);
    }

    @Override
    public Pair<HolderSet<Biome>, Instance> asHolderMapping() {
        return Pair.of(biomes, toInstance());
    }

    public record Instance(
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> grassColor,
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> foliageColor,
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> skyColor,
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> waterColor,
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> waterFogColor,
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> fogColor
    ) implements Mergable<Instance> {
        private static final Enum2ObjectMap<SolarTerm, ColorMode.Instance> EMPTY_COLOR_MAP = new Enum2ObjectMap<>(SolarTerm.class, null);

        private static final Instance EMPTY = new Instance(EMPTY_COLOR_MAP, EMPTY_COLOR_MAP, EMPTY_COLOR_MAP, EMPTY_COLOR_MAP, EMPTY_COLOR_MAP, EMPTY_COLOR_MAP);

        @Override
        public Instance merge(Instance next) {
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> newGrassColor = new Enum2ObjectMap<>(SolarTerm.class);
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> newFoliageColor = new Enum2ObjectMap<>(SolarTerm.class);
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> newSkyColor = new Enum2ObjectMap<>(SolarTerm.class);
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> newWaterColor = new Enum2ObjectMap<>(SolarTerm.class);
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> newWaterFogColor = new Enum2ObjectMap<>(SolarTerm.class);
            Enum2ObjectMap<SolarTerm, ColorMode.Instance> newFogColor = new Enum2ObjectMap<>(SolarTerm.class);
            newGrassColor.putAll(grassColor);
            newGrassColor.putAll(next.grassColor);
            newFoliageColor.putAll(foliageColor);
            newFoliageColor.putAll(next.foliageColor);
            newSkyColor.putAll(skyColor);
            newSkyColor.putAll(next.skyColor);
            newWaterColor.putAll(waterColor);
            newWaterColor.putAll(next.waterColor);
            newWaterFogColor.putAll(waterFogColor);
            newWaterFogColor.putAll(next.waterFogColor);
            newFogColor.putAll(fogColor);
            newFogColor.putAll(next.fogColor);
            return new Instance(newGrassColor, newFoliageColor, newSkyColor, newWaterColor, newWaterFogColor, newFogColor);
        }
    }

}
