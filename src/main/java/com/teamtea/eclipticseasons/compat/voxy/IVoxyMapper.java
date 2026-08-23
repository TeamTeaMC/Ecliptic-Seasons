package com.teamtea.eclipticseasons.compat.voxy;

import me.cortex.voxy.common.world.other.Mapper;
import org.jspecify.annotations.Nullable;

public interface IVoxyMapper {
    Mapper.@Nullable BiomeEntry eclipticseasons$getBiomeEntry(int biomeId);
}
