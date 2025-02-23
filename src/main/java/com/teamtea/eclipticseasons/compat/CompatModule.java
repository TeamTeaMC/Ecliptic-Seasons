package com.teamtea.eclipticseasons.compat;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.teamtea.eclipticseasons.compat.cold_sweat.Cold_Sweat;
import com.teamtea.eclipticseasons.compat.dynamictrees.DynamicTreeMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class CompatModule {

    private static boolean cold_sweat = false;

    public static void init() {
        cold_sweat = Platform.isModLoaded("cold_sweat");
    }

    public static void register(IEventBus gameBus, IEventBus modBus) {
        if (isCold_sweat()) {
            gameBus.register(Cold_Sweat.INSTANCE);
        }
    }

    public static void register() {

        if (ModList.get().isLoaded(DynamicTrees.MOD_ID)) {
            DynamicTreeMod.init();
        }
    }


    public static boolean isCold_sweat() {
        return cold_sweat;
    }


    public static class CommonConfig {
        public static ForgeConfigSpec.BooleanValue sereneSeasons;

        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_springs;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_summers;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_autumns;
        public static ForgeConfigSpec.ConfigValue<List<? extends Double>> cold_sweat_winters;

        public static void load(ForgeConfigSpec.Builder builder) {
            builder.push("Compat");
            sereneSeasons = builder.comment("Compatible with mods using SereneSeasons' CropTag.")
                    .define("SereneSeasonsCropTag", true);
            if (isCold_sweat()) {
                builder.push("ColdSweat");
                cold_sweat_springs = builder.comment("Spring Temperatures, divided into six periods according to the solar term table.")
                        .defineList("SpringTemps",
                                () -> List.of(-0.25d, -0.15d, -0.1d, 0d, 0d, 0.05d),
                                o -> o instanceof Double);
                cold_sweat_summers = builder.comment("Summer Temperatures divided into six periods according to the solar term table.")
                        .defineList("SummerTemps",
                                () -> List.of(0.1d, 0.15d, 0.15d, 0.2d, 0.2d, 0.25d),
                                o -> o instanceof Double);
                cold_sweat_autumns = builder.comment("Autumn Temperatures divided into six periods according to the solar term table.")
                        .defineList("AutumnTemps",
                                () -> List.of(0.15d, 0.1d, 0.05d, 0d, -0.1d, -0.2d),
                                o -> o instanceof Double);
                cold_sweat_winters = builder.comment("Winter Temperatures divided into six periods according to the solar term table.")
                        .defineList("WinterTemps",
                                () -> List.of(-0.3d, -0.35d, -0.35d, -0.4d, -0.45d, -0.4d),
                                o -> o instanceof Double);
                builder.pop();
            }


            builder.pop();
        }
    }
}
