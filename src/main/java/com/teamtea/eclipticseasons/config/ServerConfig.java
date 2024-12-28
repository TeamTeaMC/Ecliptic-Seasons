package com.teamtea.eclipticseasons.config;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Objects;

public class ServerConfig {
    public static final ModConfigSpec SERVER_CONFIG = new ModConfigSpec.Builder().configure(ServerConfig::new).getRight();

    protected ServerConfig(ModConfigSpec.Builder builder) {
        Season.load(builder);
        Weather.load(builder);
        Temperature.load(builder);
        Crop.load(builder);
        Animal.load(builder);
        Map.load(builder);

        Compat.load(builder);
        Debug.load(builder);

    }

    public static class Compat {
        public static ModConfigSpec.BooleanValue sereneSeasons;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            builder.pop();
        }
    }

    public static class Debug {
        public static ModConfigSpec.BooleanValue logIllegalUse;
        public static ModConfigSpec.BooleanValue notLightAbove;
        public static ModConfigSpec.BooleanValue iceMelt;
        public static ModConfigSpec.BooleanValue snowyFullCollisionShape;
        public static ModConfigSpec.BooleanValue snowOverlayGlowingBlock;
        public static ModConfigSpec.BooleanValue disableSnowOverlayControlTag;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Debug");
            logIllegalUse = builder.comment("Enable debug option to detect illegal use of functions.")
                    .define("LogIllegalUse", false);
            notLightAbove = builder.comment("Without snowy block under the light blocks which level is 0.")
                    .define("NoSnowyUnderLight0", false);
            iceMelt = builder.comment("Enables legacy mode for snow and ice, where snow accumulates when it's cold in snowy day and melts when it's hot.")
                    .define("LegacyIceAndSnowAccumulationMelt", false);
            snowyFullCollisionShape = builder.comment("Snow overlay block if has full collision shape not just full render shape.")
                    .define("SnowyFullCollisionShape", false);
            snowOverlayGlowingBlock = builder.comment("Snow can cover the block which would lights.")
                    .define("NotSnowOverlayGlowingBlock", false);
            disableSnowOverlayControlTag = builder.comment("Set to false to disable tag which stops block from snowy is tagged with \"eclipticseasons:snow_overlay_cannot_survive_on\".")
                    .define("DisableSnowOverlayControlTag", false);
            builder.pop();
        }
    }

    public static class Temperature {
        public static ModConfigSpec.BooleanValue heatStroke;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Temperature");
            heatStroke = builder.comment("Add heat stroke effect in summer noon while in hot biome.")
                    .define("HeatStroke", true);
            builder.pop();
        }
    }

    public static class Season {
        public static ModConfigSpec.BooleanValue enableInform;
        public static ModConfigSpec.IntValue lastingDaysOfEachTerm;
        public static ModConfigSpec.IntValue initialSolarTermIndex;
        public static ModConfigSpec.ConfigValue<List<? extends String>> validDimensions;
        public static ModConfigSpec.BooleanValue daylightChange;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Season");
            lastingDaysOfEachTerm = builder.comment("The lasting days of each term, while 4 seasons in 1 year, 6 terms in 1 season.")
                    .defineInRange("LastingDaysOfEachTerm", 7, 1, 5000);
            initialSolarTermIndex = builder.comment("The index of the initial solar term.")
                    .defineInRange("InitialSolarTermIndex", 1, 1, 24);

            enableInform = builder.comment("Enable solar term change inform.")
                    .define("EnableInform", true);
            daylightChange=builder.comment("In summer, the days are long and the nights are short, while in winter, the days are short and the nights are long.")
                    .define("DynamicDaylightDuration", true);

            validDimensions = builder.comment("Which dimensions will have season effects? Note that it must be natrual and have time lapse.")
                    .defineListAllowEmpty("ValidDimensions",
                            () -> List.of(Level.OVERWORLD.location().toString()),
                            () -> Level.OVERWORLD.location().toString(),
                            o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
            builder.pop();
        }
    }

    public static class Crop {


        public static ModConfigSpec.BooleanValue enableCrop;
        public static ModConfigSpec.DoubleValue cropGrowChanceInWrongSeason;
        public static ModConfigSpec.BooleanValue enableCropHumidityControl;
        public static ModConfigSpec.BooleanValue useDefaultValue;


        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Crop");
            enableCrop = builder.comment("Enable crop season control.")
                    .define("EnableSeasonalCrop", true);
            cropGrowChanceInWrongSeason = builder.comment("How much chance can crop grow in wrong season.")
                    .defineInRange("CropGrowChanceInWrongSeason", 0.25, 0, 1);
            enableCropHumidityControl = builder.comment("Enable crop humidity control.")
                    .define("EnableCropHumidityControl", true);
            useDefaultValue = builder.comment("If a crop is not registered for a season or humid type, default values will be used.")
                    .define("UseDefaultValue", false);
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
                    .define("ServerRealisticSnowyChange", false);
            builder.pop();
        }
    }


}

