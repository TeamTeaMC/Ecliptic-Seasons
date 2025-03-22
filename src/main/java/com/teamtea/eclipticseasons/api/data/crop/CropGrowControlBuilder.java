package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.TestOnly;

import java.util.*;

public record CropGrowControlBuilder(
        HolderSet<AgroClimaticZone> cropClimateType,
        HolderSet<Block> applyTarget,
        HolderSet<CropGrowControlBuilder> parent,
        Optional<GrowParameter> defaultSolarTermGrowParameter,
        Optional<GrowParameter> defaultHumidityGrowParameter,
        EnumMap<SolarTerm, GrowParameter> solarTermList,
        EnumMap<Season, GrowParameter> seasonList,
        EnumMap<Humidity, GrowParameter> humidList) {

    public static final Codec<SolarTerm> SOLAR_TERM_CODEC_STRING = Codec.STRING
            .comapFlatMap(s -> {
                try {
                    return DataResult.success(SolarTerm.valueOf(s.toUpperCase()));
                } catch (ResourceLocationException resourcelocationexception) {
                    return DataResult.error(() -> "Not a valid solar term: " + s + " " + resourcelocationexception.getMessage());
                }
            }, SolarTerm::getName)
            .stable();
    public static final Codec<EnumMap<Season, GrowParameter>> Season_ENUM_MAP_CODEC = CodecUtil.enumMapCodec(StringRepresentable.fromEnum(Season::collectValues), GrowParameter.CODEC, Season.class);
    public static final Codec<EnumMap<Humidity, GrowParameter>> HUMID_ENUM_MAP_CODEC = CodecUtil.enumMapCodec(StringRepresentable.fromEnum(Humidity::collectValues), GrowParameter.CODEC, Humidity.class);
    public static final Codec<EnumMap<SolarTerm, GrowParameter>> SOLAR_TERM_ENUM_MAP_CODEC = CodecUtil.enumMapCodec(StringRepresentable.fromEnum(SolarTerm::collectValues), GrowParameter.CODEC, SolarTerm.class);
    public static final HolderSet.Direct<CropGrowControlBuilder> EMPTY = HolderSet.direct(List.of());


    public static final Codec<HolderSet<Block>> BLOCK_HOLDER_SET_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(b -> Optional.ofNullable(b))
            ).apply(instance, b -> b.orElseGet(HolderSet::direct))
    );

    // 输出的json与这里的排序有关，这里是六个，那么前三个将在后面，具体看情况，，但是基本都是对半分
    public static final Codec<CropGrowControlBuilder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            GrowParameter.CODEC.optionalFieldOf("humidity_default").forGetter(CropGrowControlBuilder::defaultHumidityGrowParameter),
            SOLAR_TERM_ENUM_MAP_CODEC.fieldOf("solar_terms").orElse(new EnumMap<>(SolarTerm.class)).forGetter(CropGrowControlBuilder::solarTermList),
            Season_ENUM_MAP_CODEC.fieldOf("seasons").orElse(new EnumMap<>(Season.class)).forGetter(CropGrowControlBuilder::seasonList),
            HUMID_ENUM_MAP_CODEC.fieldOf("humidity").orElse(new EnumMap<>(Humidity.class)).forGetter(CropGrowControlBuilder::humidList),
            CodecUtil.holderSetCodec(ESRegistries.AGRO_CLIMATE).fieldOf("climate").forGetter(CropGrowControlBuilder::cropClimateType),
            BLOCK_HOLDER_SET_CODEC.fieldOf("apply_target").forGetter(CropGrowControlBuilder::applyTarget),
            CodecUtil.holderSetCodec(ESRegistries.CROP).fieldOf("parent").orElse(EMPTY).forGetter(CropGrowControlBuilder::parent),
            GrowParameter.CODEC.optionalFieldOf("season_default").forGetter(CropGrowControlBuilder::defaultSolarTermGrowParameter)
    ).apply(ins, (defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, holders, blockPredicate, holders2, defaultGrowParameter) ->
            new CropGrowControlBuilder(holders, blockPredicate, holders2, defaultGrowParameter, defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap)
    ));

    // note 1.20: 与1.21不同的是，数据包同步到客户端时Tag未准备好，此时呼叫HolderSet会导致无法进入服务器
    // note 1.20: 与1.21不同的是，网络同步时无法使用HolderSet，原因是设计限制，如上
    public static final Codec<CropGrowControlBuilder> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            GrowParameter.CODEC.optionalFieldOf("humidity_default").forGetter(CropGrowControlBuilder::defaultHumidityGrowParameter),
            SOLAR_TERM_ENUM_MAP_CODEC.fieldOf("solar_terms").orElse(new EnumMap<>(SolarTerm.class)).forGetter(CropGrowControlBuilder::solarTermList),
            Season_ENUM_MAP_CODEC.fieldOf("seasons").orElse(new EnumMap<>(Season.class)).forGetter(CropGrowControlBuilder::seasonList),
            HUMID_ENUM_MAP_CODEC.fieldOf("humidity").orElse(new EnumMap<>(Humidity.class)).forGetter(CropGrowControlBuilder::humidList),
            GrowParameter.CODEC.optionalFieldOf("season_default").forGetter(CropGrowControlBuilder::defaultSolarTermGrowParameter)
    ).apply(ins, (defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, defaultGrowParameter) ->
            new CropGrowControlBuilder(HolderSet.direct(), HolderSet.direct(), HolderSet.direct(), defaultGrowParameter, defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap)
    ));


    public SimplePair<Block, CropGrowControl> build() {
        SimplePair<Block, CropGrowControl> pair = SimplePair.of(null, null);

        return pair;
    }


    /**
     * We need to asure every crop info and climate type is matched.
     **/
    public boolean isChildClimateType(HolderSet<AgroClimaticZone> parent) {
        if (cropClimateType().size() > parent().size()) return false;
        // not use stream!!! would create many objects.
        Set<Holder<AgroClimaticZone>> cropClimateTypes = new HashSet<>();
        for (int i = 0; i < parent.size(); i++) {
            Holder<AgroClimaticZone> cropClimateTypeHolder = parent.get(i);
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
