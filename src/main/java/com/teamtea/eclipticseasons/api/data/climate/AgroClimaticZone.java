package com.teamtea.eclipticseasons.api.data.climate;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.crop.CropGrowControl;
import com.teamtea.eclipticseasons.api.data.crop.GrowParameter;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link AgroClimaticZone} enum is used to define specific climate requirements for crops, distinguishing them from
 * the broader {@link  com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags ClimateTypeBiomeTags}. While {@link  com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags ClimateTypeBiomeTags} categorizes biomes based on general seasonal and weather patterns
 * (e.g., wet/dry seasons, temperature variations), {@link AgroClimaticZone} focuses on the precise conditions needed
 * for crops to thrive, such as grow rate ranges in seasons and humidity levels.
 * <p>
 * This distinction is necessary because crops in the game may require very specific conditions (like high humidity
 * or extreme heat) that do not necessarily correlate with the broader biome classification, ensuring realistic crop growth
 * mechanics.
 * <p>
 * Additionally, {@link AgroClimaticZone} allows creation of maps to automatically populate data for newly added agroclimate types.
 */
public record AgroClimaticZone(HolderSet<Biome> biomes,
                               Optional<GrowParameter> growParameter,
                               Optional<Map<Either<Season, SolarTerm>, Float>> defaultMapping,
                               Optional<Map<Either<Season, SolarTerm>, List<Map<Either<Season, SolarTerm>, Float>>>> mapping) {

    public static final Codec<Either<Season, SolarTerm>> EITHER_CODEC = Codec.either(StringRepresentable.fromEnum(Season::collectValues), StringRepresentable.fromEnum(SolarTerm::collectValues));
    public static final Codec<Map<Either<Season, SolarTerm>, Float>> EITHER_PAIR_CODEC = CodecUtil.mapCodec(EITHER_CODEC, Codec.FLOAT);
    public static final Codec<Map<Either<Season, SolarTerm>, List<Map<Either<Season, SolarTerm>, Float>>>> EITHER_MAP_CODEC =
            CodecUtil.mapCodec(EITHER_CODEC, EITHER_PAIR_CODEC.listOf());

    public static final Codec<AgroClimaticZone> CODEC =
            RecordCodecBuilder.create(builder -> builder.group(
                    CodecUtil.holderSetCodec(Registries.BIOME).fieldOf("biomes").forGetter(AgroClimaticZone::biomes),
                    GrowParameter.CODEC.optionalFieldOf("global").forGetter(AgroClimaticZone::growParameter),
                    EITHER_PAIR_CODEC.optionalFieldOf("default_mapping").forGetter(AgroClimaticZone::defaultMapping),
                    EITHER_MAP_CODEC.optionalFieldOf("mappings").forGetter(AgroClimaticZone::mapping)
            ).apply(builder, AgroClimaticZone::new));

    // note 1.20: 与1.21不同的是，网络同步时无法使用HolderSet，原因是设计限制，CropGrowControlBuilder
    // note 可见RegistryFixedCodec和ClientBoundLoginPacket
    public static final Codec<AgroClimaticZone> DIRECT_CODEC =
            RecordCodecBuilder.create(builder -> builder.group(
                    GrowParameter.CODEC.optionalFieldOf("global").forGetter(AgroClimaticZone::growParameter),
                    EITHER_PAIR_CODEC.optionalFieldOf("default_mapping").forGetter(AgroClimaticZone::defaultMapping),
                    EITHER_MAP_CODEC.optionalFieldOf("mappings").forGetter(AgroClimaticZone::mapping)
            ).apply(builder, ((global, default_mapping, mappings) -> new AgroClimaticZone(HolderSet.direct(),global,default_mapping,mappings))));


    public GrowParameter buildFromList(CropGrowControl deaultCropGrowControl, List<Map<Either<Season, SolarTerm>, Float>> list) {
        GrowParameter growParameterResult = null;
        if (list != null && deaultCropGrowControl != null) {
            float chance = 0;
            for (int m = 0, listSize = list.size(); m < listSize; m++) {
                Map<Either<Season, SolarTerm>, Float> eitherFloatMap = list.get(m);
                for (Map.Entry<Either<Season, SolarTerm>, Float> eitherFloatEntry : eitherFloatMap.entrySet()) {
                    Either<Season, SolarTerm> key = eitherFloatEntry.getKey();
                    if (key.right().isPresent()) {
                        GrowParameter orDefault = deaultCropGrowControl.getGrowParameter(key.right().get());
                        if (orDefault != null) {
                            chance += orDefault.grow_chance() * eitherFloatEntry.getValue();
                        }
                    } else if (key.left().isPresent()) {
                        GrowParameter orDefault = deaultCropGrowControl.getGrowParameter(key.left().get());
                        if (orDefault != null) {
                            chance += orDefault.grow_chance() * eitherFloatEntry.getValue();
                        }
                    }
                }
            }
            growParameterResult = GrowParameter.builder().growChance(chance).end();
        }
        return growParameterResult;
    }

    public GrowParameter getGrowParameterFromMapping(CropGrowControl deaultCropGrowControl, SolarTerm solarTerm) {
        GrowParameter growParameterResult = null;
        if (this.mapping().isPresent()) {
            Map<Either<Season, SolarTerm>, List<Map<Either<Season, SolarTerm>, Float>>> eitherListMap = this.mapping().get();
            List<Map<Either<Season, SolarTerm>, Float>> list = eitherListMap.getOrDefault(Either.right(solarTerm), null);
            growParameterResult = buildFromList(deaultCropGrowControl, list);
            if(growParameterResult==null) {
                list = eitherListMap.getOrDefault(Either.left(solarTerm.getSeason()), null);
                growParameterResult = buildFromList(deaultCropGrowControl, list);
            }

        }

        if (growParameterResult == null
                && this.defaultMapping().isPresent()) {
            growParameterResult = buildFromList(deaultCropGrowControl, List.of(defaultMapping().get()));
        }

        if (growParameterResult == null && this.growParameter().isPresent()) {
            growParameterResult = this.growParameter().get();
        }
        return growParameterResult;
    }


    public static Builder builder(HolderSet<Biome> biomes) {
        return new Builder(biomes);
    }

    public static class Builder {
        private HolderSet<Biome> biomes;
        private GrowParameter growParameter;
        private Map<Either<Season, SolarTerm>, List<Map<Either<Season, SolarTerm>, Float>>> mapping;
        private Map<Either<Season, SolarTerm>, Float> defaultMapping;

        private Builder(HolderSet<Biome> biomes) {
            this.biomes = biomes;
        }

        public Builder growParameter(GrowParameter growParameter) {
            this.growParameter = growParameter;
            return this;
        }

        public Builder mapping(Map<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>> mapping) {
            Map<Either<Season, SolarTerm>, List<Map<Either<Season, SolarTerm>, Float>>> mapping2 = new LinkedHashMap<>(24);
            for (Map.Entry<Either<Season, SolarTerm>, List<Pair<Either<Season, SolarTerm>, Float>>> eitherListEntry : mapping.entrySet()) {
                mapping2.put(eitherListEntry.getKey(), eitherListEntry.getValue().stream().map(
                        p -> Map.of(p.getFirst(), p.getSecond())
                ).toList());
            }
            this.mapping = mapping2;
            return this;
        }

        public Builder defaultMapping(Pair<Either<Season, SolarTerm>, Float> pair) {
            Map<Either<Season, SolarTerm>, Float> mapping2 = new LinkedHashMap<>(24);
            mapping2.put(pair.getFirst(), pair.getSecond());
            this.defaultMapping = mapping2;
            return this;
        }

        public AgroClimaticZone end() {
            if (biomes == null) {
                throw new IllegalArgumentException("Biomes must not be null");
            }
            return new AgroClimaticZone(biomes, Optional.ofNullable(growParameter), Optional.ofNullable(defaultMapping), Optional.ofNullable(mapping));
        }
    }
}
