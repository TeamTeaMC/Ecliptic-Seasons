package com.teamtea.eclipticseasons.common.core;

import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.core.Holder;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import org.jspecify.annotations.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class SolarHolders {
    public static final Map<Level, SolarDataManager> DATA_MANAGER_MAP = new IdentityHashMap<>();
    public static final Map<Holder<WorldClock>, SolarDataManager> CLOCK_MAP = new IdentityHashMap<>();

    // Check if we can have a same clock
    public static void createSaveData(Level level, SolarDataManager solarDataManager) {
        DATA_MANAGER_MAP.putIfAbsent(level, solarDataManager);
        var worldClockHolder = level.dimensionType().defaultClock();
        worldClockHolder.ifPresent(clockHolder -> CLOCK_MAP.putIfAbsent(clockHolder, solarDataManager));
    }

    public static @Nullable SolarDataManager getSaveData(Level level) {
        return DATA_MANAGER_MAP.getOrDefault(level, null);
    }

    @ApiStatus.Experimental
    public static @Nullable SolarDataManager getSaveData(Holder<WorldClock> clock) {
        return CLOCK_MAP.getOrDefault(clock, null);
    }

    public static Optional<SolarDataManager> getSaveDataLazy(Level level) {
        SolarDataManager saveData = getSaveData(level);
        return Optional.ofNullable(saveData);
    }

    public static void remove(Level level) {
        SolarHolders.DATA_MANAGER_MAP.remove(level);
        SolarHolders.CLOCK_MAP.remove(level.dimensionType().defaultClock().orElse(null));
    }


}
