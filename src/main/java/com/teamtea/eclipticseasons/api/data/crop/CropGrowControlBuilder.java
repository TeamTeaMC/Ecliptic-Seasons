package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.IdentifierException;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.TestOnly;

import java.util.*;


public record CropGrowControlBuilder(
        HolderSet<AgroClimaticZone> cropClimateType,
        BlockPredicate applyTarget,
        HolderSet<CropGrowControlBuilder> parent,
        Optional<GrowParameter> defaultSolarTermGrowParameter,
        Optional<GrowParameter> defaultHumidityGrowParameter,
        Enum2ObjectMap<SolarTerm, GrowParameter> solarTermList,
        Enum2ObjectMap<Season, GrowParameter> seasonList,
        Enum2ObjectMap<Humidity, GrowParameter> humidList,
        Optional<BlockPredicate> notGreenHouse) {

    public static final Codec<SolarTerm> SOLAR_TERM_CODEC_STRING = Codec.STRING
            .comapFlatMap(s -> {
                try {
                    return DataResult.success(SolarTerm.valueOf(s.toUpperCase()));
                } catch (IdentifierException Identifierexception) {
                    return DataResult.error(() -> "Not a valid solar term: " + s + " " + Identifierexception.getMessage());
                }
            }, SolarTerm::getName)
            .stable();
    public static final Codec<Enum2ObjectMap<Season, GrowParameter>> Season_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SEASON, GrowParameter.CODEC, Season.class);
    public static final Codec<Enum2ObjectMap<Humidity, GrowParameter>> HUMID_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.HUMIDITY, GrowParameter.CODEC, Humidity.class);
    public static final Codec<Enum2ObjectMap<SolarTerm, GrowParameter>> SOLAR_TERM_ENUM_MAP_CODEC = CodecUtil.enum2ObjectMapCodec(ESExtraCodec.SOLAR_TERM, GrowParameter.CODEC, SolarTerm.class);

    // 输出的json与这里的排序有关，这里是六个，那么前三个将在后面，具体看情况，，但是基本都是对半分
    public static final Codec<CropGrowControlBuilder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            GrowParameter.CODEC.optionalFieldOf("humidity_default").forGetter(CropGrowControlBuilder::defaultHumidityGrowParameter),
            SOLAR_TERM_ENUM_MAP_CODEC.optionalFieldOf("solar_terms",new Enum2ObjectMap<>(SolarTerm.class)).forGetter(CropGrowControlBuilder::solarTermList),
            Season_ENUM_MAP_CODEC.optionalFieldOf("seasons",new Enum2ObjectMap<>(Season.class)).forGetter(CropGrowControlBuilder::seasonList),
            HUMID_ENUM_MAP_CODEC.optionalFieldOf("humidity",new Enum2ObjectMap<>(Humidity.class)).forGetter(CropGrowControlBuilder::humidList),
            CodecUtil.holderSetCodec(ESRegistries.AGRO_CLIMATE).fieldOf("climate").forGetter(CropGrowControlBuilder::cropClimateType),
            BlockPredicate.CODEC.optionalFieldOf("unlike_greenhouse_material").forGetter(CropGrowControlBuilder::notGreenHouse),
            CodecUtil.holderSetCodec(ESRegistries.CROP).fieldOf("parent").orElse(HolderSet.empty()).forGetter(CropGrowControlBuilder::parent),
            GrowParameter.CODEC.optionalFieldOf("season_default").forGetter(CropGrowControlBuilder::defaultSolarTermGrowParameter),
            BlockPredicate.CODEC.fieldOf("apply_target").forGetter(CropGrowControlBuilder::applyTarget)
    ).apply(ins, (defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, holders,notGreenHouse,  holders2, defaultGrowParameter, blockPredicate) ->
            new CropGrowControlBuilder(holders, blockPredicate, holders2, defaultGrowParameter, defaultGrowParameter2, solarTermGrowParameterEnumMap, seasonGrowParameterEnumMap, humidityGrowParameterEnumMap, notGreenHouse)
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
