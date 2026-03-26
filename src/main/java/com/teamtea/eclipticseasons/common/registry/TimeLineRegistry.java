package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.EasingType;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.timeline.Timeline;

public class TimeLineRegistry {
    public static final ResourceKey<Timeline> SOLAR_TERM_PROGRESS = createKey("solar_term_progress");
    public static final ResourceKey<Timeline> SEASON_GOING = createKey("season_going");

    private static ResourceKey<Timeline> createKey(String name) {
        return ResourceKey.create(Registries.TIMELINE, EclipticSeasons.rl(name));
    }

    public static void bootstrap(BootstrapContext<Timeline> context) {
        HolderGetter<WorldClock> clocks = context.lookup(Registries.WORLD_CLOCK);
        Holder.Reference<WorldClock> overworldClock = clocks.getOrThrow(WorldClocks.OVERWORLD);
        int dayLengthInMinecraft = EclipticUtil.getDayLengthInMinecraftStatic();
        int length = dayLengthInMinecraft * 7;
        context.register(SEASON_GOING,
                Timeline.builder(overworldClock)
                        .setPeriodTicks(length * 24)
                        .addTrack(EnvironmentAttributeRegistry.SEASONAL_WORLD_ATTRIBUTE,
                                track -> track.addKeyframe(0, true))
                        .addTrack(EnvironmentAttributeRegistry.SOLAR_DAY_ATTRIBUTE,
                                track -> {
                                    track.setEasing(EasingType.LINEAR);
                                    for (int i = 0; i < 7 * 24; i++) {
                                        track.addKeyframe(i * dayLengthInMinecraft, i);
                                    }
                                })
                        .addTrack(EnvironmentAttributeRegistry.SOLAR_TERM_ATTRIBUTE,
                                track -> {
                                    SolarTerm[] collectValidValues = SolarTerm.collectValidValues();
                                    for (int i = 0; i < collectValidValues.length; i++) {
                                        track.addKeyframe(i * length, collectValidValues[i]);
                                    }
                                })
                        .addTrack(EnvironmentAttributeRegistry.SEASON_ATTRIBUTE,
                                track -> {
                                    Season[] collectValidValues = Season.collectValidValues();
                                    for (int i = 0; i < collectValidValues.length; i++) {
                                        track.addKeyframe(i * length * 6, collectValidValues[i]);
                                    }
                                })
                        .build());
    }
}
