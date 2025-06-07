package com.teamtea.eclipticseasons.config;


import com.teamtea.eclipticseasons.compat.CompatModule;
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
        public static ModConfigSpec.IntValue minChunkCompileWaringTime;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Debug");

            debugInfo = builder.comment("Info used for development shown in GUI.")
                    .define("DebugInfo", false);
            minChunkCompileWaringTime = builder.comment("If a render chunk compilation takes longer than expected, a warning will be emitted in the log.")
                    .defineInRange("MinChunkCompileWaringTime", 100, 5, 2000);
            builder.pop();
        }
    }

    public static class GUI {
        public static ModConfigSpec.BooleanValue agriculturalInformation;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("GUI");

            agriculturalInformation = builder.comment("Displays the season and humidity levels suitable for growing crops.")
                    .define("AgriculturalInformation", true);
            builder.pop();
        }
    }

    public static class Renderer {
        public static ModConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ModConfigSpec.BooleanValue enhancementChunkRenderUpdate;
        public static ModConfigSpec.BooleanValue resetRendererAfterSleep;

        public static ModConfigSpec.BooleanValue useVanillaCheck;
        public static ModConfigSpec.BooleanValue realisticSnowyChange;

        public static ModConfigSpec.BooleanValue flowerOnGrass;
        public static ModConfigSpec.BooleanValue seasonalGrassColorChange;


        public static ModConfigSpec.BooleanValue snowUnderFence;
        public static ModConfigSpec.BooleanValue snowUnderTree;
        public static ModConfigSpec.BooleanValue snowyTree;
        public static ModConfigSpec.BooleanValue snowTransitionBlend;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Renderer");
            forceChunkRenderUpdate = builder.comment("Force to update chunk rendering.")
                    .define("ForceChunkRenderUpdate", true);
            enhancementChunkRenderUpdate = builder.comment("Enhanced reload, which will refresh all sections periodically.")
                    .define("EnhancementChunkRenderUpdate", false);
            resetRendererAfterSleep = builder.comment("Whether to reset the renderer after waking up.")
                    .define("ResetRendererAfterSleep", false);

            useVanillaCheck = builder.comment("Determines whether snow is falling based on vanilla lighting checks.")
                    .define("UseVanillaCheck", false);


            realisticSnowyChange = builder.comment("When the block is updated, the snow cover will not refresh immediately, but will be updated after a delay. Please note that this will consume more performance and should not be open when 'RealisticSnowyChange' of common config is true.")
                    .define("RealisticSnowyChange", true);
            snowUnderFence = builder.comment("Blocks underneath fences etc. may also be covered with snow.")
                    .define("SnowUnderFence", true);
            snowyTree = builder.comment("Not just the top layer—now even the leaves below are dusted with frost and snow.")
                    .define("SnowyTree", true);
            snowUnderTree = builder.comment("Blocks under tree may also be covered with snow, note that this is only a client-side effect.")
                    .define("snowUnderTree", false);
            snowTransitionBlend = builder.comment("Smooths the transition between biome and lighting edges using noise. Improves visual appearance but adds extra rendering load (about 10% during snowfall). Not recommended for survival mode.")
                    .define("SnowTransitionBlend", false);

            seasonalGrassColorChange = builder.comment("The colors of the grass and leaves change with the time of year.")
                    .define("SeasonalGrassColorChange", true);
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

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Particle");
            seasonParticle = builder.comment("See butterflies in the spring, fireflies in the summer, and fallen leaves.")
                    .define("SeasonParticle", true);
            butterfly = builder.comment("In spring, butterflies fly over the flowers.")
                    .define("Butterfly", true);
            butterflySpawnWeight = builder.comment("The difficulty multiplier of butterfly particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("butterflySpawnWeight", 10, 1, 10000);

            fallenLeaves = builder.comment("Leaf blocks will drop leaves, and most frequently in the fall.")
                    .define("FallenLeaves", true);
            fallenLeavesDropWeight = builder.comment("The difficulty multiplier of fallen leaves particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("FallenLeavesDropWeight", 10, 1, 10000);

            firefly = builder.comment("In the summer evenings, you can see fireflies beside the flowers.")
                    .define("Firefly", true);
            fireflySpawnWeight = builder.comment("The difficulty multiplier of firefly particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("FireflySpawnWeight", 10, 1, 10000);

            wildGoose = builder.comment("When the grass and trees turn yellow, the wild geese fly south.")
                    .define("WildGoose", true);
            wildGooseSpawnWeight = builder.comment("The difficulty multiplier of wild geese particles, the value should be between 1-10000, the default is 10.")
                    .defineInRange("WildGooseSpawnWeight", 10, 1, 10000);

            builder.pop();
        }
    }

    public static class Weather {
        public static ModConfigSpec.IntValue weatherBufferDistance;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Weather");
            weatherBufferDistance = builder.comment("Modify the buffer distance for local weather changes.")
                    .defineInRange("WeatherBufferDistance", 16, 1, 80);
            builder.pop();
        }
    }
}
