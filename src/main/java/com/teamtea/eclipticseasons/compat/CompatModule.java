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
        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> cold_sweat_springs;
        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> cold_sweat_summers;
        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> cold_sweat_autumns;
        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> cold_sweat_winters;

        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> legendarysurvivaloverhaul_springs;
        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> legendarysurvivaloverhaul_summers;
        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> legendarysurvivaloverhaul_autumns;
        public static ForgeConfigSpec.ConfigValue<List<? extends Float>> legendarysurvivaloverhaul_winters;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            if (isCold_sweat()) {
                builder.push("ColdSweat");
                cold_sweat_springs = builder.comment("Spring Temperatures, divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SpringTemps",
                                () -> List.of(-0.25f, -0.15f, -0.1f, 0f, 0f, 0.05f),
                                o -> o instanceof Float);
                cold_sweat_summers = builder.comment("Summer Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SummerTemps",
                                () -> List.of(0.1f, 0.15f, 0.15f, 0.2f, 0.2f, 0.25f),
                                o -> o instanceof Float);
                cold_sweat_autumns = builder.comment("Autumn Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("AutumnTemps",
                                () -> List.of(0.15f, 0.1f, 0.05f, 0f, -0.1f, -0.2f),
                                o -> o instanceof Float);
                cold_sweat_winters = builder.comment("Winter Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("WinterTemps",
                                () -> List.of(-0.3f, -0.35f, -0.35f, -0.5f, -0.45f, -0.4f),
                                o -> o instanceof Float);
                builder.pop();
            }
            if (isLegendarysurvivaloverhaul()) {
                builder.push("LegendarySurvivalOverhaul");
                legendarysurvivaloverhaul_springs = builder.comment("Spring Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SpringTemps",
                                () -> List.of(-2.5f, -1.5f, -1f, 0f, 0f, 0.5f),
                                o -> o instanceof Float);
                legendarysurvivaloverhaul_summers = builder.comment("Summer Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("SummerTemps",
                                () -> List.of(1f, 1.5f, 1.5f, 2f, 2f, 2.5f),
                                o -> o instanceof Float);
                legendarysurvivaloverhaul_autumns = builder.comment("Autumn Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("AutumnTemps",
                                () -> List.of(1.5f, 1f, 0.5f, 0f, -1f, -2f),
                                o -> o instanceof Float);
                legendarysurvivaloverhaul_winters = builder.comment("Winter Temperatures divided into six periods according to the solar term table.")
                        .defineListAllowEmpty("WinterTemps",
                                () -> List.of(-3f, -3.5f, -3.5f, -4f, -4.5f, -4f),
                                o -> o instanceof Float);
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
