package com.teamtea.eclipticseasons.common.core.solar;

import net.minecraft.world.World;

import java.lang.ref.WeakReference;


public class ClientSolarDataManager extends SolarDataManager {

    public ClientSolarDataManager(World level) {
        super(level);
        this.levelWeakReference = new WeakReference<>(level);
    }


}
