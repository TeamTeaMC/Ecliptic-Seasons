package com.teamtea.eclipticseasons.compat;


import com.teamtea.eclipticseasons.compat.theoneprobe.TOPHook;
import lombok.Getter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;


public class CompatModule {

    private static boolean ctm = false;
    private static boolean continuity = false;
    @Getter
    private static boolean fabric_renderer_indigo = false;
    @Getter
    private static boolean sodium = false;
    @Getter
    private static boolean iris = false;
    @Getter
    private static boolean modernui = false;
    @Getter
    private static boolean distanthorizons = false;

    @Getter
    private static boolean voxy = false;
    //@Getter
    //private static boolean voxyTest = false;


    /**
     * Used for mod init detect.
     **/
    public static void init() {
        ctm = Platform.isModLoaded("ctm");
        continuity = Platform.isModLoaded("continuity");
        fabric_renderer_indigo = Platform.isModLoaded("fabric_renderer_indigo");
        sodium = Platform.isModLoaded("sodium");
        iris = Platform.isModLoaded("iris");
        modernui = Platform.isModLoaded("modernui");
        distanthorizons = Platform.isModLoaded("distanthorizons");
        voxy = Platform.isModLoaded("voxy");
        //if (isVoxy()) {
        //    CommentedFileConfig oldConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(EclipticSeasons.defaultConfigName(ModConfig.Type.COMMON, EclipticSeasons.MODID)))
        //            .preserveInsertionOrder().build();
        //    oldConfig.load();
        //    voxyTest = oldConfig.getOrElse("Compat.VoxyTest", false);
        //    oldConfig.close();
        //}
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {
        if (isModernui() && FMLLoader.getCurrent().getDist() == Dist.CLIENT) {
            try {
                Class<?> iuiHandlerClass = Class.forName("com.teamtea.eclipticseasons.compat.modernui.MUIHandler");
                gameBus.register(iuiHandlerClass.getField("INSTANCE").get(null));
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {

            }
        }
        if (isVoxy() && FMLLoader.getCurrent().getDist() == Dist.CLIENT) {
            try {
                Class<?> handler = Class.forName("com.teamtea.eclipticseasons.compat.voxy.client.VoxyEsHandler");
                gameBus.register(handler.getField("INSTANCE").get(null));
            } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {

            }
        }
    }


    public static void onInterModEnqueue(final InterModEnqueueEvent event) {
        event.enqueueWork(TOPHook::init);
    }


    /**
     * Used for mod setup.
     **/
    public static void setup() {

    }


    public static class CommonConfig {
        public static ModConfigSpec.BooleanValue sereneSeasons;
        public static ModConfigSpec.BooleanValue sereneSeasonsIgnoreSapling;
        public static ModConfigSpec.BooleanValue sereneSeasonBasedHumidity;
        public static ModConfigSpec.ConfigValue<List<? extends String>> modsWithoutSereneSeasonBasedHumidity;
        public static ModConfigSpec.BooleanValue fixBiome;
        // public static ModConfigSpec.DoubleValue weatherVotePercent;
        public static ModConfigSpec.BooleanValue DistantHorizonsWinterLOD;
        public static ModConfigSpec.BooleanValue voxyCompatibility;
        public static ModConfigSpec.BooleanValue voxyAutoRefresh;
        public static ModConfigSpec.BooleanValue voxyRefreshOnSolarTermChange;
        public static ModConfigSpec.BooleanValue showCropGrowthInfoInProbe;
        public static void load(ModConfigSpec.Builder builder) {
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
                    () -> "", o -> o instanceof String
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

            if (isVoxy()) {
                voxyCompatibility = builder
                        .worldRestart()
                        .comment("Enables compatibility with Voxy.")
                        .define("VoxyCompatibility", true);

                voxyAutoRefresh = builder
                        .comment("Automatically updates distant LODs when snow coverage changes.")
                        .define("VoxyAutoRefresh", true);

                voxyRefreshOnSolarTermChange = builder
                        .comment("Updates seasonal LODs when the solar term changes.")
                        .define("VoxyRefreshOnSolarTermChange", true);
            }
            builder.pop();
        }
    }

    public static class ClientConfig {
        public static ModConfigSpec.BooleanValue unifiedSnowyBlockShading;
        public static ModConfigSpec.BooleanValue unifiedSnowyBlockSides;
        public static ModConfigSpec.BooleanValue unifiedFrozenWater;
        public static ModConfigSpec.BooleanValue DistantHorizonsWinterLODForceUpdateAll;

        public static void load(ModConfigSpec.Builder builder) {
            builder.push("Compat");
            if (isIris()) {
                builder.push("Iris");
                unifiedSnowyBlockShading = builder.comment("Harmonizes shading parameters for all snow-covered surfaces when using shaders.")
                        .define("UnifiedSnowyBlockShading", true);
                unifiedSnowyBlockSides = builder.comment("Extends unified shading to the side faces of snow-covered blocks.")
                        .define("UnifiedSnowyBlockSides", true);
                unifiedFrozenWater = builder
                        .comment("Shader Fix: Prevents thin ice from being incorrectly flagged as 'Water' during post-processing.")
                        .define("UnifiedFrozenWater", false);
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
