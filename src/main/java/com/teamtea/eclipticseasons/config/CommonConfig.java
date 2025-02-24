package com.teamtea.eclipticseasons.config;

import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public static final ForgeConfigSpec SERVER_CONFIG = new ForgeConfigSpec.Builder().configure(CommonConfig::new).getRight();

    protected CommonConfig(ForgeConfigSpec.Builder builder) {
        Temperature.load(builder);
        Season.load(builder);
        Crop.load(builder);
        Debug.load(builder);
        CompatModule.CommonConfig.load(builder);
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


            enableInform = builder.comment("Enable solar term change inform.")
                    .define("EnableInform", true);

            rainChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of rain, the range should be between 0 and 1000.")
                    .defineInRange("RainChancePercentMultiplier", 60, 0, 1000);
            builder.pop();
        }
    }

    public static class Crop {
        public static ForgeConfigSpec.BooleanValue enableCrop;
        public static ForgeConfigSpec.BooleanValue enableCropHumidityControl;
        public static ForgeConfigSpec.DoubleValue cropGrowChanceInWrongSeason;
        public static ForgeConfigSpec.DoubleValue cropGrowChanceInWrongHumidity;
        public static ForgeConfigSpec.IntValue greenHouseMaxDiameter;
        public static ForgeConfigSpec.IntValue greenHouseMaxHeight;
        public static ForgeConfigSpec.BooleanValue complexGreenHouseCheck;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Crop");
            enableCrop = builder.comment("Enable crop season control.")
                    .define("EnableSeasonalCrop", true);
            cropGrowChanceInWrongSeason = builder.comment("How much chance can crop grow in wrong season.")
                    .defineInRange("CropGrowChanceInWrongSeason", 0.05, 0, 1);
            enableCropHumidityControl = builder.comment("Enable crop humidity control.")
                    .define("EnableCropHumidityControl", true);
            cropGrowChanceInWrongHumidity = builder.comment("How much base chance can crop grow in wrong humidity.")
                    .defineInRange("CropGrowChanceInWrongHumidity", 0.25, 0.0001, 0.9999);
            greenHouseMaxDiameter =builder.comment("The maximum effective diameter of the greenhouse.")
                    .defineInRange("GreenHouseMaxDiameter", 24, 5, 256);
            greenHouseMaxHeight =builder.comment("The maximum effective diameter of the greenhouse.")
                    .defineInRange("GreenHouseMaxDiameter", 10, 3, 128);
            complexGreenHouseCheck = builder.comment("Whether to enable complex shape checking.")
                    .define("ComplexGreenHouseCheck", true);
            builder.pop();
        }
    }
}

