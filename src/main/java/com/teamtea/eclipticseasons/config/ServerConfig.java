package com.teamtea.eclipticseasons.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public static final ForgeConfigSpec SERVER_CONFIG = new ForgeConfigSpec.Builder().configure(ServerConfig::new).getRight();

    protected ServerConfig(ForgeConfigSpec.Builder builder) {
        Season.load(builder);
        Weather.load(builder);
        Temperature.load(builder);
        Crop.load(builder);
        Debug.load(builder);
    }

    public static class Debug {
        public static ForgeConfigSpec.BooleanValue debugMode;
        public static ForgeConfigSpec.BooleanValue notSnowyUnderLight;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Debug");
            debugMode = builder.comment("Enable debug option to detect illegal use of functions.")
                    .define("Debug", false);
            notSnowyUnderLight = builder.comment("Without snowy block under the light blocks which level is 0.")
                    .define("NotSnowyUnderLight0", false);
            builder.pop();
        }
    }

    public static class Temperature {
        public static ForgeConfigSpec.BooleanValue iceMelt;
        public static ForgeConfigSpec.BooleanValue heatStroke;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Temperature");
            iceMelt = builder.comment("Ice or snow layer will melt in warm place..")
                    .define("IceAndSnowMelt", false);
            heatStroke = builder.comment("Add heat stroke effect in summer noon while in hot biome.")
                    .define("HeatStroke", true);
            builder.pop();
        }
    }

    public static class Season {
        public static ForgeConfigSpec.BooleanValue enableInform;
        public static ForgeConfigSpec.IntValue lastingDaysOfEachTerm;
        public static ForgeConfigSpec.IntValue initialSolarTermIndex;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Season");
            lastingDaysOfEachTerm = builder.comment("The lasting days of each term (24 in total).")
                    .defineInRange("LastingDaysOfEachTerm", 7, 1, 30);
            initialSolarTermIndex = builder.comment("The index of the initial solar term.")
                    .defineInRange("InitialSolarTermIndex", 1, 1, 24);
            enableInform = builder.comment("Enable solar term change inform.")
                    .define("EnableInform", true);
            builder.pop();
        }
    }

    public static class Crop {

        public static ForgeConfigSpec.BooleanValue enableCrop;
        public static ForgeConfigSpec.DoubleValue cropGrowChanceInWrongSeason;
        public static ForgeConfigSpec.BooleanValue enableCropHumidityControl;

        private static void load(ForgeConfigSpec.Builder builder) {
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

    public static class Weather {
        public static ForgeConfigSpec.BooleanValue useSolarWeather;
        public static ForgeConfigSpec.IntValue rainChanceMultiplier;
        public static ForgeConfigSpec.IntValue thunderChanceMultiplier;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Weather");
            useSolarWeather = builder.comment("Enable solar term weather system with biome.")
                    .define("UseSolarWeather", false);
            rainChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of rain, the range should be between 0 and 1000.")
                    .defineInRange("RainChancePercentMultiplier", 60, 0, 1000);
            thunderChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of thunder in the rain, the range should be between 0 and 1000.")
                    .defineInRange("ThunderChancePercentMultiplier", 80, 0, 1000);
            builder.pop();
        }
    }
}

