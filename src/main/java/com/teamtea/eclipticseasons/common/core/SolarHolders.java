package com.teamtea.eclipticseasons.common.core;

import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class SolarHolders {

    public static final Map<Level, SolarDataManager> DATA_MANAGER_MAP = new IdentityHashMap<>();

    public static @Nullable SolarDataManager getSaveData(Level level) {
        return DATA_MANAGER_MAP.getOrDefault(level, null);
    }

    public static LazyOptional<SolarDataManager> getSaveDataLazy(Level level) {
        SolarDataManager saveData = getSaveData(level);
        return LazyOptional.of(saveData == null ? null : () -> saveData);
    }

    public static void createSaveData(Level level, SolarDataManager solarDataManager) {
        DATA_MANAGER_MAP.put(level, solarDataManager);
        // if(!level.isClientSide()&& MapChecker.isValidDimension(level)){
        //     BiomeClimateManager.updateTemperature(level,solarDataManager.getSolarTerm());
        // }
    }
}
