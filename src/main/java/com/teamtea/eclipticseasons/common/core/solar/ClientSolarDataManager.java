package com.teamtea.eclipticseasons.common.core.solar;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

import java.lang.ref.WeakReference;


public class ClientSolarDataManager extends SolarDataManager {

    public ClientSolarDataManager(Level level) {
        super(level);
        this.levelWeakReference = new WeakReference<>(level);
    }

    public static SolarDataManager get(ClientLevel clientLevel) {
        return new ClientSolarDataManager(clientLevel);
    }

}
