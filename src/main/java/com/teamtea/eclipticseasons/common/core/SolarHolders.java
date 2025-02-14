package com.teamtea.eclipticseasons.common.core;

import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.level.Level;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class SolarHolders {
    public static final Map<Level, SolarDataManager> DATA_MANAGER_MAP = new IdentityHashMap<>();

    public static void createSaveData(Level level, SolarDataManager solarDataManager) {
         DATA_MANAGER_MAP.put(level, solarDataManager);
    }

    public static SolarDataManager getSaveData(Level level) {
        return DATA_MANAGER_MAP.getOrDefault(level, null);
    }

    // Lazy
    public static Optional<SolarDataManager> getSaveDataLazy(Level level) {
        return Optional.of(DATA_MANAGER_MAP.getOrDefault(level, new SolarDataManager(level)));
    }


}
