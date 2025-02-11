package com.teamtea.eclipticseasons.compat.dynamictrees;

import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;


public class EclipticSeasonProvider implements com.ferreusveritas.dynamictrees.compat.seasons.SeasonProvider {

    private float seasonValue = 1.0f;

    @Override
    public Float getSeasonValue(World level, BlockPos pos) {
        return seasonValue;
    }

    @Override
    public void updateTick(World level, long dayTime) {
        LazyOptional<SolarDataManager> solarDataManager = SolarHolders.getSaveDataLazy(level);
        if (solarDataManager.resolve().isPresent()) {
            seasonValue = solarDataManager.resolve().get().getSolarTermsDay() / (6f * CommonConfig.Season.lastingDaysOfEachTerm.get());
        }
    }

    @Override
    public boolean shouldSnowMelt(World level, BlockPos pos) {
        return false;
    }
}
