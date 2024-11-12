package com.teamtea.eclipticseasons.compat;


public class CompatModule {

    private static boolean CTMLoad =false;

    public static void register() {
        CTMLoad =Platform.isModLoaded("ctm");

        // if (ModList.get().isLoaded(DynamicTrees.MOD_ID)) {
        //     DynamicTreeMod.init();
        // }
    }

    public static boolean isCTMLoad() {
        return CTMLoad;
    }
}
