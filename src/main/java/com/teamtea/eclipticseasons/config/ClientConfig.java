package com.teamtea.eclipticseasons.config;


import com.teamtea.eclipticseasons.client.color.season.FoliageColorSourceDefault;
import com.teamtea.eclipticseasons.compat.CompatModule;
import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

public class ClientConfig {

    public static final ForgeConfigSpec CLIENT_CONFIG = new ForgeConfigSpec.Builder().configure(ClientConfig::new).getRight();

    protected ClientConfig(ForgeConfigSpec.Builder builder) {
        Debug.load(builder);
        GUI.load(builder);
        Renderer.load(builder);
        Sound.load(builder);
        Particle.load(builder);
        Weather.load(builder);
        CompatModule.ClientConfig.load(builder);
    }

    public static class Debug {

        public static ForgeConfigSpec.BooleanValue debugInfo;
        public static ForgeConfigSpec.BooleanValue smoothSnowyEdges;
        //public static ForgeConfigSpec.IntValue minChunkCompileWarningTime;
        public static ForgeConfigSpec.BooleanValue frozenWater;
        public static ForgeConfigSpec.BooleanValue frozenWaterBreakable;
        public static ForgeConfigSpec.BooleanValue frozenWaterCheckLight;
        public static ForgeConfigSpec.BooleanValue fogWeather;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Debug");

            debugInfo = builder.comment("Show development-related metrics in the on-screen display.")
                    .define("DebugInfo", false);
            smoothSnowyEdges = builder.comment("Renders decorative snow edges on adjacent blocks for seamless terrain transitions.")
                    .define("SmoothSnowyEdges", false);
            //minChunkCompileWarningTime = builder.comment("Sets the threshold (in ms) for chunk compilation before logging a performance warning.")
            //        .defineInRange("MinChunkCompileWarningTime", 100, 5, 2000);

            frozenWater = builder.comment("Visual effect: Surface water appears to be covered by a thin, cosmetic layer of ice.")
                    .define("FrozenWater", false);
            frozenWaterBreakable = builder.comment("If true, the thin ice layer on water can be broken by players/entities.")
                    .define("FrozenWaterBreakable", true);
            frozenWaterCheckLight = builder.comment("Prevents the visual frozen water effect in areas with high light levels.")
                    .define("FrozenWaterCheckLight", true);

            fogWeather = builder.comment("Experimental: Adds a cinematic fog effect during rainfall.")
                    .define("FoggyWeather", false);

