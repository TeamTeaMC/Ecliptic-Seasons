package com.teamtea.eclipticseasons.compat;


import com.teamtea.eclipticseasons.compat.theoneprobe.TOPHook;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


public class CompatModule {

    private static boolean ctm = false;
    private static boolean continuity = false;
    private static boolean fabric_renderer_indigo = false;
    private static boolean sodium = false;

    /**
     * Used for mod init detect.
     **/
    public static void init() {
        ctm = Platform.isModLoaded("ctm");
        continuity = Platform.isModLoaded("continuity");
        fabric_renderer_indigo = Platform.isModLoaded("fabric_renderer_indigo");
        sodium = Platform.isModLoaded("sodium");
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {

    }


    public static void onInterModEnqueue(final InterModEnqueueEvent event) {
        event.enqueueWork(TOPHook::init);
    }


    /**
     * Used for mod setup.
     **/
    public static void setup() {

    }

    public static boolean isCtm() {
        return ctm;
    }


    public static boolean isFabric_renderer_indigo() {
        return fabric_renderer_indigo;
    }


    public static boolean isContinuity() {
        return continuity;
    }

    public static boolean isSodium() {
        return sodium;
    }


    public static class CommonConfig {
        public static ModConfigSpec.BooleanValue sereneSeasons;

        public static void load(ModConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            builder.pop();
        }
    }

    public static class ClientConfig {
        // public static ModConfigSpec.BooleanValue journeyMapSupport;

        public static void load(ModConfigSpec.Builder builder) {
            builder.push("Compat");
            // if (isJourneymap()) {
            //     builder.push("JourneyMap");
            //     journeyMapSupport = builder.comment("Shows snow-covered blocks on the map.")
            //             .define("ShowSnowyBlock", true);
            //     builder.pop();
            // }
            builder.pop();
        }
    }
}
