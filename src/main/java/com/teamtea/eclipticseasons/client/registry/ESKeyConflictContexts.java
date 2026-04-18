package com.teamtea.eclipticseasons.client.registry;

import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.jspecify.annotations.NonNull;

public class ESKeyConflictContexts {

    public static final IKeyConflictContext DEBUG_CONTEXT = new IKeyConflictContext() {
        public boolean isActive() {
            return KeyConflictContext.IN_GAME.isActive();
        }

        public boolean conflicts(@NonNull IKeyConflictContext other) {
            return this == other;
        }
    };
}
