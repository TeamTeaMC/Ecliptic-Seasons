package com.teamtea.eclipticseasons.api.data.crop;

import com.teamtea.eclipticseasons.api.data.misc.SolarTermValueMap;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

public record SeasonalBlock(
        BlockPredicate blockPredicate,
        HolderSet<Biome> biomes,
        SolarTermValueMap<BlockState> blockStateSolarTermValueMap
) {

}
