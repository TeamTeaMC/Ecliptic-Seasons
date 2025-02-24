package com.teamtea.eclipticseasons.api;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

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
     * or get the climate classification of the biome{@link SolarTerm#getSnowTerm(Biome)},
     * and which solar terms of the biome snow{@link SolarTerm#getSnowTerm(Biome)}.
     *
     * <p>Only dimensions marked as {@linkplain DimensionType#natural()  natural} have solar term changes.</p>
     *
     */
    SolarTerm getSolarTerm(World level);

    boolean isDay(World level);

    boolean isNight(World level);

    /**
     * The nighttime is used to process the time command.
     * It is also used as a time to distinguish between day and night.
     * After this time, the player can fall asleep quickly.
     */
    int getNightTime(World level);

    /**
     * Determine if it is noon, a few hours around tick 6000.
     */
    boolean isNoon(World level);

    /**
     * Judging whether it is evening now, it will not last until deep into midnight.
     */
    boolean isEvening(World level);

    boolean isRainOrSnowAt(World level, BlockPos pos);

    boolean isRainAt(World level,BlockPos pos);

    boolean isSnowAt(World level,BlockPos pos);

    boolean isThunderAt(World level,BlockPos pos);

    Biome.RainType getPrecipitationAt(World level, BlockPos pos);
}
