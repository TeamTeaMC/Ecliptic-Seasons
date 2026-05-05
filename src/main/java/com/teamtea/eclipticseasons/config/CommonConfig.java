package com.teamtea.eclipticseasons.config;


import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.compat.CompatModule;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

public class CommonConfig {
    public static final ModConfigSpec COMMON_CONFIG = new ModConfigSpec.Builder().configure(CommonConfig::new).getRight();

    protected CommonConfig(ModConfigSpec.Builder builder) {
        Season.load(builder);
        Weather.load(builder);
        Temperature.load(builder);
        Crop.load(builder);
        Animal.load(builder);
        Map.load(builder);
        Snow.load(builder);
        CompatModule.CommonConfig.load(builder);
        Debug.load(builder);
        Resource.load(builder);
    }

    public static class Debug {
        public static ModConfigSpec.BooleanValue logIllegalUse;
        public static ModConfigSpec.BooleanValue notLightAbove;
        public static ModConfigSpec.BooleanValue snowOverlayGlowingBlock;

        public static ModConfigSpec.BooleanValue disableChunkCacheCleaner;
        public static ModConfigSpec.BooleanValue disableUniqueRebindingBiomeTags;

        public static ModConfigSpec.BooleanValue disableIceOrSnowCauldron;
        public static ModConfigSpec.BooleanValue forceServerConfig;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Debug");

            forceServerConfig = builder
                    .comment(
                            "Force using server synchronized config when joining a multiplayer server.",
                            "If disabled, client keeps its own local config when exit."
                    )
                    .define("ForceServerConfig", true);

            logIllegalUse = builder.comment("Log errors when internal functions are used incorrectly.")
                    .define("LogIllegalUse", false);
            notLightAbove = builder.comment("Prevent snow overlays from rendering under blocks with zero light emission.")
                    .define("NoSnowyUnderLight0", false);

            snowOverlayGlowingBlock = builder.comment("Allow snow overlays to cover light-emitting blocks.")
                    .define("SnowOverlayGlowingBlock", false);

            disableChunkCacheCleaner = builder.comment("Keep chunk extra info in memory (disables the cache cleaner).")
                    .define("DisableChunkCacheCleaner", false);

            disableUniqueRebindingBiomeTags = builder.comment("Prevent biome tags from being rebound (may lead to tag overlapping).")
                    .define("DisableUniqueBiomeTagsRebinding", false);

            disableIceOrSnowCauldron = builder.comment("Cauldrons will no longer collect snow or ice during winter weather.")
                    .define("DisableIceOrSnowCauldron", false);


