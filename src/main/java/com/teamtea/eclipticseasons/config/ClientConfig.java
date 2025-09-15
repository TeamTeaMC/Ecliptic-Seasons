package com.teamtea.eclipticseasons.config;


import com.teamtea.eclipticseasons.compat.CompatModule;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec CLIENT_CONFIG = new ModConfigSpec.Builder().configure(ClientConfig::new).getRight();

    protected ClientConfig(ModConfigSpec.Builder builder) {
        Debug.load(builder);
        GUI.load(builder);
        Renderer.load(builder);
        Sound.load(builder);
        Particle.load(builder);
        Weather.load(builder);
        CompatModule.ClientConfig.load(builder);
    }

    public static class Debug {

        public static ModConfigSpec.BooleanValue debugInfo;
        public static ModConfigSpec.BooleanValue smoothSnowyEdges;
        public static ModConfigSpec.IntValue minChunkCompileWarningTime;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Debug");

            debugInfo = builder.comment("Info used for development shown in GUI.")
                    .define("DebugInfo", false);
            smoothSnowyEdges = builder.comment("Render snow edge overlay on neighbors for smoother snowy transitions.")
                    .define("SmoothSnowyEdges", false);
            minChunkCompileWarningTime = builder.comment("If the game takes too long to load a chunk for render, a warning will be shown in the log.")
                    .defineInRange("MinChunkCompileWarningTime", 100, 5, 2000);
            builder.pop();
        }
    }

    public static class GUI {
        public static ModConfigSpec.BooleanValue agriculturalInformation;
        public static ModConfigSpec.BooleanValue itemInformation;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("GUI");
            agriculturalInformation = builder.comment("Displays the season and humidity levels suitable for growing crops.")
                    .define("AgriculturalInformation", true);
            itemInformation = builder.comment("Displays the use or source of a item.")
                    .define("ItemInformation", true);
            builder.pop();
        }
    }

    public static class Renderer {
        public static ModConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ModConfigSpec.BooleanValue enhancementChunkRenderUpdate;
        public static ModConfigSpec.BooleanValue resetRendererAfterSleep;
        public static ModConfigSpec.BooleanValue topFaceCulling;

        public static ModConfigSpec.BooleanValue useVanillaCheck;
        // public static ModConfigSpec.BooleanValue realisticSnowyChange;

        public static ModConfigSpec.BooleanValue flowerOnGrass;
        public static ModConfigSpec.BooleanValue seasonalGrassColorChange;
        public static ModConfigSpec.BooleanValue seasonalColorChangeExtend;
        public static ModConfigSpec.BooleanValue smootherSeasonalGrassColorChange;


        public static ModConfigSpec.BooleanValue snowUnderFence;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Renderer");
            forceChunkRenderUpdate = builder.comment("When enabled, chunks will be reloaded regularly to fix rendering glitches, but this may impact performance.")
                    .define("ForceChunkRenderUpdate", true);
            enhancementChunkRenderUpdate = builder.comment("Enhanced reload, which will refresh all sections periodically.")
                    .define("EnhancementChunkRenderUpdate", false);
            topFaceCulling = builder.comment("Cull the top face if snowy model is applied.")
                    .define("CullTopFaceWithSnow", false);

            resetRendererAfterSleep = builder.comment("Whether to reset the renderer after waking up.")
                    .define("ResetRendererAfterSleep", false);

            useVanillaCheck = builder.comment("Use Minecraft’s default lighting rules to decide if snow should fall.")
                    .define("UseVanillaSnowCheck", false);


            // realisticSnowyChange = builder.comment("Snow cover updates with a delay after block changes, making it look more natural. This uses more performance. Do not enable if the common config’s 'RealisticSnowyChange' is also enabled.")
            //         .define("RealisticSnowyChange", false);
            snowUnderFence = builder.comment("Blocks underneath solid blocks etc. may also be covered with snow.")
                    .define("SnowUnderShadow", false);

            seasonalGrassColorChange = builder.comment("Changes grass and leaf colors with seasons visually.")
                    .define("SeasonalGrassColorChange", true);
            seasonalColorChangeExtend = builder.comment("Birch, spruce, and mangrove leaves colors also have seasonal changes.")
                    .define("SeasonalColorChangeExtend", true);

            smootherSeasonalGrassColorChange = builder.comment("When applying changes, perform mean calculation based on the percentage progress of the solar term instead of using a fixed value.")
                    .define("SmootherSeasonalGrassColorChange", true);
            flowerOnGrass = builder.comment("In spring, grass blocks will occasionally have small flowers on them.")
                    .define("FlowerOnGrass", true);
            builder.pop();
        }
    }

    public static class Sound {
        public static ModConfigSpec.BooleanValue naturalSound;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Sound");
            naturalSound = builder.comment("Listen to the sounds of nature.")
                    .define("NaturalSound", true);
            builder.pop();
        }
    }


    public static class Particle {
        public static ModConfigSpec.BooleanValue seasonParticle;

        public static ModConfigSpec.BooleanValue butterfly;
        public static ModConfigSpec.IntValue butterflySpawnWeight;
        public static ModConfigSpec.BooleanValue fallenLeaves;
        public static ModConfigSpec.IntValue fallenLeavesDropWeight;
        public static ModConfigSpec.BooleanValue firefly;
        public static ModConfigSpec.IntValue fireflySpawnWeight;
        public static ModConfigSpec.BooleanValue wildGoose;
        public static ModConfigSpec.IntValue wildGooseSpawnWeight;
        public static ModConfigSpec.BooleanValue seasonGreenhouse;
        public static ModConfigSpec.IntValue SeasonGreenhouseParticleSpawnCount;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Particle");
            seasonParticle = builder.comment("See butterflies in the spring, fireflies in the summer, and fallen leaves.")
                    .define("SeasonalParticles", true);

            butterfly = builder.comment("In spring, butterflies fly over the flowers.")
                    .define("Butterfly", true);
            butterflySpawnWeight = builder.comment("The interval/delay of butterfly particles. Higher values make butterflies appear less frequently.")
                    .defineInRange("ButterflySpawnDelay", 10, 1, 10000);

            fallenLeaves = builder.comment("Leaf blocks will drop leaves, and most frequently in the fall.")
                    .define("FallenLeaves", true);
            fallenLeavesDropWeight = builder.comment("The interval/delay of fallen leaf particles. Higher values make butterflies appear less frequently.")
                    .defineInRange("FallenLeavesDropDelay", 10, 1, 10000);

            firefly = builder.comment("In the summer evenings, you can see fireflies beside the flowers.")
                    .define("Firefly", true);
            fireflySpawnWeight = builder.comment("The interval/delay of firefly particles. Higher values make butterflies appear less frequently.")
                    .defineInRange("FireflySpawnDelay", 10, 1, 10000);

            wildGoose = builder.comment("When the grass and trees turn yellow, the wild geese fly south.")
                    .define("WildGoose", true);
            wildGooseSpawnWeight = builder.comment("The interval/delay of wild goose particles. Higher values make butterflies appear less frequently.")
                    .defineInRange("WildGooseSpawnDelay", 10, 1, 10000);


            seasonGreenhouse = builder.comment("When the season core block is active, emits soft light particles to indicate the growth environment.")
                    .define("SeasonGreenhouse", true);
            SeasonGreenhouseParticleSpawnCount = builder.comment("Number of particles emitted by the season greenhouse effect. The higher the value, the denser the effect.")
                    .defineInRange("SeasonGreenhouseParticleSpawnCount", 30, 0, 160);


            builder.pop();
        }
    }

    public static class Weather {
        public static ModConfigSpec.IntValue weatherBufferDistance;
        public static ModConfigSpec.DoubleValue weatherTransitionSpeed;
        public static ModConfigSpec.BooleanValue weatherFrontBias;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Weather");
            weatherBufferDistance = builder.comment("Radius (in blocks) around the player within which weather is sampled and averaged to create smoother local weather transitions.")
                    .defineInRange("WeatherBufferDistance", 6, 1, 80);
            weatherTransitionSpeed = builder.comment(
                            "Speed at which local weather transitions occur. Higher values mean faster transitions.")
                    .defineInRange("WeatherTransitionSpeed", 0.008d, 0.0008d, 0.08d);
            weatherFrontBias = builder.comment(
                            "Whether to apply stronger sampling weight in the direction the player is facing.")
                    .define("WeatherFrontBias", true);
            builder.pop();
        }
    }

    private static boolean topFaceCulling = false;

    public static boolean isTopFaceCulling() {
        return topFaceCulling;
    }

    public static void UpdateConfig(ModConfigEvent modConfigEvent) {
        if (!(modConfigEvent instanceof ModConfigEvent.Unloading)
                && modConfigEvent.getConfig().getSpec() == CLIENT_CONFIG) {
            topFaceCulling = Renderer.topFaceCulling.get();
        }
    }
}
