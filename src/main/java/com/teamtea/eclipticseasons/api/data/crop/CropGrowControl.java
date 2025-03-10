package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;

public record CropGrowControl(
        CropGrow base,
        Optional<IdentityHashMap<BlockState, CropGrow>> blocks,
        Optional<List<Pair<BlockPredicate, CropGrow>>> entities
) {


    @ApiStatus.Internal
    public GrowParameter getGrowParameter(SolarTerm solarTerm) {
        GrowParameter growParameter = base().solarTermsMap().getOrDefault(solarTerm, null);
        if (growParameter == null) {
            growParameter = base().seasonMap().getOrDefault(solarTerm.getSeason(), null);
        }
        if (growParameter == null) {
            growParameter = base().growParameter().orElse(null);
        }
        return growParameter;
    }

    @ApiStatus.Internal
    public GrowParameter getGrowParameter(Season season) {
        GrowParameter growParameter = base().seasonMap().getOrDefault(season, null);
        if (growParameter == null) {
            if (season == Season.NONE) return base().solarTermsMap().getOrDefault(SolarTerm.NONE, null);
            float a_chance = 0;
            float b_chance = 0;
            float c_chance = 0;
            int ordinal = season.ordinal();
            for (int l = ordinal * 6; l < ordinal * 6 + 6; l++) {
                GrowParameter termParameter = base().solarTermsMap().getOrDefault(SolarTerm.collectValues()[l], null);
                if (termParameter != null) {
                    a_chance += termParameter.grow_chance();
                    b_chance += termParameter.fertile_chance();
                    c_chance += termParameter.death_chance();
                }
            }
            growParameter = GrowParameter.builder()
                    .growChance(a_chance / 6f)
                    .fertileChance(b_chance / 6f)
                    .deathChance(c_chance / 6f)
                    .end();
        }
        return growParameter;
    }

    @ApiStatus.Internal
    public GrowParameter getGrowParameter(Humidity env) {
        GrowParameter growParameter = base().humidMap().getOrDefault(env, null);
        if (growParameter == null) {
            growParameter = base().growParameter2().orElse(null);
        }
        return growParameter;
    }
}
