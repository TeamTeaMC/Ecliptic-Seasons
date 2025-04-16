package com.teamtea.eclipticseasons.config;

import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

public class CommonConfig {
    public static final ForgeConfigSpec COMMON_CONFIG = new ForgeConfigSpec.Builder().configure(com.teamtea.eclipticseasons.config.CommonConfig::new).getRight();

    protected CommonConfig(ForgeConfigSpec.Builder builder) {
        Season.load(builder);
        Weather.load(builder);
        Temperature.load(builder);
        Crop.load(builder);
        CompatModule.CommonConfig.load(builder);
        Debug.load(builder);
    }

    // public static class Compat {
    //     public static ForgeConfigSpec.BooleanValue sereneSeasons;
    //
    //     private static void load(ForgeConfigSpec.Builder builder) {
    //         builder.push("Compat");
    //         CompatModule.CommonConfig.load(builder);
    //         sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
    //                 .define("SereneSeasonsCropTag", true);
    //         builder.pop();
    //     }
    // }

    public static class Debug {
        public static ForgeConfigSpec.BooleanValue logIllegalUse;
        public static ForgeConfigSpec.BooleanValue notLightAbove;

        public static ForgeConfigSpec.BooleanValue snowyFullCollisionShape;
        public static ForgeConfigSpec.BooleanValue snowOverlayGlowingBlock;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Debug");
            logIllegalUse = builder.comment("Enable debug option to detect illegal use of functions.")
                    .define("LogIllegalUse", false);
            notLightAbove = builder.comment("Without snowy block under the light blocks which level is 0.")
                    .define("NotSnowyUnderLight0", false);
            snowyFullCollisionShape = builder.comment("Snow overlay block if has full collision shape not just full render shape.")
                    .define("SnowyFullCollisionShape", false);
            snowOverlayGlowingBlock = builder.comment("Snow can cover the block which would lights.")
                    .define("NotSnowOverlayGlowingBlock", false);

