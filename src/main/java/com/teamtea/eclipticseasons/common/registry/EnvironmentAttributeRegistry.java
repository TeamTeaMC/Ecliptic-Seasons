package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeTypes;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EnvironmentAttributeRegistry {
    public static final DeferredRegister<EnvironmentAttribute<?>> ENVIRONMENT_ATTRIBUTES
            = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, EclipticSeasonsApi.MODID);

    public static final EnvironmentAttribute<SolarTerm> SOLAR_TERM_ATTRIBUTE =
            EnvironmentAttribute.builder(AttributeTypeRegistry.SOLAR_TERM_TYPE)
                    .defaultValue(SolarTerm.NONE)
                    .notPositional()
                    .syncable()
                    .build();

    public static final EnvironmentAttribute<Season> SEASON_ATTRIBUTE =
            EnvironmentAttribute.builder(AttributeTypeRegistry.SEASON_TYPE)
                    .defaultValue(Season.NONE)
                    .notPositional()
                    .syncable()
                    .build();

    public static final EnvironmentAttribute<TimePeriod> TIME_PERIOD_ATTRIBUTE =
            EnvironmentAttribute.builder(AttributeTypeRegistry.TIME_PERIOD_TYPE)
                    .defaultValue(TimePeriod.NONE)
                    .syncable()
                    .notPositional()
                    .build();

    public static final EnvironmentAttribute<Float> HUMIDITY_ATTRIBUTE =
            EnvironmentAttribute.builder(AttributeTypes.FLOAT)
                    .defaultValue(0f)
                    .build();

    public static final EnvironmentAttribute<Boolean> SEASONAL_WORLD_ATTRIBUTE =
            EnvironmentAttribute.builder(AttributeTypes.BOOLEAN)
                    .defaultValue(false)
                    .syncable()
                    .notPositional()
                    .build();

    public static final EnvironmentAttribute<Integer> SOLAR_DAY_ATTRIBUTE =
            EnvironmentAttribute.builder(AttributeTypes.INTEGER)
                    .defaultValue(0)
                    .syncable()
                    .notPositional()
                    .build();

    private static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<SolarTerm>> SOLAR_TERM =
            ENVIRONMENT_ATTRIBUTES.register("solar_term", () -> SOLAR_TERM_ATTRIBUTE);

    private static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Season>> SEASON =
            ENVIRONMENT_ATTRIBUTES.register("season", () -> SEASON_ATTRIBUTE);

    private static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<TimePeriod>> TIME_PERIOD =
            ENVIRONMENT_ATTRIBUTES.register("time_period", () -> TIME_PERIOD_ATTRIBUTE);

    private static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Float>> HUMIDITY =
            ENVIRONMENT_ATTRIBUTES.register("humidity", () -> HUMIDITY_ATTRIBUTE);

    private static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Boolean>> SEASONAL_WORLD =
            ENVIRONMENT_ATTRIBUTES.register("seasonal_world", () -> SEASONAL_WORLD_ATTRIBUTE);

    private static final DeferredHolder<EnvironmentAttribute<?>, EnvironmentAttribute<Integer>> SOLAR_DAY =
            ENVIRONMENT_ATTRIBUTES.register("solar_day", () -> SOLAR_DAY_ATTRIBUTE);
}
