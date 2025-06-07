package com.teamtea.eclipticseasons.api.data.crop;

import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import com.teamtea.eclipticseasons.api.data.season.BiomeSet;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;

public record SeasonalBlock(
        BlockPredicate blockPredicate,
        Holder<BiomeSet> biomeSetHolder,
        SolarTermValueMap<BlockState> blockStateSolarTermValueMap
) {

}
