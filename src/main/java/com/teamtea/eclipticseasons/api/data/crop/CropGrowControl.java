package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.misc.util.Mergable;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;

public record CropGrowControl(
        CropGrow base,
        Optional<IdentityHashMap<BlockState, CropGrow>> blocks,
        Optional<List<Pair<BlockPredicate, CropGrow>>> entities,
        Optional<BlockPredicate> notGreenHouse
) implements Mergable<CropGrowControl> {

    public @NonNull CropGrow getCropGrow(@Nullable BlockState state) {
        if (state == null || blocks().isEmpty() || blocks().get().isEmpty()) {
            return this.base();
        }
        return blocks().map(m -> m.getOrDefault(state, this.base()))
                .orElse(this.base());
    }


    @Deprecated(forRemoval = true, since = "0.12")
    @ApiStatus.Internal
    public GrowParameter getGrowParameter(SolarTerm solarTerm) {
        return getGrowParameter(solarTerm, null);
    }

    @Deprecated(forRemoval = true, since = "0.12")
    @ApiStatus.Internal
    public GrowParameter getGrowParameter(Season season) {
        return getGrowParameter(season, null);
    }

    @Deprecated(forRemoval = true, since = "0.12")
    @ApiStatus.Internal
    public GrowParameter getGrowParameter(Humidity env) {
        return getGrowParameter(env, null);
    }

    @ApiStatus.Internal
    public GrowParameter getGrowParameter(SolarTerm solarTerm, BlockState state) {
        CropGrow cropGrow = getCropGrow(state);
        GrowParameter growParameter = cropGrow.solarTermsMap().getOrDefault(solarTerm, null);
        if (growParameter == null) {
            growParameter = cropGrow.seasonMap().getOrDefault(solarTerm.getSeason(), null);
        }
        if (growParameter == null) {
            growParameter = cropGrow.growParameter().orElse(null);
        }
        return growParameter;
    }

    @ApiStatus.Internal
    public GrowParameter getGrowParameter(Season season, BlockState state) {
        CropGrow cropGrow = getCropGrow(state);
        GrowParameter growParameter = cropGrow.seasonMap().getOrDefault(season, null);
        if (growParameter == null) {
            // if season is none, there only have none solar term
            if (season == Season.NONE) return cropGrow.solarTermsMap().getOrDefault(SolarTerm.NONE, null);
            float a_chance = 0;
            float b_chance = 0;
            float c_chance = 0;
            int ordinal = season.ordinal();
            boolean any = false;
            for (int l = ordinal * 6; l < ordinal * 6 + 6; l++) {
                GrowParameter termParameter = cropGrow.solarTermsMap().getOrDefault(SolarTerm.collectValues()[l], null);
                if (termParameter != null) {
                    a_chance += termParameter.grow_chance();
                    b_chance += termParameter.fertile_chance();
                    c_chance += termParameter.death_chance();
                    any = true;
                }
            }
            if (any) {
                growParameter = GrowParameter.builder()
                        .growChance(a_chance / 6f)
                        .fertileChance(b_chance / 6f)
                        .deathChance(c_chance / 6f)
                        .end();
            }
        }
        return growParameter;
    }

    @ApiStatus.Internal
    public GrowParameter getGrowParameter(Humidity env, BlockState state) {
        CropGrow cropGrow = getCropGrow(state);
        GrowParameter growParameter = cropGrow.humidMap().getOrDefault(env, null);
        if (growParameter == null) {
            growParameter = cropGrow.growParameter2().orElse(null);
        }
        return growParameter;
    }

    @ApiStatus.Internal
    public GrowParameter getGrowParameter(float env, BlockState state) {
        Humidity.Environment environment = Humidity.getEnvironment(env);
        List<Humidity.Composition> compositions = environment.compositions();
        CropGrow cropGrow = getCropGrow(state);
        GrowParameter growParameter = cropGrow.humidMap().getOrDefault(environment.base(), null);
        if (growParameter == null) {
            growParameter = cropGrow.growParameter2().orElse(null);
        } else xx:
                if (compositions.size() > 1) {
                    float growChance = 0;
                    float fertileChance = 0;
                    float deathChance = 0;
                    for (Humidity.Composition composition : compositions) {
                        GrowParameter g = cropGrow.humidMap().getOrDefault(composition.humidity(), null);
                        if (g == null) break xx;
                        else {
                            growChance += g.grow_chance() * composition.percent();
                            fertileChance += g.fertile_chance() * composition.percent();
                            deathChance += g.death_chance() * composition.percent();
                        }
                    }
                    growParameter = GrowParameter.builder()
                            .growChance(growChance)
                            .fertileChance(fertileChance)
                            .deathChance(deathChance)
                            .deadState(growParameter.deadState())
                            .end();
                }
        return growParameter;
    }

    @Override
    public CropGrowControl merge(CropGrowControl next) {
        CropGrowControl control = this;
        control.base().solarTermsMap().putAll(next.base().solarTermsMap());
        control.base().seasonMap().putAll(next.base().seasonMap());
        control.base().humidMap().putAll(next.base().humidMap());

        if (control.blocks().isPresent()
                && next.blocks().isPresent()) {
            control.blocks().get().putAll(next.blocks().get());
        } else if (control.blocks().isEmpty()
                && next.blocks().isPresent()) {
            control = new CropGrowControl(control.base(), next.blocks(), control.entities(), control.notGreenHouse());
        }

        if (next.entities().isPresent()) {
            List<Pair<BlockPredicate, CropGrow>> arrayList = new ArrayList<>(control.entities().orElse(new ArrayList<>()));
            arrayList.addAll(next.entities().get());
            control = new CropGrowControl(control.base(), control.blocks(), Optional.of(arrayList), control.notGreenHouse());
        }

        if (control.notGreenHouse().isEmpty()
                && next.notGreenHouse().isPresent()) {
            control = new CropGrowControl(control.base(), control.blocks(), control.entities(), next.notGreenHouse());
        }
        return control;
    }
}
