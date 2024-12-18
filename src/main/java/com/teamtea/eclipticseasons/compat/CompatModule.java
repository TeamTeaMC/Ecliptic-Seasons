package com.teamtea.eclipticseasons.compat;


public class CompatModule {

    private static boolean CTMLoad = false;
    private static boolean continuityLoad = false;
    private static boolean yuushyaLoad = false;
    private static boolean fabric_renderer_indigoLoad = false;
    private static boolean sodiumLoad = false;

    public static void init() {
        CTMLoad = Platform.isModLoaded("ctm");
        continuityLoad = Platform.isModLoaded("continuity");
        yuushyaLoad = Platform.isModLoaded("yuushya");
        fabric_renderer_indigoLoad = Platform.isModLoaded("fabric_renderer_indigo");
        sodiumLoad = Platform.isModLoaded("sodium");
        // if (ModList.get().isLoaded(DynamicTrees.MOD_ID)) {
        //     DynamicTreeMod.init();
        // }
    }

    public static boolean isCTMLoad() {
        return CTMLoad;
    }


    public static boolean isFabric_renderer_indigoLoad() {
        return fabric_renderer_indigoLoad;
    }

    public static boolean isYuuLoad() {
        return yuushyaLoad;
    }

    public static boolean isContinuityLoad() {
        return continuityLoad;
    }

    public static boolean isSodiumLoad() {
        return sodiumLoad;
    }
}
