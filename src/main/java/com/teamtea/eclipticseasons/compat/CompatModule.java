package com.teamtea.eclipticseasons.compat;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

public class CompatModule {

    private static boolean ctm = false;
    private static boolean continuity = false;
    private static boolean yuushya = false;
    private static boolean fabric_renderer_indigo = false;
    private static boolean sodium = false;
    private static boolean dynamictrees = false;

    /**
     * Used for mod init detect.
     **/
    public static void init() {
        ctm = Platform.isModLoaded("ctm");
        continuity = Platform.isModLoaded("continuity");
        yuushya = Platform.isModLoaded("yuushya");
        fabric_renderer_indigo = Platform.isModLoaded("fabric_renderer_indigo");
        sodium = Platform.isModLoaded("sodium");
        dynamictrees = Platform.isModLoaded("dynamictrees");
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {

    }


    /**
     * Used for mod setup.
     **/
    public static void setup() {
        // if (isDynamictrees()) {
        //     DynamicTreeMod.init();
        // }
    }

    public static boolean isCtm() {
        return ctm;
    }


    public static boolean isFabric_renderer_indigo() {
        return fabric_renderer_indigo;
    }

    public static boolean isYuuLoad() {
        return yuushya;
    }

    public static boolean isContinuity() {
        return continuity;
    }

    public static boolean isSodium() {
        return sodium;
    }

    public static boolean isDynamictrees() {
        return dynamictrees;
    }
}
