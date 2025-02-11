package com.teamtea.eclipticseasons.common.core;

import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.Map;

public class SolarHolders {
    public static final Map<World, SolarDataManager> DATA_MANAGER_MAP = new HashMap<>();

    public static SolarDataManager getSaveData(World level) {
        return DATA_MANAGER_MAP.getOrDefault(level, null);
    }

    public static LazyOptional<SolarDataManager> getSaveDataLazy(World level) {
        return LazyOptional.of(() -> DATA_MANAGER_MAP.getOrDefault(level, new SolarDataManager(level)));
    }

    public static void createSaveData(World level, SolarDataManager solarDataManager) {
        DATA_MANAGER_MAP.put(level, solarDataManager);
    }
}
