package com.teamtea.eclipticseasons.compat.voxy.client;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class VoxyTintManager {

    private static final Set<IVoxyModelBakerySubsystem> INSTANCES =
            ConcurrentHashMap.newKeySet();

    private VoxyTintManager() {
    }

    public static void register(IVoxyModelBakerySubsystem instance) {
        INSTANCES.add(instance);
    }

    public static void unregister(IVoxyModelBakerySubsystem instance) {
        INSTANCES.remove(instance);
    }

    public static void refreshAll() {
        for (IVoxyModelBakerySubsystem instance : INSTANCES) {
            instance.eclipticseasons$refreshTint();
        }
    }
}
