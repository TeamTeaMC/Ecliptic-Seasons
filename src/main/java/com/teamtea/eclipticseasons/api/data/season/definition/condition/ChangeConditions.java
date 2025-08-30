package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ChangeConditions {

    public static final Map<ResourceLocation, MapCodec<? extends IChangeCondition>> CONDITIONS = new HashMap<>();
    public static final ResourceLocation EMPTY_ABOVE = EclipticSeasons.rl("empty_above");
    public static final ResourceLocation PRECIPITATION = EclipticSeasons.rl("precipitation");
    public static final ResourceLocation TIME_PERIOD = EclipticSeasons.rl("time_period");

    static {
        CONDITIONS.put(EMPTY_ABOVE, EmptyAboveCondition.CODEC);
        CONDITIONS.put(PRECIPITATION, PrecipitationCondition.CODEC);
        CONDITIONS.put(TIME_PERIOD, TimePeriodCondition.CODEC);
    }
}
