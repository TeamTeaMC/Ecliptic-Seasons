package com.teamtea.eclipticseasons.api;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ESApiLegacy {

    EclipticSeasonsApi asSelf();

    @Deprecated(forRemoval = true, since = "0.14.0")
    default Season getAgroSeason(Level level, BlockPos pos) {
        return asSelf().getSeasonSignal(level, pos);
    }

    @Deprecated(forRemoval = true, since = "0.14.0")
    default int getSolarYears(Level level) {
        return asSelf().getSolarYear(level);
    }

    @Deprecated
    default int getTimeInTerm(Level level) {
        return asSelf().getDayInTerm(level);
    }
}
