package com.teamtea.eclipticseasons.api.data.crop;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.fast.Enum2ObjectMap;

import java.util.EnumMap;
import java.util.Optional;


// 这些都应该提前填充好，如果查不到就是没有
// todo 后续填充的时候应该使用Climate参数
public record CropGrow(
        Optional<GrowParameter> growParameter,
        Optional<GrowParameter> growParameter2,
        Enum2ObjectMap<SolarTerm, GrowParameter> solarTermsMap,
        Enum2ObjectMap<Season, GrowParameter> seasonMap,
        Enum2ObjectMap<Humidity, GrowParameter> humidMap) {

    public static final CropGrow EMPTY = new CropGrow(
            Optional.empty(), Optional.empty(),
            new Enum2ObjectMap<>(SolarTerm.class), new Enum2ObjectMap<>(Season.class), new Enum2ObjectMap<>(Humidity.class)
    );
}
