package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.advancement.ParentNeedCriterion;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import net.minecraft.advancements.CriteriaTriggers;

public class ModAdvancements {
    public static SolarTermsCriterion solarTermsCriterion = new SolarTermsCriterion(EclipticSeasons.rl("solar_terms"));
    public static SolarTermsCriterion heatStrokeCriterion = new SolarTermsCriterion(EclipticSeasons.rl("heat_stroke"));
    public static ParentNeedCriterion parentNeedCriterion = new ParentNeedCriterion(EclipticSeasons.rl("parent"));

    public static void register() {
        CriteriaTriggers.register(solarTermsCriterion);
        CriteriaTriggers.register(heatStrokeCriterion);
    }
}
