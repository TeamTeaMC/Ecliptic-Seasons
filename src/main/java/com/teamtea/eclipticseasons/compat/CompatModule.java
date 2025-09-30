package com.teamtea.eclipticseasons.compat;


import com.teamtea.eclipticseasons.compat.theoneprobe.TOPReflector;
import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.List;

public class CompatModule {

    // private static boolean dynamictrees = false;
    // private static boolean cold_sweat = false;
    @Getter
    private static boolean oculus = false;

    /**
     * Used for mod init detect.
     **/
    public static void init() {
        // dynamictrees = Platform.isModLoaded("dynamictrees");
        // cold_sweat = Platform.isModLoaded("cold_sweat");
        oculus = Platform.isModLoaded("oculus");
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
        public static ForgeConfigSpec.BooleanValue sereneSeasonsIgnoreSapling;
        public static ForgeConfigSpec.BooleanValue sereneSeasonBasedHumidity;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> modsWithoutSereneSeasonBasedHumidity;
        public static ForgeConfigSpec.BooleanValue fixBiome;
        public static ForgeConfigSpec.DoubleValue weatherVotePercent;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            sereneSeasonsIgnoreSapling = builder
                    .comment(
                            "If true, saplings will be ignored when applying Serene Seasons crop tags.\n" +
                                    "Set to false if you want saplings to also follow seasonal crop rules."
                    ).define("SereneSeasonsCropTagIgnoreSapling", true);
            sereneSeasonBasedHumidity = builder
                    .comment(
                            "Crops should get automatic humidity values based on seasons from Serene Season Crop Tag."
                    ).define("SereneSeasonCropTagBasedHumidity", true);
            modsWithoutSereneSeasonBasedHumidity = builder
                    .comment(
                            "A blacklist of mods whose crops should NOT get automatic humidity values based on seasons.\n" +
                                    "Add mod IDs here to prevent seasonal humidity assignment.\n" +
                                    "Example: [\"vinery\", \"meadow\"]"
                    ).defineListAllowEmpty(
                            "ModsWithoutSereneSeasonBasedHumidity", List::of,
                            o -> o instanceof String
                    );
            fixBiome = builder.comment("If a mod tries to query biome precipitation using the raw method, would adjust it to correctly ignore small biomes like rivers.")
                    .define("FixBiomePrecipitation", true);
            weatherVotePercent = builder.comment("When a mod tries to query global weather parameters directly instead of using our API, " +
                            "Solar Weather will determine the result based on a weighted vote from the areas around players.")
                    .defineInRange("WeatherVotePercent", 0.5f, 0, 1d);
            builder.pop();
        }
    }

    public static class ClientConfig {
        public static ForgeConfigSpec.BooleanValue unifiedSnowyBlockShading;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            if (isOculus()) {
                builder.push("Oculus");
                unifiedSnowyBlockShading = builder.comment("Unify the shading and surface parameters of snow-covered blocks.")
                        .define("UnifiedSnowyBlockShading", true);
                builder.pop();
            }
            builder.pop();
        }
    }


}
