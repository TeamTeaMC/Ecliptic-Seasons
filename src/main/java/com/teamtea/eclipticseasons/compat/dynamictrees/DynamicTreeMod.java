package com.teamtea.eclipticseasons.compat.dynamictrees;

import com.ferreusveritas.dynamictrees.compat.CompatHandler;
import com.ferreusveritas.dynamictrees.compat.season.ActiveSeasonGrowthCalculator;
import com.ferreusveritas.dynamictrees.compat.season.NormalSeasonManager;
import com.ferreusveritas.dynamictrees.compat.season.NullSeasonGrowthCalculator;
import com.ferreusveritas.dynamictrees.compat.season.NullSeasonProvider;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.Tags;

public class DynamicTreeMod {
    public static void init() {
        CompatHandler.registerSeasonManager(EclipticSeasons.MODID, () -> {
            NormalSeasonManager seasonManager = new NormalSeasonManager(
                    level -> MapChecker.isValidDimension(level) ?
                            new Tuple<>(new EclipticSeasonProvider(), new ActiveSeasonGrowthCalculator()) :
                            new Tuple<>(new NullSeasonProvider(), new NullSeasonGrowthCalculator())
            );
            seasonManager.setTropicalPredicate((world, pos) -> world.getBiome(pos).is(Tags.Biomes.IS_HOT_OVERWORLD));
            return seasonManager;
        });
    }
}
