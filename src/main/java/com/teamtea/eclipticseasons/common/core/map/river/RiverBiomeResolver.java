package com.teamtea.eclipticseasons.common.core.map.river;

import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.RandomState;
import org.jspecify.annotations.NonNull;

public class RiverBiomeResolver {
    // net.minecraft.world.level.biome.OverworldBiomeBuilder MIDDLE_BIOMES
    private static final ResourceKey<Biome>[][] CLIMATE_BIOMES = new ResourceKey[][]{
            {
                    Biomes.SNOWY_PLAINS,
                    Biomes.SNOWY_PLAINS,
                    Biomes.SNOWY_PLAINS,
                    Biomes.SNOWY_TAIGA,
                    Biomes.TAIGA
            },
            {
                    Biomes.PLAINS,
                    Biomes.PLAINS,
                    Biomes.FOREST,
                    Biomes.TAIGA,
                    Biomes.OLD_GROWTH_SPRUCE_TAIGA
            },
            {
                    Biomes.FLOWER_FOREST,
                    Biomes.PLAINS,
                    Biomes.FOREST,
                    Biomes.BIRCH_FOREST,
                    Biomes.DARK_FOREST
            },
            {
                    Biomes.SAVANNA,
                    Biomes.SAVANNA,
                    Biomes.FOREST,
                    Biomes.JUNGLE,
                    Biomes.JUNGLE
            },
            {
                    Biomes.DESERT,
                    Biomes.DESERT,
                    Biomes.DESERT,
                    Biomes.DESERT,
                    Biomes.DESERT
            }
    };


    public static ResourceKey<Biome> getClimateBiome(Climate.TargetPoint point) {
        float temperature = Climate.unquantizeCoord(point.temperature());
        float humidity = Climate.unquantizeCoord(point.humidity());

        int tempIndex = getTemperatureIndex(temperature);
        int humidityIndex = getHumidityIndex(humidity);

        return CLIMATE_BIOMES[tempIndex][humidityIndex];
    }

    // net.minecraft.world.level.biome.OverworldBiomeBuilder temperatures
    private static int getTemperatureIndex(float value) {
        if (value < -0.45F) return 0;
        if (value < -0.15F) return 1;
        if (value < 0.2F) return 2;
        if (value < 0.55F) return 3;
        return 4;
    }

    private static int getHumidityIndex(float value) {
        if (value < -0.35F) return 0;
        if (value < -0.1F) return 1;
        if (value < 0.1F) return 2;
        if (value < 0.3F) return 3;
        return 4;
    }


    // public static ResourceKey<Biome> getClimateTemperature(Climate.TargetPoint point) {
    //     float temperature = Climate.unquantizeCoord(point.temperature());
    //
    //     ResourceKey<Biome> biome;
    //     // net.minecraft.world.level.biome.OverworldBiomeBuilder temperatures
    //     if (temperature < -0.45F) {
    //         biome = Biomes.SNOWY_PLAINS;
    //     } else if (temperature < -0.15F) {
    //         biome = Biomes.TAIGA;
    //     } else if (temperature < 0.2F) {
    //         biome = Biomes.PLAINS;
    //     } else if (temperature < 0.55F) {
    //         biome = Biomes.SAVANNA;
    //     } else {
    //         biome = Biomes.DESERT;
    //     }
    //     return biome;
    // }


    /**
     * <p>Use the vanilla Climate.Sampler sampling pipeline instead of directly
     * evaluating DensityFunctions.
     *
     * <p>Climate.Sampler#sample is the entry point used by Minecraft for climate
     * sampling and may be modified by world-generation or climate-related mods.
     * Directly calling sampler.temperature().compute() and other DensityFunctions
     * bypasses these modifications and can cause compatibility issues.
     */
    public static Climate.@NonNull TargetPoint getClimateTargetPoint(RandomState randomState, BlockPos.MutableBlockPos blockPos) {
        int qx = QuartPos.fromBlock(blockPos.getX());
        int qy = QuartPos.fromBlock(blockPos.getY());
        int qz = QuartPos.fromBlock(blockPos.getZ());
        Climate.Sampler sampler = randomState.sampler();
        return sampler.sample(qx, qy, qz);
        // DensityFunction.SinglePointContext densityfunction$singlepointcontext = new DensityFunction.SinglePointContext(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        // return Climate.target(
        //         (float) sampler.temperature().compute(densityfunction$singlepointcontext),
        //         (float) sampler.humidity().compute(densityfunction$singlepointcontext),
        //         (float) sampler.continentalness().compute(densityfunction$singlepointcontext),
        //         (float) sampler.erosion().compute(densityfunction$singlepointcontext),
        //         (float) sampler.depth().compute(densityfunction$singlepointcontext),
        //         (float) sampler.weirdness().compute(densityfunction$singlepointcontext)
        // );
    }
}
