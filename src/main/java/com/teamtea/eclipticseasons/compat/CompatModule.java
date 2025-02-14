package com.teamtea.eclipticseasons.compat;


import com.teamtea.eclipticseasons.compat.cold_sweat.Cold_Sweat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class CompatModule {

    private static boolean ctm = false;
    private static boolean continuity = false;
    private static boolean yuushya = false;
    private static boolean fabric_renderer_indigo = false;
    private static boolean sodium = false;
    private static boolean dynamictrees = false;
    private static boolean cold_sweat = false;
    private static boolean journeymap = false;

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
        cold_sweat = Platform.isModLoaded("cold_sweat");
        journeymap = Platform.isModLoaded("journeymap");
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {
        if (isCold_sweat()) {
            gameBus.register(Cold_Sweat.INSTANCE);
        }
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

    public static boolean isCold_sweat() {
        return cold_sweat;
    }

    public static boolean isJourneymap() {
        return journeymap;
    }

    public static class CommonConfig {
        public static ModConfigSpec.BooleanValue sereneSeasons;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_springs;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_summers;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_autumns;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_winters;

        public static void load(ModConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            if (isCold_sweat()) {
                builder.push("ColdSweat");
                cold_sweat_springs = builder.comment("Spring Temperatures, divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SpringTemps",
                                () -> List.of(-0.25d, -0.15d, -0.1d, 0d, 0d, 0.05d),
                                () -> 0d,
                                o -> o instanceof Double);
                cold_sweat_summers = builder.comment("Summer Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SummerTemps",
                                () -> List.of(0.1d, 0.15d, 0.15d, 0.2d, 0.2d, 0.25d), () -> 0d,
                                o -> o instanceof Double);
                cold_sweat_autumns = builder.comment("Autumn Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("AutumnTemps",
                                () -> List.of(0.15d, 0.1d, 0.05d, 0d, -0.1d, -0.2d), () -> 0d,
                                o -> o instanceof Double);
                cold_sweat_winters = builder.comment("Winter Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("WinterTemps",
                                () -> List.of(-0.3d, -0.35d, -0.35d, -0.5d, -0.45d, -0.4d), () -> 0d,
                                o -> o instanceof Double);
                builder.pop();
            }


            builder.pop();
        }
    }

    public static class ClientConfig {
        public static ModConfigSpec.BooleanValue journeyMapSupport;

        public static void load(ModConfigSpec.Builder builder) {
            builder.push("Compat");
            if (isJourneymap()) {
                builder.push("JourneyMap");
                journeyMapSupport = builder.comment("Shows snow-covered blocks on the map.")
                        .define("ShowSnowyBlock", true);
                builder.pop();
            }
            builder.pop();
        }
    }
}
