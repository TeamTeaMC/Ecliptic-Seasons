package com.teamtea.eclipticseasons.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static final ForgeConfigSpec SERVER_CONFIG = new ForgeConfigSpec.Builder().configure(CommonConfig::new).getRight();

    protected CommonConfig(ForgeConfigSpec.Builder builder) {
        Temperature.load(builder);
        Season.load(builder);
        Debug.load(builder);
    }

    public static class Debug {
        public static ForgeConfigSpec.BooleanValue debugMode;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Debug");
            debugMode = builder.comment("Enable debug option to detect illegal use of functions.")
                    .define("Debug", false);
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
        public static ForgeConfigSpec.BooleanValue enableCrop;
        public static ForgeConfigSpec.BooleanValue enableCropHumidityControl;
        public static ForgeConfigSpec.BooleanValue enableInform;
        public static ForgeConfigSpec.IntValue lastingDaysOfEachTerm;
        public static ForgeConfigSpec.IntValue initialSolarTermIndex;
        public static ForgeConfigSpec.IntValue rainChanceMultiplier;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Season");
            lastingDaysOfEachTerm = builder.comment("The lasting days of each term (24 in total).")
                    .defineInRange("LastingDaysOfEachTerm", 7, 1, 30);
            initialSolarTermIndex = builder.comment("The index of the initial solar term.")
                    .defineInRange("InitialSolarTermIndex", 1, 1, 24);

            enableCrop = builder.comment("Enable seasonal crop.")
                    .define("EnableSeasonalCrop", true);
            enableCropHumidityControl = builder.comment("Enable crop humidity control.")
                    .define("EnableCropHumidityControl", true);

            enableInform = builder.comment("Enable solar term change inform.")
                    .define("EnableInform", true);

            rainChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of rain, the range should be between 0 and 1000.")
                    .defineInRange("RainChancePercentMultiplier", 60, 0, 1000);
            builder.pop();
        }
    }

}

