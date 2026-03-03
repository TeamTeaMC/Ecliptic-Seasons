package me.cortex.voxy.commonImpl;

import me.cortex.voxy.common.world.WorldEngine;
import net.fabricmc.api.ModInitializer;

public class VoxyCommon implements ModInitializer {
    private static VoxyInstance INSTANCE;

    public static VoxyInstance getInstance() {
        return INSTANCE;
    }

}
