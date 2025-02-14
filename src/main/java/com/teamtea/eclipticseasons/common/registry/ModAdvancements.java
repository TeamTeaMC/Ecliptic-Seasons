package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModAdvancements {
    // advancement
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_DEFERRED_REGISTER = DeferredRegister.create(Registries.TRIGGER_TYPE, EclipticSeasonsApi.MODID);
    public static final Supplier<SolarTermsCriterion> heatStrokeCriterion = TRIGGER_DEFERRED_REGISTER.register("heat_stroke", SolarTermsCriterion::new);
    public static final Supplier<SolarTermsCriterion> solarTermsCriterion = TRIGGER_DEFERRED_REGISTER.register("solar_terms", SolarTermsCriterion::new);
}
