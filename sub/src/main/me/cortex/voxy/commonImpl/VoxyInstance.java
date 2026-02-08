package me.cortex.voxy.commonImpl;

import me.cortex.voxy.common.thread.ServiceManager;
import me.cortex.voxy.common.world.WorldEngine;

import java.util.function.BooleanSupplier;

public abstract class VoxyInstance {

    public final BooleanSupplier savingServiceRateLimiter = null;


    public WorldEngine getNullable(WorldIdentifier identifier) {
        return null;
    }

    public ServiceManager getServiceManager() {
        return null;
    }
}