            builder.pop();
        }
    }

    public static class GUI {
        public static ForgeConfigSpec.BooleanValue agriculturalInformation;
        public static ForgeConfigSpec.BooleanValue itemInformation;
        public static ForgeConfigSpec.BooleanValue simpleSeasonHud;
        public static ForgeConfigSpec.BooleanValue showGregorianYear;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("GUI");
            agriculturalInformation = builder.comment("Show tooltips for ideal seasons and humidity levels on crop items.")
                    .define("AgriculturalInformation", true);
            itemInformation = builder.comment("Show additional information regarding item usage or origins.")
                    .define("ItemInformation", true);
            simpleSeasonHud = builder.comment("Whether to enable a simplified HUD overlay that displays the current season and solar term on the screen.")
                    .define("SimpleSeasonHud", false);
            showGregorianYear = builder
                    .comment("Display the standard Gregorian year instead of the solar calendar year.")
                    .define("ShowGregorianYear", false);
            builder.pop();
        }
    }

    public static class Renderer {
        public static ForgeConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue enhancementChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue resetRendererAfterSleep;
        public static ForgeConfigSpec.BooleanValue topFaceCulling;

        public static ForgeConfigSpec.BooleanValue useVanillaCheck;
        // public static ForgeConfigSpec.BooleanValue realisticSnowyChange;

        public static ForgeConfigSpec.BooleanValue flowerOnGrass;
        public static ForgeConfigSpec.BooleanValue seasonalGrassColorChange;
        public static ForgeConfigSpec.BooleanValue seasonalColorChangeExtend;
        public static ForgeConfigSpec.BooleanValue smootherSeasonalGrassColorChange;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> seasonalColorOverrides;
        public static ForgeConfigSpec.BooleanValue foliageUnderTree;


        public static ForgeConfigSpec.BooleanValue snowUnderFence;
        public static ForgeConfigSpec.BooleanValue snowInFence;
        public static ForgeConfigSpec.IntValue snowInFenceCount;
        public static ForgeConfigSpec.BooleanValue snowInFenceDirection;
        public static ForgeConfigSpec.BooleanValue snowInFenceOnlySnowy;

        public static ForgeConfigSpec.BooleanValue extraSnowLayer;
        public static ForgeConfigSpec.IntValue extraSnowLayerMaxLayers;
        public static ForgeConfigSpec.IntValue extraSnowLayerMaxLayersOnLeaves;
        public static ForgeConfigSpec.BooleanValue extraSnowLayerCulling;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Renderer");
            forceChunkRenderUpdate = builder.comment("Periodically force-reloads chunk rendering to fix visual glitches (may impact FPS).")
                    .define("ForceChunkRenderUpdate", true);
            enhancementChunkRenderUpdate = builder.comment("(ForceChunkRenderUpdate) A more thorough reload that refreshes all chunk sections periodically.")
                    .define("EnhancementChunkRenderUpdate", false);
            topFaceCulling = builder.comment("Optimized rendering: Cull the top face of a block if it is hidden by a snow model.")
                    .define("CullTopFaceWithSnow", false);

            resetRendererAfterSleep = builder.comment("Refreshes the renderer state immediately after the player wakes up.")
                    .define("ResetRendererAfterSleep", false);

            useVanillaCheck = builder.comment("Use standard Vanilla light/sky rules to determine if snow should fall in a location.")
                    .define("UseVanillaSnowCheck", false);


            snowUnderFence = builder.comment("Allow decorative snow overlays to appear under solid blocks (e.g., in shadows or under eaves).")
                    .define("SnowUnderShadow", false);
            snowInFence = builder.comment("[Sodium/Embeddium] Renders a virtual snow layer inside fences and tall grass for a seamless look.")
                    .define("SnowInFence", false);
            snowInFenceCount = builder.comment("[Sodium/Embeddium] Minimum number of adjacent snow-covered blocks required to trigger the effect.")
                    .defineInRange("SnowInFenceCount", 2, 1, 8);
            snowInFenceDirection = builder.comment("[Sodium/Embeddium] Check all eight surrounding directions instead of just the cardinal four.")
                    .define("SnowInFenceDirection", false);
            snowInFenceOnlySnowy = builder.comment("[Sodium/Embeddium] Render snow layers inside snow-connected fences only when in a snowy state.")
                    .define("SnowInFenceOnlySnowy", false);
            extraSnowLayer = builder
                    .comment("[Sodium/Embeddium] Render an additional snow layer on blocks that already use the snowy model.")
                    .define("ExtraSnowLayer", false);
            extraSnowLayerMaxLayers = builder.comment("[Sodium/Embeddium] Maxmum number of additional snow layer on blocks.")
                    .defineInRange("ExtraSnowLayerMaxLayers", 2, 0, 8);
            extraSnowLayerMaxLayersOnLeaves = builder.comment("[Sodium/Embeddium] Maxmum number of additional snow layer on leave blocks.")
                    .defineInRange("ExtraSnowLayerMaxLayersOnLeaves", 1, 0, 8);
            extraSnowLayerCulling = builder
                    .comment("[Sodium/Embeddium] Culling extra fake snow layer for necessary.")
                    .define("ExtraSnowLayerCulling", true);

            seasonalGrassColorChange = builder.comment("Apply seasonal color shifts to grass and leaf textures.")
                    .define("SeasonalGrassColorChange", true);
            seasonalColorChangeExtend = builder.comment("Extend seasonal color shifts to birch, spruce, and mangrove leaves.")
                    .define("SeasonalColorChangeExtend", true);
            foliageUnderTree = builder.comment("Visual detail: Adds a brownish, withered foliage effect under trees during Autumn.")
                    .define("FoliageUnderTree", false);
            seasonalColorOverrides = builder.comment(
                            "Custom seasonal colors for single-tint blocks only.",
                            "Format: \"block_id@color1,color2,...,colorN,placeholder_color,base_color\"",
                            "The number of colors (N) must be a factor of 24 (e.g., 4, 12, or 24).",
                            "- 4 colors: Seasonal (6 terms each)",
                            "- 12 colors: Monthly (2 terms each)",
                            "The 'placeholder_color' maps to index 24; 'base_color' is the final reference hex."
                    )
                    .worldRestart()
                    .defineListAllowEmpty("SeasonalColorOverrides",
                            FoliageColorSourceDefault::createConfig,
                            // FoliageColorSourceDefault::createSingle,
                            o -> o instanceof String s && FoliageColorSourceDefault.isValid(s));
            smootherSeasonalGrassColorChange = builder.comment("Calculate seasonal color shifts based on exact solar term progress for smoother transitions.")
                    .define("SmootherSeasonalGrassColorChange", true);
            flowerOnGrass = builder.comment("Visual detail: Occasionally adds small, decorative flowers to grass blocks in Spring.")
                    .define("FlowerOnGrass", true);
            builder.pop();
        }
    }

    public static class Sound {
        public static ForgeConfigSpec.BooleanValue sound;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Sound");
            sound = builder.comment("Enable ambient environmental sounds (birds, wind, etc.) based on the season.")
                    .worldRestart().define("NaturalSound", true);
            builder.pop();
        }
    }


    public static class Particle {
        public static ForgeConfigSpec.BooleanValue seasonParticle;

        public static ForgeConfigSpec.BooleanValue butterfly;
        public static ForgeConfigSpec.IntValue butterflySpawnWeight;
        public static ForgeConfigSpec.BooleanValue fallenLeaves;
        public static ForgeConfigSpec.IntValue fallenLeavesDropWeight;
        public static ForgeConfigSpec.BooleanValue fallenLeavesGroundFade;
        public static ForgeConfigSpec.BooleanValue firefly;
        public static ForgeConfigSpec.IntValue fireflySpawnWeight;
        public static ForgeConfigSpec.BooleanValue wildGoose;
        public static ForgeConfigSpec.IntValue wildGooseSpawnWeight;
        public static ForgeConfigSpec.BooleanValue seasonGreenhouse;
        public static ForgeConfigSpec.IntValue SeasonGreenhouseParticleSpawnCount;

        public static ForgeConfigSpec.BooleanValue snowLeafParticles;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Particle");
            seasonParticle = builder.comment("Enable seasonal particles like butterflies, fireflies, and falling leaves.")
                    .define("SeasonalParticles", true);

            butterfly = builder.comment("Spawns butterflies over flowers during Spring.")
                    .define("Butterfly", true);
            butterflySpawnWeight = builder.comment("Density between butterfly spawns. Higher values result in fewer butterflies.")
                    .defineInRange("ButterflySpawnDelay", 10, 1, 10000);

            fallenLeaves = builder.comment("Leaf blocks will drop falling leaf particles, peaking in frequency during Autumn.")
                    .define("FallenLeaves", true);
            fallenLeavesDropWeight = builder.comment("Density between leaf particles. Higher values result in fewer leaves falling.")
                    .defineInRange("FallenLeavesDropDelay", 10, 1, 10000);
            fallenLeavesGroundFade = builder.comment("Fallen leaves on the ground will gradually shrink instead of disappearing instantly.")
                    .define("FallenLeavesGroundFade", true);

            firefly = builder.comment("Spawns fireflies near flowers during Summer evenings.")
                    .define("Firefly", true);
            fireflySpawnWeight = builder.comment("Density between firefly spawns. Higher values result in fewer fireflies.")
                    .defineInRange("FireflySpawnDelay", 10, 1, 10000);

            wildGoose = builder.comment("Visual effect: Displays wild geese flying south during autumnal transitions.")
                    .define("WildGoose", true);
            wildGooseSpawnWeight = builder.comment("Density between wild goose sightings. Higher values result in fewer sightings.")
                    .defineInRange("WildGooseSpawnDelay", 10, 1, 10000);


            seasonGreenhouse = builder.comment("Emits soft ambient particles when the Season Core or Greenhouse is active.")
                    .define("GreenHouseParticles", true);
            SeasonGreenhouseParticleSpawnCount = builder.comment("Density of particles emitted by the Greenhouse effect.")
                    .defineInRange("SeasonGreenhouseParticleSpawnCount", 30, 0, 160);

            snowLeafParticles = builder.comment("Breaking snow-covered leaves will release a burst of snow particles.")
                    .define("SnowLeafParticles", true);

            builder.pop();
        }
    }

    public static class Weather {
        // public static ForgeConfigSpec.IntValue weatherBufferDistance;
        // public static ForgeConfigSpec.DoubleValue weatherTransitionSpeed;
        public static ForgeConfigSpec.BooleanValue tweakPrecipitationParticleTexture;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Weather");
            // weatherBufferDistance = builder.comment("The radius (in blocks) used to sample and blend weather patterns for smooth transitions.")
            //         .defineInRange("WeatherBufferDistance", 6, 1, 80);
            // weatherTransitionSpeed = builder.comment(
            //                 "How quickly local weather conditions change. Higher values mean faster shifts.")
            //         .defineInRange("WeatherTransitionSpeed", 0.008d, 0.0008d, 0.08d);
            tweakPrecipitationParticleTexture = builder.comment("When enabled, Ecliptic Seasons may adjust rain and snow particle textures when needed.")
                    .define("TweakPrecipitationParticleTexture", true);
            builder.pop();
        }
    }

    @Getter
    private static boolean topFaceCulling = false;

    public static void UpdateConfig(ModConfigEvent modConfigEvent) {
        if (!(modConfigEvent instanceof ModConfigEvent.Unloading)
                && modConfigEvent.getConfig().getSpec() == CLIENT_CONFIG) {
            topFaceCulling = Renderer.topFaceCulling.get();
        }
    }
}
