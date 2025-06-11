package com.teamtea.eclipticseasons.api;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.ApiStatus;

/**
 * This API code exists for other mods to query the solar term status or other situations.
 * Please try not to use other internal APIs directly, as they are likely to change.
 */
public interface EclipticSeasonsApi {

    String MODID = "eclipticseasons";
    String SMODID = "ecliptic";

    /**
     * Use this static method to get an API instance.
     */
    static EclipticSeasonsApi getInstance() {
        return EclipticUtil.INSTANCE;
    }

    /**
     * Get the solar term.
     * Or use it to get the season{@link SolarTerm#getSeason()},
     * or get the climate classification of the biome{@link SolarTerm#getBiomeRain(Holder)},
     * and which solar terms of the biome snow{@link SolarTerm#getSnowTerm(Biome)}.
     *
     * <p>Only dimensions marked as {@linkplain DimensionType#natural()  natural} have solar term changes.</p>
     *
     */
    SolarTerm getSolarTerm(Level level);

    boolean isDay(Level level);

    boolean isNight(Level level);

    /**
     * The nighttime is used to process the time command.
     * It is also used as a time to distinguish between day and night.
     * After this time, the player can fall asleep quickly.
     */
    int getNightTime(Level level);

    /**
     * Determine if it is noon, a few hours around tick 6000.
     */
    boolean isNoon(Level level);

    /**
     * Judging whether it is evening now, it will not last until deep into midnight.
     */
    boolean isEvening(Level level);

    /**
     * Checks if the surface should be snowy.
     * Note that the position may be off {@linkplain tip if the snow cover is not high enough},
     * but will not be miscalculated if the surface is fully snow covered or not covered.
     */
    @Deprecated
    boolean isSnowySurfaceAt(Level level, BlockPos pos);

    /**
     * Checks if the block at the pos should be snowy.
     */
    boolean isSnowyBlock(Level level, BlockState state, BlockPos pos);

    boolean isRainOrSnowAt(Level level,BlockPos pos);

    boolean isRainAt(Level level,BlockPos pos);

    boolean isSnowAt(Level level,BlockPos pos);

    boolean isThunderAt(Level level,BlockPos pos);

    Biome.Precipitation getPrecipitationAt(Level level, BlockPos pos);

    /**
     * Roughly checks whether the surface biome or level has weather conditions, ignoring exact position.
     */
    boolean isRainingOrSnowing(Level level, BlockPos pos);

    /**
     * Roughly checks whether it is thundering in the given level, ignoring exact position.
     */
    boolean isThundering(Level level, BlockPos pos);

    /**
     * Gets the base humidity at the given position,
     * based on biome, season, and elevation.
     */
    Humidity getBaseHumidity(Level level, BlockPos pos);

    /**
     * Gets the final humidity at the given position,
     * including effects like greenhouses or other modifiers.
     * This value is more volatile and may fluctuate frequently.
     */
    @ApiStatus.Experimental
    Humidity getAdjustedHumidity(ServerLevel level, BlockPos pos);
}
