package com.teamtea.eclipticseasons.common.environment;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.TimePeriod;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.api.util.WeatherUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarAngelHelper;
import com.teamtea.eclipticseasons.common.registry.EnvironmentAttributeRegistry;
import jdk.jfr.Experimental;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Experimental
public class SolarTime {
    public static void attachSolarLayer(Level level, EnvironmentAttributeSystem.Builder environmentAttributes) {
        // TexTime.attachSolarLayer(level, environmentAttributes);

        environmentAttributes.addTimeBasedLayer(EnvironmentAttributeRegistry.SOLAR_TERM_ATTRIBUTE, (solarTerm, cacheTickId) ->
                EclipticSeasonsApi.getInstance().getSolarTerm(level));

        environmentAttributes.addPositionalLayer(EnvironmentAttributeRegistry.SEASON_ATTRIBUTE, (baseValue, pos, biomeInterpolator) ->
                EclipticSeasonsApi.getInstance().getAgroSeason(level, BlockPos.containing(pos)));


        environmentAttributes.addPositionalLayer(EnvironmentAttributeRegistry.HUMIDITY_ATTRIBUTE, (baseValue, pos, biomeInterpolator) ->
                EclipticUtil.getHumidityLevelAt(level, BlockPos.containing(pos)));


        environmentAttributes.addTimeBasedLayer(
                EnvironmentAttributeRegistry.TIME_PERIOD_ATTRIBUTE, new EnvironmentAttributeLayer.TimeBased<TimePeriod>() {
                    @Override
                    public TimePeriod applyTimeBased(TimePeriod skyFactor, int cacheTickId) {
                        return TimePeriod.fromTimeOfDay(SimpleUtil.getTimeOfDay(level));
                    }
                }
        );

        ClientCon.getAgent().attachEnvironment(level, environmentAttributes);

        // environmentAttributes.addPositionalLayer(EnvironmentAttributes.BEES_STAY_IN_HIVE, (baseValue, pos, biomeInterpolator) ->
        //         EclipticSeasonsApi.getInstance().isNight(level)
        //                 || WeatherUtil.isBlockInRainOrSnow(level, BlockPos.containing(pos)));
    }


    public static long getTotalTicks(Holder<WorldClock> clock, long totalTicks, long loopTicks) {
        if (loopTicks <= 0) return totalTicks;
        try {
            // loopTicks = 24000;
            // float percent = TexTime.getSeasonalDayTick(clock, totalTicks);
            // totalTicks = (long) (loopTicks * ((int) (totalTicks / loopTicks) + percent));
            int solarAngelTime = SolarAngelHelper.getSolarAngelTime(clock, totalTicks, loopTicks);
            totalTicks = (loopTicks * ((int) (totalTicks / loopTicks))) + solarAngelTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalTicks;
    }

    public static void updateTimeMarks(Level level) {
        ClockManager clockManager = level.clockManager();
        if (clockManager instanceof ServerClockManager serverClockManager) {
            Optional<Holder<WorldClock>> worldClockHolder = level.dimensionType().defaultClock();
            if (worldClockHolder.isEmpty()) return;
            if (!MapChecker.isValidDimension(level)) return;
            Optional<Holder.Reference<Timeline>> timelineReference = level.registryAccess().lookupOrThrow(Registries.TIMELINE)
                    .get(Timelines.OVERWORLD_DAY);
            if (timelineReference.isEmpty()) return;
            Timeline timeline = timelineReference.get().value();

            Holder<WorldClock> clock = worldClockHolder.get();
            int dayLengthInMinecraft = EclipticUtil.getDayLengthInMinecraft(level);
            ServerClockManager.ClockInstance instance = serverClockManager.getInstance(clock);
            Map<ResourceKey<ClockTimeMarker>, ClockTimeMarker> timeMarkers = instance.timeMarkers;
            HashSet<Map.Entry<ResourceKey<ClockTimeMarker>, ClockTimeMarker>> entries = new HashSet<>(timeMarkers.entrySet());
            for (Map.Entry<ResourceKey<ClockTimeMarker>, ClockTimeMarker> entry : entries) {
                ResourceKey<ClockTimeMarker> key = entry.getKey();
                ClockTimeMarker value = entry.getValue();
                int periodTicks = value.periodTicks().orElse(0);
                if (periodTicks == dayLengthInMinecraft) {
                    Timeline.TimeMarkerInfo timeMarkerInfo = timeline.timeMarkers.get(key);
                    if (timeMarkerInfo == null) continue;
                    // int ticks = SolarAngelHelper.getSolarAngelTime(clock, value.ticks(), periodTicks);
                    int ticks = SolarAngelHelper.getDayTimeFromSolarAngelTime(clock, timeMarkerInfo.ticks(), periodTicks, key);
                    ClockTimeMarker clockTimeMarker = new ClockTimeMarker(value.clock(), ticks, value.periodTicks(), value.showInCommands());
                    timeMarkers.put(key, clockTimeMarker);
                }
            }
        }
    }
}