            builder.pop();
        }
    }

    public static class Temperature {
        public static ForgeConfigSpec.BooleanValue heatStroke;
        public static ForgeConfigSpec.BooleanValue iceMelt;
        public static ForgeConfigSpec.BooleanValue snowDown;

        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Temperature");
            iceMelt = builder.comment("Ice or snow layer will melt in warm time.")
                    .define("IceAndSnowMelt", false);
            snowDown = builder.comment("It will snow in cold time.")
                    .define("IceAndSnow", false);
            heatStroke = builder.comment("Add heat stroke effect in summer noon while in hot biome.")
                    .define("HeatStroke", true);
            builder.pop();
        }
    }

    public static class Season {
        public static ForgeConfigSpec.BooleanValue enableInform;
        public static ForgeConfigSpec.BooleanValue enableInformIcon;
        public static ForgeConfigSpec.BooleanValue calendarItemHint;

        public static ForgeConfigSpec.IntValue lastingDaysOfEachTerm;
        public static ForgeConfigSpec.IntValue initialSolarTermIndex;
        public static ForgeConfigSpec.BooleanValue daylightChange;
        public static ForgeConfigSpec.ConfigValue<List<? extends String>> validDimensions;
        public static ForgeConfigSpec.BooleanValue shouldInitWeather;

        public static ForgeConfigSpec.BooleanValue snowyWinter;
        public static ForgeConfigSpec.BooleanValue notSnowyNearGlowingBlock;
        public static ForgeConfigSpec.IntValue notSnowyNearGlowingBlockLevel;


        private static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Season");
            lastingDaysOfEachTerm = builder.comment("The lasting days of each term, while 4 seasons in 1 year, 6 terms in 1 season.")
                    .defineInRange("LastingDaysOfEachTerm", 7, 1, 5000);
            initialSolarTermIndex = builder.comment("The index of the initial solar term, and note it only can be used to first start the world with the mod.")
                    .defineInRange("InitialSolarTermIndex", 1, 1, 24);
            enableInform = builder.comment("Enable solar term change inform.")
                    .define("EnableInform", true);
            enableInformIcon = builder.comment("Whether send inform with icon.")
                    .define("EnableInformIcon", true);
            calendarItemHint = builder.comment("Whether to pop up the solar term reminder when the calendar item cannot be placed.")
                    .define("CalendarItemHint", false);
            daylightChange = builder.comment("In summer, the days are long and the nights are short, while in winter, the days are short and the nights are long.")
                    .define("DynamicDaylightDuration", true);
            validDimensions = builder.comment("Which dimensions will have season effects? Note that it must be natrual and have time lapse.")
                    .defineListAllowEmpty("ValidDimensions",
                            () -> List.of(Level.OVERWORLD.location().toString()),
                            o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
            shouldInitWeather = builder.comment("Set it true to initialize weather and snow when loading the mod or level for the first time.")
                    .define("ShouldInitWeather", true);

            snowyWinter = builder.comment("If snow falls during cold weather, it will gradually cover all solid blocks and grass.")
                    .define("SnowyWinter", true);
            notSnowyNearGlowingBlock = builder.comment("Snow will not appear in overly bright areas, here define restriction levels.")
                    .define("NotSnowyNearGlowingBlock", true);
            notSnowyNearGlowingBlockLevel = builder.comment("Snow will not appear in overly bright areas.")
                    .defineInRange("NotSnowyNearGlowingBlockLevel", 10, 1, 15);
            builder.pop();
        }
    }

    public static class Crop {

        public static ForgeConfigSpec.BooleanValue enableCrop;
        public static ForgeConfigSpec.DoubleValue cropGrowChanceInWrongSeason;
        public static ForgeConfigSpec.DoubleValue cropGrowChanceInWrongHumidity;
        public static ForgeConfigSpec.BooleanValue enableCropHumidityControl;
        public static ForgeConfigSpec.IntValue greenHouseMaxDiameter;
        public static ForgeConfigSpec.IntValue greenHouseMaxHeight;
        public static ForgeConfigSpec.IntValue darkGreenhouseFailChance;

        public static ForgeConfigSpec.BooleanValue complexGreenHouseCheck;
        public static ForgeConfigSpec.BooleanValue useDefaultValue;

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
            greenHouseMaxDiameter = builder.comment("The maximum effective diameter of the greenhouse.")
                    .defineInRange("GreenHouseMaxDiameter", 32, 5, 256);
            greenHouseMaxHeight =builder.comment("The maximum effective diameter of the greenhouse.")
                    .defineInRange("GreenHouseMaxHeight", 10, 3, 128);
            darkGreenhouseFailChance = builder.comment("The possibility of crops not growing when there is insufficient sunlight in green house.")
                    .defineInRange("DarkGreenhouseFailChance", 2000, 0, 10000);

            complexGreenHouseCheck = builder.comment("Whether to enable complex shape checking.")
                    .define("ComplexGreenHouseCheck", true);
            useDefaultValue = builder.comment("If a crop is not registered for a season or humid type, default values will be used.")
                    .define("RegisterCropDefaultValue", false);
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
                    .define("UseSolarWeather", true);
            rainChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of rain, the range should be between 0 and 1000.")
                    .defineInRange("RainChancePercentMultiplier", 40, 0, 1000);
            thunderChanceMultiplier = builder.comment("Set the percentage multiplier of the probability of thunder in the rain, the range should be between 0 and 1000.")
                    .defineInRange("ThunderChancePercentMultiplier", 20, 0, 1000);
            builder.pop();
        }
    }

    private static boolean useSolarWeather = true;

    public static boolean isUseSolarWeather() {
        return useSolarWeather;
    }

    public static void UpdateConfig(ModConfigEvent modConfigEvent) {
        if (!(modConfigEvent instanceof ModConfigEvent.Unloading)
                && modConfigEvent.getConfig().getSpec() == COMMON_CONFIG) {
            useSolarWeather = Weather.useSolarWeather.get();
        }
    }

}

