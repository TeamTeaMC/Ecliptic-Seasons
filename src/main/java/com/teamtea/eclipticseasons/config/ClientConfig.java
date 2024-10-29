package com.teamtea.eclipticseasons.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig
{

    public static final ForgeConfigSpec CLIENT_CONFIG = new ForgeConfigSpec.Builder().configure(ClientConfig::new).getRight();

    protected ClientConfig(ForgeConfigSpec.Builder builder)
    {
        GUI.load(builder);
        Renderer.load(builder);
        Sound.load(builder);
        Particle.load(builder);
    }

    public static class GUI
    {
        public static ForgeConfigSpec.IntValue playerTemperatureX;
        public static ForgeConfigSpec.IntValue playerTemperatureY;
        public static ForgeConfigSpec.BooleanValue debugInfo;

        private static void load(ForgeConfigSpec.Builder builder)
        {
            builder.push("GUI");
            playerTemperatureX = builder.comment("The position X of Player Temperature UI")
                    .defineInRange("PlayerTemperatureX", 10, Integer.MIN_VALUE, Integer.MAX_VALUE);
            playerTemperatureY = builder.comment("The position Y of Player Temperature UI")
                    .defineInRange("PlayerTemperatureY", 40, Integer.MIN_VALUE, Integer.MAX_VALUE);
            debugInfo = builder.comment("Info used for development.")
                    .define("DebugInfo", false);
            builder.pop();
        }
    }

    public static class Renderer
    {
        public static ForgeConfigSpec.BooleanValue forceChunkRenderUpdate;
        public static ForgeConfigSpec.BooleanValue useVanillaCheck;
        public static ForgeConfigSpec.IntValue weatherBufferDistance;

        public static ForgeConfigSpec.BooleanValue snowyWinter;
        public static ForgeConfigSpec.BooleanValue deeperSnow;
        public static ForgeConfigSpec.BooleanValue underSnow;

        public static ForgeConfigSpec.BooleanValue seasonalGrassColorChange;
        public static ForgeConfigSpec.BooleanValue flowerOnGrass;

        private static void load(ForgeConfigSpec.Builder builder)
        {
            builder.push("Renderer");
            forceChunkRenderUpdate = builder.comment("Force to update chunk rendering.")
                    .define("ForceChunkRenderUpdate", true);
            useVanillaCheck = builder.comment("Force to update chunk rendering.")
                    .define("useVanillaCheck", false);
            snowyWinter = builder.comment("If snow falls during cold weather, it will gradually cover all solid blocks and grass.")
                    .define("SnowyWinter", true);
            deeperSnow = builder.comment("Occasionally a thicker layer of snow will cover the flowers and grass, especially.")
                    .define("DeeperSnow", false);
            underSnow = builder.comment("Blocks below fences and bamboo will also accumulate snow.")
                    .define("UnderSnow", false);

            weatherBufferDistance = builder.comment("Modify the buffer distance for local weather changes.")
                    .defineInRange("WeatherBufferDistance", 16, 1, 80);


            seasonalGrassColorChange = builder.comment("The colors of the grass and leaves change with the time of year.")
                    .define("SeasonalGrassColorChange", true);
            flowerOnGrass = builder.comment("In spring, grass blocks will occasionally have small flowers on them.")
                    .define("FlowerOnGrass", true);

            builder.pop();
        }
    }

    public static class Sound
    {
        public static ForgeConfigSpec.BooleanValue sound;

        private static void load(ForgeConfigSpec.Builder builder)
        {
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
}
