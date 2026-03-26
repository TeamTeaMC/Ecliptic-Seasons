package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ChangeConditions {

    public static final Map<Identifier, MapCodec<? extends IChangeCondition>> CONDITIONS = new HashMap<>();
    public static final Identifier EMPTY_ABOVE = EclipticSeasons.rl("empty_above");
    public static final Identifier PRECIPITATION = EclipticSeasons.rl("precipitation");
    public static final Identifier TIME_PERIOD = EclipticSeasons.rl("time_period");
    public static final Identifier IS_SNOWY = EclipticSeasons.rl("is_snowy");

    static {
        CONDITIONS.put(EMPTY_ABOVE, EmptyAboveCondition.CODEC);
        CONDITIONS.put(PRECIPITATION, PrecipitationCondition.CODEC);
        CONDITIONS.put(TIME_PERIOD, TimePeriodCondition.CODEC);
        CONDITIONS.put(IS_SNOWY, SnowyBlockCondition.CODEC);
    }
}
