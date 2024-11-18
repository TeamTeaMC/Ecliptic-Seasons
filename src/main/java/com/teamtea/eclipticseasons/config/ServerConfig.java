package com.teamtea.eclipticseasons.config;


import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    public static final ModConfigSpec SERVER_CONFIG = new ModConfigSpec.Builder().configure(ServerConfig::new).getRight();

    protected ServerConfig(ModConfigSpec.Builder builder) {
        Season.load(builder);
        Weather.load(builder);
        Temperature.load(builder);
        Crop.load(builder);
        Animal.load(builder);
        Debug.load(builder);
        Map.load(builder);

    }

    public static class Debug {
        public static ModConfigSpec.BooleanValue debugMode;
        public static ModConfigSpec.BooleanValue notLightAbove;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Debug");
            debugMode = builder.comment("Enable debug option to detect illegal use of functions.")
                    .define("Debug", false);
            notLightAbove = builder.comment("Without snowy block under the light blocks which level is 0.")
                    .define("NotSnowyUnderLight0", false);
            builder.pop();
        }
    }

    public static class Temperature {
        public static ModConfigSpec.BooleanValue iceMelt;
        public static ModConfigSpec.BooleanValue heatStroke;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Temperature");
            iceMelt = builder.comment("Ice or snow layer will melt in warm place.")
                    .define("IceAndSnowMelt", false);
            heatStroke = builder.comment("Add heat stroke effect in summer noon while in hot biome.")
                    .define("HeatStroke", true);
            builder.pop();
        }
    }

    public static class Season {
        public static ModConfigSpec.BooleanValue enableInform;
        public static ModConfigSpec.IntValue lastingDaysOfEachTerm;
        public static ModConfigSpec.IntValue initialSolarTermIndex;


        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Season");
            lastingDaysOfEachTerm = builder.comment("The lasting days of each term (24 in total).")
                    .defineInRange("LastingDaysOfEachTerm", 7, 1, 1000);
            initialSolarTermIndex = builder.comment("The index of the initial solar term.")
                    .defineInRange("InitialSolarTermIndex", 1, 1, 24);

            enableInform = builder.comment("Enable solar term change inform.")
                    .define("EnableInform", true);


            builder.pop();
        }
    }

    public static class Crop {


        public static ModConfigSpec.BooleanValue enableCrop;
        public static ModConfigSpec.DoubleValue cropGrowChanceInWrongSeason;
        public static ModConfigSpec.BooleanValue enableCropHumidityControl;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Crop");
            enableCrop = builder.comment("Enable crop season control.")
                    .define("EnableSeasonalCrop", true);
            cropGrowChanceInWrongSeason = builder.comment("How much chance can crop grow in wrong season.")
                    .defineInRange("CropGrowChanceInWrongSeason", 0.25, 0, 1);
            enableCropHumidityControl = builder.comment("Enable crop humidity control.")
                    .define("EnableCropHumidityControl", true);
            builder.pop();
        }
    }

    public static class Animal {


        public static ModConfigSpec.BooleanValue enableBreed;
        public static ModConfigSpec.BooleanValue enableBee;
        public static ModConfigSpec.BooleanValue enableFishing;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Animal");
            enableBreed = builder.comment("Enable seasonal animal breed.")
                    .define("EnableSeasonalBreed", false);

            enableBee = builder.comment("Enable seasonal bee behavior, bee would like spring and not like winter and cold.")
                    .define("EnableSeasonalBee", false);

            enableFishing = builder.comment("Enable seasonal fishing behavior, let enjoy summer.")
                    .define("EnableSeasonalFishing", false);
            builder.pop();
        }
    }

    public static class Weather {

        public static ModConfigSpec.BooleanValue useSolarWeather;
        public static ModConfigSpec.IntValue rainChanceMultiplier;
        public static ModConfigSpec.IntValue thunderChanceMultiplier;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Weather");
            useSolarWeather = builder.comment("Enable solar term weather system with biome.")
                    .define("UseSolarWeather", true);
            rainChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of rain, the range should be between 0 and 1000.")
                    .defineInRange("RainChancePercentMultiplier", 40, 0, 1000);
            thunderChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of thunder in the rain, the range should be between 0 and 1000.")
                    .defineInRange("ThunderChancePercentMultiplier", 80, 0, 1000);
            builder.pop();
        }
    }

    public static class Map {
        public static ModConfigSpec.BooleanValue delayedUpdates;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Map");
            delayedUpdates = builder.comment("When snow falls, the top block does not immediately become snowy if the height map change.")
                    .define("RealisticSnowyChange", true);
            builder.pop();
        }
    }
}

