package com.teamtea.eclipticseasons.compat.dynamictrees;

import com.dtteam.dynamictrees.systems.season.SeasonProvider;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EclipticSeasonProvider implements SeasonProvider {

    private float seasonValue = 1.0f;

    @Override
    public Float getSeasonValue(Level level, BlockPos pos) {
        return seasonValue;
    }

    @Override
    public void updateTick(Level level, long dayTime) {
        var solarDataManager = SolarHolders.getSaveData(level);
        if (solarDataManager != null) {
            seasonValue = solarDataManager.getSolarTermsDay() / (6f * solarDataManager.getSolarTermLastingDays());
        }
    }

    @Override
    public boolean shouldSnowMelt(Level level, BlockPos pos) {
        return false;
    }
}
