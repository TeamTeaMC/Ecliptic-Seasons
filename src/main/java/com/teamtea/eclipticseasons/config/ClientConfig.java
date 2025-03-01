package com.teamtea.eclipticseasons.config;

import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraftforge.common.ForgeConfigSpec;

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

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Debug");
            debugInfo = builder.comment("Info used for development.")
                    .define("DebugInfo", false);
            builder.pop();
        }
    }
    public static class GUI {

        public static ForgeConfigSpec.BooleanValue agriculturalInformation;
        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("GUI");
            agriculturalInformation = builder.comment("Displays the season and humidity levels suitable for growing crops.")
                    .define("AgriculturalInformation", true);
            builder.pop();
        }
    }

    public static class Renderer {
        public static ForgeConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue enhancementChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue useVanillaCheck;

        public static ForgeConfigSpec.BooleanValue betterSnow;
        public static ForgeConfigSpec.BooleanValue realisticSnowyChange;

        public static ForgeConfigSpec.BooleanValue seasonalGrassColorChange;
        public static ForgeConfigSpec.BooleanValue flowerOnGrass;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Renderer");
            forceChunkRenderUpdate = builder.comment("Force to update chunk rendering.")
                    .define("ForceChunkRenderUpdate", true);
            enhancementChunkRenderUpdate = builder.comment("Enhanced reload, which will refresh all sections periodically.")
                    .define("EnhancementChunkRenderUpdate", false);

            useVanillaCheck = builder.comment("Determines whether snow is falling based on vanilla lighting checks.")
                    .define("UseVanillaCheck", false);

            realisticSnowyChange = builder.comment("When the block is updated, the snow cover will not refresh immediately, but will be updated after a delay. Please note that this will consume more performance..")
                    .define("RealisticSnowyChange", true);
            betterSnow = builder.comment("Blocks underneath fences etc. may also be covered with snow.")
                    .define("SnowUnderFence", true);


            seasonalGrassColorChange = builder.comment("The colors of the grass and leaves change with the time of year.")
                    .define("SeasonalGrassColorChange", true);
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

        private static void load(ForgeConfigSpec.Builder builder) {
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
        public static ForgeConfigSpec.IntValue weatherBufferDistance;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Weather");
            weatherBufferDistance = builder.comment("Modify the buffer distance for local weather changes.")
                    .defineInRange("WeatherBufferDistance", 16, 1, 80);
            builder.pop();
        }
    }
}