            builder.pop();
        }
    }

    public static class Temperature {
        public static ModConfigSpec.BooleanValue heatStroke;
        public static ModConfigSpec.BooleanValue iceMelt;
        public static ModConfigSpec.BooleanValue snowDown;
        public static ModConfigSpec.BooleanValue waterFreezesInFrozenBiomes;
        public static ModConfigSpec.BooleanValue snowKeepInSnowyBiomes;

        private static void load(ModConfigSpec.Builder builder) {
            builder.comment("This module governs how seasonal temperature shifts affect the physical world and the player.");
            builder.push("Temperature");
            iceMelt = builder.comment("Allow accumulated snow layers and ice to melt as the temperature rises.")
                    .define("IceAndSnowMelt", false);
            snowDown = builder.comment("Allow natural snowfall and snow accumulation during cold periods.")
                    .define("IceAndSnow", false);
            waterFreezesInFrozenBiomes = builder.comment("If enabled, water in frozen biomes follows vanilla behavior and turns into ice.")
                    .define("WaterFreezesInFrozenBiomes", true);
            snowKeepInSnowyBiomes = builder.comment("Prevents physical snow from melting in naturally snowy biomes, even during hot seasons.")
                    .define("SnowKeepInSnowyBiomes", true);
            heatStroke = builder.comment("Apply a 'Heatstroke' status effect if players stay in hot biomes during Summer noon.")
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
        public static ModConfigSpec.IntValue monthOffset;
        public static ModConfigSpec.IntValue dayOffset;

        public static ModConfigSpec.ConfigValue<List<? extends String>> validDimensions;

        public static ModConfigSpec.BooleanValue daylightChange;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> springDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> summerDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> autumnDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> winterDayTimes;
        public static ModConfigSpec.ConfigValue<List<? extends Integer>> noneDayTimes;

        public static ModConfigSpec.BooleanValue dynamicSnowTerm;

        public static ModConfigSpec.BooleanValue realWorldSolarTerms;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Season");
            lastingDaysOfEachTerm = builder.comment("The duration of a single Solar Term in Minecraft days.\nLogic: 1 Year = 4 Seasons | 1 Season = 6 Solar Terms.")
                    .defineInRange("LastingDaysOfEachTerm", 7, 1, 5000);
            initialSolarTermIndex = builder.comment("The index of the Solar Term when the world is first created (1-24).")
                    .defineInRange("InitialSolarTermIndex", 4, 1, 24);
            monthOffset = builder.comment("Shifts the displayed month index. This does NOT affect internal solar term calculations.")
                    .defineInRange("MonthOffset", 1, -11, 11);

            dayOffset = builder.comment("Shifts the calendar day relative to solar terms. Example: 2 means solar day 0 appears as the 3rd day of the month.")
                    .defineInRange("DayOffset", 2, -64, 64);

            enableInform = builder.comment("Display a chat message whenever the Solar Term changes.")
                    .define("EnableInform", true);
            enableInformIcon = builder.comment("Include a custom icon in the change notification for better visibility.")
                    .define("EnableInformIcon", true);
            enableLocalInfoCalendar = builder.comment("Synchronize the in-game calendar and seasonal data with the server/client local time.")
                    .define("EnableLocalInfoAndCalendar", false);
            calendarItemHint = builder.comment("Show a tooltip or message if a calendar item cannot be placed in the current location.")
                    .define("CalendarItemHint", false);


            daylightChange = builder.comment("Adjust daylight duration based on the time of year (longer in Summer, shorter in Winter).")
                    .define("DynamicDaylightDuration", true);

            validDimensions = builder.comment("List of dimension IDs where seasonal effects should be active.")
                    .defineListAllowEmpty("ValidDimensions",
                            () -> List.of(Level.OVERWORLD.identifier().toString()),
                            () -> Level.OVERWORLD.identifier().toString(),
                            o -> o instanceof String s && Identifier.tryParse(s) != null);
            springDayTimes = builder.comment("Daylight length in Ticks for each of the 6 Solar Terms in Spring.")
                    .defineList(List.of("SpringDayTimes"),
                            () -> List.of(10500, 11000, 11500, 12000, 12500, 13000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= EclipticUtil.getDayLengthInMinecraftStatic()),
                            ModConfigSpec.Range.of(6, 6));
            summerDayTimes = builder.comment("Daylight length in Ticks for each of the 6 Solar Terms in Summer.")
                    .defineList(List.of("SummerDayTimes"),
                            () -> List.of(13500, 14000, 14500, 15000, 14500, 14000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= EclipticUtil.getDayLengthInMinecraftStatic()),
                            ModConfigSpec.Range.of(6, 6));
            autumnDayTimes = builder.comment("Daylight length in Ticks for each of the 6 Solar Terms in Autumn.")
                    .defineList(List.of("AutumnDayTimes"),
                            () -> List.of(13500, 13000, 12500, 12000, 11500, 11000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= EclipticUtil.getDayLengthInMinecraftStatic()),
                            ModConfigSpec.Range.of(6, 6));
            winterDayTimes = builder.comment("Daylight length in Ticks for each of the 6 Solar Terms in Winter.")
                    .defineList(List.of("WinterDayTimes"),
                            () -> List.of(10500, 10000, 9500, 9000, 9500, 10000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= EclipticUtil.getDayLengthInMinecraftStatic()),
                            ModConfigSpec.Range.of(6, 6));
            noneDayTimes = builder.comment("Default daylight length in Ticks when no seasonal effects are active.")
                    .defineList(List.of("NoneDayTimes"),
                            () -> List.of(12000),
                            () -> 12000,
                            o -> o instanceof Integer i && (i >= 0 && i <= EclipticUtil.getDayLengthInMinecraftStatic()),
                            ModConfigSpec.Range.of(1, 1));
            dynamicSnowTerm = builder.comment("Introduces random yearly variations to the snowfall date for unpredictability.")
                    .define("DynamicSnowTerm", false);

            realWorldSolarTerms = builder
                    .comment("Sync in-game seasons with the real-world date.\nWarning: May cause day-counter or calendar conflicts with other mods.")
                    .define("RealWorldSolarTerms", false);

            builder.pop();
        }
    }

    public static class Crop {
        public static ModConfigSpec.BooleanValue enableCrop;
        public static ModConfigSpec.BooleanValue enableCropHumidityControl;
        public static ModConfigSpec.BooleanValue cropHumidityTransition;

        public static ModConfigSpec.IntValue greenHouseMaxDiameter;
        public static ModConfigSpec.IntValue greenHouseMaxHeight;
        public static ModConfigSpec.IntValue darkGreenhouseFailChance;
        public static ModConfigSpec.BooleanValue complexGreenHouseCheck;
        public static ModConfigSpec.BooleanValue registerCropDefaultValue;
        public static ModConfigSpec.BooleanValue forceCompatMode;
        public static ModConfigSpec.BooleanValue cropLeavesPatch;
        public static ModConfigSpec.BooleanValue simpleGreenHouse;
        public static ModConfigSpec.BooleanValue noCostHumidifier;
        public static ModConfigSpec.BooleanValue useBoxDistance;
        public static ModConfigSpec.IntValue seasonCoreRange;
        public static ModConfigSpec.BooleanValue boneMealFailureMessage;
        public static ModConfigSpec.BooleanValue boneMealConsumeOnFailure;

        public static ModConfigSpec.BooleanValue saveChunkEnvironmentalHumidity;
        public static ModConfigSpec.IntValue seasonalPrayerRitualCropBonusReduction;
        public static ModConfigSpec.DoubleValue seasonalPrayerRitualTimeCost;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Crop");
            enableCrop = builder.comment("Restrict plant growth based on their compatible seasons or humidity.")
                    .define("EnableSeasonalCrop", true);
            enableCropHumidityControl = builder.comment("Restrict plant growth based on local soil/environmental humidity.")
                    .define("EnableCropHumidityControl", true);
            cropHumidityTransition = builder.comment("Smooths out humidity changes between different areas or time periods.")
                    .define("CropHumidityTransition", true);
            boneMealFailureMessage = builder.comment("Show a message if bone meal fails to work due to incorrect season or humidity.")
                    .define("BoneMealFailureMessage", true);
            boneMealConsumeOnFailure = builder.comment("Consume the bone meal item even if the growth attempt fails.")
                    .define("BoneMealConsumeOnFailure", true);
            greenHouseMaxDiameter = builder.comment("The horizontal detection radius for the Greenhouse.")
                    .defineInRange("GreenHouseMaxDiameter", 32, 5, 256);
            greenHouseMaxHeight = builder.comment("The vertical detection height for the Greenhouse.")
                    .defineInRange("GreenHouseMaxHeight", 10, 3, 128);
            darkGreenhouseFailChance = builder.comment("Probability (per tick) that greenhouse crops fail to grow due to low light levels.")
                    .defineInRange("LowLightGreenhouseFailChance", 2000, 0, 10000);
            simpleGreenHouse = builder.comment("Simplifies greenhouse logic, removing the need for core blocks or humidity modifiers.")
                    .define("SimpleGreenHouseMode", false);
            noCostHumidifier = builder.comment("If true, the Humidifier block will not consume resources during operation.")
                    .define("NoCostHumidifier", false);
            seasonCoreRange = builder.comment("The effective radius of the 'Season Core' block.")
                    .defineInRange("SeasonCoreRange", 15, 4, 31);
            complexGreenHouseCheck = builder.comment("Enables more precise shape detection for greenhouse structures.")
                    .define("ComplexGreenHouseCheck", true);
            useBoxDistance = builder.comment("Use Manhattan distance (square) instead of Euclidean (circle) for greenhouse range.")
                    .define("UseBoxDistance", true);
            registerCropDefaultValue = builder.comment("[Deprecated] Use default seasonal/humidity values for unregistered crops.")
                    .define("RegisterCropDefaultValue", false);
            forceCompatMode = builder.comment("Force all plants to follow growth rules, even those without specific mod tags.")
                    .define("ForceCompatMode", true);
            cropLeavesPatch = builder.comment("Apply patch withering code for crop leave blocks if tick failed.")
                    .define("CropLeavesPatch", true);

            saveChunkEnvironmentalHumidity = builder.comment("Saves local humidity data to chunk files for persistent tracking.")
                    .define("SaveChunkEnvironmentalHumidity", true);
            seasonalPrayerRitualCropBonusReduction = builder
                    .comment("Adjusts the power of the Seasonal Prayer Ritual. Higher values result in lower bonuses.")
                    .defineInRange("SeasonalPrayerRitualCropBonusReduction", 500, 5, Integer.MAX_VALUE);
            seasonalPrayerRitualTimeCost = builder
                    .comment("The duration required for the Prayer Ritual (relative to one Solar Term).")
                    .defineInRange("SeasonalPrayerRitualTimeCost", 2, 0.00001d, 5000);
            builder.pop();
        }
    }

    public static class Animal {

        public static ModConfigSpec.BooleanValue enableBreed;
        public static ModConfigSpec.BooleanValue enableTimeBreed;
        public static ModConfigSpec.BooleanValue enableBee;
        public static ModConfigSpec.ConfigValue<List<? extends String>> beePollinateSeasons;
        public static ModConfigSpec.ConfigValue<List<? extends String>> beeActiveSeasons;

        public static ModConfigSpec.BooleanValue enableFishing;
        public static ModConfigSpec.ConfigValue<List<? extends String>> fishingSeasons;
        public static ModConfigSpec.BooleanValue lessFishInThunder;
        public static ModConfigSpec.BooleanValue enableCoreWork;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Animal");
            enableBreed = builder.comment("Limit animal breeding to their natural, compatible seasons.")
                    .define("EnableSeasonalBreed", false);

            enableTimeBreed = builder
                    .comment("Restrict breeding to specific hours of the day.")
                    .define("EnableTimeBreed", false);

            enableBee = builder.comment("Bees become inactive during cold seasons and active during warm ones.")
                    .define("EnableSeasonalBee", false);
            beePollinateSeasons = builder.comment("List of seasons where bees are allowed to pollinate plants.",
                    "Default: [SPRING]").defineListAllowEmpty("BeePollinateSeasons",
                    () -> List.of(
                            com.teamtea.eclipticseasons.api.constant.solar.Season.SPRING.toString()
                    ),
                    com.teamtea.eclipticseasons.api.constant.solar.Season.SPRING::toString,
                    CommonConfig::validSeason);

            beeActiveSeasons = builder.comment("List of seasons where bees will leave the hive to fly around.",
                    "Default: [SPRING, SUMMER, AUTUMN]").defineListAllowEmpty("BeeActiveSeasons",
                    () -> List.of(
                            com.teamtea.eclipticseasons.api.constant.solar.Season.SPRING.toString(),
                            com.teamtea.eclipticseasons.api.constant.solar.Season.SUMMER.toString(),
                            com.teamtea.eclipticseasons.api.constant.solar.Season.AUTUMN.toString()
                    ),
                    com.teamtea.eclipticseasons.api.constant.solar.Season.SPRING::toString,
                    CommonConfig::validSeason);

            enableFishing = builder.comment("Modify fishing mechanics based on the current season (e.g., Summer bonuses).")
                    .define("EnableSeasonalFishing", false);

            fishingSeasons = builder.comment("List of seasons where fishing is most effective.",
                    "Default: [SUMMER]"
            ).defineListAllowEmpty("FishingSeasons",
                    () -> List.of(
                            com.teamtea.eclipticseasons.api.constant.solar.Season.SUMMER.toString()
                    ),
                    com.teamtea.eclipticseasons.api.constant.solar.Season.SUMMER::toString,
                    CommonConfig::validSeason);

            lessFishInThunder = builder.comment("Decrease the bite rate significantly during active thunderstorms.")
                    .define("LessFishInThunder", false);

            enableCoreWork = builder.comment("Allow the Season Core to provide breeding bonuses to animals without a greenhouse.")
                    .define("SeasonCoreAffectsAnimals", true);
            builder.pop();
        }
    }

    public static boolean validSeason(Object o) {
        if (o instanceof String s) {
            try {
                com.teamtea.eclipticseasons.api.constant.solar.Season.valueOf(s);
                return true;
            } catch (IllegalArgumentException ignored) {
            }
        }
        return o instanceof com.teamtea.eclipticseasons.api.constant.solar.Season;
    }

    public static boolean validSolarTerm(Object o) {
        if (o instanceof String s) {
            try {
                SolarTerm.valueOf(s);
                return true;
            } catch (IllegalArgumentException ignored) {
            }
        }
        return o instanceof SolarTerm;
    }

    public static Set<com.teamtea.eclipticseasons.api.constant.solar.Season> castSeasons(List<? extends String> strings) {
        var es1 = EnumSet.noneOf(com.teamtea.eclipticseasons.api.constant.solar.Season.class);
        for (String string : strings) {
            es1.add(com.teamtea.eclipticseasons.api.constant.solar.Season.valueOf(string));
        }
        return es1;
    }

    public static List<com.teamtea.eclipticseasons.api.constant.solar.Season> castSeasonList(List<? extends String> strings) {
        var es1 = new ArrayList<com.teamtea.eclipticseasons.api.constant.solar.Season>();
        for (String string : strings) {
            es1.add(com.teamtea.eclipticseasons.api.constant.solar.Season.valueOf(string));
        }
        return es1;
    }

    public static Set<SolarTerm> castSolarTerms(List<? extends String> strings) {
        var es1 = EnumSet.noneOf(SolarTerm.class);
        for (String string : strings) {
            es1.add(SolarTerm.valueOf(string));
        }
        return es1;
    }

    public static List<SolarTerm> castSolarTermList(List<? extends String> strings) {
        var es1 = new ArrayList<SolarTerm>();
        for (String string : strings) {
            es1.add(SolarTerm.valueOf(string));
        }
        return es1;
    }

    public static class Weather {

        public static ModConfigSpec.BooleanValue useSolarWeather;
        public static ModConfigSpec.BooleanValue notRainInDesert;
        public static ModConfigSpec.IntValue rainChanceMultiplier;
        public static ModConfigSpec.IntValue thunderChanceMultiplier;
        public static ModConfigSpec.DoubleValue snowAccumulationSpeedMultiplier;
        public static ModConfigSpec.DoubleValue snowMeltSpeedMultiplier;
        public static ModConfigSpec.BooleanValue shouldInitWeather;
        public static ModConfigSpec.BooleanValue shouldInitSnowForExtremeColdBiomes;
        public static ModConfigSpec.BooleanValue clearAfterSleep;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Weather");
            useSolarWeather = builder.comment("Enable localized weather patterns where rain or sun is determined per-biome.")
                    .define("UseSolarWeather", true);
            notRainInDesert = builder.comment("Disable rain/snow in biomes with no natural precipitation (e.g., Deserts).")
                    .define("NotRainInDesert", true);
            shouldInitWeather = builder.comment("Force initialize weather and snow states when the mod or world is first loaded.")
                    .define("ShouldInitWeather", false);
            shouldInitSnowForExtremeColdBiomes = builder.comment("Force initialize snow states for extreme cold biomes when the mod or world is first loaded.")
                    .define("ShouldInitSnowDepthForExtremeColdBiomes", true);
            rainChanceMultiplier = builder.comment("Adjust the overall frequency of rain.")
                    .defineInRange("RainChancePercentMultiplier", 40, 0, 1000);
            thunderChanceMultiplier = builder.comment("Adjust the overall frequency of thunder.")
                    .defineInRange("ThunderChancePercentMultiplier", 20, 0, 1000);
            snowAccumulationSpeedMultiplier = builder
                    .comment("Adjusts the spread rate of atmospheric snow overlays across the ground.")
                    .defineInRange("SnowAccumulationSpeedMultiplier", 1.0, 0.0, 20.0);
            snowMeltSpeedMultiplier = builder
                    .comment("Adjusts the recession speed of atmospheric snow overlays during warmer periods.")
                    .defineInRange("SnowMeltSpeedMultiplier", 1.0, 0.0, 20.0);
            clearAfterSleep = builder.comment("Automatically reset weather to clear after the player wakes up from a bed.")
                    .define("ClearAfterSleep", true);
            builder.pop();
        }
    }

    public static class Map {
        public static ModConfigSpec.BooleanValue changeMapColor;
        public static ModConfigSpec.BooleanValue changeMapColorMapItem;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Map");
            changeMapColor = builder.comment(
                    "Synchronize all map color rendering with visual snow overlays. May affect compatibility with other mods."
            ).define("ChangeMapColorIfSnowy", false);
            changeMapColorMapItem = builder.comment(
                    "Only adjust map item colors to reflect visual snow overlays. Safer for mod compatibility."
            ).define("ChangeMapColorMapItem", true);
            builder.pop();
        }
    }

    public static class Snow {
        public static ModConfigSpec.BooleanValue snowyWinter;
        public static ModConfigSpec.BooleanValue snowyTree;
        public static ModConfigSpec.BooleanValue notSnowyNearGlowingBlock;
        public static ModConfigSpec.IntValue notSnowyNearGlowingBlockLevel;
        public static ModConfigSpec.ConfigValue<List<? extends String>> blocksNotSnowy;
        public static ModConfigSpec.ConfigValue<List<? extends List<? extends Serializable>>> biomeSnowLines;

        public static ModConfigSpec.BooleanValue snowInWorld;
        public static ModConfigSpec.BooleanValue forceChunkUpdate;
        public static ModConfigSpec.BooleanValue forceChunkUpdateOnlyWhenMelt;
        public static ModConfigSpec.BooleanValue snowyUnderSnowLike;
        public static ModConfigSpec.BooleanValue stepMelt;

        @SafeVarargs
        private static <T> List<T> of(T... objs) {
            return Arrays.stream(objs).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        private static boolean testLList(Object o) {
            if (o instanceof List<?> innerChildren) {
                if (innerChildren.size() == 2) {
                    if (innerChildren.get(0) instanceof String s) {
                        try {
                            s = s.startsWith("#") ? s.substring(1, s.length() - 1) : s;
                            Identifier.parse(s);
                        } catch (Exception e) {
                            EclipticSeasons.logger(e);
                            return false;
                        }
                        return innerChildren.get(1) instanceof Integer;
                    } else {
                        return false;
                    }
                }
            }
            return false;
        }

        private static void load(ModConfigSpec.Builder builder) {
            builder.comment("Snow overlays are visual, atmospheric effects and are distinct from physical snow blocks.");
            builder.push("Snow");
            snowyWinter = builder.comment("Controls atmospheric snow overlays during cold weather. "
                            + "Visual snow gradually covers solid blocks and grass, even in warmer biomes.")
                    .define("SnowyWinter", true);
            snowyTree = builder.comment("Apply frost and snow overlays to the undersides of leaves and tree branches.")
                    .define("SnowyTree", true);
            notSnowyNearGlowingBlock = builder.comment("Prevents snow overlays from building up near blocks that emit significant light.")
                    .define("NotSnowyNearGlowingBlock", true);
            notSnowyNearGlowingBlockLevel = builder.comment("The minimum light level required to prevent snow accumulation.")
                    .defineInRange("NotSnowyNearGlowingBlockLevel", 10, 1, 15);
            blocksNotSnowy = builder.comment("List of Block IDs that will never be covered by snow overlays.")
                    .defineListAllowEmpty("ForceBlocksNotSnowy",
                            List::of,
                            () -> "",
                            o -> o instanceof String s && Identifier.tryParse(s) != null);
            biomeSnowLines = builder.comment("Set custom snow-line altitudes for biomes (e.g., [\"#c:is_cold\", 200]).")
                    .defineListAllowEmpty("BiomeSnowLines",
                            List.of(),
                            List::of,
                            Snow::testLList);

            snowInWorld = builder.comment("Track snow based on exact world coordinates (allows for localized snow clearing).")
                    .define("SnowInWorld", false);
            forceChunkUpdate = builder.comment("(SnowInWorld) Sync chunk snow overlay states immediately when they are loaded.")
                    .define("ForceSnowyChunkUpdate", true);
            forceChunkUpdateOnlyWhenMelt = builder.comment("(SnowInWorld) Only force chunk snow overlays sync when the weather is warm enough for snow to melt.")
                    .define("ForceChunkUpdateOnlyWhenMelt", false);
            snowyUnderSnowLike = builder.comment("(SnowInWorld) Render snow overlays on the sides of blocks located beneath a snow layer block.")
                    .define("SnowCoverUnderBlocks", true);
            stepMelt = builder.comment("(SnowInWorld) Snow layers have a chance to melt when entities walk over them.")
                    .define("SnowStepMelt", false);
            builder.pop();
        }
    }

    public static class Resource {
        public static ModConfigSpec.BooleanValue SnowTogether;
        // public static ModConfigSpec.BooleanValue RainTogether;
        public static ModConfigSpec.BooleanValue RegionalSnowTime;
        public static ModConfigSpec.BooleanValue VanillaBiomeClimateSettings;
        public static ModConfigSpec.BooleanValue NotIgnoreRiver;
        public static ModConfigSpec.BooleanValue springGrass;

        private static void load(ModConfigSpec.Builder builder) {
            builder.push("Resource");

            // RainTogether = builder.comment("Synchronizes weather states across all Overworld biomes, ensuring global rainfall.")
            //         .define("RainTogether", true);

            SnowTogether = builder.comment("Synchronizes the snowfall schedule for all Overworld biomes.")
                    .define("SnowTogether", false);

            RegionalSnowTime = builder.comment("Aligns snowfall schedules based on three broad climate zones (Warm, Temperate, and Cold) instead of per-biome.")
                    .define("RegionalSnowTime", true);

            VanillaBiomeClimateSettings = builder.comment("Enforces original Vanilla temperature and precipitation settings to prevent other mods from creating extreme environmental values.")
                    .define("VanillaBiomeClimateSettings", true);

            NotIgnoreRiver = builder.comment("When enabled, rivers are no longer treated as ignored climate zones. This reduces performance overhead but may result in less natural weather transitions near riverbanks.")
                    .define("NotIgnoreRiver", false);

            springGrass = builder.comment("Spring brings life to grass and flowers, while autumn marks the fading.")
                    .define("SpringGrass", false);

            builder.pop();
        }
    }


    @Getter
    private static boolean useSolarWeather = true;

    @Getter
    private static boolean forceCropCompatMode = false;

    @Getter
    private static boolean snowyWinter = true;
    @Getter
    private static boolean snowInWorld = true;

    @Getter
    private static final int[] dayTimesForSeason = new int[SolarTerm.collectValues().length];
    @Getter
    private static boolean useDayTimes = false;
    @Getter
    private static boolean cropHumidityTransition = true;
    @Getter
    private static final Set<Block> forceBlocksNotSnowy = new HashSet<>();

    @Getter
    private static boolean vanillaSnowAndIce = false;

    public static void UpdateConfig(ModConfigEvent modConfigEvent) {
        if (!(modConfigEvent instanceof ModConfigEvent.Unloading)
                && modConfigEvent.getConfig().getSpec() == COMMON_CONFIG) {
            vanillaSnowAndIce = Temperature.iceMelt.get() && Temperature.snowDown.get();
            useSolarWeather = Weather.useSolarWeather.get();
            forceCropCompatMode = Crop.forceCompatMode.get();
            snowyWinter = Snow.snowyWinter.get();
            snowInWorld = Snow.snowInWorld.get() && snowyWinter;
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
            for (String s : Snow.blocksNotSnowy.get()) {
                Optional<Holder.Reference<Block>> blockReference = BuiltInRegistries.BLOCK.get(Identifier.parse(s));
                if (blockReference.isEmpty()) continue;
                Block block = blockReference.get().value();
                if (block != Blocks.AIR) {
                    forceBlocksNotSnowy.add(block);
                }
            }
        }
    }


}

