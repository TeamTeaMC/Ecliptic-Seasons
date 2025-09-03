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
        Enum2ObjectMap<Season, GrowParameter> seasonList,
        Enum2ObjectMap<Humidity, GrowParameter> humidList,
        Optional<HolderSet<Block>> notGreenHouse) {

    public static final Codec<SolarTerm> SOLAR_TERM_CODEC_STRING = Codec.STRING
            .comapFlatMap(s -> {
                try {
                    return DataResult.success(SolarTerm.valueOf(s.toUpperCase()));
                } catch (ResourceLocationException resourcelocationexception) {
                    return DataResult.error(() -> "Not a valid solar term: " + s + " " + resourcelocationexception.getMessage());
                }
            }, SolarTerm::getName)
            .stable();
    public static final Codec<Enum2ObjectMap<Season, GrowParameter>> Season_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SEASON, GrowParameter.CODEC, Season.class);
    public static final Codec<Enum2ObjectMap<Humidity, GrowParameter>> HUMID_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.HUMIDITY, GrowParameter.CODEC, Humidity.class);
    public static final Codec<Enum2ObjectMap<SolarTerm, GrowParameter>> SOLAR_TERM_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SOLAR_TERM, GrowParameter.CODEC, SolarTerm.class);

    public static final HolderSet.Direct<CropGrowControlBuilder> EMPTY = HolderSet.direct(List.of());


    // 输出的json与这里的排序有关，这里是六个，那么前三个将在后面，具体看情况，，但是基本都是对半分

    public static final Codec<CropGrowControlBuilder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            GrowParameter.CODEC.optionalFieldOf("humidity_default").forGetter(CropGrowControlBuilder::defaultHumidityGrowParameter),
            SOLAR_TERM_ENUM_MAP_CODEC.optionalFieldOf("solar_terms",new Enum2ObjectMap<>(SolarTerm.class)).forGetter(CropGrowControlBuilder::solarTermList),
            Season_ENUM_MAP_CODEC.optionalFieldOf("seasons",new Enum2ObjectMap<>(Season.class)).forGetter(CropGrowControlBuilder::seasonList),
            HUMID_ENUM_MAP_CODEC.optionalFieldOf("humidity",new Enum2ObjectMap<>(Humidity.class)).forGetter(CropGrowControlBuilder::humidList),
            CodecUtil.holderSetCodec(ESRegistries.AGRO_CLIMATE).fieldOf("climate").forGetter(CropGrowControlBuilder::cropClimateType),
            ESExtraCodec.BLOCK_HOLDER_SET_CODEC.optionalFieldOf("unlike_greenhouse_material").forGetter(CropGrowControlBuilder::notGreenHouse),
            CodecUtil.holderSetCodec(ESRegistries.CROP).fieldOf("parent").orElse(HolderSet.direct()).forGetter(CropGrowControlBuilder::parent),
            GrowParameter.CODEC.optionalFieldOf("season_default").forGetter(CropGrowControlBuilder::defaultSolarTermGrowParameter),
            FakeBlockPredicate.CODEC.fieldOf("apply_target").forGetter(CropGrowControlBuilder::applyTarget)
    ).apply(ins, (defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, holders,notGreenHouse,  holders2, defaultGrowParameter, blockPredicate) ->
            new CropGrowControlBuilder(holders, blockPredicate, holders2, defaultGrowParameter, defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, notGreenHouse)
    ));
    // note 1.20: 与1.21不同的是，数据包同步到客户端时Tag未准备好，此时呼叫HolderSet会导致无法进入服务器
    // note 1.20: 与1.21不同的是，网络同步时无法使用HolderSet，原因是设计限制，如上
    public static final Codec<CropGrowControlBuilder> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            GrowParameter.CODEC.optionalFieldOf("humidity_default").forGetter(CropGrowControlBuilder::defaultHumidityGrowParameter),
            SOLAR_TERM_ENUM_MAP_CODEC.optionalFieldOf("solar_terms",new Enum2ObjectMap<>(SolarTerm.class)).forGetter(CropGrowControlBuilder::solarTermList),
            Season_ENUM_MAP_CODEC.optionalFieldOf("seasons",new Enum2ObjectMap<>(Season.class)).forGetter(CropGrowControlBuilder::seasonList),
            HUMID_ENUM_MAP_CODEC.optionalFieldOf("humidity",new Enum2ObjectMap<>(Humidity.class)).forGetter(CropGrowControlBuilder::humidList),
            GrowParameter.CODEC.optionalFieldOf("season_default").forGetter(CropGrowControlBuilder::defaultSolarTermGrowParameter)
    ).apply(ins, (defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, defaultGrowParameter) ->
            new CropGrowControlBuilder(HolderSet.direct(), new FakeBlockPredicate(Optional.empty(),Optional.empty()), HolderSet.direct(), defaultGrowParameter, defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap,Optional.empty())
    ));


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
