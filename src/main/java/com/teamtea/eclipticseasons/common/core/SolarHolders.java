package com.teamtea.eclipticseasons.common.core;

import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

public class SolarHolders {
    public static final Map<Level, SolarDataManager> DATA_MANAGER_MAP = new IdentityHashMap<>();

    public static void createSaveData(Level level, SolarDataManager solarDataManager) {
        DATA_MANAGER_MAP.put(level, solarDataManager);
        // note 不再需要更新
        // if(!level.isClientSide()&& MapChecker.isValidDimension(level)){
        //     BiomeClimateManager.updateTemperature(level,solarDataManager.getSolarTerm());
        // }
    }

    public static @Nullable SolarDataManager getSaveData(Level level) {
        return DATA_MANAGER_MAP.getOrDefault(level, null);
    }

    // Lazy
    public static Optional<SolarDataManager> getSaveDataLazy(Level level) {
        SolarDataManager saveData = getSaveData(level);
        if (saveData == null) {
            saveData = new SolarDataManager(level);
        }
        return Optional.of(saveData);
    }


}
