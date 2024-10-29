package com.teamtea.eclipticseasons.api;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

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
    boolean isSnowySurfaceAt(Level level, BlockPos pos);
}
