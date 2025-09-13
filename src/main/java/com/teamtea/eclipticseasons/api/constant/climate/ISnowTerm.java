package com.teamtea.eclipticseasons.api.constant.climate;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.ApiStatus;

public interface ISnowTerm {

    SolarTerm getStart();

    SolarTerm getEnd();

    @ApiStatus.Internal
    default ISnowTerm cast(float tempChange) {
        return this;
    }

    default boolean maySnow(SolarTerm solarTerm) {
        return solarTerm.isInTerms(getStart(), getEnd());
    }

    default boolean maySnow(SolarTerm solarTerm, Biome biome, BlockPos pos, boolean isServer) {
        if (MapChecker.isAboveSnowLine(biome, pos.getY(), isServer)) return true;
        return maySnow(solarTerm);
    }
}
