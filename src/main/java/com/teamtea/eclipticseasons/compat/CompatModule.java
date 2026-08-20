package com.teamtea.eclipticseasons.compat;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.theoneprobe.TOPReflector;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

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
    //@Getter
    //private static boolean voxyTest = false;
    @Getter
    private static boolean configured = false;
    /**
     * Used for mod init detect.
     **/
    public static void init() {
        iui_forge = Platform.isModLoaded("iui_forge");
        modernui = Platform.isModLoaded("modernui");
        oculus = Platform.isModLoaded("oculus");
        distanthorizons = Platform.isModLoaded("distanthorizons");
        configured = Platform.isModLoaded("configured");
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
        // public static ForgeConfigSpec.DoubleValue weatherVotePercent;
        public static ForgeConfigSpec.BooleanValue DistantHorizonsWinterLOD;
        public static ForgeConfigSpec.BooleanValue showCropGrowthInfoInProbe;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Enables compatibility with mods that utilize Serene Seasons' CropTag system.")
                    .define("SereneSeasonsCropTag", true);
            sereneSeasonsIgnoreSapling = builder.comment(
                    "Excludes saplings from Serene Seasons' seasonal growth restrictions.\n" +
                            "Set to false to force saplings to follow the same seasonal rules as crops."
            ).define("SereneSeasonsCropTagIgnoreSapling", true);
            sereneSeasonBasedHumidity = builder.comment(
                    "Automatically assigns humidity requirements to crops based on their Serene Seasons seasonal tags."
            ).define("SereneSeasonCropTagBasedHumidity", true);
            modsWithoutSereneSeasonBasedHumidity = builder.comment(
                    "A blacklist of Mod IDs whose crops should NOT receive automatic humidity assignments.\n" +
                            "Example: [\"vinery\", \"meadow\"]"
            ).defineListAllowEmpty(
                    "ModsWithoutSereneSeasonBasedHumidity", List::of,
                    o -> o instanceof String
            );
            fixBiome = builder.comment("Intercepts raw biome precipitation queries to ensure small biomes (like rivers) do not disrupt large-scale weather logic.")
                    .define("FixBiomePrecipitation", true);
            showCropGrowthInfoInProbe = builder
                    .comment("Show crop growth diagnostics in Jade or TOP.")
                    .define("ShowCropGrowthInfoInProbe", true);
            // weatherVotePercent = builder.comment("Determines global weather state based on player locations when external mods bypass our API.\n" +
            //                 "This represents the weighted threshold required to trigger a specific weather condition.")
            //         .defineInRange("WeatherVotePercent", 0.5f, 0, 1.0d);
            if (isDistanthorizons())
                DistantHorizonsWinterLOD = builder.comment("Enables winter-themed Level of Detail (LOD) textures for Distant Horizons to ensure visual consistency at long distances.")
                        .define("DistantHorizonsWinterLOD", true);

            builder.pop();
        }
    }

    public static class ClientConfig {
        public static ForgeConfigSpec.BooleanValue unifiedSnowyBlockShading;
        //public static ForgeConfigSpec.BooleanValue unifiedSnowyBlockSides;
        //public static ForgeConfigSpec.BooleanValue unifiedFrozenWater;
        public static ForgeConfigSpec.BooleanValue DistantHorizonsWinterLODForceUpdateAll;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            if (isOculus()) {
                builder.push("Oculus");
                unifiedSnowyBlockShading = builder.comment("Harmonizes shading parameters for all snow-covered surfaces when using shaders.")
                        .define("UnifiedSnowyBlockShading", true);
                //unifiedSnowyBlockSides = builder.comment("Extends unified shading to the side faces of snow-covered blocks.")
                //        .define("UnifiedSnowyBlockSides", true);
                //unifiedFrozenWater = builder
                //        .comment("Shader Fix: Prevents thin ice from being incorrectly flagged as 'Water' during post-processing.")
                //        .define("UnifiedFrozenWater", false);
                builder.pop();
            }
            if (isDistanthorizons()) {
                builder.push("DistantHorizons");
                DistantHorizonsWinterLODForceUpdateAll = builder
                        .comment("""
                                Force Distant Horizons to refresh all LODs timely.
                                WARNING: Enabling this may cause a full LOD rebuild and significant lag spikes.""".strip()
                        ).define("DistantHorizonsWinterLODForceUpdateAll", true);
                builder.pop();
            }
            builder.pop();
        }
    }


}
