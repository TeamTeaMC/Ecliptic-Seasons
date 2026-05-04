package com.teamtea.eclipticseasons.client.sound;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.data.client.SeasonalBackgroundMusic;
import com.teamtea.eclipticseasons.api.data.season.SpecialDays;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SeasonalBackgroundMusicSelectManager {

    public static Music getMusic(Music originalMusic, BlockPos containing, Holder<Biome> biome,
                                 boolean isCreative, boolean isUnderwater) {
        Music music = null;
        Level level = ClientCon.getUseLevel();

        SolarTerm solarTerm = ClientCon.nowSolarTerm;
        Season season = ClientCon.nowSeason;
        boolean isDayNow = ClientCon.isDay;

        if (MapChecker.isValidDimension(level)) {

            boolean raining = level.isRaining();
            TimePeriod timePeriod = TimePeriod.fromTimeOfDay(level.getTimeOfDay(0));
            List<SeasonalBackgroundMusic> seasonalBackgroundMusicList = new ArrayList<>();
            for (SeasonalBackgroundMusic sound : ClientRef.musics) {
                // if (sound.isIndoor() != indoor) continue;
                if (sound.rain() != raining) continue;
                if (sound.season() != Season.NONE) {
                    if (sound.season() != season) continue;
                } else if (sound.specialDays().size() == 0) {
                    if (!solarTerm.isInTerms(sound.start(), sound.end())) continue;
                } else {
                    List<Holder<SpecialDays>> specialDays = EclipticSeasonsApi.getInstance().getSpecialDays(level, containing);
                    boolean inSpecialDays = false;
                    for (Holder<SpecialDays> specialDay : specialDays) {
                        if (sound.specialDays().contains(specialDay)) {
                            inSpecialDays = true;
                            break;
                        }
                    }
                    if (!inSpecialDays) continue;
                }
                if (!sound.ignore_time()) {
                    if (sound.timePeriod() != TimePeriod.NONE) {
                        if (sound.timePeriod() != timePeriod) continue;
                    } else {
                        if (sound.day() != isDayNow) continue;
                    }
                }
                // if (sound.isInwater() != inWater) continue;
                if (sound.biomes().size() > 0 && !sound.biomes().contains(biome)) continue;
                if (sound.ignored_biomes().contains(biome)) continue;
                // if (sound.getWeight() > 0 && level.getRandom().nextInt(sound.getSeed()) > 0) continue;
                seasonalBackgroundMusicList.add(sound);
            }
            if (seasonalBackgroundMusicList.size() > 1) {
                seasonalBackgroundMusicList.sort(Comparator.comparing(SeasonalBackgroundMusic::priority));
            }
            music = seasonalBackgroundMusicList.isEmpty() ? null :
                    seasonalBackgroundMusicList.getFirst().musicHolder()
                            .select(isCreative, isUnderwater)
                            .orElse(null);
        }
        return music != null ? music : originalMusic;
    }
}
