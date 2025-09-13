package com.teamtea.eclipticseasons.config;

import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

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

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Debug");
            debugInfo = builder.comment("Info used for development.")
                    .define("DebugInfo", false);
            smoothSnowyEdges = builder.comment("Render snow edge overlay on neighbors for smoother snowy transitions.")
                    .define("SmoothSnowyEdges", false);
            builder.pop();
        }
    }

    public static class GUI {

        public static ForgeConfigSpec.BooleanValue agriculturalInformation;
        public static ForgeConfigSpec.BooleanValue itemInformation;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("GUI");
            agriculturalInformation = builder.comment("Displays the season and humidity levels suitable for growing crops.")
                    .define("AgriculturalInformation", true);
            itemInformation = builder.comment("Displays the use or source of a item.")
                    .define("ItemInformation", true);
            builder.pop();
        }
    }

    public static class Renderer {
        public static ForgeConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue enhancementChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue resetRendererAfterSleep;
        public static ForgeConfigSpec.BooleanValue topFaceCulling;

        public static ForgeConfigSpec.BooleanValue useVanillaCheck;

        public static ForgeConfigSpec.BooleanValue snowUnderFence;

        public static ForgeConfigSpec.BooleanValue seasonalGrassColorChange;
        public static ForgeConfigSpec.BooleanValue seasonalColorChangeExtend;
        public static ForgeConfigSpec.BooleanValue smootherSeasonalGrassColorChange;

        public static ForgeConfigSpec.BooleanValue flowerOnGrass;

        private static void load(ForgeConfigSpec.Builder builder) {
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
        public static ForgeConfigSpec.BooleanValue sound;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Sound");
            sound = builder.comment("Ambient Sound.")
                    .define("Sound", true);
            builder.pop();
        }
    }

    public static class Particle {
        public static ForgeConfigSpec.BooleanValue seasonParticle;

        public static ForgeConfigSpec.BooleanValue butterfly;
        public static ForgeConfigSpec.IntValue butterflySpawnWeight;
        public static ForgeConfigSpec.BooleanValue fallenLeaves;
        public static ForgeConfigSpec.IntValue fallenLeavesDropWeight;
        public static ForgeConfigSpec.BooleanValue firefly;
        public static ForgeConfigSpec.IntValue fireflySpawnWeight;
        public static ForgeConfigSpec.BooleanValue wildGoose;
        public static ForgeConfigSpec.IntValue wildGooseSpawnWeight;
        public static ForgeConfigSpec.BooleanValue seasonGreenhouse;
        public static ForgeConfigSpec.IntValue SeasonGreenhouseParticleSpawnCount;

        private static void load(ForgeConfigSpec.Builder builder) {
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
        public static ForgeConfigSpec.IntValue weatherBufferDistance;
        public static ForgeConfigSpec.DoubleValue weatherTransitionSpeed;
        public static ForgeConfigSpec.BooleanValue weatherFrontBias;

        private static void load(ForgeConfigSpec.Builder builder) {
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
