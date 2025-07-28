package com.teamtea.eclipticseasons.api.util.codec;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import net.minecraft.util.StringRepresentable;


public class ESExtraCodec {

    public static final StringRepresentable.EnumCodec<SolarTerm> SOLAR_TERM = StringRepresentable.fromEnum(SolarTerm::collectValues);

    public static final StringRepresentable.EnumCodec<Season> SEASON = StringRepresentable.fromEnum(Season::collectValues);

    public static final StringRepresentable.EnumCodec<Humidity> HUMIDITY = StringRepresentable.fromEnum(Humidity::collectValues);

    public static final StringRepresentable.EnumCodec<TimePeriod> TIME_PERIOD = StringRepresentable.fromEnum(TimePeriod::collectValues);

}
