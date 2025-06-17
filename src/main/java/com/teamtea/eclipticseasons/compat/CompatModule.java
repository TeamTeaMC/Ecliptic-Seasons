package com.teamtea.eclipticseasons.compat;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.compat.cold_sweat.Cold_Sweat;
import com.teamtea.eclipticseasons.compat.dynamictrees.DynamicTreeMod;
import com.teamtea.eclipticseasons.compat.theoneprobe.TOPReflector;
import com.teamtea.eclipticseasons.compat.touhou_little_maid.LittleMaid;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import java.util.List;

public class CompatModule {

    private static boolean dynamictrees = false;
    private static boolean cold_sweat = false;
    private static boolean legendarysurvivaloverhaul = false;
    private static boolean journeymap = false;
    private static boolean snowyspirit = false;
    private static boolean hauntedharvest = false;
    private static boolean touhou_little_maid = false;

    /**
     * Used for mod init detect.
     **/
    public static void init() {
        dynamictrees = Platform.isModLoaded("dynamictrees");
        cold_sweat = Platform.isModLoaded("cold_sweat");
        legendarysurvivaloverhaul = Platform.isModLoaded("legendarysurvivaloverhaul");
        journeymap = Platform.isModLoaded("journeymap");
        snowyspirit = Platform.isModLoaded("snowyspirit");
        hauntedharvest = Platform.isModLoaded("hauntedharvest");
        touhou_little_maid = Platform.isModLoaded("touhou_little_maid");

    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {
        if (isCold_sweat()) {
            gameBus.register(Cold_Sweat.INSTANCE);
        }
        if (isTouhou_little_maid()) {
            gameBus.register(LittleMaid.INSTANCE);
        }
    }

    public static void onInterModEnqueue(final InterModEnqueueEvent event) {
        event.enqueueWork(TOPReflector::init);
    }

    /**
     * Used for mod setup.
     **/
    public static void setup() {

        if (isDynamictrees()) {
            DynamicTreeMod.init();
        }

    }


    public static class CommonConfig {
        public static ForgeConfigSpec.BooleanValue sereneSeasons;

        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_springs;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_summers;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_autumns;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_winters;

        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> legendarysurvivaloverhaul_springs;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> legendarysurvivaloverhaul_summers;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> legendarysurvivaloverhaul_autumns;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> legendarysurvivaloverhaul_winters;

        public static ForgeConfigSpec.ConfigValue<List<? extends SolarTerm>> snowyspirit_winters;
        public static ForgeConfigSpec.BooleanValue snowyspirit_enable;

        public static ForgeConfigSpec.ConfigValue<List<? extends SolarTerm>> hauntedharvest_halloween_time;
        public static ForgeConfigSpec.ConfigValue<List<? extends SolarTerm>> hauntedharvest_mobs_wear_pumpkins_time;
        public static ForgeConfigSpec.BooleanValue hauntedharvest_enable;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            if (isCold_sweat()) {
                builder.push("ColdSweat");
                cold_sweat_springs = builder.comment("Spring Temperatures, divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SpringTemps",
                                () -> List.of(-0.25d, -0.15d, -0.1d, 0d, 0d, 0.05d),
                                o -> o instanceof Double);
                cold_sweat_summers = builder.comment("Summer Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SummerTemps",
                                () -> List.of(0.1d, 0.15d, 0.15d, 0.2d, 0.2d, 0.25d),
                                o -> o instanceof Double);
                cold_sweat_autumns = builder.comment("Autumn Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("AutumnTemps",
                                () -> List.of(0.15d, 0.1d, 0.05d, 0d, -0.1d, -0.2d),
                                o -> o instanceof Double);
                cold_sweat_winters = builder.comment("Winter Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("WinterTemps",
                                () -> List.of(-0.3d, -0.35d, -0.35d, -0.4d, -0.45d, -0.4d),
                                o -> o instanceof Double);
                builder.pop();
            }
            if (isLegendarysurvivaloverhaul() && false) {
                builder.push("LegendarySurvivalOverhaul");
                legendarysurvivaloverhaul_springs = builder.comment("Spring Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SpringTemps",
                                () -> List.of(-2.5d, -1.5d, -1d, 0d, 0d, 0.5d),
                                o -> o instanceof Double);
                legendarysurvivaloverhaul_summers = builder.comment("Summer Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SummerTemps",
                                () -> List.of(1d, 1.5d, 1.5d, 2d, 2d, 2.5d),
                                o -> o instanceof Double);
                legendarysurvivaloverhaul_autumns = builder.comment("Autumn Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("AutumnTemps",
                                () -> List.of(1.5d, 1d, 0.5d, 0d, -1d, -2d),
                                o -> o instanceof Double);
                legendarysurvivaloverhaul_winters = builder.comment("Winter Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("WinterTemps",
                                () -> List.of(-3d, -3.5d, -3.5d, -4d, -4.5d, -4d),
                                o -> o instanceof Double);
                builder.pop();
            }
            if (isSnowyspirit()) {
                builder.push("SnowySpirit");
                snowyspirit_enable = builder.comment("Enable special time with SnowySpirit.")
                        .define("Enable", true);
                snowyspirit_winters = builder.comment("Solar Terms in which SnowySpirit villager AI behaviors will be active.")
                        .defineListAllowEmpty("WinterTime",
                                () -> List.of(SolarTerm.BEGINNING_OF_WINTER,
                                        SolarTerm.LIGHT_SNOW,
                                        SolarTerm.HEAVY_SNOW,
                                        SolarTerm.WINTER_SOLSTICE,
                                        SolarTerm.LESSER_COLD,
                                        SolarTerm.GREATER_COLD),
                                o -> o instanceof SolarTerm);
                builder.pop();
            }
            if (isHauntedharvest()) {
                builder.push("Hauntedharvest");
                hauntedharvest_enable = builder.comment("Enable special time with Hauntedharvest.")
                        .define("Enable", true);
                hauntedharvest_halloween_time = builder.comment("Solar Terms in which Hauntedharvest villager AI behaviors will be active.")
                        .defineListAllowEmpty("Halloween Time",
                                () -> List.of(
                                        SolarTerm.COLD_DEW,
                                        SolarTerm.FIRST_FROST),
                                o -> o instanceof SolarTerm);
                hauntedharvest_mobs_wear_pumpkins_time = builder.comment("Adds custom times in which mobs can wear pumpkins. Leave empty to ignore.")
                        .defineListAllowEmpty(" Mobs Wear Pumpkins Time",
                                () -> List.of(SolarTerm.FIRST_FROST),
                                o -> o instanceof SolarTerm);
                builder.pop();
            }

            builder.pop();
        }
    }

    public static class ClientConfig {
        public static ForgeConfigSpec.BooleanValue journeyMapSupport;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            if (isJourneymap()) {
                builder.push("JourneyMap");
                journeyMapSupport = builder.comment("Shows snow-covered blocks on the map.")
                        .define("ShowSnowyBlock", true);
                builder.pop();
            }
            builder.pop();
        }
    }

    public static boolean isDynamictrees() {
        return dynamictrees;
    }

    public static boolean isCold_sweat() {
        return cold_sweat;
    }

    public static boolean isLegendarysurvivaloverhaul() {
        return legendarysurvivaloverhaul;
    }

    public static boolean isJourneymap() {
        return journeymap;
    }

    public static boolean isSnowyspirit() {
        return snowyspirit;
    }

    public static boolean isHauntedharvest() {
        return hauntedharvest;
    }

    public static boolean isTouhou_little_maid() {
        return touhou_little_maid;
    }
}
