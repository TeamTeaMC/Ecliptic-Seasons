package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.backport.FakeBlockPredicate;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.backport.FakeStatePropertiesPredicate;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;

import java.util.*;

public record CropGrowControlBuilder(
        HolderSet<AgroClimaticZone> cropClimateType,
        FakeBlockPredicate applyTarget,
        HolderSet<CropGrowControlBuilder> parent,
        Optional<GrowParameter> defaultSolarTermGrowParameter,
        Optional<GrowParameter> defaultHumidityGrowParameter,
        Enum2ObjectMap<SolarTerm, GrowParameter> solarTermList,
        @Deprecated(forRemoval = true)
        Enum2ObjectMap<Season.Sub, GrowParameter> subSeasonList,
        Enum2ObjectMap<Season, GrowParameter> seasonList,
        Enum2ObjectMap<Humidity, GrowParameter> humidList,
        Optional<HolderSet<Block>> notGreenHouse) {

    public CropGrowControlBuilder(
            HolderSet<AgroClimaticZone> cropClimateType,
            FakeBlockPredicate applyTarget,
            HolderSet<CropGrowControlBuilder> parent,
            Optional<GrowParameter> defaultSolarTermGrowParameter,
            Optional<GrowParameter> defaultHumidityGrowParameter,
            Enum2ObjectMap<SolarTerm, GrowParameter> solarTermList,
            Enum2ObjectMap<Season, GrowParameter> seasonList,
            Enum2ObjectMap<Humidity, GrowParameter> humidList,
            Optional<HolderSet<Block>> notGreenHouse) {
        this(cropClimateType, applyTarget, parent, defaultSolarTermGrowParameter, defaultHumidityGrowParameter,
                solarTermList, new Enum2ObjectMap<>(Season.Sub.class), seasonList, humidList, notGreenHouse);
    }

    public static final Codec<Enum2ObjectMap<Season, GrowParameter>> Season_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SEASON, GrowParameter.CODEC, Season.class);
    public static final Codec<Enum2ObjectMap<Humidity, GrowParameter>> HUMID_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.HUMIDITY, GrowParameter.CODEC, Humidity.class);
    public static final Codec<Enum2ObjectMap<SolarTerm, GrowParameter>> SOLAR_TERM_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SOLAR_TERM, GrowParameter.CODEC, SolarTerm.class);
    public static final Codec<Enum2ObjectMap<Season.Sub, GrowParameter>> SUB_SEASON_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SUB_SEASON, GrowParameter.CODEC, Season.Sub.class);

    public static final HolderSet.Direct<CropGrowControlBuilder> EMPTY = HolderSet.direct(List.of());

    @SuppressWarnings("removal")
    public static final Codec<CropGrowControlBuilder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            GrowParameter.CODEC.optionalFieldOf("humidity_default").forGetter(CropGrowControlBuilder::defaultHumidityGrowParameter),
            SOLAR_TERM_ENUM_MAP_CODEC.optionalFieldOf("solar_terms", new Enum2ObjectMap<>(SolarTerm.class)).forGetter(CropGrowControlBuilder::solarTermList),
            SUB_SEASON_ENUM_MAP_CODEC.optionalFieldOf("sub_seasons", new Enum2ObjectMap<>(Season.Sub.class)).forGetter(CropGrowControlBuilder::subSeasonList),
            Season_ENUM_MAP_CODEC.optionalFieldOf("seasons", new Enum2ObjectMap<>(Season.class)).forGetter(CropGrowControlBuilder::seasonList),
            HUMID_ENUM_MAP_CODEC.optionalFieldOf("humidity", new Enum2ObjectMap<>(Humidity.class)).forGetter(CropGrowControlBuilder::humidList),
            CodecUtil.holderSetCodec(ESRegistries.AGRO_CLIMATE).fieldOf("climate").forGetter(CropGrowControlBuilder::cropClimateType),
            ESExtraCodec.BLOCK_HOLDER_SET_CODEC.optionalFieldOf("unlike_greenhouse_material").forGetter(CropGrowControlBuilder::notGreenHouse),
            CodecUtil.holderSetCodec(ESRegistries.CROP).fieldOf("parent").orElse(HolderSet.direct()).forGetter(CropGrowControlBuilder::parent),
            GrowParameter.CODEC.optionalFieldOf("season_default").forGetter(CropGrowControlBuilder::defaultSolarTermGrowParameter),
            FakeBlockPredicate.CODEC.fieldOf("apply_target").forGetter(CropGrowControlBuilder::applyTarget)
    ).apply(ins, (defaultGrowParameter2, solarTermGrowParameterEnumMap,subMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, holders, notGreenHouse, holders2, defaultGrowParameter, blockPredicate) ->
            new CropGrowControlBuilder(holders, blockPredicate, holders2, defaultGrowParameter, defaultGrowParameter2, toSolarTermList(solarTermGrowParameterEnumMap, subMap, holders),  seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, notGreenHouse)
    ));

    // Note 1.20: Tags are uninitialized during client-side datapack sync (unlike 1.21); calling HolderSet here causes a connection failure.
    // Note 1.20: HolderSet is unavailable during network sync due to the aforementioned design constraints.
    public static final Codec<CropGrowControlBuilder> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            GrowParameter.CODEC.optionalFieldOf("humidity_default").forGetter(CropGrowControlBuilder::defaultHumidityGrowParameter),
            SOLAR_TERM_ENUM_MAP_CODEC.optionalFieldOf("solar_terms", new Enum2ObjectMap<>(SolarTerm.class)).forGetter(CropGrowControlBuilder::solarTermList),
            Season_ENUM_MAP_CODEC.optionalFieldOf("seasons", new Enum2ObjectMap<>(Season.class)).forGetter(CropGrowControlBuilder::seasonList),
            HUMID_ENUM_MAP_CODEC.optionalFieldOf("humidity", new Enum2ObjectMap<>(Humidity.class)).forGetter(CropGrowControlBuilder::humidList),
            GrowParameter.CODEC.optionalFieldOf("season_default").forGetter(CropGrowControlBuilder::defaultSolarTermGrowParameter)
    ).apply(ins, (defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, defaultGrowParameter) ->
            new CropGrowControlBuilder(HolderSet.direct(), new FakeBlockPredicate(Optional.empty(), Optional.empty()), HolderSet.direct(), defaultGrowParameter, defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, Optional.empty())
    ));

    private static Enum2ObjectMap<SolarTerm, GrowParameter> toSolarTermList(
            Enum2ObjectMap<SolarTerm, GrowParameter> solarTermList,
            Enum2ObjectMap<Season.Sub, GrowParameter> subMap,
            HolderSet<AgroClimaticZone> cropClimateType) {
        if (!subMap.isEmpty()) {
            for (Season.Sub subSeason : Season.Sub.collectValues()) {
                GrowParameter value = subMap.get(subSeason);
                if (value != null) {
                    if (subSeason.isValid()) {
                        SolarTerm start = subSeason.getFirstSolarTerm();
                        SolarTerm end = subSeason.getEndSolarTerm();
                        solarTermList.putIfAbsent(start, value);
                        solarTermList.putIfAbsent(end, value);
                    } else {
                        solarTermList.putIfAbsent(SolarTerm.NONE, value);
                    }
                }
            }
        }
        return solarTermList;
    }

    public SimplePair<Block, CropGrowControl> build() {
        SimplePair<Block, CropGrowControl> pair = SimplePair.of(null, null);

        return pair;
    }


    /**
     * We need to asure every crop info and climate type is matched.
     **/
    public boolean isChildClimateType(HolderSet<AgroClimaticZone> parentClimateType) {
        if (cropClimateType().size() > parentClimateType.size()) return false;
        // not use stream!!! would create many objects.
        Set<Holder<AgroClimaticZone>> cropClimateTypes = new HashSet<>();
        for (int i = 0; i < parentClimateType.size(); i++) {
            Holder<AgroClimaticZone> cropClimateTypeHolder = parentClimateType.get(i);
            cropClimateTypes.add(cropClimateTypeHolder);
        }
        for (int i = 0; i < cropClimateType().size(); i++) {
            Holder<AgroClimaticZone> cropClimateTypeHolder = cropClimateType().get(i);
            if (!cropClimateTypes.contains(cropClimateTypeHolder))
                return false;
        }
        return true;
    }
}
