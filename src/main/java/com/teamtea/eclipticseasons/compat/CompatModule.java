package com.teamtea.eclipticseasons.compat;

import com.teamtea.eclipticseasons.compat.cold_sweat.Cold_Sweat;
import com.teamtea.eclipticseasons.compat.dynamictrees.DynamicTreeMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;

public class CompatModule {

    private static boolean dynamictrees = false;
    private static boolean cold_sweat = false;
    private static boolean legendarysurvivaloverhaul = false;

    /**
     * Used for mod init detect.
     **/
    public static void init() {
        dynamictrees = Platform.isModLoaded("dynamictrees");
        cold_sweat = Platform.isModLoaded("cold_sweat");
        legendarysurvivaloverhaul = Platform.isModLoaded("legendarysurvivaloverhaul");
    }

    /**
     * Used for mod init event register.
     **/
    public static void register(IEventBus gameBus, IEventBus modBus) {
        if (isCold_sweat()) {
            gameBus.register(Cold_Sweat.INSTANCE);
        }
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
                                () -> List.of(-0.3d, -0.35d, -0.35d, -0.5d, -0.45d, -0.4d),
                                o -> o instanceof Double);
                builder.pop();
            }
            if (isLegendarysurvivaloverhaul()) {
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
            builder.pop();
        }
    }

    public static class ClientConfig {
        public static ForgeConfigSpec.BooleanValue sereneSeasons;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");

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
}
