package com.teamtea.eclipticseasons.api.data.crop;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;

import java.util.EnumMap;
import java.util.Optional;

// TODO:这里是不是用array查表更快
// 这些都应该提前填充好，如果查不到就是没有
public record CropGrow(
        Optional<GrowParameter> growParameter,
        Optional<GrowParameter>  growParameter2,
        EnumMap<SolarTerm, GrowParameter> solarTermsMap,
        EnumMap<Season, GrowParameter> seasonMap,
        EnumMap<Humidity, GrowParameter> humidMap) {


}
