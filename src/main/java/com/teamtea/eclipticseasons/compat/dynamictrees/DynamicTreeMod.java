package com.teamtea.eclipticseasons.compat.dynamictrees;


import com.dtteam.dynamictrees.systems.season.*;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import net.minecraft.util.Tuple;
import net.neoforged.neoforge.common.Tags;

public class DynamicTreeMod {
    public static void init() {
        SeasonCompatibilityHandler.registerSeasonManager(EclipticSeasonsApi.MODID, () -> {
            NormalSeasonManager seasonManager = new NormalSeasonManager(
                    world -> world.dimensionType().natural() ?
                            new Tuple<>(new EclipticSeasonProvider(), new ActiveSeasonGrowthCalculator()) :
                            new Tuple<>(new NullSeasonProvider(), new NullSeasonGrowthCalculator())
            );
            seasonManager.setTropicalPredicate((world, pos) -> world.getBiome(pos).is(Tags.Biomes.IS_HOT_OVERWORLD));
            return seasonManager;
        });
    }
}
