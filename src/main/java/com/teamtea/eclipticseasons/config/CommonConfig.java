package com.teamtea.eclipticseasons.config;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.compat.CompatModule;
import lombok.Getter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;
import java.util.stream.Stream;

public class CommonConfig {
    public static final ModConfigSpec COMMON_CONFIG = new ModConfigSpec.Builder().configure(com.teamtea.eclipticseasons.config.CommonConfig::new).getRight();

    protected CommonConfig(ModConfigSpec.Builder builder) {
        Season.load(builder);
        Weather.load(builder);
        Temperature.load(builder);
        Crop.load(builder);
        Animal.load(builder);
        Map.load(builder);
        Resource.load(builder);

        CompatModule.CommonConfig.load(builder);
        Debug.load(builder);

    }

    public static class Debug {
        public static ModConfigSpec.BooleanValue logIllegalUse;
        public static ModConfigSpec.BooleanValue notLightAbove;
        public static ModConfigSpec.BooleanValue snowyFullCollisionShape;
        public static ModConfigSpec.BooleanValue snowOverlayGlowingBlock;
        public static ModConfigSpec.BooleanValue disableSnowOverlayControlTag;
        public static ModConfigSpec.BooleanValue seasonDefinition;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Debug");
            logIllegalUse = builder.comment("Enable debug option to detect illegal use of functions.")
                    .define("LogIllegalUse", false);
            notLightAbove = builder.comment("Disable snowy blocks beneath light sources with light level 0.")
                    .define("NoSnowyUnderLight0", false);
            snowyFullCollisionShape = builder.comment("Snow overlay block if has full collision shape not just full render shape.")
                    .define("SnowyFullCollisionShape", false);
            snowOverlayGlowingBlock = builder.comment("Snow can cover the block which would lights.")
                    .define("SnowOverlayGlowingBlock", false);
            disableSnowOverlayControlTag = builder.comment("Set to false to disable tag which stops block from snowy is tagged with \"eclipticseasons:snow_overlay_cannot_survive_on\".")
                    .define("DisableSnowOverlayControlTag", false);
            seasonDefinition = builder.comment("Enable the season definitions system.")
                    .define("EnableSeasonDefinition", false);
            builder.pop();
        }
    }

    public static class Temperature {
        public static ModConfigSpec.BooleanValue heatStroke;
        public static ModConfigSpec.BooleanValue iceMelt;
        public static ModConfigSpec.BooleanValue snowDown;

        private static void load(ModConfigSpec.Builder builder) {
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
        public static ModConfigSpec.BooleanValue enableInform;
        public static ModConfigSpec.BooleanValue enableInformIcon;
        public static ModConfigSpec.BooleanValue enableLocalInfoCalendar;
        public static ModConfigSpec.BooleanValue calendarItemHint;

        public static ModConfigSpec.IntValue lastingDaysOfEachTerm;
        public static ModConfigSpec.IntValue initialSolarTermIndex;

        public static ModConfigSpec.ConfigValue<List<? extends String>> validDimensions;

        public static ModConfigSpec.BooleanValue daylightChange;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> springDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> summerDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> autumnDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> winterDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> noneDayTimes;

        public static ModConfigSpec.BooleanValue snowyWinter;
        public static ModConfigSpec.BooleanValue snowyTree;
        public static ModConfigSpec.BooleanValue notSnowyNearGlowingBlock;
        public static ModConfigSpec.IntValue notSnowyNearGlowingBlockLevel;

        public static ModConfigSpec.ConfigValue<List<? extends String>> blocksNotSnowy;


        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Season");
            lastingDaysOfEachTerm = builder.comment("The lasting days of each term, while 4 seasons in 1 year, 6 terms in 1 season.")
                    .defineInRange("LastingDaysOfEachTerm", 7, 1, 5000);
            initialSolarTermIndex = builder.comment("The index of the initial solar term, and note it only can be used to first start the world with the mod.")
                    .defineInRange("InitialSolarTermIndex", 4, 1, 24);

            enableInform = builder.comment("Enable solar term change inform.")
                    .define("EnableInform", true);
            enableInformIcon = builder.comment("Whether send inform with icon.")
                    .define("EnableInformIcon", true);
            enableLocalInfoCalendar = builder.comment("Enable local calendar and local info synchronization.")
                    .define("EnableLocalInfoAndCalendar", false);
            calendarItemHint = builder.comment("Whether to pop up the solar term reminder when the calendar item cannot be placed.")
                    .define("CalendarItemHint", false);

            daylightChange = builder.comment("In summer, the days are long and the nights are short, while in winter, the days are short and the nights are long.")
                    .define("DynamicDaylightDuration", true);

            validDimensions = builder.comment("List of dimensions where season effects apply. Must be natural worlds with a day-night cycle.")
                    .defineListAllowEmpty("ValidDimensions",
                            () -> List.of(Level.OVERWORLD.location().toString()),
                            () -> Level.OVERWORLD.location().toString(),
                            o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
            springDayTimes = builder.comment("Day time length of spring, divided into six periods according to the solar term table.")
                    .defineList(List.of("SpringDayTimes"),
                            () -> List.of(10500, 11000, 11500, 12000, 12500, 13000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= 24000),
                            ModConfigSpec.Range.of(6, 6));
            summerDayTimes = builder.comment("Day time length of summer, divided into six periods according to the solar term table.")
                    .defineList(List.of("SummerDayTimes"),
                            () -> List.of(13500, 14000, 14500, 15000, 14500, 14000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= 24000),
                            ModConfigSpec.Range.of(6, 6));
            autumnDayTimes = builder.comment("Day time length of autumn, divided into six periods according to the solar term table.")
                    .defineList(List.of("AutumnDayTimes"),
                            () -> List.of(13500, 13000, 12500, 12000, 11500, 11000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= 24000),
                            ModConfigSpec.Range.of(6, 6));
            winterDayTimes = builder.comment("Day time length of winter, divided into six periods according to the solar term table.")
                    .defineList(List.of("WinterDayTimes"),
                            () -> List.of(10500, 10000, 9500, 9000, 9500, 10000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= 24000),
                            ModConfigSpec.Range.of(6, 6));
            noneDayTimes = builder.comment("Day time length of none season, divided into six periods according to the solar term table.")
                    .defineList(List.of("NoneDayTimes"),
                            () -> List.of(12000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= 24000),
                            ModConfigSpec.Range.of(1, 1));
            snowyWinter = builder.comment("If snow falls during cold weather in warm biomes, it will gradually cover all solid blocks and grass.")
                    .define("SnowyWinter", true);
            snowyTree = builder.comment("Not just the top layer, now even the leaves below are dusted with frost and snow.")
                    .define("SnowyTree", true);
            notSnowyNearGlowingBlock = builder.comment("Snow will not appear in overly bright areas, here define restriction levels.")
                    .define("NotSnowyNearGlowingBlock", true);
            notSnowyNearGlowingBlockLevel = builder.comment("Snow will not appear in overly bright areas.")
                    .defineInRange("NotSnowyNearGlowingBlockLevel", 10, 1, 15);
            blocksNotSnowy = builder.comment("Specify block IDs here to prevent those blocks from being covered by snow.")
                    .defineListAllowEmpty("ForceBlocksNotSnowy",
                            List::of,
                            () -> "",
                            o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
            builder.pop();
        }
    }

    public static class Crop {
        public static ModConfigSpec.BooleanValue enableCrop;
        public static ModConfigSpec.DoubleValue cropGrowChanceInWrongSeason;
        public static ModConfigSpec.DoubleValue cropGrowChanceInWrongHumidity;
        public static ModConfigSpec.BooleanValue enableCropHumidityControl;
        public static ModConfigSpec.BooleanValue cropHumidityTransition;

        public static ModConfigSpec.IntValue greenHouseMaxDiameter;
        public static ModConfigSpec.IntValue greenHouseMaxHeight;
        public static ModConfigSpec.IntValue darkGreenhouseFailChance;
        public static ModConfigSpec.BooleanValue complexGreenHouseCheck;
        public static ModConfigSpec.BooleanValue registerCropDefaultValue;
        public static ModConfigSpec.BooleanValue forceCompatMode;
        public static ModConfigSpec.BooleanValue simpleGreenHouse;
        public static ModConfigSpec.BooleanValue noCostHumidifier;
        public static ModConfigSpec.BooleanValue useBoxDistance;
        public static ModConfigSpec.IntValue seasonCoreRange;
        public static ModConfigSpec.BooleanValue boneMealFailureMessage;
        public static ModConfigSpec.BooleanValue boneMealConsumeOnFailure;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Crop");
            enableCrop = builder.comment("Enable crop season control.")
                    .define("EnableSeasonalCrop", true);
            cropGrowChanceInWrongSeason = builder.comment("How much grow_chance can crop grow in wrong season.")
                    .defineInRange("[Deprecated]CropGrowChanceInWrongSeason", 0.25, 0, 1);
            enableCropHumidityControl = builder.comment("Enable crop humidity control.")
                    .define("EnableCropHumidityControl", true);
            cropHumidityTransition = builder.comment("If enabled, humidity check will transition smoothly instead of snapping.")
                    .define("CropHumidityTransition", true);
            cropGrowChanceInWrongHumidity = builder.comment("How much base grow_chance can crop grow in wrong humidity.")
                    .defineInRange("[Deprecated]CropGrowChanceInWrongHumidity", 0.25, 0.0001, 0.9999);
            boneMealFailureMessage = builder.comment("Send message to player if failed to use bone meal on crop.")
                    .define("BoneMealFailureMessage", true);
            boneMealConsumeOnFailure = builder.comment("Consume anyway if failed to use bone meal on crop.")
                    .define("BoneMealConsumeOnFailure", true);
            greenHouseMaxDiameter = builder.comment("The maximum effective diameter of the greenhouse.")
                    .defineInRange("GreenHouseMaxDiameter", 32, 5, 256);
            greenHouseMaxHeight = builder.comment("The maximum effective diameter of the greenhouse.")
                    .defineInRange("GreenHouseMaxHeight", 10, 3, 128);
            darkGreenhouseFailChance = builder.comment("Chance that crops fail to grow due to low sunlight inside the greenhouse.")
                    .defineInRange("DarkGreenhouseFailChance", 2000, 0, 10000);
            simpleGreenHouse = builder.comment("Build a simple greenhouse without core blocks and humidity modifiers.")
                    .define("SimpleGreenHouseMode", false);
            noCostHumidifier = builder.comment("If true, the Humidifier block will no longer consume blocks during conversion.")
                    .define("NoCostHumidifier", false);
            seasonCoreRange = builder.comment("The working range of the Season Core block.")
                    .defineInRange("SeasonCoreRange", 15, 4, 31);
            complexGreenHouseCheck = builder.comment("Whether to enable complex shape checking.")
                    .define("ComplexGreenHouseCheck", true);
            useBoxDistance = builder.comment("Calculate the working range of the greenhouse block by box distance and not Euclidean range.")
                    .define("UseBoxDistance", true);
            registerCropDefaultValue = builder.comment("[Deprecated]If a crop is not registered for a season or humid type, default values will be used.")
                    .define("RegisterCropDefaultValue", false);
            forceCompatMode = builder.comment("Force all crops to use compatibility mode for growth control, not just those tagged as eclipticseasons:natural_plants.")
                    .define("ForceCompatMode", false);
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
        public static ModConfigSpec.BooleanValue shouldInitWeather;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Weather");
            useSolarWeather = builder.comment("Enable solar term weather system with biome.")
                    .define("UseSolarWeather", true);
            shouldInitWeather = builder.comment("Set it true to initialize weather and snow when loading the mod or level for the first time.")
                    .define("ShouldInitWeather", false);
            rainChanceMultiplier = builder.comment("Multiplier (0-1000) affecting how likely rain will occur.")
                    .defineInRange("RainChancePercentMultiplier", 40, 0, 1000);
            thunderChanceMultiplier = builder.comment("Multiplier (0-1000) affecting how likely thunder will occur.")
                    .defineInRange("ThunderChancePercentMultiplier", 20, 0, 1000);
            builder.pop();
        }
    }

    public static class Map {
        public static ModConfigSpec.BooleanValue delayedUpdates;
        public static ModConfigSpec.BooleanValue changeMapColor;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Map");
            delayedUpdates = builder.comment("Delay updating the top block to snowy when height map changes during snowfall.")
                    .define("ServerRealisticSnowyChange", false);
            changeMapColor = builder.comment("The map color of blocks will change during snow.")
                    .define("ChangeMapColor", true);
            builder.pop();
        }
    }

    public static class Resource {
        public static ModConfigSpec.BooleanValue extraSnow;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Resource");
            extraSnow = builder.comment("Enable extra built-in snow definitions resourcepack for game.")
                    .define("ExtraSnowDefinitions", false);
            builder.pop();
        }
    }
    @Getter
    private static boolean seasonDefinition = false;

    @Getter
    private static boolean useSolarWeather = true;

    @Getter
    private static boolean forceCropCompatMode = false;
    @Getter
    private static boolean snowyWinter = false;

    @Getter
    private static final int[] dayTimesForSeason = new int[SolarTerm.collectValues().length];
    @Getter
    private static boolean useDayTimes = false;
    @Getter
    private static boolean cropHumidityTransition = true;
    @Getter
    private static final Set<Block> forceBlocksNotSnowy = new HashSet<>();

    public static void UpdateConfig(ModConfigEvent modConfigEvent) {
        if (!(modConfigEvent instanceof ModConfigEvent.Unloading)
                && modConfigEvent.getConfig().getSpec() == COMMON_CONFIG) {
            useSolarWeather = Weather.useSolarWeather.get();
            forceCropCompatMode = Crop.forceCompatMode.get();
            snowyWinter = Season.snowyWinter.get();
            seasonDefinition = Debug.seasonDefinition.get();
            cropHumidityTransition = Crop.cropHumidityTransition.get();

            int[] ints = Stream.of(Season.springDayTimes, Season.summerDayTimes, Season.autumnDayTimes, Season.winterDayTimes, Season.noneDayTimes)
                    .map(ModConfigSpec.ConfigValue::get)
                    .flatMap(Collection::stream)
                    .mapToInt(Integer::intValue)
                    .toArray();
            if (ints.length == dayTimesForSeason.length) {
                System.arraycopy(ints, 0, dayTimesForSeason, 0, ints.length);
                boolean isSame = true;
                for (int i = 0; i < dayTimesForSeason.length; i++) {
                    if (dayTimesForSeason[i] != SolarTerm.get(i).getOriginalDayTime()) {
                        isSame = false;
                        break;
                    }
                }
                useDayTimes = !isSame;
            } else {
                useDayTimes = false;
                EclipticSeasons.logger("Invalid Day Times length in configuration:", ints.length);
            }

            forceBlocksNotSnowy.clear();
            for (String s : Season.blocksNotSnowy.get()) {
                Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(s));
                if (block != Blocks.AIR) {
                    forceBlocksNotSnowy.add(block);
                }
            }
        }
    }


}

