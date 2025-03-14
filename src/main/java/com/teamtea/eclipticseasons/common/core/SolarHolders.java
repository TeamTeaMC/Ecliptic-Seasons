package com.teamtea.eclipticseasons.common.core;

import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.Map;


public class SolarHolders {

    public static final Map<Level, SolarDataManager> DATA_MANAGER_MAP = new HashMap<>();

    public static SolarDataManager getSaveData(Level level) {
        return DATA_MANAGER_MAP.getOrDefault(level, null);
    }

    @Deprecated
    public static LazyOptional<SolarDataManager> getSaveDataLazy(Level level) {
        return LazyOptional.of(() -> DATA_MANAGER_MAP.getOrDefault(level, new SolarDataManager(level)));
    }

    public static void createSaveData(Level level, SolarDataManager solarDataManager) {
        DATA_MANAGER_MAP.put(level, solarDataManager);
        if (!level.isClientSide() && MapChecker.isValidDimension(level)) {
            BiomeClimateManager.updateTemperature(level, solarDataManager.getSolarTerm());
        }
    }
}
