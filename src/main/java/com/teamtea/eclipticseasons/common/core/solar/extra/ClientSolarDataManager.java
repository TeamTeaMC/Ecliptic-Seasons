package com.teamtea.eclipticseasons.common.core.solar.extra;

import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;


public class ClientSolarDataManager extends SolarDataManager {

    public ClientSolarDataManager(Level level) {
        super(level);
        this.levelWeakReference = new WeakReference<>(level);
    }

    public static SolarDataManager get(ClientLevel clientLevel) {
        return CommonConfig.Season.realWorldSolarTerms.get() ?
                new FixedSolarDataManagerLocal(clientLevel) : new ClientSolarDataManager(clientLevel);
    }

}
