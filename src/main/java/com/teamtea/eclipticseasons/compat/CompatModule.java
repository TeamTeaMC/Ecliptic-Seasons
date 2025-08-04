package com.teamtea.eclipticseasons.compat;


import com.teamtea.eclipticseasons.compat.theoneprobe.TOPReflector;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.List;

public class CompatModule {

    // private static boolean dynamictrees = false;
    // private static boolean cold_sweat = false;

    /**
     * Used for mod init detect.
     **/
    public static void init() {
        // dynamictrees = Platform.isModLoaded("dynamictrees");
        // cold_sweat = Platform.isModLoaded("cold_sweat");
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {
    }

    public static void onInterModEnqueue(final InterModEnqueueEvent event) {
        event.enqueueWork(TOPReflector::init);
    }

    /**
     * Used for mod setup.
     **/
    public static void setup() {

    }


    public static class CommonConfig {
        public static ForgeConfigSpec.BooleanValue sereneSeasons;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            builder.pop();
        }
    }

    public static class ClientConfig {

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");

            builder.pop();
        }
    }


}
