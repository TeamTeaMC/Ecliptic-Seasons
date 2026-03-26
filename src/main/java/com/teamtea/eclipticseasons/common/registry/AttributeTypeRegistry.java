package com.teamtea.eclipticseasons.common.registry;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.LerpFunction;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AttributeTypeRegistry {
    public static final DeferredRegister<AttributeType<?>> ATTRIBUTE_TYPES
            = DeferredRegister.create(Registries.ATTRIBUTE_TYPE, EclipticSeasonsApi.MODID);

    public static final AttributeType<SolarTerm> SOLAR_TERM_TYPE =
            AttributeType.ofNotInterpolated(ESExtraCodec.SOLAR_TERM);

    public static final AttributeType<Season> SEASON_TYPE =
            AttributeType.ofNotInterpolated(ESExtraCodec.SEASON);

    public static final AttributeType<TimePeriod> TIME_PERIOD_TYPE =
            AttributeType.ofNotInterpolated(ESExtraCodec.TIME_PERIOD);

    public static final AttributeType<Float> HUMIDITY_TYPE =
            AttributeType.ofInterpolated(
                    Codec.FLOAT,
                    AttributeModifier.FLOAT_LIBRARY,
                    LerpFunction.ofFloat()
            );

    public static final AttributeType<Float> RAINFALL_TYPE =
            AttributeType.ofInterpolated(
                    Codec.FLOAT,
                    AttributeModifier.FLOAT_LIBRARY,
                    LerpFunction.ofFloat()
            );

    public static final AttributeType<Float> TEMPERATURE_TYPE =
            AttributeType.ofInterpolated(
                    Codec.FLOAT,
                    AttributeModifier.FLOAT_LIBRARY,
                    LerpFunction.ofFloat()
            );

    private static final DeferredHolder<AttributeType<?>, AttributeType<SolarTerm>> SOLAR_TERM =
            ATTRIBUTE_TYPES.register("solar_term", () -> SOLAR_TERM_TYPE);

    private static final DeferredHolder<AttributeType<?>, AttributeType<Season>> SEASON =
            ATTRIBUTE_TYPES.register("season", () -> SEASON_TYPE);

    private static final DeferredHolder<AttributeType<?>, AttributeType<TimePeriod>> TIME_PERIOD =
            ATTRIBUTE_TYPES.register("time_period", () -> TIME_PERIOD_TYPE);

    private static final DeferredHolder<AttributeType<?>, AttributeType<Float>> HUMIDITY =
            ATTRIBUTE_TYPES.register("humidity", () -> HUMIDITY_TYPE);

    private static final DeferredHolder<AttributeType<?>, AttributeType<Float>> RAINFALL =
            ATTRIBUTE_TYPES.register("rainfall", () -> RAINFALL_TYPE);

    private static final DeferredHolder<AttributeType<?>, AttributeType<Float>> TEMPERATURE =
            ATTRIBUTE_TYPES.register("temperature", () -> TEMPERATURE_TYPE);
}
