package com.teamtea.eclipticseasons.api.data.season;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.List;


@Deprecated(forRemoval = true)
public record SnowTermHolder(
        Holder<BiomeSet> biomes,
        SolarTerm startSnow,
        SolarTerm endSnow
) {


}
