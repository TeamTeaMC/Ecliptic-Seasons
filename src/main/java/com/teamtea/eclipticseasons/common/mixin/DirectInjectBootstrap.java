package com.teamtea.eclipticseasons.common.mixin;

import com.teamtea.eclipticseasons.common.mixin.expression.internal.DirectExpressionInjectionInfo;
import com.teamtea.eclipticseasons.common.mixin.injector.internal.DirectInjectInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;

/** Registers DirectInject with Mixin exactly once. */
public final class DirectInjectBootstrap {
    private static boolean initialized;

    private DirectInjectBootstrap() {
    }

    public static synchronized void init() {
        if (initialized) {
            return;
        }

        InjectionInfo.register(DirectInjectInjectionInfo.class);
        InjectionInfo.register(DirectExpressionInjectionInfo.class);

        initialized = true;
    }
}
