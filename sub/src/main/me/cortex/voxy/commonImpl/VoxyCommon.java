package me.cortex.voxy.commonImpl;

import net.fabricmc.api.ModInitializer;

public class VoxyCommon implements ModInitializer {
    private static VoxyInstance INSTANCE;

    public static VoxyInstance getInstance() {
        return INSTANCE;
    }

}
