package com.teamtea.eclipticseasons.compat.voxy.client;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class VoxyGeometryRefreshManager {

    private static final Set<IVoxyAsyncNodeManager> INSTANCES = ConcurrentHashMap.newKeySet();

    private VoxyGeometryRefreshManager() {
    }

    public static void register(IVoxyAsyncNodeManager instance) {
        INSTANCES.add(instance);
    }

    public static void unregister(IVoxyAsyncNodeManager instance) {
        INSTANCES.remove(instance);
    }


    public static boolean refreshAll() {
        if (INSTANCES.isEmpty()) return true;

        for (IVoxyAsyncNodeManager instance : INSTANCES) {
            if (!instance.eclipticseasons$canRefreshGeometry()) {
                return false;
            }
        }

        for (IVoxyAsyncNodeManager instance : INSTANCES) {
            instance.eclipticseasons$refreshAllGeometry();
        }

        return true;
    }
}
