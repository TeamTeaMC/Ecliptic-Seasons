package com.teamtea.eclipticseasons.compat;


import com.teamtea.eclipticseasons.compat.theoneprobe.TOPReflector;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;

public class CompatModule {

    // private static boolean dynamictrees = false;
    // private static boolean cold_sweat = false;
    @Getter
    private static boolean oculus = false;

    @Getter
    private static boolean iui_forge = false;
    @Getter
    private static boolean modernui = false;

    @Getter
    private static boolean distanthorizons = false;
    /**
     * Used for mod init detect.
     **/
    public static void init() {
        iui_forge = Platform.isModLoaded("iui_forge");
        modernui = Platform.isModLoaded("modernui");
        oculus = Platform.isModLoaded("oculus");
        distanthorizons = Platform.isModLoaded("distanthorizons");
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {
        if (isIui_forge() && FMLLoader.getDist() == Dist.CLIENT) {
            try {
                Class<?> iuiHandlerClass = Class.forName("com.teamtea.eclipticseasons.compat.iui_forge.IUIHandler");
                Class<?> iuiSetupClass = Class.forName("com.teamtea.eclipticseasons.compat.iui_forge.IUISetup");
                gameBus.register(iuiHandlerClass.getField("INSTANCE").get(null));
                modBus.register(iuiSetupClass.getField("INSTANCE").get(null));
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        if (isModernui() && FMLLoader.getDist() == Dist.CLIENT) {
            try {
                Class<?> iuiHandlerClass = Class.forName("com.teamtea.eclipticseasons.compat.modernui.MUIHandler");
                gameBus.register(iuiHandlerClass.getField("INSTANCE").get(null));
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
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
        public static ForgeConfigSpec.BooleanValue DistantHorizonsWinterLOD;

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
            if (isDistanthorizons())
                DistantHorizonsWinterLOD = builder.comment("Provides winter LOD for Distant Horizons.")
                        .define("DistantHorizonsWinterLOD", true);
            builder.pop();
        }
    }

    public static class ClientConfig {
        public static ForgeConfigSpec.BooleanValue unifiedSnowyBlockShading;
        public static ForgeConfigSpec.BooleanValue DistantHorizonsWinterLODForceUpdateAll;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            if (isOculus()) {
                builder.push("Oculus");
                unifiedSnowyBlockShading = builder.comment("Unify the shading and surface parameters of snow-covered blocks.")
                        .define("UnifiedSnowyBlockShading", true);
                builder.pop();
            }
            if(isDistanthorizons()){
                DistantHorizonsWinterLODForceUpdateAll= builder
                        .comment("""
                                        Force Distant Horizons to refresh all LODs timely.
                                        WARNING: Enabling this may cause a full LOD rebuild and significant lag spikes.""".strip()
                        ).define("DistantHorizonsWinterLODForceUpdateAll", false);

            }
            builder.pop();
        }
    }


}
