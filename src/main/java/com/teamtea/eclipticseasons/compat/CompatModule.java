package com.teamtea.eclipticseasons.compat;


import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.compat.theoneprobe.TOPHook;
import lombok.Getter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
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
    @Getter
    private static boolean voxyTest = false;

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
        if (isVoxy()) {
            CommentedFileConfig oldConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(EclipticSeasons.defaultConfigName(ModConfig.Type.CLIENT, EclipticSeasons.MODID)))
                    .preserveInsertionOrder().build();
            oldConfig.load();
            voxyTest = oldConfig.getOrElse("Compat.VoxyTest", false);
            oldConfig.close();
        }
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {
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
        public static ModConfigSpec.DoubleValue weatherVotePercent;
        public static ModConfigSpec.BooleanValue DistantHorizonsWinterLOD;

        public static void load(ModConfigSpec.Builder builder) {
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
            modsWithoutSereneSeasonBasedHumidity = builder.comment(
                    "A blacklist of mods whose crops should NOT get automatic humidity values based on seasons.\n" +
                            "Add mod IDs here to prevent seasonal humidity assignment.\n" +
                            "Example: [\"vinery\", \"meadow\"]"
            ).defineListAllowEmpty(
                    "ModsWithoutSereneSeasonBasedHumidity", List::of,
                    () -> "", o -> o instanceof String
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
        public static ModConfigSpec.BooleanValue unifiedSnowyBlockShading;
        public static ModConfigSpec.BooleanValue unifiedSnowyBlockSides;
        public static ModConfigSpec.BooleanValue unifiedFrozenWater;
        public static ModConfigSpec.BooleanValue DistantHorizonsWinterLODForceUpdateAll;
        private static ModConfigSpec.BooleanValue voxyTest;

        public static void load(ModConfigSpec.Builder builder) {
            builder.push("Compat");
            if (isIris()) {
                builder.push("Iris");
                unifiedSnowyBlockShading = builder.comment("Unify the shading and surface parameters of snow-covered blocks.")
                        .define("UnifiedSnowyBlockShading", true);
                unifiedSnowyBlockSides = builder.comment("Whether to also unify the shading on the sides of snow-covered blocks.")
                        .define("UnifiedSnowyBlockSides", true);
                unifiedFrozenWater = builder
                        .comment("Adjusts the rendering of thin ice so that the block it occupies is not treated as a water type during shader post-processing.")
                        .define("UnifiedFrozenWater", false);
                builder.pop();
            }
            if (isDistanthorizons()) {
                builder.push("DistantHorizons");
                DistantHorizonsWinterLODForceUpdateAll = builder
                        .comment("""
                                Force Distant Horizons to refresh all LODs timely.
                                WARNING: Enabling this may cause a full LOD rebuild and significant lag spikes.""".strip()
                        ).define("DistantHorizonsWinterLODForceUpdateAll", false);
                builder.pop();
            }
            if (isVoxy()) {
                voxyTest = builder
                        .gameRestart()
                        .comment("""
                                .
                                Just for test.
                                .""".strip()
                        ).define("VoxyTest", false);

            }
            builder.pop();
        }
    }
}
