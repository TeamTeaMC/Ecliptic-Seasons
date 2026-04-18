package com.teamtea.eclipticseasons.api;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
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

import java.util.List;

/**
 * This API code exists for other mods to query the solar term status or other situations.
 * Please try not to use other internal APIs directly, as they are likely to change.
 * <p>
 * Another reason is that this API implements automatic switching based on the configuration.
 * If you use the API directly, it's easy to run under an incorrect configuration.
 */
public interface EclipticSeasonsApi {

    String MODID = "eclipticseasons";
    String SMODID = "ecliptic";

    List<String> MODID_LIST = List.of(EclipticSeasonsApi.SMODID, EclipticSeasonsApi.MODID, "season");

    /**
     * Use this static method to get an API instance.
     */
    static EclipticSeasonsApi getInstance() {
        return EclipticUtil.INSTANCE;
    }

    /**
     * Get the solar term.
     * Or use it to get the season{@link SolarTerm#getSeason()},
     * or get the climate classification of the biome{@link com.teamtea.eclipticseasons.api.util.SolarUtil#getBiomeRain(SolarTerm, Holder)},
     * and which solar terms of the biome snow{@link SolarTerm#getSnowTerm(Biome)}.
     *
     * <p>Only dimensions marked as {@linkplain com.teamtea.eclipticseasons.config.CommonConfig.Season#validDimensions} have solar term changes.</p>
     */
    SolarTerm getSolarTerm(Level level);

    /**
     * Returns the localized (Agro) season at the given position.
     * Unlike the global season, this considers regional climate.
     */
    Season getAgroSeason(Level level, BlockPos pos);

    int getSolarDays(Level level);

    int getSolarYears(Level level);

    int getLastingDaysOfEachTerm(Level level);

    /**
     * Day index within the current solar term, from 0 to (lastingDays - 1).
     */
    int getTimeInTerm(Level level);

    /**
     * Checks whether the seasonal system is enabled for the given level.
     */
    boolean isSeasonEnabled(Level level);

    boolean hasLocalWeather(Level level);

    /**
     * Returns the adjusted daytime in ticks for the given level, as an API version of {@link SolarTerm#getDayTime()},
     * taking into account seasonal variations in day length.
     */
    long getDayTime(Level level);

    @Deprecated
    boolean isDay(Level level);

    @Deprecated
    boolean isNight(Level level);

    /**
     * The nighttime is used to process the time command.
     * It is also used as a time to distinguish between day and night.
     * After this time, the player can fall asleep quickly.
     */
    @Deprecated
    int getNightTime(Level level);

    /**
     * Determine if it is noon, a few hours around tick 6000.
     */
    @Deprecated
    boolean isNoon(Level level);

    /**
     * Judging whether it is evening now, it will not last until deep into midnight.
     */
    @Deprecated
    boolean isEvening(Level level);

    /**
     * Checks if the surface should be snowy.
     * Note that the id may be off {@linkplain tip if the snow cover is not high enough},
     * but will not be miscalculated if the surface is fully snow covered or not covered.
     */
    @Deprecated
    boolean isSnowySurfaceAt(Level level, BlockPos pos);

    /**
     * Checks if the block at the pos should be snowy.
     */
    boolean isSnowyBlock(Level level, BlockState state, BlockPos pos);

    boolean isRainOrSnowAt(Level level, BlockPos pos);

    boolean isRainAt(Level level, BlockPos pos);

    boolean isSnowAt(Level level, BlockPos pos);

    boolean isThunderAt(Level level, BlockPos pos);

    /**
     * Gets the precipitation type at the surface climate biome over a period of time.
     */
    Biome.Precipitation getPrecipitationAt(Level level, BlockPos pos);

    /**
     * Gets the current precipitation type at the surface climate biome (i.e., at the current moment).
     */
    Biome.Precipitation getCurrentPrecipitationAt(Level level, BlockPos pos);

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
